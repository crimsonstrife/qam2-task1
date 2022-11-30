package main;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
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
import java.security.Timestamp;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.util.Optional;
import java.beans.Statement;
import java.io.FileWriter;
import javafx.scene.control.*;
import main.JDBC;
import main.Utils;
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

    @FXML
    private RadioButton weekly_radio;

    @FXML
    private RadioButton monthly_radio;

    @FXML
    public ToggleGroup appointment_filter;

    public ObservableList<appointments> allAppointments = FXCollections.observableArrayList();

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
    @FXML
    private TableColumn col_appointment_ID;
    @FXML
    private TableColumn col_appointment_title;
    @FXML
    private TableColumn col_appointment_desc;
    @FXML
    private TableColumn col_appointment_location;
    @FXML
    private TableColumn col_appointment_contact;
    @FXML
    private TableColumn col_appointment_type;
    @FXML
    private TableColumn col_appointment_startdate;
    @FXML
    private TableColumn col_appointment_enddate;
    @FXML
    private TableColumn col_appointment_customerID;
    @FXML
    private TableColumn col_appointment_userID;

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
        do_appointments_load();
        appointments_pane.setVisible(true);
        appointments_pane.setDisable(false);
        customers_pane.setVisible(false);
        customers_pane.setDisable(true);
    }

    /**
     * Load the Appointments Table with data from the database, sorted start date by
     * week. Display the appointments in the table.
     *
     */
    public void do_appointments_load() {
        allAppointments = getAllAppointments();
        populateAppointments();
    }

    /**
     * Get all appointments from the database
     *
     * @return all appointments
     * @throws SQLException
     */
    public ObservableList<appointments> getAllAppointments() throws SQLException {
        allAppointments.clear();
        JDBC.makeConnection();
        Connection connection = JDBC.connection;

        Statement statement = (Statement) connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments");

        while (resultSet.next()) {
            int appointment_ID = resultSet.getInt("Appointment_ID");
            String appointment_title = resultSet.getString("Title");
            String appointment_desc = resultSet.getString("Description");
            String appointment_location = resultSet.getString("Location");
            int appointment_contact = resultSet.getInt("Contact_ID");
            String appointment_type = resultSet.getString("Type");
            Timestamp appointment_startdate = resultSet.getTimestamp("Start");
            Timestamp appointment_enddate = resultSet.getTimestamp("End");
            int appointment_customerID = resultSet.getInt("Customer_ID");
            int appointment_userID = resultSet.getInt("User_ID");

            allAppointments.add(new appointments(appointment_ID, appointment_title, appointment_desc,
                    appointment_location, appointment_contact, appointment_type, appointment_startdate,
                    appointment_enddate, appointment_customerID, appointment_userID));
        }
        return allAppointments;
    }

    /**
     * Method using a lambda stream to both populate the appointments table and
     * update the appointments
     *
     * @throws SQLException
     */
    public void populateAppointments() throws SQLException {
        getAllAppointments();
        allAppointments.stream().forEach(appointments -> {
            col_appointment_ID.setCellValueFactory(new PropertyValueFactory<>("appointment_ID"));
            col_appointment_title.setCellValueFactory(new PropertyValueFactory<>("appointment_title"));
            col_appointment_desc.setCellValueFactory(new PropertyValueFactory<>("appointment_desc"));
            col_appointment_location.setCellValueFactory(new PropertyValueFactory<>("appointment_location"));
            col_appointment_contact.setCellValueFactory(new PropertyValueFactory<>("appointment_contact"));
            col_appointment_type.setCellValueFactory(new PropertyValueFactory<>("appointment_type"));
            col_appointment_startdate.setCellValueFactory(new PropertyValueFactory<>("appointment_startdate"));
            col_appointment_enddate.setCellValueFactory(new PropertyValueFactory<>("appointment_enddate"));
            col_appointment_customerID.setCellValueFactory(new PropertyValueFactory<>("appointment_customerID"));
            col_appointment_userID.setCellValueFactory(new PropertyValueFactory<>("appointment_userID"));
            table_appointments.setItems(allAppointments);
        });
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
