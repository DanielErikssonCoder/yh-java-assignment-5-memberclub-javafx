package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.Lantern;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Lanterns.
 * It adds fields for brightness and power source.
 */
public class LanternStrategy extends BaseStrategy {

    // Specific input fields for the lantern
    private TextField lumensField;
    private ComboBox<PowerSource> powerBox;

    /**
     * Tells the system that this strategy handles Lanterns.
     * @return The ItemType constant for lanterns.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.LANTERN;
    }

    /**
     * Creates and displays the input fields for brightness and power source.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        lumensField = createTextField("Ljusstyrka");
        powerBox = createComboBox(PowerSource.values());

        // Add them to the grid
        addField("Ljusstyrka (Lumen)", lumensField);
        addField("Energikälla", powerBox);
    }

    /**
     * Fills the fields with data from an existing lantern.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {

        if (item instanceof Lantern lantern) {
            lumensField.setText(String.valueOf(lantern.getBrightness()));
            powerBox.setValue(lantern.getPowerSource());
        }
    }

    /**
     * Creates a new Lantern object using data from the form.
     * @return A new Lantern instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new Lantern(gen.generateLanternId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(lumensField), powerBox.getValue());
    }

    /**
     * Updates an existing lantern with the values from the input fields.
     * @param item The lantern to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof Lantern lantern) {
            lantern.setBrightness(getInt(lumensField));
            lantern.setPowerSource(powerBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the brightness or power source.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof Lantern lantern)) {
            return false;
        }

        try {

            if (getInt(lumensField) != lantern.getBrightness()) {
                return true;
            }

            if (powerBox.getValue() != lantern.getPowerSource()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}