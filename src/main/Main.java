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
import java.util.Optional;
import java.io.FileWriter;
import javafx.scene.control.*;
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
     * Configure the Application Fields
     */

    /**
     * Configure the Application Buttons
     */
    @FXML
    private Button btn_appointments;

    @FXML
    private Button btn_customers;

    @FXML
    private Button btn_users;

    @FXML
    private Button btn_signout;

    @FXML
    private Button btn_appointmentcreate;

    @FXML
    private Button btn_appointmentmodify;

    @FXML
    private Button btn_appointmentdelete;

    /**
     * Configure the Application Panes
     */
    @FXML
    private Pane appointments_pane;

    @FXML
    private Pane customers_pane;

    /**
     * Configure the Appointments Table
     */
    @FXML
    private TableView<Appointments> table_appointments;

    @FXML
    private TableColumn<Appointments, String> col_appointment_ID;

    @FXML
    private TableColumn<Appointments, String> col_appointment_title;

    @FXML
    private TableColumn<Appointments, String> col_appointment_desc;

    @FXML
    private TableColumn<Appointments, String> col_appointment_location;

    @FXML
    private TableColumn<Appointments, String> col_appointment_contact;

    @FXML
    private TableColumn<Appointments, String> col_appointment_type;

    @FXML
    private TableColumn<Appointments, String> col_appointment_startdate;

    @FXML
    private TableColumn<Appointments, String> col_appointment_enddate;

    @FXML
    private TableColumn<Appointments, String> col_appointment_customerID;

    @FXML
    private TableColumn<Appointments, String> col_appointment_userID;

    /**
     * Configure the Customers Table
     */
    @FXML
    private TableView<Customers> table_customers;

    @FXML
    private TableColumn<Customers, String> col_customer_ID;

    @FXML
    private TableColumn<Customers, String> col_customer_name;

    @FXML
    private TableColumn<Customers, String> col_customer_address;

    @FXML
    private TableColumn<Customers, String> col_customer_postal;

    @FXML
    private TableColumn<Customers, String> col_customer_phone;

    @FXML
    private TableColumn<Customers, String> col_customer_creationdate;

    @FXML
    private TableColumn<Customers, String> col_customer_createdby;

    @FXML
    private TableColumn<Customers, String> col_customer_lastupdated;

    @FXML
    private TableColumn<Customers, String> col_customer_lastupdatedby;

    @FXML
    private TableColumn<Customers, String> col_customer_divisionID;

    /**
     * Use Zone ID to set the location label.
     *
     */
    public void initialize() {
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
                alertTitle = "Erreur d'identification";
                alertHeader = "Erreur d'identification";
                alertContext = "Le nom d'utilisateur ou le mot de passe que vous avez tapé est incorrect.";
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

    /**
     * Application Methods
     */
    /**
     * Sign Out Button Event Handler - Close the application.
     *
     * @param event triggered by the sign out button
     */
    public void do_signout(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Show the Appointments Pane and hide the Customers Pane
     *
     * @param event triggered by the appointments button
     */
    public void do_appointments(ActionEvent event) {
        appointments_pane.setVisible(true);
        appointments_pane.setDisable(false);
        customers_pane.setVisible(false);
        customers_pane.setDisable(true);
    }

    /**
     * Show the Customers Pane and hide the Appointments Pane
     *
     * @param event triggered by the customers button
     */
    public void do_customers(ActionEvent event) {
        appointments_pane.setVisible(false);
        appointments_pane.setDisable(true);
        customers_pane.setVisible(true);
        customers_pane.setDisable(false);
    }

    /**
     * Show the Reports Pane and hide the Appointments and Customers Panes
     *
     * @param event triggered by the reports button
     */
    public void do_reports(ActionEvent event) {
        appointments_pane.setVisible(false);
        appointments_pane.setDisable(true);
        customers_pane.setVisible(false);
        customers_pane.setDisable(true);
    }

    /**
     * Open the Add Appointment window
     *
     * @param event triggered by the new appointment button
     */
    public void do_createappointment(ActionEvent event) {
    }

    /**
     * Open the Modify Selected Appointment window
     *
     * @param event triggered by the modify appointment button
     */
    public void do_modifyappointment(ActionEvent event) {
    }

    /**
     * Delete the selected appointment
     *
     * @param event triggered by the delete appointment button
     */
    public void do_deleteappointment(ActionEvent event) {
    }

    /**
     * Filter the appointments table by month or week radio buttons
     *
     * @param event triggered by the month or week radio buttons
     */
    public void do_filterweekly(ActionEvent event) {
    }

    public void do_filtermonthly(ActionEvent event) {
    }

    /**
     * Open the Add Customer window
     *
     * @param event triggered by the new customer button
     */
    public void do_createcustomer(ActionEvent event) {
    }

    /**
     * Open the Modify Selected Customer window
     *
     * @param event triggered by the modify customer button
     */
    public void do_updatecustomer(ActionEvent event) {
    }

    /**
     * Delete the selected customer
     *
     * @param event triggered by the delete customer button
     */
    public void do_deletecustomer(ActionEvent event) {
    }
}
