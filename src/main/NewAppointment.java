package main;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */

import main.JDBC;
import main.Utils;
import main.Main;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Appointments;
import main.Contacts;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicBoolean;

public class NewAppointment {
    public TextField newapp_title;
    public TextField newapp_description;
    public TextField newapp_location;
    public TextField newapp_type;
    public DatePicker newapp_startDate;
    public DatePicker newapp_endDate;
    public ChoiceBox newapp_startTime;
    public ChoiceBox newapp_endTime;
    public TextField newapp_customerID;
    public TextField newapp_userID;
    public ChoiceBox newapp_contactbox;
    public ObservableList<Appointments> appointments = FXCollections.observableArrayList();

    /**
     * Oberverable list of all appointments
     *
     * @return
     */
    public ObservableList<Appointments> getAllAppointments() throws SQLException {
        JDBC.makeConnection(); // Connect to database
        Connection connection = JDBC.connection; // Get connection
        Statement statement = connection.createStatement(); // Create statement
        ResultSet resultSet = statement.executeQuery("SELECT * FROM client_schedule.appointments"); // Execute query
        ObservableList<Appointments> appointments = FXCollections.observableArrayList(); // Create observable list
        while (resultSet.next()) { // Loop through results
            ZoneId zoneId = ZoneId.systemDefault(); // Get system timezone
            appointments.add(new Appointments( // Add appointment to list
                    resultSet.getInt("Appointment_ID"), // Appointment ID
                    resultSet.getString("Title"), // Title
                    resultSet.getString("Description"), // Description
                    resultSet.getString("Location"), // Location
                    resultSet.getString("Type"), // Type
                    resultSet.getTimestamp("Start"), // Start
                    resultSet.getTimestamp("End"), // End
                    resultSet.getTimestamp("Create_Date"), // Create date
                    resultSet.getString("Created_By"), // Created by
                    resultSet.getTimestamp("Last_Update"), // Last update
                    resultSet.getString("Last_Updated_By"), // Last update by
                    resultSet.getInt("Customer_ID"), // Customer ID
                    resultSet.getInt("User_ID"), // User ID
                    resultSet.getInt("Contact_ID"))); // Contact ID
        }
        return appointments;
    }

    /**
     * Observable list of all contacts
     *
     * @return
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
     * Initialize the Appointment Creation Form
     *
     */
    public void initialize() throws SQLException {
        appointments.clear(); // Clear the appointments list
        appointments = getAllAppointments(); // Get all appointments
        newapp_contactbox.getItems().clear(); // clears the choice box
        for (int i = 0; i < getAllContacts().size(); i++) {
            newapp_contactbox.getItems().add(getAllContacts().get(i).getContact_Name()); // contacts to the choice box
        } // end for loop
    }

    /**
     * Cancel the appointment creation
     *
     * @param event
     */
    public void do_newappCancel(ActionEvent event) {
        Stage stage = (Stage) newapp_title.getScene().getWindow(); // Get the stage
        stage.close(); // Close the stage
    }

    /**
     * Save the new appointment
     *
     * @param event triggered by the save button
     */
    public void do_newappSave(ActionEvent event) {
        try {
            if (newapp_title.getText().isEmpty() || newapp_description.getText().isEmpty()
                    || newapp_location.getText().isEmpty() || newapp_type.getText().isEmpty()
                    || newapp_startDate.getValue() == null || newapp_endDate.getValue() == null
                    || newapp_startTime.getValue() == null || newapp_endTime.getValue() == null
                    || newapp_customerID.getText().isEmpty() || newapp_userID.getText().isEmpty()
                    || newapp_contactbox.getValue() == null) {
                Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert
                alert.setTitle("Error"); // Set alert title
                alert.setHeaderText("Error"); // Set alert header
                alert.setContentText("Please fill out all fields."); // Set alert content
                alert.showAndWait(); // Show alert
            } else {
                String title = newapp_title.getText(); // Get title
                String description = newapp_description.getText(); // Get description
                String location = newapp_location.getText(); // Get location
                String type = newapp_type.getText(); // Get type
                String contact = getContactID(newapp_contactbox.getValue().toString()); // Get contact ID
                LocalTime localtimestart = LocalTime.parse(newapp_startTime.getValue().toString()); // Get start time
                LocalDateTime localdatetimestart = LocalDateTime.of(newapp_startDate.getValue(), localtimestart);
                Timestamp startTimeStamp = Timestamp.valueOf(localdatetimestart); // Convert to timestamp
                LocalTime localtimeend = LocalTime.parse(newapp_endTime.getValue().toString()); // Get end time
                LocalDateTime localdatetimeend = LocalDateTime.of(newapp_endDate.getValue(), localtimeend);
                Timestamp endTimeStamp = Timestamp.valueOf(localdatetimeend); // Convert to timestamp
                String customerID = newapp_customerID.getText(); // Get customer ID
                String userID = newapp_userID.getText(); // Get user ID

                Appointments appointment = new Appointments(0, title, description, location, type, startTimeStamp,
                        endTimeStamp, Timestamp.valueOf(LocalDateTime.now()), JDBC.getUserName(Main.loggedInUserID),
                        Timestamp.valueOf(LocalDateTime.now()), JDBC.getUserName(Main.loggedInUserID),
                        Integer.parseInt(customerID), Integer.parseInt(userID), Integer.parseInt(contact));

                if (validateNewAppointment(appointment)) {
                    String query = "INSERT INTO client_schedule.appointments (Title, Description, Location, Type, Start, End, Create_Date, Created_By, Last_Update, Last_Update_By, Customer_ID, User_ID, Contact_ID) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                    JDBC.makeConnection(); // Connect to database
                    Connection connection = JDBC.connection; // Get connection
                    PreparedStatement statement = connection.prepareStatement(query); // Create statement
                    statement.setString(1, title); // Set title
                    statement.setString(2, description); // Set description
                    statement.setString(3, location); // Set location
                    statement.setString(4, type); // Set type
                    statement.setTimestamp(5, startTimeStamp); // Set start time
                    statement.setTimestamp(6, endTimeStamp); // Set end time
                    statement.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now())); // Set create date
                    statement.setString(8, JDBC.getUserName(Main.loggedInUserID)); // Set created by
                    statement.setTimestamp(9, Timestamp.valueOf(LocalDateTime.now())); // Set last update
                    statement.setString(10, JDBC.getUserName(Main.loggedInUserID)); // Set last update by
                    statement.setInt(11, Integer.parseInt(customerID)); // Set customer ID
                    statement.setInt(12, Integer.parseInt(userID)); // Set user ID
                    statement.setInt(13, Integer.parseInt(contact)); // Set contact ID
                    statement.executeUpdate(); // Execute statement
                    Stage stage = (Stage) this.newapp_title.getScene().getWindow(); // Get stage
                    stage.close(); // Close stage
                }
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handle the start date and time
     *
     * @param event
     */
    public void do_StartTime(ActionEvent event) {
        newapp_startTime.disableProperty().setValue(false); // Enable the start time
        ObservableList<LocalDateTime> times = FXCollections.observableArrayList(); // Create observable list
        LocalDate startDate = newapp_startDate.getValue(); // Get start date
        for (int i = 8; i < 22; i++) {
            // add times in 15 minute increments
            times.add(LocalDateTime.of(startDate, LocalTime.of(i, 0))); // Add times to the list
            times.add(LocalDateTime.of(startDate, LocalTime.of(i, 15))); // Add times to the list
            times.add(LocalDateTime.of(startDate, LocalTime.of(i, 30))); // Add times to the list
            times.add(LocalDateTime.of(startDate, LocalTime.of(i, 45))); // Add times to the list
        } // end for loop
        for (int i = 0; i < times.size(); i++) { // Loop through the list
            newapp_startTime.getItems() // Get the start time items
                    .add(times.get(i).atZone(ZoneId.of("US/Eastern")).withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        } // end for loop
        newapp_startTime.getSelectionModel().selectFirst(); // Select first item by default
    }

    /**
     * Handle the end date and time
     *
     * @param event
     */
    public void do_EndTime(ActionEvent event) {
        newapp_endTime.disableProperty().setValue(false); // Enable the end time
        ObservableList<LocalDateTime> times = FXCollections.observableArrayList(); // Create observable list
        LocalDate endDate = newapp_endDate.getValue(); // Get end date
        for (int i = 8; i < 22; i++) {
            // add times in 15 minute increments
            times.add(LocalDateTime.of(endDate, LocalTime.of(i, 0))); // Add times to the list
            times.add(LocalDateTime.of(endDate, LocalTime.of(i, 15))); // Add times to the list
            times.add(LocalDateTime.of(endDate, LocalTime.of(i, 30))); // Add times to the list
            times.add(LocalDateTime.of(endDate, LocalTime.of(i, 45))); // Add times to the list
        } // end for loop
        for (int i = 0; i < times.size(); i++) { // Loop through the list
            newapp_endTime.getItems() // Get the end time items
                    .add(times.get(i).atZone(ZoneId.of("US/Eastern")).withZoneSameInstant(ZoneId.systemDefault())
                            .toLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        } // end for loop
        newapp_endTime.getSelectionModel().selectFirst(); // Select first item by default
    }

    /**
     * Validate the appointment
     *
     * @param appointment
     * @return
     */
    public boolean validateNewAppointment(Appointments appointment) {
        AtomicBoolean valid = new AtomicBoolean(true); // Initialize boolean
        try {
            String query = "SELECT * FROM client_schedule.appointments WHERE Customer_ID = ?";
            JDBC.makeConnection(); // Connect to database
            Connection connection = JDBC.connection; // Get connection
            PreparedStatement statement = connection.prepareStatement(query); // Create statement
            statement.setString(1, newapp_customerID.getText()); // Set customer ID
            ResultSet resultSet = statement.executeQuery(); // Execute query
            while (resultSet.next()) {
                Timestamp start = resultSet.getTimestamp("Start"); // Get start time
                Timestamp end = resultSet.getTimestamp("End"); // Get end time
                if (appointment.getStart().before(end) && appointment.getEnd().after(start)) {
                    valid.set(false); // Set valid to false
                    Alert alert = new Alert(Alert.AlertType.ERROR); // Create alert
                    alert.setTitle("Error"); // Set alert title
                    alert.setHeaderText("Invalid Times"); // Set alert header
                    alert.setContentText("The appointment times conflict with another appointment."); // Set alert
                    alert.showAndWait(); // Show alert
                }
            }
        } catch (Exception e) {
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
                    valid.set(false); // Set valid to false
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Invalid Appointment Time");
                    alert.setContentText("Start and End Times are outside 8:00 AM - 10:00 PM EST");
                    alert.showAndWait();
                }
                break;
            case SATURDAY:
            case SUNDAY:
                valid.set(false); // Set valid to false
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Appointment Day");
                alert.setContentText("Appointments may only be scheduled on weekdays.");
                alert.showAndWait();
                break;
        }
        return valid.get(); // Return valid
    }
}
