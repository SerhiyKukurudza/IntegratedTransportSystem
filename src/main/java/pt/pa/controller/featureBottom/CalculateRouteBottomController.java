/******************************************************************************************************************************
 * File Name: CalculateRouteBottomController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureBottom;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.javatuples.Quartet;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;
import pt.pa.utils.TransportColor;

import java.util.Collection;
import java.util.List;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;
import static pt.pa.ui.DisplayHelper.toggleNodesVisibility;

/**
 * Represents the controller class for the CalculateRouteBottom view.
 */
public class CalculateRouteBottomController extends TransportBaseController implements FeatureBottomController {

    /**
     * Represents the background pane of the calculate route bottom tab.
     */
    @FXML
    private Pane bottomCalculateRoutePane;

    /**
     * Represents the tips version of the bottom menu.
     */
    @FXML
    private VBox vBoxTips;

    /**
     * Represents the dynamic version of the bottom menu.
     */
    @FXML
    private VBox vBoxCalculatedRoute;

    /**
     * Represents the origin stop name.
     */
    @FXML
    private Label labelOriginStopName;

    /**
     * Represents the destination stop name.
     */
    @FXML
    private Label labelDestinationStopName;

    /**
     * Represents the total distance of the route.
     */
    @FXML
    private Label labelDistance;

    /**
     * Represents the total duration of the route.
     */
    @FXML
    private Label labelDuration;

    /**
     * Represents the total cost of the route.
     */
    @FXML
    private Label labelCost;

    /**
     * Represents the grid pane with the route information.
     */
    @FXML
    private GridPane gridPaneCalculatedRoute;

    /**
     * Constructs a CalculateRouteBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap   The transport map instance.
     * @param baseController The base controller instance.
     */
    public CalculateRouteBottomController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the calculate route bottom tab view.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
        setNodeVisibility(vBoxCalculatedRoute, false);
    }

    /**
     * Displays the calculated route details including the origin, destination,
     * and the total transport metrics (distance, duration, and cost).
     * This method also toggles the visibility of the bottom menus between tips and calculated route.
     *
     * @param originStopVertex      The vertex representing the origin stop.
     * @param destinationStopVertex The vertex representing the destination stop.
     * @param middleStops           The list of middle stops, if any, between the origin and destination.
     * @param pathEdges             The collection of edges representing the path between the stops
     *                              (each including the route color, route label, and distance).
     * @param totalTransportMetrics The total transport metrics for the calculated route (distance, duration, and cost).
     */
    public void showCalculatedRoute(Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex, List<Vertex<Stop>> middleStops, Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges, TransportMetrics totalTransportMetrics) {
        turnCalculatedRouteVisibility();

        updateRouteCalculationSummary(originStopVertex, destinationStopVertex, totalTransportMetrics);
        showCalculatedRouteDetails(originStopVertex, pathEdges);
    }

    /**
     * Shows the calculated route details by iterating and processing each route.
     * The table details consist in the following: (Route, Transport, Cost)
     *
     * @param originStopVertex The origin stop vertex.
     * @param pathEdges        The collection of edges representing the path between the stops
     *                         (each including the route color, route label, and distance).
     */
    private void showCalculatedRouteDetails(Vertex<Stop> originStopVertex, Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges) {
        int columnIndex = 0;

        Vertex<Stop> previousStop = originStopVertex;

        for (Quartet<Edge<Route, Stop>, String, TransportColor, Double> pathEdge : pathEdges) {
            previousStop = processCurrentRouteEdge(pathEdge, previousStop, columnIndex);
            columnIndex++;
        }
    }

    /**
     * Processes the current route edge and determines the next stop vertex to process.
     * This method also updates the grid representation with the route information.
     *
     * @param pathEdge     The edge representing the current route segment
     *                     (route color, route label, and distance).
     * @param previousStop The vertex representing the last processed stop.
     * @param columnIndex  The index of the column in the grid to update with the route information.
     * @return The vertex representing the next stop to process along the route.
     */
    private Vertex<Stop> processCurrentRouteEdge(
            Quartet<Edge<Route, Stop>, String, TransportColor, Double> pathEdge, Vertex<Stop> previousStop,
            int columnIndex) {
        Edge<Route, Stop> edgeRoute = pathEdge.getValue0();

        Vertex<Stop>[] incidentVertices = edgeRoute.vertices();
        Vertex<Stop> vertex1 = incidentVertices[0];
        Vertex<Stop> vertex2 = incidentVertices[1];

        // Determine the correct order of stops based on the previous stop
        Vertex<Stop> currentStop, nextStop;
        if (vertex1.equals(previousStop)) {
            currentStop = vertex1;
            nextStop = vertex2;
        } else {
            currentStop = vertex2;
            nextStop = vertex1;
        }

        addRouteToGrid(pathEdge, getRouteStopsName(currentStop, nextStop), columnIndex);
        return nextStop;
    }

    /**
     * Gets the interval name of two stops.
     *
     * @param currentStop The current stop.
     * @param nextStop    The next stop.
     * @return The concatenated name of both stops.
     */
    private String getRouteStopsName(Vertex<Stop> currentStop, Vertex<Stop> nextStop) {
        String currentStopName = currentStop.element().getName();
        String nextStopName = nextStop.element().getName();

        return currentStopName + " - " + nextStopName;
    }

    /**
     * Adds a route to the route grid.
     *
     * @param routeName   The name of the current route.
     * @param pathEdge    The path edge quartet with all relevante route calculation values.
     * @param columnIndex The column index to put the route in the grid at.
     */
    private void addRouteToGrid(Quartet<Edge<Route, Stop>, String, TransportColor, Double> pathEdge, String routeName, int columnIndex) {
        String transport = pathEdge.getValue1();
        Double cost = pathEdge.getValue3();

        Label labelRoute = new Label(routeName);
        Label labelTransport = new Label(transport);
        Label labelCost = new Label(String.format("%.1f", cost));

        gridPaneCalculatedRoute.add(labelRoute, columnIndex, 0);
        gridPaneCalculatedRoute.add(labelTransport, columnIndex, 1);
        gridPaneCalculatedRoute.add(labelCost, columnIndex, 2);
    }

    /**
     * Updated the route calculation summary labels.
     *
     * @param originStopVertex      The origin stop vertex.
     * @param destinationStopVertex The destination stop vertex.
     * @param totalTransportMetrics The total of transport metrics.
     */
    private void updateRouteCalculationSummary(Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex, TransportMetrics totalTransportMetrics) {
        labelOriginStopName.setText(originStopVertex.element().getName() + " ");
        labelDestinationStopName.setText(" " + destinationStopVertex.element().getName());

        labelDistance.setText(String.format(" %.1f", totalTransportMetrics.getDistance()) + " km");
        labelDuration.setText(String.format(" %.1f", totalTransportMetrics.getDuration()) + " minutes");
        labelCost.setText(String.format(" %.1f", totalTransportMetrics.getCost()));
    }

    /**
     * Turns the calculated route VBox visible and clears old routes from older calculations.
     */
    private void turnCalculatedRouteVisibility() {
        toggleNodesVisibility(vBoxCalculatedRoute, vBoxTips);
        gridPaneCalculatedRoute.getChildren().clear();
    }
}

