package br.com.taskmanager.infrastructure.repositories;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.application.queries.TaskScope;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.infrastructure.exceptions.QueryException;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.mysql.MySQLContainer;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class MySqlTaskRepositoryTest {

    private static final Instant CREATED_AT = Instant.parse("2026-01-01T10:00:00Z");
    private static final Instant UPDATED_AT = Instant.parse("2026-01-01T11:00:00Z");
    private static final Instant DEADLINE = Instant.parse("2026-01-02T10:00:00Z");
    private static final Instant FINISHED_AT = Instant.parse("2026-01-01T12:00:00Z");
    private static final Instant DELETED_AT = Instant.parse("2026-01-01T13:00:00Z");
    private static final TaskQuery ACTIVE_TASKS = new TaskQuery("", null, TaskScope.ACTIVE);

    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
            .withDatabaseName("taskmanager")
            .withUsername("test")
            .withPassword("test")
            .withUrlParam("connectionTimeZone", "UTC");

    private static Connection connection;
    private static HikariDataSource dataSource;
    private MySqlTaskRepository repository;

    @BeforeAll
    static void beforeAll() throws Exception {
        connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        String ddl = readMigration();
        try (Statement statement = connection.createStatement()) {
            statement.execute(ddl);
        }
        dataSource = dataSource();
    }

    private static String readMigration() throws IOException {
        try (InputStream stream = MySqlTaskRepositoryTest.class.getClassLoader()
                .getResourceAsStream("db/migration/V1__create_tasks.sql")) {
            if (stream == null) {
                throw new IllegalStateException("Migration db/migration/V1__create_tasks.sql não encontrada no classpath");
            }
            return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    @AfterAll
    static void afterAll() throws Exception {
        if (dataSource != null) {
            dataSource.close();
        }
        if (connection != null) {
            connection.close();
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        try (Statement statement = connection.createStatement()) {
            statement.execute("TRUNCATE TABLE tasks");
        }
        repository = new MySqlTaskRepository(dataSource);
    }

    @Test
    @DisplayName("salva e busca uma task PENDING com todos os campos")
    void salvaEBuscaTaskPending() {
        UUID uuid = UUID.randomUUID();
        repository.save(task(uuid, "Estudar Java", TaskStatus.PENDING, null, null));

        Optional<Task> found = repository.findByUuid(uuid);

        assertTrue(found.isPresent());
        assertEquals(uuid, found.get().getUuid());
        assertEquals("Estudar Java", found.get().getName());
        assertEquals("descrição", found.get().getDescription());
        assertEquals(TaskStatus.PENDING, found.get().getStatus());
        assertEquals(DEADLINE, found.get().getDeadline());
        assertNull(found.get().getFinishedAt());
        assertEquals(CREATED_AT, found.get().getCreatedAt());
        assertEquals(UPDATED_AT, found.get().getUpdatedAt());
        assertNull(found.get().getDeletedAt());
    }

    @Test
    @DisplayName("busca por uuid inexistente retorna Optional vazio")
    void buscaUuidInexistente() {
        Optional<Task> found = repository.findByUuid(UUID.randomUUID());

        assertTrue(found.isEmpty());
    }

    @Test
    @DisplayName("mapeia task COMPLETED com finishedAt")
    void mapeiaTaskCompleted() {
        UUID uuid = UUID.randomUUID();
        repository.save(task(uuid, "Concluída", TaskStatus.COMPLETED, FINISHED_AT, null));

        Task found = repository.findByUuid(uuid).orElseThrow();

        assertEquals(TaskStatus.COMPLETED, found.getStatus());
        assertEquals(FINISHED_AT, found.getFinishedAt());
    }

    @Test
    @DisplayName("mapeia task deletada com deletedAt")
    void mapeiaTaskDeletada() {
        UUID uuid = UUID.randomUUID();
        repository.save(task(uuid, "Deletada", TaskStatus.PENDING, null, DELETED_AT));

        Task found = repository.findByUuid(uuid).orElseThrow();

        assertEquals(DELETED_AT, found.getDeletedAt());
    }

    @Test
    @DisplayName("preserva descrição nula no round-trip")
    void preservaDescricaoNula() {
        UUID uuid = UUID.randomUUID();
        Task task = new Task(uuid, "Sem descrição", null, TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null);

        repository.save(task);

        assertNull(repository.findByUuid(uuid).orElseThrow().getDescription());
    }

    @Test
    @DisplayName("upsert atualiza a task sem duplicar a linha e mantém createdAt")
    void upsertAtualizaSemDuplicar() throws Exception {
        UUID uuid = UUID.randomUUID();
        repository.save(task(uuid, "nome original", TaskStatus.PENDING, null, null));

        Instant novoUpdatedAt = UPDATED_AT.plusSeconds(60);
        Instant novoDeadline = DEADLINE.plusSeconds(3600);
        Task atualizada = new Task(uuid, "nome atualizado", "nova descrição", TaskStatus.COMPLETED,
                novoDeadline, FINISHED_AT, CREATED_AT, novoUpdatedAt, null);

        repository.save(atualizada);

        Task found = repository.findByUuid(uuid).orElseThrow();
        assertEquals("nome atualizado", found.getName());
        assertEquals("nova descrição", found.getDescription());
        assertEquals(TaskStatus.COMPLETED, found.getStatus());
        assertEquals(novoDeadline, found.getDeadline());
        assertEquals(FINISHED_AT, found.getFinishedAt());
        assertEquals(CREATED_AT, found.getCreatedAt());
        assertEquals(novoUpdatedAt, found.getUpdatedAt());
        assertEquals(1L, countTasks());
    }

    @Test
    @DisplayName("search pagina as tarefas ativas e informa o total")
    void searchPaginaTarefasAtivas() {
        repository.save(task(UUID.randomUUID(), "primeira", TaskStatus.PENDING,
                DEADLINE.minusSeconds(3600), null, null));
        repository.save(task(UUID.randomUUID(), "segunda", TaskStatus.PENDING, DEADLINE, null, null));
        repository.save(task(UUID.randomUUID(), "terceira", TaskStatus.PENDING,
                DEADLINE.plusSeconds(3600), null, null));
        repository.save(task(UUID.randomUUID(), "deletada", TaskStatus.PENDING, null, DELETED_AT));

        PageResult<Task> firstPage = repository.search(ACTIVE_TASKS, new PageRequest(0, 2));

        assertEquals(3, firstPage.totalElements());
        assertEquals(2, firstPage.totalPages());
        assertTrue(firstPage.hasNext());
        assertFalse(firstPage.hasPrevious());
        assertEquals(List.of("primeira", "segunda"),
                firstPage.items().stream().map(Task::getName).toList());

        PageResult<Task> secondPage = repository.search(ACTIVE_TASKS, new PageRequest(1, 2));

        assertEquals(3, secondPage.totalElements());
        assertFalse(secondPage.hasNext());
        assertTrue(secondPage.hasPrevious());
        assertEquals(List.of("terceira"),
                secondPage.items().stream().map(Task::getName).toList());
    }

    @Test
    @DisplayName("search filtra por nome e descrição ignorando caixa")
    void searchFiltraPorNomeEDescricao() {
        repository.save(new Task(UUID.randomUUID(), "Estudar Java", "revisar coleções",
                TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));
        repository.save(new Task(UUID.randomUUID(), "Comprar café", "Java no mercado",
                TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));
        repository.save(new Task(UUID.randomUUID(), "Ler livro", "ficção",
                TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

        PageResult<Task> byName = repository.search(
                new TaskQuery("estudar", null, TaskScope.ACTIVE), new PageRequest(0, 10));
        PageResult<Task> byDescription = repository.search(
                new TaskQuery("mercado", null, TaskScope.ACTIVE), new PageRequest(0, 10));
        PageResult<Task> ignoringCase = repository.search(
                new TaskQuery("JAVA", null, TaskScope.ACTIVE), new PageRequest(0, 10));

        assertEquals(List.of("Estudar Java"), byName.items().stream().map(Task::getName).toList());
        assertEquals(List.of("Comprar café"), byDescription.items().stream().map(Task::getName).toList());
        assertEquals(2, ignoringCase.totalElements());
    }

    @Test
    @DisplayName("search filtra por status")
    void searchFiltraPorStatus() {
        repository.save(task(UUID.randomUUID(), "pendente", TaskStatus.PENDING, null, null));
        repository.save(task(UUID.randomUUID(), "concluída", TaskStatus.COMPLETED, FINISHED_AT, null));
        repository.save(task(UUID.randomUUID(), "cancelada", TaskStatus.CANCELED, null, null));

        PageResult<Task> completed = repository.search(
                new TaskQuery("", TaskStatus.COMPLETED, TaskScope.ACTIVE), new PageRequest(0, 10));

        assertEquals(1, completed.totalElements());
        assertEquals(TaskStatus.COMPLETED, completed.items().getFirst().getStatus());
    }

    @Test
    @DisplayName("search separa tarefas ativas das excluídas")
    void searchSeparaAtivasEDeletadas() {
        repository.save(task(UUID.randomUUID(), "ativa", TaskStatus.PENDING, null, null));
        repository.save(task(UUID.randomUUID(), "deletada", TaskStatus.PENDING, null, DELETED_AT));

        PageResult<Task> active = repository.search(ACTIVE_TASKS, new PageRequest(0, 10));
        PageResult<Task> trashed = repository.search(
                new TaskQuery("", null, TaskScope.TRASHED), new PageRequest(0, 10));

        assertEquals(1, active.totalElements());
        assertEquals(1, trashed.totalElements());
        assertEquals("deletada", trashed.items().getFirst().getName());
    }

    @Test
    @DisplayName("search escapa curingas do LIKE")
    void searchEscapaCuringasDoLike() {
        repository.save(task(UUID.randomUUID(), "100% concluído", TaskStatus.PENDING, null, null));
        repository.save(task(UUID.randomUUID(), "100X concluído", TaskStatus.PENDING, null, null));

        PageResult<Task> result = repository.search(
                new TaskQuery("100%", null, TaskScope.ACTIVE), new PageRequest(0, 10));

        assertEquals(1, result.totalElements());
        assertEquals("100% concluído", result.items().getFirst().getName());
    }

    @Test
    @DisplayName("search ordena por prazo e desempata por uuid")
    void searchOrdenaPorPrazoEUuid() {
        UUID primeiroId = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID segundoId = UUID.fromString("00000000-0000-0000-0000-000000000002");
        repository.save(new Task(segundoId, "segunda", "descrição",
                TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));
        repository.save(new Task(primeiroId, "primeira", "descrição",
                TaskStatus.PENDING, DEADLINE, null, CREATED_AT, UPDATED_AT, null));

        PageResult<Task> result = repository.search(ACTIVE_TASKS, new PageRequest(0, 10));

        assertEquals(List.of(primeiroId, segundoId),
                result.items().stream().map(Task::getUuid).toList());
    }

    @Test
    @DisplayName("search retorna página vazia quando não há tarefas")
    void searchSemResultados() {
        PageResult<Task> active = repository.search(ACTIVE_TASKS, new PageRequest(0, 10));
        PageResult<Task> trashed = repository.search(
                new TaskQuery("", null, TaskScope.TRASHED), new PageRequest(0, 10));

        assertTrue(active.items().isEmpty());
        assertEquals(0, active.totalElements());
        assertEquals(0, active.totalPages());
        assertFalse(active.hasNext());
        assertFalse(active.hasPrevious());
        assertTrue(trashed.items().isEmpty());
    }

    @Test
    @DisplayName("lança QueryException quando o DataSource está fechado")
    void lancaQueryExceptionComDataSourceFechado() {
        HikariDataSource closedDataSource = dataSource();
        closedDataSource.close();
        MySqlTaskRepository failingRepository = new MySqlTaskRepository(closedDataSource);
        UUID uuid = UUID.randomUUID();

        QueryException findByUuidError = assertThrows(QueryException.class,
                () -> failingRepository.findByUuid(uuid));
        assertTrue(findByUuidError.getMessage().contains(uuid.toString()));
        assertThrows(QueryException.class,
                () -> failingRepository.search(ACTIVE_TASKS, new PageRequest(0, 10)));
        assertThrows(QueryException.class,
                () -> failingRepository.save(task(uuid, "nome", TaskStatus.PENDING, null, null)));
    }

    @Test
    @DisplayName("devolve a conexão ao pool entre as operações")
    void devolveConexaoAoPool() {
        UUID uuid = UUID.randomUUID();
        repository.save(task(uuid, "primeira", TaskStatus.PENDING, null, null));

        assertEquals(1, repository.search(ACTIVE_TASKS, new PageRequest(0, 10)).items().size());
        assertTrue(repository.findByUuid(uuid).isPresent());
        assertEquals(0, repository.search(
                new TaskQuery("", null, TaskScope.TRASHED), new PageRequest(0, 10)).items().size());
    }

    private static HikariDataSource dataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(MYSQL.getJdbcUrl());
        config.setUsername(MYSQL.getUsername());
        config.setPassword(MYSQL.getPassword());
        config.setMaximumPoolSize(1);
        config.setConnectionTimeout(2000);
        return new HikariDataSource(config);
    }

    private Task task(UUID uuid, String name, TaskStatus status, Instant finishedAt, Instant deletedAt) {
        return task(uuid, name, status, DEADLINE, finishedAt, deletedAt);
    }

    private Task task(UUID uuid, String name, TaskStatus status, Instant deadline, Instant finishedAt, Instant deletedAt) {
        return new Task(uuid, name, "descrição", status, deadline, finishedAt, CREATED_AT, UPDATED_AT, deletedAt);
    }

    private long countTasks() throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM tasks")) {
            resultSet.next();
            return resultSet.getLong(1);
        }
    }
}
