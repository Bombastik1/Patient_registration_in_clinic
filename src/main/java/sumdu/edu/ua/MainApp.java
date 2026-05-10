package sumdu.edu.ua;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * The main entry point for the JavaFX application.
 * This class is responsible for loading the primary user interface,
 * applying stylesheets, setting application icons, and displaying the main window.
 */
public class MainApp extends Application {

    /**
     * Starts the JavaFX application by setting up the primary stage.
     *
     * @param stage the primary stage for this application, onto which
     * the application scene is set
     * @throws IOException if the FXML file or other resources cannot be loaded
     */
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/main-view.fxml"));

        Scene scene = new Scene(fxmlLoader.load(), 800, 600);

        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/style.css")).toExternalForm());

        stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/logo.png"))));

        stage.setTitle("Реєстрація пацієнтів клініки");
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The main method that launches the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}