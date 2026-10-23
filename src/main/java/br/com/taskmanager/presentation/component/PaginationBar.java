package br.com.taskmanager.presentation.component;

import br.com.taskmanager.presentation.viewmodel.TaskListViewModel;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public final class PaginationBar extends HBox {

    private final Button previousButton = new Button("‹ Anterior");
    private final Button nextButton = new Button("Próxima ›");
    private final Label summary = new Label();

    private boolean busy;
    private boolean hasPrevious;
    private boolean hasNext;

    public PaginationBar(Runnable onPrevious, Runnable onNext) {
        super(8);
        setAlignment(Pos.CENTER_LEFT);
        getStyleClass().add("pagination-bar");

        previousButton.getStyleClass().add("secondary-button");
        nextButton.getStyleClass().add("secondary-button");
        previousButton.setOnAction(event -> onPrevious.run());
        nextButton.setOnAction(event -> onNext.run());

        summary.getStyleClass().add("pagination-summary");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        getChildren().addAll(previousButton, nextButton, spacer, summary);
        refreshState();
    }

    public void update(TaskListViewModel viewModel) {
        this.hasPrevious = viewModel.hasPrevious();
        this.hasNext = viewModel.hasNext();
        summary.setText(viewModel.pageLabel() + "  •  " + viewModel.totalLabel());
        refreshState();
    }

    public void setBusy(boolean busy) {
        this.busy = busy;
        refreshState();
    }

    private void refreshState() {
        previousButton.setDisable(busy || !hasPrevious);
        nextButton.setDisable(busy || !hasNext);
    }
}
