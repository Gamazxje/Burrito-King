// EditProfileController.java
package main.java.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.java.model.User;
import main.java.util.DBConnection;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditProfileController {
    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private PasswordField passwordField;

    private User user;

    public void setUser(User user) {
        this.user = user;
        // Set the user's information in the text fields
        if (user != null) {
            firstNameField.setText(user.getFirstName());
            lastNameField.setText(user.getLastName());
        }
    }

    @FXML
    private void handleSaveChanges() {
        // Update the user's information based on the values in the text fields
        if (user != null) {
            user.setFirstName(firstNameField.getText());
            user.setLastName(lastNameField.getText());
            if (!passwordField.getText().isEmpty()) {
                user.setPassword(passwordField.getText());  // Assuming setPassword handles password hashing or other security measures
            }

            // Update the user information in the database
            try (Connection conn = DBConnection.getConnection()) {
                String sql = "UPDATE users SET firstName = ?, lastName = ?, password = ? WHERE username = ?";
                try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                    stmt.setString(1, user.getFirstName());
                    stmt.setString(2, user.getLastName());
                    stmt.setString(3, user.getPassword());  // Ensure password is properly hashed before storing
                    stmt.setString(4, user.getUsername());
                    stmt.executeUpdate();
                }
                showAlert("Success", "Profile updated successfully!");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Error", "Failed to update profile.");
            }
        } else {
            showAlert("Error", "User information is not set.");
        }
    }

    @FXML
    private void handleBack() {
        // Navigate back to the Dashboard
        try {
            URL resource = new File("src/main/resources/fxml/DashboardView.fxml").toURI().toURL();
            FXMLLoader loader = new FXMLLoader(resource);
            Scene scene = new Scene(loader.load());
            Stage stage = (Stage) firstNameField.getScene().getWindow();
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
