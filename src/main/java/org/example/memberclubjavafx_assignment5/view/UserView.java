package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.controls.PasswordTextField;
import atlantafx.base.theme.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.Optional;

/**
 * This class builds the user interface for managing staff members (Users).
 * It allows to view a list of all users, and to create or remove them.
 */
public class UserView {

    // The main system that holds all the data and logic
    private final ClubSystem system;

    // The top-level container for the entire application, used for showing popups and notifications
    private final StackPane rootStack;

    // Table and action buttons
    private TableView<User> userTable;
    private Button removeButton;
    private Button addButton;

    // Input fields for creating users
    private CustomTextField usernameField;
    private PasswordTextField passwordField;
    private CustomTextField firstNameField;
    private CustomTextField lastNameField;

    /**
     * Creates a new UserView.
     * @param system The central system logic manager.
     * @param rootStack The root pane for showing overlays.
     */
    public UserView(ClubSystem system, StackPane rootStack) {
        this.system = system;
        this.rootStack = rootStack;
    }

    /**
     * Builds and returns the entire User Management view, including the table and the input form.
     * @return The main {@code Parent} node of the view, wrapped in a main panel.
     */
    public Parent getView() {

        VBox layout = new VBox(20);

        // Header
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Personal");
        titleLabel.getStyleClass().add("page-title");

        headerBox.getChildren().add(titleLabel);

        // Table Panel
        VBox tablePanel = new VBox(10);
        tablePanel.getStyleClass().add("card-glass");
        tablePanel.setPadding(new Insets(20));
        VBox.setVgrow(tablePanel, Priority.ALWAYS);

        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT);

        Label listTitle = new Label("Översikt");
        listTitle.getStyleClass().add(Styles.TITLE_4);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button reloadBtn = new Button("", new FontIcon(Feather.REFRESH_CW));
        reloadBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        reloadBtn.setTooltip(new Tooltip("Ladda om data"));
        reloadBtn.setOnAction(e -> {
            system.loadData();
            refreshTable();
            NotificationFactory.show(
                    "Uppdaterad",
                    "Användare har laddats från fil",
                    NotificationFactory.Type.INFO,
                    rootStack
            );
        });

        cardHeader.getChildren().addAll(listTitle, spacer, reloadBtn);

        userTable = new TableView<>();
        userTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(userTable, Priority.ALWAYS);

        TableColumn<User, String> usernameColumn = new TableColumn<>("Användarnamn");
        usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));

        TableColumn<User, String> firstNameColumn = new TableColumn<>("Förnamn");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));

        TableColumn<User, String> lastNameColumn = new TableColumn<>("Efternamn");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<User, String> passwordColumn = new TableColumn<>("Lösenord");
        passwordColumn.setCellValueFactory(cd -> new SimpleStringProperty("********"));

        userTable.getColumns().add(usernameColumn);
        userTable.getColumns().add(firstNameColumn);
        userTable.getColumns().add(lastNameColumn);
        userTable.getColumns().add(passwordColumn);

        // Show or hide the remove button based on whether a user is selected in the table
        userTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            boolean hasSelection = newVal != null;
            removeButton.setVisible(hasSelection);
            removeButton.setManaged(hasSelection);
        });

        refreshTable();

        tablePanel.getChildren().addAll(cardHeader, userTable);

        // Form Panel
        VBox formPanel = new VBox();
        formPanel.getStyleClass().add("card-glass");
        formPanel.setPadding(new Insets(20));

        VBox formWrapper = new VBox(15);
        formWrapper.setMaxWidth(500);

        Label formTitle = new Label("Registrera Personal");
        formTitle.getStyleClass().add(Styles.TITLE_4);

        HBox credentialsBox = new HBox(10);

        usernameField = new CustomTextField();
        usernameField.setPromptText("Användarnamn");
        usernameField.setLeft(new FontIcon(Feather.USER));
        HBox.setHgrow(usernameField, Priority.ALWAYS);

        passwordField = new PasswordTextField();
        passwordField.setPromptText("Lösenord");
        passwordField.setLeft(new FontIcon(Feather.LOCK));
        HBox.setHgrow(passwordField, Priority.ALWAYS);

        credentialsBox.getChildren().addAll(usernameField, passwordField);

        HBox nameBox = new HBox(10);

        firstNameField = new CustomTextField();
        firstNameField.setPromptText("Förnamn");
        firstNameField.setLeft(new FontIcon(Feather.TAG));
        HBox.setHgrow(firstNameField, Priority.ALWAYS);

        lastNameField = new CustomTextField();
        lastNameField.setPromptText("Efternamn");
        lastNameField.setLeft(new FontIcon(Feather.TAG));
        HBox.setHgrow(lastNameField, Priority.ALWAYS);

        nameBox.getChildren().addAll(firstNameField, lastNameField);

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        addButton = new Button("Lägg till", new FontIcon(Feather.USER_PLUS));
        addButton.getStyleClass().addAll("action-btn", Styles.SUCCESS);
        addButton.setDisable(true);
        addButton.setOnAction(e -> handleAddUser());

        removeButton = new Button("Ta bort", new FontIcon(Feather.TRASH));
        removeButton.getStyleClass().addAll("action-btn", "danger-icon-only");
        removeButton.setVisible(false);
        removeButton.setManaged(false);
        removeButton.setOnAction(e -> handleRemoveUser());

        buttonBox.getChildren().addAll(addButton, removeButton);

        formWrapper.getChildren().addAll(
                formTitle,
                credentialsBox,
                nameBox,
                buttonBox
        );

        formPanel.getChildren().add(formWrapper);

        setupFieldListeners();

        layout.getChildren().addAll(headerBox, tablePanel, formPanel);

        // Use the utility method to wrap the view in the main panel style
        return ViewUtils.wrapInMainPanel(layout);
    }

    /**
     * Sets up listeners for the input fields to check if the 'Add User' button should be enabled.
     */
    private void setupFieldListeners() {
        var listener = (javafx.beans.value.ChangeListener<String>) (obs, o, n) -> checkFields();
        usernameField.textProperty().addListener(listener);
        passwordField.textProperty().addListener(listener);
        firstNameField.textProperty().addListener(listener);
        lastNameField.textProperty().addListener(listener);
    }

    /**
     * Checks if all required form fields are non-empty and enables/disables the add button.
     */
    private void checkFields() {

        boolean filled = !usernameField.getText().isBlank() && !passwordField.getText().isBlank() &&
                !firstNameField.getText().isBlank() && !lastNameField.getText().isBlank();
        addButton.setDisable(!filled);
    }

    /**
     * Reloads the list of users from the system and updates the table view.
     */
    private void refreshTable() {
        userTable.setItems(FXCollections.observableArrayList(system.getAllUsers()));
        userTable.getSelectionModel().clearSelection();
    }

    /**
     * Handles the logic for creating a new user based on the input form data.
     * Shows a notification on success or failure.
     */
    private void handleAddUser() {
        boolean success = system.createUser(
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                firstNameField.getText().trim(),
                lastNameField.getText().trim()
        );

        if (success) {
            NotificationFactory.show("Lyckades", "Ny användare skapad", NotificationFactory.Type.SUCCESS, rootStack);
            clearForm();
            refreshTable();
        } else {
            NotificationFactory.show("Fel", "Användarnamnet existerar redan", NotificationFactory.Type.ERROR, rootStack);
        }
    }

    /**
     * Handles the process of removing a selected user.
     * It first shows a custom confirmation dialog to the user.
     */
    private void handleRemoveUser() {

        User selected = userTable.getSelectionModel().getSelectedItem();

        // Stop if no user is selected
        if (selected == null) {
            return;
        }

        Scene scene = rootStack.getScene();

        // Stop if the scene is not available
        if (scene == null) {
            return;
        }

        // Apply a blur effect to the background content to focus on the dialog
        Node appContent = scene.lookup("#app-content");
        BoxBlur blur = new BoxBlur(10, 10, 3);
        if (appContent != null) appContent.setEffect(blur);

        // Create the custom dialog structure
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(scene.getWindow());
        dialog.initModality(Modality.NONE);
        dialog.initStyle(StageStyle.TRANSPARENT);

        DialogPane pane = dialog.getDialogPane();
        pane.getButtonTypes().clear();
        pane.setHeader(null);
        pane.setGraphic(null);
        pane.setPadding(Insets.EMPTY);
        pane.setBackground(Background.EMPTY);
        pane.setBorder(Border.EMPTY);
        pane.getStyleClass().clear();

        // Build the content inside the dialog
        VBox content = new VBox(20);
        content.getStyleClass().addAll("card-panel", "modal-box");

        // Set custom styling for the dialog box
        content.setPadding(new Insets(30));
        content.setMaxWidth(550);

        Label header = new Label("Ta bort användare");
        header.getStyleClass().add(Styles.TITLE_3);

        Label message = new Label("Är du säker på att du vill ta bort " + selected.getFullName() + "?");
        message.setWrapText(true);
        message.setStyle("-fx-font-size: 16px;");

        Button cancel = new Button("Avbryt");
        cancel.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        cancel.setOnAction(e -> {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        });

        Button confirm = new Button("Ta bort", new FontIcon(Feather.TRASH));
        confirm.getStyleClass().addAll("action-btn", "danger-icon-only");
        confirm.setOnAction(e -> {
            dialog.setResult(ButtonType.OK);
            dialog.close();
        });

        HBox buttons = new HBox(15, cancel, confirm);
        buttons.setAlignment(Pos.CENTER);

        content.getChildren().addAll(header, message, buttons);

        StackPane wrapper = new StackPane(content);
        wrapper.setBackground(Background.EMPTY);
        pane.setContent(wrapper);

        // Add a transparent pane behind the dialog to intercept clicks and close it
        Pane glassPane = new Pane();
        glassPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        glassPane.setOnMouseClicked(e -> {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        });

        rootStack.getChildren().add(glassPane);

        // Logic to center the dialog and add CSS after it is shown
        dialog.setOnShown(e -> {

            Scene dialogScene = pane.getScene();

            if (dialogScene != null) {

                dialogScene.setFill(Color.TRANSPARENT);

                try {
                    // Try to load the custom CSS file for the dialog
                    String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                    dialogScene.getStylesheets().add(cssPath);

                } catch (Exception exception) {
                    System.err.println("Kunde inte ladda CSS: " + exception.getMessage());
                }

                // Center the dialog window over the main window
                Window window = dialogScene.getWindow();
                Window owner = dialog.getOwner();

                if (owner != null) {
                    window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                    window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                }

                pane.requestFocus();
            }
        });

        // Show the dialog and wait for the user's response
        Optional<ButtonType> result = dialog.showAndWait();

        // Remove blur and the glass pane after the dialog is closed
        if (appContent != null) appContent.setEffect(null);
        rootStack.getChildren().remove(glassPane);

        // Check the result and perform the removal if confirmed
        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (system.removeUser(selected.getUsername())) {
                refreshTable();
                NotificationFactory.show("Borttagen", "Användare borttagen", NotificationFactory.Type.SUCCESS, rootStack);

            } else {
                NotificationFactory.show("Fel", "Borttagning misslyckades", NotificationFactory.Type.ERROR, rootStack);
            }
        }
    }

    /**
     * Clears all text from the user creation form fields.
     */
    private void clearForm() {
        usernameField.clear();
        passwordField.clear();
        firstNameField.clear();
        lastNameField.clear();

        // Re-check fields to disable the Add button
        checkFields();
    }
}