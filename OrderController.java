package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import main.java.model.Order;
import main.java.model.OrderItem;
import main.java.model.User;
import main.java.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class OrderController {
    @FXML
    private ListView<String> ordersListView;

    private User user;
    private UserController userController = new UserController();
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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
            for (Order order : orders) {
                ordersListView.getItems().add(order.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load orders.");
        }
    }

    public List<Order> getActiveOrders(String username) throws SQLException {
        List<Order> activeOrders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT * FROM orders WHERE username = ? AND status = 'placed'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Order order = new Order(
                    rs.getString("orderNumber"),
                    rs.getTimestamp("orderTime").toLocalDateTime()
                );
                order.setTotalPrice(rs.getDouble("totalPrice"));
                order.setStatus(rs.getString("status"));
                activeOrders.add(order);
            }
        }
        return activeOrders;
    }

    public void addOrder(String username, List<OrderItem> items, double totalPrice, String status) throws SQLException {
        double finalPrice = totalPrice;
        if (user.isVIP()) {
            finalPrice = applyVIPDiscount(totalPrice);
        }

        try (Connection conn = DBConnection.getConnection()) {
            String orderNumber = generateOrderNumber();
            String sql = "INSERT INTO orders (orderNumber, username, orderTime, totalPrice, status) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, orderNumber);
            stmt.setString(2, username);
            stmt.setTimestamp(3, java.sql.Timestamp.valueOf(LocalDateTime.now()));
            stmt.setDouble(4, finalPrice);
            stmt.setString(5, status);
            stmt.executeUpdate();

            for (OrderItem item : items) {
                String itemSql = "INSERT INTO order_items (orderNumber, itemName, quantity, price) VALUES (?, ?, ?, ?)";
                PreparedStatement itemStmt = conn.prepareStatement(itemSql);
                itemStmt.setString(1, orderNumber);
                itemStmt.setString(2, item.getName());
                itemStmt.setInt(3, item.getQuantity());
                itemStmt.setDouble(4, item.getPrice());
                itemStmt.executeUpdate();
            }

            if (user.isVIP()) {
                collectCredits(user, finalPrice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add the order to the database.");
        }
    }

    private double applyVIPDiscount(double totalPrice) {
        return totalPrice - 3.0; // Applying a $3 discount for VIP users
    }

    private void collectCredits(User user, double finalPrice) throws SQLException {
        int creditsEarned = (int) finalPrice; // 1 credit per dollar spent
        int newCredits = user.getCredits() + creditsEarned;
        userController.updateCredits(user, newCredits);
    }

    private String generateOrderNumber() {
        return "ORD" + System.currentTimeMillis();
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
            controller.setUser(user);
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Dashboard.");
        }
    }

    private List<Order> getOrdersForUser(String username) throws SQLException {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "SELECT orderNumber, orderTime, totalPrice, status FROM orders WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LocalDateTime orderTime = rs.getTimestamp("orderTime").toLocalDateTime();
                Order order = new Order(
                    rs.getString("orderNumber"),
                    orderTime
                );
                order.setTotalPrice(rs.getDouble("totalPrice"));
                order.setStatus(rs.getString("status"));
                orders.add(order);
            }
        }
        return orders;
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
