package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.Rental;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * This class creates a custom dialog window (a popup) that displays a detailed
 * receipt after a successful rental booking has been completed.
 * It extends Dialog to act as a modal window.
 */
public class ReceiptDialog extends Dialog<ButtonType> {

    /**
     * Constructs the receipt dialog window, populating it with member details,
     * a list of rented items, the total cost, and employee information.
     * @param member The Member who made the booking.
     * @param rentals A list of Rental objects created in the transaction.
     * @param totalCost The calculated cost after any discounts.
     * @param system The ClubSystem instance to retrieve item details.
     * @param helper The User (employee) who processed the booking.
     */
    public ReceiptDialog(Member member, List<Rental> rentals, double totalCost, ClubSystem system, User helper) {

        // Make the window transparent to allow for custom styling and rounded corners
        initStyle(StageStyle.TRANSPARENT);

        // UI Fixes for transparency and focus
        setOnShowing(e -> {
            Scene scene = getDialogPane().getScene();
            if (scene != null) {
                scene.setFill(Color.TRANSPARENT);

                // Load custom CSS
                try {
                    String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                    scene.getStylesheets().add(cssPath);
                } catch (Exception ex) {
                    System.err.println("Kunde inte ladda CSS: " + ex.getMessage());
                }

                ViewUtils.addFocusClearListener(scene);

                ViewUtils.addComboBoxFocusHandler(scene);

                // Center the dialog over its owner window
                Window window = scene.getWindow();
                Window owner = getOwner();

                if (owner != null) {
                    window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                    window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                }
            }
        });

        // Clear standard DialogPane styling to make it completely custom
        DialogPane pane = getDialogPane();
        pane.setHeader(null);
        pane.setGraphic(null);
        pane.setPadding(Insets.EMPTY);
        pane.setBackground(Background.EMPTY);
        pane.setBorder(Border.EMPTY);
        pane.getStyleClass().clear();

        // Clear default buttons to use our custom close button
        pane.getButtonTypes().clear();

        // Create the main vertical box that holds everything
        VBox receiptContent = new VBox(15);

        // Apply custom style classes
        receiptContent.getStyleClass().addAll("card-panel", "modal-box");
        receiptContent.setPadding(new Insets(30));
        receiptContent.setPrefWidth(550);

        // Create the header title and subtitle
        Label headerTitle = new Label("WIGELLS FRILUFTSUTHYRNING");
        headerTitle.getStyleClass().add(Styles.TITLE_2);
        headerTitle.setStyle("-fx-font-weight: 800;");

        Label headerSubtitle = new Label("BOKNINGSBEKRÄFTELSE");
        headerSubtitle.getStyleClass().add(Styles.TEXT_MUTED);

        // Put the title and subtitle in a centered box
        VBox headerBox = new VBox(2, headerTitle, headerSubtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Create the section that shows customer information
        VBox customerInfo = new VBox(5);

        // Get the current date and time and format it nicely
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String now = LocalDateTime.now().format(dtf);

        // Add rows for date, member name and membership level using a helper method
        customerInfo.getChildren().addAll(
                createSummaryRow("Datum:", now, Styles.TEXT_MUTED),
                createSummaryRow("Medlem:", member.getFirstName() + " " + member.getLastName(), "text-header"),
                createSummaryRow("Medlemsnivå:", translateMembershipLevel(member.getMembershipLevel()), "text-header")
        );

        // Create a line to separate sections
        Separator itemSeparator = new Separator();

        // Use a GridPane to show the list of items with aligned columns
        GridPane itemList = new GridPane();
        itemList.setHgap(10);
        itemList.setVgap(8);
        itemList.setPadding(new Insets(10, 0, 10, 0));

        // Create the headers for the item list
        Label labelArticle = new Label("ARTIKEL");
        Label labelTime = new Label("TID");
        Label labelPrice = new Label("PRIS (ORD)");

        labelArticle.setStyle("-fx-font-weight: bold;");
        labelTime.setStyle("-fx-font-weight: bold;");
        labelPrice.setStyle("-fx-font-weight: bold; -fx-alignment: CENTER-RIGHT;");

        // Add headers to the first row of the grid
        itemList.add(labelArticle, 0, 0);
        itemList.add(labelTime, 1, 0);
        itemList.add(labelPrice, 2, 0);

        // Make the item name column grow to fill available space
        GridPane.setHgrow(labelArticle, Priority.ALWAYS);
        labelArticle.setMaxWidth(Double.MAX_VALUE);
        labelTime.setPrefWidth(80);
        labelPrice.setPrefWidth(90);

        // Variable to sum up the original price of all items
        double calculatedOriginalTotal = 0.0;

        // Loop through all rented items and add them to the grid
        int row = 1;
        for (Rental rental : rentals) {

            // Find the item object from the system using the ID
            Item item = system.getInventory().getItem(rental.getItemId());

            // Get item name or use 'Okänd' (Unknown) if not found
            String name = (item != null) ? item.getName() : "Okänd";

            // If name is too long, cut it off to fit the receipt
            if (name.length() > 25) name = name.substring(0, 24) + "...";

            // Variables to calculate rental duration
            long duration;
            String unit;

            // Calculate difference in whole days
            long days = ChronoUnit.DAYS.between(rental.getStartDate().toLocalDate(), rental.getExpectedReturnDate().toLocalDate());

            // Calculate difference in whole hours
            long hours = ChronoUnit.HOURS.between(rental.getStartDate(), rental.getExpectedReturnDate());

            // Ensure minimum 1 hour is displayed if dates are the same
            if (hours == 0) {
                hours = 1;
            }

            // Logic to calculate original price based on item settings
            double originalItemPrice = 0.0;

            if (item != null) {
                if (days > 0) {
                    duration = days;
                    unit = (days == 1) ? "dag" : "dagar";

                    // Calculate price using the item's daily rate
                    originalItemPrice = item.getPricePerDay() * days;

                } else {

                    duration = hours;
                    unit = (hours == 1) ? "tim" : "timmar";

                    // Calculate price using the item's hourly rate
                    originalItemPrice = item.getPricePerHour() * hours;
                }
            } else {
                duration = 0;
                unit = "";
            }

            // Add this item's original price to the running total
            calculatedOriginalTotal += originalItemPrice;

            // Create the string for time
            String timeString = duration + " " + unit;

            // Add the item name and duration to the grid
            itemList.add(new Label(name), 0, row);
            itemList.add(new Label(timeString), 1, row);

            // Format price to show two decimals (Original Price)
            Label priceLabel = new Label(String.format("%.2f", originalItemPrice));
            priceLabel.getStyleClass().add("align-right");
            itemList.add(priceLabel, 2, row);

            // Align the price text to the right side of the cell
            GridPane.setHalignment(priceLabel, Pos.CENTER_RIGHT.getHpos());

            // Move to next row
            row++;
        }

        // Create the total cost section separator
        Separator totalSeparator = new Separator();

        // Create a box to hold the totals, aligned to the right
        VBox totalsBox = new VBox(5);
        totalsBox.setAlignment(Pos.CENTER_RIGHT);

        // Show the total original price
        totalsBox.getChildren().add(createSummaryRow("Pris (Ordinarie):", String.format("%.2f", calculatedOriginalTotal), Styles.TEXT_MUTED));

        // Calculate the actual discount amount
        double discountAmount = calculatedOriginalTotal - totalCost;

        // If there is a discount, show it on the receipt
        if (discountAmount > 0.01) {
            String discountText = "-" + String.format("%.2f", discountAmount);
            totalsBox.getChildren().add(createSummaryRow("Rabatt:", discountText, "text-success"));
        }

        // Show the final total price
        totalsBox.getChildren().add(createSummaryRow("TOTALT (SEK):", String.format("%.2f", totalCost), Styles.TITLE_3));

        // Create the footer text section
        VBox footer = new VBox(5);
        footer.setAlignment(Pos.CENTER);
        footer.setPadding(new Insets(20, 0, 0, 0));

        // Show which employee handled the booking
        footer.getChildren().add(new Label("Du betjänades av " + helper.getFullName() + "."));
        footer.getChildren().add(new Label("Tack för din bokning!"));

        // Add the close button in the footer
        Button closeButton = new Button("Stäng");
        closeButton.getStyleClass().addAll("action-btn", Styles.ACCENT);
        closeButton.setPrefWidth(120);
        closeButton.setOnAction(e -> {
            setResult(ButtonType.CLOSE);
            close();
        });

        // Add extra spacing above the button
        VBox.setMargin(closeButton, new Insets(20, 0, 0, 0));
        footer.getChildren().add(closeButton);

        // Add all the different parts to the main content box
        receiptContent.getChildren().addAll(
                headerBox,
                new Separator(),
                customerInfo,
                itemSeparator,
                itemList,
                totalSeparator,
                totalsBox,
                footer
        );

        // Wrap content in a transparent StackPane to allow border radius effects
        StackPane wrapper = new StackPane(receiptContent);
        wrapper.setBackground(Background.EMPTY);
        pane.setContent(wrapper);
    }

    /**
     * Helper method to create a horizontal row with a key label and a value label.
     * @param key The descriptive text
     * @param value The corresponding data
     * @param styleClass The CSS style class to apply to the value label.
     * @return An HBox representing the summary row.
     */
    private HBox createSummaryRow(String key, String value, String styleClass) {

        HBox row = new HBox(10);

        // Create label for the key
        Label keyLabel = new Label(key);
        keyLabel.getStyleClass().add(Styles.TEXT_MUTED);
        keyLabel.setPrefWidth(120);

        // Spacer to push the value to the right side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Create label for the value
        Label valueLabel = new Label(value);
        valueLabel.getStyleClass().add(styleClass);
        valueLabel.setAlignment(Pos.CENTER_RIGHT);

        // Add everything to the row
        row.getChildren().addAll(keyLabel, spacer, valueLabel);
        return row;
    }

    /**
     * Helper method to get the discount rate based on the member's membership level.
     * @param level The MembershipLevel of the member.
     * @return The discount rate as a decimal (like 0.20 for 20% etc.)
     */
    private double getDiscount(MembershipLevel level) {
        return switch (level) {
            case STUDENT -> 0.20;
            case PREMIUM -> 0.30;
            default -> 0.0;
        };
    }

    /**
     * Helper method to translate the membership level Enum to a Swedish string.
     * @param level The MembershipLevel Enum.
     * @return The translated Swedish word.
     */
    private String translateMembershipLevel(MembershipLevel level) {
        return switch (level) {
            case STANDARD -> "Standard";
            case STUDENT -> "Student";
            case PREMIUM -> "Premium";
            default -> "";
        };
    }
}