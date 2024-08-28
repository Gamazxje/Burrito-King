package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.java.model.OrderItem;
import main.java.model.User;
import main.java.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Optional;

public class CheckoutController {
    @FXML
    private ListView<OrderItem> orderSummaryListView;
    @FXML
    private Label totalPriceLabel;
    @FXML
    private Label creditsLabel;
    @FXML
    private TextField redeemCreditsField;
    @FXML
    private TextField cardNumberField;
    @FXML
    private TextField expiryDateField;
    @FXML
    private TextField cvvField;

    private List<OrderItem> orderItems;
    private User user;

    private UserController userController = new UserController();

    public void setUser(User user) {
        this.user = user;
        creditsLabel.setText(String.valueOf(user.getCredits()));
        System.out.println("User set in CheckoutController: " + user.getUsername());
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
        orderSummaryListView.getItems().addAll(orderItems);
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        double totalPrice = orderItems.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
        totalPriceLabel.setText(String.valueOf(totalPrice));
    }

    @FXML
private void handlePlaceOrder() {
    if (user == null) {
        showAlert("Error", "User is not set.");
        return;
    }

    String cardNumber = cardNumberField.getText();
    String expiryDate = expiryDateField.getText();
    String cvv = cvvField.getText();

    if (validatePaymentDetails(cardNumber, expiryDate, cvv)) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Order Placement Time");
        dialog.setHeaderText("Specify Fake Order Placement Time");
        dialog.setContentText("Enter order placement time (yyyy-MM-dd HH:mm):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(fakeTime -> {
            try {
                LocalDateTime orderTime = LocalDateTime.parse(fakeTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                int creditsToRedeem = Integer.parseInt(redeemCreditsField.getText());
                if (creditsToRedeem > user.getCredits()) {
                    showAlert("Error", "You do not have enough credits.");
                    return;
                }

                double totalPrice = Double.parseDouble(totalPriceLabel.getText());
                double discount = creditsToRedeem / 100.0;
                double finalPrice = totalPrice - discount;

                placeOrder(finalPrice, orderTime);
                int remainingCredits = user.getCredits() - creditsToRedeem;
                userController.updateCredits(user, remainingCredits);
            } catch (NumberFormatException | SQLException | DateTimeParseException e) {
                showAlert("Error", "Invalid input or failed to update credits.");
            }
        });
    }
}
private void placeOrder(double finalPrice, LocalDateTime orderTime) {
    try (Connection conn = DBConnection.getConnection()) {
        String orderNumber = generateOrderNumber();

        String sql = "INSERT INTO orders (orderNumber, username, orderTime, totalPrice, status) VALUES (?, ?, ?, ?, 'placed')";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, orderNumber);
            stmt.setString(2, user.getUsername());
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(orderTime));
            stmt.setDouble(4, finalPrice);
            stmt.executeUpdate();
        }

        for (OrderItem item : orderItems) {
            sql = "INSERT INTO order_items (orderNumber, itemName, quantity, price) VALUES (?, ?, ?, ?)";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, orderNumber);
                stmt.setString(2, item.getName());
                stmt.setInt(3, item.getQuantity());
                stmt.setDouble(4, item.getPrice());
                stmt.executeUpdate();
            }
        }

        showAlert("Success", "Order placed successfully!");
        navigateToDashboard();
    } catch (SQLException e) {
        e.printStackTrace();
        showAlert("Error", "Failed to place order.");
    }
}



    private boolean validatePaymentDetails(String cardNumber, String expiryDate, String cvv) {
        if (cardNumber.length() != 16 || !cardNumber.matches("\\d+")) {
            showAlert("Error", "Invalid card number. It must have 16 digits.");
            return false;
        }

        if (!expiryDate.matches("(0[1-9]|1[0-2])/\\d{2}")) {
            showAlert("Error", "Invalid expiry date. It must be in MM/YY format.");
            return false;
        }

        if (cvv.length() != 3 || !cvv.matches("\\d+")) {
            showAlert("Error", "Invalid CVV. It must have 3 digits.");
            return false;
        }

        return true;
    }

    private void placeOrder(double finalPrice) {
        try (Connection conn = DBConnection.getConnection()) {
            String orderNumber = generateOrderNumber();
            LocalDateTime orderTime = LocalDateTime.now();

            String sql = "INSERT INTO orders (orderNumber, username, orderTime, totalPrice, status) VALUES (?, ?, ?, ?, 'placed')";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, orderNumber);
                stmt.setString(2, user.getUsername());
                stmt.setTimestamp(3, java.sql.Timestamp.valueOf(orderTime));
                stmt.setDouble(4, finalPrice);
                stmt.executeUpdate();
            }

            for (OrderItem item : orderItems) {
                sql = "INSERT INTO order_items (orderNumber, itemName, quantity, price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, orderNumber);
                    stmt.setString(2, item.getName());
                    stmt.setInt(3, item.getQuantity());
                    stmt.setDouble(4, item.getPrice());
                    stmt.executeUpdate();
                }
            }

            showAlert("Success", "Order placed successfully!");
            navigateToDashboard();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to place order.");
        }
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void navigateToDashboard() {
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) orderSummaryListView.getScene().getWindow();
            stage.setScene(scene);
            DashboardController controller = loader.getController();
            controller.setUser(user); // Pass the user to DashboardController
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Dashboard.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) orderSummaryListView.getScene().getWindow();
            stage.setScene(scene);
            DashboardController controller = loader.getController();
            controller.setUser(user); // Pass the user to DashboardController
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Dashboard.");
        }
    }
}
