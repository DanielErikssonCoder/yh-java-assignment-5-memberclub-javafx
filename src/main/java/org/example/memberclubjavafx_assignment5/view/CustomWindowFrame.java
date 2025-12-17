package org.example.memberclubjavafx_assignment5.view;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * This is a wrapper class that adds a custom title bar to any given content.
 * This is needed because the main window uses {@code StageStyle.TRANSPARENT}
 * and doesn't have a default title bar.
 */
public class CustomWindowFrame {

    // The main window object
    private final Stage stage;

    // The main system object
    private final ClubSystem system;

    // The handler that allows resizing by dragging the window edges
    private final WindowResizeHandler resizeHandler;

    /**
     * Constructs the custom window frame logic.
     * @param stage The primary stage (window) to control.
     * @param system The central system logic manager.
     * @param resizeHandler The handler responsible for detecting resize edges.
     */
    public CustomWindowFrame(Stage stage, ClubSystem system, WindowResizeHandler resizeHandler) {
        this.stage = stage;
        this.system = system;
        this.resizeHandler = resizeHandler;
    }

    /**
     * Wraps the applications content in a {@code BorderPane} and adds the custom title bar on top.
     * @param content The main content to be displayed.
     * @return A {@code Parent} node (BorderPane) containing the title bar and the content.
     */
    public Parent wrap(Parent content) {
        BorderPane wrapper = new BorderPane();

        // Title bar
        StackPane titleBar = createTitleBar();

        // Attach drag events to the title bar for moving the window
        titleBar.setOnMousePressed(event -> {

            // Only start dragging if the mouse cursor is not showing a resize icon
            if (stage.getScene().getCursor() == Cursor.DEFAULT) {
                resizeHandler.startDragging(event);
            }
        });

        titleBar.setOnMouseDragged(event -> {

            // Only update dragging if the mouse cursor is not showing a resize icon
            if (stage.getScene().getCursor() == Cursor.DEFAULT) {
                resizeHandler.updateDragging(event);
            }
        });

        titleBar.setOnMouseReleased(event -> {

            // Only stop dragging if the mouse cursor is not showing a resize icon
            if (stage.getScene().getCursor() == Cursor.DEFAULT) {
                resizeHandler.stopDragging();
            }
        });

        // Double click on the title bar toggles maximize
        titleBar.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) {
                toggleMaximize();
            }
        });

        // Set the layout
        wrapper.setTop(titleBar);
        wrapper.setCenter(content);

        return wrapper;
    }

    /**
     * Creates the custom title bar component, including the application title,
     * uptime display, and window control buttons (Minimize, Maximize, Close).
     * @return A {@code StackPane} containing the constructed title bar.
     */
    private StackPane createTitleBar() {
        StackPane titleBar = new StackPane();
        titleBar.getStyleClass().add("custom-window-bar");
        titleBar.setPrefHeight(40);
        titleBar.setMinHeight(40);

        // Title
        Label titleLabel = new Label("Wigells Friluftsuthyrning");
        titleLabel.getStyleClass().add("window-title-label");

        // Add the image icon instead of font icon
        HBox leftBox = new HBox(10, titleLabel);
        leftBox.setAlignment(Pos.CENTER_LEFT);

        // Let events pass through if not clicked directly on a child node
        leftBox.setPickOnBounds(false);

        // Uptime display
        Label uptimeLabel = new Label();
        uptimeLabel.getStyleClass().add("window-uptime-label");

        // Bind the label text to the UptimeService's property for automatic updates
        if (system != null && system.getUptimeService() != null) {
            uptimeLabel.textProperty().bind(system.getUptimeService().uptimeProperty());
        }

        // Window controls (Minimize, Maximize, Close)
        HBox controls = new HBox();
        controls.getStyleClass().add("window-controls");
        controls.setAlignment(Pos.CENTER_RIGHT);
        controls.setPickOnBounds(false);

        // Minimize button
        Button minBtn = createNavBtn(Feather.MINUS, e -> stage.setIconified(true));

        // Maximize/Restore button
        Button maxBtn = createNavBtn(Feather.SQUARE, e -> toggleMaximize());

        // Close button
        Button closeBtn = createNavBtn(Feather.X, e -> {

            // Exit the JavaFX application cleanly
            Platform.exit();
            System.exit(0);
        });

        closeBtn.getStyleClass().add("close-btn");

        controls.getChildren().addAll(minBtn, maxBtn, closeBtn);

        // Layout layer (used inside the StackPane to position items)
        BorderPane layout = new BorderPane();
        layout.setLeft(leftBox);
        layout.setRight(controls);
        layout.setPickOnBounds(false);

        // Add both the layout (buttons, title) and the uptime label to the StackPane
        titleBar.getChildren().addAll(layout, uptimeLabel);

        // Center the uptime label inside the StackPane
        StackPane.setAlignment(uptimeLabel, Pos.CENTER);

        return titleBar;
    }

    /**
     * Helper method to create a standardized button for window controls.
     * @param icon The {@code Feather} icon to display.
     * @param action The action to perform when the button is clicked.
     * @return The created button.
     */
    private Button createNavBtn(Feather icon, javafx.event.EventHandler<javafx.event.ActionEvent> action) {
        Button btn = new Button("", new FontIcon(icon));
        btn.getStyleClass().add("window-btn");
        btn.setFocusTraversable(false);
        btn.setOnAction(action);
        return btn;
    }

    /**
     * Toggles the maximize state of the main window and tells the resize
     * handler to reset its boundaries.
     */
    private void toggleMaximize() {
        stage.setMaximized(!stage.isMaximized());
        resizeHandler.initializePositions();
    }
}