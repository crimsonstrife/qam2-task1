package main;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */
import main.JDBC;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Countries;
import main.Customers;
import main.Divisions;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class UpdateCustomer {
    @FXML
    private TextField modcus_customerID;
    @FXML
    private TextField modcus_customerName;
    @FXML
    private TextField modcus_address;
    @FXML
    private TextField modcus_postalCode;
    @FXML
    private TextField modcus_phone;
    @FXML
    public ChoiceBox modcus_countryChoice;
    @FXML
    public ChoiceBox modcus_divisionLevel;
    public Customers customer;

    /**
     * Updates the customer and populates the fields
     *
     * @param customer
     */
    public void updateCustomer(Customers customer) {
        this.customer = customer;
        populateExistingFields();
    }

    /**
     * Cancels the Customer update
     *
     * @param event
     */
    public void do_modcusCancel(ActionEvent event) {
        Stage stage = (Stage) modcus_customerID.getScene().getWindow(); // Get the stage
        stage.close(); // Close the stage
    }

    /**
     * Populates the existing fields
     */
    public void populateExistingFields() {
        modcus_customerID.setText(String.valueOf(customer.getCustomerID()));
        modcus_customerName.setText(customer.getCustomer_Name());
        modcus_address.setText(customer.getAddress());
        modcus_postalCode.setText(customer.getPostal());
        modcus_phone.setText(customer.getPhone());
        modcus_countryChoice.getItems().clear();
        modcus_divisionLevel.getItems().clear();
        determineCountryAndDivision();
        modcus_countryChoice.getSelectionModel().select(get_Country().getCountry());
        modcus_divisionLevel.getSelectionModel().select(get_Division().getDivision());
    }

    /**
     * Determines the country and division
     */
    public void determineCountryAndDivision() {
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSetCountry = statement.executeQuery("SELECT * FROM client_schedule.countries");
            while (resultSetCountry.next()) {
                modcus_countryChoice.getItems().add(resultSetCountry.getString("Country"));
            }

            ResultSet resultSetDivision = statement
                    .executeQuery("SELECT * FROM client_schedule.first_level_divisions WHERE Country_ID = "
                            + get_Country().getCountry_ID());
            while (resultSetDivision.next()) {
                modcus_divisionLevel.getItems().add(resultSetDivision.getString("Division"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets the country
     *
     * @return country
     */
    public Countries get_Country() {
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSetCountry = statement
                    .executeQuery("SELECT * FROM client_schedule.first_level_divisions WHERE Division_ID = '"
                            + customer.getDivision_ID() + "'");
            resultSetCountry.next();
            int countryID = resultSetCountry.getInt("Country_ID");
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM client_schedule.countries WHERE Country_ID = '" + countryID + "'");
            resultSet.next();
            Countries country = new Countries(resultSet.getInt("Country_ID"), resultSet.getString("Country"));
            return country;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the Division
     *
     * @return division
     */
    public Divisions get_Division() {
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement
                    .executeQuery("SELECT * FROM client_schedule.first_level_divisions WHERE Division_ID = '"
                            + customer.getDivision_ID() + "'");
            resultSet.next();
            Divisions division = new Divisions(resultSet.getInt("Division_ID"), resultSet.getString("Division"),
                    resultSet.getInt("COUNTRY_ID"));
            return division;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the Division Level based on the Country selected
     *
     * @param event
     */
    public void do_modcusDivision(ActionEvent event) {
        modcus_divisionLevel.getItems().clear();
        try {
            JDBC.makeConnection();
            Connection connection = JDBC.getConnection();
            Statement statement = connection.createStatement();
            int selectedCountry = modcus_countryChoice.getSelectionModel().getSelectedIndex() + 1;
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM client_schedule.first_level_divisions WHERE COUNTRY_ID = " + selectedCountry);
            while (resultSet.next()) {
                modcus_divisionLevel.getItems().add(resultSet.getString("Division"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Save the new Customer
     *
     * @param event
     */
    public void do_modcusSave(ActionEvent event) {
        try {
            if (modcus_customerName.getText().isEmpty() || modcus_address.getText().isEmpty()
                    || modcus_postalCode.getText().isEmpty() || modcus_phone.getText().isEmpty()
                    || modcus_countryChoice.getSelectionModel().isEmpty()
                    || modcus_divisionLevel.getSelectionModel().isEmpty()) {
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
                                + modcus_divisionLevel.getValue() + "'");
                resultSet.next();
                int divisionID = resultSet.getInt("Division_ID");
                statement.executeUpdate("UPDATE client_schedule.customers SET Customer_Name = '"
                        + modcus_customerName.getText() + "', Address = '" + modcus_address.getText()
                        + "', Postal_Code = '"
                        + modcus_postalCode.getText() + "', Phone = '" + modcus_phone.getText() + "', Division_ID = '"
                        + getDivisionID() + "' WHERE Customer_ID = " + modcus_customer.getCustomerID());
                Stage stage = (Stage) modcus_customerID.getScene().getWindow(); // Get the stage
                stage.close(); // Close the stage
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
