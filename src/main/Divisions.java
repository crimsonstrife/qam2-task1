package main;

/**
 *
 * @author Patrick Barnhardt
 *
 *         JAVADOC Location: in the Root of the Project folder - in a folder
 *         called JAVADOCS.
 */

public class Divisions {
    private int division_ID;
    private String division;
    private int country_ID;

    /**
     * Constructor for the Divisions class.
     *
     * @param division_ID
     * @param division
     * @param country_ID
     */
    public Divisions(int division_ID, String division, int country_ID) {
        this.division_ID = division_ID;
        this.division = division;
        this.country_ID = country_ID;
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
     * @param division the division to set
     */
    public void setDivision(String division) {
        this.division = division;
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

}
