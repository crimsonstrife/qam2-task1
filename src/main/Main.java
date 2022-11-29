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
     * login.
     *
     * @param event triggered by the login button
     */
    public void LoginBtnAction(ActionEvent event) {
        System.out.println("Login Button Pressed");
    }
}
