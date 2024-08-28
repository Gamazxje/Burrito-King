package main.java.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBSetup {
    //fixed to correct path
    private static final String URL = "jdbc:sqlite:/Users/PhucDatBich/Desktop/jaiowjiowej/burritoking.db";

    public static void setupDatabase() {
        try (Connection conn = DriverManager.getConnection(URL);
             Statement stmt = conn.createStatement()) {
            // Create users table
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users (" +
                         "username TEXT PRIMARY KEY," +
                         "password TEXT NOT NULL," +
                         "first_name TEXT NOT NULL," +
                         "last_name TEXT NOT NULL," +
                         "isVIP BOOLEAN NOT NULL DEFAULT 0," +
                         "email TEXT DEFAULT ''," +
                         "credits INTEGER DEFAULT 0" +
                         ")";
            stmt.execute(createUsersTable);

            // Create orders table
            String createOrdersTable = "CREATE TABLE IF NOT EXISTS orders (" +
                         "orderNumber TEXT PRIMARY KEY," +
                         "username TEXT NOT NULL," +
                         "orderTime TIMESTAMP NOT NULL," +
                         "totalPrice REAL NOT NULL," +
                         "status TEXT NOT NULL," +
                         "collectionTime TIMESTAMP," +
                         "FOREIGN KEY (username) REFERENCES users(username)" +
                         ")";
            stmt.execute(createOrdersTable);

            System.out.println("Database setup complete.");
        } catch (SQLException e) {
            System.out.println("Error setting up database: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        setupDatabase();
    }
}
