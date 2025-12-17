package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.fishing.FishingBait;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Fishing Bait.
 * It adds fields for bait type and quantity.
 */
public class FishingBaitStrategy extends BaseStrategy {

    // Specific input fields for fishing bait
    private ComboBox<BaitType> typeBox;
    private TextField quantityField;

    /**
     * Tells the system that this strategy handles Fishing Bait.
     * @return The ItemType constant for fishing bait.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.FISHING_BAIT;
    }

    /**
     * Creates and displays the input fields for bait type and quantity.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        typeBox = createComboBox(BaitType.values());
        quantityField = createTextField("Antal artiklar");

        // Add them to the grid
        addField("Betestyp", typeBox);
        addField("Kvantitet", quantityField);
    }

    /**
     * Fills the fields with data from existing fishing bait.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {

        if (item instanceof FishingBait fishingBait) {
            typeBox.setValue(fishingBait.getBaitType());
            quantityField.setText(String.valueOf(fishingBait.getQuantity()));
        }
    }

    /**
     * Creates a new FishingBait object using data from the form.
     * @return A new FishingBait instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new FishingBait(gen.generateBaitId(), name, pDay, pHour, yr, col, mat, w, brand, typeBox.getValue(), getInt(quantityField));
    }

    /**
     * Updates existing fishing bait with the values from the input fields.
     * @param item The bait to update.
     */
    @Override
    public void updateItem(Item item) {

        if (item instanceof FishingBait fishingBait) {

            fishingBait.setBaitType(typeBox.getValue());
            fishingBait.setQuantity(getInt(quantityField));
        }
    }

    /**
     * Checks if the user has changed the type or quantity.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof FishingBait bait)) {
            return false;
        }

        try {

            if (typeBox.getValue() != bait.getBaitType()) {
                return true;
            }

            if (getInt(quantityField) != bait.getQuantity()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}