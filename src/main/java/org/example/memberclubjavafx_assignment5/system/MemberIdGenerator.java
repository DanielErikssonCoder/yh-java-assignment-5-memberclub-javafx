package org.example.memberclubjavafx_assignment5.system;

/**
 * This class creates unique ID numbers for members.
 * It works just like the item generator but for people.
 */
public class MemberIdGenerator {

    // Simple counter that starts at 1
    private int counter = 1;

    // Returns the next ID number and then increases the counter
    public int generateMemberId() {
        return counter++;
    }

    //  Updates the counter to a specific number
    public void setNextId(int nextId) {
        this.counter = nextId;
    }
}