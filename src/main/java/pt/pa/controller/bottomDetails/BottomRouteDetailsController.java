/******************************************************************************************************************************
 * File Name: BottomRouteDetailsController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.bottomDetails;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.javatuples.Pair;
import org.kordamp.ikonli.javafx.FontIcon;
import org.kordamp.ikonli.javafx.Icon;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.model.*;
import pt.pa.patterns.command.concrete.findStops.ViewRouteCommand;
import pt.pa.patterns.observer.Observer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;

/**
 * Represents the controller class for the BottomRouteDetails view.
 */
public class BottomRouteDetailsController extends TransportBaseController implements Observer, BottomDetailsController {

    /**
     * Constant that holds the row span of the route details grid.
     * The number of rows plus 2 to add enough space for the scroll bar
     */
    public static final int ROUTE_DETAILS_ROW_SPAN = 5 + 2;

    /**
     * Represents the enabled transport icon.
     */
    public static final String ENABLED_TRANSPORT_ICON = "mdi-check";

    /**
     * Represents the enabled transport color.
     */
    public static final String ENABLED_TRANSPORT_COLOR = "green";

    /**
     * Represents the enabled transport icon.
     */
    public static final String DISABLED_TRANSPORT_ICON = "mdi-close";

    /**
     * Represents the enabled transport color.
     */
    public static final String DISABLED_TRANSPORT_COLOR = "red";

    /**
     * Represents the enabled transport icon size.
     */
    public static final int ENABLED_TRANSPORT_ICON_SIZE = 20;

    /**
     * Represents the pane where the view of this controller lies.
     */
    @FXML
    private Pane paneBottomRouteDetails;

    /**
     * Represents the button to close the route details pane.
     */
    @FXML
    private Button buttonClose;

    /**
     * Represents the label displaying the name of the first stop on the route.
     */
    @FXML
    private Label labelFirstStopName;

    /**
     * Represents the label displaying the number of connections at the first stop.
     */
    @FXML
    private Label labelFirstStopConnections;

    /**
     * Represents the label displaying the latitude of the first stop on the route.
     */
    @FXML
    private Label labelFirstStopLatitude;

    /**
     * Represents the label displaying the longitude of the first stop on the route.
     */
    @FXML
    private Label labelFirstStopLongitude;

    /**
     * Represents the label displaying the name of the second stop on the route.
     */
    @FXML
    private Label labelSecondStopName;

    /**
     * Represents the label displaying the number of connections at the second stop.
     */
    @FXML
    private Label labelSecondStopConnections;

    /**
     * Represents the label displaying the latitude of the second stop on the route.
     */
    @FXML
    private Label labelSecondStopLatitude;

    /**
     * Represents the label displaying the longitude of the second stop on the route.
     */
    @FXML
    private Label labelSecondStopLongitude;

    /**
     * Represents the scroll pane where the adjacent stops are stored at.
     */
    @FXML
    private ScrollPane scrollBottomPaneRouteDetails;

    /**
     * Represents the grid where the adjacent stops will be dynamically added to.
     */
    @FXML
    private GridPane gridBottomPaneRouteDetails;

    /**
     * Represents the currently selected route edge.
     */
    private Edge<Route, Stop> routeEdge;

    /**
     * Constructs a BottomRouteDetailsController instance with a transport map model and its base controller.
     *
     * @param transportMap   The transport map instance.
     * @param baseController The base controller instance.
     */
    public BottomRouteDetailsController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the base view.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);

        turnBottomDetailsNotVisible();
        setTriggers();
    }

    /**
     * Makes the bottom details pane visible.
     */
    private void turnBottomDetailsVisible() {
        setNodeVisibility(paneBottomRouteDetails, true);
    }

    /**
     * Makes the bottom details pane invisible.
     */
    public void turnBottomDetailsNotVisible() {
        setNodeVisibility(paneBottomRouteDetails, false);
    }

    /**
     * Sets up the triggers for user interactions with elements in the view.
     */
    private void setTriggers() {
        buttonClose.setOnAction(e -> {
            turnBottomDetailsNotVisible();
        });
    }

    /**
     * Selects the route that was notified with the observer pattern.
     * Updates the tab state and shows the route as is.
     *
     * @param routeEdge The selected route edge.
     */
    private void selectRoute(Edge<Route, Stop> routeEdge) {
        this.routeEdge = routeEdge;
        showRouteDetails(routeEdge);
    }

    /**
     * Displays the details of the selected route edge in the bottom details' pane.
     * This includes making the pane visible, dynamically populating the route details table,
     * and showing the incident stop details.
     *
     * @param routeEdge the edge representing the route whose details are to be displayed.
     */
    public void showRouteDetails(Edge<Route, Stop> routeEdge) {
        turnBottomDetailsVisible();

        UserActionLogger.getInstance().performUserAction(
                new ViewRouteCommand(routeEdge)
        );

        updateDynamicRouteDetailsTable(routeEdge);
        updateRouteIncidentStops(routeEdge);
    }

    /**
     * Dynamically populates the route details table with transport metrics.
     * For each transport type in the route, it displays the type, distance, duration, and cost
     * in the grid pane.
     *
     * @param edgeRoute the edge containing the route and its transport details to display.
     */
    private void updateDynamicRouteDetailsTable(Edge<Route, Stop> edgeRoute) {
        gridBottomPaneRouteDetails.getChildren().clear();
        GridPane.setRowSpan(scrollBottomPaneRouteDetails, ROUTE_DETAILS_ROW_SPAN);

        Collection<Transport> transports = edgeRoute.element().getTransports();

        int columnIndex = 0;
        for (Transport transport : transports) {
            columnIndex = addTransportToGrid(transport, columnIndex);
        }
    }

    /**
     * Adds a transport information entry in the grid.
     *
     * @param transport   The pair with the name and metrics of a transport mode.
     * @param columnIndex The current column index in the grid.
     * @return The updated value of the column index for the next grid item.
     */
    private int addTransportToGrid(Transport transport, int columnIndex) {
        String transportName = transport.getName();
        TransportMetrics metrics = transport.getTransportMetrics();

        Label labelTransportMode = new Label(transportName);
        Label labelTransportDistance = new Label(String.valueOf(metrics.getDistance()));
        Label labelTransportDuration = new Label(String.valueOf(metrics.getDuration()));
        Label labelTransportCost = new Label(String.valueOf(metrics.getCost()));
        FontIcon iconTransportEnabled = getTransportEnabledIcon(transport.isEnabled());

        gridBottomPaneRouteDetails.add(labelTransportMode, columnIndex, 0);
        gridBottomPaneRouteDetails.add(labelTransportDuration, columnIndex, 1);
        gridBottomPaneRouteDetails.add(labelTransportDistance, columnIndex, 2);
        gridBottomPaneRouteDetails.add(labelTransportCost, columnIndex, 3);
        gridBottomPaneRouteDetails.add(iconTransportEnabled, columnIndex, 4);

        columnIndex++;
        return columnIndex;
    }

    /**
     * Gets the transport enabled icon according to its state.
     *
     * @param isEnabled Whether the transport is enabled or not.
     * @return The {@code FontIcon} instance with the correct icon and color.
     */
    private static FontIcon getTransportEnabledIcon(boolean isEnabled) {
        FontIcon icon;

        if (isEnabled) {
            icon = new FontIcon(ENABLED_TRANSPORT_ICON);
            icon.setIconColor(Color.web(ENABLED_TRANSPORT_COLOR));
        } else {
            icon = new FontIcon(DISABLED_TRANSPORT_ICON);
            icon.setIconColor(Color.web(DISABLED_TRANSPORT_COLOR));
        }

        icon.setIconSize(ENABLED_TRANSPORT_ICON_SIZE);

        return icon;
    }

    /**
     * Displays details of the incident stops of the selected route in the bottom details' pane.
     * This includes showing the name, number of connections, latitude, and longitude
     * for both the first and second stops of the route.
     *
     * @param routeEdge the edge representing the route whose incident stop details are to be displayed.
     */
    public void updateRouteIncidentStops(Edge<Route, Stop> routeEdge) {
        Vertex<Stop> stopVertex = routeEdge.vertices()[0];
        Vertex<Stop> stopVertex2 = routeEdge.vertices()[1];

        Stop stop = stopVertex.element();
        updateFirstStopDetails(stop, stopVertex);

        Stop stop2 = stopVertex2.element();
        updateSecondStopDetails(stop2, stopVertex2);
    }

    /**
     * Updates the labels that show the first incident stop details.
     *
     * @param firstStopVertex The first stop vertex.
     */
    private void updateFirstStopDetails(Stop stop, Vertex<Stop> firstStopVertex) {
        labelFirstStopName.setText(stop.getName());
        labelFirstStopConnections.setText(String.valueOf(transportMap.getNumAdjacentStops(firstStopVertex)));
        labelFirstStopLongitude.setText(String.valueOf(stop.getLongitude()));
        labelFirstStopLatitude.setText(String.valueOf(stop.getLatitude()));
    }

    /**
     * Updates the labels that show the second incident stop details.
     *
     * @param secondStopVertex The second stop vertex.
     */
    private void updateSecondStopDetails(Stop stop2, Vertex<Stop> secondStopVertex) {
        labelSecondStopName.setText(stop2.getName());
        labelSecondStopConnections.setText(String.valueOf(transportMap.getNumAdjacentStops(secondStopVertex)));
        labelSecondStopLongitude.setText(String.valueOf(stop2.getLongitude()));
        labelSecondStopLatitude.setText(String.valueOf(stop2.getLatitude()));
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        if (routeEdge == null) return;

        showRouteDetails(routeEdge);
    }

    /**
     * Updates the view every time the subject notifies its observers.
     *
     * @param object - The updated object.
     */
    @Override
    public void update(Object object) {
        if (object instanceof TransportMap) {
            loadDynamicContent();
        } else if (object instanceof Edge<?, ?> routeEdge) {
            selectRoute((Edge<Route, Stop>) routeEdge);
        } else if (object instanceof Vertex<?>) {
            turnBottomDetailsNotVisible();
        }
    }
}
