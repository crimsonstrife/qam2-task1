package main;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */

public class Contacts {
    private int contact_ID;
    private String contact_Name;
    private String contact_email;

    /**
     *
     *
     * @param contact_ID
     * @param contact_Name
     * @param contact_email
     */
    public Contacts(int contact_ID, String contact_Name, String contact_email) {
        this.contact_ID = contact_ID;
        this.contact_Name = contact_Name;
        this.contact_email = contact_email;
    }

    /**
     * Get the contact_ID
     *
     * @return
     */
    public int getContact_ID() {
        return contact_ID;
    }

    /**
     * Set the contact_ID
     *
     * @param contact_ID
     */
    public void setContact_ID(int contact_ID) {
        this.contact_ID = contact_ID;
    }

    /**
     * Get the contact_Name
     *
     * @return
     */
    public String getContact_Name() {
        return contact_Name;
    }

    /**
     * Set the contact_Name
     *
     * @param contact_Name
     */
    public void setContact_Name(String contact_Name) {
        this.contact_Name = contact_Name;
    }

    /**
     * Get the contact_email
     *
     * @return
     */
    public String getContact_email() {
        return contact_email;
    }

    /**
     * Set the contact_email
     *
     * @param contact_email
     */
    public void setContact_email(String contact_email) {
        this.contact_email = contact_email;
    }
}
