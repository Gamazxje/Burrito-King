package main.java.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    //fixed to correct path
    private static final String URL = "jdbc:sqlite:/Users/PhucDatBich/Desktop/jaiowjiowej/burritoking.db"; 
    
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection(URL);
        } catch (ClassNotFoundException e) {
            throw new SQLException("Unable to load the SQLite JDBC driver", e);
        }
    }
}
