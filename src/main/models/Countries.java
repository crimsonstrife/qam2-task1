package main.models;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */

public class Countries {
    private int country_ID;
    private String country;

    /**
     * Constructor for the Countries class.
     *
     * @param country_ID
     * @param country
     */
    public Countries(int country_ID, String country) {
        this.country_ID = country_ID;
        this.country = country;
    }

    /**
     * Returns the country_ID.
     *
     * @return the country_ID
     */
    public int getCountry_ID() {
        return country_ID;
    }

    /**
     * Sets the country_ID.
     *
     * @param country_ID the country_ID to set
     */
    public void setCountry_ID(int country_ID) {
        this.country_ID = country_ID;
    }

    /**
     * Returns the country.
     *
     * @return the country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Sets the country.
     *
     * @param country the country to set
     */
    public void setCountry(String country) {
        this.country = country;
    }

}
