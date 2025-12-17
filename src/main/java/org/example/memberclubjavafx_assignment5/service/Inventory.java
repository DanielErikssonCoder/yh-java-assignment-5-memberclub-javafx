package org.example.memberclubjavafx_assignment5.service;

import org.example.memberclubjavafx_assignment5.model.Item;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class keeps track of all the items we have.
 * It uses a HashMap so we can find items quickly using their ID.
 */
public class Inventory {

    // We store items here. The ID is the key.
    private final Map<String, Item> items;

    /**
     * Constructor that creates an empty list (map) for items.
     */
    public Inventory() {
        this.items = new HashMap<>();
    }

    /**
     * Adds an item to our list.
     */
    public void addItem(Item item) {
        items.put(item.getId(), item);
    }

    /**
     * Looks for an item using its ID.
     * Returns the item if found, otherwise null.
     */
    public Item getItem(String itemId) {
        return items.get(itemId);
    }

    /**
     * Removes an item from the list.
     */
    public void removeItem(String itemId) {
        items.remove(itemId);
    }

    /**
     * Returns a list of all items we have.
     */
    public List<Item> getAllItems() {

        // Create a new ArrayList from the map values and return it
        return new ArrayList<>(items.values());
    }

    /**
     * Counts how many items we have.
     */
    public int getItemCount() {
        return items.size();
    }
}