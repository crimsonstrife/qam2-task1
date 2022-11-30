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
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.Modality;

import java.net.URL;
import java.util.Optional;
import java.io.FileWriter;
import javafx.scene.control.*;
import main.JDBC;
import java.time.ZoneId;
import java.util.ResourceBundle;

/**
 * The Main controller.
 */
public class Main extends Application implements Initializable {

    /**
     * Set Application Variables
     *
     */
    private static String userLoggedIn = "";
    private static Integer userLoggedInID = null;
    private static String userZone = ZoneId.systemDefault().toString();
    private static String userLanguage = System.getProperty("user.language");

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
    private TitledPane appointments_pane;

    @FXML
    private TitledPane customers_pane;

    /**
     * Configure the Appointments Table
     */
    @FXML
    private TableView table_appointments;

    /**
     * Configure the Customers Table
     */
    @FXML
    private TableView table_customers;

    /**
     * Initialize method
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }

    public void start(Stage primaryStage) throws Exception {
        String appTitle = "";
        if (userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
        primaryStage.setTitle(appTitle);
        primaryStage.setScene(new Scene(root, 1920, 1080));
        primaryStage.show();
    }

    /**
     * Constructor
     */
    public Main() {
    }

    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
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
     * Load the Appointments Table with data from the database, sorted start date by
     * month.
     *
     * @param event
     */
    public void do_appointments_load(ActionEvent event) {
        table_appointments.setItems(JDBC.getAppointments("Month"));
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
    public void do_createCustomer(ActionEvent event) {
    }

    /**
     * Open the Modify Selected Customer window
     *
     * @param event triggered by the modify customer button
     */
    public void do_updateCustomer(ActionEvent event) {
    }

    /**
     * Delete the selected customer
     *
     * @param event triggered by the delete customer button
     */
    public void do_deleteCustomer(ActionEvent event) {
    }
}
