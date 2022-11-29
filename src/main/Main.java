package main;

/**
 *
 * @author Patrick Barnhardt
 *
 * JAVADOC Location: in the Root of the Project folder - in a folder called JAVADOCS.
 */
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import main.JDBC;

/**
 * The type Main controller.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("login.fxml"));
        primaryStage.setTitle("Scheduler");
        primaryStage.setScene(new Scene(root, 640, 480));
        primaryStage.show();
    }

    /**
     * Configure the Login Fields.
     */
    // configure the username field
    @FXML
    private TextField login_username;
    // configure the password field
    @FXML
    private PasswordField login_password;

    public static void main(String[] args) {
        JDBC.makeConnection();
        launch(args);
    }

    /**
     * Exit Button Event Handler - Close the application.
     *
     * @param event triggered by the exit button
     */
    public void ExitBtnAction(ActionEvent event) {
        System.exit(0);
    }

    /**
     * Login Button Event Handler - Use the entered username and password to attempt
     * login. If successful, open the main application window.
     * Get the login_username and login_password fx:id in login.fxml for the values
     *
     * @param event triggered by the login button
     *
     *
     */
    public void LoginBtnAction(ActionEvent event) {
        if (JDBC.login(login_username.getText(), login_password.getText())) {
            try {
                Parent root = FXMLLoader.load(getClass().getResource("main.fxml"));
                Stage stage = new Stage();
                stage.setTitle("Scheduler");
                stage.setScene(new Scene(root, 1920, 1080));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
