package org.example.memberclubjavafx_assignment5.model.enums;

/**
 * Represents the status of a rental transaction.
 */
public enum RentalStatus {

    // Rental is currently ongoing
    ACTIVE,

    // Rental has been returned and completed
    COMPLETED,

    // Rental was cancelled before completion
    CANCELLED
}