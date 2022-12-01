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
import main.UpdateCustomer;
import main.UpdateAppointment;
import java.io.IOException;
import java.net.URL;
import java.sql.*;
import javafx.scene.control.*;
import main.models.Appointments;
import main.models.Customers;
import main.utilities.JDBC;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ResourceBundle;

/**
 * The Main controller.
 */
public class Main extends Application implements Initializable {

    /**
     * Set Application Variables
     */
    private static String userLoggedIn = "";
    private static Integer userLoggedInID = null;
    private static String userZone = ZoneId.systemDefault().toString();
    public static String userLanguage = System.getProperty("user.language");
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
     * Initialize method
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        try {
            if (checkAppointmentTimes() == true) {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Alert");
                alert.setHeaderText("Appointment Alert");
                alert.setContentText("There is an appointment due within the next 15 minutes.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Appointment Alert");
                alert.setHeaderText("Appointment Alert");
                alert.setContentText("There are no appointments due within the next 15 minutes.");
                alert.showAndWait();
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            populateAppointments();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public void start(Stage primaryStage) throws Exception {
        String appTitle = "";
        if (userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        Parent main = FXMLLoader.load(getClass().getResource("resources/views/main.fxml"));
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
     * Check appointment times for any occurring within the next 15 minutes.
     * return true if there is an appointment within the next 15 minutes.
     */
    public boolean checkAppointmentTimes() throws SQLException {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fifteenMinutesFromNow = now.plusMinutes(15);
        boolean appointmentWithin15Minutes = false;
        JDBC.makeConnection();
        Connection connection = JDBC.getConnection();
        Statement statement = (Statement) connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments");
        while (resultSet.next()) {
            LocalDateTime appointmentStart = resultSet.getTimestamp("start").toLocalDateTime();
            if (appointmentStart.isAfter(now) && appointmentStart.isBefore(fifteenMinutesFromNow)) {
                appointmentWithin15Minutes = true;
            } else {
                appointmentWithin15Minutes = false;
            }
        }
        return appointmentWithin15Minutes;
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
     * Show the Reports Pane and hide the Appointments
     *
     * @param event triggered by the reports button
     */
    public void do_reports(ActionEvent event) {
        appointments_pane.setVisible(false);
        appointments_pane.setDisable(true);
    }

    /**
     * Show the Customers Pane
     *
     * @param event triggered by the customers button
     */
    public void do_customers(ActionEvent event) throws IOException {
        try {
            Parent viewCustomers = FXMLLoader.load(getClass().getResource("resources/views/customers.fxml"));
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
            Parent createapp = FXMLLoader.load(getClass().getResource("resources/views/newAppointment.fxml"));
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
        if (table_appointments.getSelectionModel().getSelectedItem() != null) {
            Appointments selectedAppointment = (Appointments) table_appointments.getSelectionModel().getSelectedItem();
            int appointmentID = selectedAppointment.getAppointment_ID();
            String appointmentIDString = Integer.toString(appointmentID);
            String appointmentTypeString = selectedAppointment.getType();
            try {
                JDBC.makeConnection();
                Connection connection = JDBC.connection;
                Statement statement = (Statement) connection.createStatement();
                statement.executeUpdate(
                        "DELETE FROM client_schedule.appointments WHERE Appointment_ID = " + appointmentID);
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
        } else {
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
        if (table_appointments.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Appointment Selected");
            alert.setContentText("Please select an appointment to modify.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader appointmentLoader = new FXMLLoader(
                        getClass().getResource("resources/views/updateAppointment.fxml"));
                Parent modapp = appointmentLoader.load();
                UpdateAppointment updateAppointment = appointmentLoader.getController();
                updateAppointment
                        .updateAppointment((Appointments) table_appointments.getSelectionModel().getSelectedItem());
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
