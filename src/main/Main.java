package main;
/**
 * Main.java
 * This is the Main class.
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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import javafx.scene.control.*;
import main.models.Appointments;
import main.utilities.JDBC;
import main.utilities.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * The Main controller.
 * This class is the controller for the Main screen.
 */
public class Main extends Application implements Initializable {

    /**
     * Set Application Variables
     */
    private static String userZone = ZoneId.systemDefault().toString(); // This is the user's time zone.
    public static String userLanguage = System.getProperty("user.language"); // This is the user's language.
    public static Integer loggedInUserID = null; // This is the user ID that is logged in.

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
    private Button btn_appointmentrefresh;
    @FXML
    private RadioButton weekly_radio;
    @FXML
    private RadioButton monthly_radio;
    @FXML
    public ToggleGroup appointment_filter;
    public ObservableList<Appointments> allAppointments = FXCollections.observableArrayList(); // This is the list of all appointments.

    /**
     * Configure the Application Panes
     */
    @FXML
    private TitledPane appointments_pane;

    /**
     * Configure the Appointments Table
     */
    @FXML
    private TableView<Appointments> table_appointments;
    @FXML
    private TableColumn<Object, Object> col_appointment_ID;
    @FXML
    private TableColumn<Object, Object> col_appointment_title;
    @FXML
    private TableColumn<Object, Object> col_appointment_desc;
    @FXML
    private TableColumn<Object, Object> col_appointment_location;
    @FXML
    private TableColumn<Object, Object> col_appointment_contact;
    @FXML
    private TableColumn<Object, Object> col_appointment_type;
    @FXML
    private TableColumn<Object, Object> col_appointment_startdate;
    @FXML
    private TableColumn<Object, Object> col_appointment_enddate;
    @FXML
    private TableColumn<Object, Object> col_appointment_customerID;
    @FXML
    private TableColumn<Object, Object> col_appointment_userID;

    /**
     * Initialize method
     * This method is called when the Main screen is loaded.
     * 
     * @param url url
     * @param rb resource bundle
     *
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            // get the most recent username from the login_activity text file.
            String username = Utils.getLoginAttempt();
            // set the logged in user ID from the username
            loggedInUserID = JDBC.getUserID(username);
            if (checkAppointmentTimes()) { // Check if there are any appointments in the next 15 minutes.
                String contentText;
                Alert alert = new Alert(Alert.AlertType.INFORMATION); // Create an alert.
                alert.setTitle("Appointment Alert"); // Set the alert title.
                alert.setHeaderText("Appointment Alert"); // Set the alert header.
                // get the upcoming appointment and assign it to a variable.
                String[] upcomingAppointment = getUpcomingAppointment();
                // cycle through the upcoming appointment and assemble the message string.
                contentText = "There is an appointment due within the next 15 minutes.\n\n";
                for (String info : upcomingAppointment) {
                    contentText += info + "\n";
                }
                alert.setContentText(contentText); // Set the alert content.
                alert.showAndWait();
            } else { // If there are no appointments in the next 15 minutes.
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Alert");
                alert.setHeaderText("Appointment Alert");
                alert.setContentText("There are no appointments due within the next 15 minutes.");
                alert.showAndWait();
            }
        } catch (SQLException | FileNotFoundException throwables) {
            throwables.printStackTrace();
        }
        try {
            populateAppointments();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * Start method
     * 
     * @throws Exception exception
     */
    public void start(Stage primaryStage) throws Exception {
        String appTitle;
        if (userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        Parent main = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/views/main.fxml")));
        primaryStage.setTitle(appTitle);
        primaryStage.setScene(new Scene(main, 900, 600));
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
     * Check appointment times for any occurring within the next 15 minutes for the logged-in user.
     * return true if there is an appointment within the next 15 minutes.
     */
    public boolean checkAppointmentTimes() throws SQLException {
        LocalDateTime now = LocalDateTime.now(); // Get the current date and time.
        LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15); // Get the date and time 15 minutes from now.
        boolean appointmentWithin15Minutes = false; // Set the appointment within 15 minutes flag to false.
        JDBC.makeConnection(); // Make a connection to the database.
        Connection connection = JDBC.getConnection(); // Get the connection.
        Statement statement = connection.createStatement(); // Create a statement.
        // ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments WHERE User_ID = " + loggedInUserID); -- received clarification that this should be all appointments, not just the logged in user, which was actually easier.  Had issues tracking the user ID at initial login, subsequent logins without closing the app worked however.
        // Execute the query.
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments");
        while (resultSet.next()) {
            LocalDateTime appointmentStart = resultSet.getTimestamp("start").toLocalDateTime(); // Get the appointment start date and time.
            // If the appointment start date and time is after now and before 15 minutes from now.
            appointmentWithin15Minutes = appointmentStart.isAfter(now) && appointmentStart.isBefore(fifteenMinutesFromNow);
        }
        return appointmentWithin15Minutes;
    }

    /**
     * Get the upcoming appointment information. Return the three strings.
     *
     * @return array of the three strings
     */
    public String[] getUpcomingAppointment() throws SQLException {
        String[] upcomingAppointment = new String[4]; // Create an array of three strings.
        LocalDateTime now = LocalDateTime.now(); // Get the current date and time.
        LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15); // Get the date and time 15 minutes from now.
        boolean appointmentWithin15Minutes = false; // Set the appointment within 15 minutes flag to false.
        if (checkAppointmentTimes()) { // Check if there are any appointments in the next 15 minutes.
            appointmentWithin15Minutes = true; // Set the appointment within 15 minutes flag to true.
        } else { // If there are no appointments in the next 15 minutes.
            appointmentWithin15Minutes = false; // Set the appointment within 15 minutes flag to false.
        }
        if (appointmentWithin15Minutes == true) {
            JDBC.makeConnection(); // Make a connection to the database.
            Connection connection = JDBC.getConnection(); // Get the connection.
            Statement statement = connection.createStatement(); // Create a statement.
            // ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments WHERE User_ID = " + loggedInUserID); -- received clarification that this should be all appointments, not just the logged in user, which was actually easier.  Had issues tracking the user ID at initial login, subsequent logins without closing the app worked however.
            // Execute the query.
            ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments");
            while (resultSet.next()) {
                LocalDateTime appointmentStart = resultSet.getTimestamp("start").toLocalDateTime(); // Get the appointment start date and time.
                String appointmentID = String.valueOf(resultSet.getInt("Appointment_ID")); // Get the appointment ID as a string.
                // Get the Local Time as a string from the appointmentStart date and time.
                String appointmentStartTime = appointmentStart.toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm"));
                // Get the Local Date as a string from the appointmentStart date and time.
                String appointmentStartDate = appointmentStart.toLocalDate().toString();
                // Get the userID of the appointment
                Integer userID = resultSet.getInt("User_ID");
                // Get the username
                String appointmentUser = JDBC.getUserName(userID);
                // If the appointment start date and time is after now and before 15 minutes from now.
                if (appointmentStart.isAfter(now) && appointmentStart.isBefore(fifteenMinutesFromNow)) {
                    upcomingAppointment[0] = "Appointment ID: " + appointmentID; // Set the appointment ID string.
                    upcomingAppointment[1] = "Date: " + appointmentStartDate; // Set the appointment start date string.
                    upcomingAppointment[2] = "Time: " + appointmentStartTime; // Set the appointment start time string.
                    upcomingAppointment[3] = "User: " + appointmentUser;
                }
            }
        }
        return upcomingAppointment;
    }

    // Application Methods
    /**
     * Sign Out Button Event Handler - Close the application.
     *
     * @param event triggered by the sign-out button
     */
    public void do_signout(ActionEvent event) {
        Stage stage = (Stage) btn_signout.getScene().getWindow();
        stage.close();
    }

    /**
     * Show the Appointments Pane and hide the Customers Pane
     *
     * @param event triggered by the appointments button
     */
    public void do_appointments(ActionEvent event) throws SQLException {
        do_appointments_load();
    }

    /**
     * Load the Appointments Table with data from the database, sorted start date by
     * week. Display the appointments in the table.
     */
    public void do_appointments_load() throws SQLException {
        allAppointments = getAllAppointments();
        populateAppointments();
    }

    /**
     * Refresh the Appointments Table with data from the database, sorted start date by
     * week. Display the appointments in the table.
     */
    public void do_refreshappointments(ActionEvent event) throws SQLException {
        do_appointments_load();
    }

    /**
     * Get all appointments from the database
     *
     * @return all appointments
     * @throws SQLException if there is an error getting the appointments from the database
     */
    public ObservableList<Appointments> getAllAppointments() throws SQLException {
        allAppointments.clear();
        JDBC.makeConnection();
        Connection connection = JDBC.connection;

        Statement statement = connection.createStatement();
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
     * @throws SQLException if there is an error getting the appointments from the database
     */
    public void populateAppointments() throws SQLException {
        getAllAppointments();
        allAppointments.forEach(appointments -> {
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
     * Show the Reports Pane and hide the Appointments
     *
     * @param event triggered by the reports button
     */
    public void do_reports(ActionEvent event) {
        try {
            Parent viewReports = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/views/reports.fxml")));
            Stage viewReportsStage = new Stage();
            viewReportsStage.setTitle("Reports");
            viewReportsStage.setScene(new Scene(viewReports, 600, 481));
            viewReportsStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Show the Customers Pane
     *
     * @param event triggered by the customers button
     */
    public void do_customers(ActionEvent event) throws IOException {
        try {
            Parent viewCustomers = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/views/customers.fxml")));
            Stage viewCustomerStage = new Stage();
            viewCustomerStage.setTitle("Customers");
            viewCustomerStage.setScene(new Scene(viewCustomers, 600, 400));
            viewCustomerStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the Add Appointment window
     *
     * @param event triggered by the new appointment button
     */
    public void do_createappointment(ActionEvent event) {
        try {
            Parent createapp = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("resources/views/newAppointment.fxml")));
            Stage createAppstage = new Stage();
            createAppstage.setTitle("Add Appointment");
            createAppstage.setScene(new Scene(createapp, 600, 400));
            createAppstage.showAndWait();
            populateAppointments();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Delete the selected appointment
     *
     * @param event triggered by the delete appointment button
     */
    public void do_deleteappointment(ActionEvent event) {
        if (table_appointments.getSelectionModel().getSelectedItem() != null) { // if an appointment is selected
            Appointments selectedAppointment = table_appointments.getSelectionModel().getSelectedItem(); // get the selected appointment
            int appointmentID = selectedAppointment.getAppointment_ID(); // get the appointment ID
            String appointmentIDString = Integer.toString(appointmentID); // convert the appointment ID to a string
            String appointmentTypeString = selectedAppointment.getType(); // get the appointment type
            try {
                JDBC.makeConnection(); // make a connection to the database
                Connection connection = JDBC.connection; // get the connection
                Statement statement = connection.createStatement(); // create a statement
                statement.executeUpdate(
                        "DELETE FROM client_schedule.appointments WHERE Appointment_ID = " + appointmentID); // delete the appointment from the database
                populateAppointments();
                // prepare and alert to notify the user that the appointment has been deleted
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Deleted");
                alert.setHeaderText("Appointment Deleted");
                alert.setContentText("The " + appointmentTypeString + " appointment with ID: " + appointmentIDString
                        + " has been deleted.");
                alert.showAndWait();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        } else { // if no appointment is selected
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an appointment to delete.");
            alert.showAndWait();
        }
    }

    /**
     * Filter the appointments table by month or week radio buttons
     *
     * @param event triggered by the month or week radio buttons
     */
    // Lambda to filter the appointments table by week
    public void do_filterweekly(ActionEvent event) {
        ObservableList<Appointments> weekFilteredAppointments = FXCollections.observableArrayList();
        LocalDateTime current = LocalDateTime.now().minusWeeks(1);
        LocalDateTime next = LocalDateTime.now().plusWeeks(1);
        allAppointments.forEach(appointment -> {
            if (appointment.getStart().toLocalDateTime().isAfter(current)
                    && appointment.getStart().toLocalDateTime().isBefore(next)) {
                weekFilteredAppointments.add(appointment);
            }
        });
        table_appointments.setItems(weekFilteredAppointments);
    }

    // Lambda to filter the appointments table by month
    public void do_filtermonthly(ActionEvent event) {
        ObservableList<Appointments> monthFilteredAppointments = FXCollections.observableArrayList();
        LocalDateTime current = LocalDateTime.now().minusMonths(1);
        LocalDateTime next = LocalDateTime.now().plusMonths(1);
        allAppointments.forEach(appointment -> {
            if (appointment.getStart().toLocalDateTime().isAfter(current)
                    && appointment.getStart().toLocalDateTime().isBefore(next)) {
                monthFilteredAppointments.add(appointment);
            }
        });
        table_appointments.setItems(monthFilteredAppointments);
    }

    /**
     * Open the Modify Selected Appointment window
     *
     * @param event triggered by the modify appointment button
     */
    public void do_modifyappointment(ActionEvent event) {
        if (table_appointments.getSelectionModel().getSelectedItem() == null) { // if no appointment is selected
            Alert alert = new Alert(Alert.AlertType.ERROR); // prepare an alert to notify the user
            alert.setTitle("Error");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an appointment to modify.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader appointmentLoader = new FXMLLoader( // load the modify appointment window
                        getClass().getResource("resources/views/updateAppointment.fxml"));
                Parent modapp = appointmentLoader.load();
                UpdateAppointment updateAppointment = appointmentLoader.getController();
                updateAppointment
                        .updateAppointment(table_appointments.getSelectionModel().getSelectedItem());
                updateAppointment.setAllAppointments(allAppointments);
                Stage modifyAppstage = new Stage();
                modifyAppstage.setTitle("Update Appointment");
                modifyAppstage.setScene(new Scene(modapp, 600, 400));
                modifyAppstage.showAndWait();
                populateAppointments();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
