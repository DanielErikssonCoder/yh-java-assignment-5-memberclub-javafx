package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.theme.Styles;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.enums.ItemType;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.components.ItemTableComponent;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.Optional;

/**
 * This class creates the main screen for managing the inventory of items.
 * It features a table view, search functionality, category filtering,
 * and options to add, edit, or delete items.
 */
public class ItemView {

    // Variables to access the system logic and show popups/notifications
    private final ClubSystem system;
    private final StackPane rootStack;

    // Custom component for the item table
    private ItemTableComponent tableComponent;

    // Search and filter variables
    private TextField searchField;
    private ComboBox<ItemType> filterTypeBox;
    private FilteredList<Item> filteredItems;
    private Label itemCountLabel;

    /**
     * Constructs a new ItemView.
     * @param system The central system logic manager.
     * @param rootStack The root pane for showing overlays and notifications.
     */
    public ItemView(ClubSystem system, StackPane rootStack) {
        this.system = system;
        this.rootStack = rootStack;
    }

    /**
     * Builds and returns the complete Item Management view.
     * @return The main {@code Parent} node of the view, wrapped in a main panel.
     */
    public Parent getView() {

        // Main layout is a vertical box with some spacing
        VBox layout = new VBox(20);

        // Header Section
        HBox headerBox = new HBox(15);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label title = new Label("Artiklar");
        title.getStyleClass().add("page-title");
        title.setPadding(new Insets(0));

        // Spacer to push search field to the right
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Setup the search field
        searchField = new TextField();
        searchField.setPromptText("Sök artikel");
        searchField.setPrefWidth(250);
        searchField.setPrefHeight(35);
        searchField.setMinHeight(35);

        // Update list when typing
        searchField.textProperty().addListener((o, old, v) -> filterTable());

        // Setup the category filter dropdown
        filterTypeBox = new ComboBox<>();

        // Add a 'null' option to represent 'All Categories'
        filterTypeBox.getItems().add(null);
        filterTypeBox.getItems().addAll(ItemType.values());
        filterTypeBox.setPromptText("Alla artiklar");

        // Use ViewUtils to show Swedish names for Enums
        filterTypeBox.setConverter(ViewUtils.createTranslator());
        filterTypeBox.setPrefHeight(35);
        filterTypeBox.setMinHeight(35);
        filterTypeBox.setOnAction(e -> filterTable());

        // New Item Button
        Button newItemBtn = new Button("Ny Artikel", new FontIcon(Feather.PLUS));
        newItemBtn.getStyleClass().addAll("action-btn", Styles.ACCENT);
        newItemBtn.setPrefHeight(35);
        newItemBtn.setMinHeight(35);

        // Action to open the form dialog to create a new item (passing null)
        newItemBtn.setOnAction(e -> openItemDialog(null));

        // Add everything to the header
        headerBox.getChildren().addAll(title, headerSpacer, searchField, filterTypeBox, newItemBtn);

        // Create the card for the item list
        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("card-glass");
        tableCard.setPadding(new Insets(15, 20, 20, 20));

        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // Header inside the card (Title, Spacer, Reload, Count)
        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label listTitle = new Label("Lager");
        listTitle.getStyleClass().add(Styles.TITLE_4);

        Region cardSpacer = new Region();
        HBox.setHgrow(cardSpacer, Priority.ALWAYS);

        // Reload Button
        Button reloadBtn = new Button("");
        reloadBtn.setGraphic(new FontIcon(Feather.REFRESH_CW));
        reloadBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        reloadBtn.setTooltip(new Tooltip("Ladda om data"));
        reloadBtn.setOnAction(e -> {
            system.loadData();
            refreshTable();
            NotificationFactory.show("Data inläst", "Lagret uppdaterat", NotificationFactory.Type.INFO, rootStack);
        });

        // Item Count Label
        itemCountLabel = new Label("Antal: 0");
        itemCountLabel.getStyleClass().addAll(Styles.TEXT_BOLD);

        cardHeader.getChildren().addAll(listTitle, cardSpacer, itemCountLabel, reloadBtn);

        // Initialize our custom table component
        tableComponent = new ItemTableComponent();
        tableComponent.getTable().setMinHeight(50);
        VBox.setVgrow(tableComponent, Priority.ALWAYS);

        // Right-click menu on table rows
        tableComponent.getTable().setRowFactory(tv -> {
            TableRow<Item> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            // Option to edit the selected item
            MenuItem editItem = new MenuItem("Hantera artikel");
            editItem.setGraphic(new FontIcon(Feather.EDIT_2));
            editItem.setOnAction(e -> openItemDialog(row.getItem()));

            // Option to delete the selected item
            MenuItem deleteItem = new MenuItem("Ta bort artikel");
            deleteItem.setGraphic(new FontIcon(Feather.TRASH));
            deleteItem.getStyleClass().add("danger");
            deleteItem.setOnAction(e -> handleDeleteItem(row.getItem()));

            menu.getItems().addAll(editItem, new SeparatorMenuItem(), deleteItem);

            // Bind the context menu to the row only if the row is not empty
            row.contextMenuProperty().bind(
                    Bindings.when(row.emptyProperty())
                        .then((ContextMenu) null)
                        .otherwise(menu)
            );

            // Double-click action also opens the edit dialog
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    openItemDialog(row.getItem());
                }
            });

            return row;
        });

        tableCard.getChildren().addAll(cardHeader, tableComponent);

        // Add all sections to the main layout
        layout.getChildren().addAll(headerBox, tableCard);

        // Load data and set up filters for the first time
        refreshTable();

        return ViewUtils.wrapInMainPanel(layout);
    }

    /**
     * Opens the {@code ItemFormDialog} for creating a new item or editing an existing one.
     * It applies a blur effect to the background while the dialog is open.
     * @param item The {@code Item} to edit, or {@code null} to create a new one.
     */
    private void openItemDialog(Item item) {

        if (rootStack.getScene() == null) {
            return;
        }

        Scene scene = rootStack.getScene();
        Node appContent = scene.lookup("#app-content");

        // This is a subtle color change when the dialog opens
        scene.setFill(javafx.scene.paint.Color.web("#10151c"));

        // Apply blur to the main application content
        BoxBlur blur = new BoxBlur(10, 10, 3);
        if (appContent != null) {
            appContent.setEffect(blur);
        }

        // Create the dialog, passing the refresh method to update the table upon save
        ItemFormDialog dialog = new ItemFormDialog(system, rootStack, item, this::refreshTable);

        // Logic to center the dialog over the owner window after it is shown
        dialog.setOnShown(e -> {
            Window window = dialog.getDialogPane().getScene().getWindow();
            Window owner = dialog.getOwner();

            if (owner != null) {
                window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
            }
        });

        // Add a transparent pane behind the dialog to catch clicks outside the dialog and close it
        Pane glassPane = new Pane();
        glassPane.setStyle("-fx-background-color: transparent;");
        glassPane.setOnMouseClicked(e -> {
            Window window = dialog.getDialogPane().getScene().getWindow();
            window.hide();
        });

        rootStack.getChildren().add(glassPane);

        // Set modality and owner for the dialog
        dialog.initModality(Modality.NONE);
        dialog.initOwner(rootStack.getScene().getWindow());
        dialog.showAndWait();

        // Remove blur and glass pane after the dialog is closed
        if (appContent != null) {
            appContent.setEffect(null);
        }

        rootStack.getChildren().remove(glassPane);
    }

    /**
     * Handles the deletion of an item, showing a custom confirmation dialog first.
     * It prevents deletion if the item is currently rented.
     * @param item The {@code Item} object to be deleted.
     */
    private void handleDeleteItem(Item item) {

        if (item == null) {
            return;
        }

        // Check if the item is currently rented
        if (item.isRented()) {
            NotificationFactory.show("Fel", "Artikeln är uthyrd och kan inte tas bort", NotificationFactory.Type.ERROR, rootStack);
            return;
        }

        Scene scene = rootStack.getScene();

        if (scene == null) {
            return;
        }

        // Apply a blur effect to the background
        Node appContent = scene.lookup("#app-content");
        BoxBlur blur = new BoxBlur(10, 10, 3);

        if (appContent != null) {
            appContent.setEffect(blur);
        }

        // Custom Dialog setup
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scene.getWindow());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().clear();
        pane.setHeader(null);
        pane.setGraphic(null);
        pane.setPadding(Insets.EMPTY);
        pane.setBackground(Background.EMPTY);
        pane.setBorder(Border.EMPTY);
        pane.getStyleClass().clear();

        VBox content = new VBox(20);
        content.getStyleClass().addAll("card-panel", "modal-box");
        content.setPadding(new Insets(30));
        content.setMaxWidth(550);

        Label header = new Label("Ta bort artikel");
        header.getStyleClass().add(Styles.TITLE_3);

        Label msg = new Label("Är du säker på att du vill ta bort " + item.getName() + "?");
        msg.setWrapText(true);
        msg.setStyle("-fx-font-size: 16px;");

        Button cancel = new Button("Avbryt");
        cancel.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        cancel.setOnAction(e -> {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        });

        Button confirm = new Button("Ta bort", new FontIcon(Feather.TRASH));
        confirm.getStyleClass().addAll("action-btn", "danger-icon-only");
        confirm.setOnAction(e -> {
            dialog.setResult(ButtonType.OK);
            dialog.close();
        });

        HBox buttons = new HBox(15, cancel, confirm);
        buttons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(header, msg, buttons);

        StackPane wrapper = new StackPane(content);
        wrapper.setBackground(Background.EMPTY);
        pane.setContent(wrapper);

        // Add a transparent pane behind the dialog to catch user clicks
        Pane glassPane = new Pane();
        glassPane.setBackground(new Background(new BackgroundFill(javafx.scene.paint.Color.TRANSPARENT, null, null)));
        glassPane.setOnMouseClicked(e -> {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        });

        rootStack.getChildren().add(glassPane);

        // Logic to center the dialog and load CSS after it is shown
        dialog.setOnShown(e -> {

            Scene dialogScene = pane.getScene();

            if (dialogScene != null) {

                dialogScene.setFill(javafx.scene.paint.Color.TRANSPARENT);

                try {
                    String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                    dialogScene.getStylesheets().add(cssPath);

                } catch (Exception ex) {
                    System.err.println("Kunde inte ladda CSS: " + ex.getMessage());
                }

                Window window = dialogScene.getWindow();
                Window owner = dialog.getOwner();

                if (owner != null) {
                    window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                    window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                }

                pane.requestFocus();
            }
        });

        // Show the dialog and wait for result
        Optional<ButtonType> res = dialog.showAndWait();

        // Remove blur and glass pane
        if (appContent != null) {
            appContent.setEffect(null);
        }

        rootStack.getChildren().remove(glassPane);

        // If the user confirmed deletion
        if (res.isPresent() && res.get() == ButtonType.OK) {

            system.getInventory().removeItem(item.getId());
            system.saveAll();

            NotificationFactory.show("Borttagen", "Artikeln raderades", NotificationFactory.Type.SUCCESS, rootStack);

            refreshTable();
        }
    }

    /**
     * Loads the latest list of items from the inventory and updates the table.
     * Also refreshes the filter and count label.
     */
    private void refreshTable() {

        // Get all items and create a list
        ObservableList<Item> masterList = FXCollections.observableArrayList(system.getInventory().getAllItems());

        // Create a filtered list based on the master list
        filteredItems = new FilteredList<>(masterList, p -> true);
        tableComponent.getTable().setItems(filteredItems);
        tableComponent.getTable().refresh();

        // Apply current filter settings
        filterTable();
    }

    /**
     * Filters the item list based on the text in the search field and the
     * selected category in the filter dropdown.
     */
    private void filterTable() {

        if (filteredItems == null) {
            return;
        }

        String search = (searchField.getText() == null) ? "" : searchField.getText().toLowerCase();

        ItemType type = filterTypeBox.getValue();

        filteredItems.setPredicate(item -> {

            // Filter by category if one is selected
            if (type != null && item.getItemType() != type) return false;

            // If no search text, show all items of the selected category
            if (search.isEmpty()) return true;

            // Filter by name or ID containing the search text
            return item.getName().toLowerCase().contains(search) || (item.getId().toLowerCase().contains(search));
        });

        // Update the label showing the number of displayed items
        itemCountLabel.setText("Antal: " + filteredItems.size());
    }
}