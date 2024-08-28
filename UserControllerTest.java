package test.java.controller;

import main.java.controller.UserController;
import main.java.model.User;
import main.java.util.DBConnection;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    private static UserController userController;

    @BeforeAll
    static void setUpBeforeClass() throws Exception {
        userController = new UserController();
        // Set up database connection and table creation for tests
        try (Connection conn = DBConnection.getConnection()) {
            String createTableSQL = "CREATE TABLE IF NOT EXISTS users (" +
                                    "username TEXT PRIMARY KEY, " +
                                    "password TEXT, " +
                                    "first_name TEXT, " +
                                    "last_name TEXT, " +
                                    "isVIP BOOLEAN, " +
                                    "email TEXT, " +
                                    "credits INTEGER)";
            try (PreparedStatement stmt = conn.prepareStatement(createTableSQL)) {
                stmt.execute();
            }
        }
    }

    @AfterAll
    static void tearDownAfterClass() throws Exception {
        // Clean up database after tests
        try (Connection conn = DBConnection.getConnection()) {
            String dropTableSQL = "DROP TABLE IF EXISTS users";
            try (PreparedStatement stmt = conn.prepareStatement(dropTableSQL)) {
                stmt.execute();
            }
        }
    }

    @BeforeEach
    void setUp() throws Exception {
        // Clear data before each test
        try (Connection conn = DBConnection.getConnection()) {
            String clearDataSQL = "DELETE FROM users";
            try (PreparedStatement stmt = conn.prepareStatement(clearDataSQL)) {
                stmt.execute();
            }
        }
    }

    @Test
    void testCreateUser() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
    }

    @Test
    void testLogin() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);
        assertEquals("testuser", user.getUsername());
        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());

        User invalidUser = userController.login("invaliduser", "password");
        assertNull(invalidUser);
    }

    @Test
    void testEditProfile() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);

        userController.editProfile(user, "Jane", "Doe", "newpassword");
        User updatedUser = userController.login("testuser", "newpassword");
        assertNotNull(updatedUser);
        assertEquals("Jane", updatedUser.getFirstName());
        assertEquals("Doe", updatedUser.getLastName());
    }

    @Test
    void testUpgradeToVIP() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);
        assertFalse(user.isVIP());

        userController.upgradeToVIP(user, "john.doe@example.com");
        User vipUser = userController.login("testuser", "password");
        assertNotNull(vipUser);
        assertTrue(vipUser.isVIP());
        assertEquals("john.doe@example.com", vipUser.getEmail());
    }

    @Test
    void testUpdateCredits() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);
        assertEquals(0, user.getCredits());

        userController.updateCredits(user, 100);
        User updatedUser = userController.login("testuser", "password");
        assertNotNull(updatedUser);
        assertEquals(100, updatedUser.getCredits());
    }

    @Test
    void testRedeemCredits() throws SQLException {
        userController.createUser("testuser", "password", "John", "Doe");
        User user = userController.login("testuser", "password");
        assertNotNull(user);
        userController.updateCredits(user, 100);

        userController.updateCredits(user, user.getCredits() - 50);
        User updatedUser = userController.login("testuser", "password");
        assertNotNull(updatedUser);
        assertEquals(50, updatedUser.getCredits());
    }
}
