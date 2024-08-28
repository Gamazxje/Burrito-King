package main.java.controller;

import main.java.model.User;
import main.java.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserController {
    public void createUser(String username, String password, String firstName, String lastName) throws SQLException {
        String sql = "INSERT INTO users (username, password, first_name, last_name, isVIP, email, credits) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            stmt.setString(3, firstName);
            stmt.setString(4, lastName);
            stmt.setBoolean(5, false);  
            stmt.setString(6, "");      
            stmt.setInt(7, 0);          
            stmt.executeUpdate();
            System.out.println("User created: " + username);
        } catch (SQLException e) {
            System.out.println("Error creating user: " + e.getMessage());
        }
    }

    public User login(String username, String password) throws SQLException {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("first_name"),
                    rs.getString("last_name"),
                    rs.getBoolean("isVIP"),
                    rs.getString("email"),
                    rs.getInt("credits")
                );
            } else {
                System.out.println("User not found: " + username);
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error logging in: " + e.getMessage());
            return null;
        }
    }

    public void editProfile(User user, String firstName, String lastName, String password) throws SQLException {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, password = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, password);
            stmt.setString(4, user.getUsername());
            stmt.executeUpdate();
            System.out.println("Profile updated: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    public void upgradeToVIP(User user, String email) throws SQLException {
        String sql = "UPDATE users SET isVIP = ?, email = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setBoolean(1, true);
            stmt.setString(2, email);
            stmt.setString(3, user.getUsername());
            stmt.executeUpdate();
            user.setVIP(true);
            user.setEmail(email);
            System.out.println("User upgraded to VIP: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error upgrading to VIP: " + e.getMessage());
        }
    }

    public void updateCredits(User user, int credits) throws SQLException {
        String sql = "UPDATE users SET credits = ? WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, credits);
            stmt.setString(2, user.getUsername());
            stmt.executeUpdate();
            user.setCredits(credits);  // Ensure user's credits are updated
            System.out.println("Credits updated for user: " + user.getUsername());
        } catch (SQLException e) {
            System.out.println("Error updating credits: " + e.getMessage());
        }
    }
}
