package com.roadmaptracker.ui;

import com.roadmaptracker.model.LearningItem;
import com.roadmaptracker.model.Phase;
import com.roadmaptracker.model.Topic;
import com.roadmaptracker.service.TrackerService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class MainView {
    private Stage stage;
    private TrackerService trackerService;

    private VBox sidebarContent;
    private Label topicTitleLabel;
    private VBox itemsContainer;
    private Label progressLabel;
    private ProgressBar progressBar;

    private Topic selectedTopic = null;

    // Color palette
    private static final String BG_DARK       = "#0F172A";
    private static final String BG_SIDEBAR    = "#1E293B";
    private static final String BG_CARD       = "#1E293B";
    private static final String BG_ITEM_ROW   = "#273449";
    private static final String ACCENT        = "#38BDF8";
    private static final String ACCENT_HOVER  = "#0EA5E9";
    private static final String TEXT_PRIMARY  = "#F1F5F9";
    private static final String TEXT_MUTED    = "#94A3B8";
    private static final String BORDER_COLOR  = "#334155";
    private static final String STATUS_DONE   = "#22C55E";
    private static final String STATUS_WIP    = "#FACC15";
    private static final String STATUS_TODO   = "#64748B";

    public MainView(Stage stage, TrackerService trackerService) {
        this.stage = stage;
        this.trackerService = trackerService;
    }

    public void show() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: " + BG_DARK + ";");

        // TOP BAR
        root.setTop(buildTopBar());

        // SIDEBAR
        root.setLeft(buildSidebar());

        // MAIN CONTENT
        root.setCenter(buildMainContent());

        Scene scene = new Scene(root, 1000, 650);
        stage.setTitle("Java Roadmap Tracker");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(500);
        stage.show();

        refreshProgress();
    }

    // ─── TOP BAR ────────────────────────────────────────────────────────────────

    private HBox buildTopBar() {
        HBox topBar = new HBox();
        topBar.setPadding(new Insets(14, 24, 14, 24));
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(16);
        topBar.setStyle(
                "-fx-background-color: " + BG_SIDEBAR + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-width: 0 0 1 0;"
        );

        // App title
        Label title = new Label("☕ Java Roadmap Tracker");
        title.setStyle(
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-font-size: 16px;" +
                        "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Progress label
        progressLabel = new Label("0 / 0 completed");
        progressLabel.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(180);
        progressBar.setPrefHeight(8);
        progressBar.setStyle(
                "-fx-accent: " + ACCENT + ";" +
                        "-fx-control-inner-background: " + BORDER_COLOR + ";"
        );

        topBar.getChildren().addAll(title, spacer, progressLabel, progressBar);
        return topBar;
    }

    // ─── SIDEBAR ────────────────────────────────────────────────────────────────

    private ScrollPane buildSidebar() {
        sidebarContent = new VBox(4);
        sidebarContent.setPadding(new Insets(16, 12, 16, 12));
        sidebarContent.setStyle("-fx-background-color: " + BG_SIDEBAR + ";");

        // Header row with "Phases" label + Add Phase button
        HBox sidebarHeader = new HBox();
        sidebarHeader.setAlignment(Pos.CENTER_LEFT);
        Label phasesLabel = new Label("PHASES");
        phasesLabel.setStyle(
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 10px;" +
                        "-fx-font-weight: bold;"
        );
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Button addPhaseBtn = new Button("+ Phase");
        styleSmallButton(addPhaseBtn);
        addPhaseBtn.setOnAction(e -> showAddPhaseDialog());

        sidebarHeader.getChildren().addAll(phasesLabel, headerSpacer, addPhaseBtn);
        sidebarHeader.setPadding(new Insets(0, 0, 10, 0));

        sidebarContent.getChildren().add(sidebarHeader);

        refreshSidebar();

        ScrollPane scrollPane = new ScrollPane(sidebarContent);
        scrollPane.setPrefWidth(220);
        scrollPane.setMinWidth(180);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: " + BG_SIDEBAR + ";" +
                        "-fx-background-color: " + BG_SIDEBAR + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-width: 0 1 0 0;"
        );
        return scrollPane;
    }

    private void refreshSidebar() {
        // Remove everything after the header row (index 0)
        while (sidebarContent.getChildren().size() > 1) {
            sidebarContent.getChildren().remove(1);
        }

        for (Phase phase : trackerService.getPhases()) {
            // Phase label row
            HBox phaseRow = new HBox(6);
            phaseRow.setAlignment(Pos.CENTER_LEFT);
            phaseRow.setPadding(new Insets(6, 8, 6, 8));
            phaseRow.setStyle(
                    "-fx-background-radius: 6;" +
                            "-fx-background-color: transparent;"
            );

            Label phaseIcon = new Label("📁");
            phaseIcon.setStyle("-fx-font-size: 13px;");

            Label phaseLabel = new Label(phase.getTitle());
            phaseLabel.setStyle(
                    "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                            "-fx-font-size: 13px;" +
                            "-fx-font-weight: bold;"
            );

            Region phaseSpacer = new Region();
            HBox.setHgrow(phaseSpacer, Priority.ALWAYS);

            Button addTopicBtn = new Button("+");
            styleSmallButton(addTopicBtn);
            addTopicBtn.setOnAction(e -> showAddTopicDialog(phase));

            phaseRow.getChildren().addAll(phaseIcon, phaseLabel, phaseSpacer, addTopicBtn);
            sidebarContent.getChildren().add(phaseRow);

            // Topics under phase
            for (Topic topic : phase.getTopics()) {
                HBox topicRow = new HBox(6);
                topicRow.setAlignment(Pos.CENTER_LEFT);
                topicRow.setPadding(new Insets(5, 8, 5, 24));
                topicRow.setStyle(
                        "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;" +
                                "-fx-background-color: " +
                                (topic == selectedTopic ? ACCENT_HOVER + "22" : "transparent") + ";"
                );

                Label topicIcon = new Label("└");
                topicIcon.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 11px;");

                Label topicLabel = new Label(topic.getTitle());
                topicLabel.setStyle(
                        "-fx-text-fill: " + (topic == selectedTopic ? ACCENT : TEXT_MUTED) + ";" +
                                "-fx-font-size: 12px;"
                );

                topicRow.getChildren().addAll(topicIcon, topicLabel);

                topicRow.setOnMouseEntered(e -> {
                    if (topic != selectedTopic) {
                        topicRow.setStyle(
                                "-fx-background-radius: 6;" +
                                        "-fx-cursor: hand;" +
                                        "-fx-background-color: " + BORDER_COLOR + ";"
                        );
                    }
                });
                topicRow.setOnMouseExited(e -> {
                    topicRow.setStyle(
                            "-fx-background-radius: 6;" +
                                    "-fx-cursor: hand;" +
                                    "-fx-background-color: " +
                                    (topic == selectedTopic ? ACCENT_HOVER + "22" : "transparent") + ";"
                    );
                });
                topicRow.setOnMouseClicked(e -> {
                    selectedTopic = topic;
                    refreshSidebar();
                    loadTopicContent(topic);
                });

                sidebarContent.getChildren().add(topicRow);
            }

            // Spacer between phases
            Region gap = new Region();
            gap.setPrefHeight(8);
            sidebarContent.getChildren().add(gap);
        }
    }

    // ─── MAIN CONTENT ───────────────────────────────────────────────────────────

    private ScrollPane buildMainContent() {
        itemsContainer = new VBox(10);
        itemsContainer.setPadding(new Insets(24));

        // Default placeholder
        showPlaceholder();

        ScrollPane scroll = new ScrollPane(itemsContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle(
                "-fx-background: " + BG_DARK + ";" +
                        "-fx-background-color: " + BG_DARK + ";"
        );
        return scroll;
    }

    private void showPlaceholder() {
        itemsContainer.getChildren().clear();
        Label placeholder = new Label("← Select a topic to view items");
        placeholder.setStyle(
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 14px;"
        );
        VBox.setMargin(placeholder, new Insets(40, 0, 0, 0));
        itemsContainer.getChildren().add(placeholder);
    }

    private void loadTopicContent(Topic topic) {
        itemsContainer.getChildren().clear();

        // Topic title + Add Item button
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);

        topicTitleLabel = new Label(topic.getTitle());
        topicTitleLabel.setStyle(
                "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-font-size: 20px;" +
                        "-fx-font-weight: bold;"
        );

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button addItemBtn = new Button("+ Add Item");
        addItemBtn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: #0F172A;" +
                        "-fx-font-weight: bold;" +
                        "-fx-font-size: 12px;" +
                        "-fx-padding: 6 14 6 14;" +
                        "-fx-background-radius: 6;" +
                        "-fx-cursor: hand;"
        );
        addItemBtn.setOnMouseEntered(e ->
                addItemBtn.setStyle(
                        "-fx-background-color: " + ACCENT_HOVER + ";" +
                                "-fx-text-fill: #0F172A;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-padding: 6 14 6 14;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;"
                )
        );
        addItemBtn.setOnMouseExited(e ->
                addItemBtn.setStyle(
                        "-fx-background-color: " + ACCENT + ";" +
                                "-fx-text-fill: #0F172A;" +
                                "-fx-font-weight: bold;" +
                                "-fx-font-size: 12px;" +
                                "-fx-padding: 6 14 6 14;" +
                                "-fx-background-radius: 6;" +
                                "-fx-cursor: hand;"
                )
        );
        addItemBtn.setOnAction(e -> showAddItemDialog(topic));

        headerRow.getChildren().addAll(topicTitleLabel, spacer, addItemBtn);
        itemsContainer.getChildren().add(headerRow);

        // Separator
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + BORDER_COLOR + ";");
        VBox.setMargin(sep, new Insets(8, 0, 8, 0));
        itemsContainer.getChildren().add(sep);

        if (topic.getItems().isEmpty()) {
            Label empty = new Label("No items yet. Click '+ Add Item' to get started.");
            empty.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 13px;");
            itemsContainer.getChildren().add(empty);
            return;
        }

        // Column headers
        HBox colHeaders = new HBox();
        colHeaders.setPadding(new Insets(4, 12, 4, 12));
        colHeaders.setSpacing(0);
        Label colTitle  = styledColHeader("Item",   400);
        Label colStatus = styledColHeader("Status", 100);
        Label colDate   = styledColHeader("Date",   140);
        Label colAction = styledColHeader("",        60);
        colHeaders.getChildren().addAll(colTitle, colStatus, colDate, colAction);
        itemsContainer.getChildren().add(colHeaders);

        // Item rows
        for (LearningItem item : topic.getItems()) {
            itemsContainer.getChildren().add(buildItemRow(item, topic));
        }
    }

    private HBox buildItemRow(LearningItem item, Topic topic) {
        HBox row = new HBox();
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(10, 12, 10, 12));
        row.setSpacing(0);
        row.setStyle(
                "-fx-background-color: " + BG_ITEM_ROW + ";" +
                        "-fx-background-radius: 8;"
        );

        // Title
        Label titleLbl = new Label(item.getTitle());
        titleLbl.setPrefWidth(400);
        titleLbl.setWrapText(true);
        titleLbl.setStyle("-fx-text-fill: " + TEXT_PRIMARY + "; -fx-font-size: 13px;");

        // Status badge
        String statusColor = switch (item.getStatus()) {
            case "Done"        -> STATUS_DONE;
            case "In Progress" -> STATUS_WIP;
            default            -> STATUS_TODO;
        };
        Label statusLbl = new Label(item.getStatus());
        statusLbl.setPrefWidth(100);
        statusLbl.setStyle(
                "-fx-text-fill: " + statusColor + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );

        // Date
        Label dateLbl = new Label(
                item.getDateCompleted() == null || item.getDateCompleted().isBlank()
                        ? "—"
                        : item.getDateCompleted()
        );
        dateLbl.setPrefWidth(140);
        dateLbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");

        // Edit button
        Button editBtn = new Button("✏");
        editBtn.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 13px;" +
                        "-fx-cursor: hand;" +
                        "-fx-padding: 0 6 0 6;"
        );
        editBtn.setOnAction(e -> showEditItemDialog(item, topic));

        row.getChildren().addAll(titleLbl, statusLbl, dateLbl, editBtn);
        return row;
    }

    // ─── DIALOGS ────────────────────────────────────────────────────────────────

    private void showAddPhaseDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Phase");
        dialog.setHeaderText("New Phase");
        styleDialog(dialog);

        TextField titleField = new TextField();
        titleField.setPromptText("Phase title (e.g. Phase 1: Foundations)");
        styleTextField(titleField);

        VBox content = new VBox(10, new Label("Title:"), titleField);
        content.setPadding(new Insets(10));
        styleDialogLabel((Label) content.getChildren().get(0));
        dialog.getDialogPane().setContent(content);

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> bt == addBtn ? titleField.getText().trim() : null);
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isBlank()) {
                trackerService.addPhase(new Phase(title));
                refreshSidebar();
                refreshProgress();
            }
        });
    }

    private void showAddTopicDialog(Phase phase) {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Add Topic");
        dialog.setHeaderText("New Topic in: " + phase.getTitle());
        styleDialog(dialog);

        TextField titleField = new TextField();
        titleField.setPromptText("Topic title (e.g. OOP Concepts)");
        styleTextField(titleField);

        VBox content = new VBox(10, new Label("Title:"), titleField);
        content.setPadding(new Insets(10));
        styleDialogLabel((Label) content.getChildren().get(0));
        dialog.getDialogPane().setContent(content);

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> bt == addBtn ? titleField.getText().trim() : null);
        dialog.showAndWait().ifPresent(title -> {
            if (!title.isBlank()) {
                phase.addTopic(new Topic(title));
                refreshSidebar();
                refreshProgress();
            }
        });
    }

    private void showAddItemDialog(Topic topic) {
        Dialog<LearningItem> dialog = new Dialog<>();
        dialog.setTitle("Add Item");
        dialog.setHeaderText("New Item in: " + topic.getTitle());
        styleDialog(dialog);

        TextField titleField = new TextField();
        titleField.setPromptText("e.g. Understand polymorphism");
        styleTextField(titleField);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("To Do", "In Progress", "Done");
        statusBox.setValue("To Do");
        styleComboBox(statusBox);

        TextField dateField = new TextField();
        dateField.setPromptText("yyyy-mm-dd (optional)");
        styleTextField(dateField);

        TextArea notesArea = new TextArea();
        notesArea.setPromptText("Notes (optional)");
        notesArea.setPrefRowCount(3);
        styleTextArea(notesArea);

        VBox content = new VBox(8,
                styledFormLabel("Title:"),     titleField,
                styledFormLabel("Status:"),    statusBox,
                styledFormLabel("Date Completed:"), dateField,
                styledFormLabel("Notes:"),     notesArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType addBtn = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == addBtn && !titleField.getText().isBlank()) {
                LearningItem li = new LearningItem();
                li.setTitle(titleField.getText().trim());
                li.setStatus(statusBox.getValue());
                li.setDateCompleted(dateField.getText().trim());
                li.setNotes(notesArea.getText().trim());
                return li;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(li -> {
            topic.addItem(li);
            loadTopicContent(topic);
            refreshProgress();
        });
    }

    private void showEditItemDialog(LearningItem item, Topic topic) {
        Dialog<LearningItem> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.setHeaderText(item.getTitle());
        styleDialog(dialog);

        TextField titleField = new TextField(item.getTitle());
        styleTextField(titleField);

        ComboBox<String> statusBox = new ComboBox<>();
        statusBox.getItems().addAll("To Do", "In Progress", "Done");
        statusBox.setValue(item.getStatus());
        styleComboBox(statusBox);

        TextField dateField = new TextField(
                item.getDateCompleted() == null ? "" : item.getDateCompleted()
        );
        styleTextField(dateField);

        TextArea notesArea = new TextArea(
                item.getNotes() == null ? "" : item.getNotes()
        );
        notesArea.setPrefRowCount(3);
        styleTextArea(notesArea);

        VBox content = new VBox(8,
                styledFormLabel("Title:"),          titleField,
                styledFormLabel("Status:"),         statusBox,
                styledFormLabel("Date Completed:"), dateField,
                styledFormLabel("Notes:"),          notesArea
        );
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType saveBtn = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveBtn, ButtonType.CANCEL);

        dialog.setResultConverter(bt -> {
            if (bt == saveBtn) {
                item.setTitle(titleField.getText().trim());
                item.setStatus(statusBox.getValue());
                item.setDateCompleted(dateField.getText().trim());
                item.setNotes(notesArea.getText().trim());
                return item;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(li -> {
            loadTopicContent(topic);
            refreshProgress();
        });
    }

    // ─── HELPERS ────────────────────────────────────────────────────────────────

    private void refreshProgress() {
        int total     = trackerService.getTotalItems();
        int completed = trackerService.getCompletedItems();
        progressLabel.setText(completed + " / " + total + " completed");
        progressBar.setProgress(total == 0 ? 0 : (double) completed / total);
    }

    private Label styledColHeader(String text, double width) {
        Label lbl = new Label(text);
        lbl.setPrefWidth(width);
        lbl.setStyle(
                "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-font-weight: bold;"
        );
        return lbl;
    }

    private Label styledFormLabel(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
        return lbl;
    }

    private void styleDialogLabel(Label lbl) {
        lbl.setStyle("-fx-text-fill: " + TEXT_MUTED + "; -fx-font-size: 12px;");
    }

    private void styleSmallButton(Button btn) {
        btn.setStyle(
                "-fx-background-color: " + BORDER_COLOR + ";" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 2 8 2 8;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;"
        );
        btn.setOnMouseEntered(e -> btn.setStyle(
                "-fx-background-color: " + ACCENT + ";" +
                        "-fx-text-fill: #0F172A;" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 2 8 2 8;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;"
        ));
        btn.setOnMouseExited(e -> btn.setStyle(
                "-fx-background-color: " + BORDER_COLOR + ";" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 11px;" +
                        "-fx-padding: 2 8 2 8;" +
                        "-fx-background-radius: 4;" +
                        "-fx-cursor: hand;"
        ));
    }

    private void styleTextField(TextField tf) {
        tf.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;" +
                        "-fx-padding: 6;"
        );
    }

    private void styleTextArea(TextArea ta) {
        ta.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-prompt-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
        );
    }

    private void styleComboBox(ComboBox<?> cb) {
        cb.setStyle(
                "-fx-background-color: " + BG_DARK + ";" +
                        "-fx-text-fill: " + TEXT_PRIMARY + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";" +
                        "-fx-border-radius: 4;" +
                        "-fx-background-radius: 4;"
        );
    }

    private void styleDialog(Dialog<?> dialog) {
        dialog.getDialogPane().setStyle(
                "-fx-background-color: " + BG_CARD + ";" +
                        "-fx-border-color: " + BORDER_COLOR + ";"
        );
        if (dialog.getDialogPane().getScene() != null &&
                dialog.getDialogPane().getScene().getRoot() != null) {
            dialog.getDialogPane().getScene().getRoot().setStyle(
                    "-fx-base: " + BG_DARK + ";"
            );
        }
    }
}