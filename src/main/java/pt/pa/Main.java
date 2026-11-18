/******************************************************************************************************************************
 * File Name: Main.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa;

import javafx.application.Application;
import javafx.stage.Stage;
import pt.pa.utils.MainSceneLoader;

/**
 * Main class for the transport application.
 * This class serves as the entry point of the application and initializes the graphical user interface.
 * It loads transport stops and routes from data files and constructs a graph representation.
 */
public class Main extends Application {
    /**
     * The default entry point of the application.
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Initializes the primary stage for the application with the main scene loader.
     */
    @Override
    public void start(Stage primaryStage) {
        MainSceneLoader.load();
    }
}
