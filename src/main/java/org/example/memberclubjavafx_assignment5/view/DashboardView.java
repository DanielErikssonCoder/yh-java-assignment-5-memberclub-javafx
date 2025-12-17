package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.theme.Styles;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.*;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Rental;
import org.example.memberclubjavafx_assignment5.model.enums.ItemType;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

/**
 * This class creates the dashboard screen, which is the overview page.
 * It displays KPIs and charts showing statistics about the member club's rentals, revenue, and inventory.
 */
public class DashboardView {

    // Variable to access the system data (members, items, rentals)
    private final ClubSystem system;

    // Variables for the revenue chart so we can update its data later
    private AreaChart<String, Number> revenueChart;
    private XYChart.Series<String, Number> revenueSeries;

    /**
     * Constructs the DashboardView.
     * @param system The central system logic manager.
     */
    public DashboardView(ClubSystem system) {
        this.system = system;
    }

    /**
     * Builds and returns the complete Dashboard view.
     * @return The main {@code Parent} node of the view, wrapped in a main panel.
     */
    public Parent getView() {

        // Create the main vertical box for the content
        VBox content = new VBox(20);

        // Create the main title
        Label title = new Label("Översikt");
        title.getStyleClass().add("page-title");

        // Create a horizontal row for the small info KPI cards
        HBox kpiRow = new HBox(20);

        // Calculate total revenue from all rentals
        double revenue = 0;
        for (Rental rental : system.getRentalService().getAllRentals()) {
            revenue += rental.getTotalCost();
        }

        // Get counts from the system
        int activeRentals = system.getRentalService().getActiveRentals().size();
        int totalMembers = system.getMemberRegistry().getMemberCount();
        int totalItems = system.getInventory().getItemCount();

        // Create the four cards and add them to the row
        kpiRow.getChildren().add(createKpiCard("Intäkter", String.format("%.0f SEK", revenue)));
        kpiRow.getChildren().add(createKpiCard("Utlånat", String.valueOf(activeRentals)));
        kpiRow.getChildren().add(createKpiCard("Medlemmar", String.valueOf(totalMembers)));
        kpiRow.getChildren().add(createKpiCard("Artiklar", String.valueOf(totalItems)));

        // Create a card container for the revenue chart
        VBox revenueCard = new VBox(15);
        revenueCard.getStyleClass().add("card-glass");
        revenueCard.setPadding(new Insets(20));

        // Let the card grow to fill space
        VBox.setVgrow(revenueCard, Priority.ALWAYS);

        // Header for the chart section
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label chartTitle = new Label("Historik (Intäkter)");
        chartTitle.getStyleClass().add(Styles.TITLE_4);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Dropdown to choose time period
        ComboBox<String> periodSelector = new ComboBox<>();
        periodSelector.setItems(FXCollections.observableArrayList("Senaste 7 dagarna", "Senaste 30 dagarna", "Senaste året"));

        // Set 'Senaste 7 dagarna' (last 7 days) as the default selection
        periodSelector.getSelectionModel().selectFirst();

        // Update chart when selection changes
        periodSelector.setOnAction(e -> updateRevenueChart(periodSelector.getValue()));

        header.getChildren().addAll(chartTitle, spacer, periodSelector);

        // Create axes for the chart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();

        // Make text horizontal so it is easy to read
        xAxis.setTickLabelRotation(0);

        // Create the area chart
        revenueChart = new AreaChart<>(xAxis, yAxis);
        revenueChart.setLegendVisible(false);

        // Disable animation to prevent bugs on certain platforms
        revenueChart.setAnimated(false);

        // Set minimum and preferred height
        revenueChart.setMinHeight(100);
        revenueChart.setPrefHeight(180);

        VBox.setVgrow(revenueChart, Priority.ALWAYS);

        // Add the data series object to the chart
        revenueSeries = new XYChart.Series<>();
        revenueChart.getData().add(revenueSeries);

        revenueCard.getChildren().addAll(header, revenueChart);

        // Create another card for the inventory chart
        VBox inventoryCard = new VBox(15);
        inventoryCard.getStyleClass().add("card-glass");
        inventoryCard.setPadding(new Insets(20));

        VBox.setVgrow(inventoryCard, Priority.ALWAYS);

        // Header for this chart
        HBox invHeader = new HBox();
        invHeader.setAlignment(Pos.CENTER_LEFT);
        invHeader.setMinHeight(46);

        Label invTitle = new Label("Lagerfördelning (Antal)");
        invTitle.getStyleClass().add(Styles.TITLE_4);
        invHeader.getChildren().add(invTitle);

        // Axes for the bar chart
        CategoryAxis invXAxis = new CategoryAxis();
        NumberAxis invYAxis = new NumberAxis();

        invXAxis.setTickLabelRotation(0);

        // Format y-axis to show only integers
        invYAxis.setTickLabelFormatter(new StringConverter<Number>() {
            @Override
            public String toString(Number object) {

                // If it is an integer, show it. If not, show nothing
                if (object.doubleValue() % 1 == 0) {
                    return String.valueOf(object.intValue());
                }

                return "";
            }

            @Override
            public Number fromString(String string) {

                if (string != null && !string.isEmpty()) {
                    return Integer.valueOf(string);
                }
                return 0;
            }
        });

        invYAxis.setMinorTickVisible(false);

        // Create the bar chart
        BarChart<String, Number> inventoryChart = new BarChart<>(invXAxis, invYAxis);
        inventoryChart.setLegendVisible(false);
        inventoryChart.setAnimated(true);

        inventoryChart.setMinHeight(100);
        inventoryChart.setPrefHeight(180);

        VBox.setVgrow(inventoryChart, Priority.ALWAYS);

        // Calculate how many items of each type we have
        XYChart.Series<String, Number> invSeries = new XYChart.Series<>();
        Map<String, Long> counts = new HashMap<>();

        // Initialize counts for all item types to 0
        for (ItemType type : ItemType.values()) {
            counts.put(ViewUtils.translate(type), 0L);
        }

        // Count items by looping through the inventory
        for (Item item : system.getInventory().getAllItems()) {
            String name = ViewUtils.translate(item.getItemType());
            counts.put(name, counts.get(name) + 1);
        }

        // Sort the data by the highest count first and add it to the chart
        counts.entrySet().stream().sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .forEach(e -> invSeries.getData().add(new XYChart.Data<>(e.getKey(), e.getValue())));

        inventoryChart.getData().add(invSeries);
        inventoryCard.getChildren().addAll(invHeader, inventoryChart);

        content.getChildren().addAll(title, kpiRow, revenueCard, inventoryCard);

        // Load the initial data for the revenue chart, using the new default
        updateRevenueChart("Senaste 7 dagarna");

        // Wrap it in the main panel style and return
        return ViewUtils.wrapInMainPanel(content);
    }

    /**
     * Helper method to create a small card displaying a KPI.
     * @param label The text label
     * @param value The calculated value
     * @return A {@code VBox} representing the KPI card.
     */
    private VBox createKpiCard(String label, String value) {

        VBox card = new VBox(10);
        card.getStyleClass().addAll("kpi-card", "card-glass");
        card.setMinWidth(150);

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lbl = new Label(label);
        lbl.getStyleClass().add("kpi-label");

        header.getChildren().add(lbl);

        Label val = new Label(value);
        val.getStyleClass().add("kpi-value");

        card.getChildren().addAll(header, val);
        HBox.setHgrow(card, Priority.ALWAYS);
        return card;
    }

    /**
     * Fetches rental data and updates the revenue chart based on the selected time period.
     * @param period The selected time period string ("Senaste veckan", "Senaste 30 dagarna" or "Senaste året").
     */
    private void updateRevenueChart(String period) {

        // Clear old data before adding new points
        revenueSeries.getData().clear();

        List<Rental> rentals = system.getRentalService().getAllRentals();
        LocalDate now = LocalDate.now();
        Locale swedishLocale = new Locale("sv", "SE");

        // Logic for last 7 days (including today)
        if ("Senaste 7 dagarna".equals(period)) {

            Map<LocalDate, Double> dailyRevenue = new TreeMap<>();

            // 6 days ago + today = 7 days
            int days = 6;

            // Fill all days in the 7-day period with 0.0 revenue initially
            for (int i = days; i >= 0; i--) {
                dailyRevenue.put(now.minusDays(i), 0.0);
            }

            // Add revenue for rentals that match the days in this period
            for (Rental rental : rentals) {

                LocalDate date = rental.getStartDate().toLocalDate();

                // Check if the rental date is within the 7 day window
                if (!date.isBefore(now.minusDays(days)) && !date.isAfter(now)) {
                    dailyRevenue.put(date, dailyRevenue.getOrDefault(date, 0.0) + rental.getTotalCost());
                }
            }

            // Add points to the chart, formatting the date to show the day of the week
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("E d MMM", swedishLocale);
            dailyRevenue.forEach((date, amount) -> revenueSeries.getData().add(new XYChart.Data<>(date.format(dtf), amount)));

        } else if ("Senaste 30 dagarna".equals(period)) {

            // Logic for last 30 days
            Map<LocalDate, Double> dailyRevenue = new TreeMap<>();

            // Initialize all 30 days with 0.0 revenue to ensure the chart shows gaps if needed
            for (int i = 29; i >= 0; i--) dailyRevenue.put(now.minusDays(i), 0.0);

            // Sum up revenue for matching days
            for (Rental rental : rentals) {

                LocalDate date = rental.getStartDate().toLocalDate();

                // Check if the rental date is within the 30 day window
                if (!date.isBefore(now.minusDays(29)) && !date.isAfter(now)) {
                    dailyRevenue.put(date, dailyRevenue.getOrDefault(date, 0.0) + rental.getTotalCost());
                }
            }

            // Format the date and add the points to the chart
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("d MMM", swedishLocale);
            dailyRevenue.forEach((date, amount) -> revenueSeries.getData().add(new XYChart.Data<>(date.format(dtf), amount)));

        } else {

            // Logic for last year ('LinkedHashMap' is used here to keep the months in the correct order)
            Map<String, Double> monthlyRevenue = new LinkedHashMap<>();

            // Initialize all 12 months with 0.0 revenue
            for (int i = 11; i >= 0; i--) {

                LocalDate d = now.minusMonths(i);

                // Get month name and capitalize the first letter
                String mName = d.getMonth().getDisplayName(TextStyle.SHORT, swedishLocale);
                mName = mName.substring(0, 1).toUpperCase() + mName.substring(1);

                // Create the key for the map
                monthlyRevenue.put(mName + " " + (d.getYear() % 100), 0.0);
            }

            // Sum up revenue per month
            for (Rental rental : rentals) {

                LocalDate date = rental.getStartDate().toLocalDate();

                // Check if the rental is within the last 12 months
                if (date.isAfter(now.minusMonths(12))) {

                    // Get month name and capitalize the first letter
                    String mName = date.getMonth().getDisplayName(TextStyle.SHORT, swedishLocale);
                    mName = mName.substring(0, 1).toUpperCase() + mName.substring(1);
                    String key = mName + " " + (date.getYear() % 100);

                    if (monthlyRevenue.containsKey(key)) {
                        monthlyRevenue.put(key, monthlyRevenue.get(key) + rental.getTotalCost());
                    }
                }
            }

            // Add the data points (month and total revenue) to the chart
            monthlyRevenue.forEach((month, amount) ->
                    revenueSeries.getData().add(new XYChart.Data<>(month, amount))
            );
        }
    }
}