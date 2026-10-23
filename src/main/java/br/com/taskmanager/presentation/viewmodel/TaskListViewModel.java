package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public final class TaskListViewModel {

    private List<Task> tasks = List.of();
    private String search = "";
    private TaskStatus statusFilter;

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks == null ? List.of() : List.copyOf(tasks);
    }

    public void setSearch(String search) {
        this.search = search == null ? "" : search;
    }

    public void setStatusFilter(TaskStatus statusFilter) {
        this.statusFilter = statusFilter;
    }

    public List<Task> visibleTasks() {
        String needle = search.trim().toLowerCase(Locale.ROOT);
        return tasks.stream()
                .filter(task -> statusFilter == null || task.getStatus() == statusFilter)
                .filter(task -> needle.isEmpty() || task.getName().toLowerCase(Locale.ROOT).contains(needle))
                .sorted(Comparator.comparing(Task::getDeadline))
                .toList();
    }

    public static List<Task> deletedOnly(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getDeletedAt() != null)
                .sorted(Comparator.comparing(Task::getDeletedAt).reversed())
                .toList();
    }

    public static boolean canEdit(Task task) {
        return task != null && task.getDeletedAt() == null;
    }

    public static boolean canStart(Task task) {
        return canEdit(task)
                && (task.getStatus() == TaskStatus.PENDING || task.getStatus() == TaskStatus.CANCELED);
    }

    public static boolean canComplete(Task task) {
        return canEdit(task)
                && (task.getStatus() == TaskStatus.PENDING || task.getStatus() == TaskStatus.IN_PROGRESS);
    }

    public static boolean canCancel(Task task) {
        return canComplete(task);
    }

    public static boolean canDelete(Task task) {
        return canEdit(task);
    }

    public static boolean canRestore(Task task) {
        return task != null && task.getDeletedAt() != null;
    }
}
