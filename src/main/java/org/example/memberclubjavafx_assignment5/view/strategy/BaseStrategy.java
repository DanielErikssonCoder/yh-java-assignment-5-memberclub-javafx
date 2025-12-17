package org.example.memberclubjavafx_assignment5.view.strategy;

import atlantafx.base.theme.Styles;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import org.example.memberclubjavafx_assignment5.view.ViewUtils;

/**
 * BaseStrategy is a helper class that helps other strategy-classes to create forms.
 * It handles the grid layout so we don't have to repeat our code.
 */
public abstract class BaseStrategy implements ItemFormStrategy {

    // The grid where we place all the fields
    private GridPane activeGrid;

    // These track where to put the next item
    private int currentRow = 0;
    private int currentCol = 0;

    protected static final double FIELD_WIDTH = 160.0;

    /**
     * Starts the grid system.
     * This must be called before adding any fields.
     * @param container The layout pane where the grid will be placed.
     */
    protected void startGrid(Pane container) {

        this.activeGrid = new GridPane();

        // Space between columns and rows
        this.activeGrid.setHgap(15);
        this.activeGrid.setVgap(12);

        // Create two columns that take up 40% width each
        ColumnConstraints col1 = new ColumnConstraints();
        col1.setPercentWidth(40);
        ColumnConstraints col2 = new ColumnConstraints();
        col2.setPercentWidth(40);

        this.activeGrid.getColumnConstraints().addAll(col1, col2);
        this.activeGrid.setMaxWidth(Double.MAX_VALUE);

        // Reset the position counters
        this.currentRow = 0;
        this.currentCol = 0;

        // Add the grid to the container
        container.getChildren().add(this.activeGrid);
    }

    /**
     * Adds a label and a field to the grid.
     * It automatically finds the next empty spot.
     * @param labelText The text to show above the input field.
     * @param control   The input field itself.
     */
    protected void addField(String labelText, Control control) {

        // Make sure we have started the grid
        if (this.activeGrid == null) {
            throw new IllegalStateException("Du måste anropa startGrid (container) först!");
        }

        // Create the label text
        Label label = new Label(labelText);
        label.getStyleClass().addAll(Styles.TEXT_BOLD, "text-muted");
        label.setStyle("-fx-font-size: 11px;");

        // Set the width of the field
        control.setPrefWidth(FIELD_WIDTH);
        control.setMaxWidth(Double.MAX_VALUE);

        // Stack the label and the field in a box
        VBox box = new VBox(2, label, control);
        box.setMaxWidth(Double.MAX_VALUE);

        // Add to the grid at the current position
        this.activeGrid.add(box, currentCol, currentRow);

        // Move to the next column
        currentCol++;

        // If we pass the second column, move down to the next row
        if (currentCol > 1) {
            currentCol = 0;
            currentRow++;
        }
    }

    /**
     * Adds a checkbox to the grid.
     * Uses an empty label to line it up correctly with other fields.
     * @param checkBox The CheckBox to add to the grid.
     */
    protected void addCheckBox(CheckBox checkBox) {

        // Empty label for alignment
        Label spacer = new Label(" ");
        spacer.setStyle("-fx-font-size: 11px;");

        VBox box = new VBox(2, spacer, checkBox);
        box.setMaxWidth(Double.MAX_VALUE);

        this.activeGrid.add(box, currentCol, currentRow);

        // Update grid position
        currentCol++;
        if (currentCol > 1) {
            currentCol = 0;
            currentRow++;
        }
    }

    /**
     * Helper method to create a text field with placeholder text.
     * @param prompt The placeholder text shown when the field is empty.
     * @return A new TextField configured with the prompt.
     */
    protected TextField createTextField(String prompt) {
        TextField textField = new TextField();
        textField.setPromptText(prompt);
        textField.setPrefWidth(FIELD_WIDTH);
        return textField;
    }

    /**
     * Helper method to create a dropdown menu (ComboBox).
     * @param items An array of items to show in the list.
     * @param <T>   The type of items (generic).
     * @return A new ComboBox populated with the items.
     */
    protected <T> ComboBox<T> createComboBox(T[] items) {
        ComboBox<T> comboBox = new ComboBox<>();
        comboBox.getItems().setAll(items);

        // Shows the Swedish name for the options
        comboBox.setConverter(ViewUtils.createTranslator());
        comboBox.setPrefWidth(FIELD_WIDTH);
        return comboBox;
    }

    /**
     * Helper method to create a checkbox.
     * @param text The text displayed next to the checkbox.
     * @return A new CheckBox.
     */
    protected CheckBox createCheckBox(String text) {
        CheckBox checkBox = new CheckBox(text);
        checkBox.setPrefHeight(40);
        checkBox.setPrefWidth(FIELD_WIDTH);
        checkBox.setWrapText(true);
        return checkBox;
    }

    /**
     * Tries to read an integer from a text field.
     * @param textField The TextField to read from.
     * @return The number as an int.
     * @throws IllegalArgumentException If the text is not a valid whole number.
     */
    protected int getInt(TextField textField) {
        try {
            return Integer.parseInt(textField.getText().trim());

        } catch (NumberFormatException e) {

            // Error shown to user
            throw new IllegalArgumentException("Värdet måste vara ett heltal.");
        }
    }

    /**
     * Tries to read a double (decimal number) from a text field.
     * @param textField The TextField to read from.
     * @return The number as a double.
     * @throws IllegalArgumentException If the text is not a valid number.
     */
    protected double getDouble(TextField textField) {
        try {
            return Double.parseDouble(textField.getText().trim());

        } catch (NumberFormatException exception) {

            // Error shown to user
            throw new IllegalArgumentException("Värdet måste vara ett nummer.");
        }
    }
}