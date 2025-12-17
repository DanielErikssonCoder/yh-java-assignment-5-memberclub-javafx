package org.example.memberclubjavafx_assignment5.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.*;
import org.example.memberclubjavafx_assignment5.model.fishing.*;
import org.example.memberclubjavafx_assignment5.model.vehicles.*;

import java.time.LocalDateTime;

/**
 * This class helps us set up 'Gson' to save our data to JSON files.
 */
public class GsonConfig {

    /**
     * This method builds the Gson object that we use to save and load files.
     * @return The Gson tool.
     */
    public static Gson createGson() {

        /*
        We have a list of 'Item' objects. But an item can be many things, like a tent or a kayak.
        This helps Gson understand which specific type of item it is looking at.
        It adds a label called "type" to the JSON file so we know what is what.
         */
        RuntimeTypeAdapterFactory<Item> adapterFactory = RuntimeTypeAdapterFactory.of(Item.class, "type");

        // Here we list all the different things we can rent out

        // Camping items
        adapterFactory.registerSubtype(Tent.class, "Tent");
        adapterFactory.registerSubtype(Backpack.class, "Backpack");
        adapterFactory.registerSubtype(SleepingBag.class, "SleepingBag");
        adapterFactory.registerSubtype(TrangiaKitchen.class, "Trangia");
        adapterFactory.registerSubtype(Lantern.class, "Lantern");

        // Fishing items
        adapterFactory.registerSubtype(FishingRod.class, "Rod");
        adapterFactory.registerSubtype(FishingNet.class, "Net");
        adapterFactory.registerSubtype(FishingBait.class, "Bait");

        // Water vehicles
        adapterFactory.registerSubtype(Kayak.class, "Kayak");
        adapterFactory.registerSubtype(MotorBoat.class, "MotorBoat");
        adapterFactory.registerSubtype(ElectricBoat.class, "ElectricBoat");
        adapterFactory.registerSubtype(RowBoat.class, "RowBoat");

        // Build the final Gson tool with all our settings (we use a helper to save dates and times, and we use pretty printing for better readability)
        return new GsonBuilder().registerTypeAdapterFactory(adapterFactory).registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter()).setPrettyPrinting().create();
    }
}