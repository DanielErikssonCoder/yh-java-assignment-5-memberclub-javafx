package org.example.memberclubjavafx_assignment5.view.strategy;

import javafx.scene.layout.Pane;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.enums.Color;
import org.example.memberclubjavafx_assignment5.model.enums.ItemType;
import org.example.memberclubjavafx_assignment5.model.enums.Material;
import org.example.memberclubjavafx_assignment5.system.ItemIdGenerator;

/**
 * This interface works like rulebook for our item forms.
 * Every specific item type has its own strategy that must follow these rules.
 * This ensures that the system can treat all forms in the same way.
 */
public interface ItemFormStrategy {

    /**
     * This method tells the system which type of item this strategy handles.
     * @return The {@code ItemType}
     */
    ItemType getItemType();

    /**
     * This method draws the specific input fields for this item type on the screen.
     * @param container The layout pane where we should add the fields.
     */
    void renderFields(Pane container);

    /**
     * This method takes an existing item and fills the input fields with its data.
     * We use this when the user wants to edit an item, so they don't have to retype everything.
     * @param item The item object containing the data we want to show.
     */
    void populateFields(Item item);

    /**
     * This method creates a new Item object.
     * It combines the common information with the specific information.
     * @param generator The helper that creates a unique ID for the new item.
     * @param name      The name of the item.
     * @param priceDay  The cost to rent this item for a day.
     * @param priceHour The cost to rent this item for an hour.
     * @param year      The year the item was made.
     * @param color     The color of the item.
     * @param material  The material the item is made of.
     * @param weight    The weight of the item.
     * @param brand     The brand or manufacturer.
     * @return A new {@code Item} object ready to be used in the system.
     */
    Item createItem(ItemIdGenerator generator, String name, double priceDay, double priceHour, int year, Color color, Material material, double weight, String brand);

    /**
     * This method updates an existing item with new values from input fields.
     * It reads what the user typed and saves it in the item object.
     * @param item The item object that needs to be updated.
     */
    void updateItem(Item item);

    /**
     * This checks if the user has changed any of the specific fields in the form compared to the original item.
     * This is useful we need to enable the 'Save' button or warn the user about unsaved changes.
     * @param item The original item object to compare against.
     * @return {@code true} if the form data is different from the item data, otherwise {@code false}.
     */
    boolean isModified(Item item);
}