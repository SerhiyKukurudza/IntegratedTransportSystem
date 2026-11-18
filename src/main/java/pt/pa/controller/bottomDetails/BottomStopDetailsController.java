package pt.pa.controller.bottomDetails;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.controller.TransportMapFeature;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.findStops.ViewStopCommand;
import pt.pa.patterns.observer.Observer;

import java.util.ArrayList;
import java.util.List;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;

/**
 * Controller for managing and displaying the details of a selected stop.
 */
public class BottomStopDetailsController extends TransportBaseController implements Observer, BottomDetailsController {

    /**
     * Represents the pane where the view of this controller lies.
     */
    @FXML
    private Pane paneBottomStopDetails;

    /**
     * Represents the button to close the route details pane.
     */
    @FXML
    private Button buttonClose;

    /**
     * Represents the label displaying the name of the stop.
     */
    @FXML
    private Label labelName;

    /**
     * Represents the label displaying the number of connections the stop has.
     */
    @FXML
    private Label labelConnections;

    /**
     * Represents the label displaying the latitude of the stop.
     */
    @FXML
    private Label labelLatitude;

    /**
     * Represents the label displaying the longitude of the stop.
     */
    @FXML
    private Label labelLongitude;

    /**
     * Represents the HBox displaying the adjacent stops of the selected stop.
     */
    @FXML
    private HBox hBoxAdjacentStops;

    /**
     * Represents the scroll pane where the adjacent stops are stored at.
     */
    @FXML
    private ScrollPane scrollBottomPaneStopDetails;

    /**
     * Represents the grid where the adjacent stops will be dynamically added to.
     */
    @FXML
    private GridPane gridBottomPaneStopDetails;

    /**
     * Represents the currently selected stop vertex.
     */
    private Vertex<Stop> stopVertex;

    /**
     * Constructs the controller with a given transport map and base controller.
     *
     * @param transportMap   The transport map instance.
     * @param baseController The base controller instance.
     */
    public BottomStopDetailsController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        turnBottomDetailsNotVisible();
        setButtonCloseTrigger();
    }

    /**
     * Makes the bottom details pane visible.
     */
    private void turnBottomDetailsVisible() {
        setNodeVisibility(paneBottomStopDetails, true);
    }

    /**
     * Makes the bottom details pane invisible.
     */
    public void turnBottomDetailsNotVisible() {
        setNodeVisibility(paneBottomStopDetails, false);
    }

    /**
     * Sets up the trigger for the close button.
     */
    private void setButtonCloseTrigger() {
        buttonClose.setOnAction(e -> turnBottomDetailsNotVisible());
    }

    /**
     * Updates the view to display details of the selected stop.
     *
     * @param stopVertex The vertex of the selected stop.
     */
    private void selectedStop(Vertex<Stop> stopVertex) {
        this.stopVertex = stopVertex;
        showStopDetails(stopVertex);
    }

    /**
     * Displays details of the given stop and its adjacent stops.
     *
     * @param stopVertex The vertex representing the selected stop.
     */
    public void showStopDetails(Vertex<Stop> stopVertex) {
        turnBottomDetailsVisible();

        UserActionLogger.getInstance().performUserAction(
                new ViewStopCommand(stopVertex)
        );

        updateStopDetails(stopVertex);
        updateAdjacentStopsTable(stopVertex);
    }

    /**
     * Updates the stop details UI with data from the selected stop.
     *
     * @param stopVertex The vertex representing the selected stop.
     */
    private void updateStopDetails(Vertex<Stop> stopVertex) {
        Stop stop = stopVertex.element();

        labelName.setText(stop.getName());
        labelConnections.setText(String.valueOf(transportMap.getNumAdjacentStops(stopVertex)));
        labelLatitude.setText(String.format("%.4f", stop.getLatitude()));
        labelLongitude.setText(String.format("%.4f", stop.getLongitude()));
    }

    /**
     * Updates the adjacent stops table dynamically based on the selected stop's routes.
     *
     * @param stopVertex The vertex representing the selected stop.
     */
    private void updateAdjacentStopsTable(Vertex<Stop> stopVertex) {
        if (!transportMap.hasIncidentRoutes(stopVertex)) {
            setNodeVisibility(hBoxAdjacentStops, false);
            return;
        }

        showAdjacentStopsTable(stopVertex);
    }

    /**
     * Shows the adjacent stops table by finding and placing the found stops in the grid.
     *
     * @param stopVertex The selected stop.
     */
    private void showAdjacentStopsTable(Vertex<Stop> stopVertex) {
        turnAdjacentStopsVisible();

        List<Pair<String, Double>> adjacentStops = getAdjacentStops(stopVertex);

        int columnIndex = 0;
        for (Pair<String, Double> stop : adjacentStops) {
            columnIndex = addAdjacentStopToGrid(stop, columnIndex);
        }
    }

    /**
     * Clears and turns on the adjacent stop table visibility.
     */
    private void turnAdjacentStopsVisible() {
        gridBottomPaneStopDetails.getChildren().clear();
        setNodeVisibility(hBoxAdjacentStops, true);
    }

    /**
     * Gets all the adjacent stops name and average distance.
     *
     * @param stopVertex The selected stop.
     * @return A list of pairs with its name and average distance.
     */
    private List<Pair<String, Double>> getAdjacentStops(Vertex<Stop> stopVertex) {
        List<Pair<String, Double>> adjacentStops = new ArrayList<>();

        for (Edge<Route, Stop> edge : transportMap.incidentRoutes(stopVertex)) {
            Route route = edge.element();
            Vertex<Stop> oppositeVertex = transportMap.oppositeStop(stopVertex, edge);
            Stop oppositeStop = oppositeVertex.element();

            double averageDistance = route.getAverageDistance();

            adjacentStops.add(new Pair<>(oppositeStop.getName(), averageDistance));
        }

        return adjacentStops;
    }

    /**
     * Adds an adjacent stop details to the adjacent stops grid.
     *
     * @param stop        The stop name and value pair.
     * @param columnIndex The index of the column of the grid.
     * @return The next column index for the grid.
     */
    private int addAdjacentStopToGrid(Pair<String, Double> stop, int columnIndex) {
        Label labelStopName = new Label(stop.getValue0());
        Label labelAverageDistance = new Label(String.format("%.2f", stop.getValue1()));

        gridBottomPaneStopDetails.add(labelStopName, columnIndex, 0);
        gridBottomPaneStopDetails.add(labelAverageDistance, columnIndex, 1);

        columnIndex++;
        return columnIndex;
    }

    /**
     * Loads and refreshes the view's dynamic content based on the model.
     */
    private void loadDynamicContent() {
        if (stopVertex == null || updatedRoute()) return;

        showStopDetails(stopVertex);
    }

    /**
     * Checks which tab is selected so it doesn't overlap the correct bottom details.
     *
     * @return {@code True} if a route was updated in the transport map,
     * {@code false} otherwise.
     */
    private boolean updatedRoute() {
        boolean updatedBicycle = baseController.checkIfTabIsSelected(TransportMapFeature.UPDATE_BICYCLE);
        boolean updatedRouteState = baseController.checkIfTabIsSelected(TransportMapFeature.DEACTIVATE_ROUTE);

        return updatedBicycle || updatedRouteState;
    }

    /**
     * Updates the view in response to model changes.
     *
     * @param object The updated object.
     */
    @Override
    public void update(Object object) {
        if (object instanceof TransportMap) {
            loadDynamicContent();
        } else if (object instanceof Vertex<?> vertex) {
            selectedStop((Vertex<Stop>) vertex);
        } else if (object instanceof Edge<?, ?>) {
            turnBottomDetailsNotVisible();
        }
    }
}