# Task Manager

Gerenciador de tarefas em Java 25 com interface JavaFX e persistência MySQL.

## Requisitos

- JDK 25
- Docker e Docker Compose
- Maven Wrapper (incluído no projeto)

## Configuração

Crie o arquivo `.env` a partir do exemplo e ajuste as credenciais:

```bash
cp .env.example .env
```

Suba o banco (a migration `V1__create_tasks.sql` é aplicada na primeira inicialização):

```bash
docker compose up -d
```

## Executar a interface

```bash
./mvnw javafx:run
```

A aplicação abre com três áreas bem separadas na barra lateral:

- **Listar tarefas**: busca, filtro por status, detalhes e ações (editar, iniciar, concluir, cancelar, excluir).
- **Nova tarefa**: formulário de criação com validação e prazo no formato `dd/MM/aaaa`.
- **Tarefas excluídas**: lixeira com restauração.

A janela abre maximizada. Pressione `F11` para alternar a tela cheia e `Esc` para sair dela.

## Testes

```bash
./mvnw test
```

Os testes de integração usam Testcontainers e exigem Docker em execução.

## Arquitetura

```
domain          entidade Task, regras de status e soft delete
application     use cases, DTOs e porta TaskRepository
infrastructure  MySQL (HikariCP), migration e Docker Compose
presentation    JavaFX: shell, telas, view models e serviços de UI
```

A direção das dependências aponta para o domínio: `presentation` e `infrastructure` dependem de `application`, que depende apenas de `domain`.
