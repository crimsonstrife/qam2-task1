package main.models;
/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */

public class Types {
    private String type;
    private int count;
    private String month;

    /**
     * @param type
     * @param month
     * @param count
     */
    public Types(String type, String month, int count) {
        this.type = type;
        this.count = count;
        this.month = month;
    }

    /**
     *
     * @return
     */
    public String getType() {
        return type;
    }

    /**
     *
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     *
     * @param month
     */
    public void setMonth(String month) {
        this.month = month;
    }

    /**
     *
     * @return
     */
    public int getCount() {
        return count;
    }

    /**
     *
     * @param count
     */
    public void setCount(int count) {
        this.count = count;
    }

    /**
     *
     * @return
     */
    public String getMonth() {
        return month;
    }
}
