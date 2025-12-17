package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.Backpack;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Backpacks.
 * It adds fields for volume and backpack type.
 */
public class BackpackStrategy extends BaseStrategy {

    // Specific input fields for backpacks
    private TextField volumeField;
    private ComboBox<BackpackType> typeBox;

    /**
     * Tells the system that this strategy handles Backpacks.
     * @return The ItemType constant for backpacks.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.BACKPACK;
    }

    /**
     * Creates and displays the input fields for volume and type.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        volumeField = createTextField("Liter");
        typeBox = createComboBox(BackpackType.values());

        // Add them to the grid
        addField("Volym (L)", volumeField);
        addField("Ryggsäckstyp", typeBox);
    }

    /**
     * Fills the fields with data from an existing backpack.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {

        if (item instanceof Backpack backpack) {
            volumeField.setText(String.valueOf(backpack.getVolume()));
            typeBox.setValue(backpack.getBackpackType());
        }
    }

    /**
     * Creates a new Backpack object using data from the form.
     * @return A new Backpack instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {
        return new Backpack(gen.generateBackpackId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(volumeField), typeBox.getValue());
    }

    /**
     * Updates an existing backpack with the values from the input fields.
     * @param item The backpack to update.
     */
    @Override
    public void updateItem(Item item) {

        if (item instanceof Backpack backpack) {
            backpack.setVolume(getInt(volumeField));
            backpack.setBackpackType(typeBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the volume or type.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof Backpack backpack)) {
            return false;
        }

        try {
            if (getInt(volumeField) != backpack.getVolume()) {
                return true;
            }

            if (typeBox.getValue() != backpack.getBackpackType()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}