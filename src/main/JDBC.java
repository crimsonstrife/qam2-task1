package main;

import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class JDBC {
    public static final String protocol = "jdbc";
    public static final String vendor = ":mysql:";
    public static final String location = "//localhost/";
    public static final String databaseName = "client_schedule";
    public static final String jdbcUrl = protocol + vendor + location + databaseName + "?connectionTimeZone = SERVER"; // LOCAL
    public static final String driver = "com.mysql.cj.jdbc.Driver"; // Driver reference
    public static final String userName = "sqlUser"; // Username
    public static String password = "Passw0rd!"; // Password
    public static Connection connection = null; // Connection Interface
    public static PreparedStatement preparedStatement;

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

}
