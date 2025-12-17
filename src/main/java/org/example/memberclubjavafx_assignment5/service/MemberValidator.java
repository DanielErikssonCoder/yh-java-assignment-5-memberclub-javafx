package org.example.memberclubjavafx_assignment5.service;

import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;

/**
 * This is a helper class that checks if the member information is correct.
 * We use this before creating or updating a member to avoid saving bad data.
 */
public class MemberValidator {

    /**
     * Checks all the fields for a member.
     * If something is wrong, it stops the program and throws an error.
     * @param firstName The first name to check.
     * @param lastName The last name to check.
     * @param email The email address (must also contain a '@').
     * @param phone The phone number.
     * @param level The membership level.
     * @throws IllegalArgumentException If any of the checks fail.
     */
    public static void validate(String firstName, String lastName, String email, String phone, MembershipLevel level) {

        // Check if first name is missing or just empty spaces
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Förnamn får inte vara tomt.");
        }

        // Check if last name is missing
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Efternamn får inte vara tomt.");
        }

        // Check if email is missing or doesn't look like a valid email
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Ogiltig e-postadress.");
        }

        // Check if phone number is missing
        if (phone == null || phone.trim().isEmpty()) {
            throw new IllegalArgumentException("Telefonnummer saknas.");
        }

        // Check if a membership level is selected
        if (level == null) {
            throw new IllegalArgumentException("Välj en medlemsnivå.");
        }
    }
}