package org.example.memberclubjavafx_assignment5;

import atlantafx.base.theme.NordDark;
import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Control;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.CustomWindowFrame;
import org.example.memberclubjavafx_assignment5.view.LoginView;
import org.example.memberclubjavafx_assignment5.view.MainLayout;
import org.example.memberclubjavafx_assignment5.view.WindowResizeHandler;
import java.net.URL;

/**
 * This is the main class that starts our application.
 * This class handles setting up the main window and switching between the Login and Main views.
 */
public class Main extends Application {

    // A reference to the 'brain' of our system where all the data and logic lives
    private ClubSystem system;

    // This is the main window of our app
    private Stage window;

    // This holds the path to our custom CSS file
    private String premiumCssPath;

    // This helper class lets the user resize the custom window frame easily.
    private WindowResizeHandler resizeHandler;

    // This class makes our window look custom
    private CustomWindowFrame windowFrame;

    /**
     * This is the very first method that runs when you start the program.
     * @param args Command line arguments, we don't really use them here.
     */
    public static void main(String[] args) {

        // These settings help JavaFX draw things faster on some computers
        System.setProperty("prism.order", "es2,es1,sw");
        System.setProperty("prism.vsync", "true");
        System.setProperty("javafx.animation.fullspeed", "true");

        // This tells JavaFX to start the application
        launch(args);
    }

    /**
     * This method is called by JavaFX right after 'main' runs.
     * It sets up the main window and the first screen (Login).
     * @param primaryStage The main window object provided by the JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {

        // We save the main window so we can use it later in other methods.
        this.window = primaryStage;

        // Setup Stage and System
        window.initStyle(StageStyle.TRANSPARENT);

        // Create the main system object that manages all data
        system = new ClubSystem();

        // Set the default theme for our application
        Application.setUserAgentStylesheet(new NordDark().getUserAgentStylesheet());

        // Load our extra CSS styles
        loadCustomCss();

        // Load app icon
        try {

            java.io.InputStream iconStream = getClass().getResourceAsStream("icon.png");

            if (iconStream != null) {
                javafx.scene.image.Image icon = new javafx.scene.image.Image(iconStream);
                window.getIcons().add(icon);
            }

        } catch (Exception exception) {
            System.err.println("Kunde inte ladda ikonen: " + exception.getMessage());
        }

        // Initialize window handler (helper for handling resizing when using an undecorated stage)
        resizeHandler = new WindowResizeHandler(window);

        // Create the custom frame that wraps our content and acts as a title bar
        windowFrame = new CustomWindowFrame(window, system, resizeHandler);

        // Set the name that appears in the taskbar
        window.setTitle("Wigells Friluftsuthyrning");

        window.setMinWidth(1280);
        window.setMinHeight(800);

        // Show the login screen first
        showLoginScreen();

        // Make the window visible
        window.show();

        // Initialize positions after window is shown
        resizeHandler.initializePositions();
    }

    /**
     * This method is called just before the application closes.
     */
    @Override
    public void stop() {

        // If the system 'exists', tell it to save data and shut down
        if (system != null) system.shutdown();
    }

    /**
     * Creates and displays the login screen for the application.
     * It also defines the action to perform when a successful login happens.
     */
    private void showLoginScreen() {

        // Create the LoginView and tell it to run the 'showMainApplication' method when the login is successful.
        LoginView loginView = new LoginView(system, this::showMainApplication);

        // Change the scene to show the login screen
        changeScene(loginView.getView());
    }

    /**
     * Creates and displays the main application interface after a user successfully logs in.
     * @param user The {@code User} object that successfully logged in, carrying user details.
     */
    private void showMainApplication(User user) {

        // Create the main content layout and tell it to run 'showLoginScreen' if the user logout
        MainLayout mainLayout = new MainLayout(system, user, this::showLoginScreen);

        // Change the scene to the main application
        changeScene(mainLayout.getRootWrapper());
    }

    /**
     * A helper method to switch to a new view (Parent node) in the main window
     * and apply the custom window frame features like the title bar.
     * @param content The new JavaFX node (like a Layout or a View) to display.
     */
    private void changeScene(javafx.scene.Parent content) {

        // Wrap the new content inside our custom frame (which adds the title bar etc.)
        javafx.scene.Parent wrappedContent = windowFrame.wrap(content);

        // Create the scene with the wrapped content and a default size to fit on small screens
        Scene scene = new Scene(wrappedContent, 1280, 800);

        // Set the scene's background to transparent
        scene.setFill(Color.TRANSPARENT);

        // Add our custom CSS styles if we found them earlier
        if (premiumCssPath != null) {
            scene.getStylesheets().add(premiumCssPath);
        }

        // Apply clipping to force rounded corners on everything
        makeRoundedCorners(scene);

        // Attach the resize listener so the user can drag the edges of the new scene
        resizeHandler.addResizeListener(scene);

        // Add a listener that helps clear focus/selection when clicking outside different controls
        addGlobalFocusListener(scene);

        // Remove combo box focus when picking an option
        addGlobalComboBoxHandler(scene);

        // Set the new scene on the main window
        window.setScene(scene);

        // Recalculate the positions for the resize handler, in case the size has changed
        resizeHandler.initializePositions();
    }

    /**
     * This method makes the corners of the application window round.
     * It uses a clipping rectangle because standard CSS {@code border-radius}
     * didn't always clip all child elements correctly in JavaFX, so this was a possible fix.
     * @param scene The current scene to apply the rounded corners on.
     */
    private void makeRoundedCorners(Scene scene) {

        // Get the root node (BorderPane from CustomWindowFrame)
        if (scene.getRoot() instanceof javafx.scene.layout.Region root) {

            // Create a rectangle that will act as the 'clipping mask' to cut off the corners
            Rectangle clipRectangle = new Rectangle();

            clipRectangle.setArcWidth(30);
            clipRectangle.setArcHeight(30);

            // Make the rectangle size always match the window size by binding them together
            clipRectangle.widthProperty().bind(scene.widthProperty());
            clipRectangle.heightProperty().bind(scene.heightProperty());

            // Set the rectangle as the 'clipping mask' on the root node
            root.setClip(clipRectangle);
        }
    }

    /**
     * Listens for all mouse clicks in the scene.
     * If the user clicks on the "background" (not a button, text field, or table),
     * it clears the current focus and any selection in tables to clean up the UI.
     * @param scene The current scene to attach the global listener to.
     */
    private void addGlobalFocusListener(Scene scene) {

        scene.addEventFilter(MouseEvent.MOUSE_PRESSED, event -> {

            // Find out which element the user clicked on
            Node target = (Node) event.getTarget();

            // If clicked on 'background' (meaning not an 'interactive control')
            if (isClickOnBackground(target)) {

                // If a TableView currently has focus, we should clear its selection
                Node focusOwner = scene.getFocusOwner();

                if (focusOwner instanceof TableView) {

                    // We cast it to a generic TableView to clear the selection
                    ((TableView<?>) focusOwner).getSelectionModel().clearSelection();
                }

                // Move focus to the root node (this removes borders from text fields etc.)
                scene.getRoot().requestFocus();
            }
        });
    }

    /**
     * Helper method to fix the issue where the blue border stays on the dropdown menus.
     * It makes sure the focus goes away from the combo box after the user picks something.
     */
    private void addGlobalComboBoxHandler(Scene scene) {

        // Listen for action events in the whole scene
        scene.addEventHandler(javafx.scene.control.ComboBox.ON_HIDDEN, event -> {

            // Check if the thing that was clicked is a ComboBox
            if (event.getTarget() instanceof javafx.scene.control.ComboBox) {

                // If we have a root element (background), give it focus
                if (scene.getRoot() != null) {
                    scene.getRoot().requestFocus();
                }
            }
        });
    }

    /**
     * Helper method to figure out if the clicked area is an 'interactive control' (like a button) or if it's just the background area.
     * @param target The JavaFX {@code Node} that was clicked.
     * @return {@code true} if the click was on the background, {@code false} if it was on a {@code Control}.
     */
    private boolean isClickOnBackground(Node target) {

        Node current = target;

        // Loop upwards in the visual structure from the clicked target
        while (current != null) {

            // If we find a Control (like Button, TextField, TableView), it means the user clicked on something that is not the background
            if (current instanceof Control) {
                return false;
            }

            // Move up to the next parent node
            current = current.getParent();
        }

        // If we reached the top of the hierarchy without finding a Control, it means the click was on the background
        return true;
    }

    /**
     * Loads the file path for our custom CSS file from the resources folder.
     * This CSS is used to customize the application on top of the default theme.
     */
    private void loadCustomCss() {

        try {
            // Try to find the 'styles.css' file next to this class file
            URL cssUrl = getClass().getResource("styles.css");

            // If we found it, save its path so we can add it to the scene later
            if (cssUrl != null) premiumCssPath = cssUrl.toExternalForm();

        } catch (Exception exception) {
            // If something goes wrong, print an error message
            System.err.println("CSS Fel: " + exception.getMessage());
        }
    }
}