package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.enums.RentalPeriod;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.components.RentalBookingComponent.CartItem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A dialog window used to create a single item entry for a new rental booking.
 * It allows the user to select a member, an item, the rental period, and the duration.
 * This dialog uses custom styling and is fixed in place.
 */
public class BookingFormDialog extends Dialog<CartItem> {

    private final ClubSystem system;

    // UI Controls for input
    private ComboBox<Member> memberComboBox;
    private ComboBox<Item> itemComboBox;
    private ComboBox<RentalPeriod> rentalPeriodComboBox;
    private TextField durationField;

    // Button to add the item to the cart
    private final Button addButton;

    /**
     * Constructs the booking form dialog, setting up all the visual elements and validation logic.
     * @param system The central system logic manager.
     */
    public BookingFormDialog(ClubSystem system) {

        this.system = system;

        // Make the dialog window completely transparent
        initStyle(StageStyle.TRANSPARENT);

        // UI Fixes for transparency and setting initial focus
        setOnShowing(e -> {

            Scene scene = getDialogPane().getScene();

            if (scene != null) {
                scene.setFill(Color.TRANSPARENT);

                // Set root background to transparent if needed
                Node root = scene.getRoot();

                if (root != null && root != getDialogPane()) {
                    root.setStyle("-fx-background-color: transparent;");
                }

                // Hide the default button bar background if it exists
                Node buttonBar = getDialogPane().lookup(".button-bar");

                if (buttonBar != null) {
                    buttonBar.setStyle("-fx-background-color: transparent;");
                }

                ViewUtils.addFocusClearListener(scene);

                ViewUtils.addComboBoxFocusHandler(scene);
            }
        });

        // Load CSS and style 'DialogPane'
        DialogPane dialogPane = getDialogPane();

        try {
            // Load the custom stylesheet
            String cssPath = Objects.requireNonNull(getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css")).toExternalForm();
            dialogPane.getStylesheets().add(cssPath);

        } catch (Exception e) {
            System.err.println("CSS Error: " + e.getMessage());
        }

        // Apply custom border and background styling
        dialogPane.getStyleClass().add("dialog-pane");
        dialogPane.setStyle(
                "-fx-background-color: -fx-color-bg-surface;" +
                        "-fx-border-color: -fx-color-border-subtle;" +
                        "-fx-border-width: 1px;" +
                        "-fx-background-radius: 14px;" +
                        "-fx-border-radius: 14px;"
        );

        // Set the preferred width for a good layout
        dialogPane.setPrefWidth(500);

        // Title bar (custom header)
        HBox titleBar = new HBox();
        titleBar.getStyleClass().add("custom-window-bar");
        titleBar.setAlignment(Pos.CENTER_LEFT);
        titleBar.setPrefHeight(40);
        titleBar.setMinHeight(40);
        titleBar.setPadding(new Insets(0, 10, 0, 15));
        titleBar.setStyle("-fx-background-radius: 14px 14px 0 0; -fx-border-radius: 14px 14px 0 0;");

        FontIcon windowIcon = new FontIcon(Feather.PLUS_CIRCLE);
        windowIcon.setIconColor(Color.web("#60a5fa"));
        windowIcon.setIconSize(14);

        Label windowTitle = new Label("Ny Bokning");
        windowTitle.getStyleClass().add("window-title-label");

        HBox titleBox = new HBox(10, windowIcon, windowTitle);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(titleBox, Priority.ALWAYS);

        // Close button
        Button closeWindowBtn = new Button("", new FontIcon(Feather.X));
        closeWindowBtn.getStyleClass().addAll("window-btn", "close-btn");
        closeWindowBtn.setOnAction(e -> {

            // Set the result to null if closed via the button
            setResult(null);
            close();
        });

        titleBar.getChildren().addAll(titleBox, closeWindowBtn);

        // Set the custom title bar as the dialog's header
        dialogPane.setHeader(titleBar);
        setGraphic(null);
        setHeaderText(null);

        // Form content (GridPane Layout)
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(20);
        grid.setPadding(new Insets(30, 40, 20, 30));

        // Column for labels
        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setMinWidth(Region.USE_PREF_SIZE);
        labelCol.setHgrow(Priority.NEVER);

        // Column for input fields
        ColumnConstraints inputCol = new ColumnConstraints();
        inputCol.setHgrow(Priority.ALWAYS);

        grid.getColumnConstraints().addAll(labelCol, inputCol);

        // Instruction text at the top
        Label instructionLabel = new Label("Välj medlem och artikel");
        instructionLabel.getStyleClass().add(Styles.TEXT_MUTED);
        grid.add(instructionLabel, 0, 0, 2, 1);

        // Initialize all the input fields and attach listeners
        setupFields();

        // Member selection
        grid.add(new Label("Medlem:"), 0, 1);
        grid.add(memberComboBox, 1, 1);

        // Rental Period (Hourly/Daily)
        grid.add(new Label("Period:"), 0, 2);
        grid.add(rentalPeriodComboBox, 1, 2);

        // Duration (Number of hours or days)
        grid.add(new Label("Längd:"), 0, 3);
        grid.add(durationField, 1, 3);

        // Item selection
        grid.add(new Label("Artikel:"), 0, 4);
        grid.add(itemComboBox, 1, 4);

        // Set the grid as the main content
        dialogPane.setContent(grid);

        // Define a custom button type for 'Add'
        ButtonType addButtonType = new ButtonType("Lägg till", ButtonBar.ButtonData.OK_DONE);
        dialogPane.getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // Style the Add Button
        addButton = (Button) dialogPane.lookupButton(addButtonType);
        addButton.getStyleClass().addAll("action-btn", Styles.SUCCESS);
        addButton.setGraphic(new FontIcon(Feather.CHECK));

        // Disabled until form is valid
        addButton.setDisable(true);

        // Style the Cancel Button
        Button cancelButton = (Button) dialogPane.lookupButton(ButtonType.CANCEL);
        cancelButton.setText("Avbryt");
        cancelButton.getStyleClass().addAll("action-btn", "button-outlined");
        cancelButton.setGraphic(new FontIcon(Feather.X));

        // Define what happens when a button is clicked
        setResultConverter(dialogButton -> {

            if (dialogButton == addButtonType) {

                // Return the new CartItem if the 'Add' button was pressed
                return createCartItem();
            }
            // Return null for Cancel or Close
            return null;
        });
    }

    /**
     * Initializes all the ComboBoxes and the duration field with data and converters.
     * Also attaches listeners for form validation and field logic.
     */
    private void setupFields() {

        // Member ComboBox setup
        memberComboBox = new ComboBox<>();
        memberComboBox.setPromptText("Välj medlem...");
        memberComboBox.setMaxWidth(Double.MAX_VALUE);
        memberComboBox.getItems().setAll(system.getMemberRegistry().getAllMembers());

        // Create a custom cell factory to display Name on the left and Badge on the right
        Callback<ListView<Member>, ListCell<Member>> memberCellFactory = lv -> new ListCell<>() {
            @Override
            protected void updateItem(Member member, boolean empty) {
                super.updateItem(member, empty);

                if (empty || member == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    // Create a horizontal box to hold content
                    HBox root = new HBox(10);
                    root.setAlignment(Pos.CENTER_LEFT);
                    root.setMaxWidth(Double.MAX_VALUE);

                    // Name Label
                    Label nameLabel = new Label(member.getFirstName() + " " + member.getLastName());
                    nameLabel.setStyle("-fx-text-fill: -color-fg-default;");

                    // Spacer to push the badge to the right
                    Region spacer = new Region();
                    HBox.setHgrow(spacer, Priority.ALWAYS);

                    // Determine badge text
                    String levelText = switch (member.getMembershipLevel()) {
                        case PREMIUM -> "Premium";
                        case STUDENT -> "Student";
                        case STANDARD -> "Standard";
                    };

                    // Create badge label with style
                    Label badge = new Label(levelText);
                    badge.getStyleClass().add("badge");
                    badge.setStyle("-fx-text-fill: #111827;");

                    // Add color based on level
                    switch (member.getMembershipLevel()) {
                        case PREMIUM -> badge.getStyleClass().add("badge-warning");
                        case STUDENT -> badge.getStyleClass().add("badge-info");
                        case STANDARD -> badge.getStyleClass().add("badge-success");
                    }

                    // Add elements to the row
                    root.getChildren().addAll(nameLabel, spacer, badge);

                    setGraphic(root);
                    setText(null);
                }
            }
        };

        // Apply the custom style to the dropdown list
        memberComboBox.setCellFactory(memberCellFactory);

        // Apply the same style to the selected button (when the menu is closed)
        memberComboBox.setButtonCell(memberCellFactory.call(null));

        // Item ComboBox setup
        itemComboBox = new ComboBox<>();
        itemComboBox.setPromptText("Välj Artikel...");
        itemComboBox.setMaxWidth(Double.MAX_VALUE);

        // Load all items (availability is checked in 'RentalBookingComponent')
        itemComboBox.getItems().setAll(system.getInventory().getAllItems());

        // Rental period ComboBox setup
        rentalPeriodComboBox = new ComboBox<>();
        rentalPeriodComboBox.getItems().setAll(RentalPeriod.values());
        rentalPeriodComboBox.getSelectionModel().select(RentalPeriod.DAILY);

        // Custom converter to show 'Days' or 'Hours'
        rentalPeriodComboBox.setConverter(new StringConverter<>() {

            @Override
            public String toString(RentalPeriod rentalPeriod) {
                if (rentalPeriod == null) {
                    return "";
                }

                return rentalPeriod == RentalPeriod.HOURLY ? "Timmar" : "Dygn";
            }

            @Override
            public RentalPeriod fromString(String string) {
                return null;
            }
        });

        // Duration field setup
        durationField = new CustomTextField();
        durationField.setPromptText("Antal dygn");

        // Listeners for validation (Check form whenever a value changes)
        memberComboBox.valueProperty().addListener((obs, old, val) -> validateForm());
        itemComboBox.valueProperty().addListener((obs, old, val) -> validateForm());
        rentalPeriodComboBox.valueProperty().addListener((obs, old, val) -> validateForm());
        durationField.textProperty().addListener((obs, old, val) -> validateForm());

        // Listener to change the prompt text and price display when period changes
        rentalPeriodComboBox.getSelectionModel().selectedItemProperty().addListener((obs, old, val) -> {

            boolean isHourly = (val == RentalPeriod.HOURLY);

            // Change prompt text to hours or days
            durationField.setPromptText(isHourly ? "Antal timmar" : "Antal dygn");

            // Update the item list converter to show the correct price (per hour or per day)
            updateItemComboDisplay(isHourly);
        });

        // Initial setup for item combo display (start with daily price)
        updateItemComboDisplay(false);
    }

    /**
     * Checks all form fields for valid input and enables or disables the 'Add' button.
     * The form is valid if all fields are selected or filled and the duration is a positive integer.
     */
    private void validateForm() {
        boolean isMemberSelected = memberComboBox.getValue() != null;
        boolean isItemSelected = itemComboBox.getValue() != null;
        boolean isPeriodSelected = rentalPeriodComboBox.getValue() != null;

        boolean isDurationValid = false;

        try {

            int duration = Integer.parseInt(durationField.getText());

            // Duration must be greater than 0
            if (duration > 0) isDurationValid = true;

        } catch (NumberFormatException ignored) {
            // Invalid number input
        }

        if (addButton != null) {
            // Enable the button only if ALL checks pass
            addButton.setDisable(!(isMemberSelected && isItemSelected && isPeriodSelected && isDurationValid));
        }
    }

    /**
     * Creates a new {@code CartItem} object from the form data.
     * This method is called when the user successfully submits the dialog.
     * @return A new {@code CartItem} with selected item, duration, and period, or {@code null} if validation failed.
     */
    private CartItem createCartItem() {
        Item item = itemComboBox.getValue();
        RentalPeriod period = rentalPeriodComboBox.getValue();
        String durStr = durationField.getText();

        // Should be safe by validateForm, but a double check
        if (item == null || durStr.isEmpty() || period == null) {
            return null;
        }

        try {
            int duration = Integer.parseInt(durStr);
            return new CartItem(item, duration, period);

        } catch (NumberFormatException e) {
            // Should not happen if validateForm() passed, but for safety
            return null;
        }
    }

    /**
     * Gets the currently selected member from the member ComboBox.
     * @return The selected {@code Member} object.
     */
    public Member getSelectedMember() {
        return memberComboBox.getValue();
    }

    /**
     * Updates the custom text shown in the item selection ComboBox.
     * The text includes the item's name, price per hour/day (based on selection), and status.
     * @param showHourlyPrice If {@code true}, displays price per hour, otherwise, displays price per day.
     */
    private void updateItemComboDisplay(boolean showHourlyPrice) {

        // Custom converter that formats the Item object into a string
        StringConverter<Item> converter = new StringConverter<>() {

            @Override
            public String toString(Item item) {

                if (item == null) {
                    return null;
                }

                // Select the correct price and unit based on the flag
                double price = showHourlyPrice ? item.getPricePerHour() : item.getPricePerDay();
                String unit = showHourlyPrice ? "kr/tim" : "kr/dygn";

                // Format: Name (price unit), Status (in swedish)
                return String.format("%s (%.2f %s) - %s", item.getName(), price, unit, ViewUtils.translate(item.getStatus()));
            }

            @Override
            public Item fromString(String string) {
                return null;
            }
        };

        // Apply the new converter
        itemComboBox.setConverter(converter);

        // This forces the ComboBox to redraw its current selection and list with the new price and unit format
        List<Item> items = new ArrayList<>(itemComboBox.getItems());
        itemComboBox.getItems().clear();
        itemComboBox.getItems().addAll(items);
    }
}