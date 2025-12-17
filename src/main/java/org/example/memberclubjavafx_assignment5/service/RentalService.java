package org.example.memberclubjavafx_assignment5.service;

import org.example.memberclubjavafx_assignment5.exceptions.ItemNotAvailableException;
import org.example.memberclubjavafx_assignment5.exceptions.ItemNotFoundException;
import org.example.memberclubjavafx_assignment5.exceptions.MemberNotFoundException;
import org.example.memberclubjavafx_assignment5.exceptions.RentalNotFoundException;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.Rental;
import org.example.memberclubjavafx_assignment5.model.enums.ItemStatus;
import org.example.memberclubjavafx_assignment5.model.enums.RentalPeriod;
import org.example.memberclubjavafx_assignment5.pricing.PricePolicy;
import org.example.memberclubjavafx_assignment5.pricing.PricingFactory;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class handles all the logic for renting items.
 * It connects the Items (Inventory), the Members (Registry), and the Prices.
 * This is where the actual work happens when the user clicks the Rent button.
 */
public class RentalService {

    // We need access to the inventory to check if items exist
    private final Inventory inventory;

    // We need access to members to know who is renting
    private final MemberRegistry memberRegistry;

    // This list stores every rental history
    private final List<Rental> rentals;

    // A simple counter to create unique IDs
    private int rentalCounter;

    /**
     * Constructor.
     * Starts the counter at 1 and prepares the empty list.
     */
    public RentalService(Inventory inventory, MemberRegistry memberRegistry) {
        this.inventory = inventory;
        this.memberRegistry = memberRegistry;
        this.rentals = new ArrayList<>();
        this.rentalCounter = 1;
    }

    /**
     * This is the main method that creates a new rental.
     * It checks everything, like if the items is free, calculates the price, and saves the receipt.
     * @param memberId Who is renting?
     * @param itemId What are they renting?
     * @param duration How long?
     * @param period Is it hours or days?
     * @return The created Rental receipt.
     * @throws MemberNotFoundException if the member does not exist.
     * @throws ItemNotFoundException if the item does not exist.
     * @throws ItemNotAvailableException if the item is not available for rental.
     */
    public Rental rentItem(int memberId, String itemId, int duration, RentalPeriod period) {

        // Validate that the member exists
        Member member = memberRegistry.getMember(memberId);
        if (member == null) {
            throw new MemberNotFoundException("Ingen medlem hittades med ID: " + memberId);
        }

        // Validate that the item exists
        Item item = inventory.getItem(itemId);
        if (item == null) {
            throw new ItemNotFoundException("Ingen artikel hittades med ID: " + itemId);
        }

        // Validate that the item is available
        if (item.getStatus() != ItemStatus.AVAILABLE) {
            throw new ItemNotAvailableException("Artikeln '" + item.getName() + "' är inte tillgänglig (Status: " + item.getStatus() + ")");
        }

        // Calculate the price (we use a 'Factory' here to get the right price rules for this member)
        PricePolicy pricing = PricingFactory.getPricing(member.getMembershipLevel());
        double totalCost = pricing.calculatePrice(item, member, duration, period);

        // Generate an ID (we add zeros to the front so the numbers aligns better in lists)
        String idString;

        if (this.rentalCounter < 10) {
            idString = "00" + this.rentalCounter;

        } else if (this.rentalCounter < 100) {
            idString = "0" + this.rentalCounter;

        } else {
            idString = "" + this.rentalCounter;
        }

        String rentalId = "RENT-" + idString;

        // Increase the counter so the next rental gets a new number
        this.rentalCounter = this.rentalCounter + 1;

        // Calculate the dates
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expectedReturnDate;

        // Add hours or days depending on what the user chose
        if (period.equals(RentalPeriod.HOURLY)) {
            expectedReturnDate = now.plusHours(duration);
        } else {
            expectedReturnDate = now.plusDays(duration);
        }

        // Create the actual Rental object (the receipt)
        Rental rental = new Rental(rentalId, memberId, itemId, now, expectedReturnDate, null, totalCost);

        // Update the system status and mark the item as 'rented' so no one else can rent it
        item.setStatus(ItemStatus.RENTED);

        // Add the rental ID to the member's history
        member.addRental(rentalId);

        // Save the rental in our main list
        this.rentals.add(rental);

        return rental;
    }

    /**
     * Handles the return of an item.
     * Finds the rental, marks it as done, and makes the item available again.
     * @param rentalId The ID of the rental to return.
     * @throws RentalNotFoundException if the rental ID is not found.
     */
    public void returnItem(String rentalId) {

        // Find the rental
        Rental rental = getRental(rentalId);

        if (rental == null) {
            throw new RentalNotFoundException("Kunde inte hitta uthyrning med ID: " + rentalId);
        }

        // Mark the rental receipt as completed (sets the actual return date)
        rental.complete();

        // Find the item and make it 'available' again so others can rent it
        Item item = inventory.getItem(rental.getItemId());

        if (item != null) {
            item.setStatus(ItemStatus.AVAILABLE);
        }
    }

    /**
     * Gets a list of rentals that are currently active (not returned yet).
     */
    public List<Rental> getActiveRentals() {

        List<Rental> activeRentals = new ArrayList<>();

        for (Rental rental : rentals) {

            // We check the 'isActive()' method on the rental object
            if (rental.isActive()) {
                activeRentals.add(rental);
            }
        }
        return activeRentals;
    }

    /**
     * Returns the full history of all rentals.
     */
    public List<Rental> getAllRentals() {
        return rentals;
    }

    /**
     * Finds a specific rental by its ID string.
     */
    public Rental getRental(String rentalId) {

        for (Rental rental : rentals) {

            if (rental.getRentalId().equals(rentalId)) {
                return rental;
            }
        }
        return null;
    }

    /**
     * Loads a list of rentals into the system.
     * This method clears old data and prevents duplicates.
     * It also updates the counter to ensure new rentals get unique IDs.
     */
    public void setRentals(List<Rental> loadedRentals) {

        // Wipe the current memory clean to prevent stacking old data and new data
        this.rentals.clear();

        // Reset counter (will be updated in the loop below)
        this.rentalCounter = 1;

        if (loadedRentals != null) {

            // Use a Set to strictly enforce unique IDs.
            Set<String> processedIds = new HashSet<>();

            for (Rental rental : loadedRentals) {

                // Only add if we haven't seen this ID in this batch
                if (!processedIds.contains(rental.getRentalId())) {
                    this.rentals.add(rental);
                    processedIds.add(rental.getRentalId());

                    // Check the ID of the loaded rental to update our counter
                    try {

                        String idStr = rental.getRentalId();

                        if (idStr.startsWith("RENT-")) {

                            // Extract the number part
                            int idNum = Integer.parseInt(idStr.substring(5));

                            // If this ID is higher than or equal to our current counter, bump the counter up
                            if (idNum >= this.rentalCounter) {
                                this.rentalCounter = idNum + 1;
                            }
                        }
                    } catch (NumberFormatException exception) {
                        // Ignore bad IDs
                    }
                }
            }
        }
    }

    /**
     * Adds a single existing rental to the system (mostly used when loading data from a file or updating the counter).
     */
    public void addRental(Rental rental) {

        this.rentals.add(rental);

        // If the rental we just loaded is still active, we must make sure the Item is marked as 'rented'
        if (rental.isActive()) {

            Item item = inventory.getItem(rental.getItemId());

            if (item != null) {
                item.setStatus(ItemStatus.RENTED);
            }
        }

        // We need to update our internal counter so the program doesn't crash
        String currentId = rental.getRentalId();

        try {
            if (currentId.startsWith("RENT-")) {

                // Remove prefix to get just the number
                String idPart = currentId.substring(5);
                int idNum = Integer.parseInt(idPart);

                // If this ID is larger than our counter, update our counter
                if (idNum >= this.rentalCounter) {
                    this.rentalCounter = idNum + 1;
                }
            }

        } catch (NumberFormatException exception) {
            System.err.println("VARNING: Kunde inte läsa Uthyrnings-ID: " + currentId);
        }
    }
}