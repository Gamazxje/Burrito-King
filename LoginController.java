// src/main/java/controller/LoginController.java
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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class LoginController {
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;

    private UserController userController = new UserController();

    @FXML
    private void handleLogin(ActionEvent event) {
        try {
            System.out.println("Attempting login for user: " + usernameField.getText());
            User user = userController.login(usernameField.getText(), passwordField.getText());
            if (user != null) {
                System.out.println("Login successful for user: " + usernameField.getText());
                URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
                FXMLLoader loader = new FXMLLoader(resource);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(loader.load()));

                DashboardController controller = loader.getController();
                controller.setUser(user);

                stage.setTitle("Burrito King - Dashboard");
            } else {
                System.out.println("Login failed for user: " + usernameField.getText());
                showAlert(Alert.AlertType.ERROR, "Login Failed", "Invalid username or password.");
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void showRegisterForm(ActionEvent event) {
        try {
            System.out.println("Register button clicked");
            URL resource = new File("src/main/resources/fxml/RegisterView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Burrito King - Register");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
