package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import main.java.model.User;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.Optional;

public class DashboardController {
    private User user;
    private UserController userController = new UserController();

    public void setUser(User user) {
        this.user = user;
    }
    

    @FXML
    private void handleEditProfile(ActionEvent event) {
    try {
        URL resource = new File("src/main/resources/fxml/EditProfileView.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(resource);
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        EditProfileController controller = loader.getController();
        controller.setUser(user);  // Pass the user to EditProfileController if needed
        stage.setTitle("Edit Profile - Burrito King");
    } catch (IOException e) {
        e.printStackTrace();
    }
}


    @FXML
    private void handleViewOrders(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/OrderView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            OrderController controller = loader.getController();
            controller.setUser(user);  // Ensure user is set here
            stage.setTitle("Burrito King - Orders");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleShoppingBasket(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/ShoppingBasketView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            ShoppingBasketController controller = loader.getController();
            controller.setUser(user);  // Ensure user is set here
            stage.setTitle("Burrito King - Shopping Basket");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleOrderManagement(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/OrderManagementView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            OrderManagementController controller = loader.getController();
            controller.setUser(user);  // Ensure user is set here
            stage.setTitle("Burrito King - Order Management");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCheckout(ActionEvent event) {
        try {
            URL resource = new File("src/main/resources/fxml/CheckoutView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            CheckoutController controller = loader.getController();
            controller.setUser(user);  // Ensure user is set here
            stage.setTitle("Burrito King - Checkout");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleLogout(ActionEvent event) {
    // Clear user information
    user = null;
    
    // Navigate to login screen or another appropriate view
    try {
        URL resource = new File("src/main/resources/fxml/LoginView.fxml").toURI().toURL();
        FXMLLoader loader = new FXMLLoader(resource);
        Scene scene = new Scene(loader.load());
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("Login - Burrito King");
    } catch (IOException e) {
        e.printStackTrace();
    }
}
    @FXML
    private void handleUpgradeToVIP(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Upgrade to VIP");
        dialog.setHeaderText("Upgrade to VIP");
        dialog.setContentText("Enter your email to receive promotions:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(email -> {
            if (email.isEmpty() || !email.contains("@")) {
                showAlert("Invalid Email", "Please provide a valid email address.");
                return;
            }
            try {
                userController.upgradeToVIP(user, email);
                showAlert("Success", "You have been upgraded to VIP! Please log out and log in again to access VIP functionalities.");
            } catch (SQLException e) {
                showAlert("Error", "Failed to upgrade to VIP: " + e.getMessage());
            }
        });
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

