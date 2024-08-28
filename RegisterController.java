package main.java.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class RegisterController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;

    private UserController userController = new UserController();

    @FXML
    private void handleRegister(ActionEvent event) {
        System.out.println("Register button clicked");
        try {
            System.out.println("Attempting to register user: " + usernameField.getText());
            userController.createUser(usernameField.getText(), passwordField.getText(), firstNameField.getText(), lastNameField.getText());
            System.out.println("User registered successfully: " + usernameField.getText());
            showAlert(Alert.AlertType.INFORMATION, "Registration Successful", "Registration successful! Please login.");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Registration failed for user: " + usernameField.getText() + " - " + e.getMessage());
            showAlert(Alert.AlertType.ERROR, "Registration Failed", "Registration failed!");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        navigateToLogin();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait().ifPresent(response -> navigateToLogin());
    }

    private void navigateToLogin() {
        try {
            Stage stage = (Stage) usernameField.getScene().getWindow();
            URL resource = new File("src/main/resources/fxml/LoginView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Burrito King - Login");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
