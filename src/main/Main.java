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
    public ObservableList<Customers> allCustomers = FXCollections.observableArrayList();

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
    @FXML
    private TableColumn col_customer_ID;
    @FXML
    private TableColumn col_customer_name;
    @FXML
    private TableColumn col_customer_address;
    @FXML
    private TableColumn col_customer_postal;
    @FXML
    private TableColumn col_customer_phone;
    @FXML
    private TableColumn col_customer_divisionID;
    @FXML
    private TableColumn col_customer_creationdate;
    @FXML
    private TableColumn col_customer_createdby;
    @FXML
    private TableColumn col_customer_lastupdated;
    @FXML
    private TableColumn col_customer_lastupdatedby;

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
        appointments_pane.setVisible(true);
        appointments_pane.setDisable(false);
        customers_pane.setVisible(false);
        customers_pane.setDisable(true);
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
     * Show the Customers Pane and hide the Appointments Pane
     *
     * @param event triggered by the customers button
     */
    public void do_customers(ActionEvent event) throws SQLException {
        do_customers_load();
        appointments_pane.setVisible(false);
        appointments_pane.setDisable(true);
        customers_pane.setVisible(true);
        customers_pane.setDisable(false);
    }

    /**
     * Load the Customers Table with data from the database.
     * Display the appointments in the table.
     */
    public void do_customers_load() throws SQLException {
        allCustomers = getAllCustomers();
        populateCustomers();
    }

    /**
     * Get all customers from the database
     *
     * @return all customers
     * @throws SQLException
     */
    public ObservableList<Customers> getAllCustomers() throws SQLException {
        allCustomers.clear();
        JDBC.makeConnection();
        Connection connection = JDBC.connection;

        Statement statement = (Statement) connection.createStatement();
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.customers");

        while (resultSet.next()) {
            int customer_ID = resultSet.getInt("Customer_ID");
            String customer_name = resultSet.getString("Customer_Name");
            String customer_address = resultSet.getString("Address");
            String customer_postal = resultSet.getString("Postal_Code");
            String customer_phone = resultSet.getString("Phone");
            Timestamp customer_createdate = resultSet.getTimestamp("Create_Date");
            String customer_createdby = resultSet.getString("Created_By");
            Timestamp customer_lastupdate = resultSet.getTimestamp("Last_Update");
            String customer_updatedby = resultSet.getString("Last_Updated_By");
            int customer_divisionID = resultSet.getInt("Division_ID");

            allCustomers.add(new Customers(customer_ID, customer_name, customer_address, customer_postal,
                    customer_phone, customer_divisionID, customer_createdate, customer_createdby, customer_lastupdate,
                    customer_updatedby));
        }
        return allCustomers;
    }

    /**
     * Method using a lambda stream to both populate the customers table and
     * update the customers accurately
     *
     * @throws SQLException
     */
    public void populateCustomers() throws SQLException {
        getAllCustomers();
        allCustomers.stream().forEach(customers -> {
            col_customer_ID.setCellValueFactory(new PropertyValueFactory<>("Customer_ID"));
            col_customer_name.setCellValueFactory(new PropertyValueFactory<>("Customer_Name"));
            col_customer_address.setCellValueFactory(new PropertyValueFactory<>("Address"));
            col_customer_postal.setCellValueFactory(new PropertyValueFactory<>("Postal"));
            col_customer_phone.setCellValueFactory(new PropertyValueFactory<>("Phone"));
            col_customer_divisionID.setCellValueFactory(new PropertyValueFactory<>("Division_ID"));
            col_customer_creationdate.setCellValueFactory(new PropertyValueFactory<>("Created_Date"));
            col_customer_createdby.setCellValueFactory(new PropertyValueFactory<>("Created_By"));
            col_customer_lastupdated.setCellValueFactory(new PropertyValueFactory<>("Updated_Date"));
            col_customer_lastupdatedby.setCellValueFactory(new PropertyValueFactory<>("Updated_By"));
            table_customers.setItems(allCustomers);
        });
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
                FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/views/updateAppointment.fxml"));
                Parent modapp = loader.load();
                UpdateAppointment updateAppointment = loader.getController();
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
     * Open the Add Customer window
     *
     * @param event triggered by the new customer button
     */
    public void do_createCustomer(ActionEvent event) {
        try {
            Parent createcus = FXMLLoader.load(getClass().getResource("resources/views/newCustomer.fxml"));
            Stage stage = new Stage();
            stage.setTitle("Add a Customer");
            stage.setScene(new Scene(createcus, 600, 312));
            stage.showAndWait();
            populateCustomers();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Open the Modify Selected Customer window
     * Use selected customer to populate the fields
     *
     * @param event triggered by the modify customer button
     */
    public void do_updateCustomer(ActionEvent event) {
        if (table_customers.getSelectionModel().getSelectedItem() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer to modify.");
            alert.showAndWait();
        } else {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("resources/views/updateAppointment.fxml"));
                Parent modcus = loader.load();
                UpdateCustomer updateCustomer = loader.getController();
                updateCustomer.updateCustomer((Customers) table_customers.getSelectionModel().getSelectedItem());
                Stage updateCustomerstage = new Stage();
                updateCustomerstage.setTitle("Update Customer");
                updateCustomerstage.setScene(new Scene(modcus, 600, 312));
                updateCustomerstage.showAndWait();
                populateCustomers();
            } catch (IOException | SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Determine if the selected customer has any appointments and return the
     * amount.
     *
     * @param customerID the customer ID to check
     * @return the number of appointments the customer has
     */
    public int getCustomerAppointmetCount(int customerID) {
        int appointmentCount = 0;
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.connection;
            Statement statement = (Statement) connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT COUNT(*) FROM client_schedule.appointments WHERE Customer_ID = " + customerID);
            while (resultSet.next()) {
                appointmentCount = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointmentCount;
    }

    /**
     * Delete the selected customer
     * Ensure that the customer has no remaining appointments before deleting
     *
     * @param event triggered by the delete customer button
     */
    public void do_deleteCustomer(ActionEvent event) {
        if (table_customers.getSelectionModel().getSelectedItem() != null) {
            Customers selectedCustomer = (Customers) table_customers.getSelectionModel().getSelectedItem();
            int selectedCustomerID = selectedCustomer.getCustomer_ID();
            String customerIDString = Integer.toString(selectedCustomerID);
            String customerNameString = selectedCustomer.getCustomer_Name();
            if (getCustomerAppointmetCount(selectedCustomerID) > 0) {
                String appointmentCountString = Integer.toString(getCustomerAppointmetCount(selectedCustomerID));
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Customer has remaining Appointments");
                alert.setContentText("The customer " + customerNameString + " with ID: " + customerIDString
                        + " has " + appointmentCountString + " remaining appointments and cannot be deleted.");
                alert.showAndWait();
            } else {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Are you sure you want to delete customer: " + customerNameString + "?");
                confirm.setHeaderText(null);
                confirm.setContentText("Please confirm you want to delete this customer.");
                confirm.showAndWait();
                if (confirm.getResult().getText().equals("OK")) {
                    try {
                        JDBC.makeConnection();
                        Connection connection = JDBC.connection;
                        Statement statement = (Statement) connection.createStatement();
                        statement.executeUpdate(
                                "DELETE FROM client_schedule.customers WHERE Customer_ID = " + selectedCustomerID);
                        populateCustomers();
                        // prepare and alert to notify the user that the customer has been deleted
                        Alert inform = new Alert(Alert.AlertType.INFORMATION);
                        inform.setTitle("Customer Deleted");
                        inform.setHeaderText("Customer Deleted");
                        inform.setContentText("The customer " + customerNameString + " with ID: " + customerIDString
                                + " has been deleted.");
                        inform.showAndWait();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No Customer Selected");
            alert.setContentText("Please select a customer to delete.");
            alert.showAndWait();
        }
    }
}
