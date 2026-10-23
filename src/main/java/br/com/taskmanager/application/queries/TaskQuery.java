package br.com.taskmanager.application.queries;

import br.com.taskmanager.domain.enums.TaskStatus;

public record TaskQuery(String search, TaskStatus status, TaskScope scope) {

    public TaskQuery {
        search = search == null ? "" : search.trim();
        scope = scope == null ? TaskScope.ACTIVE : scope;
    }
}
