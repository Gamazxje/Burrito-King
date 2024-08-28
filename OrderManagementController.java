package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import main.java.model.Order;
import main.java.model.User;
import main.java.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderManagementController {
    @FXML
    private ListView<Order> ordersListView;

    private User user;

    public void setUser(User user) {
        this.user = user;
        loadOrders();
    }

    private void loadOrders() {
        try {
            if (user == null) {
                throw new IllegalStateException("User must be set before loading orders.");
            }
            List<Order> orders = getOrdersForUser(user.getUsername());
            ordersListView.getItems().setAll(orders);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load orders.");
        }
    }

    private List<Order> getOrdersForUser(String username) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT orderNumber, username, orderTime, totalPrice, status, collectionTime FROM orders WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                    rs.getString("orderNumber"),
                    rs.getString("username"),
                    rs.getTimestamp("orderTime").toLocalDateTime(),
                    rs.getDouble("totalPrice"),
                    rs.getString("status")
                );
                if (rs.getTimestamp("collectionTime") != null) {
                    order.setCollectionTime(rs.getTimestamp("collectionTime").toLocalDateTime());
                }
                orders.add(order);
            }
        }
        return orders;
    }

    @FXML
    private void handleCollectOrder() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && "placed".equals(selectedOrder.getStatus())) {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Collect Order");
            dialog.setHeaderText("Collect Order");
            dialog.setContentText("Enter fake collection time (yyyy-MM-dd HH:mm):");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(fakeTime -> {
                try {
                    System.out.println("Fake time entered: " + fakeTime); // Debug statement
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
                    LocalDateTime collectionTime = LocalDateTime.parse(fakeTime, formatter);
                    System.out.println("Parsed collection time: " + collectionTime); // Debug statement
                    if (collectionTime.isAfter(selectedOrder.getOrderTime().plusMinutes(15))) { // Assuming 15 minutes preparation time
                        updateOrderStatus(selectedOrder.getOrderNumber(), "collected", collectionTime);
                    } else {
                        showAlert("Error", "Collection time must be at least 15 minutes after order time.");
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Debug statement
                    showAlert("Error", "Invalid date format. Please use yyyy-MM-dd HH:mm.");
                }
            });
        } else {
            showAlert("Error", "Please select a valid placed order to collect.");
        }
    }

    @FXML
    private void handleCancelOrder() {
        Order selectedOrder = ordersListView.getSelectionModel().getSelectedItem();
        if (selectedOrder != null && "placed".equals(selectedOrder.getStatus())) {
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE orders SET status = 'cancelled' WHERE orderNumber = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, selectedOrder.getOrderNumber());
                    stmt.executeUpdate();
                }
                showAlert("Success", "Order cancelled successfully!");
                loadOrders();
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to cancel order.");
            }
        } else {
            showAlert("Error", "Please select a valid placed order to cancel.");
        }
    }

    private void updateOrderStatus(String orderNumber, String status, LocalDateTime collectionTime) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE orders SET status = ?, collectionTime = ? WHERE orderNumber = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, status);
                stmt.setTimestamp(2, java.sql.Timestamp.valueOf(collectionTime));
                stmt.setString(3, orderNumber);
                stmt.executeUpdate();
            }
            showAlert("Success", "Order collected successfully!");
            loadOrders();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to update order status.");
        }
    }

    @FXML
    private void handleExportOrders() {
        try {
            List<Order> orders = getOrdersForUser(user.getUsername());
            if (orders.isEmpty()) {
                showAlert("Error", "No orders to export.");
                return;
            }

            // Show a dialog to select the destination and filename
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save Orders");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
            File file = fileChooser.showSaveDialog(ordersListView.getScene().getWindow());

            if (file != null) {
                exportOrdersToCSV(file, orders);
                showAlert("Success", "Orders exported successfully.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to export orders.");
        }
    }

    private void exportOrdersToCSV(File file, List<Order> orders) {
        try (PrintWriter writer = new PrintWriter(file)) {
            writer.println("Order Number,Username,Order Time,Total Price,Status,Collection Time");

            for (Order order : orders) {
                writer.printf("%s,%s,%s,%.2f,%s,%s\n", order.getOrderNumber(), order.getUsername(),
                        order.getOrderTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), 
                        order.getTotalPrice(), order.getStatus(),
                        order.getCollectionTime() != null ? order.getCollectionTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to write to file.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ordersListView.getScene().getWindow();
            stage.setScene(scene);
            DashboardController controller = loader.getController();
            controller.setUser(user); // Pass the user to DashboardController
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Dashboard.");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}