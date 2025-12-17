package org.example.memberclubjavafx_assignment5.view;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

/**
 * This class is a utility factory for creating and displaying animated popup
 * notification messages in the corner of the application window.
 */
public class NotificationFactory {

    /**
     * These are the different types of messages we can show, each having a unique color and icon.
     */
    public enum Type {
        SUCCESS,
        ERROR,
        INFO,
        WARNING
    }

    /**
     * Creates, animates, and displays a notification message in the top right corner of the application.
     * @param title The main title of the notification
     * @param message The detailed message text.
     * @param type The {@code Type} of notification
     * @param root The main {@code StackPane} of the application, where the notification will be added as an overlay.
     */
    public static void show(String title, String message, Type type, StackPane root) {

        // Create the title label and make it bold
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("notification-title");
        titleLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");

        // Create the message label with smaller text
        Label messsageLabel = new Label(message);
        messsageLabel.getStyleClass().add("notification-message");
        messsageLabel.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 13px;");

        // Put the text labels inside a vertical box
        VBox textBox = new VBox(4);
        textBox.getChildren().addAll(titleLabel, messsageLabel);

        // Create the main box for the notification
        HBox toast = new HBox(15);
        toast.getStyleClass().add("toast-bar");
        toast.setMaxSize(300, 80);

        // Check which type of message it is to pick the right color and icon
        if (type == Type.SUCCESS) {
            toast.getStyleClass().add("toast-success");

            FontIcon icon = new FontIcon(Feather.CHECK_CIRCLE);
            icon.getStyleClass().add("icon-success");
            icon.setIconSize(26);

            toast.getChildren().add(icon);

        } else if (type == Type.ERROR) {
            toast.getStyleClass().add("toast-error");

            FontIcon icon = new FontIcon(Feather.ALERT_CIRCLE);
            icon.getStyleClass().add("icon-error");
            icon.setIconSize(26);

            toast.getChildren().add(icon);

        } else if (type == Type.WARNING) {
            toast.getStyleClass().add("toast-warning");

            FontIcon icon = new FontIcon(Feather.ALERT_TRIANGLE);
            icon.getStyleClass().add("icon-warning");
            icon.setIconSize(26);

            toast.getChildren().add(icon);

        } else {

            // Default to INFO if nothing else matches
            FontIcon icon = new FontIcon(Feather.INFO);
            icon.getStyleClass().add("icon-info");
            icon.setIconSize(26);

            toast.getChildren().add(icon);
        }

        // Add the text box to the notification box
        toast.getChildren().add(textBox);

        // Add the notification to the screen and position it in the top right corner
        root.getChildren().add(toast);
        StackPane.setAlignment(toast, Pos.TOP_RIGHT);
        StackPane.setMargin(toast, new Insets(30, 30, 0, 0));

        // Set the starting position (outside the screen to the right)
        toast.setTranslateX(350);
        toast.setOpacity(0);

        // Animation to slide in horizontally
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(400), toast);
        slideIn.setToX(0);

        // Animation to fade in
        FadeTransition fadeIn = new FadeTransition(Duration.millis(400), toast);
        fadeIn.setToValue(1.0);

        // Pause for 4 seconds so the user can read the message
        PauseTransition wait = new PauseTransition(Duration.seconds(4));

        // Animation to slide out horizontally
        TranslateTransition slideOut = new TranslateTransition(Duration.millis(400), toast);
        slideOut.setToX(350);

        // Animation to fade out
        FadeTransition fadeOut = new FadeTransition(Duration.millis(400), toast);
        fadeOut.setToValue(0.0);

        // Create a sequence to run the animations in order: Enter, Wait, Exit
        SequentialTransition sequence = new SequentialTransition();

        // Run slide and fade in at the same time
        ParallelTransition enter = new ParallelTransition(slideIn, fadeIn);

        // Run slide and fade out at the same time
        ParallelTransition exit = new ParallelTransition(slideOut, fadeOut);

        // Add all steps to the sequence
        sequence.getChildren().addAll(enter, wait, exit);

        // When the animation is done, remove the notification from the screen
        sequence.setOnFinished(e -> {
            root.getChildren().remove(toast);
        });

        // Start the animation sequence
        sequence.play();
    }
}