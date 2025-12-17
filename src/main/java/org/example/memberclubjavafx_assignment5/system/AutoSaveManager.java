package org.example.memberclubjavafx_assignment5.system;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * This class handles the automatic saving.
 * It runs a timer in the background that saves the files every minute.
 * I created this so we don't lose any data if the program crashes.
 */
public class AutoSaveManager {

    // This is the code that will run when we want to save
    private final Runnable saveTask;

    // This is the timer that schedules when the saving should happen
    private ScheduledExecutorService scheduler;

    /**
     * Constructor.
     * @param saveTask The method we want to run to save the data.
     */
    public AutoSaveManager(Runnable saveTask) {
        this.saveTask = saveTask;
    }

    /**
     * This starts the automatic saving.
     */
    public void start() {

        // If the timer is already running, we don't need to start it again
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        // We need a special thread factory to make a 'daemon' thread.
        ThreadFactory threadFactory = r -> {
            Thread thread = new Thread(r, "Autosave-Thread");

            // I set this to true so the program can close properly.
            // If it is false, the program might keep running in the background even if I close the window.
            thread.setDaemon(true);
            return thread;
        };

        // Create the scheduler with 1 thread
        this.scheduler = Executors.newSingleThreadScheduledExecutor(threadFactory);

        // Run the save task every 1 minute
        this.scheduler.scheduleAtFixedRate(() -> {

            try {
                // Run the save code
                saveTask.run();

                // Print a message to the console so we can see that it works
                System.out.println("Autosparning: Data sparad till fil.");

            } catch (Exception exception) {
                // If something goes wrong, print the error but don't crash the program
                System.err.println("Autosparning misslyckades: " + exception.getMessage());
                exception.printStackTrace();
            }

        }, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * Stops the automatic saving.
     * We call this when we close the program to clean up.
     */
    public void stop() {

        if (scheduler != null) {

            // Stop the timer immediately
            scheduler.shutdownNow();
        }
    }
}