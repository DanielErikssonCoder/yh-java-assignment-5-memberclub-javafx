package org.example.memberclubjavafx_assignment5.view;

import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * This helper class makes it possible for the user to resize a custom, undecorated window by dragging its edges.
 * It uses an 'Anchor' logic to make sure the opposite side of the window stays still when resizing.
 */
public class WindowResizeHandler {

    // How close the mouse needs to be to the edge to start resizing
    private static final double RESIZE_MARGIN = 7.0;

    // The main window (Stage) that we are controlling
    private final Stage stage;

    // Holds the type of cursor to show the user what they can do
    private Cursor cursorEvent = Cursor.DEFAULT;

    // Starting values when the user first clicks and starts dragging to resize
    private double startX, startY, startW, startH;

    // The starting mouse position on the entire screen for calculating the drag distance
    private double startScreenX, startScreenY;

    // These are used when resizing from the left or top edges to keep the opposite edge fixed
    private double anchorRight, anchorBottom;

    // Variables for title bar dragging (moving the window without resizing)
    private double xOffset = 0, yOffset = 0;

    /**
     * Creates a new handler for window resizing.
     * @param stage The primary stage (window) whose size and position will be controlled.
     */
    public WindowResizeHandler(Stage stage) {
        this.stage = stage;
    }

    /**
     * Attaches all the necessary mouse event handlers to the scene.
     * These handlers watch for mouse movement, clicking, and dragging
     * to enable the resizing functionality.
     * @param scene The current scene to attach the listeners to.
     */
    public void addResizeListener(Scene scene) {

        // When the mouse moves, check if we are over a resize edge
        scene.addEventHandler(MouseEvent.MOUSE_MOVED, this::handleMoved);

        // When the mouse is clicked, save the starting positions
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, this::handlePressed);

        // When the mouse is dragged, actually perform the resizing
        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, this::handleDragged);
    }

    /**
     * This method is part of an interface but is not used in this implementation.
     * The starting positions are calculated right when the user clicks down.
     */
    public void initializePositions() {
        // Not needed here, it's initialized when the mouse is pressed
    }

    /**
     * Stores the initial mouse position when the user starts dragging the title bar.
     * This helps calculate the window's new position.
     * @param event The mouse event that triggered the start of the drag.
     */
    public void startDragging(MouseEvent event) {

        // Save the mouse offset inside the window
        xOffset = event.getSceneX();
        yOffset = event.getSceneY();
    }

    /**
     * Moves the window as the user drags the mouse.
     * @param event The mouse drag event.
     */
    public void updateDragging(MouseEvent event) {

        // We can't move a window if it's maximized, so we stop here
        if (stage.isMaximized()) return;

        // Calculate the new X and Y position and set it
        stage.setX(event.getScreenX() - xOffset);
        stage.setY(event.getScreenY() - yOffset);

    }

    /**
     * Called when the title bar dragging stops.
     * (Currently, there is no cleanup code needed here).
     */
    public void stopDragging() {
        // Nothing to do here right now
    }

    /**
     * This handler checks the mouse position to see if it is near a window edge and changes the mouse cursor icon
     * accordingly to let the user know they can resize.
     * @param event The mouse movement event.
     */
    private void handleMoved(MouseEvent event) {

        // If the window is maximized, resizing is disabled
        if (stage.isMaximized()) {

            // Restore cursor if maximized
            if (stage.getScene().getCursor() != Cursor.DEFAULT) {
                stage.getScene().setCursor(Cursor.DEFAULT);
            }

            return;
        }

        Scene scene = stage.getScene();
        if (scene == null) return;

        // Get the mouse position inside the window and the window's current size
        double mouseX = event.getSceneX();
        double mouseY = event.getSceneY();
        double width = scene.getWidth();
        double height = scene.getHeight();

        // Detect which resize zone the mouse is in (check for corners first)
        if (mouseX < RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
            cursorEvent = Cursor.NW_RESIZE;
        }

        else if (mouseX < RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
            cursorEvent = Cursor.SW_RESIZE;
        }

        else if (mouseX > width - RESIZE_MARGIN && mouseY < RESIZE_MARGIN) {
            cursorEvent = Cursor.NE_RESIZE;
        }

        else if (mouseX > width - RESIZE_MARGIN && mouseY > height - RESIZE_MARGIN) {
            cursorEvent = Cursor.SE_RESIZE;
        }

        // Then check for sides
        else if (mouseX < RESIZE_MARGIN) {
            cursorEvent = Cursor.W_RESIZE;
        }

        else if (mouseX > width - RESIZE_MARGIN) {
            cursorEvent = Cursor.E_RESIZE;
        }

        else if (mouseY < RESIZE_MARGIN) {
            cursorEvent = Cursor.N_RESIZE;
        }

        else if (mouseY > height - RESIZE_MARGIN) {
            cursorEvent = Cursor.S_RESIZE;
        }

        // If not near any edge, set to default cursor
        else {
            cursorEvent = Cursor.DEFAULT;
        }

        // Only change the cursor if it's different from the current one
        if (scene.getCursor() != cursorEvent) {
            scene.setCursor(cursorEvent);
        }
    }

    /**
     * This handler is called when the user presses the mouse button down.
     * It saves the window's starting position and size, which is needed to calculate
     * the new size and position when dragging starts.
     * @param event The mouse pressed event.
     */
    private void handlePressed(MouseEvent event) {

        // If maximized or not over a resize area, we do nothing
        if (stage.isMaximized() || Cursor.DEFAULT.equals(cursorEvent)) return;

        // Save the window's current position and size
        startX = stage.getX();
        startY = stage.getY();
        startW = stage.getWidth();
        startH = stage.getHeight();

        // Save the mouse position on the screen
        startScreenX = event.getScreenX();
        startScreenY = event.getScreenY();

        // Calculate the anchor points (the edges that should stay put), to minimize flickering
        anchorRight = startX + startW;
        anchorBottom = startY + startH;
    }

    /**
     * This is the main resizing logic. It's called continuously as the user drags the mouse
     * after clicking on a resize area.
     * @param event The mouse dragged event.
     */
    private void handleDragged(MouseEvent event) {

        // Stop if maximized or not currently resizing
        if (stage.isMaximized() || Cursor.DEFAULT.equals(cursorEvent)) {
            return;
        }

        // Calculate how much the mouse has moved since the click
        double deltaX = event.getScreenX() - startScreenX;
        double deltaY = event.getScreenY() - startScreenY;

        // Get the minimum allowed size for the window
        double minWidth = Math.max(stage.getMinWidth(), 200);
        double minHeight = Math.max(stage.getMinHeight(), 200);

        // Variables to hold the calculated new window values
        double newX = startX;
        double newY = startY;
        double newW = startW;
        double newH = startH;

        // Right side resize
        if (cursorEvent == Cursor.E_RESIZE || cursorEvent == Cursor.NE_RESIZE || cursorEvent == Cursor.SE_RESIZE) {

            newW = startW + deltaX;

            // Check against the minimum width
            if (newW < minWidth) newW = minWidth;
        }

        // Left side resize
        else if (cursorEvent == Cursor.W_RESIZE || cursorEvent == Cursor.NW_RESIZE || cursorEvent == Cursor.SW_RESIZE) {

            // New width is the original width minus the drag distance
            newW = startW - deltaX;

            // Handle minimum width boundary
            if (newW < minWidth) newW = minWidth;

            // Calculate the new X position based on the anchor
            newX = anchorRight - newW;
        }

        // Bottom side resize
        if (cursorEvent == Cursor.S_RESIZE || cursorEvent == Cursor.SE_RESIZE || cursorEvent == Cursor.SW_RESIZE) {

            newH = startH + deltaY;

            if (newH < minHeight) {
                newH = minHeight;
            }
        }

        // Top side resize
        else if (cursorEvent == Cursor.N_RESIZE || cursorEvent == Cursor.NE_RESIZE || cursorEvent == Cursor.NW_RESIZE) {

            // New height is the original height minus the vertical drag distance
            newH = startH - deltaY;

            // Handle minimum height boundary
            if (newH < minHeight) newH = minHeight;

            // Calculate the new Y position based on the anchor, this ensures the bottom edge of the window remains still
            newY = anchorBottom - newH;
        }

        // Apply all the calculated changes to the window
        stage.setX(newX);
        stage.setY(newY);
        stage.setWidth(newW);
        stage.setHeight(newH);
    }
}