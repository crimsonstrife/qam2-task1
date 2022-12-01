package main;

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
import javafx.stage.Stage;
import javafx.stage.Modality;
import java.net.URL;

import javafx.scene.control.*;
import main.utilities.JDBC;

import java.time.ZoneId;
import java.util.ResourceBundle;
import static main.utilities.Utils.recordLoginAttempt;

/**
 * The Login controller.
 * This class is the controller for the Login screen.
 */
public class Login extends Application implements Initializable {
    /**
     * Set Application Variables
     */
    private static String userLoggedIn = ""; // This is the user that is logged in.
    private static Integer userLoggedInID = null; // This is the user ID that is logged in.
    private static String userZone = ZoneId.systemDefault().toString(); // This is the user's time zone.
    private static String userLanguage = System.getProperty("user.language"); // This is the user's language.
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
        String loginDate = java.time.LocalDate.now().toString(); // get the current date
        String loginTime = java.time.LocalTime.now().toString(); // get the current time
        // translate the app title based on system language //
        String appTitle = "";
        if (userLanguage.equals("fr")) { // if the user's language is French
            appTitle = "Planificateur"; // set the app title to French
        } else {
            appTitle = "Scheduler"; // otherwise, set the app title to English
        }
        if (JDBC.login(login_username.getText(), login_password.getText())) { // if the login is successful
            try { // try to open the main application window
                Parent login = FXMLLoader.load(getClass().getResource("resources/views/main.fxml"));
                Stage stage = new Stage();
                stage.setTitle(appTitle);
                stage.setScene(new Scene(login, 900, 600));
                stage.show();
                userLoggedIn = login_username.getText();
                userLoggedInID = JDBC.getUserID(userLoggedIn);
                recordLoginAttempt(userLoggedIn, true, userZone, loginDate, loginTime);
                // store the logged in user ID
                Main.loggedInUserID = userLoggedInID;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else { // if the login is unsuccessful
            String alertTitle = "";
            String alertHeader = "";
            String alertContext = "";
            // translate the alert content based on system language //
            if (userLanguage.equals("fr")) {
                alertTitle = "Erreur d'identification";
                alertHeader = "Erreur d'identification";
                alertContext = "Le nom d'utilisateur ou le mot de passe que vous avez tapé est incorrect.";
            } else {
                alertTitle = "Login Error";
                alertHeader = "Login Error";
                alertContext = "The username or password you entered is incorrect.";
            }
            Alert alert = new Alert(Alert.AlertType.ERROR); // create an alert
            alert.setTitle(alertTitle); // set the alert title
            alert.setHeaderText(alertHeader); // set the alert header
            alert.setContentText(alertContext); // set the alert content
            alert.initModality(Modality.APPLICATION_MODAL); // set the alert to modal
            alert.showAndWait(); // show the alert
            recordLoginAttempt(login_username.getText(), false, userZone, loginDate, loginTime); // record the login
                                                                                                 // attempt
        }
    }

    /**
     * Constructor
     */
    public Login() {
    }

    public static void main(String[] args) {
        JDBC.makeConnection();
        Application.launch(args);
    }

    /**
     * Start the application.
     *
     * @throws Exception
     */
    public void start(Stage primaryStage) throws Exception {
        String appTitle = ""; // set the app title
        if (userLanguage.equals("fr")) { // if the user's language is French
            appTitle = "Planificateur"; // set the app title to French
        } else {
            appTitle = "Scheduler"; // otherwise, set the app title to English
        }
        Parent login = FXMLLoader.load(getClass().getResource("resources/views/login.fxml")); // load the login screen
        primaryStage.setTitle(appTitle); // set the app title
        primaryStage.setScene(new Scene(login, 640, 480));
        primaryStage.show(); // show the login screen
    }

    /**
     * Initialize method
     * 
     * @param url
     * @param rb
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Use Zone ID to set the location label.
        login_location.setText(userZone);
        // set the field labels to the user's language
        if (userLanguage.equals("fr")) {
            login_username_label.setText("Nom d'utilisateur: ");
            login_password_label.setText("Mot de passe: ");
        } else {
            login_username_label.setText("Username: ");
            login_password_label.setText("Password: ");
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
}
