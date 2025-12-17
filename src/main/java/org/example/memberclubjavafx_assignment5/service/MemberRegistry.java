package org.example.memberclubjavafx_assignment5.service;

import org.example.memberclubjavafx_assignment5.model.Member;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class keeps track of all the members in the club.
 * It works just like the Inventory but for people.
 */
public class MemberRegistry {

    // We store members here. The ID is the key.
    private final Map<Integer, Member> members;

    /**
     * Constructor that creates an empty list (map) for members.
     */
    public MemberRegistry() {
        this.members = new HashMap<>();
    }

    /**
     * Adds a new member to the list.
     */
    public void addMember(Member member) {
        members.put(member.getId(), member);
    }

    /**
     * Finds a member using their ID number.
     */
    public Member getMember(int memberId) {
        return members.get(memberId);
    }

    /**
     * Removes a member from the club.
     */
    public boolean removeMember(int memberId) {

        if (members.containsKey(memberId)) {
            members.remove(memberId);
            return true;

        } else {
            return false;
        }
    }

    /**
     * Returns a list of everyone in the club.
     */
    public List<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }

    /**
     * Counts how many members we have.
     */
    public int getMemberCount() {
        return members.size();
    }
}