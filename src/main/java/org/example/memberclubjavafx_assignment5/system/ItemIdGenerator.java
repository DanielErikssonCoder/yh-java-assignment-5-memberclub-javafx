package org.example.memberclubjavafx_assignment5.system;

/**
 * This class helps us create unique IDs for every new item.
 * It uses simple counters that go up by one for each type of item.
 */
public class ItemIdGenerator {

    // Counters for camping gear
    private int backpackCounter = 1;
    private int lanternCounter = 1;
    private int sleepingBagCounter = 1;
    private int tentCounter = 1;
    private int trangiaCounter = 1;

    // Counters for fishing gear
    private int baitCounter = 1;
    private int netCounter = 1;
    private int rodCounter = 1;

    // Counters for boats
    private int kayakCounter = 1;
    private int electricBoatCounter = 1;
    private int motorBoatCounter = 1;
    private int rowBoatCounter = 1;

    // Creates an ID for a backpack
    public String generateBackpackId() {

        // We use %03d to add zeros before the number
        return "BACK-" + String.format("%03d", backpackCounter++);
    }

    // Creates an ID for a lantern
    public String generateLanternId() {
        return "LANT-" + String.format("%03d", lanternCounter++);
    }

    // Creates an ID for a sleeping bag
    public String generateSleepingBagId() {
        return "SLEEP-" + String.format("%03d", sleepingBagCounter++);
    }

    // Creates an ID for a tent
    public String generateTentId() {
        return "TENT-" + String.format("%03d", tentCounter++);
    }

    // Creates an ID for a trangia stove
    public String generateTrangiaId() {
        return "TRANG-" + String.format("%03d", trangiaCounter++);
    }

    // Creates an ID for bait
    public String generateBaitId() {
        return "BAIT-" + String.format("%03d", baitCounter++);
    }

    // Creates an ID for a net
    public String generateNetId() {
        return "NET-" + String.format("%03d", netCounter++);
    }

    // Creates an ID for a fishing rod
    public String generateRodId() {
        return "ROD-" + String.format("%03d", rodCounter++);
    }

    // Creates an ID for a kayak
    public String generateKayakId() {
        return "KAY-" + String.format("%03d", kayakCounter++);
    }

    // Creates an ID for an electric boat
    public String generateElectricBoatId() {
        return "EBOAT-" + String.format("%03d", electricBoatCounter++);
    }

    // Creates an ID for a motorboat
    public String generateMotorBoatId() {
        return "MBOAT-" + String.format("%03d", motorBoatCounter++);
    }

    // Creates an ID for a rowboat
    public String generateRowBoatId() {
        return "RBOAT-" + String.format("%03d", rowBoatCounter++);
    }
}