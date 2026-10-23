package br.com.taskmanager.infrastructure.repositories;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.application.queries.TaskScope;
import br.com.taskmanager.application.repositories.TaskRepository;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;
import br.com.taskmanager.infrastructure.exceptions.QueryException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class MySqlTaskRepository implements TaskRepository {

    private final DataSource dataSource;

    public MySqlTaskRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Optional<Task> findByUuid(UUID uuid) {
        String sql = "SELECT * FROM tasks WHERE uuid = ?;";
        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, uuid.toString());
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(this.mapRow(resultSet)) : Optional.empty();
            }
        } catch (SQLException throwable) {
            throw new QueryException("Não foi possível buscar a task de uuid " + uuid.toString(), throwable);
        }
    }

    @Override
    public PageResult<Task> search(TaskQuery query, PageRequest page) {
        List<Object> parameters = new ArrayList<>();
        String where = buildWhere(query, parameters);
        String sql = "SELECT * FROM tasks" + where + " ORDER BY deadline ASC, uuid ASC LIMIT ? OFFSET ?";

        try (Connection connection = this.dataSource.getConnection()) {
            long total = count(connection, where, parameters);
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                int index = bind(statement, parameters);
                statement.setInt(index++, page.size());
                statement.setLong(index, page.offset());
                try (ResultSet resultSet = statement.executeQuery()) {
                    List<Task> tasks = new ArrayList<>();
                    while (resultSet.next()) {
                        tasks.add(this.mapRow(resultSet));
                    }
                    return new PageResult<>(tasks, total, page.page(), page.size());
                }
            }
        } catch (SQLException exception) {
            throw new QueryException("Não foi possível consultar as tasks", exception);
        }
    }

    private String buildWhere(TaskQuery query, List<Object> parameters) {
        StringBuilder where = new StringBuilder(" WHERE 1 = 1");

        if (query.scope() == TaskScope.ACTIVE) {
            where.append(" AND deleted_at IS NULL");
        } else if (query.scope() == TaskScope.TRASHED) {
            where.append(" AND deleted_at IS NOT NULL");
        }

        if (query.status() != null) {
            where.append(" AND status = ?");
            parameters.add(query.status().name());
        }

        if (!query.search().isBlank()) {
            where.append(" AND (name LIKE ? ESCAPE '!' OR description LIKE ? ESCAPE '!')");
            String pattern = likePattern(query.search());
            parameters.add(pattern);
            parameters.add(pattern);
        }

        return where.toString();
    }

    private String likePattern(String term) {
        String escaped = term.trim()
                .replace("!", "!!")
                .replace("%", "!%")
                .replace("_", "!_");
        return "%" + escaped + "%";
    }

    private long count(Connection connection, String where, List<Object> parameters) throws SQLException {
        String sql = "SELECT COUNT(*) FROM tasks" + where;
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            bind(statement, parameters);
            try (ResultSet resultSet = statement.executeQuery()) {
                resultSet.next();
                return resultSet.getLong(1);
            }
        }
    }

    private int bind(PreparedStatement statement, List<Object> parameters) throws SQLException {
        int index = 1;
        for (Object parameter : parameters) {
            statement.setObject(index++, parameter);
        }
        return index;
    }

    @Override
    public Task save(Task task) {
        String sql = """
            INSERT INTO tasks
                 (uuid, name, description, status, deadline, finished_at, created_at, updated_at, deleted_at)
             VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) AS new
             ON DUPLICATE KEY UPDATE
                 name = new.name,
                 description = new.description,
                 status = new.status,
                 deadline = new.deadline,
                 finished_at = new.finished_at,
                 updated_at = new.updated_at,
                 deleted_at = new.deleted_at
        """;

        try (Connection connection = this.dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getUuid().toString());
            statement.setString(2, task.getName());
            statement.setString(3, task.getDescription());
            statement.setString(4, task.getStatus().name());
            statement.setTimestamp(5, toTimestamp(task.getDeadline()));
            statement.setTimestamp(6, toTimestamp(task.getFinishedAt()));
            statement.setTimestamp(7, toTimestamp(task.getCreatedAt()));
            statement.setTimestamp(8, toTimestamp(task.getUpdatedAt()));
            statement.setTimestamp(9, toTimestamp(task.getDeletedAt()));
            statement.executeUpdate();
            return task;
        } catch (SQLException e) {
            throw new QueryException("Não foi possível salvar a task de uuid " + task.getUuid(), e);
        }
    }

    private Timestamp toTimestamp(Instant instant) {
        return instant == null ? null : Timestamp.from(instant);
    }

    private Task mapRow(ResultSet resultSet) throws SQLException {
        return Task.fromDatabase(
                UUID.fromString(resultSet.getString("uuid")),
                resultSet.getString("name"),
                resultSet.getString("description"),
                TaskStatus.valueOf(resultSet.getString("status")),
                toInstant(resultSet.getTimestamp("deadline")),
                toInstant(resultSet.getTimestamp("finished_at")),
                toInstant(resultSet.getTimestamp("created_at")),
                toInstant(resultSet.getTimestamp("updated_at")),
                toInstant(resultSet.getTimestamp("deleted_at"))
        );
    }

    private Instant toInstant(Timestamp timestamp) {
        return timestamp == null ? null : timestamp.toInstant();
    }
}
