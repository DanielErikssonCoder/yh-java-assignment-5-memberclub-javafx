package org.example.memberclubjavafx_assignment5.pricing;

import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.enums.RentalPeriod;

/**
 * Interface defining the contract for pricing strategies.
 * Implementations calculate rental cost based on item, member, duration, and period.
 */
public interface PricePolicy {

    /**
     * Calculates the total rental cost.
     * @param item the item being rented
     * @param member the member renting
     * @param duration rental duration (hours or days depending on period)
     * @param period billing period (HOURLY or DAILY)
     * @return total cost in SEK
     */
    double calculatePrice(Item item, Member member, int duration, RentalPeriod period);
}
