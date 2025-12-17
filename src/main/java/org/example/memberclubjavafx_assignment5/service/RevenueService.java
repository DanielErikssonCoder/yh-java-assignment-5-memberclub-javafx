package org.example.memberclubjavafx_assignment5.service;

/**
 * This class counts how much revenue the memberclub has generated.
 */
public class RevenueService {

    // Keeps track of the total revenue
    private double totalRevenue;

    /**
     * Constructor. Starts at zero.
     */
    public RevenueService() {
        this.totalRevenue = 0.0;
    }

    /**
     * Adds money to the total.
     */
    public void addRevenue(double amount) {

        // Only add if amount is positive
        if (amount > 0) {
            this.totalRevenue += amount;
        }
    }

    /**
     * Returns the total amount.
     */
    public double getTotalRevenue() {
        return totalRevenue;
    }

    /**
     * Resets the counter to zero.
     */
    public void resetRevenue() {
        this.totalRevenue = 0.0;
    }
}