package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.fishing.FishingRod;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Fishing Rods.
 * It adds fields for rod length and rod type.
 */
public class FishingRodStrategy extends BaseStrategy {

    // Specific input fields for fishing rods
    private TextField lengthField;
    private ComboBox<RodType> typeBox;

    /**
     * Tells the system that this strategy handles Fishing Rods.
     * @return The ItemType constant for fishing rods.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.FISHING_ROD;
    }

    /**
     * Creates and displays the input fields for length and rod type.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        lengthField = createTextField("Meter");
        typeBox = createComboBox(RodType.values());

        // Add them to the grid
        addField("Längd (m)", lengthField);
        addField("Typ av fiskespö", typeBox);
    }

    /**
     * Fills the fields with data from an existing fishing rod.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof FishingRod fishingRod) {
            lengthField.setText(String.valueOf(fishingRod.getRodLength()));
            typeBox.setValue(fishingRod.getRodType());
        }
    }

    /**
     * Creates a new FishingRod object using data from the form.
     * @return A new FishingRod instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new FishingRod(gen.generateRodId(), name, pDay, pHour, yr, col, mat, w, brand, getDouble(lengthField), typeBox.getValue());
    }

    /**
     * Updates an existing fishing rod with the values from the input fields.
     * @param item The rod to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof FishingRod fishingRod) {
            fishingRod.setRodLength(getDouble(lengthField));
            fishingRod.setRodType(typeBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the length or type.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof FishingRod rod)) {
            return false;
        }

        try {

            // Use a small tolerance for comparing decimal numbers
            if (Math.abs(getDouble(lengthField) - rod.getRodLength()) > 0.001) {
                return true;
            }

            if (typeBox.getValue() != rod.getRodType()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}