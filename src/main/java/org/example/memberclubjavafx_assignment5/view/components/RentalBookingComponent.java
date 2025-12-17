package org.example.memberclubjavafx_assignment5.view.components;

import atlantafx.base.theme.Styles;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.BoxBlur;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import org.example.memberclubjavafx_assignment5.exceptions.ItemNotAvailableException;
import org.example.memberclubjavafx_assignment5.exceptions.ItemNotFoundException;
import org.example.memberclubjavafx_assignment5.exceptions.MemberNotFoundException;
import org.example.memberclubjavafx_assignment5.model.Item;
import org.example.memberclubjavafx_assignment5.model.Member;
import org.example.memberclubjavafx_assignment5.model.Rental;
import org.example.memberclubjavafx_assignment5.model.User;
import org.example.memberclubjavafx_assignment5.model.enums.MembershipLevel;
import org.example.memberclubjavafx_assignment5.model.enums.RentalPeriod;
import org.example.memberclubjavafx_assignment5.system.ClubSystem;
import org.example.memberclubjavafx_assignment5.view.BookingFormDialog;
import org.example.memberclubjavafx_assignment5.view.NotificationFactory;
import org.example.memberclubjavafx_assignment5.view.ReceiptDialog;
import org.example.memberclubjavafx_assignment5.view.ViewUtils;
import org.kordamp.ikonli.feather.Feather;
import org.kordamp.ikonli.javafx.FontIcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * This component handles the booking process.
 * It allows the user to create a cart, add items to it, and perform the checkout.
 */
public class RentalBookingComponent extends HBox {

    private final ClubSystem system;
    private final StackPane rootStack;
    private final User currentUser;
    private final Runnable onBookingSuccess;

    private final TableView<CartItem> cartTable;
    private final Label totalLabel;
    private final Button checkoutBtn;
    private final Button clearCartBtn;

    // The member currently selected for booking
    private Member activeMember = null;
    private final Label activeMemberLabel;

    // The 'shopping cart' list
    private final ObservableList<CartItem> cartList = FXCollections.observableArrayList();

    public RentalBookingComponent(ClubSystem system, StackPane rootStack, User currentUser, Runnable onBookingSuccess) {

        this.system = system;
        this.rootStack = rootStack;
        this.currentUser = currentUser;
        this.onBookingSuccess = onBookingSuccess;

        getStyleClass().addAll("booking-box", "card-panel-glass");
        setSpacing(20);

        // Action Box on the left side
        VBox actionBox = new VBox(15);
        actionBox.setPrefWidth(200);
        actionBox.setMinWidth(200);
        actionBox.setAlignment(Pos.TOP_LEFT);
        HBox.setHgrow(actionBox, Priority.NEVER);

        Label actionHeader = new Label("Hantera");
        actionHeader.getStyleClass().add(Styles.TITLE_4);

        Button newBookingBtn = new Button("Ny Bokning");
        newBookingBtn.setGraphic(new FontIcon(Feather.PLUS_CIRCLE));
        newBookingBtn.getStyleClass().addAll("action-btn", Styles.ACCENT);
        newBookingBtn.setMaxWidth(Double.MAX_VALUE);
        newBookingBtn.setPrefHeight(45);
        newBookingBtn.setOnAction(e -> openBookingDialog());

        Label memberTitle = new Label("Kund:");
        memberTitle.getStyleClass().add(Styles.TEXT_BOLD);

        activeMemberLabel = new Label("Ingen vald");
        activeMemberLabel.getStyleClass().add(Styles.TEXT_MUTED);
        activeMemberLabel.setWrapText(true);

        actionBox.getChildren().addAll(actionHeader, newBookingBtn, new Separator(), memberTitle, activeMemberLabel);

        // Cart box on the right side
        VBox cartBox = new VBox(10);
        HBox.setHgrow(cartBox, Priority.ALWAYS);
        cartBox.setMaxWidth(Double.MAX_VALUE);

        // Cart header
        HBox cartHeaderBox = new HBox();
        cartHeaderBox.setAlignment(Pos.CENTER_LEFT);

        Label cartHeader = new Label("Varukorg");
        cartHeader.getStyleClass().add(Styles.TEXT_BOLD);

        // Spacer to push total label to the right
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        // Initiate totalLabel
        totalLabel = new Label("Totalt: 0.00 kr");
        totalLabel.getStyleClass().addAll(Styles.TEXT_BOLD, Styles.ACCENT);

        cartHeaderBox.getChildren().addAll(cartHeader, headerSpacer, totalLabel);

        cartTable = new TableView<>();
        cartTable.setPlaceholder(new Label("Varukorgen är tom"));
        cartTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        VBox.setVgrow(cartTable, Priority.ALWAYS);
        cartTable.getStyleClass().add(Styles.STRIPED);

        setupTableColumns();

        cartTable.setItems(cartList);

        // Update total and buttons when list changes
        cartList.addListener((ListChangeListener<CartItem>) c -> {
            updateTotal();
            updateButtonStates();
        });

        // Bottom buttons
        Region spacerBottom = new Region();
        HBox.setHgrow(spacerBottom, Priority.ALWAYS);

        clearCartBtn = new Button("Rensa", new FontIcon(Feather.X));
        clearCartBtn.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
        clearCartBtn.setOnAction(e -> {
            cartList.clear();
            activeMember = null;
            activeMemberLabel.setText("Ingen vald");
            updateTotal();
        });

        checkoutBtn = new Button("Slutför", new FontIcon(Feather.CHECK));
        checkoutBtn.getStyleClass().addAll("action-btn", Styles.SUCCESS);
        checkoutBtn.setOnAction(e -> handleCheckout());

        // Set initial state
        updateButtonStates();

        HBox cartActions = new HBox(15, spacerBottom, clearCartBtn, checkoutBtn);
        cartActions.setAlignment(Pos.CENTER_RIGHT);

        cartBox.getChildren().addAll(cartHeaderBox, cartTable, cartActions);

        getChildren().addAll(actionBox, cartBox);
    }

    private void updateButtonStates() {
        boolean isEmpty = cartList.isEmpty();
        clearCartBtn.setDisable(isEmpty);
        checkoutBtn.setDisable(isEmpty);
    }

    /**
     * Opens a dialog where the user can pick an item and a member.
     */
    private void openBookingDialog() {

        if (this.getScene() == null) {
            return;
        }

        javafx.scene.Scene scene = this.getScene();
        Node appContent = scene.lookup("#app-content");

        // Apply blur effect to background
        scene.setFill(javafx.scene.paint.Color.web("#10151c"));
        BoxBlur blur = new BoxBlur(10, 10, 3);

        if (appContent != null) {
            appContent.setEffect(blur);
        }

        BookingFormDialog dialog = new BookingFormDialog(system);

        // Transparent pane to capture clicks outside
        Pane glassPane = new Pane();
        glassPane.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        glassPane.setStyle("-fx-background-color: transparent;");

        glassPane.setOnMouseClicked(e -> {
            dialog.setResult(null);
            dialog.close();
        });

        if (rootStack != null) {
            rootStack.getChildren().add(glassPane);
        }

        dialog.initModality(Modality.NONE);
        dialog.initOwner(scene.getWindow());

        // Wait for user to finish
        Optional<CartItem> result = dialog.showAndWait();

        // Remove effects
        if (appContent != null) {
            appContent.setEffect(null);
        }
        if (rootStack != null) {
            rootStack.getChildren().remove(glassPane);
        }

        // Process result
        result.ifPresent(cartItem -> {

            if (!cartItem.item.isAvailable()) {
                NotificationFactory.show("Tyvärr", "Artikeln är redan uthyrd.", NotificationFactory.Type.WARNING, rootStack);
                return;
            }

            Member selectedMember = dialog.getSelectedMember();

            if (selectedMember == null) {
                NotificationFactory.show("Fel", "Du måste välja en medlem.", NotificationFactory.Type.ERROR, rootStack);
                return;
            }

            // Check if user is switching member while cart has items
            if (activeMember != null && activeMember.getId() != selectedMember.getId()) {

                // This creates a custom modal dialog to warn the user
                Scene currentScene = rootStack.getScene();
                Node currentAppContent = currentScene.lookup("#app-content");
                if (currentAppContent != null) currentAppContent.setEffect(new BoxBlur(10, 10, 3));

                Dialog<ButtonType> alert = new Dialog<>();
                alert.initOwner(currentScene.getWindow());
                alert.initModality(Modality.NONE);
                alert.initStyle(javafx.stage.StageStyle.TRANSPARENT);

                DialogPane pane = alert.getDialogPane();
                pane.getButtonTypes().clear();
                pane.setHeader(null);
                pane.setGraphic(null);
                pane.setPadding(javafx.geometry.Insets.EMPTY);
                pane.setBackground(Background.EMPTY);
                pane.setBorder(Border.EMPTY);
                pane.getStyleClass().clear();

                VBox content = new VBox(20);
                content.getStyleClass().addAll("card-panel", "modal-box");
                content.setPadding(new javafx.geometry.Insets(30));
                content.setMaxWidth(550);

                Label header = new Label("Byta kund?");
                header.getStyleClass().add(Styles.TITLE_3);

                Label message = new Label("Du har bytt medlem. Varukorgen rensas om du fortsätter.");
                message.setWrapText(true);
                message.setStyle("-fx-font-size: 16px;");

                Button cancel = new Button("Avbryt");
                cancel.getStyleClass().addAll("action-btn", Styles.BUTTON_OUTLINED);
                cancel.setOnAction(e -> {
                    alert.setResult(ButtonType.CANCEL);
                    alert.close();
                });

                Button confirm = new Button("Fortsätt", new FontIcon(Feather.CHECK));
                confirm.getStyleClass().addAll("action-btn", Styles.ACCENT);
                confirm.setOnAction(e -> {
                    alert.setResult(ButtonType.OK);
                    alert.close();
                });

                // Group buttons
                HBox buttons = new HBox(15, cancel, confirm);
                buttons.setAlignment(Pos.CENTER);

                content.getChildren().addAll(header, message, buttons);

                // Wrap content to center it
                StackPane wrapper = new StackPane(content);
                wrapper.setBackground(Background.EMPTY);
                pane.setContent(wrapper);

                Pane glassPaneAlert = new Pane();
                glassPaneAlert.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, null, null)));

                // Close if clicking outside
                glassPaneAlert.setOnMouseClicked(e -> {
                    alert.setResult(ButtonType.CANCEL);
                    alert.close();
                });

                rootStack.getChildren().add(glassPaneAlert);

                // Setup window position
                alert.setOnShown(e -> {

                    Scene dialogScene = pane.getScene();

                    if (dialogScene != null) {

                        dialogScene.setFill(Color.TRANSPARENT);

                        // Load styles
                        try {
                            String cssPath = getClass().getResource("/org/example/memberclubjavafx_assignment5/styles.css").toExternalForm();
                            dialogScene.getStylesheets().add(cssPath);

                        } catch (Exception exception) {}

                        // Center the dialog manually
                        javafx.stage.Window window = dialogScene.getWindow();
                        javafx.stage.Window owner = alert.getOwner();

                        if (owner != null) {
                            window.setX(owner.getX() + (owner.getWidth() - window.getWidth()) / 2);
                            window.setY(owner.getY() + (owner.getHeight() - window.getHeight()) / 2);
                        }
                        pane.requestFocus();
                    }
                });

                Optional<ButtonType> alertRes = alert.showAndWait();

                if (currentAppContent != null) currentAppContent.setEffect(null);
                rootStack.getChildren().remove(glassPaneAlert);

                // Handle result
                if (alertRes.isPresent() && alertRes.get() == ButtonType.OK) {
                    cartList.clear();
                    setActiveMember(selectedMember);
                    cartList.add(cartItem);
                }

            } else {

                // Normal case (just add item)
                setActiveMember(selectedMember);
                cartList.add(cartItem);
            }
        });
    }

    /**
     * Sets the active member for the current booking session.
     * Updates the UI label with the member's details and recalculates the total price.
     * @param member The selected member, or null to clear the selection.
     */
    private void setActiveMember(Member member) {

        this.activeMember = member;

        if (member != null) {
            String fullName = member.getFirstName() + " " + member.getLastName();
            String level = ViewUtils.translate(member.getMembershipLevel());
            activeMemberLabel.setText(fullName + "\n" + level);

        } else {
            activeMemberLabel.setText("Ingen vald");
        }
        updateTotal();
    }

    /**
     * Configures the columns for the shopping cart table.
     * Defines how the item type, name, duration, and calculated price are displayed.
     */
    private void setupTableColumns() {

        TableColumn<CartItem, String> cartTypeCol = new TableColumn<>("Typ");
        cartTypeCol.setCellValueFactory(cell -> new SimpleStringProperty(ViewUtils.translate(cell.getValue().item.getItemType())));

        TableColumn<CartItem, String> cartNameCol = new TableColumn<>("Artikel");
        cartNameCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().item.getName()));

        TableColumn<CartItem, String> cartTimeCol = new TableColumn<>("Tid");
        cartTimeCol.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getTimeString()));

        TableColumn<CartItem, String> cartPriceCol = new TableColumn<>("Pris");
        cartPriceCol.getStyleClass().add("align-right");

        cartPriceCol.setCellValueFactory(cell -> {
            CartItem cartItem = cell.getValue();
            double price = (cartItem.period == RentalPeriod.HOURLY) ? cartItem.item.getPricePerHour() : cartItem.item.getPricePerDay();
            String unit = (cartItem.period == RentalPeriod.HOURLY) ? "kr/tim" : "kr/dygn";
            return new SimpleStringProperty(String.format("%.2f %s", price, unit));
        });

        cartTable.getColumns().add(cartTypeCol);
        cartTable.getColumns().add(cartNameCol);
        cartTable.getColumns().add(cartTimeCol);
        cartTable.getColumns().add(cartPriceCol);
    }

    /**
     * Calculates the total price of the cart, applying discounts based on membership level.
     */
    private void updateTotal() {

        if (cartList.isEmpty()) {
            totalLabel.setText("Totalt: 0.00 kr");
            return;
        }

        double discountFactor = 1.0;

        if (activeMember != null) {

            if (activeMember.getMembershipLevel() == MembershipLevel.STUDENT) {
                discountFactor = 0.8;
            }

            else if (activeMember.getMembershipLevel() == MembershipLevel.PREMIUM) {
                discountFactor = 0.7;
            }
        }

        double total = 0;

        for (CartItem cartItem : cartList) {
            total += (cartItem.getCalculatedPrice() * discountFactor);
        }

        String discountText = (discountFactor < 1.0) ? " (Rabatt)" : "";
        totalLabel.setText("Totalt: " + String.format("%.2f kr", total) + discountText);
    }

    /**
     * Finalizes the booking.
     * Creates rentals for all items in the cart and shows a receipt.
     */
    private void handleCheckout() {
        if (activeMember == null || cartList.isEmpty()) {
            NotificationFactory.show("Tom", "Varukorgen är tom", NotificationFactory.Type.WARNING, rootStack);
            return;
        }

        int successCount = 0;
        double totalCost = 0;

        List<Rental> newRentals = new ArrayList<>();

        // Loop through the cart and try to rent each item
        for (CartItem cartItem : cartList) {

            try {
                // Try to perform the rental. This might throw exceptions if something is wrong.
                Rental rental = system.getRentalService().rentItem(activeMember.getId(), cartItem.item.getId(), cartItem.duration, cartItem.period);

                if (rental != null) {
                    successCount++;
                    totalCost += rental.getTotalCost();
                    newRentals.add(rental);
                }

            } catch (MemberNotFoundException | ItemNotFoundException exception) {
                // If member or item is missing (data error)
                NotificationFactory.show("Hittades inte", exception.getMessage(), NotificationFactory.Type.WARNING, rootStack);

            } catch (ItemNotAvailableException e) {
                // If the item is already rented or broken
                NotificationFactory.show("Ej tillgänglig", e.getMessage(), NotificationFactory.Type.ERROR, rootStack);

            } catch (Exception exception) {
                // Catch any other unexpected errors
                exception.printStackTrace();
                NotificationFactory.show("Fel", "Ett oväntat fel inträffade vid bokning.", NotificationFactory.Type.ERROR, rootStack);
            }
        }

        if (successCount > 0) {

            // Get scene to apply blur effect
            Scene scene = rootStack.getScene();
            Node appContent = null;

            if (scene != null) {
                appContent = scene.lookup("#app-content");

                if (appContent != null) {
                    appContent.setEffect(new BoxBlur(10, 10, 3));
                }
            }

            // Add glassPane to block clicks
            Pane glassPane = new Pane();
            glassPane.setStyle("-fx-background-color: transparent;");

            if (rootStack != null) {
                rootStack.getChildren().add(glassPane);
            }

            ReceiptDialog dialog = new ReceiptDialog(activeMember, newRentals, totalCost, system, currentUser);

            // Set owner to center correctly
            if (scene != null) {
                dialog.initOwner(scene.getWindow());
            }

            dialog.showAndWait();

            // Remove effects
            if (appContent != null) {
                appContent.setEffect(null);
            }

            if (rootStack != null) {
                rootStack.getChildren().remove(glassPane);
            }

            cartList.clear();
            activeMember = null;
            activeMemberLabel.setText("Ingen vald");
            system.saveAll();

            NotificationFactory.show("Klart", successCount + " artiklar uthyrda", NotificationFactory.Type.SUCCESS, rootStack);

            if (onBookingSuccess != null) {
                onBookingSuccess.run();
            }
        }
    }

    /**
     * Helper class to represent an item in the shopping cart.
     */
    public static class CartItem {
        public Item item;
        public int duration;
        public RentalPeriod period;

        public CartItem(Item item, int duration, RentalPeriod period) {
            this.item = item;
            this.duration = duration;
            this.period = period;
        }

        public double getCalculatedPrice() {

            if (period == RentalPeriod.HOURLY) {
                return item.getPricePerHour() * duration;
            }

            else {
                return item.getPricePerDay() * duration;
            }
        }

        public String getTimeString() {
            return duration + (period == RentalPeriod.HOURLY ? " timmar" : " dygn");
        }
    }
}