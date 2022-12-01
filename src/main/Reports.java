package main;
/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.application.Application;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.stage.Stage;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.models.Appointments;
import main.models.Customers;
import main.models.Types;
import main.utilities.JDBC;
import main.models.Contacts;
/**
 * The Reports controller.
 */
public class Reports extends Application implements Initializable {
    public ObservableList<Appointments> allAppointments = FXCollections.observableArrayList();
    public ObservableList<Contacts> allContacts = FXCollections.observableArrayList();
    @FXML
    public ComboBox ContactBox;
    @FXML public TableView table_reports;

    /**
     *
     * @throws SQLException
     */
    public void getAllAppointments() throws SQLException {
        String query = "SELECT * FROM client_schedule.appointments";
        JDBC.makeConnection();
        Connection connection = JDBC.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            allAppointments.add(new Appointments(
                    resultSet.getInt("Appointment_ID"),
                    resultSet.getString("Title"),
                    resultSet.getString("Description"),
                    resultSet.getString("Location"),
                    resultSet.getString("Type"),
                    resultSet.getTimestamp("Start"),
                    resultSet.getTimestamp("End"),
                    resultSet.getTimestamp("Create_Date"),
                    resultSet.getString("Created_By"),
                    resultSet.getTimestamp("Last_Update"),
                    resultSet.getString("Last_Updated_By"),
                    resultSet.getInt("Customer_ID"),
                    resultSet.getInt("User_ID"),
                    resultSet.getInt("Contact_ID")
            ));
        }
    }

    /**
     * Get all contacts
     * @throws SQLException
     */
    public void getAllContacts() throws SQLException {
        String query = "SELECT * FROM client_schedule.contacts";
        JDBC.makeConnection();
        Connection connection = JDBC.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        while (resultSet.next()) {
            allContacts.add(new Contacts(
                    resultSet.getInt("Contact_ID"),
                    resultSet.getString("Contact_Name"),
                    resultSet.getString("Email")
            ));
        }
    }

    /**
     * Fill the contact box with list of contacts
     * @throws SQLException
     */
    public void populateContacts() throws SQLException {
        for (Contacts contact : allContacts) {
            ContactBox.getItems().add(contact.getContact_Name());
        }
    }

    @Override
    public void start(Stage stage) throws Exception {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            getAllAppointments();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            getAllContacts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        try {
            populateContacts();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     *
     * @param actionEvent
     */
    public void do_ContactSchedule(ActionEvent actionEvent) {
        table_reports.getColumns().clear();
        TableColumn<Appointments, String> appointmentID = new TableColumn<>("Appointment_ID");
        TableColumn<Appointments, String> title = new TableColumn<>("Title");
        TableColumn<Appointments, String> description = new TableColumn<>("Description");
        TableColumn<Appointments, String> location = new TableColumn<>("Location");
        TableColumn<Appointments, String> type = new TableColumn<>("Type");
        TableColumn<Appointments, String> start = new TableColumn<>("Start");
        TableColumn<Appointments, String> end = new TableColumn<>("End");
        TableColumn<Appointments, String> customer_ID = new TableColumn<>("Customer_ID");
        TableColumn<Appointments, String> user_ID = new TableColumn<>("User_ID");

        appointmentID.setCellValueFactory(new PropertyValueFactory<>("appointmentID"));
        description.setCellValueFactory(new PropertyValueFactory<>("Description"));
        location.setCellValueFactory(new PropertyValueFactory<>("Location"));
        type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        start.setCellValueFactory(new PropertyValueFactory<>("Start"));
        end.setCellValueFactory(new PropertyValueFactory<>("End"));
        customer_ID.setCellValueFactory(new PropertyValueFactory<>("customer_ID"));
        user_ID.setCellValueFactory(new PropertyValueFactory<>("user_ID"));
        title.setCellValueFactory(new PropertyValueFactory<>("Title"));

        table_reports.getColumns().addAll(title, description, location, type, start, end, customer_ID, user_ID);
        table_reports.getItems().clear();
        for (Appointments appointment : allAppointments) {
            if (appointment.getContact_ID() == ContactBox.getSelectionModel().getSelectedIndex() + 1) {
                table_reports.getItems().add(appointment);
            }
        }

    }

    /**
     *
     * @param actionEvent
     * @throws SQLException
     */
    public void do_AppointmentsBy(ActionEvent actionEvent) throws SQLException {
        table_reports.getColumns().clear();
        TableColumn<Types, String> type = new TableColumn<>("Type");
        TableColumn<Types, String> Month = new TableColumn<>("Month");
        TableColumn<Types, String> count = new TableColumn<>("Count");

        type.setCellValueFactory(new PropertyValueFactory<>("Type"));
        Month.setCellValueFactory(new PropertyValueFactory<>("Month"));
        count.setCellValueFactory(new PropertyValueFactory<>("Count"));

        table_reports.getColumns().addAll(type, Month, count);

        table_reports.getItems().clear();

        ObservableList<Types> allTypes = FXCollections.observableArrayList();

        String query = "SELECT Type, MONTHNAME(Start) AS Month, COUNT(*) AS Count FROM client_schedule.appointments GROUP BY Type, MONTHNAME(Start)";
        JDBC.makeConnection();
        Connection connection = JDBC.connection;
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);

        while (resultSet.next()) {
            allTypes.add(new Types(
                    resultSet.getString("Type"),
                    resultSet.getString("Month"),
                    resultSet.getInt("Count")
            ));
        }
        table_reports.getItems().addAll(allTypes);
    }

    public void do_Quantity(ActionEvent actionEvent) {
    }
}
