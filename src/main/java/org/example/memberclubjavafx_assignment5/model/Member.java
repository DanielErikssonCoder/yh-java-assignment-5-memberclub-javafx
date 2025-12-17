package org.example.memberclubjavafx_assignment5.model;


import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a member in the rental club.
 * Each member has an id, name, email, phone, membership level and rental history.
 */
public class Member {

    private final int id;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;
    private MembershipLevel membershipLevel;
    private final List<String> rentalHistory;

    /**
     * Constructor that creates a new member with the given information.
     * @param id unique member id
     * @param firstName member's first name
     * @param lastName member's last name
     * @param phone member's phone number
     * @param email member's email adress
     * @param membershipLevel membership level (STANDARD, STUDENT, or PREMIUM)
     */
    public Member(int id, String firstName, String lastName, String phone, String email, MembershipLevel membershipLevel) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.email = email;
        this.membershipLevel = membershipLevel;
        this.rentalHistory = new ArrayList<>();
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public MembershipLevel getMembershipLevel() {
        return membershipLevel;
    }

    public List<String> getRentalHistory() {
        return rentalHistory;
    }

    // Setters
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMembershipLevel(MembershipLevel membershipLevel) {
        this.membershipLevel = membershipLevel;
    }

    /**
     * Adds a rental to the member's history.
     * @param rentalId is the id of the rental to add
     */
    public void addRental(String rentalId) {
        rentalHistory.add(rentalId);
    }

    // Returns string representation of Member for easy printing or debugging
    @Override
    public String toString() {
        return "Medlem{" + "id=" + id + ", förnamn=" + firstName + ", efternamn=" + lastName + ", nivå=" + membershipLevel + ", historik=" + rentalHistory.size() + "}";
    }
}