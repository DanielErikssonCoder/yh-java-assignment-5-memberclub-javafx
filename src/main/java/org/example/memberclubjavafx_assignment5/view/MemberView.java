package org.example.memberclubjavafx_assignment5.view;

import atlantafx.base.controls.CustomTextField;
import atlantafx.base.theme.Styles;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;
import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.effect.BoxBlur;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * This class builds the main user interface for managing members.
 * It provides a table view of all members, a search filter, and a form
 * for creating, updating, or deleting member records.
 */
public class MemberView {

    // Variables to access the system logic and show popups/notifications
    private final ClubSystem system;
    private final StackPane rootStack;

    // The table that lists all members
    private TableView<Member> memberTable;
    private Label memberCountLabel;

    // Input fields for the form
    private CustomTextField firstNameField;
    private CustomTextField lastNameField;
    private CustomTextField emailField;
    private CustomTextField phoneField;
    private ComboBox<MembershipLevel> levelBox;

    // Buttons and logic variables
    private Member currentEditingMember = null;
    private Button saveBtn;
    private Button deleteBtn;
    private Button clearBtn;

    // Search functionality variables
    private TextField searchField;
    private FilteredList<Member> filteredMembers;

    /**
     * Creates a new MemberView.
     * @param system The central system logic manager.
     * @param rootStack The root pane for showing overlays and notifications.
     */
    public MemberView(ClubSystem system, StackPane rootStack) {
        this.system = system;
        this.rootStack = rootStack;
    }

    /**
     * Builds and returns the complete Member Management view.
     * @return The main {@code Parent} node of the view, wrapped in a main panel.
     */
    public Parent getView() {

        // Main vertical layout
        VBox layout = new VBox(20);

        // Header Section
        HBox headerBox = new HBox(20);
        headerBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Medlemmar");
        titleLabel.getStyleClass().add("page-title");
        titleLabel.setPadding(new Insets(0));

        // Spacer to push search field to the right
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Setup the search field
        searchField = new TextField();
        searchField.setPromptText("Sök medlem");
        searchField.setPrefWidth(300);
        searchField.setPrefHeight(35);
        searchField.setMinHeight(35);
        searchField.setMaxHeight(35);

        // Update list when typing in the search field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterTable());

        headerBox.getChildren().addAll(titleLabel, headerSpacer, searchField);

        // Create the card for the member list
        VBox tableCard = new VBox(10);
        tableCard.getStyleClass().add("card-glass");
        tableCard.setPadding(new Insets(20));

        // Make the card grow to fill space (Priority.ALWAYS) so it takes up available height
        VBox.setVgrow(tableCard, Priority.ALWAYS);

        // Header inside the card
        HBox cardHeader = new HBox(15);
        cardHeader.setAlignment(Pos.CENTER_LEFT); // Left aligned so title starts at left

        Label listTitle = new Label("Register");
        listTitle.getStyleClass().add(Styles.TITLE_4);

        // Spacer to push the tools (count & reload) to the right
        Region listSpacer = new Region();
        HBox.setHgrow(listSpacer, Priority.ALWAYS);

        memberCountLabel = new Label("Antal: 0");
        memberCountLabel.getStyleClass().add(Styles.TEXT_BOLD);

        Button reloadBtn = new Button("");
        reloadBtn.setGraphic(new FontIcon(Feather.REFRESH_CW));
        reloadBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        reloadBtn.setTooltip(new Tooltip("Ladda om data"));
        reloadBtn.setOnAction(e -> {
            system.loadData();
            refreshTable();
            NotificationFactory.show("Data inläst", "Medlemmar har laddats från fil", NotificationFactory.Type.INFO, rootStack);
        });

        // Add everything to the header: Title -> Spacer -> Count -> Reload
        cardHeader.getChildren().addAll(listTitle, listSpacer, memberCountLabel, reloadBtn);

        // Setup the table
        memberTable = new TableView<>();
        memberTable.setMaxHeight(Double.MAX_VALUE);
        memberTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(memberTable, Priority.ALWAYS);

        // Create table columns
        TableColumn<Member, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        idColumn.setMaxWidth(60);

        TableColumn<Member, String> firstNameColumn = new TableColumn<>("Förnamn");
        firstNameColumn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        firstNameColumn.setMaxWidth(100);

        TableColumn<Member, String> lastNameColumn = new TableColumn<>("Efternamn");
        lastNameColumn.setCellValueFactory(new PropertyValueFactory<>("lastName"));

        TableColumn<Member, String> emailColumn = new TableColumn<>("E-post");
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        TableColumn<Member, String> phoneColumn = new TableColumn<>("Telefon");
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // Special column for membership level (shows a colored badge)
        TableColumn<Member, MembershipLevel> levelColumn = new TableColumn<>("Nivå");
        levelColumn.setCellValueFactory(new PropertyValueFactory<>("membershipLevel"));
        levelColumn.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(MembershipLevel level, boolean empty) {
                super.updateItem(level, empty);

                if (empty || level == null) {
                    setGraphic(null); setText(null);

                } else {

                    String levelText = switch (level) {
                        case PREMIUM -> "Premium";
                        case STUDENT -> "Student";
                        case STANDARD -> "Standard";
                    };

                    Label badge = new Label(levelText);
                    badge.getStyleClass().add("badge");

                    switch (level) {
                        case PREMIUM -> badge.getStyleClass().add("badge-warning");
                        case STUDENT -> badge.getStyleClass().add("badge-info");
                        case STANDARD -> badge.getStyleClass().add("badge-success");
                    }

                    setGraphic(badge);
                    setText(null);
                    setAlignment(Pos.CENTER_LEFT);
                }
            }
        });

        memberTable.getColumns().add(idColumn);
        memberTable.getColumns().add(firstNameColumn);
        memberTable.getColumns().add(lastNameColumn);
        memberTable.getColumns().add(emailColumn);
        memberTable.getColumns().add(phoneColumn);
        memberTable.getColumns().add(levelColumn);

        memberTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {

            if (selected != null) {
                populateForm(selected);
                deleteBtn.setVisible(true);
                deleteBtn.setManaged(true);
            } else {
                deleteBtn.setVisible(false);
                deleteBtn.setManaged(false);
            }
        });

        tableCard.getChildren().addAll(cardHeader, memberTable);

        // Create the card for the form
        VBox formContainer = new VBox(20);
        formContainer.getStyleClass().add("card-glass");
        formContainer.setPadding(new Insets(20));

        VBox.setVgrow(formContainer, Priority.NEVER);

        // Wrapper to limit the width
        VBox formWrapper = new VBox(15);
        formWrapper.setMaxWidth(650);

        // Form Title
        HBox formHeader = new HBox();
        formHeader.setAlignment(Pos.CENTER_LEFT);

        Label formTitle = new Label("Hantera Medlem");
        formTitle.getStyleClass().add(Styles.TITLE_4);

        formHeader.getChildren().add(formTitle);

        // Form body
        HBox formBody = new HBox(15);
        formBody.setAlignment(Pos.TOP_LEFT);

        // Left side, The four text fields in a VBox
        VBox fieldsContainer = new VBox(10);
        HBox.setHgrow(fieldsContainer, Priority.ALWAYS);

        // First Name and Last Name
        HBox nameRow = new HBox(10);
        firstNameField = new CustomTextField();
        firstNameField.setPromptText("Förnamn");
        firstNameField.setLeft(new FontIcon(Feather.USER));
        HBox.setHgrow(firstNameField, Priority.ALWAYS);

        lastNameField = new CustomTextField();
        lastNameField.setPromptText("Efternamn");
        lastNameField.setLeft(new FontIcon(Feather.USER));
        HBox.setHgrow(lastNameField, Priority.ALWAYS);

        nameRow.getChildren().addAll(firstNameField, lastNameField);

        // Email and Phone
        HBox contactRow = new HBox(10);
        emailField = new CustomTextField();
        emailField.setPromptText("E-postadress");
        emailField.setLeft(new FontIcon(Feather.MAIL));
        HBox.setHgrow(emailField, Priority.ALWAYS);

        phoneField = new CustomTextField();
        phoneField.setPromptText("Telefon");
        phoneField.setLeft(new FontIcon(Feather.PHONE));
        HBox.setHgrow(phoneField, Priority.ALWAYS);

        contactRow.getChildren().addAll(emailField, phoneField);

        // Add rows to the fields container
        fieldsContainer.getChildren().addAll(nameRow, contactRow);

        // Right side, Level Box
        VBox levelContainer = new VBox();
        levelBox = new ComboBox<>();
        levelBox.setPromptText("Välj medlemsnivå");
        levelBox.setPrefWidth(200);
        levelBox.getItems().setAll(MembershipLevel.values());

        levelBox.setConverter(new StringConverter<>() {
            @Override public String toString(MembershipLevel level) {

                if (level == null) {
                    return null;
                }
                return switch (level) {
                    case STANDARD -> "Standard";
                    case STUDENT -> "Student";
                    case PREMIUM -> "Premium";
                };
            }

            @Override public MembershipLevel fromString(String s) {
                return null;
            }
        });

        levelContainer.getChildren().add(levelBox);

        // Add both parts to the body
        formBody.getChildren().addAll(fieldsContainer, levelContainer);

        // Buttons
        HBox buttonBox = new HBox(15);
        buttonBox.setAlignment(Pos.CENTER_LEFT);
        buttonBox.setPadding(new Insets(10, 0, 0, 0));

        saveBtn = new Button("Spara");
        saveBtn.setGraphic(new FontIcon(Feather.USER_PLUS));
        saveBtn.getStyleClass().addAll("action-btn", Styles.SUCCESS);
        saveBtn.setOnAction(e -> handleSave());

        clearBtn = new Button("Rensa");
        clearBtn.setGraphic(new FontIcon(Feather.X));
        clearBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        clearBtn.setOnAction(e -> clearForm());

        deleteBtn = new Button("Ta bort");
        deleteBtn.setGraphic(new FontIcon(Feather.TRASH));
        deleteBtn.getStyleClass().addAll("action-btn", "danger-icon-only");
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        deleteBtn.setOnAction(e -> handleDelete());

        buttonBox.getChildren().addAll(saveBtn, clearBtn, deleteBtn);

        // Assemble the form wrapper
        formWrapper.getChildren().addAll(formHeader, formBody, buttonBox);
        formContainer.getChildren().add(formWrapper);

        // Listeners
        ChangeListener<Object> validationListener = (obs, oldVal, newVal) -> updateButtonStates();
        firstNameField.textProperty().addListener(validationListener);
        lastNameField.textProperty().addListener(validationListener);
        emailField.textProperty().addListener(validationListener);
        phoneField.textProperty().addListener(validationListener);
        levelBox.valueProperty().addListener(validationListener);

        updateButtonStates();

        layout.getChildren().addAll(headerBox, tableCard, formContainer);

        refreshTable();

        return ViewUtils.wrapInMainPanel(layout);
    }

    /**
     * Checks the state of all input fields and updates the enabled/disabled
     * status of the Save and Clear buttons.
     */
    private void updateButtonStates() {
        boolean isFirstNameEmpty = firstNameField.getText().trim().isEmpty();
        boolean isLastNameEmpty = lastNameField.getText().trim().isEmpty();
        boolean isEmailEmpty = emailField.getText().trim().isEmpty();
        boolean isPhoneEmpty = phoneField.getText().trim().isEmpty();
        boolean isLevelEmpty = levelBox.getValue() == null;

        boolean isSaveDisabled = isFirstNameEmpty || isLastNameEmpty || isEmailEmpty || isPhoneEmpty || isLevelEmpty;
        saveBtn.setDisable(isSaveDisabled);

        boolean isClearDisabled = isFirstNameEmpty && isLastNameEmpty && isEmailEmpty && isPhoneEmpty && isLevelEmpty;
        clearBtn.setDisable(isClearDisabled);
    }

    /**
     * Handles the logic for creating a new member or updating the currently selected member.
     * Uses the system's service layer for validation and persistence.
     */
    private void handleSave() {
        try {
            String fName = firstNameField.getText().trim();
            String lName = lastNameField.getText().trim();
            String email = emailField.getText().trim();
            String phone = phoneField.getText().trim();
            MembershipLevel level = levelBox.getValue();

            if (currentEditingMember == null) {
                system.getMembershipService().addMember(fName, lName, phone, email, level);
                NotificationFactory.show("Sparat", fName + " har lagts till", NotificationFactory.Type.SUCCESS, rootStack);
            } else {
                system.getMembershipService().updateMemberDetails(currentEditingMember, fName, lName, phone, email, level);
                system.saveAll();
                NotificationFactory.show("Uppdaterad", "Ändringar sparades", NotificationFactory.Type.SUCCESS, rootStack);
            }

            refreshTable();
            clearForm();

        } catch (IllegalArgumentException exception) {
            NotificationFactory.show("Fel i formuläret", exception.getMessage(), NotificationFactory.Type.ERROR, rootStack);
        }
    }

    /**
     * Handles the process of deleting the selected member, first showing a custom confirmation dialog.
     */
    private void handleDelete() {

        Member selected = memberTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            return;
        }

        Scene scene = rootStack.getScene();

        if (scene == null) {
            return;
        }

        Node appContent = scene.lookup("#app-content");
        BoxBlur blur = new BoxBlur(10, 10, 3);

        if (appContent != null) {
            appContent.setEffect(blur);
        }

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

        VBox content = new VBox(20);
        content.getStyleClass().addAll("card-panel", "modal-box");
        content.setPadding(new Insets(30));
        content.setMaxWidth(550);

        Label header = new Label("Ta bort medlem");
        header.getStyleClass().add(Styles.TITLE_3);

        Label message = new Label("Är du säker på att du vill ta bort " + selected.getFirstName() + " " + selected.getLastName() + "?");
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

        Pane glassPane = new Pane();
        glassPane.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));
        glassPane.setOnMouseClicked(e -> {
            dialog.setResult(ButtonType.CANCEL);
            dialog.close();
        });

        rootStack.getChildren().add(glassPane);

        dialog.setOnShown(e -> {

            Scene dialogScene = pane.getScene();

            if (dialogScene != null) {
                dialogScene.setFill(Color.TRANSPARENT);

                try {
                    String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                    dialogScene.getStylesheets().add(cssPath);

                } catch (Exception exception) {
                    System.err.println("Kunde inte ladda CSS: " + exception.getMessage());
                }

                Window window = dialogScene.getWindow();
                Window owner = dialog.getOwner();

                if (owner != null) {
                    window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                    window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                }
                pane.requestFocus();
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();

        if (appContent != null) {
            appContent.setEffect(null);
        }

        rootStack.getChildren().remove(glassPane);

        if (result.isPresent() && result.get() == ButtonType.OK) {

            if (system.getMembershipService().removeMember(selected.getId())) {
                refreshTable();
                clearForm();
                NotificationFactory.show("Borttagen", "Medlem raderades", NotificationFactory.Type.SUCCESS, rootStack);

            } else {
                NotificationFactory.show("Fel", "Kunde inte ta bort medlem", NotificationFactory.Type.ERROR, rootStack);
            }
        }
    }

    /**
     * Fills the form fields with the details of the selected member from the table.
     * Also updates the Save button to become an 'Update' button.
     */
    private void populateForm(Member member) {
        currentEditingMember = member;
        firstNameField.setText(member.getFirstName());
        lastNameField.setText(member.getLastName());
        emailField.setText(member.getEmail());
        phoneField.setText(member.getPhone());
        levelBox.getSelectionModel().select(member.getMembershipLevel());

        saveBtn.setText("Uppdatera");
        saveBtn.setGraphic(new FontIcon(Feather.SAVE));
        saveBtn.getStyleClass().removeAll(Styles.SUCCESS);
        saveBtn.getStyleClass().add("accent");
        updateButtonStates();
    }

    /**
     * Resets the form to its initial state.
     */
    private void clearForm() {
        currentEditingMember = null;
        firstNameField.clear();
        lastNameField.clear();
        emailField.clear();
        phoneField.clear();
        levelBox.getSelectionModel().clearSelection();
        memberTable.getSelectionModel().clearSelection();

        saveBtn.setText("Spara");
        saveBtn.setGraphic(new FontIcon(Feather.USER_PLUS));
        saveBtn.getStyleClass().removeAll("accent");
        saveBtn.getStyleClass().add(Styles.SUCCESS);
        deleteBtn.setVisible(false);
        deleteBtn.setManaged(false);
        updateButtonStates();
    }

    /**
     * Loads the latest list of members from the system and updates the table.
     */
    private void refreshTable() {
        ObservableList<Member> masterList = FXCollections.observableArrayList(system.getMemberRegistry().getAllMembers());
        filteredMembers = new FilteredList<>(masterList, p -> true);
        memberTable.setItems(filteredMembers);
        memberTable.refresh();
        memberTable.getSelectionModel().clearSelection();
        filterTable();
    }

    /**
     * Filters the member table based on the text entered in the search field.
     */
    private void filterTable() {

        if (filteredMembers == null) {
            return;
        }

        String filter = (searchField.getText() == null) ? "" : searchField.getText().toLowerCase();

        filteredMembers.setPredicate(member -> {

            if (filter.isEmpty()) {
                return true;
            }
            return member.getFirstName().toLowerCase().contains(filter) ||
                    member.getLastName().toLowerCase().contains(filter) ||
                    member.getEmail().toLowerCase().contains(filter);
        });

        if (memberCountLabel != null) {
            memberCountLabel.setText("Antal: " + filteredMembers.size());
        }
    }
}