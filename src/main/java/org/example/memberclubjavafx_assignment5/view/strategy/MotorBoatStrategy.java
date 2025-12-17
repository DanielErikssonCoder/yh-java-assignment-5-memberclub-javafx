package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.vehicles.MotorBoat;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Motor Boats.
 * It adds fields for engine power, fuel type, speed, and fish finder.
 */
public class MotorBoatStrategy extends BaseStrategy {

    // Specific input fields for motor boats
    private TextField capacityField;
    private TextField lengthField;
    private TextField speedField;
    private TextField engineField;
    private ComboBox<FuelType> fuelBox;
    private CheckBox fishFinderCheck;

    /**
     * Tells the system that this strategy handles Motor Boats.
     * @return The ItemType constant for motor boats.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.MOTOR_BOAT;
    }

    /**
     * Creates and displays the input fields for motor boat properties.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        capacityField = createTextField("Personer");
        lengthField = createTextField("Meter");
        speedField = createTextField("Knop");
        engineField = createTextField("Hästkrafter");
        fuelBox = createComboBox(FuelType.values());
        fishFinderCheck = createCheckBox("Har Ekolod?");

        // Add them to the grid
        addField("Kapacitet (pers)", capacityField);
        addField("Längd (m)", lengthField);
        addField("Maxfart (knop)", speedField);
        addField("Hästkrafter (HP)", engineField);
        addField("Bränsletyp", fuelBox);
        addCheckBox(fishFinderCheck);
    }

    /**
     * Fills the fields with data from an existing motor boat.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof MotorBoat motorBoat) {
            capacityField.setText(String.valueOf(motorBoat.getCapacity()));
            lengthField.setText(String.valueOf(motorBoat.getLength()));
            speedField.setText(String.valueOf(motorBoat.getMaxSpeed()));
            engineField.setText(String.valueOf(motorBoat.getEnginePower()));
            fuelBox.setValue(motorBoat.getFuelType());
            fishFinderCheck.setSelected(motorBoat.isHasFishFinder());
        }
    }

    /**
     * Creates a new MotorBoat object using data from the form.
     * @return A new MotorBoat instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new MotorBoat(gen.generateMotorBoatId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(capacityField), getDouble(lengthField),
                fishFinderCheck.isSelected(), getDouble(speedField), getInt(engineField), fuelBox.getValue());
    }

    /**
     * Updates an existing motor boat with the values from the input fields.
     * @param item The boat to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof MotorBoat motorBoat) {
            motorBoat.setCapacity(getInt(capacityField));
            motorBoat.setLength(getDouble(lengthField));
            motorBoat.setMaxSpeed(getDouble(speedField));
            motorBoat.setEnginePower(getInt(engineField));
            motorBoat.setFuelType(fuelBox.getValue());
            motorBoat.setHasFishFinder(fishFinderCheck.isSelected());
        }
    }

    /**
     * Checks if the user has changed any properties like engine power or fuel type.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof MotorBoat boat)) {
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

            if (getInt(engineField) != boat.getEnginePower()) {
                return true;
            }

            if (fuelBox.getValue() != boat.getFuelType()) {
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