package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.java.model.OrderItem;
import main.java.model.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ShoppingBasketController {
    @FXML
    private ListView<OrderItem> basketListView;

    private User user;

    public void setUser(User user) {
        this.user = user;
    }

    @FXML
    private void handleAddBurrito() {
        addItemToBasket("Burrito");
    }

    @FXML
    private void handleAddFries() {
        addItemToBasket("Fries");
    }

    @FXML
    private void handleAddSoda() {
        addItemToBasket("Soda");
    }

    @FXML
    private void handleRemoveSelected() {
        OrderItem selectedItem = basketListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            basketListView.getItems().remove(selectedItem);
        } else {
            showAlert("Error", "No item selected");
        }
    }

    @FXML
    private void handleUpdateQuantity() {
        OrderItem selectedItem = basketListView.getSelectionModel().getSelectedItem();
        if (selectedItem != null) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(selectedItem.getQuantity()));
            dialog.setTitle("Update Quantity");
            dialog.setHeaderText("Update Quantity");
            dialog.setContentText("Enter new quantity:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(quantity -> selectedItem.setQuantity(Integer.parseInt(quantity)));
            basketListView.refresh();
        } else {
            showAlert("Error", "No item selected");
        }
    }

    @FXML
    private void handleCheckout() {
        List<OrderItem> orderItems = new ArrayList<>(basketListView.getItems());
        navigateToCheckout(orderItems);
    }

    @FXML
    private void handleAddOrder() {
        List<OrderItem> orderItems = new ArrayList<>(basketListView.getItems());
        if (orderItems.isEmpty()) {
            showAlert("Error", "No items in the basket to add as an order.");
            return;
        }

        try {
            // Properly load OrderController
            URL resource = new File("src/main/resources/fxml/OrderView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            loader.load();
            OrderController orderController = loader.getController();

            double totalPrice = calculateTotalPrice(orderItems);
            orderController.setUser(user);  // Ensure user is set
            orderController.addOrder(user.getUsername(), orderItems, totalPrice, "placed");

            showAlert("Success", "Order added successfully.");
            basketListView.getItems().clear(); // Clear the basket after adding the order
            navigateToDashboard();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to add the order.");
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) basketListView.getScene().getWindow();
            stage.setScene(scene);
            DashboardController controller = loader.getController();
            controller.setUser(user); // Pass the user to DashboardController
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Dashboard.");
        }
    }

    private void addItemToBasket(String itemName) {
        TextInputDialog dialog = new TextInputDialog("1");
        dialog.setTitle("Add Item");
        dialog.setHeaderText("Add " + itemName);
        dialog.setContentText("Enter quantity:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(quantity -> {
            OrderItem item = new OrderItem(itemName, Integer.parseInt(quantity), getItemPrice(itemName));
            basketListView.getItems().add(item);
        });
    }

    private double getItemPrice(String itemName) {
        switch (itemName) {
            case "Burrito":
                return 7.00;
            case "Fries":
                return 4.00;
            case "Soda":
                return 2.50;
            default:
                return 0.0;
        }
    }

    private double calculateTotalPrice(List<OrderItem> items) {
        return items.stream().mapToDouble(item -> item.getPrice() * item.getQuantity()).sum();
    }

    private void navigateToCheckout(List<OrderItem> orderItems) {
        try {
            URL resource = new File("src/main/resources/fxml/CheckoutView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Stage stage = (Stage) basketListView.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));

            CheckoutController controller = loader.getController();
            controller.setOrderItems(orderItems);
            controller.setUser(user); // Ensure the user is set

            stage.setTitle("Burrito King - Checkout");
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Failed to load Checkout view.");
        }
    }

    private void navigateToDashboard() {
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Stage stage = (Stage) basketListView.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            
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
