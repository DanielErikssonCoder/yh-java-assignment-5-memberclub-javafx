package org.example.memberclubjavafx_assignment5.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.components.ItemFormComponent;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Objects;

/**
 * A custom dialog window used for adding a new item or editing an existing one
 * in the inventory. It wraps the {@code ItemFormComponent} for the actual form content.
 */
public class ItemFormDialog extends Dialog<Void> {

    // The component that holds all the input fields and form logic
    private final ItemFormComponent formComponent;

    /**
     * Constructs the custom item form dialog.
     * @param system The central system logic manager.
     * @param rootStack The application's root pane (used for notifications).
     * @param itemToEdit The item to populate the form with for editing, or {@code null} for a new item.
     * @param onSaveSuccess A function to call back to the calling view after a successful save.
     */
    public ItemFormDialog(ClubSystem system, StackPane rootStack, Item itemToEdit, Runnable onSaveSuccess) {

        // Make the dialog window completely transparent
        initStyle(StageStyle.TRANSPARENT);

        // UI Fixes for transparency when the dialog is shown
        setOnShowing(e -> {

            Scene scene = getDialogPane().getScene();

            if (scene != null) {

                scene.setFill(Color.TRANSPARENT);

                Node root = scene.getRoot();

                ViewUtils.addFocusClearListener(scene);

                ViewUtils.addComboBoxFocusHandler(scene);

                if (root != null && root != getDialogPane()) {
                    root.setStyle("-fx-background-color: transparent;");
                }
            }
        });

        // Load CSS and style DialogPane
        DialogPane dialogPane = getDialogPane();

        try {
            // Find and load the custom CSS file
            String cssPath = Objects.requireNonNull(getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css")).toExternalForm();
            dialogPane.getStylesheets().add(cssPath);

        } catch (Exception exception) {
            System.err.println("CSS Error: " + exception.getMessage());
        }

        // Apply custom background/border styles directly to the dialog pane
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setStyle(
                "-fx-background-color: -fx-color-bg-surface;" +
                "-fx-border-color: -fx-color-border-subtle;" +
                "-fx-border-width: 1px;" +
                "-fx-background-radius: 14px;" +
                "-fx-border-radius: 14px;"
        );

        // Set the size
        dialogPane.setMinWidth(1000);
        dialogPane.setPrefWidth(1000);
        dialogPane.setMinHeight(Region.USE_PREF_SIZE);

        // Cap the height so it fits on screens
        dialogPane.setMaxHeight(750);

        // Title bar (The custom header)
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("custom-window-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(40);
        titleBar.setMinHeight(40);
        titleBar.setPadding(new Insets(0, 10, 0, 15));
        titleBar.setStyle("-fx-background-radius: 14px 14px 0 0; -fx-border-radius: 14px 14px 0 0;");

        FontIcon windowIcon = new FontIcon(Feather.BOX);
        windowIcon.setIconColor(Color.web("#60a5fa"));
        windowIcon.setIconSize(14);

        // Set title text based on whether we are adding or editing
        String titleText = (itemToEdit == null) ? "Lägg till Artikel" : "Hantera Artikel";
        Label windowTitle = new Label(titleText);
        windowTitle.getStyleClass().add("window-title-label");

        HBox titleBox = new HBox(10, windowIcon, windowTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // Close button on the title bar
        Button closeWindowBtn = new Button("", new FontIcon(Feather.X));
        closeWindowBtn.getStyleClass().addAll("window-btn", "close-btn");

        // Force close the window when the close button is clicked
        closeWindowBtn.setOnAction(e -> {
            Window window = getDialogPane().getScene().getWindow();
            window.hide();
        });

        titleBar.getChildren().addAll(titleBox, closeWindowBtn);

        // Set the custom title bar as the dialog's header
        dialogPane.setHeader(titleBar);
        setGraphic(null);
        setHeaderText(null);

        // Create the 'ItemFormComponent' and pass a close action on successful save
        this.formComponent = new ItemFormComponent(system, rootStack, () -> {

            // Call the callback to refresh the parent view
            if (onSaveSuccess != null) {
                onSaveSuccess.run();
            }

            // Close the dialog on success
            Window window = getDialogPane().getScene().getWindow();
            window.hide();
        });

        // If we are editing an item, prefill the form fields
        if (itemToEdit != null) {
            formComponent.populateForm(itemToEdit);
        }

        // Set the form component as the main content of the dialog
        dialogPane.setContent(formComponent);

        // Remove standard dialog buttons (since we use buttons inside the form)
        dialogPane.getButtonTypes().clear();
    }
}