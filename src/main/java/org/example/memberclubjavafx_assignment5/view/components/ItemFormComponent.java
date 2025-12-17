package org.example.memberclubjavafx_assignment5.view.components;

import atlantafx.base.theme.Styles;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.*;
import org.example.memberclubjavafx_assignment5.model.fishing.*;
import org.example.memberclubjavafx_assignment5.model.vehicles.*;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.NotificationFactory;
import org.example.memberclubjavafx_assignment5.view.ViewUtils;
import org.example.memberclubjavafx_assignment5.view.strategy.ItemFormStrategy;
import org.example.memberclubjavafx_assignment5.view.strategy.ItemStrategyFactory;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * This component creates the form used to add or update items in the inventory.
 * It changes dynamically depending on which item type is selected.
 */
public class ItemFormComponent extends VBox {

    private final ClubSystem system;
    private final StackPane rootStack;
    private final Runnable onSaveAction;

    // The input fields for basic information
    private final ComboBox<ItemType> typeSelector;
    private final TextField nameField;
    private final TextField priceField;
    private final TextField hourlyPriceField;
    private final TextField brandField;
    private final TextField weightField;
    private final TextField yearField;
    private final ComboBox<Color> colorBox;
    private final ComboBox<Material> materialBox;

    // Action buttons
    private final Button addBtn;
    private final Button clearBtn;

    // Containers for the dynamic part of the form
    private final Label dynamicHeaderLabel;
    private final VBox dynamicFieldsContainer;

    // The item we are currently editing (null if we are creating a new one)
    private Item currentEditingItem = null;

    // Flags to check if we are editing and if data has changed
    private boolean isEditMode = false;
    private boolean dynamicFieldsDirty = false;

    // The strategy that handles specific fields for the chosen type
    private ItemFormStrategy currentStrategy;

    // Standard width for input fields
    private final double FIELD_WIDTH = 160.0;

    /**
     * Constructor that sets up the entire form layout.
     * @param system       The main system to save data to.
     * @param rootStack    The root layout (used for showing notifications).
     * @param onSaveAction A piece of code to run when an item is successfully saved.
     */
    public ItemFormComponent(ClubSystem system, StackPane rootStack, Runnable onSaveAction) {

        this.system = system;
        this.rootStack = rootStack;
        this.onSaveAction = onSaveAction;

        setSpacing(10);
        setPadding(new Insets(40, 40, 30, 40));

        // Create the main layout box (Left side for basic info, and right side for specific type info)
        HBox formLayout = new HBox(30);
        formLayout.setAlignment(Pos.TOP_LEFT);

        VBox leftSideWrapper = new VBox(8);
        HBox.setHgrow(leftSideWrapper, Priority.ALWAYS);

        Label header = new Label("Grundinformation");
        header.getStyleClass().add(Styles.TITLE_4);
        header.setMinHeight(30);
        header.setAlignment(Pos.CENTER_LEFT);

        // Initialize all input fields
        typeSelector = createCombo(ItemType.values());
        nameField = createField();
        brandField = createField();
        priceField = createField();
        hourlyPriceField = createField();
        yearField = createField();
        weightField = createField();
        colorBox = createCombo(Color.values());
        materialBox = createCombo(Material.values());

        // Add listeners to check if user changes anything
        addDirtyListeners();

        // Create a Grid for the fields for 3 columns
        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(12);

        // Configure columns to take up equal width
        ColumnConstraints col1 = new ColumnConstraints(); col1.setPercentWidth(33.33);
        ColumnConstraints col2 = new ColumnConstraints(); col2.setPercentWidth(33.33);
        ColumnConstraints col3 = new ColumnConstraints(); col3.setPercentWidth(33.33);
        grid.getColumnConstraints().addAll(col1, col2, col3);

        grid.setMaxWidth(Double.MAX_VALUE);

        // Add fields to the grid
        grid.add(createLabeledField("Typ", typeSelector), 0, 0);
        grid.add(createLabeledField("Namn", nameField), 1, 0);
        grid.add(createLabeledField("Märke", brandField), 2, 0);

        grid.add(createLabeledField("Pris/Dag", priceField), 0, 1);
        grid.add(createLabeledField("Pris/Tim", hourlyPriceField), 1, 1);
        grid.add(createLabeledField("Årsmodell", yearField), 2, 1);

        grid.add(createLabeledField("Vikt (kg)", weightField), 0, 2);
        grid.add(createLabeledField("Färg", colorBox), 1, 2);
        grid.add(createLabeledField("Material", materialBox), 2, 2);

        leftSideWrapper.getChildren().addAll(header, grid);

        VBox rightSideWrapper = new VBox(8);
        rightSideWrapper.setMinWidth(360);
        HBox.setHgrow(rightSideWrapper, Priority.ALWAYS);

        // Header for the specific specifications
        dynamicHeaderLabel = new Label("Specifikationer");
        dynamicHeaderLabel.getStyleClass().add(Styles.TITLE_4);
        dynamicHeaderLabel.setMinHeight(30);
        dynamicHeaderLabel.setAlignment(Pos.CENTER_LEFT);

        // Hide header initially until a type is selected
        dynamicHeaderLabel.setVisible(false);

        // This container will be filled by the Strategy
        dynamicFieldsContainer = new VBox(10);
        dynamicFieldsContainer.setAlignment(Pos.TOP_LEFT);
        dynamicFieldsContainer.setMaxWidth(Double.MAX_VALUE);
        dynamicFieldsContainer.setPadding(new Insets(0, 5, 0, 0));

        // Put only the fields in the scroll pane in case there are to many fields
        ScrollPane dynamicScroll = new ScrollPane(dynamicFieldsContainer);
        dynamicScroll.setFitToWidth(true);
        dynamicScroll.setStyle("-fx-background-color:transparent; -fx-border-width: 0; -fx-padding: 0;");
        dynamicScroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        dynamicScroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);

        // Make it match the height of the left side grid
        dynamicScroll.maxHeightProperty().bind(leftSideWrapper.heightProperty().subtract(30));
        VBox.setVgrow(dynamicScroll, Priority.ALWAYS);

        // Only show scroll if type is selected
        dynamicScroll.visibleProperty().bind(typeSelector.valueProperty().isNotNull());
        dynamicScroll.managedProperty().bind(dynamicScroll.visibleProperty());

        rightSideWrapper.getChildren().addAll(dynamicHeaderLabel, dynamicScroll);

        // Add both sides to the main layout
        formLayout.getChildren().addAll(leftSideWrapper, rightSideWrapper);
        getChildren().add(formLayout);

        // Buttons (bottom row)
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(15, 0, 0, 0));

        addBtn = new Button("Lägg till i lager");
        addBtn.setGraphic(new FontIcon(Feather.PLUS));
        addBtn.getStyleClass().addAll("action-btn", Styles.SUCCESS);
        addBtn.setOnAction(e -> handleSaveItem());

        clearBtn = new Button("Rensa");
        clearBtn.setGraphic(new FontIcon(Feather.X));
        clearBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        clearBtn.setOnAction(e -> clearForm());

        buttonBox.getChildren().addAll(addBtn, clearBtn);
        getChildren().add(buttonBox);

        // Listen for changes in the Type selector to load the correct strategy
        typeSelector.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {

            if (newVal != null) {
                loadStrategy(newVal);
                checkDirtyState();

            } else {
                dynamicHeaderLabel.setVisible(false);
            }
        });
    }

    /**
     * Helper to create a simple text field with standard width.
     */
    private TextField createField() {
        TextField textField = new TextField();
        textField.setPrefWidth(FIELD_WIDTH);
        return textField;
    }

    /**
     * Helper to create a dropdown menu (ComboBox).
     */
    private <T> ComboBox<T> createCombo(T[] items) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().addAll(items);

        // Translates items to Swedish
        comboBox.setConverter(ViewUtils.createTranslator());
        comboBox.setPrefWidth(FIELD_WIDTH);
        return comboBox;
    }

    /**
     * Helper that wraps a control with a label above it.
     */
    private VBox createLabeledField(String text, Control control) {

        Label label = new Label(text);
        label.getStyleClass().addAll(Styles.TEXT_BOLD, "text-muted");
        label.setStyle("-fx-font-size: 11px;");

        VBox box = new VBox(2, label, control);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }

    /**
     * Loads the specific form fields for the selected item type.
     * Uses the Factory Pattern.
     */
    private void loadStrategy(ItemType type) {

        // Clear old fields
        dynamicFieldsContainer.getChildren().clear();

        // Get the correct strategy from the factory
        currentStrategy = ItemStrategyFactory.getStrategy(type);

        // Update header
        dynamicHeaderLabel.setText("Specifikationer: " + ViewUtils.translate(type));
        dynamicHeaderLabel.setVisible(true);

        // Tell the strategy to render its fields
        currentStrategy.renderFields(dynamicFieldsContainer);

        // Attach listeners to new dynamic fields to detect changes
        attachDynamicListeners(dynamicFieldsContainer);
    }

    /**
     * Helper to add listeners to all inputs in the dynamic container.
     * This makes sure we know if the user changes anything.
     */
    private void attachDynamicListeners(Parent parent) {

        for (Node node : parent.getChildrenUnmodifiable()) {

            if (node instanceof TextField textField) {
                textField.textProperty().addListener((obs, old, val) -> markDynamicDirty());

            } else if (node instanceof ComboBox<?> comboBox) {
                comboBox.valueProperty().addListener((obs, old, val) -> markDynamicDirty());

            } else if (node instanceof CheckBox checkBox) {
                checkBox.selectedProperty().addListener((obs, old, val) -> markDynamicDirty());

            } else if (node instanceof Parent p) {
                attachDynamicListeners(p);
            }
        }
    }

    private void markDynamicDirty() {
        if (isEditMode) {
            this.dynamicFieldsDirty = true;
            checkDirtyState();
        }
    }

    /**
     * Adds listeners to the basic fields to detect changes.
     */
    private void addDirtyListeners() {

        ChangeListener<Object> listener = (obs, old, val) -> checkDirtyState();

        nameField.textProperty().addListener(listener);
        brandField.textProperty().addListener(listener);
        priceField.textProperty().addListener(listener);
        hourlyPriceField.textProperty().addListener(listener);
        yearField.textProperty().addListener(listener);
        weightField.textProperty().addListener(listener);
        colorBox.valueProperty().addListener(listener);
        materialBox.valueProperty().addListener(listener);
        typeSelector.valueProperty().addListener(listener);
    }

    /**
     * Checks if the form has different data than the original item.
     * Enables or disables the 'Update' button based on this.
     */
    private void checkDirtyState() {

        if (!isEditMode) {
            addBtn.setDisable(false);
            return;
        }

        if (currentEditingItem == null) {
            return;
        }

        boolean isDirty = false;

        // Check static fields
        try {

            if (!nameField.getText().equals(currentEditingItem.getName())) {
                isDirty = true;
            }

            if (!brandField.getText().equals(getBrandFromItem(currentEditingItem))) {
                isDirty = true;
            }

            // Use Math.abs for double comparison to avoid precision errors
            if (Math.abs(Double.parseDouble(priceField.getText()) - currentEditingItem.getPricePerDay()) > 0.001) {
                isDirty = true;
            }

            if (Math.abs(Double.parseDouble(hourlyPriceField.getText()) - currentEditingItem.getPricePerHour()) > 0.001) {
                isDirty = true;
            }

            if (Integer.parseInt(yearField.getText()) != currentEditingItem.getYear()) {
                isDirty = true;
            }

            if (Math.abs(Double.parseDouble(weightField.getText()) - getWeightFromItem(currentEditingItem)) > 0.001) {
                isDirty = true;
            }

            if (colorBox.getValue() != currentEditingItem.getColor()) {
                isDirty = true;
            }

            if (materialBox.getValue() != getMaterialFromItem(currentEditingItem)) {
                isDirty = true;
            }

            if (typeSelector.getValue() != currentEditingItem.getItemType()) {
                isDirty = true;
            }

            // Ask the strategy if its fields are modified
            if (currentStrategy != null && currentStrategy.isModified(currentEditingItem)) {
                isDirty = true;
            }

        } catch (NumberFormatException | NullPointerException ignored) {
            // If parsing fails, form is dirty/invalid, but we let validation handle it on save
            isDirty = true;
        }

        addBtn.setDisable(!isDirty);
    }

    // Helper methods to extract values safely from Item subclasses
    private String getBrandFromItem(Item item) {

        if (item instanceof CampingEquipment campingEquipment) {
            return campingEquipment.getBrand();
        }

        if (item instanceof FishingEquipment fishingEquipment) {
            return fishingEquipment.getBrand();
        }

        if (item instanceof WaterVehicle waterVehicle) {
            return waterVehicle.getBrand();
        }
        return "";
    }

    private double getWeightFromItem(Item item) {

        if (item instanceof CampingEquipment campingEquipment) {
            return campingEquipment.getWeight();
        }

        if (item instanceof FishingEquipment fishingEquipment) {
            return fishingEquipment.getWeight();
        }

        if (item instanceof WaterVehicle waterVehicle) {
            return waterVehicle.getWeight();
        }

        return 0.0;
    }

    private Material getMaterialFromItem(Item item) {

        if (item instanceof CampingEquipment campingEquipment) {
            return campingEquipment.getMaterial();
        }

        if (item instanceof FishingEquipment fishingEquipment) {
            return fishingEquipment.getMaterial();
        }

        if (item instanceof WaterVehicle waterVehicle) {
            return waterVehicle.getMaterial();
        }

        return null;
    }

    /**
     * Handles the click on the 'Add' or 'Update' button.
     * Validates input and saves the item.
     */
    private void handleSaveItem() {

        try {

            // Get values from basic fields
            String name = nameField.getText();

            if (name.isEmpty()) {
                throw new IllegalArgumentException("Namn kan inte vara tomt");
            }

            String brand = brandField.getText();

            if (brand.isEmpty()) {
                throw new IllegalArgumentException("Märke kan inte vara tomt");
            }

            double price;
            try {

                price = Double.parseDouble(priceField.getText());

            } catch (Exception exception) {
                throw new IllegalArgumentException("Priset måste vara ett nummer");
            }

            double hourlyPrice;

            try {

                hourlyPrice = Double.parseDouble(hourlyPriceField.getText());

            } catch (Exception exception) {
                throw new IllegalArgumentException("Timpriset måste vara ett nummer");
            }

            int year;

            try {

                year = Integer.parseInt(yearField.getText());

            } catch (Exception exception) {
                throw new IllegalArgumentException("År måste vara ett nummer");
            }

            double weight;

            try {

                weight = Double.parseDouble(weightField.getText());

            } catch (Exception exception) {
                throw new IllegalArgumentException("Vikt måste vara ett nummer");
            }

            Color color = colorBox.getValue();
            Material material = materialBox.getValue();

            if (color == null || material == null) {
                throw new IllegalArgumentException("Färg och material måste väljas");
            }

            if (currentStrategy == null) {
                throw new IllegalArgumentException("Välj en artikeltyp");
            }

            // Check if we are editing an existing item
            if (currentEditingItem != null) {

                // Update basic fields
                currentEditingItem.setName(name);
                currentEditingItem.setPricePerDay(price);
                currentEditingItem.setPricePerHour(hourlyPrice);
                currentEditingItem.setYear(year);
                currentEditingItem.setColor(color);

                // Update subclass specific fields
                updateBaseSubClassFields(currentEditingItem, brand, weight, material);

                // Let the strategy update the dynamic fields
                currentStrategy.updateItem(currentEditingItem);

                // Save to file
                system.saveAll();
                NotificationFactory.show("Uppdaterad", "Ändringar sparade", NotificationFactory.Type.SUCCESS, rootStack);

            } else {

                // Create a completely new item using the strategy
                Item newItem = currentStrategy.createItem(
                        system.getItemIdGenerator(), name, price, hourlyPrice, year, color, material, weight, brand
                );

                // Add to inventory and save
                system.getInventory().addItem(newItem);
                system.saveAll();
                NotificationFactory.show("Sparad", name + " lades till", NotificationFactory.Type.SUCCESS, rootStack);
            }

            // Callback to update the table in the other view
            if (onSaveAction != null) {
                onSaveAction.run();
            }

            clearForm();

        } catch (Exception exception) {
            NotificationFactory.show("Fel", exception.getMessage(), NotificationFactory.Type.ERROR, rootStack);
        }
    }

    /**
     * Helper to update fields that belong to specific subclasses.
     */
    private void updateBaseSubClassFields(Item item, String brand, double weight, Material mat) {

        if (item instanceof CampingEquipment) {
            ((CampingEquipment) item).setBrand(brand);
            ((CampingEquipment) item).setWeight(weight);
            ((CampingEquipment) item).setMaterial(mat);
        }

        else if (item instanceof FishingEquipment) {
            ((FishingEquipment) item).setBrand(brand);
            ((FishingEquipment) item).setWeight(weight);
            ((FishingEquipment) item).setMaterial(mat);
        }

        else if (item instanceof WaterVehicle) {
            ((WaterVehicle) item).setBrand(brand);
            ((WaterVehicle) item).setWeight(weight);
            ((WaterVehicle) item).setMaterial(mat);
        }
    }

    /**
     * This method is called when the user selects a row in the table.
     * It fills the form with that item's data so it can be edited.
     * @param item The item selected in the table.
     */
    public void populateForm(Item item) {

        clearForm();

        currentEditingItem = item;
        isEditMode = true;

        nameField.setText(item.getName());
        priceField.setText(String.valueOf(item.getPricePerDay()));
        hourlyPriceField.setText(String.valueOf(item.getPricePerHour()));
        yearField.setText(String.valueOf(item.getYear()));
        colorBox.setValue(item.getColor());
        typeSelector.setValue(item.getItemType());

        // Fill subclass fields using pattern matching
        switch (item) {

            case CampingEquipment campingEquipment -> {
                brandField.setText(campingEquipment.getBrand());
                weightField.setText(String.valueOf(campingEquipment.getWeight()));
                materialBox.setValue(campingEquipment.getMaterial());
            }

            case FishingEquipment fishingEquipment -> {
                brandField.setText(fishingEquipment.getBrand());
                weightField.setText(String.valueOf(fishingEquipment.getWeight()));
                materialBox.setValue(fishingEquipment.getMaterial());
            }

            case WaterVehicle waterVehicle -> {
                brandField.setText(waterVehicle.getBrand());
                weightField.setText(String.valueOf(waterVehicle.getWeight()));
                materialBox.setValue(waterVehicle.getMaterial());
            }

            default -> {}
        }

        // Use strategy to populate dynamic fields
        if (currentStrategy != null) {
            currentStrategy.populateFields(item);
        }

        // Change button to 'Update' mode
        addBtn.setText("Uppdatera");
        addBtn.getStyleClass().removeAll(Styles.SUCCESS);
        addBtn.getStyleClass().add(Styles.ACCENT);
        addBtn.setGraphic(new FontIcon(Feather.SAVE));

        addBtn.setDisable(true);
        dynamicFieldsDirty = false;
    }

    /**
     * Resets the form to its empty state.
     * Clears all fields and resets mode to 'Add'.
     */
    public void clearForm() {

        currentEditingItem = null;
        isEditMode = false;
        dynamicFieldsDirty = false;

        nameField.clear();
        brandField.clear();
        priceField.clear();
        hourlyPriceField.clear();
        weightField.clear();
        yearField.clear();
        colorBox.getSelectionModel().clearSelection();
        materialBox.getSelectionModel().clearSelection();
        typeSelector.getSelectionModel().clearSelection();

        if (dynamicFieldsContainer != null) {
            dynamicFieldsContainer.getChildren().clear();
        }

        dynamicHeaderLabel.setVisible(false);

        currentStrategy = null;

        // Reset button to 'Add' mode
        addBtn.setText("Lägg till i lager");
        addBtn.getStyleClass().removeAll(Styles.ACCENT);
        addBtn.getStyleClass().add(Styles.SUCCESS);
        addBtn.setGraphic(new FontIcon(Feather.PLUS));
        addBtn.setDisable(false);
    }
}