package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.vehicles.ElectricBoat;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Electric Boats.
 * It adds fields for battery capacity, speed, and charging time.
 */
public class ElectricBoatStrategy extends BaseStrategy {

    // Specific input fields for electric boats
    private TextField capacityField;
    private TextField lengthField;
    private TextField speedField;
    private TextField batteryField;
    private TextField chargeField;
    private CheckBox fishFinderCheck;

    /**
     * Tells the system that this strategy handles Electric Boats.
     * @return The ItemType constant for electric boats.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.ELECTRIC_BOAT;
    }

    /**
     * Creates and displays the input fields for electric boat properties.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        capacityField = createTextField("Personer");
        lengthField = createTextField("Meter");
        speedField = createTextField("Knop");
        batteryField = createTextField("kWh");
        chargeField = createTextField("Timmar");
        fishFinderCheck = createCheckBox("Har Ekolod?");

        // Add them to the grid
        addField("Kapacitet (pers)", capacityField);
        addField("Längd (m)", lengthField);
        addField("Maxfart (knop)", speedField);
        addField("Batteri (kWh)", batteryField);
        addField("Laddningstid (h)", chargeField);
        addCheckBox(fishFinderCheck);
    }

    /**
     * Fills the fields with data from an existing electric boat.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof ElectricBoat electricBoat) {
            capacityField.setText(String.valueOf(electricBoat.getCapacity()));
            lengthField.setText(String.valueOf(electricBoat.getLength()));
            speedField.setText(String.valueOf(electricBoat.getMaxSpeed()));
            batteryField.setText(String.valueOf(electricBoat.getBatteryCapacity()));
            chargeField.setText(String.valueOf(electricBoat.getChargeTime()));
            fishFinderCheck.setSelected(electricBoat.isHasFishFinder());
        }
    }

    /**
     * Creates a new ElectricBoat object using data from the form.
     * @return A new ElectricBoat instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {
        return new ElectricBoat(gen.generateElectricBoatId(), name, pDay, pHour, yr, col, mat, w, brand,
                getInt(capacityField), getDouble(lengthField), fishFinderCheck.isSelected(),
                getDouble(speedField), getDouble(batteryField), getInt(chargeField));
    }

    /**
     * Updates an existing electric boat with the values from the input fields.
     * @param item The boat to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof ElectricBoat electricBoat) {
            electricBoat.setCapacity(getInt(capacityField));
            electricBoat.setLength(getDouble(lengthField));
            electricBoat.setMaxSpeed(getDouble(speedField));
            electricBoat.setBatteryCapacity(getDouble(batteryField));
            electricBoat.setChargeTime(getInt(chargeField));
            electricBoat.setHasFishFinder(fishFinderCheck.isSelected());
        }
    }

    /**
     * Checks if the user has changed any properties like speed or battery.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof ElectricBoat boat)) {
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

            if (Math.abs(getDouble(speedField) - boat.getMaxSpeed()) > 0.001) {
                return true;
            }

            if (Math.abs(getDouble(batteryField) - boat.getBatteryCapacity()) > 0.001) {
                return true;
            }

            if (getInt(chargeField) != boat.getChargeTime()) {
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