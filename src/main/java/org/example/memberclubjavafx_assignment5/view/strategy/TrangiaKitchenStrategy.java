package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.TrangiaKitchen;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Trangia Kitchens.
 * It adds fields for the number of burners and fuel type.
 */
public class TrangiaKitchenStrategy extends BaseStrategy {

    // Specific input fields for Trangia kitchens
    private TextField burnersField;
    private ComboBox<FuelType> fuelBox;

    /**
     * Tells the system that this strategy handles Trangia Kitchens.
     * @return The ItemType constant for Trangia kitchens.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.TRANGIA_KITCHEN;
    }

    /**
     * Creates and displays the input fields for burners and fuel type.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        burnersField = createTextField("Antal");
        fuelBox = createComboBox(FuelType.values());

        // Add them to the grid with Swedish labels
        addField("Brännare", burnersField);
        addField("Bränsletyp", fuelBox);
    }

    /**
     * Fills the fields with data from an existing Trangia kitchen.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof TrangiaKitchen trangiaKitchen) {
            burnersField.setText(String.valueOf(trangiaKitchen.getBurners()));
            fuelBox.setValue(trangiaKitchen.getFuelType());
        }
    }

    /**
     * Creates a new TrangiaKitchen object using data from the form.
     * @return A new TrangiaKitchen instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new TrangiaKitchen(gen.generateTrangiaId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(burnersField), fuelBox.getValue());
    }

    /**
     * Updates an existing Trangia kitchen with the values from the input fields.
     * @param item The kitchen to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof TrangiaKitchen trangiaKitchen) {
            trangiaKitchen.setBurners(getInt(burnersField));
            trangiaKitchen.setFuelType(fuelBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the burners or fuel type.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof TrangiaKitchen trangiaKitchen)) {
            return false;
        }

        try {

            if (getInt(burnersField) != trangiaKitchen.getBurners()) {
                return true;
            }

            if (fuelBox.getValue() != trangiaKitchen.getFuelType()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}