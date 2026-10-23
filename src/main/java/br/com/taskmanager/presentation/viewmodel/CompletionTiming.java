package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;

public enum CompletionTiming {

    ON_TIME("No prazo", "timing-on-time"),
    LATE("Atrasada", "timing-late");

    private final String label;
    private final String styleClass;

    CompletionTiming(String label, String styleClass) {
        this.label = label;
        this.styleClass = styleClass;
    }

    public String label() {
        return label;
    }

    public String styleClass() {
        return styleClass;
    }

    public static CompletionTiming of(Task task) {
        if (task == null || task.getFinishedAt() == null) {
            return null;
        }
        return task.getFinishedAt().isAfter(task.getDeadline()) ? LATE : ON_TIME;
    }
}
