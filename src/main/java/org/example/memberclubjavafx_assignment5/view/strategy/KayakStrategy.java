package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.vehicles.Kayak;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Kayaks.
 * It adds fields for capacity, length, number of seats, and kayak type.
 */
public class KayakStrategy extends BaseStrategy {

    // Specific input fields for kayaks
    private TextField capacityField;
    private TextField lengthField;
    private TextField seatsField;
    private ComboBox<KayakType> typeBox;

    /**
     * Tells the system that this strategy handles Kayaks.
     * @return The ItemType constant for kayaks.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.KAYAK;
    }

    /**
     * Creates and displays the input fields for kayak properties.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {

        startGrid(container);

        // Create the inputs
        capacityField = createTextField("Personer");
        lengthField = createTextField("Meter");
        seatsField = createTextField("Antal");
        typeBox = createComboBox(KayakType.values());

        // Add them to the grid
        addField("Kapacitet (pers)", capacityField);
        addField("Längd (m)", lengthField);
        addField("Antal säten", seatsField);
        addField("Kajaktyp", typeBox);
    }

    /**
     * Fills the fields with data from an existing kayak.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof Kayak kayak) {
            capacityField.setText(String.valueOf(kayak.getCapacity()));
            lengthField.setText(String.valueOf(kayak.getLength()));
            seatsField.setText(String.valueOf(kayak.getSeats()));
            typeBox.setValue(kayak.getKayakType());
        }
    }

    /**
     * Creates a new Kayak object using data from the form.
     * @return A new Kayak instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new Kayak(gen.generateKayakId(), name, pDay, pHour, yr, col, mat, w, brand, getInt(capacityField), getDouble(lengthField), getInt(seatsField), typeBox.getValue());
    }

    /**
     * Updates an existing kayak with the values from the input fields.
     * @param item The kayak to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof Kayak kayak) {
            kayak.setCapacity(getInt(capacityField));
            kayak.setLength(getDouble(lengthField));
            kayak.setSeats(getInt(seatsField));
            kayak.setKayakType(typeBox.getValue());
        }
    }

    /**
     * Checks if the user has changed any properties like length or seats.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof Kayak kayak)) {
            return false;
        }

        try {

            if (getInt(capacityField) != kayak.getCapacity()) {
                return true;
            }

            // Use a small tolerance for comparing decimal numbers
            if (Math.abs(getDouble(lengthField) - kayak.getLength()) > 0.001) {
                return true;
            }

            if (getInt(seatsField) != kayak.getSeats()) {
                return true;
            }

            if (typeBox.getValue() != kayak.getKayakType()) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}