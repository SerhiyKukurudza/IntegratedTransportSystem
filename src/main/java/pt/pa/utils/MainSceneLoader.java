/******************************************************************************************************************************
 * File Name: MainSceneLoader.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.javatuples.Triplet;
import pt.pa.Main;
import pt.pa.controller.BaseController;
import pt.pa.controller.MapController;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.ui.ControlNotification;
import pt.pa.ui.resources.FXMLResourceBundle;
import pt.pa.view.MapView;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Represents the main scene loader.
 * Loads the transport map model and the other MVC components.
 */
public class MainSceneLoader {
    /**
     * Represents the file path to the base view in fxml.
     */
    public final static String BASE_VIEW_PATH = "/views/base.fxml";

    /**
     * Represents the file path to the notifications style
     */
    public final static String NOTIFICATIONS_STYLE_PATH = "/styles/control-notification.css";

    /**
     * Represents the window title set at the config properties.
     */
    private static String windowTitle;

    /**
     * Represents the main and only stage of the JavaFX application.
     */
    private static Stage stage;

    /**
     * Loads the graphical interface with an MVC Pattern.
     */
    public static void load() {
        loadConfigurationProperties();
        setStage();
    }

    /**
     * Imports all configuration properties at resources/properties/config.properties into their respective variables.
     */
    private static void loadConfigurationProperties() {
        try {
            windowTitle = PropertiesUtil.getInstance().getProperty("window.title");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Initializes the Model-View-Controller (MVC) components for the application.
     *
     * @return The base controller that connects the map view and transport map.
     */
    private static BaseController initializeMVC() {
        TransportMap transportMap = new TransportMap();
        importRoutesAndStops(transportMap);

        MapView mapView = new MapView(transportMap);
        MapController mapController = new MapController(transportMap, mapView);

        BaseController baseController = new BaseController(mapController, transportMap);
        mapController.setBaseController(baseController);

        return baseController;
    }

    /**
     * Imports all routes and stop data from the DataSetImporter.
     *
     * @param transportMap The transport map instance to import to.
     */
    public static void importRoutesAndStops(TransportMap transportMap) {
        DataSetImporter importer = new DataSetImporter();

        try {
            importTransports(transportMap, importer);
            Map<String, Stop> stopMap = importStops(transportMap, importer);
            importRoutes(transportMap, importer, stopMap);

        } catch (IOException | CsvValidationException e) {
            showLoadingError(e, "Error loading data",
                    "An error occurred while loading data"
            );
        }
    }

    /**
     * Imports the routes into the transport map.
     *
     * @param transportMap The transport map to import the transports into.
     * @param importer     The importer used to extract the information.
     * @param stopMap      A map with every stop's code and stop object.
     * @throws IOException            If any problem occurs during the importation.
     * @throws CsvValidationException If any CSV validation error occurs during the importation.
     */
    private static void importRoutes(TransportMap transportMap, DataSetImporter importer, Map<String, Stop> stopMap) throws IOException, CsvValidationException {
        List<Triplet<Stop, Stop, Route>> routes = importer.importRoutes(stopMap);
        transportMap.addRoutes(routes);
    }

    /**
     * Imports the stops into the transport map and creates a map with each stop's code and instance.
     *
     * @param transportMap The transport map to import the transports into.
     * @param importer     The importer used to extract the information.
     * @return A map with the stop code and the stop.
     * @throws IOException            If any problem occurs during the importation.
     * @throws CsvValidationException If any CSV validation error occurs during the importation.
     */
    private static Map<String, Stop> importStops(TransportMap transportMap, DataSetImporter importer) throws IOException, CsvValidationException {
        List<Stop> stops = importer.importStops();
        transportMap.addStops(stops);

        Map<String, Stop> stopMap = new HashMap<>();
        for (Stop stop : stops) {
            stopMap.put(stop.getCode(), stop);
        }
        return stopMap;
    }

    /**
     * Imports the transports into the transport map.
     *
     * @param transportMap The transport map to import the transports into.
     * @param importer     The importer used to extract the information.
     * @throws IOException            If any problem occurs during the importation.
     * @throws CsvValidationException If any CSV validation error occurs during the importation.
     */
    private static void importTransports(TransportMap transportMap, DataSetImporter importer) throws IOException, CsvValidationException {
        transportMap.addTransportModes(importer.importTransportColors());
    }


    /**
     * Loads the base view and sets up the JavaFX stage with a base controller.
     */
    public static void setStage() {
        BaseController baseController = initializeMVC();
        Parent root = getRootParentFromFXMLLoader(baseController);

        Scene scene = new Scene(root);
        scene.getStylesheets().add(Objects.requireNonNull(Main.class.getResource(NOTIFICATIONS_STYLE_PATH)).toExternalForm());

        initializeStage();

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Creates a parent with the FXML file loader from the base controller and its view.
     * If a problem occurs during the loading process, a message is shown.
     *
     * @param baseController The base controller that will manage all other controllers.
     * @return The parent which contains the base view.
     */
    private static Parent getRootParentFromFXMLLoader(BaseController baseController) {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource(BASE_VIEW_PATH));
        loader.setController(baseController);
        loader.setResources(FXMLResourceBundle.getInstance().getResourceBundle());

        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            showLoadingError(e, "Error loading graphical interface",
                    "An error occurred while loading the graphical interface"
            );
        }
        return root;
    }

    /**
     * Shows the java fx default loader to show a critical startup error.
     *
     * @param exception  The exception that caused the problem.
     * @param title      The title to present.
     * @param headerText The header text to present.
     */
    private static void showLoadingError(Exception exception, String title, String headerText) {
        exception.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(headerText);
        alert.setContentText(exception.getMessage());

        alert.showAndWait();
    }

    /**
     * Initializes the stage it doesn't already exist.
     */
    private static void initializeStage() {
        if (stage != null) {
            return;
        }

        stage = new Stage(StageStyle.DECORATED);
        stage.setTitle(windowTitle);
        ControlNotification.setWindow(stage);

        stage.setResizable(false);
    }
}
