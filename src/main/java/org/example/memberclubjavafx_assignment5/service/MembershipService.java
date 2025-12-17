package org.example.memberclubjavafx_assignment5.service;

import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;
import org.example.memberclubjavafx_assignment5.system.MemberIdGenerator;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles everything related to members.
 * It acts as a middleman between the rest of the app and the list of members (Registry).
 * Here we can add new members, update their info, or search for them.
 */
public class MembershipService {

    // This is where the actual list of members is kept
    private final MemberRegistry memberRegistry;

    // This helper gives us a unique number (ID) for every new member
    private final MemberIdGenerator memberIdGenerator;

    /**
     * Constructor. We need the registry and the ID generator.
     */
    public MembershipService(MemberRegistry memberRegistry, MemberIdGenerator memberIdGenerator) {
        this.memberRegistry = memberRegistry;
        this.memberIdGenerator = memberIdGenerator;
    }

    /**
     * Adds an existing Member object to the system directly.
     */
    public void addMember(Member member) {
        memberRegistry.addMember(member);
    }

    /**
     * Creates a new member.
     * This method validates the input, generates a new ID, and then saves the member.
     * @param firstName The member's first name
     * @param lastName The member's last name
     * @param phone The phone number
     * @param email The email address
     * @param level The membership level
     */
    public void addMember(String firstName, String lastName, String phone, String email, MembershipLevel level) {

        // Check if the input is valid, if it's not OK, this 'validate' method will stop the code and throw an error.
        MemberValidator.validate(firstName, lastName, email, phone, level);

        // Get a new unique ID number
        int memberId = memberIdGenerator.generateMemberId();

        // Create the Member object
        Member newMember = new Member(memberId, firstName, lastName, phone, email, level);

        // Save it to our list
        memberRegistry.addMember(newMember);
    }

    /**
     * Removes a member from the memberclub.
     * @param memberId The ID of the member to remove.
     * @return true if the member was found and removed, otherwise false.
     */
    public boolean removeMember(int memberId) {
        return memberRegistry.removeMember(memberId);
    }

    /**
     * Finds a specific member by their ID.
     */
    public Member getMember(int memberId) {
        return memberRegistry.getMember(memberId);
    }

    /**
     * Changes the membership level.
     */
    public boolean updateMemberLevel(int memberId, MembershipLevel newLevel) {
        Member member = memberRegistry.getMember(memberId);

        // If the member doesn't exist, we can't update them
        if (member == null) {
            return false;
        }

        member.setMembershipLevel(newLevel);

        return true;
    }

    /**
     * Updates all the details of a member.
     * We use the same validation rules here as when we create a new member.
     */
    public void updateMemberDetails(Member member, String fName, String lName, String phone, String email, MembershipLevel level) {

        // Check if the new info is valid before saving
        MemberValidator.validate(fName, lName, email, phone, level);

        // Apply the changes
        member.setFirstName(fName);
        member.setLastName(lName);
        member.setPhone(phone);
        member.setEmail(email);
        member.setMembershipLevel(level);
    }

    /**
     * Returns a list of every member in the club.
     */
    public List<Member> getAllMembers() {
        return memberRegistry.getAllMembers();
    }

    /**
     * Searches for members whose first name contains the search text (useful for the search bar).
     * @param searchTerm The text to search for.
     * @return A list of all matching members.
     */
    public List<Member> searchMemberByName(String searchTerm) {
        List<Member> allMembers = memberRegistry.getAllMembers();
        List<Member> results = new ArrayList<>();

        // Loop through everyone and check their name
        for (Member member : allMembers) {

            // Convert both to lowercase
            if (member.getFirstName().toLowerCase().contains(searchTerm.toLowerCase())) {
                results.add(member);
            }
        }
        return results;
    }
}