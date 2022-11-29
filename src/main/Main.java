package main;

import javafx.scene.control.Label;
import org.w3c.dom.Text;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import main.JDBC;
import java.time.ZoneId;

/**
 * The type Main controller.
 */
public class Main extends Application {

    /**
     * Set Application Variables
     *
     */
    private static String userLoggedIn = "";
    private static Integer userLoggedInID = null;
    private static String userZone = ZoneId.systemDefault().toString();

    /**
     * Configure the Login Fields.
     */
    // configure the username field
    @FXML
    private TextField login_username;
    // configure the password field
    @FXML
    private PasswordField login_password;
    // configure the location label
    @FXML
    private Label login_location;

    /**
     * Use Zone ID to set the location label.
     *
     */
    public void initialize() {
        login_location.setText(userZone);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
    }

    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
    }

    /**
     * Exit Button Event Handler - Close the application.
     *
     * @param event triggered by the exit button
     */
    public void ExitBtnAction(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Login Button Event Handler - Use the entered username and password to attempt
     * login. If successful, open the main application window.
     * Get the login_username and login_password fx:id in login.fxml for the values
     *
     * @param event triggered by the login button
     *
     *
     */
    public void LoginBtnAction(ActionEvent event) {
        if (JDBC.login(login_username.getText(), login_password.getText())) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Scheduler");
                stage.setScene(new Scene(root, 1920, 1080));
                stage.show();
                userLoggedIn = login_username.getText();
                userLoggedInID = JDBC.getUserID(userLoggedIn);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Record User Login Attempts to local file in the application directory.
     *
     * @param username  entered username
     * @param success   - true if login was successful, false if not
     * @param location  - the location of the login attempt
     * @param timestamp - the time of the login attempt
     * @param datestamp - the date of the login attempt
     */
    public static void recordLoginAttempt(String username, boolean success, String location, String timestamp,
            String datestamp) {
        try {
            String loginAttempt = username + "," + success + "," + location + "," + datestamp + "," + timestamp;
            String fileName = "login_activity.txt";
            FileWriter fw = new FileWriter(fileName, true); // append new data to the file
            fw.write(loginAttempt + "\n");
            fw.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // System.out.println("Login attempt recorded."); - DEBUG
        }
    }
}
