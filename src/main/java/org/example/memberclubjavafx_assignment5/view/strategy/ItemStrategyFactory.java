package org.example.memberclubjavafx_assignment5.view.strategy;

import org.example.memberclubjavafx_assignment5.model.enums.ItemType;

/**
 * This factory class decides which strategy to use. It acts like a receptionist that directs you to the right department.
 */
public class ItemStrategyFactory {

    /**
     * This method picks and returns the correct strategy for a specific 'ItemType'.
     * @param type The type of item we are working with.
     * @return A new strategy object that knows how to handle that item.
     * @throws IllegalArgumentException If we ask for a type that doesn't have a strategy.
     */
    public static ItemFormStrategy getStrategy(ItemType type) {

        // We use a standard switch statement to pick the right strategy
        return switch (type) {

            // Camping Equipment
            case TENT -> new TentStrategy();
            case BACKPACK -> new BackpackStrategy();
            case SLEEPING_BAG -> new SleepingBagStrategy();
            case LANTERN -> new LanternStrategy();
            case TRANGIA_KITCHEN -> new TrangiaKitchenStrategy();

            // Fishing Equipment
            case FISHING_ROD -> new FishingRodStrategy();
            case FISHING_NET -> new FishingNetStrategy();
            case FISHING_BAIT -> new FishingBaitStrategy();

            // Water Vehicles
            case KAYAK -> new KayakStrategy();
            case ROW_BOAT -> new RowBoatStrategy();
            case MOTOR_BOAT -> new MotorBoatStrategy();
            case ELECTRIC_BOAT -> new ElectricBoatStrategy();

            // If we don't have a strategy for this type, we crash nicely with an error
            default -> throw new IllegalArgumentException("Ingen strategi hittad för typ: " + type);
        };
    }
}