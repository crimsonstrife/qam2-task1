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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert;
import javafx.stage.Modality;
import java.util.Optional;
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
    private static String userLanguage = System.getProperty("user.language");

    /**
     * Configure the Login Fields.
     */
    // configure the username field
    @FXML
    private TextField login_username;
    // configure the username label
    @FXML
    private Label login_username_label;
    // configure the password field
    @FXML
    private PasswordField login_password;
    // configure the password label
    @FXML
    private Label login_password_label;
    // configure the location label
    @FXML
    private Label login_location;
    // configure the login button
    @FXML
    private Button btn_login;
    // configure the exit button
    @FXML
    private Button btn_exit;

    /**
     * Use Zone ID to set the location label.
     *
     */
    public void initialize() {
        login_location.setText(userZone);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        String appTitle = "";
        if (userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle(appTitle);
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
        // set the field labels to the user's language
        if (userLanguage.equals("fr")) {
            login_username_label.setText("Nom d'utilisateur");
            login_password_label.setText("Mot de passe");
        } else {
            login_username_label.setText("Username");
            login_password_label.setText("Password");
        }
        // set the buttons to the user's language
        if (userLanguage.equals("fr")) {
            btn_login.setText("Connexion");
            btn_exit.setText("Sortie");
        } else {
            btn_login.setText("Login");
            btn_exit.setText("Exit");
        }
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
     * Set the language for the application based on the system language.
     *
     */
    public static void setLanguage() {
        String language = System.getProperty("user.language");
        if (language.equals("fr")) {
            userLanguage = "fr";
        } else {
            userLanguage = "en";
        }
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
        String loginDate = java.time.LocalDate.now().toString();
        String loginTime = java.time.LocalTime.now().toString();
        // translate the app title based on system language //
        String appTitle = "";
        if (userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        if (JDBC.login(login_username.getText(), login_password.getText())) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.setTitle(appTitle);
                stage.setScene(new Scene(root, 1920, 1080));
                stage.show();
                userLoggedIn = login_username.getText();
                userLoggedInID = JDBC.getUserID(userLoggedIn);
                recordLoginAttempt(userLoggedIn, true, userZone, loginDate, loginTime);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            String alertTitle = "";
            String alertHeader = "";
            String alertContext = "";
            // translate the alert content based on system language //
            if (userLanguage.equals("fr")) {
                String alertTitle = "Erreur d'identification";
                String alertHeader = "Erreur d'identification";
                String alertContext = "Le nom d'utilisateur ou le mot de passe que vous avez tapé est incorrect.";
            } else {
                alertTitle = "Login Error";
                alertHeader = "Login Error";
                alertContext = "The username or password you entered is incorrect.";
            }
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(alertTitle);
            alert.setHeaderText(alertHeader);
            alert.setContentText(alertContext);
            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
            recordLoginAttempt(login_username.getText(), false, userZone, loginDate, loginTime);
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
