package org.example.memberclubjavafx_assignment5.system;

import org.example.memberclubjavafx_assignment5.model.*;
import org.example.memberclubjavafx_assignment5.service.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This is the 'brain' of the application.
 * It connects all the different parts (Inventory, Members, Rentals) together.
 * Instead of passing everything around separately, we just pass this 'ClubSystem' object.
 */
public class ClubSystem {

    // These represent the different 'departments' of our system
    private final Inventory inventory;
    private final MemberRegistry memberRegistry;
    private final RentalService rentalService;
    private final MembershipService membershipService;
    private final RevenueService revenueService;

    // Helpers to create unique IDs for new items and members
    private final ItemIdGenerator itemIdGenerator;
    private final MemberIdGenerator memberIdGenerator;

    // Handles saving and loading to files
    private final StorageService storageService;

    // We store the admin users here (username -> User object)
    private final Map<String, User> users;

    // A service that tracks how long the app has been running
    private final UptimeService uptimeService;

    // A helper that automatically saves data every minute
    private final AutoSaveManager autoSaveManager;

    /**
     * Constructor.
     * This sets up all the services and loads data from the hard drive.
     */
    public ClubSystem() {

        // Create the ID generators first
        this.itemIdGenerator = new ItemIdGenerator();
        this.memberIdGenerator = new MemberIdGenerator();

        // Create the storage service so we can load files
        this.storageService = new StorageService();

        // Create the registries (lists) for items and members
        this.inventory = new Inventory();
        this.memberRegistry = new MemberRegistry();

        // Create services that use the registries (for example, RentalService needs to know about both Items and Members)
        this.rentalService = new RentalService(inventory, memberRegistry);
        this.membershipService = new MembershipService(memberRegistry, memberIdGenerator);
        this.revenueService = new RevenueService();

        // Initialize the user list
        this.users = new HashMap<>();

        this.uptimeService = new UptimeService();

        // Setup the auto-saver. We tell it to run the 'saveAll' method periodically
        this.autoSaveManager = new AutoSaveManager(this::saveAll);

        // Load all saved data from files
        loadData();

        // Start the background threads (uptime counter and auto save)
        this.autoSaveManager.start();
        this.uptimeService.start();
    }

    /**
     * Tries to load data from JSON files.
     * If no data exists (first time running), it creates sample data.
     */
    public void loadData() {

        // Users
        List<User> loadedUsers = storageService.loadUsers();

        if (loadedUsers.isEmpty()) {

            System.out.println("Inga användare hittades. Skapar standardkonton...");

            // Create default admin accounts if none exist
            createUser("danieleriksson", "0000", "Daniel", "Eriksson");
            createUser("tomaswigell", "5555", "Tomas", "Wigell");

        } else {

            // Put loaded users into our map
            for (User user : loadedUsers) {
                users.put(user.getUsername(), user);
            }
        }

        // Items
        List<Item> loadedItems = storageService.loadItems();

        if (loadedItems.isEmpty()) {

            System.out.println("Inget lager hittades. Laddar testdata...");

            // Load sample items (tents, boats etc.) so the app isn't empty
            SampleDataLoader.loadSampleItems(inventory, itemIdGenerator);

            // Save immediately so they exist next time
            saveAll();

        } else {

            for (Item item : loadedItems) {

                // Check if item already exists to avoid duplicates
                if (inventory.getItem(item.getId()) == null) {
                    inventory.addItem(item);
                }
            }
        }

        // Members
        List<Member> loadedMembers = storageService.loadMembers();

        if (loadedMembers.isEmpty()) {
            SampleDataLoader.loadSampleMembers(memberRegistry, memberIdGenerator);
            saveAll();

        } else {

            int maxId = 0;

            for (Member member : loadedMembers) {

                if (memberRegistry.getMember(member.getId()) == null) {
                    memberRegistry.addMember(member);
                }

                // We need to find the highest ID so the generator knows where it should continue
                if (member.getId() > maxId) {
                    maxId = member.getId();
                }
            }

            // Update the generator so the next new member gets a correct ID
            memberIdGenerator.setNextId(maxId + 1);
        }

        // Load the rental history
        List<Rental> loadedRentals = storageService.loadRentals();
        rentalService.setRentals(loadedRentals);
    }

    /**
     * Saves all data (Users, Items, Members, Rentals) to files.
     * I use 'synchronized' so that two parts of the program don't try to save at the exact same time.
     */
    public synchronized void saveAll() {

        // Convert our maps and internal lists to simple Lists that allow easy saving
        List<User> usersToSave = new ArrayList<>(getAllUsers());
        List<Item> itemsToSave = new ArrayList<>(inventory.getAllItems());
        List<Member> membersToSave = new ArrayList<>(memberRegistry.getAllMembers());
        List<Rental> rentalsToSave = new ArrayList<>(rentalService.getAllRentals());

        // Ask our storage service to write to disk
        storageService.saveUsers(usersToSave);
        storageService.saveItems(itemsToSave);
        storageService.saveMembers(membersToSave);
        storageService.saveRentals(rentalsToSave);
    }

    // Getters
    public Inventory getInventory() {
        return inventory;
    }

    public MemberRegistry getMemberRegistry() {
        return memberRegistry;
    }

    public RentalService getRentalService() {
        return rentalService;
    }

    public MembershipService getMembershipService() {
        return membershipService;
    }

    public RevenueService getRevenueService() {
        return revenueService;
    }

    public ItemIdGenerator getItemIdGenerator() {
        return itemIdGenerator;
    }

    public MemberIdGenerator getMemberIdGenerator() {
        return memberIdGenerator;
    }

    public UptimeService getUptimeService() {
        return uptimeService;
    }


    /**
     * Adds a user manually to the map.
     */
    public void addUser(User user) {
        users.put(user.getUsername(), user);
    }

    /**
     * Finds a user by their username.
     */
    public User getUser(String username) {
        return users.get(username);
    }

    /**
     * Creates a new account for the user and then saves it.
     * @return true if created, false if the username was taken.
     */
    public boolean createUser(String username, String password, String firstName, String lastName) {

        if (users.containsKey(username)) {
            return false;
        }

        User newUser = new User(username, password, firstName, lastName);
        users.put(username, newUser);

        // Save immediately to update the file
        storageService.saveUsers(new ArrayList<>(users.values()));

        return true;
    }

    /**
     * Removes a user account.
     * @return true if removed, false if not found.
     */
    public boolean removeUser(String username) {

        if (users.containsKey(username)) {
            users.remove(username);
            storageService.saveUsers(new ArrayList<>(users.values()));
            return true;
        }
        return false;
    }

    /**
     * Returns a list of all admin users.
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    /**
     * Checks if a login is valid.
     * @return The User object if login is correct, otherwise 'null'.
     */
    public User authenticateUser(String username, String password) {
        User user = users.get(username);

        if (user == null) {

            // User not found
            return null;
        }

        if (user.validatePassword(password)) {

            // Password correct
            return user;
        }

        // Password wrong
        return null;
    }

    /**
     * Shuts down the system safely.
     * Stops background threads and does a final save.
     */
    public void shutdown() {
        uptimeService.stop();
        saveAll();
        autoSaveManager.stop();
    }
}