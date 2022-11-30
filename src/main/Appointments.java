package main;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */

import javafx.collections.ObservableList;
import java.sql.*;
import main.JDBC;
import main.Utils;
import java.time.ZoneId;
import static main.Utils.recordLoginAttempt;

public class Appointments {
    private Integer appointment_ID;
    private String title;
    private String description;
    private String location;
    private String type;
    private Timestamp start;
    private Timestamp end;
    private Timestamp createDate;
    private String createdBy;
    private Timestamp lastUpdate;
    private String lastUpdateBy;
    private Integer customer_ID;
    private Integer user_ID;
    private Integer contact_ID;

    public Appointments(Integer appointment_ID, String title, String description, String location, String type,
            Timestamp start, Timestamp end, Timestamp createDate, String createdBy, Timestamp lastUpdate,
            String lastUpdateBy,
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

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }

    public Timestamp getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Timestamp createDate) {
        this.createDate = createDate;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
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
}
