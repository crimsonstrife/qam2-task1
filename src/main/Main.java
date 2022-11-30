package main;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.fxml.*;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import javafx.scene.control.*;
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
    public static Integer loggedInUserID = null;

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

    public ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();

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
        primaryStage.setScene(new Scene(root, 900, 600));
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
    public void do_appointments(ActionEvent event) throws SQLException {
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
    public void do_appointments_load() throws SQLException {
        allAppointments = getAllAppointments();
        populateAppointments();
    }

    /**
     * Get all appointments from the database
     *
     * @return all appointments
     * @throws SQLException
     */
    public ObservableList<Appointments> getAllAppointments() throws SQLException {
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
            Timestamp appointment_createdate = resultSet.getTimestamp("Create_Date");
            String appointment_createdby = resultSet.getString("Created_By");
            Timestamp appointment_lastupdate = resultSet.getTimestamp("Last_Update");
            String appointment_updatedby = resultSet.getString("Last_Updated_By");
            int appointment_customerID = resultSet.getInt("Customer_ID");
            int appointment_userID = resultSet.getInt("User_ID");

            allAppointments.add(new Appointments(appointment_ID, appointment_title, appointment_desc,
                    appointment_location, appointment_type, appointment_startdate, appointment_enddate,
                    appointment_createdate, appointment_createdby, appointment_lastupdate, appointment_updatedby,
                    appointment_customerID, appointment_userID, appointment_contact));
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
            col_appointment_title.setCellValueFactory(new PropertyValueFactory<>("title"));
            col_appointment_desc.setCellValueFactory(new PropertyValueFactory<>("description"));
            col_appointment_location.setCellValueFactory(new PropertyValueFactory<>("location"));
            col_appointment_contact.setCellValueFactory(new PropertyValueFactory<>("contact_ID"));
            col_appointment_type.setCellValueFactory(new PropertyValueFactory<>("type"));
            col_appointment_startdate.setCellValueFactory(new PropertyValueFactory<>("start"));
            col_appointment_enddate.setCellValueFactory(new PropertyValueFactory<>("end"));
            col_appointment_customerID.setCellValueFactory(new PropertyValueFactory<>("customer_ID"));
            col_appointment_userID.setCellValueFactory(new PropertyValueFactory<>("user_ID"));
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
