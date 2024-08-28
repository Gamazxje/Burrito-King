package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.model.User;

import java.io.IOException;
import java.sql.SQLException;

public class UserProfileController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField emailField;

    private User user;
    private UserController userController = new UserController();

    public void setUser(User user) {
        this.user = user;
        firstNameField.setText(user.getFirstName());
        lastNameField.setText(user.getLastName());
        emailField.setText(user.getEmail());
    }

    @FXML
    private void handleSave(ActionEvent event) {
        try {
            userController.editProfile(user, firstNameField.getText(), lastNameField.getText(), passwordField.getText());
            showAlert(Alert.AlertType.INFORMATION, "Profile Updated", "Profile updated successfully!");
            handleCancel(event);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update profile.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        try {
            Stage stage = (Stage) firstNameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/DashboardView.fxml"));
            stage.setScene(new Scene(loader.load()));
            DashboardController controller = loader.getController();
            controller.setUser(user);
            stage.setTitle("Burrito King - Dashboard");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUpgradeToVIP(ActionEvent event) {
        try {
            String email = emailField.getText();
            if (email.isEmpty() || !email.contains("@")) {
                showAlert(Alert.AlertType.ERROR, "Invalid Email", "Please provide a valid email address.");
                return;
            }
            userController.upgradeToVIP(user, email);
            showAlert(Alert.AlertType.INFORMATION, "Upgrade Successful", "You have been upgraded to VIP! Please log out and log in again to access VIP functionalities.");
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Upgrade Failed", "Failed to upgrade to VIP.");
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
