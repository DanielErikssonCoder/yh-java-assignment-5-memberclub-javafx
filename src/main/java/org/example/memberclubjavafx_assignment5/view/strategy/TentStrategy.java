package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.Tent;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Tents.
 * It adds fields for capacity, season rating, and tent type.
 */
public class TentStrategy extends BaseStrategy {

    // Specific input fields for tents
    private TextField capacityField;
    private ComboBox<SeasonRating> seasonBox;
    private ComboBox<TentType> typeBox;

    /**
     * Tells the system that this strategy handles Tents.
     * @return The ItemType constant for tents.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.TENT;
    }

    /**
     * Creates and displays the input fields for capacity, season, and type.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        capacityField = createTextField("Antal personer");
        seasonBox = createComboBox(SeasonRating.values());
        typeBox = createComboBox(TentType.values());

        // Add them to the grid
        addField("Kapacitet (pers)", capacityField);
        addField("Säsong", seasonBox);
        addField("Tälttyp", typeBox);
    }

    /**
     * Fills the fields with data from an existing tent.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof Tent tent) {
            capacityField.setText(String.valueOf(tent.getCapacity()));
            seasonBox.setValue(tent.getSeasonRating());
            typeBox.setValue(tent.getTentType());
        }
    }

    /**
     * Creates a new Tent object using data from the form.
     * @return A new Tent instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new Tent(gen.generateTentId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(capacityField), seasonBox.getValue(), typeBox.getValue());
    }

    /**
     * Updates an existing tent with the values from the input fields.
     * @param item The tent to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof Tent tent) {
            tent.setCapacity(getInt(capacityField));
            tent.setSeasonRating(seasonBox.getValue());
            tent.setTentType(typeBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the capacity, season, or type.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof Tent tent)) {
            return false;
        }

        try {

            if (getInt(capacityField) != tent.getCapacity()) {
                return true;
            }

            if (seasonBox.getValue() != tent.getSeasonRating()) {
                return true;
            }

            if (typeBox.getValue() != tent.getTentType()) {
                return true;
            }

        } catch (NumberFormatException exception) {
            // If field is invalid/empty, it counts as modified
            return true;
        }
        return false;
    }
}