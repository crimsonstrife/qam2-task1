package main;
/**
 * newCustomer.java
 * Purpose: This class is used to create a new Customer.
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.event.ActionEvent;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.utilities.JDBC;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class newCustomer {
    @FXML
    private TextField newcus_customerID;
    @FXML
    private TextField newcus_customerName;
    @FXML
    private TextField newcus_address;
    @FXML
    private TextField newcus_postalCode;
    @FXML
    private TextField newcus_phone;
    @FXML
    public ChoiceBox<String> newcus_countryChoice;
    @FXML
    public ChoiceBox<String> newcus_divisionLevel;
    public Integer newcus_userID = Main.loggedInUserID;

    /**
     * Initializes the controller class.
     */
    public void initialize() {
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.countries");
            while (resultSet.next()) {
                newcus_countryChoice.getItems().add(resultSet.getString("Country"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Cancel the Customer creation
     *
     * @param event the customer creation is cancelled
     */
    public void do_newcusCancel(ActionEvent event) {
        Stage stage = (Stage) newcus_customerID.getScene().getWindow(); // Get the stage
        stage.close(); // Close the stage
    }

    /**
     * Set the Division Level based on the Country selected
     *
     * @param event the country is selected
     */
    public void do_newcusDivision(ActionEvent event) {
        newcus_divisionLevel.getItems().clear();
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            int selectedCountry = newcus_countryChoice.getSelectionModel().getSelectedIndex() + 1;
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM client_schedule.first_level_divisions WHERE COUNTRY_ID = " + selectedCountry);
            while (resultSet.next()) {
                newcus_divisionLevel.getItems().add(resultSet.getString("Division"));
            }
            JDBC.closeConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the new Customer
     *
     * @param event the save button is clicked
     */
    public void do_newcusSave(ActionEvent event) {
        try {
            if (newcus_customerName.getText().isEmpty() || newcus_address.getText().isEmpty()
                    || newcus_postalCode.getText().isEmpty() || newcus_phone.getText().isEmpty()
                    || newcus_countryChoice.getSelectionModel().isEmpty()
                    || newcus_divisionLevel.getSelectionModel().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert
                alert.setTitle("Error"); // Set alert title
                alert.setHeaderText("Error"); // Set alert header
                alert.setContentText("Please fill out all fields."); // Set alert content
                alert.showAndWait(); // Show alert
            } else {
                JDBC.makeConnection();
                Connection connection = JDBC.getConnection();
                Statement statement = connection.createStatement();
                ResultSet resultSet = statement
                        .executeQuery("SELECT * FROM client_schedule.first_level_divisions WHERE Division = '"
                                + newcus_divisionLevel.getValue() + "'");
                resultSet.next();
                int divisionID = resultSet.getInt("Division_ID");
                statement.executeUpdate(
                        "INSERT INTO client_schedule.customers (Customer_Name, Address, Postal_Code, Phone, Create_Date, Created_By, Last_Update, Last_Updated_By, Division_ID) VALUES ('"
                                + newcus_customerName.getText() + "', '" + newcus_address.getText() + "', '"
                                + newcus_postalCode.getText() + "', '" + newcus_phone.getText() + "', NOW(), '"
                                + newcus_userID + "', NOW(), '" + newcus_userID + "', '" + divisionID + "')");
                Stage stage = (Stage) newcus_customerID.getScene().getWindow(); // Get the stage
                stage.close(); // Close the stage
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
