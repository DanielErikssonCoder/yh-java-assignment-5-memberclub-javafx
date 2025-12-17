package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.components.RentalBookingComponent;
import org.example.memberclubjavafx_assignment5.view.components.RentalHistoryComponent;
import javafx.geometry.Pos;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

/**
 * This class builds the main screen for managing all rental operations.
 * It combines the booking form (for creating new rentals) and the history list (for viewing and managing ongoing/past rentals).
 */
public class RentalView {

    // Variables for the system logic and user
    private final ClubSystem system;

    // The top-level container for showing popups and notifications
    private final StackPane rootStack;
    private final User currentUser;

    // These are the two main parts of the screen
    private RentalBookingComponent bookingComponent;
    private RentalHistoryComponent historyComponent;

    /**
     * Creates a new RentalView.
     * @param system The central system logic manager.
     * @param rootStack The root pane for showing overlays.
     * @param currentUser The user currently logged in.
     */
    public RentalView(ClubSystem system, StackPane rootStack, User currentUser) {
        this.system = system;
        this.rootStack = rootStack;
        this.currentUser = currentUser;
    }

    /**
     * Builds and returns the complete Rental Management view.
     * @return The main {@code Parent} node of the view, wrapped in a main panel.
     */
    public Parent getView() {

        // Create the main vertical box layout
        VBox layout = new VBox(20);

        // Create the title of the page
        Label titleLabel = new Label("Uthyrning");
        titleLabel.getStyleClass().add(Styles.TITLE_2);

        // Create the history list first so we can use it later
        historyComponent = new RentalHistoryComponent(system, rootStack);

        // Create the booking form, and send a small function to update the history when a booking is done
        bookingComponent = new RentalBookingComponent(system, rootStack, currentUser, () -> historyComponent.refreshTable());

        // Create a container for the history part
        VBox historyPanel = new VBox(15);
        historyPanel.getStyleClass().add("card-glass");
        historyPanel.setPadding(new Insets(20));

        // Create a header container to keep the title and total count on the same line
        HBox historyHeader = new HBox();
        historyHeader.setAlignment(Pos.CENTER_LEFT);

        // Title on the left side
        Label historyTitle = new Label("Historik");
        historyTitle.getStyleClass().add(Styles.TITLE_4);

        // Spacer to push the total count to the right
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Label for the total count on the right
        Label totalLabel = new Label("Totalt: ");
        totalLabel.getStyleClass().addAll(Styles.TEXT_BOLD);

        // This links the history list to the label so the count updates automatically
        historyComponent.setOnActiveCountChange(count -> totalLabel.setText("Totalt: " + count));

        // Add the parts to the header container
        historyHeader.getChildren().addAll(historyTitle, spacer, totalLabel);

        // Add the header and the history component to the panel
        historyPanel.getChildren().addAll(historyHeader, historyComponent);

        // Make the history list fill the space inside the panel
        VBox.setVgrow(historyComponent, Priority.ALWAYS);

        // Make the panel fill the rest of the screen
        VBox.setVgrow(historyPanel, Priority.ALWAYS);

        // Add all parts to the main layout
        layout.getChildren().addAll(titleLabel, bookingComponent, historyPanel);

        // Wrap it in the main panel style and return
        return ViewUtils.wrapInMainPanel(layout);
    }
}