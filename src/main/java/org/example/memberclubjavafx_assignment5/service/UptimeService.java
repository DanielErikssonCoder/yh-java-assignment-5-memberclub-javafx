package org.example.memberclubjavafx_assignment5.service;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This class tracks how long the program has been running.
 * It runs in a separate background thread so it doesn't freeze the main window.
 */
public class UptimeService implements Runnable {

    // Property that can be connected to the UI label
    private final SimpleStringProperty uptimeProperty;

    // Variables for the thread loop
    private boolean running;
    private Thread thread;

    // Start time in milliseconds
    private final long startMillis;

    /**
     * Constructor. Sets up the initial values and saves the start time.
     */
    public UptimeService() {

        uptimeProperty = new SimpleStringProperty("00:00:00");
        running = false;

        // Save the time when we created the service
        startMillis = System.currentTimeMillis();
    }

    /**
     * Starts the thread.
     */
    public void start() {

        // If it is already running, don't do anything
        if (running) {
            return;
        }

        running = true;

        // Create the thread and pass 'this' class because we implement 'Runnable'
        thread = new Thread(this);
        thread.setName("Uptime-Thread");

        // Daemon means it will die when the main program closes
        thread.setDaemon(true);

        // Start the background work
        thread.start();
    }

    /**
     * Stops the thread loop.
     */
    public void stop() {
        running = false;
    }

    /**
     * This method runs inside the new thread.
     */
    @Override
    public void run() {

        // Loop as long as 'running' is true
        while (running) {
            try {

                //Calculate elapsed time
                long currentMillis = System.currentTimeMillis();
                long difference = currentMillis - startMillis;

                // Convert to seconds
                long totalSeconds = difference / 1000;

                // Calculate hours, minutes, seconds
                long hours = totalSeconds / 3600;
                long remainder = totalSeconds % 3600;
                long minutes = remainder / 60;
                long seconds = remainder % 60;

                // Create the text string
                String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);
                String finalString = "Upptid: " + timeString;

                // Update the UI (we cannot update the UI directly from a background thread so we use 'Platform.runLater' to ask the JavaFX thread to do it
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        uptimeProperty.set(finalString);
                    }
                });

                // Sleep for 1 second
                Thread.sleep(1000);

            } catch (Exception exception) {

                // If something crashes
                exception.printStackTrace();
                running = false;
            }
        }
    }

    /**
     * Returns the property so the UI can bind a Label to it.
     */
    public StringProperty uptimeProperty() {
        return uptimeProperty;
    }
}