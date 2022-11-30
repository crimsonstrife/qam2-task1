package main;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */

import main.JDBC;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Customers {
    private int customer_ID;
    private String customer_Name;
    private String address;
    private String postal;
    private String phone;
    private int division_ID;
    private String division;
    private String created_Date;
    private String created_By;
    private String updated_Date;
    private String updated_By;

    /**
     * Constructor for the Customers class.
     *
     * @param customer_ID
     * @param customer_Name
     * @param address
     * @param postal
     * @param phone
     * @param division_ID
     * @param created_Date
     * @param created_By
     * @param updated_Date
     * @param updated_By
     */
    public Customers(int customer_ID, String customer_Name, String address, String postal, String phone,
            int division_ID, String created_Date, String created_By, String updated_Date, String updated_By) {
        this.customer_ID = customer_ID;
        this.customer_Name = customer_Name;
        this.address = address;
        this.postal = postal;
        this.phone = phone;
        this.division_ID = division_ID;
        this.created_Date = created_Date;
        this.created_By = created_By;
        this.updated_Date = updated_Date;
        this.updated_By = updated_By;
        setCustomerDivision();
    }

    /**
     * Returns the customer_ID.
     *
     * @return the customer_ID
     */
    public int getCustomer_ID() {
        return customer_ID;
    }

    /**
     * Sets the customer_ID.
     *
     * @param customer_ID the customer_ID to set
     */
    public void setCustomer_ID(int customer_ID) {
        this.customer_ID = customer_ID;
    }

    /**
     * Returns the customer_Name.
     *
     * @return the customer_Name
     */
    public String getCustomer_Name() {
        return customer_Name;
    }

    /**
     * Sets the customer_Name.
     *
     * @param customer_Name the customer_Name to set
     */
    public void setCustomer_Name(String customer_Name) {
        this.customer_Name = customer_Name;
    }

    /**
     * Returns the address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns the postal code.
     *
     * @return the postal code
     */
    public String getPostal() {
        return postal;
    }

    /**
     * Sets the postal code.
     *
     * @param postal the postal code to set
     */
    public void setPostal(String postal) {
        this.postal = postal;
    }

    /**
     * Returns the phone number.
     *
     * @return the phone number
     */
    public String getPhone() {
        return phone;
    }

    /**
     * Sets the phone number.
     *
     * @param phone the phone number to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * Returns the division_ID.
     *
     * @return the division_ID
     */
    public int getDivision_ID() {
        return division_ID;
    }

    /**
     * Sets the division_ID.
     *
     * @param division_ID the division_ID to set
     */
    public void setDivision_ID(int division_ID) {
        this.division_ID = division_ID;
    }

    /**
     * Returns the division.
     *
     * @return the division
     */
    public String getDivision() {
        return division;
    }

    /**
     * Sets the division.
     *
     */
    private void setCustomerDivision() {
        JDBC.makeConnection();
        Connection connection = JDBC.connection; // Get connection
        Statement statement = connection.createStatement(); // Create statement
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM client_schedule.first_level_divisions WHERE Division_ID = '" + this.division_ID + "'");
        resultSet.next();
        Divisions division = new Divisions(resultSet.getInt("Division_ID"), resultSet.getString("Division"),
                resultSet.getInt("COUNTRY_ID"));
        this.division = division.getDivision();
    }

    /**
     * Returns the created_Date.
     *
     * @return the created_Date
     */
    public String getCreated_Date() {
        return created_Date;
    }

    /**
     * Sets the created_Date.
     *
     * @param created_Date the created_Date to set
     */
    public void setCreated_Date(String created_Date) {
        this.created_Date = created_Date;
    }

    /**
     * Returns the created_By.
     *
     * @return the created_By
     */
    public String getCreated_By() {
        return created_By;
    }

    /**
     * Sets the created_By.
     *
     * @param created_By the created_By to set
     */
    public void setCreated_By(String created_By) {
        this.created_By = created_By;
    }

    /**
     * Returns the updated_Date.
     *
     * @return the updated_Date
     */
    public String getUpdated_Date() {
        return updated_Date;
    }

    /**
     * Sets the updated_Date.
     *
     * @param updated_Date the updated_Date to set
     */
    public void setUpdated_Date(String updated_Date) {
        this.updated_Date = updated_Date;
    }

    /**
     * Returns the updated_By.
     *
     * @return the updated_By
     */
    public String getUpdated_By() {
        return updated_By;
    }

    /**
     * Sets the updated_By.
     *
     * @param updated_By the updated_By to set
     */
    public void setUpdated_By(String updated_By) {
        this.updated_By = updated_By;
    }

}
