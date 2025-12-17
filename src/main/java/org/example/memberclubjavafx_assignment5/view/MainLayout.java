package org.example.memberclubjavafx_assignment5.view;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;

/**
 * This class serves as the main frame for the application after login.
 * It uses a {@code BorderPane} to place the 'Sidebar' on the left and the main content area (StackPane) in the center.
 */
public class MainLayout extends BorderPane {

    // Variables to keep track of the system logic and the logged in user
    private final ClubSystem system;
    private final User currentUser;

    // This is where the actual view will be displayed
    private final StackPane contentArea;

    // The collapsible side menu for navigation
    private final SidebarView sidebar;

    // The root stack sits on top of everything to handle notifications and modal dialog overlays
    private final StackPane rootStack;

    /**
     * Constructs the main application layout.
     * @param system The central system logic manager.
     * @param currentUser The user who successfully logged in.
     * @param logoutHandler A function to call when the user clicks the logout button in the sidebar.
     */
    public MainLayout(ClubSystem system, User currentUser, Runnable logoutHandler) {

        this.system = system;
        this.currentUser = currentUser;

        // Create the area in the middle where we show different screens
        this.contentArea = new StackPane();
        this.contentArea.setPadding(new Insets(20));

        // Create the root stack
        this.rootStack = new StackPane();

        // Set pickOnBounds to false so that if this pane is empty, user clicks go to the BorderPane
        this.rootStack.setPickOnBounds(false);

        // Create the side menu, giving it functions for navigation and logout
        this.sidebar = new SidebarView(

                currentUser,
                system.getUptimeService(),

                // Send a function to handle clicks
                title -> handleNavigation(title),
                logoutHandler
        );

        // Set the layout (Menu on Left, Content in Center)
        this.setLeft(sidebar);
        this.setCenter(contentArea);

        // Show the initial screen (Dashboard) immediately when opening
        handleNavigation("Översikt");

        // Highlight the correct button in the sidebar
        sidebar.setActiveButton("Översikt");
    }

    /**
     * Wraps the main layout in an outer {@code StackPane} to create a layer for overlays like dialogs and notifications (the {@code rootStack}).
     * @return A {@code StackPane} containing the main application view and the overlay layer.
     */
    public StackPane getRootWrapper() {

        StackPane wrapper = new StackPane();

        // Assign an ID to the main content. This allows us to blur only this part
        this.setId("app-content");

        // Put the main layout at the bottom
        wrapper.getChildren().add(this);

        // Put the popup layer on top
        wrapper.getChildren().add(rootStack);

        return wrapper;
    }

    /**
     * Selects and loads a new view into the center content area based on the title of the button clicked in the sidebar.
     * @param viewName The name of the view to load.
     */
    private void handleNavigation(String viewName) {

        Parent nextView;

        // Check which view name was passed from the sidebar button
        switch (viewName) {
            case "Översikt":
                nextView = new DashboardView(system).getView();
                break;
            case "Medlemmar":
                nextView = new MemberView(system, rootStack).getView();
                break;
            case "Artiklar":
                nextView = new ItemView(system, rootStack).getView();
                break;
            case "Uthyrning":
                nextView = new RentalView(system, rootStack, currentUser).getView();
                break;
            case "Personal":
                nextView = new UserView(system, rootStack).getView();
                break;
            default:
                // Default fallback to DashboardView
                nextView = new DashboardView(system).getView();
                break;
        }

        // Call the method that swaps the content with animation
        switchContent(nextView);
    }

    /**
     * Replaces the current content in the center area with the new view
     * and plays a smooth fade in and slide up animation.
     * @param newView The new view (Parent node) to display.
     */
    private void switchContent(Parent newView) {

        // Remove the old screen
        contentArea.getChildren().clear();

        // Apply a CSS class that sets a maximum width
        newView.getStyleClass().add("content-constrained");

        // Ensure the view is centered horizontally in the top
        StackPane.setAlignment(newView, Pos.TOP_CENTER);

        // Add the new screen
        contentArea.getChildren().add(newView);

        // Prepare the animation: start invisible and slightly below its final position
        newView.setOpacity(0);
        newView.setTranslateY(20);

        // Creates a fade in effect
        FadeTransition fade = new FadeTransition(Duration.millis(200), newView);
        fade.setFromValue(0);
        fade.setToValue(1);

        // Creates a slide up effect
        TranslateTransition slide = new TranslateTransition(Duration.millis(200), newView);
        slide.setFromY(20);
        slide.setToY(0);

        // Play both animations at the same time
        ParallelTransition anim = new ParallelTransition(fade, slide);
        anim.play();
    }
}