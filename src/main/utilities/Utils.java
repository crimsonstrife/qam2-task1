package main.utilities;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.time.ZoneId;

public class Utils {
    /**
     * Set Application Variables
     *
     */
    private static String userLoggedIn = "";
    private static Integer userLoggedInID = null;
    private static String userZone = ZoneId.systemDefault().toString();
    private static String userLanguage = System.getProperty("user.language");

    /**
     * Record User Login Attempts to local file in the application directory.
     *
     * @param username  entered username
     * @param success   - true if login was successful, false if not
     * @param location  - the location of the login attempt
     * @param timestamp - the time of the login attempt
     * @param datestamp - the date of the login attempt
     */
    public static void recordLoginAttempt(String username, boolean success, String location, String timestamp,
            String datestamp) {
        try {
            String loginAttempt = username + "," + success + "," + location + "," + datestamp + "," + timestamp;
            String fileName = "login_activity.txt";
            FileWriter fw = new FileWriter(fileName, true); // append new data to the file
            fw.write(loginAttempt + "\n");
            fw.close();
            if (success == true) {
                String userLog = username;
                String file = "current_user.txt";
                //overwrite the current user file
                FileWriter fw2 = new FileWriter(file);
                fw2.write(userLog);
                fw2.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // System.out.println("Login attempt recorded."); - DEBUG
        }
    }

    /**
     * Retrieve Login Attempt from login_activity.txt
     *
     * @return String - the username of the last successful login attempt
     */
    public static String getLoginAttempt() throws FileNotFoundException {
        String fileName = "current_user.txt";
        FileReader fr = new FileReader(fileName);
        //read the file and pass it to a string
        String user = fr.toString();
        return user;
    }

    /**
     * Set the language for the application based on the system language.
     *
     */
    public static void setLanguage() {
        String language = System.getProperty("user.language");
        if (language.equals("fr")) {
            userLanguage = "fr";
        } else {
            userLanguage = "en";
        }
    }
}
