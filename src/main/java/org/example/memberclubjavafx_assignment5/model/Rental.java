package org.example.memberclubjavafx_assignment5.model;

import org.example.memberclubjavafx_assignment5.model.enums.RentalStatus;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class Rental {

    private final String rentalId;
    private final int memberId;
    private final String itemId;
    private final LocalDateTime startDate;
    private final LocalDateTime expectedReturnDate;
    private LocalDateTime endDate;
    private final double totalCost;
    private RentalStatus status;

    public Rental(String rentalId, int memberId, String itemId, LocalDateTime startDate, LocalDateTime expectedReturnDate, LocalDateTime endDate, double totalCost) {
        this.rentalId = rentalId;
        this.memberId = memberId;
        this.itemId = itemId;
        this.startDate = startDate;
        this.expectedReturnDate = expectedReturnDate;
        this.endDate = endDate;
        this.totalCost = totalCost;
        this.status = RentalStatus.ACTIVE;
    }

    // ... Getters ... (som tidigare)
    public String getRentalId() { return rentalId; }
    public int getMemberId() { return memberId; }
    public String getItemId() { return itemId; }
    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }
    public LocalDateTime getExpectedReturnDate() { return expectedReturnDate; }
    public double getTotalCost() { return totalCost; }
    public RentalStatus getStatus() { return status; }
    public void setStatus(RentalStatus status) { this.status = status; }

    public long getDurationInDays() {
        if (endDate == null) return 0;
        return ChronoUnit.DAYS.between(startDate, endDate);
    }

    public boolean isActive() {
        return status == RentalStatus.ACTIVE;
    }

    public void complete() {
        this.endDate = LocalDateTime.now();
        this.status = RentalStatus.COMPLETED;
    }

    public void cancel() {
        this.status = RentalStatus.CANCELLED;
    }

    // --- NEW DOMAIN LOGIC ---

    /**
     * Checks if the rental is currently overdue.
     */
    public boolean isLate() {
        if (status != RentalStatus.ACTIVE || expectedReturnDate == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(expectedReturnDate);
    }

    /**
     * Calculates hours late. Returns 0 if not late.
     */
    public long getHoursLate() {
        if (!isLate()) return 0;
        long hours = ChronoUnit.HOURS.between(expectedReturnDate, LocalDateTime.now());
        // Return at least 1 hour if it is late but less than an hour
        return (hours <= 0) ? 1 : hours;
    }

    /**
     * Calculates the penalty fee based on the item's hourly price.
     */
    public double calculatePenaltyFee(Item item) {
        if (!isLate() || item == null) return 0.0;
        return getHoursLate() * item.getPricePerHour();
    }

    @Override
    public String toString() {
        return "Rental{" + "id=" + rentalId + ", status=" + status + "}";
    }
}