package main;

import javafx.collections.ObservableList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBC {
    private static final String protocol = "jdbc";
    private static final String vendor = ":mysql:";
    private static final String location = "//localhost/";
    private static final String databaseName = "client_schedule";
    private static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    private static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    private static final String userName = "sqlUser"; // Username
    private static String password = "Passw0rd!"; // Password
    private static Connection connection = null; // Connection Interface
    private static PreparedStatement preparedStatement;

    public static void makeConnection() {

        try {
            Class.forName(driver); // Locate Driver
            // password = Details.getPassword(); // Assign password
            connection = DriverManager.getConnection(jdbcUrl, userName, password); // reference Connection object
            System.out.println("Connection successful!");
        } catch (ClassNotFoundException e) {
            System.out.println("Error:" + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Error:" + e.getMessage());
        }
    }

    public static Connection getConnection() {
        return connection;
    }

    public static void closeConnection() {
        try {
            connection.close();
            System.out.println("Connection closed!");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void makePreparedStatement(String sqlStatement, Connection conn) throws SQLException {
        if (conn != null)
            preparedStatement = conn.prepareStatement(sqlStatement);
        else
            System.out.println("Prepared Statement Creation Failed!");
    }

    public static PreparedStatement getPreparedStatement() throws SQLException {
        if (preparedStatement != null)
            return preparedStatement;
        else
            System.out.println("Null reference to Prepared Statement");
        return null;
    }

    /**
     * Login Method - Use the entered username and password to attempt login.
     * Get the login_username and login_password fx:id in login.fxml for the values
     *
     * @param username entered username
     * @param password entered password
     *
     */
    public static boolean login(String username, String password) {
        try {
            makePreparedStatement("SELECT * FROM users WHERE User_Name = ? AND Password = ?", connection);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            if (preparedStatement.executeQuery().next()) {
                // System.out.println("Logged In"); - DEBUG
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // System.out.println("Username or Password Incorrect"); - DEBUG
        return false;
    }

    /**
     * Get User ID Method - Use the entered username to get the user ID.
     *
     * @param username entered username
     */
    public static int getUserID(String username) {
        try {
            makePreparedStatement("SELECT User_ID FROM users WHERE User_Name = ?", connection);
            preparedStatement.setString(1, username);
            if (preparedStatement.executeQuery().next()) {
                // System.out.println("User ID was found"); - DEBUG
                return preparedStatement.getResultSet().getInt("User_ID");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // System.out.println("User ID not found"); - DEBUG
        return 0;
    }

    /**
     * Get all Appointments Method
     *
     * @param sortedBy - either "Month" or "Week" to sort the appointments by start
     *                 date
     * @return
     */
    public static ObservableList getAppointments(String sortedBy) {
        try {
            makePreparedStatement("SELECT * FROM appointments ORDER BY Start " + sortedBy, connection);
            if (preparedStatement.executeQuery().next()) {
                // System.out.println("Appointments were found"); - DEBUG
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        // System.out.println("Appointments not found"); - DEBUG
        // return 0;
        return null;
    }
}
