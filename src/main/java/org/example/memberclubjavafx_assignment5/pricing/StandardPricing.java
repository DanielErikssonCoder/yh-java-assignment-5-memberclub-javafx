package org.example.memberclubjavafx_assignment5.pricing;

import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.enums.RentalPeriod;

/**
 * Standard pricing strategy with no discount.
 * Applies 1.0x multiplier.
 */
public class StandardPricing implements PricePolicy {

    // Implements the pricing calculation from PricePolicy interface
    @Override
    public double calculatePrice(Item item, Member member, int duration, RentalPeriod period) {

        // Calculate cost based on a rental period (hourly or daily)
        if (period == RentalPeriod.HOURLY) {
            return item.getPricePerHour() * duration;
        } else {
            return item.getPricePerDay() * duration;
        }
    }
}
