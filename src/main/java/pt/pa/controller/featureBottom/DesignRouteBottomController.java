/******************************************************************************************************************************
 * File Name: DesignRouteBottomController.java
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
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static pt.pa.model.TransportMetrics.getEmptyTransportMetrics;
import static pt.pa.ui.DisplayHelper.setNodeVisibility;
import static pt.pa.ui.DisplayHelper.toggleNodesVisibility;

/**
 * Represents the controller class for the DesignRouteBottom view.
 */
public class DesignRouteBottomController extends TransportBaseController implements FeatureBottomController {
    /**
     * Constant that holds the row span of the designed route stops grid.
     * The number of rows plus 2 to add enough space for the scroll bar
     */
    public static final int DESIGNED_ROUTE_ROW_SPAN = 3 + 2;

    /**
     * Represents the background pane of the design route bottom tab.
     */
    @FXML
    private Pane bottomDesignRoutePane;

    /**
     * Represents the tips version of the bottom menu.
     */
    @FXML
    private VBox vBoxTips;

    /**
     * Represents the dynamic version of the bottom menu.
     */
    @FXML
    private VBox vBoxDesigningRoute;

    /**
     * Represents the label displaying the name of the first stop
     */
    @FXML
    private Label labelFirstStopName;

    /**
     * Represents the label displaying the distance information
     * for the route.
     */
    @FXML
    private Label labelDistance;

    /**
     * Represents the label displaying the duration information
     * for the route.
     */
    @FXML
    private Label labelDuration;

    /**
     * Represents the label displaying the cost information
     * for the route.
     */
    @FXML
    private Label labelCost;

    /**
     * Represents the scroll pane containing the adjacent stops table,
     * allowing users to scroll through its content.
     */
    @FXML
    private ScrollPane scrollBottomPaneDesignRouteDetails;

    /**
     * Represents the grid pane where the rows of the adjacent stops table
     * will be dynamically populated with data.
     */
    @FXML
    private GridPane gridBottomPaneDesignRouteDetails;

    /**
     * Constructs a DesignRouteBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public DesignRouteBottomController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the design route bottom tab.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
        setNodeVisibility(vBoxDesigningRoute, false);
    }

    /**
     * Shows the designed route by toggling the visibility of the designing route and tips sections.
     */
    public void showDesignedRoute() {
        toggleNodesVisibility(vBoxDesigningRoute, vBoxTips);
    }

    /**
     * Hides the designed route by toggling the visibility of the tips and designing route sections.
     */
    public void hideDesignedRoute() {
        toggleNodesVisibility(vBoxTips, vBoxDesigningRoute);
    }

    /**
     * Updates the dynamic route design by displaying the selected stops and selected routes.
     * It also updates the first stop name, total route metrics, and dynamically populates the route metrics table.
     *
     * @param selectedStops  The list of selected stops for the route.
     * @param selectedRoutes The list of selected routes with their associated cost types.
     */
    public void updateDynamicRouteDesign(List<Vertex<Stop>> selectedStops, List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        showDesignedRoute();

        updateFirstStopName(selectedStops.get(0));
        updateCurrentTotalRouteMetrics(getTransportMetricsTotal(selectedRoutes));
        updateDynamicDesignRouteMetricsTable(getTableData(selectedStops, selectedRoutes));
    }

    /**
     * Retrieves the transport metrics (distance, duration, cost) of the selected routes.
     * It sums up the metrics of all selected routes using the cost type for the transport.
     *
     * @param selectedRoutes The list of selected routes with their associated cost types.
     * @return The total transport metrics (distance, duration, cost) for the selected routes.
     */
    private TransportMetrics getTransportMetricsTotal(List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        TransportMetrics transportMetrics = getEmptyTransportMetrics();

        for (Pair<Edge<Route, Stop>, CostType> edgeCostTypePair : selectedRoutes) {
            Edge<Route, Stop> routeEdge = edgeCostTypePair.getValue0();
            CostType costType = edgeCostTypePair.getValue1();

            Route route = routeEdge.element();
            Triplet<String, Double, TransportMetrics> cheapestTransport = route.getCheapestTransportWithCost(transportMap.getUniqueTransports(), costType);

            TransportMetrics selectedTransportMetrics = cheapestTransport.getValue2();
            transportMetrics.sumMetrics(selectedTransportMetrics);
        }

        return transportMetrics;
    }

    /**
     * Updates the total route metrics (distance, duration, and cost) labels
     * based on the calculated transport metrics.
     *
     * @param transportMetricsTotal The total transport metrics for the selected route.
     */
    private void updateCurrentTotalRouteMetrics(TransportMetrics transportMetricsTotal) {
        String distanceLabel = String.format("%.1f", transportMetricsTotal.getDistance()) + " km";
        String durationLabel = String.format("%.1f", transportMetricsTotal.getDuration()) + " minutes";
        String costLabel = String.format("%.1f", transportMetricsTotal.getCost());

        labelDistance.setText(distanceLabel);
        labelDuration.setText(durationLabel);
        labelCost.setText(costLabel);
    }

    /**
     * Retrieves the table data to display the destination stop name, transport name, and preferred cost.
     *
     * @param selectedStops  The list of selected stops, which includes the destination stop names.
     * @param selectedRoutes The list of selected routes with cost types.
     * @return A list of Triplets containing the destination stop name, transport name, and preferred cost.
     */
    private List<Triplet<String, String, Double>> getTableData(List<Vertex<Stop>> selectedStops, List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        List<Triplet<String, String, Double>> tableData = new ArrayList<>();

        List<Vertex<Stop>> destinationStops = getDestinationStops(selectedStops);

        int currentStop = 0;
        for (Pair<Edge<Route, Stop>, CostType> edgeCostTypePair : selectedRoutes) {
            addStopToTableData(edgeCostTypePair, destinationStops, currentStop, tableData);

            currentStop++;
        }

        return tableData;
    }

    /**
     * Adds a new stop to the table data.
     *
     * @param edgeCostTypePair The pair with the route edge and its preferred cost.
     * @param destinationStops The destination stop vertices list.
     * @param currentStop      The current stop.
     * @param tableData        The table data list that will be updated.
     */
    private void addStopToTableData(Pair<Edge<Route, Stop>, CostType> edgeCostTypePair,
                                    List<Vertex<Stop>> destinationStops, int currentStop,
                                    List<Triplet<String, String, Double>> tableData) {
        Edge<Route, Stop> routeEdge = edgeCostTypePair.getValue0();
        CostType costType = edgeCostTypePair.getValue1();

        Route route = routeEdge.element();
        Triplet<String, Double, TransportMetrics> cheapestTransport = route.getCheapestTransportWithCost(transportMap.getUniqueTransports(), costType);

        String stopName = destinationStops.get(currentStop).element().getName();
        String transportName = cheapestTransport.getValue0();
        Double transportMetric = cheapestTransport.getValue1();

        tableData.add(new Triplet<>(stopName, transportName, transportMetric));
    }

    /**
     * Skips the origin stop and returns a list with all the other stops.
     *
     * @param selectedStops A list with all selected stops.
     * @return The destination stops sub list.
     */
    private static List<Vertex<Stop>> getDestinationStops(List<Vertex<Stop>> selectedStops) {
        return selectedStops.subList(1, selectedStops.size());
    }

    /**
     * Updates the first stop name label based on the selected stop vertex.
     *
     * @param stopVertex The vertex representing the first stop in the route.
     */
    private void updateFirstStopName(Vertex<Stop> stopVertex) {
        Stop stop = stopVertex.element();

        labelFirstStopName.setText(" " + stop.getName());
    }

    /**
     * Updates the grid pane to dynamically display the route metrics table.
     * This method adds the destination stop name, transport name, and preferred cost to the grid.
     *
     * @param tableData The list of route metrics to be displayed in the grid.
     */
    private void updateDynamicDesignRouteMetricsTable(Collection<Triplet<String, String, Double>> tableData) {
        GridPane.setRowSpan(scrollBottomPaneDesignRouteDetails, DESIGNED_ROUTE_ROW_SPAN);

        gridBottomPaneDesignRouteDetails.getChildren().clear();

        int columnIndex = 0;

        // | Destination Stop Name | Transport Name | Preferred Cost |
        for (Triplet<String, String, Double> selectedStop : tableData) {
            columnIndex = addSelectedStopToTheGrid(selectedStop, columnIndex);
        }
    }

    /**
     * Adds a found stop to the grid and updates the column index.
     *
     * @param selectedStop The selected stop to add.
     * @param columnIndex  The current column index.
     * @return The next stop column index.
     */
    private int addSelectedStopToTheGrid(Triplet<String, String, Double> selectedStop, int columnIndex) {
        Label labelDestinationStopName = new Label(selectedStop.getValue0());
        Label labelTransportName = new Label(String.valueOf(selectedStop.getValue1()));
        Label labelPreferredCost = new Label(String.valueOf(selectedStop.getValue2()));

        gridBottomPaneDesignRouteDetails.add(labelDestinationStopName, columnIndex, 0);
        gridBottomPaneDesignRouteDetails.add(labelTransportName, columnIndex, 1);
        gridBottomPaneDesignRouteDetails.add(labelPreferredCost, columnIndex, 2);

        columnIndex++;
        return columnIndex;
    }
}

