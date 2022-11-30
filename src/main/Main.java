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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import javafx.scene.control.*;
import java.time.ZoneId;
import java.util.Observable;
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
        try {
            Parent root = FXMLLoader.load(getClass().getResource("newAppointment.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add Appointment");
            stage.setScene(new Scene(root, 600, 400));
            stage.showAndWait();
            populateAppointments();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("updateAppointment.fxml"));
                Parent root = loader.load();
                UpdateAppointment controller = loader.getController();
                controller.updateAppointment((Appointments) table_appointments.getSelectionModel().getSelectedItem());
                controller.setAllAppointments(allAppointments);
                Stage stage = new Stage();
                stage.setTitle("Update Appointment");
                stage.setScene(new Scene(root, 600, 400));
                stage.showAndWait();
                populateAppointments();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
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
        allAppointments.foreach(appointment -> {
            if (appointment.getStart().isAfter(current) && appointment.getStart().isBefore(next)) {
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
        allAppointments.foreach(appointment -> {
            if (appointment.getStart().isAfter(current) && appointment.getStart().isBefore(next)) {
                monthFilteredAppointments.add(appointment);
            }
        });
        table_appointments.setItems(monthFilteredAppointments);
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
