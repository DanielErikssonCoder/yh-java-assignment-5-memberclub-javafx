package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.vehicles.RowBoat;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Row Boats.
 * It adds fields for capacity, length, number of oars, and fish finder.
 */
public class RowBoatStrategy extends BaseStrategy {

    // Specific input fields for row boats
    private TextField capacityField;
    private TextField lengthField;
    private TextField oarsField;
    private CheckBox fishFinderCheck;

    /**
     * Tells the system that this strategy handles Row Boats.
     * @return The ItemType constant for row boats.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.ROW_BOAT;
    }

    /**
     * Creates and displays the input fields for row boat properties.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        capacityField = createTextField("Personer");
        lengthField = createTextField("Meter");
        oarsField = createTextField("Antal");
        fishFinderCheck = createCheckBox("Har Ekolod?");

        // Add them to the grid
        addField("Kapacitet (pers)", capacityField);
        addField("Längd (m)", lengthField);
        addField("Antal åror", oarsField);
        addCheckBox(fishFinderCheck);
    }

    /**
     * Fills the fields with data from an existing row boat.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof RowBoat rowBoat) {
            capacityField.setText(String.valueOf(rowBoat.getCapacity()));
            lengthField.setText(String.valueOf(rowBoat.getLength()));
            oarsField.setText(String.valueOf(rowBoat.getOars()));
            fishFinderCheck.setSelected(rowBoat.isHasFishFinder());
        }
    }

    /**
     * Creates a new RowBoat object using data from the form.
     * @return A new RowBoat instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        // We set a default speed for row boats
        double speed = 3.0;

        return new RowBoat(gen.generateRowBoatId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(capacityField), getDouble(lengthField), fishFinderCheck.isSelected(), speed, getInt(oarsField));
    }

    /**
     * Updates an existing row boat with the values from the input fields.
     * @param item The boat to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof RowBoat rowBoat) {
            rowBoat.setCapacity(getInt(capacityField));
            rowBoat.setLength(getDouble(lengthField));
            rowBoat.setOars(getInt(oarsField));
            rowBoat.setHasFishFinder(fishFinderCheck.isSelected());
        }
    }

    /**
     * Checks if the user has changed any properties like oars or length.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof RowBoat boat)) {
            return false;
        }

        try {

            if (getInt(capacityField) != boat.getCapacity()) {
                return true;
            }

            // Use a small tolerance for comparing decimal numbers
            if (Math.abs(getDouble(lengthField) - boat.getLength()) > 0.001) {
                return true;
            }

            if (getInt(oarsField) != boat.getOars()) {
                return true;
            }

            if (fishFinderCheck.isSelected() != boat.isHasFishFinder()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}