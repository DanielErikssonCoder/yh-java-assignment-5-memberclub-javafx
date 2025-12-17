package org.example.memberclubjavafx_assignment5.view.components;

import atlantafx.base.theme.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.example.memberclubjavafx_assignment5.exceptions.RentalNotFoundException;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.Rental;
import org.example.memberclubjavafx_assignment5.model.enums.RentalStatus;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.NotificationFactory;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Consumer;

/**
 * Displays rental history and active rentals in a table.
 * Also handles the return of items and calculation of late fees.
 */
public class RentalHistoryComponent extends VBox {

    // Access to the core system logic
    private final ClubSystem system;

    // Reference to the root layout for showing popups and notifications
    private final StackPane rootStack;

    // Data models for the table
    private final TableView<Rental> table;

    // Holds all the rentals loaded from the system
    private final ObservableList<Rental> masterData = FXCollections.observableArrayList();

    // 'filteredData' is a view of 'masterData' that we can filter (search/hide history)
    private final FilteredList<Rental> filteredData;

    // UI Controls that we need to access in multiple methods
    private TextField searchField;
    private ToggleButton showAllToggle;
    private Button returnBtn;

    // Callback to update the 'Active Rentals' count in the main view
    private Consumer<Integer> onActiveCountChange;

    // Date formatter
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * Constructor. This runs when we create this component.
     * @param system The main system object.
     * @param rootStack The root view for displaying overlays.
     */
    public RentalHistoryComponent(ClubSystem system, StackPane rootStack) {

        this.system = system;
        this.rootStack = rootStack;

        // Use full height available
        VBox.setVgrow(this, Priority.ALWAYS);
        setSpacing(10);

        // Setup data and filter
        filteredData = new FilteredList<>(masterData, p -> true);

        // Create toolbar
        HBox toolbar = createToolbar();

        // Create table
        table = createTable();
        VBox.setVgrow(table, Priority.ALWAYS);

        // Add selection listener
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {

            boolean hasSelection = (newVal != null);

            // Only active rentals can be returned
            boolean isActive = hasSelection && (newVal.getStatus() == RentalStatus.ACTIVE);

            if (returnBtn != null) {
                returnBtn.setDisable(!isActive);
            }
        });

        getChildren().addAll(toolbar, table);

        // Load data immediately
        refreshTable();
    }

    /**
     * Sets a listener that gets notified when the number of active rentals changes.
     */
    public void setOnActiveCountChange(Consumer<Integer> listener) {

        this.onActiveCountChange = listener;

        // Trigger immediately
        refreshTable();
    }

    /**
     * Creates the toolbar containing the search field and action buttons.
     * @return An HBox containing the controls.
     */
    private HBox createToolbar() {

        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        toolbar.setPadding(new Insets(0, 0, 10, 0));

        // Search Field
        searchField = new TextField();
        searchField.setPromptText("Sök ID, medlem...");
        searchField.setPrefWidth(250);

        // When the user types something, run the 'updateFilter' method automatically
        searchField.textProperty().addListener((observable, oldValue, newValue) -> updateFilter());

        // Toggle Button
        showAllToggle = new ToggleButton("Visa Historik");
        showAllToggle.setGraphic(new FontIcon(Feather.CLOCK));
        showAllToggle.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        showAllToggle.setTooltip(new Tooltip("Växla mellan att se endast aktiva eller all historik"));

        // Listen for clicks on the toggle button
        showAllToggle.selectedProperty().addListener((obs, old, val) -> {

            // Re-run the filter logic
            updateFilter();

            // Update the button to reflect the new state
            if (val) {
                showAllToggle.setText("Dölj Historik");
                showAllToggle.getStyleClass().add("active");

            } else {
                showAllToggle.setText("Visa Historik");
                showAllToggle.getStyleClass().remove("active");
            }
        });

        // Spacer to push the next buttons to the right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Reload Button
        Button reloadBtn = new Button("", new FontIcon(Feather.REFRESH_CW));
        reloadBtn.setTooltip(new Tooltip("Ladda om data från fil"));
        reloadBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);

        // When clicked, tell the system to read JSON files (again) and refresh the UI
        reloadBtn.setOnAction(e -> {
            system.loadData();
            refreshTable();
            NotificationFactory.show("Uppdaterad", "Data laddades från fil", NotificationFactory.Type.INFO, rootStack);
        });

        // Return Button
        returnBtn = new Button("Återlämna", new FontIcon(Feather.CORNER_DOWN_LEFT));
        returnBtn.getStyleClass().addAll("action-btn", Styles.ACCENT);

        // Disabled by default until a valid row is selected
        returnBtn.setDisable(true);
        returnBtn.setOnAction(e -> handleReturn());

        toolbar.getChildren().addAll(searchField, showAllToggle, spacer, reloadBtn, returnBtn);
        return toolbar;
    }

    /**
     * Configures and creates the main TableView.
     * @return The configured TableView.
     */
    private TableView<Rental> createTable() {

        TableView<Rental> tableView = new TableView<>();

        // Make columns resize
        tableView.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Removed Tweaks.EDGE_TO_EDGE to get the borders back, matching the other table
        tableView.getStyleClass().add(Styles.STRIPED);

        // Bind the table to our filtered list
        tableView.setItems(filteredData);
        tableView.setPlaceholder(new Label("Inga bokningar hittades"));

        // ID
        TableColumn<Rental, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("rentalId"));
        idCol.setPrefWidth(90);

        // Member
        TableColumn<Rental, String> memberCol = new TableColumn<>("Medlem");
        memberCol.setCellValueFactory(cell -> {
            Member member = system.getMemberRegistry().getMember(cell.getValue().getMemberId());
            return new SimpleStringProperty(member != null ? member.getFirstName() + " " + member.getLastName() : "Okänd");
        });

        // Item
        TableColumn<Rental, String> itemCol = new TableColumn<>("Artikel");
        itemCol.setCellValueFactory(cell -> {
            Item item = system.getInventory().getItem(cell.getValue().getItemId());
            return new SimpleStringProperty(item != null ? item.getName() : "Okänd");
        });

        // Start Date
        TableColumn<Rental, String> startCol = new TableColumn<>("Starttid");
        startCol.setCellValueFactory(cell -> {

            if (cell.getValue().getStartDate() != null) {
                return new SimpleStringProperty(cell.getValue().getStartDate().format(timeFormatter));
            }
            return new SimpleStringProperty("-");
        });

        // Expected Return
        TableColumn<Rental, String> endCol = new TableColumn<>("Förväntad retur");
        endCol.setCellValueFactory(cell -> {

            if (cell.getValue().getExpectedReturnDate() != null) {
                return new SimpleStringProperty(cell.getValue().getExpectedReturnDate().format(timeFormatter));
            }
            return new SimpleStringProperty("-");
        });

        // Cost
        TableColumn<Rental, Double> costCol = new TableColumn<>("Kostnad");
        costCol.setCellValueFactory(new PropertyValueFactory<>("totalCost"));

        // Custom cell factory to format the number as currency
        costCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double price, boolean empty) {

                super.updateItem(price, empty);

                if (empty || price == null) {
                    setText(null);

                } else {
                    setText(String.format("%.2f kr", price));
                }
            }
        });

        // Status
        TableColumn<Rental, RentalStatus> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setMaxWidth(100);
        statusCol.setMinWidth(100);

        // Custom cell factory for status colors and check if the returned item is overdue
        statusCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(RentalStatus status, boolean empty) {

                super.updateItem(status, empty);

                if (empty || status == null) {
                    setText(null);
                    setStyle("");

                } else {

                    boolean isLate = false;

                    // If rental is active, check if the current time is past the due date
                    if (status == RentalStatus.ACTIVE) {

                        Rental rental = getTableRow().getItem();

                        if (rental != null && rental.getExpectedReturnDate() != null && LocalDateTime.now().isAfter(rental.getExpectedReturnDate())) {
                            isLate = true;
                        }
                    }

                    // Apply styles based on the result
                    if (isLate) {
                        setText("Försenad");
                        setStyle("-fx-text-fill: -color-danger-fg; -fx-font-weight: bold;");

                    } else {

                        // Translate the enum to Swedish
                        switch (status) {
                            case ACTIVE -> {
                                setText("Aktiv");
                                setStyle("-fx-text-fill: -color-success-fg; -fx-font-weight: bold;");
                            }
                            case COMPLETED -> {
                                setText("Avslutad");
                                setStyle("-fx-text-fill: -color-fg-default;");
                            }
                            case CANCELLED -> {
                                setText("Avbruten");
                                setStyle("-fx-text-fill: -color-danger-fg;");
                            }
                            default -> {
                                setText(status.toString());
                                setStyle("");
                            }
                        }
                    }
                }
            }
        });

        // Add all columns to the table
        tableView.getColumns().add(statusCol);
        tableView.getColumns().add(memberCol);
        tableView.getColumns().add(itemCol);
        tableView.getColumns().add(startCol);
        tableView.getColumns().add(endCol);
        tableView.getColumns().add(costCol);
        tableView.getColumns().add(idCol);


        // Context menu when right clicking on the table rows
        tableView.setRowFactory(table -> {

            TableRow<Rental> row = new TableRow<>();
            ContextMenu menu = new ContextMenu();

            MenuItem returnItem = new MenuItem("Återlämna", new FontIcon(Feather.CORNER_DOWN_LEFT));

            returnItem.setOnAction(e -> {

                // Select the row and trigger the return action
                tableView.getSelectionModel().select(row.getItem());
                handleReturn();
            });

            menu.getItems().add(returnItem);

            // Only show the menu if the row is not empty
            row.contextMenuProperty().bind(
                    javafx.beans.binding.Bindings.when(row.emptyProperty())
                            .then((ContextMenu) null)
                            .otherwise(menu)
            );

            return row;
        });

        return tableView;
    }

    /**
     * Updates the filter for the list.
     * It checks both the toggle button state and the search field text.
     */
    private void updateFilter() {

        String filterText = (searchField.getText() == null) ? "" : searchField.getText().toLowerCase();

        boolean showHistory = showAllToggle.isSelected();

        filteredData.setPredicate(rental -> {

            // Check status filter
            boolean statusMatch;

            if (showHistory) {

                // Show everything
                statusMatch = true;

            } else {
                statusMatch = (rental.getStatus() == RentalStatus.ACTIVE);
            }

            if (!statusMatch) {
                return false;
            }

            // Check text search filter
            if (filterText.isEmpty()) {
                return true;
            }

            // Fetch member and item to search by their names
            Member member = system.getMemberRegistry().getMember(rental.getMemberId());
            Item item = system.getInventory().getItem(rental.getItemId());

            String memberName = (member != null) ? member.getFirstName() + " " + member.getLastName() : "";
            String itemName = (item != null) ? item.getName() : "";

            // Return true if any of the fields contain the search text
            return rental.getRentalId().toLowerCase().contains(filterText) || memberName.toLowerCase().contains(filterText) || itemName.toLowerCase().contains(filterText);
        });
    }

    /**
     * Handles the return process.
     * Checks if the return is late and asks for confirmation if a fee applies.
     */
    private void handleReturn() {

        Rental rental = table.getSelectionModel().getSelectedItem();

        if (rental == null) {
            return;
        }

        if (rental.getStatus() != RentalStatus.ACTIVE) {
            NotificationFactory.show("Info", "Denna uthyrning är redan klar", NotificationFactory.Type.INFO, rootStack);
            return;
        }

        // Check if late
        if (rental.isLate()) {

            Item item = system.getInventory().getItem(rental.getItemId());

            // Use the rental object to calculate fees
            double penalty = rental.calculatePenaltyFee(item);
            long hoursLate = rental.getHoursLate();

            // Start the custom dialog here
            Scene scene = rootStack.getScene();
            if (scene == null) return;

            // Blur the background to focus on the modal
            Node appContent = scene.lookup("#app-content");
            BoxBlur blur = new BoxBlur(10, 10, 3);
            if (appContent != null) appContent.setEffect(blur);

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(scene.getWindow());
            dialog.initModality(javafx.stage.Modality.NONE);
            dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);

            // Clean up the standard dialog container
            DialogPane pane = dialog.getDialogPane();
            pane.getButtonTypes().clear();
            pane.setHeader(null);
            pane.setGraphic(null);
            pane.setPadding(Insets.EMPTY);
            pane.setBackground(Background.EMPTY);
            pane.setBorder(Border.EMPTY);
            pane.getStyleClass().clear();

            // Create the content layout
            VBox content = new VBox(20);
            content.getStyleClass().addAll("card-panel", "modal-box");
            content.setPadding(new Insets(30));
            content.setMaxWidth(550);

            Label header = new Label("Försenad retur");
            header.getStyleClass().add(Styles.TITLE_3);

            // Use the calculated values here and show the late fee for the user
            Label message = new Label("Sen " + hoursLate + " timmar. Avgift: " + String.format("%.2f", penalty) + " kr.\nReturnera ändå?");
            message.setWrapText(true);
            message.setStyle("-fx-font-size: 16px;");

            Button cancel = new Button("Avbryt");
            cancel.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
            cancel.setOnAction(e -> {
                dialog.setResult(ButtonType.CANCEL);
                dialog.close();
            });

            Button confirm = new Button("Returnera", new FontIcon(Feather.CHECK));
            confirm.getStyleClass().addAll("action-btn", Styles.SUCCESS);
            confirm.setOnAction(e -> {
                dialog.setResult(ButtonType.OK);
                dialog.close();
            });

            HBox buttons = new HBox(15, cancel, confirm);
            buttons.setAlignment(Pos.CENTER);

            content.getChildren().addAll(header, message, buttons);

            // Wrap content in a StackPane
            StackPane wrapper = new StackPane(content);
            wrapper.setBackground(Background.EMPTY);
            pane.setContent(wrapper);

            // Create a glass pane (with a clickable background) to close dialog when clicking outside
            Pane glassPane = new Pane();
            glassPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
            glassPane.setOnMouseClicked(e -> {
                dialog.setResult(ButtonType.CANCEL);
                dialog.close();
            });

            rootStack.getChildren().add(glassPane);

            // When the dialog is shown, ensure it gets the correct CSS and positioning
            dialog.setOnShown(e -> {

                Scene dialogScene = pane.getScene();

                if (dialogScene != null) {

                    dialogScene.setFill(Color.TRANSPARENT);

                    try {
                        String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                        dialogScene.getStylesheets().add(cssPath);

                    } catch (Exception exception) {}

                    // Center the dialog over the parent window
                    javafx.stage.Window window = dialogScene.getWindow();
                    javafx.stage.Window owner = dialog.getOwner();

                    if (owner != null) {
                        window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                        window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                    }
                    pane.requestFocus();
                }
            });

            Optional<ButtonType> type = dialog.showAndWait();

            // Remove blur and the glass pane
            if (appContent != null) appContent.setEffect(null);
            rootStack.getChildren().remove(glassPane);

            // Process return if confirmed
            if (type.isPresent() && type.get() == ButtonType.OK) {
                performReturn(rental);
            }
            return;
        }

        // If not late, return directly
        performReturn(rental);
    }

    /**
     * Executes the actual return in the system.
     */
    private void performReturn(Rental r) {

        try {
            // Call the service to update the status and inventory. Now returns void and throws exceptions if failed.
            system.getRentalService().returnItem(r.getRentalId());

            // If successful (no exception thrown):
            refreshTable();

            // Save changes to file
            system.saveAll();

            NotificationFactory.show("Returnerad", "Artikeln är nu tillgänglig igen.", NotificationFactory.Type.SUCCESS, rootStack);

        } catch (RentalNotFoundException exception) {
            // If the rental ID wasn't found in the system
            NotificationFactory.show("Fel", exception.getMessage(), NotificationFactory.Type.ERROR, rootStack);

        } catch (Exception exception) {
            // Catch any unexpected system errors
            exception.printStackTrace();
            NotificationFactory.show("Fel", "Kunde inte returnera (Systemfel).", NotificationFactory.Type.ERROR, rootStack);
        }
    }

    /**
     * Reloads data from the system and refreshes the table.
     * This ensures the view is updated with the latest file changes.
     */
    public void refreshTable() {

        masterData.clear();

        List<Rental> allRentals = system.getRentalService().getAllRentals();

        if (allRentals != null) {

            // We use a 'Set' to keep track of IDs to prevent duplicate rows
            Set<String> seenIds = new HashSet<>();
            List<Rental> uniqueRentals = new ArrayList<>();

            int activeCount = 0;
            for (Rental rental : allRentals) {

                if (!seenIds.contains(rental.getRentalId())) {
                    uniqueRentals.add(rental);
                    seenIds.add(rental.getRentalId());

                    // Count how many rentals that are active for the dashboard counter
                    if (rental.getStatus() == RentalStatus.ACTIVE) {
                        activeCount++;
                    }
                }
            }

            // Sort 'Active' first, then by date with the newest first
            uniqueRentals.sort(Comparator.comparing((Rental rental) -> rental.getStatus() != RentalStatus.ACTIVE).thenComparing(Rental::getStartDate).reversed());

            masterData.addAll(uniqueRentals);

            // Update 'Active' count in parent view
            if (onActiveCountChange != null) {
                onActiveCountChange.accept(activeCount);
            }
        }

        // Re apply filters and refresh the table view
        updateFilter();
        table.refresh();
    }
}