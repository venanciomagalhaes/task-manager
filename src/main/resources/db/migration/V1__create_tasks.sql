CREATE TABLE tasks (
    uuid        CHAR(36)     NOT NULL PRIMARY KEY,
    name        VARCHAR(255) NOT NULL,
    description TEXT         NULL,
    status      VARCHAR(20)  NOT NULL,
    deadline    DATETIME(6)  NOT NULL,
    finished_at DATETIME(6)  NULL,
    created_at  DATETIME(6)  NOT NULL,
    updated_at  DATETIME(6)  NOT NULL,
    deleted_at  DATETIME(6)  NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
