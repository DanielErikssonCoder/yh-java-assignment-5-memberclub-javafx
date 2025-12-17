package org.example.memberclubjavafx_assignment5.view.components;

import javafx.beans.property.SimpleStringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.camping.CampingEquipment;
import org.example.memberclubjavafx_assignment5.model.enums.ItemStatus;
import org.example.memberclubjavafx_assignment5.model.fishing.FishingEquipment;
import org.example.memberclubjavafx_assignment5.model.vehicles.WaterVehicle;
import org.example.memberclubjavafx_assignment5.view.ViewUtils;

/**
 * This class creates a table that lists all items in the inventory.
 * It inherits from VBox so the table fills the layout vertically.
 */
public class ItemTableComponent extends VBox {

    // The main table element
    private final TableView<Item> table;

    /**
     * Constructor that builds the table and its columns.
     */
    public ItemTableComponent() {

        // Add some padding at the bottom
        setPadding(new Insets(0, 0, 8, 0));

        // Create the table
        table = new TableView<>();
        table.setMinHeight(200);

        // Make columns resize automatically to fill the width
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        // Text to show when the list is empty
        table.setPlaceholder(new Label("Inga artiklar i lager"));

        VBox.setVgrow(table, Priority.ALWAYS);

        // ID Column (gets the 'id' field from Item)
        TableColumn<Item, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        idCol.setMinWidth(80);
        idCol.setMaxWidth(120);

        // Type Column that also uses our helper to translate the type to Swedish
        TableColumn<Item, String> typeCol = new TableColumn<>("Typ");
        typeCol.setMinWidth(110);
        typeCol.setCellValueFactory(cell -> {
            Item item = cell.getValue();
            String translatedType = ViewUtils.translate(item.getItemType());
            return new SimpleStringProperty(translatedType);
        });

        // Name Column
        TableColumn<Item, String> nameCol = new TableColumn<>("Namn");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setMinWidth(160);

        // Brand Column (Must check which subclass it is to find the brand)
        TableColumn<Item, String> brandCol = new TableColumn<>("Märke");
        brandCol.setMinWidth(120);
        brandCol.setCellValueFactory(cell -> {

            Item item = cell.getValue();

            // Check the type and cast to get the brand
            if (item instanceof CampingEquipment campingEquipment) {
                return new SimpleStringProperty(campingEquipment.getBrand());
            }
            else if (item instanceof FishingEquipment fishingEquipment) {
                return new SimpleStringProperty(fishingEquipment.getBrand());
            }
            else if (item instanceof WaterVehicle waterVehicle) {
                return new SimpleStringProperty(waterVehicle.getBrand());
            }

            // Fallback if no brand found
            return new SimpleStringProperty("-");
        });

        // Daily Price Column
        TableColumn<Item, Double> priceCol = new TableColumn<>("Pris/Dygn");
        priceCol.setMinWidth(100);
        priceCol.setMaxWidth(140);
        priceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerDay"));

        // Custom formatting to add 'kr' (Swedish currency) after the price
        priceCol.setCellFactory(new Callback<TableColumn<Item, Double>, TableCell<Item, Double>>() {

            @Override
            public TableCell<Item, Double> call(TableColumn<Item, Double> param) {

                return new TableCell<Item, Double>() {

                    @Override
                    protected void updateItem(Double price, boolean empty) {

                        super.updateItem(price, empty);

                        if (empty || price == null) {
                            setText(null);

                        } else {
                            // Format with two decimals
                            String text = String.format("%.2f kr", price);
                            setText(text);
                        }
                    }
                };
            }
        });

        // Hourly Price Column
        TableColumn<Item, Double> hourlyPriceCol = new TableColumn<>("Pris/Tim");
        hourlyPriceCol.setMinWidth(100);
        hourlyPriceCol.setMaxWidth(140);
        hourlyPriceCol.setCellValueFactory(new PropertyValueFactory<>("pricePerHour"));

        // Same custom formatting for hourly price
        hourlyPriceCol.setCellFactory(new Callback<TableColumn<Item, Double>, TableCell<Item, Double>>() {

            @Override
            public TableCell<Item, Double> call(TableColumn<Item, Double> param) {

                return new TableCell<Item, Double>() {

                    @Override
                    protected void updateItem(Double price, boolean empty) {

                        super.updateItem(price, empty);

                        if (empty || price == null) {
                            setText(null);

                        } else {
                            String text = String.format("%.2f kr", price);
                            setText(text);
                        }
                    }
                };
            }
        });

        // Status Column
        TableColumn<Item, ItemStatus> statusCol = new TableColumn<>("Status");
        statusCol.setMinWidth(120);
        statusCol.setMaxWidth(180);
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell factory to create colored badges
        statusCol.setCellFactory(new Callback<TableColumn<Item, ItemStatus>, TableCell<Item, ItemStatus>>() {

            @Override
            public TableCell<Item, ItemStatus> call(TableColumn<Item, ItemStatus> param) {

                return new TableCell<Item, ItemStatus>() {

                    @Override
                    protected void updateItem(ItemStatus status, boolean empty) {

                        super.updateItem(status, empty);

                        if (empty || status == null) {
                            setGraphic(null);
                            setText(null);

                        } else {

                            // Translate status to Swedish
                            String statusText = ViewUtils.translate(status);
                            Label badge = new Label(statusText);
                            badge.getStyleClass().add("badge");

                            // Pick color based on status
                            switch (status) {

                                case AVAILABLE:
                                    badge.getStyleClass().add("badge-success");
                                    break;

                                case RENTED:
                                    badge.getStyleClass().add("badge-warning");
                                    break;

                                case BROKEN:
                                    badge.getStyleClass().add("badge-danger");
                                    break;
                            }

                            // Show the badge
                            setGraphic(badge);
                            setText(null);
                            setAlignment(Pos.CENTER_LEFT);
                        }
                    }
                };
            }
        });

        // Add all columns to the table
        table.getColumns().add(idCol);
        table.getColumns().add(typeCol);
        table.getColumns().add(nameCol);
        table.getColumns().add(brandCol);
        table.getColumns().add(priceCol);
        table.getColumns().add(hourlyPriceCol);
        table.getColumns().add(statusCol);

        // Add the table to this VBox
        getChildren().add(table);
    }

    /**
     * Getter to access the table from outside.
     * @return The TableView object.
     */
    public TableView<Item> getTable() {
        return table;
    }
}