package main;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */

import javafx.fxml.FXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.models.Appointments;
import main.models.Contacts;
import main.utilities.JDBC;

import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class UpdateAppointment {
    @FXML
    private TextField modapp_appointmentID;
    @FXML
    private TextField modapp_title;
    @FXML
    private TextField modapp_description;
    @FXML
    private TextField modapp_location;
    @FXML
    private ChoiceBox modapp_contactbox;
    @FXML
    private TextField modapp_type;
    @FXML
    private DatePicker modapp_startDate;
    @FXML
    private DatePicker modapp_endDate;
    @FXML
    private ChoiceBox modapp_startTime;
    @FXML
    private ChoiceBox modapp_endTime;
    @FXML
    private TextField modapp_CustomerID;
    @FXML
    private TextField modapp_UserID;
    private Appointments appointment;

    public ObservableList<Appointments> appointments = FXCollections.observableArrayList();

    /**
     * Initialize the Update Appointment Form.
     *
     */
    public void initialize() throws SQLException {
        modapp_contactbox.getItems().clear();
        for (int i = 0; i < getAllContacts().size(); i++) {
            modapp_contactbox.getItems().add(getAllContacts().get(i).getContact_Name());
        }
    }

    /**
     * Cancel the appointment update
     *
     * @param event
     */
    public void do_modappCancel(ActionEvent event) {
        Stage stage = (Stage) modapp_title.getScene().getWindow(); // Get the stage
        stage.close(); // Close the stage
    }

    /**
     * Get the Contact ID from the contact name
     *
     * @param contact_Name
     * @return
     */
    public String getContactID(String contact_Name) throws SQLException {
        String contact_ID = ""; // Initialize the contact ID
        for (int i = 0; i < getAllContacts().size(); i++) { // Loop through all contacts
            if (getAllContacts().get(i).getContact_Name().equals(contact_Name)) { // If the contact name matches
                contact_ID = String.valueOf(getAllContacts().get(i).getContact_ID()); // Set the contact ID
            }
        } // end for loop
        return contact_ID;
    }

    /**
     * Update the appointment (Save)
     *
     * @param event triggered by the Update button
     */
    public void do_modappUpdate(ActionEvent event) {
        try {
            if (modapp_title.getText().isEmpty() || modapp_description.getText().isEmpty()
                    || modapp_location.getText().isEmpty() || modapp_type.getText().isEmpty()
                    || modapp_startDate.getValue() == null || modapp_endDate.getValue() == null
                    || modapp_startTime.getValue() == null || modapp_endTime.getValue() == null
                    || modapp_CustomerID.getText().isEmpty() || modapp_UserID.getText().isEmpty()
                    || modapp_contactbox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert
                alert.setTitle("Error"); // Set alert title
                alert.setHeaderText("Error"); // Set alert header
                alert.setContentText("Please fill out all fields."); // Set alert content
                alert.showAndWait(); // Show alert
            } else {
                String title = modapp_title.getText();
                String description = modapp_description.getText();
                String location = modapp_location.getText();
                String type = modapp_type.getText();
                String contact = getContactID(modapp_contactbox.getValue().toString()); // Get contact ID
                LocalTime localtimestart = LocalTime.parse(modapp_startTime.getValue().toString()); // Get start time
                LocalDateTime localdatetimestart = LocalDateTime.of(modapp_startDate.getValue(), localtimestart);
                Timestamp startTimeStamp = Timestamp.valueOf(localdatetimestart); // Convert to timestamp
                LocalTime localtimeend = LocalTime.parse(modapp_endTime.getValue().toString()); // Get end time
                LocalDateTime localdatetimeend = LocalDateTime.of(modapp_endDate.getValue(), localtimeend);
                Timestamp endTimeStamp = Timestamp.valueOf(localdatetimeend); // Convert to timestamp
                String customerID = modapp_CustomerID.getText();
                String userID = modapp_UserID.getText();

                Appointments appointment = new Appointments(0, title, description, location, type, startTimeStamp,
                        endTimeStamp, Timestamp.valueOf(LocalDateTime.now()), JDBC.getUserName(Main.loggedInUserID),
                        Timestamp.valueOf(LocalDateTime.now()), JDBC.getUserName(Main.loggedInUserID),
                        Integer.parseInt(customerID), Integer.parseInt(userID), Integer.parseInt(contact));

                if (validateUpdateToAppointment(appointment)) {
                    String query = "UPDATE client_schedule.appointments SET Title = ?, Description = ?, Location = ?, Type = ?, Start = ?, End = ?, Customer_ID = ?, User_ID = ?, Contact_ID = ? WHERE Appointment_ID = ?";
                    JDBC.makeConnection();
                    Connection connection = JDBC.connection;
                    PreparedStatement statement = connection.prepareStatement(query);
                    statement.setString(1, title);
                    statement.setString(2, description);
                    statement.setString(3, location);
                    statement.setString(4, type);
                    statement.setTimestamp(5, startTimeStamp);
                    statement.setTimestamp(6, endTimeStamp);
                    statement.setInt(7, Integer.parseInt(customerID));
                    statement.setInt(8, Integer.parseInt(userID));
                    statement.setInt(9, Integer.parseInt(contact));
                    statement.setInt(10, Integer.parseInt(modapp_appointmentID.getText()));
                    statement.executeUpdate();
                    Stage stage = (Stage) modapp_title.getScene().getWindow(); // Get the stage
                    stage.close(); // Close the stage
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

    }

    /**
     * ObservableList of All Appointments
     *
     * @param appointments
     */
    public void setAllAppointments(ObservableList<Appointments> appointments) {
        appointments = appointments;
    }

    /**
     * Set Individual Appointment
     *
     * @param appointment
     */
    public void updateAppointment(Appointments appointment) throws SQLException {
        this.appointment = appointment;
        populateExistingFields();
        addUpdateTime();
    }

    /**
     * Populate Existing Fields
     */
    public void populateExistingFields() throws SQLException {
        modapp_appointmentID.setText(String.valueOf(appointment.getAppointment_ID()));
        modapp_title.setText(appointment.getTitle());
        modapp_description.setText(appointment.getDescription());
        modapp_location.setText(appointment.getLocation());
        for (int i = 0; i < modapp_contactbox.getItems().size(); i++) {
            if (getAllContacts().get(i).getContact_ID() == appointment.getContact_ID()) {
                modapp_contactbox.getSelectionModel().select(getAllContacts().get(i).getContact_Name());
            }
        }
        modapp_type.setText(appointment.getType());
        modapp_startDate.setValue(appointment.getStart().toLocalDateTime().toLocalDate());
        modapp_endDate.setValue(appointment.getEnd().toLocalDateTime().toLocalDate());
        modapp_startTime.setValue(appointment.getStart().toLocalDateTime().toLocalTime());
        modapp_endTime.setValue(appointment.getEnd().toLocalDateTime().toLocalTime());
        modapp_CustomerID.setText(String.valueOf(appointment.getCustomer_ID()));
        modapp_UserID.setText(String.valueOf(appointment.getUser_ID()));
    }

    /**
     * Update Time
     */
    public void addUpdateTime() {
        ObservableList<LocalDateTime> startTimes = FXCollections.observableArrayList();
        ObservableList<LocalDateTime> endTimes = FXCollections.observableArrayList();
        LocalDateTime start = appointment.getStart().toLocalDateTime();
        LocalDateTime end = appointment.getEnd().toLocalDateTime();
        LocalDate startDate = appointment.getStart().toLocalDateTime().toLocalDate();
        LocalDate endDate = appointment.getEnd().toLocalDateTime().toLocalDate();

        for (int i = 8; i <= 22; i++) {
            for (int k = 0; k < 60; k += 15) {
                startTimes.add(LocalDateTime.of(startDate, LocalTime.of(i, k)));
                endTimes.add(LocalDateTime.of(endDate, LocalTime.of(i, k)));
            }
        }

        for (int i = 0; i < startTimes.size(); i++) {
            modapp_startTime.getItems()
                    .add(startTimes.get(i).atZone(ZoneId.of("US/Eastern")).withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }

        for (int i = 0; i < endTimes.size(); i++) {
            modapp_endTime.getItems()
                    .add(endTimes.get(i).atZone(ZoneId.of("US/Eastern")).withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
    }

    /**
     * Observable list of all contacts
     */
    public ObservableList<Contacts> getAllContacts() throws SQLException {
        JDBC.makeConnection(); // Connect to database
        Connection connection = JDBC.connection; // Get connection
        Statement statement = connection.createStatement(); // Create statement
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.contacts"); // Execute query
        ObservableList<Contacts> contacts = FXCollections.observableArrayList(); // Create observable list
        while (resultSet.next()) { // Loop through results
            contacts.add(new Contacts( // Add new contact to list
                    resultSet.getInt("Contact_ID"), // Contact ID
                    resultSet.getString("Contact_Name"), // Contact name
                    resultSet.getString("Email"))); // Contact email
        }
        return contacts; // returns the list of contacts
    }

    /**
     * Validate the appointment update
     *
     * @param appointment
     * @return
     */
    public boolean validateUpdateToAppointment(Appointments appointment) {
        AtomicBoolean isValid = new AtomicBoolean(true);
        try {
            String query = "SELECT * FROM client_schedule.appointments WHERE Customer_ID = ? and Appointment_ID != ?";
            JDBC.makeConnection();
            Connection connection = JDBC.connection;
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, modapp_CustomerID.getText());
            statement.setString(2, modapp_appointmentID.getText());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Timestamp start = resultSet.getTimestamp("Start"); // Get start time
                Timestamp end = resultSet.getTimestamp("End"); // Get end time
                if (appointment.getStart().before(end) && appointment.getEnd().after(start)) {
                    isValid.set(false); // Set valid to false
                    Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert
                    alert.setTitle("Error"); // Set alert title
                    alert.setHeaderText("Invalid Times"); // Set alert header
                    alert.setContentText("The appointment times conflict with another appointment."); // Set alert
                    alert.showAndWait(); // Show alert
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        /**
         * Verify the appointment is within business hours
         */
        switch (appointment.getStart().toLocalDateTime().getDayOfWeek()) {
            case MONDAY:
            case TUESDAY:
            case WEDNESDAY:
            case THURSDAY:
            case FRIDAY:
                if (appointment.getStart().toLocalDateTime().atZone(ZoneId.systemDefault())
                        .withZoneSameInstant(ZoneId.of("US/Eastern")).getHour() < 8
                        || appointment.getEnd().toLocalDateTime().atZone(ZoneId.systemDefault())
                                .withZoneSameInstant(ZoneId.of("US/Eastern")).getHour() > 22) {
                    isValid.set(false); // Set valid to false
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Appointment Time");
                    alert.setContentText("Start and End Times are outside 8:00 AM - 10:00 PM EST");
                    alert.showAndWait();
                }
                break;
            case SATURDAY:
            case SUNDAY:
                isValid.set(false); // Set valid to false
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Appointment Day");
                alert.setContentText("Appointments may only be scheduled on weekdays.");
                alert.showAndWait();
                break;
        }
        return isValid.get(); // Return valid
    }

}
