package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.function.Consumer;
import javafx.application.Platform;

/**
 * This class creates the visual interface for the application's login screen.
 * It handles input fields for username and password and authentication logic.
 */
public class LoginView {

    // Input fields for typing username and password
    private CustomTextField usernameField;
    private PasswordTextField passwordField;

    // Variables to access the system and what to do when login works
    private final ClubSystem system;

    // This function will be called if the login is successful
    private final Consumer<User> onLoginSuccess;

    /**
     * Constructs the LoginView and saves necessary dependencies.
     * @param system The central system object for authentication.
     * @param onLoginSuccess The action to perform upon a successful login.
     */
    public LoginView(ClubSystem system, Consumer<User> onLoginSuccess) {
        this.system = system;
        this.onLoginSuccess = onLoginSuccess;
    }

    /**
     * Builds and returns the visuals of the login screen.
     * @return The main {@code Parent} node of the view.
     */
    public Parent getView() {

        // Create the main background container
        StackPane root = new StackPane();
        root.setAlignment(Pos.CENTER);

        // Create the card box that holds all the login items
        VBox card = new VBox(15);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(400);
        card.setMaxHeight(Region.USE_PREF_SIZE);
        card.setAlignment(Pos.TOP_CENTER);

        // Load our logo
        ImageView logoImage = new ImageView();
        try {
            Image image = new Image(getClass().getResourceAsStream("/org/example/memberclubjavafx_assignment5/icon.png"));
            logoImage.setImage(image);
            logoImage.setFitWidth(100);
            logoImage.setPreserveRatio(true);
        } catch (Exception exception) {
            System.err.println("Kunde inte ladda logotyp: " + exception.getMessage());
        }

        // Create the main title text
        Label brand = new Label("FRILUFTSUTHYRNING");
        brand.getStyleClass().add("title-2");
        brand.setStyle("-fx-font-size: 24px; -fx-font-weight: 800; -fx-text-fill: -fx-color-text-header;");

        // Create the subtitle text
        Label subtitle = new Label("Logga in för att fortsätta");
        subtitle.getStyleClass().add("subtitle-text");

        // Put title and subtitle in a box
        VBox headerBox = new VBox(5, brand, subtitle);
        headerBox.setAlignment(Pos.CENTER);

        // Put icon and text together in a container
        VBox brandingContainer = new VBox(15, logoImage, headerBox);
        brandingContainer.setAlignment(Pos.CENTER);
        brandingContainer.setPadding(new Insets(0, 0, 40, 0));

        // Create the label for the username field
        Label userLabel = new Label("Användarnamn");
        userLabel.getStyleClass().add(Styles.TEXT_BOLD);
        userLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-color-text-muted;");

        // Create the input field for username
        usernameField = new CustomTextField();
        usernameField.setLeft(new FontIcon(Feather.USER));
        usernameField.setPromptText("");
        usernameField.setPrefHeight(46);

        // Put label and field in a box and limit width
        VBox userBox = new VBox(6, userLabel, usernameField);
        userBox.setFillWidth(true);
        userBox.setAlignment(Pos.CENTER_LEFT);
        userBox.setMaxWidth(250);

        // Create the label for the password field
        Label passLabel = new Label("Lösenord");
        passLabel.getStyleClass().add(Styles.TEXT_BOLD);
        passLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: -fx-color-text-muted;");

        // Create the password field (hides the text)
        passwordField = new PasswordTextField();
        passwordField.setLeft(new FontIcon(Feather.LOCK));
        passwordField.setPromptText("");
        passwordField.setPrefHeight(46);

        // Put label and field in a box
        VBox passBox = new VBox(6, passLabel, passwordField);
        passBox.setFillWidth(true);
        passBox.setAlignment(Pos.CENTER_LEFT);
        passBox.setMaxWidth(250);

        // Add some empty space before the buttons
        Region spacer = new Region();
        spacer.setPrefHeight(10);

        // Create a horizontal box to put buttons side by side
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setMaxWidth(300);

        // Create the login button and set its action
        Button loginButton = new Button("Logga in");
        loginButton.setDefaultButton(true);
        loginButton.getStyleClass().addAll("accent", "action-btn");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> handleLogin(root));

        // Create the exit button and set its action
        Button exitButton = new Button("Avsluta");
        exitButton.getStyleClass().addAll("button-outlined", "action-btn");
        exitButton.setMaxWidth(Double.MAX_VALUE);
        exitButton.setStyle("-fx-text-fill: -fx-color-danger; -fx-border-color: -fx-color-border-subtle;");
        exitButton.setOnAction(e -> System.exit(0));

        // Make both buttons grow to fill the space equally
        HBox.setHgrow(loginButton, Priority.ALWAYS);
        HBox.setHgrow(exitButton, Priority.ALWAYS);

        // Add buttons to the horizontal box
        buttonBox.getChildren().addAll(loginButton, exitButton);

        // Add all the parts to the card container
        card.getChildren().addAll(
                brandingContainer,
                userBox,
                passBox,
                spacer,
                buttonBox
        );

        // Add the card to the root view
        root.getChildren().add(card);

        Platform.runLater(() -> root.requestFocus());

        return root;
    }

    /**
     * Attempts to log in the user using the username and password.
     * Shows an error notification if authentication fails or fields are empty.
     * @param root The {@code StackPane} used as the root for displaying notifications.
     */
    private void handleLogin(StackPane root) {

        // Get the text from the input boxes
        String username = usernameField.getText().trim();
        String password = passwordField.getPassword().trim();

        // Check if the fields are empty
        if (username.isEmpty() || password.isEmpty()) {
            NotificationFactory.show("Saknas", "Fyll i alla fält", NotificationFactory.Type.ERROR, root);
            return;
        }

        // Ask the system to check if the user exists and password is correct
        User user = system.authenticateUser(username, password);

        // If login was successful, run the success action 'onLoginSuccess'
        if (user != null) {
            onLoginSuccess.accept(user);

        } else {

            // If login failed, show an error message and clear the password field
            NotificationFactory.show("Fel", "Fel användarnamn eller lösenord", NotificationFactory.Type.ERROR, root);
            passwordField.clear();
        }
    }
}