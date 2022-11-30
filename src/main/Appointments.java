package main;

import javafx.collections.ObservableList;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Time;

import main.JDBC;
import main.Utils;
import java.time.ZoneId;
import static main.Utils.recordLoginAttempt;

public abstract class Appointments {
    private Integer appointment_ID;
    private String title;
    private String description;
    private String location;
    private String type;
    private Date start;
    private Date end;
    private Date createDate;
    private String createdBy;
    private Time lastUpdate;
    private String lastUpdateBy;
    private Integer customer_ID;
    private Integer user_ID;
    private Integer contact_ID;

    public Appointments(Integer appointment_ID, String title, String description, String location, String type,
            Date start, Date end, Date createDate, String createdBy, Time lastUpdate, String lastUpdateBy,
            Integer customer_ID, Integer user_ID, Integer contact_ID) {
        this.appointment_ID = appointment_ID;
        this.title = title;
        this.description = description;
        this.location = location;
        this.type = type;
        this.start = start;
        this.end = end;
        this.createDate = createDate;
        this.createdBy = createdBy;
        this.lastUpdate = lastUpdate;
        this.lastUpdateBy = lastUpdateBy;
        this.customer_ID = customer_ID;
        this.user_ID = user_ID;
        this.contact_ID = contact_ID;
    }

    public Integer getAppointment_ID() {
        return appointment_ID;
    }

    public void setAppointment_ID(Integer appointment_ID) {
        this.appointment_ID = appointment_ID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getStart() {
        return start;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public Date getEnd() {
        return end;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Time getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Time lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getLastUpdateBy() {
        return lastUpdateBy;
    }

    public void setLastUpdateBy(String lastUpdateBy) {
        this.lastUpdateBy = lastUpdateBy;
    }

    public Integer getCustomer_ID() {
        return customer_ID;
    }

    public void setCustomer_ID(Integer customer_ID) {
        this.customer_ID = customer_ID;
    }

    public Integer getUser_ID() {
        return user_ID;
    }

    public void setUser_ID(Integer user_ID) {
        this.user_ID = user_ID;
    }

    public Integer getContact_ID() {
        return contact_ID;
    }

    public void setContact_ID(Integer contact_ID) {
        this.contact_ID = contact_ID;
    }

    /**
     * Method to get appointments from the database and return them.
     *
     * @return
     */
    public static ObservableList<Appointments> getAppointments() {
        ObservableList<Appointments> appointments = null;
        try {
            Connection conn = DriverManager.getConnection(JDBC.jdbcUrl, JDBC.userName, JDBC.password);
            PreparedStatement ps = conn.prepareStatement("SELECT * FROM appointments");
            ps.execute();
            appointments = ps.getResultSet();
            ps.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}
