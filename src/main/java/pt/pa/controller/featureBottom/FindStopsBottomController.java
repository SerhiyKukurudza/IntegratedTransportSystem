/******************************************************************************************************************************
 * File Name: FindStopsBottomController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureBottom;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.javatuples.Triplet;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;
import static pt.pa.ui.DisplayHelper.toggleNodesVisibility;

/**
 * Represents the controller class for the FindStopsBottom view.
 */
public class FindStopsBottomController extends TransportBaseController implements FeatureBottomController {
    /**
     * Constant that holds the row span of the found stops grid.
     * The number of rows plus 2 to add enough space for the scroll bar
     */
    public static final int FOUND_STOPS_ROW_SPAN = 3 + 2;

    /**
     * Represents the background pane of find stops bottom tab.
     */
    @FXML
    private Pane bottomFindStopsPane;

    /**
     * Represents the tips version of the bottom menu.
     */
    @FXML
    private VBox vBoxTips;

    /**
     * Represents the dynamic version of the bottom menu.
     */
    @FXML
    private VBox vBoxFoundStops;

    /**
     * Represents the label that will hold the origin stop name.
     */
    @FXML
    private Label labelOriginStopName;

    /**
     * Represents the label that will hold the distance of stops in number.
     */
    @FXML
    private Label labelDistanceNumber;

    /**
     * Represents the scroll pane where the found stops grid is stored at.
     */
    @FXML
    private ScrollPane scrollPaneFoundStops;

    /**
     * Represents the grid where the stops will be dynamically added to.
     */
    @FXML
    private GridPane gridPaneFoundStops;

    /**
     * Constructs a FindStopsBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     */
    public FindStopsBottomController(TransportMap transportMap) {
        super(transportMap);
    }

    /**
     * Initializes every aspect of find stops bottom tab.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
        setNodeVisibility(vBoxFoundStops, false);
    }

    /**
     * Updated the corresponding labels of the found stops menu.
     *
     * @param originStopName The name of the origin stop.
     * @param distanceNumber The distance from the origin to the destinations.
     */
    private void updateDynamicTitle(String originStopName, String distanceNumber) {
        labelDistanceNumber.setText(" " + distanceNumber + " ");
        labelOriginStopName.setText(originStopName);
    }

    /**
     * Shows the found stops at the bottom menu dynamically.
     * If no stops were found, the tips will be shown.
     *
     * @param originStopName The origin stop to show.
     * @param distanceNumber The distance number to show.
     * @param stopVertices   The stop destinations to show.
     */
    public void showFoundStops(String originStopName, int distanceNumber, Collection<Vertex<Stop>> stopVertices) {
        if (stopVertices.isEmpty()) {
            toggleNodesVisibility(vBoxTips, vBoxFoundStops);
            return;
        }

        updateDynamicTitle(originStopName, String.valueOf(distanceNumber));
        updateDynamicFoundStopsTable(stopVertices);

        toggleNodesVisibility(vBoxFoundStops, vBoxTips);
    }

    /**
     * Updates the internal grid pane to dynamically add all found vertices and its details.
     *
     * @param stopVertices The collection of stop vertices to load.
     */
    private void updateDynamicFoundStopsTable(Collection<Vertex<Stop>> stopVertices) {
        turnFindStopsVisibility();
        showFoundStops(getFoundStops(stopVertices));
    }

    /**
     * Iterates all found stops and shows them on the grid.
     *
     * @param foundStops The found stops.
     */
    private void showFoundStops(List<Triplet<String, Double, Double>> foundStops) {
        int columnIndex = 0;

        // | StopName | Distance
        for (Triplet<String, Double, Double> foundStop : foundStops) {
            columnIndex = addFoundStopToTheGrid(foundStop, columnIndex);
        }
    }

    /**
     * Gets a list of triplet with all the necessary information about the found stops.
     *
     * @param stopVertices The stops that were found.
     * @return A list with the name, longitude and latitude of a stop.
     */
    private List<Triplet<String, Double, Double>> getFoundStops(Collection<Vertex<Stop>> stopVertices) {
        List<Triplet<String, Double, Double>> foundStops = new ArrayList<>();

        for (Vertex<Stop> stopVertex : stopVertices) {
            Stop stop = stopVertex.element();
            foundStops.add(new Triplet<>(stop.getName(), stop.getLatitude(), stop.getLongitude()));
        }
        return foundStops;
    }

    /**
     * Turns the found stops scroll pane visible and clears previously found stops.
     */
    private void turnFindStopsVisibility() {
        GridPane.setRowSpan(scrollPaneFoundStops, FOUND_STOPS_ROW_SPAN);
        gridPaneFoundStops.getChildren().clear();
    }

    /**
     * Adds the found stop to get grid and updates the column index.
     *
     * @param foundStop   The found stop to add to the grid.
     * @param columnIndex The current column index.
     * @return The next stop column index.
     */
    private int addFoundStopToTheGrid(Triplet<String, Double, Double> foundStop, int columnIndex) {
        Label labelStopName = new Label(foundStop.getValue0());
        Label labelDistance = new Label(String.valueOf(foundStop.getValue1()));
        Label labelLongitude = new Label(String.valueOf(foundStop.getValue2()));

        labelStopName.setStyle("-fx-font-style: italic");

        gridPaneFoundStops.add(labelStopName, columnIndex, 0);
        gridPaneFoundStops.add(labelDistance, columnIndex, 1);
        gridPaneFoundStops.add(labelLongitude, columnIndex, 2);

        columnIndex++;
        return columnIndex;
    }
}
