package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.SleepingBag;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Sleeping Bags.
 * It adds fields for temperature rating and season rating.
 */
public class SleepingBagStrategy extends BaseStrategy {

    // Specific input fields for sleeping bags
    private TextField tempField;
    private ComboBox<SeasonRating> seasonBox;

    /**
     * Tells the system that this strategy handles Sleeping Bags.
     * @return The ItemType constant for sleeping bags.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.SLEEPING_BAG;
    }

    /**
     * Creates and displays the input fields for temperature and season.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        tempField = createTextField("Grader °C");
        seasonBox = createComboBox(SeasonRating.values());

        // Add them to the grid
        addField("Temperaturklass (°C)", tempField);
        addField("Säsong", seasonBox);
    }

    /**
     * Fills the fields with data from an existing sleeping bag.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof SleepingBag sleepingBag) {
            tempField.setText(String.valueOf(sleepingBag.getTemperatureRating()));
            seasonBox.setValue(sleepingBag.getSeasonRating());
        }
    }

    /**
     * Creates a new SleepingBag object using data from the form.
     * @return A new SleepingBag instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new SleepingBag(gen.generateSleepingBagId(), name, pDay, pHour, yr, col, mat, w, brand, getDouble(tempField), seasonBox.getValue());
    }

    /**
     * Updates an existing sleeping bag with the values from the input fields.
     * @param item The sleeping bag to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof SleepingBag sleepingBag) {
            sleepingBag.setTemperatureRating(getDouble(tempField));
            sleepingBag.setSeasonRating(seasonBox.getValue());
        }
    }

    /**
     * Checks if the user has changed the temperature or season.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof SleepingBag bag)) {
            return false;
        }

        try {

            // Use a small tolerance for comparing decimal numbers
            if (Math.abs(getDouble(tempField) - bag.getTemperatureRating()) > 0.001) {
                return true;
            }

            if (seasonBox.getValue() != bag.getSeasonRating()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}