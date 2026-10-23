package br.com.taskmanager.presentation.viewmodel;

import br.com.taskmanager.application.queries.PageRequest;
import br.com.taskmanager.application.queries.PageResult;
import br.com.taskmanager.application.queries.TaskQuery;
import br.com.taskmanager.application.queries.TaskScope;
import br.com.taskmanager.domain.entities.Task;
import br.com.taskmanager.domain.enums.TaskStatus;

import java.util.List;

public final class TaskListViewModel {

    public static final int DEFAULT_PAGE_SIZE = 10;

    private final TaskScope scope;
    private final int pageSize;
    private TaskQuery query;
    private PageResult<Task> page;

    public TaskListViewModel() {
        this(TaskScope.ACTIVE);
    }

    public TaskListViewModel(TaskScope scope) {
        this(scope, DEFAULT_PAGE_SIZE);
    }

    public TaskListViewModel(TaskScope scope, int pageSize) {
        this.scope = scope;
        this.pageSize = pageSize;
        this.query = new TaskQuery("", null, scope);
        this.page = emptyPage();
    }

    public void setSearch(String search) {
        this.query = new TaskQuery(search, this.query.status(), this.scope);
        resetPage();
    }

    public void setStatus(TaskStatus status) {
        this.query = new TaskQuery(this.query.search(), status, this.scope);
        resetPage();
    }

    public void clearFilters() {
        this.query = new TaskQuery("", null, this.scope);
        resetPage();
    }

    public TaskQuery query() {
        return query;
    }

    public PageRequest request() {
        return new PageRequest(page.page(), pageSize);
    }

    public PageRequest nextRequest() {
        return new PageRequest(page.page() + 1, pageSize);
    }

    public PageRequest previousRequest() {
        return new PageRequest(Math.max(0, page.page() - 1), pageSize);
    }

    public void apply(PageResult<Task> result) {
        this.page = result;
    }

    public PageResult<Task> page() {
        return page;
    }

    public boolean hasNext() {
        return page.hasNext();
    }

    public boolean hasPrevious() {
        return page.hasPrevious();
    }

    public String pageLabel() {
        return "Página " + (page.page() + 1) + " de " + Math.max(1, page.totalPages());
    }

    public String totalLabel() {
        return page.totalElements() + (page.totalElements() == 1 ? " tarefa" : " tarefas");
    }

    private void resetPage() {
        this.page = emptyPage();
    }

    private PageResult<Task> emptyPage() {
        return new PageResult<>(List.of(), 0, 0, pageSize);
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
