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
import main.Main;
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
 * The CustomersView controller.
 *
 * Created this to abvoid a casting issue I was having in Main with both
 * appointments and customer updates
 */
public class CustomersView extends Application implements Initializable {
    @FXML
    private Button btn_signout;
    @FXML
    private TitledPane customers_pane;

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

    public ObservableList<Customers> allCustomers = FXCollections.observableArrayList();

    @Override
    public void start(Stage secondaryStage) throws Exception {
        String appTitle = "";
        if (Main.userLanguage.equals("fr")) {
            appTitle = "Planificateur";
        } else {
            appTitle = "Scheduler";
        }
        Parent customerview = FXMLLoader.load(getClass().getResource("resources/views/main.fxml"));
        secondaryStage.setTitle(appTitle);
        secondaryStage.setScene(new Scene(customerview, 900, 600));
        secondaryStage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            populateCustomers();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

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
     * Show the Customers Pane
     *
     * @param event triggered by the customers button
     */
    public void do_customers(ActionEvent event) throws SQLException {
        do_customers_load();
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
                Parent createcus = FXMLLoader.load(getClass().getResource("resources/views/updateCustomer.fxml"));
                // get the selection model from the table and get the selected item
                Customers selectedCustomer = table_customers.getSelectionModel().getSelectedItem();
                Stage stage = new Stage();
                stage.setTitle("Update Customer");
                stage.setScene(new Scene(createcus, 600, 312));
                stage.showAndWait();
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
