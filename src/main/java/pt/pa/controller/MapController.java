/******************************************************************************************************************************
 * File Name: MapController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import org.javatuples.Pair;
import pt.pa.model.*;
import pt.pa.patterns.observer.ClickNotifier;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;
import pt.pa.utils.TransportColor;
import pt.pa.view.MapView;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller of the map of transportation.
 */
public class MapController extends TransportBaseController {
    /**
     * Constant for delay per stop (in milliseconds)
     */
    public static final long MARK_MIDDLE_STOPS_DELAY_MS = 300;

    /**
     * Constant for delay per stop for destinations only (in milliseconds)
     */
    public static final int MARK_DESTINATION_STOPS_DELAY_MS = 100;

    /**
     * Represents the map view used to show the transport map.
     */
    private final MapView mapView;

    /**
     * Represents the double-clicking notification (Observer pattern) system.
     */
    private final ClickNotifier clickNotifier;

    /**
     * Constructs an instance of the MapController.
     *
     * @param transportMap The transport map model to control.
     * @param mapView      The map view to represent.
     */
    public MapController(TransportMap transportMap, MapView mapView) {
        super(transportMap);
        this.mapView = mapView;

        mapView.setTriggers(this);
        transportMap.addObservers(mapView);

        clickNotifier = new ClickNotifier();
    }

    /**
     * Gets the map view stored in the map controller.
     *
     * @return The map view.
     */
    public Pane getMapView() {
        return this.mapView;
    }

    /**
     * Notify the observers to the click notifier.
     *
     * @param object The object to update the observers.
     */
    public void notifyObservers(Object object) {
        clickNotifier.notifyObservers(object);
    }

    /**
     * Add observers to the click notifier.
     *
     * @param observers The observers to add.
     */
    public void addObservers(Observer... observers) {
        clickNotifier.addObservers(observers);
    }

    /**
     * Unmarks all previous marked stops and marks the new origin stop in the view.
     *
     * @param originVertex           The origin stop to mark.
     * @param unmarkDestinationStops A flag that unmarks the destinations stops if set to {@code true}.
     */
    public void doMarkOriginStop(Vertex<Stop> originVertex, boolean unmarkDestinationStops) {
        mapView.unmarkMiddleStops();
        mapView.unmarkOriginStops();
        mapView.unmarkRoutes();

        if (unmarkDestinationStops) mapView.unmarkDestinationStops();

        mapView.markOriginStop(originVertex);
    }

    /**
     * Unmarks all previous marked stops and marks the new destination stop in the view.
     *
     * @param destinationVertex The destination stop to mark.
     */
    public void doMarkDestinationStop(Vertex<Stop> destinationVertex) {
        mapView.unmarkMiddleStops();
        mapView.unmarkDestinationStops();
        mapView.unmarkRoutes();
        mapView.markDestinationStop(destinationVertex);
    }

    /**
     * Unmarks all previous destination stops and marks the new destination stops in the map.
     * If no stops were found, a notification is sent.
     *
     * @param stopVertices The destination stops to mark.
     */
    public void doMarkDestinationStops(Collection<Vertex<Stop>> stopVertices) {
        mapView.unmarkDestinationStops();

        if (stopVertices.isEmpty()) {
            ControlNotification.show(
                    getStringResource("mapController.notification.title"),
                    getStringResource("mapController.notification.content"),
                    ControlNotification.NotificationType.INFORMATION
            );
            return;
        }

        mapView.markStopsWithDelay(() -> stopVertices, mapView::markDestinationStop, Duration.millis(MARK_DESTINATION_STOPS_DELAY_MS));
    }

    /**
     * Marks a list of middle stops.
     *
     * @param stopVertices The stop vertices.
     */
    public void doMarkMiddleStops(List<Vertex<Stop>> stopVertices) {
        for (Vertex<Stop> stopVertex : stopVertices) {
            mapView.markMiddleStop(stopVertex);
        }
    }

    /**
     * Marks a collection of middle stops with a delay.
     *
     * @param stops The list of middle stops to mark.
     */
    public void doMarkMiddleStopsWithDelay(List<Vertex<Stop>> stops) {
        Supplier<Collection<Vertex<Stop>>> vertexSupplier = () -> stops;
        Consumer<Vertex<Stop>> markingMethod = mapView::markMiddleStop;
        Duration delayByStop = Duration.millis(MARK_MIDDLE_STOPS_DELAY_MS);

        mapView.markStopsWithDelay(vertexSupplier, markingMethod, delayByStop);
    }

    /**
     * Unmarks all stops from the view.
     */
    public void doUnmarkStops() {
        mapView.unmarkOriginStops();
        mapView.unmarkMiddleStops();
        mapView.unmarkDestinationStops();
    }

    /**
     * Marks routes in the map with a collection of edge routes and their preferred cost type.
     *
     * @param selectedRoutes A collection of pairs, where each pair consists of a route edge
     *                       and the associated preferred cost type.
     */
    public void doMarkRoutes(Collection<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        for (Pair<Edge<Route, Stop>, CostType> edgeCostTypePair : selectedRoutes) {
            markEdgeRouteByCostType(edgeCostTypePair);
        }
    }

    /**
     * Mark an edge route based on the cheapest transport mode by a specified cost metric.
     *
     * @param edgeCostTypePair The pair consisting of a route edge and the cost metric.
     */
    private void markEdgeRouteByCostType(Pair<Edge<Route, Stop>, CostType> edgeCostTypePair) {
        Edge<Route, Stop> routeEdge = edgeCostTypePair.getValue0();
        CostType costType = edgeCostTypePair.getValue1();

        Route route = routeEdge.element();
        Triplet<String, Double, TransportMetrics> cheapestTransport = route.getCheapestTransportWithCost(transportMap.getUniqueTransports(), costType);

        String transportName = cheapestTransport.getValue0();
        TransportColor transportColor = transportMap.getTransportModeColor(transportName);
        Double transportMetric = cheapestTransport.getValue1();

        mapView.markRoute(new Quartet<>(routeEdge, transportName, transportColor, transportMetric));
    }

    /**
     * Marks the routes in the given path with a delay, while reducing the opacity of other routes.
     * The path is represented by a collection of triplets, each containing an edge (route),
     * a color, and a delay value for marking the route.
     *
     * @param path A collection of triplets representing the edges (routes) to be marked,
     *             along with the associated color and delay for each route.
     */
    public void doMarkRoutesWithDelay(Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> path) {
        Set<Edge<Route, Stop>> pathEdges = getPathEdgesFromQuartet(path);
        List<Edge<Route, Stop>> otherEdges = getOtherEdgesFromQuartet(pathEdges);

        mapView.changeRoutesOpacity(otherEdges, 0.2);
        mapView.markRoutesWithDelay(() -> path, mapView::markRoute, Duration.millis(MARK_MIDDLE_STOPS_DELAY_MS));
    }

    /**
     * Gets the path edges from the quartet by stream iteration.
     *
     * @param path The path quartet that has all path edges.
     * @return A set with all path edges.
     */
    private Set<Edge<Route, Stop>> getPathEdgesFromQuartet(Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> path) {
        return path.stream()
                .map(Quartet::getValue0)
                .collect(Collectors.toSet());
    }

    /**
     * Gets all edges that are not part of the calculated path edges.
     *
     * @param pathEdges The path edges that should not be included.
     * @return A list with all other edges.
     */
    private List<Edge<Route, Stop>> getOtherEdgesFromQuartet(Set<Edge<Route, Stop>> pathEdges) {
        List<Edge<Route, Stop>> otherEdges = new ArrayList<>();

        for (Edge<Route, Stop> edge : transportMap.getRoutes()) {
            if (!pathEdges.contains(edge)) {
                otherEdges.add(edge);
            }
        }
        return otherEdges;
    }

    /**
     * Unmarks all marked routes in the map.
     */
    public void doUnmarkRoutes() {
        mapView.unmarkRoutes();
    }

    /**
     * Performs the setting of the transport map background with a file path.
     *
     * @param filePath The file path of the background image.
     */
    public void doSetTransportMapBackgroundImage(String filePath) {
        mapView.setTransportMapBackgroundImage(filePath);
    }
}
