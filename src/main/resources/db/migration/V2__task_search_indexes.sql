CREATE INDEX idx_tasks_deleted_deadline ON tasks (deleted_at, deadline);
CREATE INDEX idx_tasks_status_deadline ON tasks (status, deadline);
