package org.example.memberclubjavafx_assignment5.service;

import java.io.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.example.memberclubjavafx_assignment5.model.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * This class handles all the saving and loading of files.
 * It saves our data (Members, Items, Rentals) into JSON text files in the 'data/' folder.
 * Without this class, all data would disappear every time we close the program.
 */
public class StorageService {

    // The tool we use to convert Java objects to JSON and back
    private final Gson gson;

    // The folder where we keep our files
    private final String folderPath = "data/";

    /**
     * Constructor. Checks if the data folder exists, and creates it if it's missing.
     */
    public StorageService() {

        // Create a File object representing the directory
        File directory = new File(folderPath);

        // If the folder doesn't exist yet, create it
        if (!directory.exists()) {
            directory.mkdir();
        }

        // Get the configured Gson tool
        this.gson = GsonConfig.createGson();
    }

    // Specific methods for each data type
    public void saveUsers(List<User> users) {
        saveList(users, "users.json", new TypeToken<ArrayList<User>>(){}.getType());
    }

    public List<User> loadUsers() {
        return loadList("users.json", new TypeToken<ArrayList<User>>(){}.getType());
    }

    public void saveItems(List<Item> items) {
        saveList(items, "items.json", new TypeToken<ArrayList<Item>>(){}.getType());
    }

    public List<Item> loadItems() {
        return loadList("items.json", new TypeToken<ArrayList<Item>>(){}.getType());
    }

    public void saveMembers(List<Member> members) {
        saveList(members, "members.json", new TypeToken<ArrayList<Member>>(){}.getType());
    }

    public List<Member> loadMembers() {
        return loadList("members.json", new TypeToken<ArrayList<Member>>(){}.getType());
    }

    public void saveRentals(List<Rental> rentals) {
        saveList(rentals, "rentals.json", new TypeToken<ArrayList<Rental>>(){}.getType());
    }

    public List<Rental> loadRentals() {
        return loadList("rentals.json", new TypeToken<ArrayList<Rental>>(){}.getType());
    }

    /**
     * Saves a list of any type of object to a file.
     * @param list The list to save (can be Users, Items, etc.)
     * @param filename The name of the file (e.g., "members.json")
     * @param type Information about what kind of list this is (needed by Gson)
     * @param <T> The type of objects in the list
     */
    private <T> void saveList(List<T> list, String filename, Type type) {

        // Automatically closes the file when we are done
        try (Writer writer = new FileWriter(folderPath + filename)) {

            // Convert the list to JSON text and write it to the file
            gson.toJson(list, type, writer);

            System.out.println("Sparade " + filename);

        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Loads a list of any type of object from a file.
     * @param filename The name of the file to read.
     * @param type Information about what kind of list we expect back.
     * @return A list of objects, or an empty list if the file didn't exist.
     * @param <T> The type of objects in the list
     */
    private <T> List<T> loadList(String filename, Type type) {

        File file = new File(folderPath + filename);

        // If the file doesn't exist, return an empty list.
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (Reader reader = new FileReader(file)) {

            // Convert the JSON text in the file back into Java objects
            List<T> list = gson.fromJson(reader, type);

            // If the file was empty or something went wrong, return an empty list
            return list != null ? list : new ArrayList<>();

        } catch (Exception exception) {
            System.out.println("Kunde inte ladda " + filename + ": " + exception.getMessage());
            return new ArrayList<>();
        }
    }
}