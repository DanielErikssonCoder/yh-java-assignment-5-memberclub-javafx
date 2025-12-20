package org.example.memberclubjavafx_assignment5.view;

import javafx.animation.*;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.CacheHint;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.service.UptimeService;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * This class builds the main side menu (sidebar) for the application.
 * It handles navigation between different views and manages the animation
 * for expanding and collapsing the menu. It inherits from {@code VBox}.
 */
public class SidebarView extends VBox {

    // Variables for the width of the menu when it is expanded or collapsed
    private final double WIDTH_EXPANDED = 220;
    private final double WIDTH_COLLAPSED = 60;

    // Settings for the animation speed and style
    private final Duration ANIM_DURATION = Duration.millis(250);
    private final Interpolator ANIM_EASE = Interpolator.EASE_BOTH;

    // A list to save all our navigation buttons so we can easily update them
    private final List<Button> navButtons = new ArrayList<>();

    // Variables to handle navigation and logout actions
    private final Consumer<String> onNavigate;
    private final Runnable onLogout;

    // UI components that we need to access in different methods
    private VBox brandTextBox;
    private HBox userProfileBox;
    private Button toggleBtn;
    private HBox logoBox;
    private HBox toggleBox;

    /**
     * Creates the sidebar and sets up all its components and functionality.
     * @param currentUser The currently logged-in {@code User}.
     * @param uptimeService Service for tracking application uptime.
     * @param onNavigate A function to call when a navigation button is clicked.
     * @param onLogout A function to call when the user clicks the logout area.
     */
    public SidebarView(User currentUser, UptimeService uptimeService, Consumer<String> onNavigate, Runnable onLogout) {

        this.onNavigate = onNavigate;
        this.onLogout = onLogout;

        // Set the alignment and padding for the whole box
        setAlignment(Pos.TOP_CENTER);
        setPadding(new Insets(15, 10, 25, 10));

        // Set the starting width of the sidebar
        setPrefWidth(WIDTH_EXPANDED);
        setMinWidth(WIDTH_EXPANDED);
        setMaxWidth(WIDTH_EXPANDED);

        // Add the CSS class for styling
        getStyleClass().add("sidebar");
        setSpacing(8);

        // This rectangle clips the content so it doesn't show outside the box when resizing
        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(widthProperty());
        clipRect.heightProperty().bind(heightProperty());
        setClip(clipRect);

        // Call the setup method to create all buttons and labels
        setup(currentUser);

        // Add a listener to handle smooth start-up animation and performance optimization
        sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {

                // Wait until the window is actually ready to show
                Platform.runLater(() -> {

                    // Force the app to calculate all styles and sizes
                    applyCss();
                    layout();

                    // Turn on cache to improve performance during startup
                    setCache(true);
                    setCacheHint(CacheHint.SPEED);

                    // Turn off cache after a short time to save memory
                    PauseTransition warmup = new PauseTransition(Duration.millis(500));
                    warmup.setOnFinished(e -> {

                        // Go back to normal settings when done
                        setCache(false);
                        setCacheHint(CacheHint.DEFAULT);
                    });

                    warmup.play();
                });
            }
        });
    }

    /**
     * Helper method to create all the visual components of the sidebar.
     * @param currentUser The user currently logged in.
     */
    private void setup(User currentUser) {

        // Create the small arrow button that collapses the menu
        toggleBtn = new Button("", new FontIcon(Feather.CHEVRON_LEFT));
        toggleBtn.getStyleClass().add("toggle-btn");
        toggleBtn.setTooltip(new Tooltip("Collapse menu"));
        toggleBtn.setOnAction(e -> toggleSidebar());

        // Put the toggle button in a box aligned to the right
        toggleBox = new HBox(toggleBtn);
        toggleBox.setAlignment(Pos.CENTER_RIGHT);
        toggleBox.setPadding(new Insets(0, 0, 5, 0));

        // Create the container for the logo and brand text
        logoBox = new HBox(12);
        logoBox.setAlignment(Pos.CENTER_LEFT);
        logoBox.setPadding(new Insets(0, 0, 30, 10));
        logoBox.setCursor(javafx.scene.Cursor.HAND);

        // Go to start page if the user clicks on the logo
        logoBox.setOnMouseClicked(e -> {
            setActiveButton("Översikt");
            onNavigate.accept("Översikt");
        });


        // Load the logo image
        ImageView brandIcon = new ImageView();
        try {
            // Loading image from resources
            Image img = new Image(getClass().getResourceAsStream("/org/example/memberclubjavafx_assignment5/icon.png"));
            brandIcon.setImage(img);
            brandIcon.setFitWidth(32);
            brandIcon.setPreserveRatio(true);
        } catch (Exception exception) {
            System.err.println("Could not load logo in Sidebar: " + exception.getMessage());
        }

        // Create the text labels for the logo
        brandTextBox = new VBox(0);
        Label brandLabel = new Label("WIGELLS");
        brandLabel.getStyleClass().addAll("title-3", "brand-title");
        Label subLabel = new Label("FRILUFTSUTHYRNING");
        subLabel.getStyleClass().add("brand-subtitle");

        brandLabel.setMinWidth(Region.USE_PREF_SIZE);
        subLabel.setMinWidth(Region.USE_PREF_SIZE);

        brandTextBox.getChildren().addAll(brandLabel, subLabel);
        logoBox.getChildren().addAll(brandIcon, brandTextBox);

        // Create all the navigation buttons using a helper method
        createNavButton("Översikt", Feather.ACTIVITY);
        createNavButton("Medlemmar", Feather.USERS);
        createNavButton("Artiklar", Feather.BOX);
        createNavButton("Uthyrning", Feather.SHOPPING_CART);
        createNavButton("Personal", Feather.USER_CHECK);

        // Add a spacer to push the user profile to the bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Create the box that shows the logged in user
        userProfileBox = new HBox(12);
        userProfileBox.getStyleClass().add("user-profile-box");
        userProfileBox.setAlignment(Pos.CENTER_LEFT);
        userProfileBox.setMaxWidth(Double.MAX_VALUE);
        userProfileBox.setPadding(new Insets(10, 10, 10, 10));

        // Get user name or use default
        String fullName = (currentUser != null) ? currentUser.getFullName() : "Admin";

        // Create labels for the user info
        VBox userInfo = new VBox(2);
        Label userName = new Label(fullName);
        userName.getStyleClass().add("user-name-label");
        userName.setMinWidth(Region.USE_PREF_SIZE);

        Label userRole = new Label("Logga ut");
        userRole.getStyleClass().add("user-role-label");
        userRole.setMinWidth(Region.USE_PREF_SIZE);

        userInfo.getChildren().addAll(userName, userRole);

        // Spacer to put the logout icon on the right
        Region pushRight = new Region();
        HBox.setHgrow(pushRight, Priority.ALWAYS);

        FontIcon logoutIcon = new FontIcon(Feather.LOG_OUT);
        logoutIcon.getStyleClass().add("logout-icon");

        // Add everything to the user box and add click event for logout
        userProfileBox.getChildren().addAll(userInfo, pushRight, logoutIcon);
        userProfileBox.setOnMouseClicked(e -> onLogout.run());

        // Add all main parts to the sidebar
        this.getChildren().addAll(toggleBox, logoBox);
        this.getChildren().addAll(navButtons);
        this.getChildren().addAll(spacer, userProfileBox);
    }

    /**
     * Helper method to quickly create a navigation button with an icon and title.
     * @param title The text label for the button (also used for navigation).
     * @param icon The {@code Feather} icon to display next to the text.
     */
    private void createNavButton(String title, Feather icon) {

        Button btn = new Button(title);
        btn.setGraphic(new FontIcon(icon));
        btn.getStyleClass().add("sidebar-btn");
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setUserData(title);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setMnemonicParsing(false);
        btn.setEllipsisString("");
        btn.setMinWidth(WIDTH_EXPANDED - 20);

        // Set action when button is clicked
        btn.setOnAction(e -> {

            if (btn.getStyleClass().contains("active")) {
                return;
            }

            // Remove active class from all buttons
            for (Button button : navButtons) {
                button.getStyleClass().remove("active");
            }

            // Add active class to the clicked button
            btn.getStyleClass().add("active");

            // Call the navigation method
            onNavigate.accept(title);
        });

        navButtons.add(btn);
    }

    /**
     * Finds a navigation button by its title and sets it as the currently active button.
     * This is useful when loading the app to show which view is currently selected.
     * @param title The title of the button to set as active.
     */
    public void setActiveButton(String title) {

        for (Button btn : navButtons) {

            if (btn.getUserData().toString().equals(title)) {

                for (Button button : navButtons) {
                    button.getStyleClass().remove("active");
                }

                btn.getStyleClass().add("active");

                break;
            }
        }
    }

    /**
     * Toggles the sidebar between its expanded and collapsed widths using animation.
     * It also manages showing/hiding text labels inside the sidebar during the transition.
     */
    private void toggleSidebar() {

        double currentWidth = this.getWidth();
        boolean isCurrentlyCollapsed = currentWidth < WIDTH_EXPANDED - 10;
        double targetWidth = isCurrentlyCollapsed ? WIDTH_EXPANDED : WIDTH_COLLAPSED;

        // Use cache for better performance during animation
        this.setCache(true);
        this.setCacheHint(CacheHint.SPEED);

        // If we are expanding the menu (going from collapsed to expanded)
        if (isCurrentlyCollapsed) {

            // Reset padding and alignment for expanded view
            this.setPadding(new Insets(15, 10, 25, 10));
            toggleBox.setAlignment(Pos.CENTER_RIGHT);
            logoBox.setAlignment(Pos.CENTER_LEFT);
            logoBox.setPadding(new Insets(0, 0, 30, 10));

            // Show brand text and animate it fading in
            brandTextBox.setVisible(true); brandTextBox.setManaged(true);
            FadeTransition fadeTransition = new FadeTransition(ANIM_DURATION, brandTextBox);
            fadeTransition.setFromValue(0); fadeTransition.setToValue(1); fadeTransition.play();

            // Show user info and animate it fading in
            if (userProfileBox.getChildren().size() >= 3) {
                userProfileBox.getChildren().get(0).setVisible(true);
                userProfileBox.getChildren().get(0).setManaged(true);
                userProfileBox.getChildren().get(1).setVisible(true);
                userProfileBox.getChildren().get(1).setManaged(true);
                userProfileBox.setAlignment(Pos.CENTER_LEFT);
                userProfileBox.getStyleClass().remove("collapsed");

                FadeTransition fadeTransitionUser = new FadeTransition(ANIM_DURATION, userProfileBox.getChildren().get(0));
                fadeTransitionUser.setFromValue(0); fadeTransitionUser.setToValue(1); fadeTransitionUser.play();
            }

            // Adjust buttons to show text next to the icon
            for (Button button : navButtons) {
                button.setContentDisplay(ContentDisplay.LEFT);
                button.setAlignment(Pos.CENTER_LEFT);
                button.getStyleClass().remove("collapsed");
            }
        }
        else {

            // If we are collapsing, fade out the text first
            FadeTransition fadeTransition = new FadeTransition(Duration.millis(150), brandTextBox);
            fadeTransition.setToValue(0); fadeTransition.play();

            if (!userProfileBox.getChildren().isEmpty()) {
                FadeTransition fadeTransitionUser = new FadeTransition(Duration.millis(150), userProfileBox.getChildren().getFirst());
                fadeTransitionUser.setToValue(0); fadeTransitionUser.play();
            }
        }

        // Create the main timeline for the width and rotation animation
        Timeline timeline = new Timeline();

        // Animate the width properties
        KeyValue keyValueWidth = new KeyValue(this.prefWidthProperty(), targetWidth, ANIM_EASE);
        KeyValue keyValueMin = new KeyValue(this.minWidthProperty(), targetWidth, ANIM_EASE);
        KeyValue keyValueMax = new KeyValue(this.maxWidthProperty(), targetWidth, ANIM_EASE);

        // Rotate the arrow icon when collapsed
        Node arrowIcon = toggleBtn.getGraphic();
        double targetAngle = isCurrentlyCollapsed ? 0 : 180;
        KeyValue keyValueRotate = new KeyValue(arrowIcon.rotateProperty(), targetAngle, ANIM_EASE);

        timeline.getKeyFrames().add(new KeyFrame(ANIM_DURATION, keyValueWidth, keyValueMin, keyValueMax, keyValueRotate));

        // When animation is finished
        timeline.setOnFinished(e -> {

            // Turn off cache
            this.setCache(false);
            this.setCacheHint(CacheHint.DEFAULT);

            // If the sidebar is now collapsed (after the animation is finished)
            if (!isCurrentlyCollapsed) {

                // Set padding and alignment for collapsed view (less or zero horizontal padding)
                this.setPadding(new Insets(15, 0, 25, 0));
                toggleBox.setAlignment(Pos.CENTER);
                logoBox.setAlignment(Pos.CENTER);
                logoBox.setPadding(new Insets(0, 0, 30, 0));

                // Hide the brand text completely and reset opacity for next expansion
                brandTextBox.setVisible(false); brandTextBox.setManaged(false);
                brandTextBox.setOpacity(1);

                // Change buttons to only show icons
                for (Button button : navButtons) {
                    button.setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
                    button.setAlignment(Pos.CENTER);
                    button.getStyleClass().add("collapsed");
                }

                // Hide user info text completely
                if (userProfileBox.getChildren().size() >= 3) {
                    userProfileBox.getChildren().get(0).setVisible(false);
                    userProfileBox.getChildren().get(0).setManaged(false);
                    userProfileBox.getChildren().get(0).setOpacity(1);

                    userProfileBox.getChildren().get(1).setVisible(false);
                    userProfileBox.getChildren().get(1).setManaged(false);

                    userProfileBox.setAlignment(Pos.CENTER);
                    userProfileBox.getStyleClass().add("collapsed");
                }
            }
        });

        // Start the animation
        timeline.play();
    }
}