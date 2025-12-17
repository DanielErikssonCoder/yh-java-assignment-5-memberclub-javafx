package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.fishing.FishingNet;
import org.example.memberclubjavafx_assignment5.model.enums.*;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * Handles the specific form fields for Fishing Nets.
 * It adds fields for net size and mesh size.
 */
public class FishingNetStrategy extends BaseStrategy {

    // Specific input fields for fishing nets
    private ComboBox<NetSize> sizeBox;
    private TextField meshField;

    /**
     * Tells the system that this strategy handles Fishing Nets.
     * @return The ItemType constant for fishing nets.
     */
    @Override
    public ItemType getItemType() {
        return ItemType.FISHING_NET;
    }

    /**
     * Creates and displays the input fields for net size and mesh size.
     * @param container The layout to add fields to.
     */
    @Override
    public void renderFields(Pane container) {
        startGrid(container);

        // Create the inputs
        sizeBox = createComboBox(NetSize.values());
        meshField = createTextField("Millimeter");

        // Add them to the grid
        addField("Storlek", sizeBox);
        addField("Nätstorlek (mm)", meshField);
    }

    /**
     * Fills the fields with data from an existing fishing net.
     * @param item The item to load data from.
     */
    @Override
    public void populateFields(Item item) {
        if (item instanceof FishingNet fishingNet) {
            sizeBox.setValue(fishingNet.getNetSize());
            meshField.setText(String.valueOf(fishingNet.getMeshSize()));
        }
    }

    /**
     * Creates a new FishingNet object using data from the form.
     * @return A new FishingNet instance.
     */
    @Override
    public Item createItem(ItemIdGenerator gen, String name, double pDay, double pHour, int yr, Color col, Material mat, double w, String brand) {

        return new FishingNet(gen.generateNetId(), name, pDay, pHour, yr, col, mat, w, brand, sizeBox.getValue(), getDouble(meshField));
    }

    /**
     * Updates existing fishing net with the values from the input fields.
     * @param item The net to update.
     */
    @Override
    public void updateItem(Item item) {
        if (item instanceof FishingNet fishingNet) {
            fishingNet.setNetSize(sizeBox.getValue());
            fishingNet.setMeshSize(getDouble(meshField));
        }
    }

    /**
     * Checks if the user has changed the size or mesh size.
     * @return true if changes are detected.
     */
    @Override
    public boolean isModified(Item item) {

        if (!(item instanceof FishingNet net)) {
            return false;
        }

        try {
            if (sizeBox.getValue() != net.getNetSize()) {
                return true;
            }

            // Use a small tolerance for comparing decimal numbers
            if (Math.abs(getDouble(meshField) - net.getMeshSize()) > 0.001) {
                return true;
            }

        } catch (Exception exception) {
            // Invalid input counts as modified
            return true;
        }
        return false;
    }
}