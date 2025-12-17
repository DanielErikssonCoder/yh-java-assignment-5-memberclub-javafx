package org.example.memberclubjavafx_assignment5.view;

import javafx.scene.Node;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import org.example.memberclubjavafx_assignment5.model.enums.*;

/**
 * This class provides helpful utility methods for the user interface (View).
 * Its main job is to translate Enum values into readable Swedish strings for display in the application.
 */
public class ViewUtils {

    /**
     * Wraps a given JavaFX component (Node) in a main panel style container. This is used to apply a consistent background.
     * @param content The JavaFX node containing the view's content.
     * @return A {@code StackPane} with the 'main-view-panel' style applied.
     */
    public static StackPane wrapInMainPanel(Node content) {
        StackPane wrapper = new StackPane(content);
        wrapper.getStyleClass().add("main-view-panel");
        VBox.setVgrow(wrapper, Priority.ALWAYS);
        return wrapper;
    }

    /**
     * Translates an object, usually an Enum, into its Swedish string representation.
     * This is used to display user-friendly text instead of raw Enum names in the UI.
     * @param object The object to be translated.
     * @return The Swedish translation as a {@code String}.
     */
    public static String translate(Object object) {
        if (object == null) {
            return "";
        }

        // Handle 'Membership' levels
        if (object instanceof MembershipLevel) {

            MembershipLevel level = (MembershipLevel) object;

            switch (level) {
                case STANDARD: return "Standard";
                case STUDENT: return "Student";
                case PREMIUM: return "Premium";
                default: return level.name();
            }
        }

        // Check if the object is an ItemType
        if (object instanceof ItemType) {

            ItemType itemType = (ItemType) object;

            switch (itemType) {

                // Camping
                case TENT: return "Tält";
                case BACKPACK: return "Ryggsäck";
                case SLEEPING_BAG: return "Sovsäck";
                case LANTERN: return "Lykta";
                case TRANGIA_KITCHEN: return "Trangiakök";

                // Fishing
                case FISHING_ROD: return "Fiskespö";
                case FISHING_NET: return "Fiskenät";
                case FISHING_BAIT: return "Fiskedrag";

                // Vehicles
                case KAYAK: return "Kajak";
                case ROW_BOAT: return "Roddbåt";
                case MOTOR_BOAT: return "Motorbåt";
                case ELECTRIC_BOAT: return "Elbåt";
                default: return itemType.name();
            }
        }

        // Check if it is ItemStatus
        if (object instanceof ItemStatus) {

            ItemStatus itemStatus = (ItemStatus) object;

            switch (itemStatus) {
                case AVAILABLE: return "Tillgänglig";
                case RENTED: return "Uthyrd";
                case BROKEN: return "Trasig";
                default: return itemStatus.name();
            }
        }

        // Check colors
        if (object instanceof Color) {

            Color color = (Color) object;

            switch (color) {
                case BLACK: return "Svart";
                case WHITE: return "Vit";
                case GRAY: return "Grå";
                case RED: return "Röd";
                case BLUE: return "Blå";
                case GREEN: return "Grön";
                case YELLOW: return "Gul";
                case ORANGE: return "Orange";
                case PURPLE: return "Lila";
                case BROWN: return "Brun";
                case SILVER: return "Silver";
                case GOLD: return "Guld";
                case CAMOUFLAGE: return "Kamouflage";
                case MULTICOLOR: return "Flerfärgad";
                default: return color.name();
            }
        }

        // Check materials
        if (object instanceof Material) {

            Material material = (Material) object;

            switch (material) {
                case ALUMINUM: return "Aluminium";
                case STEEL: return "Stål";
                case STAINLESS_STEEL: return "Rostfritt stål";
                case CARBON_FIBER: return "Kolfiber";
                case FIBERGLASS: return "Glasfiber";
                case NYLON: return "Nylon";
                case RIPSTOP_NYLON: return "Ripstop Nylon";
                case POLYESTER: return "Polyester";
                case CANVAS: return "Canvas";
                case PLASTIC: return "Plast";
                case DOWN: return "Dun";
                case SYNTHETIC: return "Syntet";
                case WOOD: return "Trä";
                case BAMBOO: return "Bambu";
                default: return material.name();
            }
        }

        // Check seasons
        if (object instanceof SeasonRating) {

            SeasonRating seasonRating = (SeasonRating) object;

            switch (seasonRating) {
                case SUMMER: return "Sommar";
                case THREE_SEASON: return "Tre-säsong";
                case WINTER: return "Vinter";
                default: return seasonRating.name();
            }
        }

        // Check tent types
        if (object instanceof TentType) {

            TentType tentType = (TentType) object;

            switch (tentType) {
                case DOME: return "Kupol";
                case TUNNEL: return "Tunnel";
                case CABIN: return "Kabin";
                default: return tentType.name();
            }
        }

        // Check backpack types
        if (object instanceof BackpackType) {

            BackpackType backpackType = (BackpackType) object;

            switch (backpackType) {
                case DAYPACK: return "Dagstur";
                case WEEKEND: return "Helg";
                case EXPEDITION: return "Expedition";
                default: return backpackType.name();
            }
        }

        // Check power sources
        if (object instanceof PowerSource) {

            PowerSource powerSource = (PowerSource) object;

            switch (powerSource) {
                case BATTERY: return "Batteri";
                case RECHARGEABLE: return "Laddningsbar";
                case GAS: return "Gas";
                case SOLAR: return "Solcell";
                default: return powerSource.name();
            }
        }

        // Check fuel types
        if (object instanceof FuelType) {

            FuelType fuelType = (FuelType) object;

            switch (fuelType) {
                case ALCOHOL: return "Sprit";
                case GAS: return "Gas";
                case MULTIFUEL: return "Multifuel";
                case GASOLINE: return "Bensin";
                case DIESEL: return "Diesel";
                default: return fuelType.name();
            }
        }

        // Check fishing rod types
        if (object instanceof RodType) {

            RodType rodType = (RodType) object;

            switch (rodType) {
                case CASTING: return "Spinn";
                case SPINNING: return "Haspel";
                case FLY: return "Fluga";
                case ICE: return "Pimpel";
                case JIGGING: return "Jigging";
                default: return rodType.name();
            }
        }

        // Check net sizes
        if (object instanceof NetSize) {

            NetSize netSize = (NetSize) object;

            switch (netSize) {
                case SMALL: return "Liten";
                case MEDIUM: return "Medium";
                case LARGE: return "Stor";
                default: return netSize.name();
            }
        }

        // Check bait types
        if (object instanceof BaitType) {

            BaitType baitType = (BaitType) object;

            switch (baitType) {
                case WOBBLER: return "Wobbler";
                case SPINNER: return "Spinnare";
                case SPOON: return "Skeddrag";
                case JIGHEAD: return "Jigg";
                case CRANKBAIT: return "Crankbait";
                default: return baitType.name();
            }
        }

        // Check kayak types
        if (object instanceof KayakType) {

            KayakType kayakType = (KayakType) object;

            switch (kayakType) {
                case SIT_ON_TOP: return "Sit-on-top";
                case SIT_INSIDE: return "Sit-inside";
                case INFLATABLE: return "Uppblåsbar";
                case FISHING: return "Fiskekajak";
                default: return kayakType.name();
            }
        }

        // If no translation found, just return the object as string
        return object.toString();
    }

    /**
     * Creates a {@code StringConverter} for JavaFX components (like ComboBoxes)
     * that automatically uses the {@code translate} method to display the Swedish name for an Enum.
     * @param <T> The type of Enum that the converter will handle.
     * @return A new {@code StringConverter} instance.
     */
    public static <T> StringConverter<T> createTranslator() {

        // Creates a new converter that uses my translate-method above
        return new StringConverter<>() {

            // This method takes the object and turns it into a string for the UI
            @Override
            public String toString(T object) {

                // Call the translate method to get Swedish text
                return translate(object);
            }

            // This method is not needed for dropdowns, so I just return null
            @Override
            public T fromString(String string) {
                return null;
            }
        };
    }

    /**
     * Adds a listener to the scene that clears focus and selection when clicking on the background.
     */
    public static void addFocusClearListener(Scene scene) {

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            Node target = (Node) event.getTarget();

            if (isClickOnBackground(target)) {

                Node focusOwner = scene.getFocusOwner();

                // Clear selection in tables if they have focus
                if (focusOwner instanceof TableView) {
                    ((TableView<?>) focusOwner).getSelectionModel().clearSelection();
                }

                // Move focus to root to remove "glow" from fields
                if (scene.getRoot() != null) {
                    scene.getRoot().requestFocus();
                }
            }
        });
    }

    /**
     * Adds a listener to remove focus from ComboBoxes when they are closed.
     */
    public static void addComboBoxFocusHandler(Scene scene) {

        scene.addEventHandler(javafx.scene.control.ComboBox.ON_HIDDEN, event -> {

            if (event.getTarget() instanceof javafx.scene.control.ComboBox) {

                if (scene.getRoot() != null) {
                    scene.getRoot().requestFocus();
                }
            }
        });
    }

    /**
     * Helper method to check if a click was on the background or an interactive control.
     */
    private static boolean isClickOnBackground(Node target) {

        Node current = target;

        while (current != null) {

            // If we hit a Control, it's not the background
            if (current instanceof Control) {
                return false;
            }
            current = current.getParent();
        }
        return true;
    }
}