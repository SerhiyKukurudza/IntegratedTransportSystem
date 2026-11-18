/******************************************************************************************************************************
 * File Name: MapView.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.view;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Graph;
import com.brunomnsilva.smartgraph.graph.Vertex;
import com.brunomnsilva.smartgraph.graphview.*;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;
import org.javatuples.Quartet;
import pt.pa.controller.MapController;
import pt.pa.model.*;
import pt.pa.patterns.observer.Observer;
import pt.pa.utils.TransportColor;
import pt.pa.utils.PropertiesUtil;

import java.awt.*;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static pt.pa.ui.DelayedUIRunner.runCollectionWithDelay;

/**
 * Represents a visual representation of a transport map using a graph structure.
 * This class extends BorderPane to provide a layout for the graph view.
 */
public class MapView extends BorderPane implements Observer {

    /**
     * Represents the path to the styles of the SmartGraph.
     */
    public static final String SMART_GRAPH_STYLES_FILE_PATH = "styles/smartgraph.css";

    /**
     * Represents the path to the SmartGraph properties.
     */
    public static final String SMART_GRAPH_PROPERTIES_FILE_PATH = "properties/smartgraph.properties";

    /**
     * Represents the style class used for the origin stop vertex.
     */
    private static final String ORIGIN_STOP_STYLE = "origin-stop";

    /**
     * Represents the style class used for any destination stop vertices.
     */
    private static final String DESTINATION_STOP_STYLE = "destination-stop";

    /**
     * Represents the style class used for any middle stop vertices.
     */
    private static final String MIDDLE_STOP_STYLE = "middle-stop";
    /**
     * Represents the height of the map panel.
     */
    public static final String MAP_HEIGHT = "720";

    /**
     * Represents the width of the map panel.
     */
    public static final String MAP_WIDTH = "1024";

    /**
     * Represents the Graph View used for the SmartGraph.
     */
    private SmartGraphPanel<Stop, Route> graphView;

    /**
     * Represents the graph that will be rendered with the SmartGraph.
     */
    private final TransportMap transportMap;

    /**
     * Constructs a MapView instance with the specified graph.
     *
     * @param transportMap The transport map with the graph to display the transportation network.
     */
    public MapView(TransportMap transportMap) {
        this.transportMap = transportMap;

        try {
            this.graphView = setupGraphPanel(transportMap.getTransportGraph());
            assignPositionsToStops();
            doLayout();
        } catch (Exception e) {
            System.err.println("Error loading graphic panel: " + e.getMessage());
            System.exit(-1);
        }
    }

    /**
     * Sets up the SmartGraphPanel with the specified graph and properties.
     *
     * @param graph The graph to be visualized in the panel.
     * @return A configured SmartGraphPanel.
     * @throws Exception If an error occurs during setup, including missing resources.
     */
    private SmartGraphPanel<Stop, Route> setupGraphPanel(Graph<Stop, Route> graph) throws Exception {
        InputStream propertiesStream = getClass().getClassLoader().getResourceAsStream(SMART_GRAPH_PROPERTIES_FILE_PATH);
        URL cssUrl = getClass().getClassLoader().getResource(SMART_GRAPH_STYLES_FILE_PATH);

        if (cssUrl != null && propertiesStream != null) {
            SmartGraphProperties properties = new SmartGraphProperties(propertiesStream);
            SmartGraphPanel<Stop, Route> panel = new SmartGraphPanel<>(graph, properties, new SmartRandomPlacementStrategy(), cssUrl.toURI());
            panel.setMaxHeight(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.height", MAP_HEIGHT)));
            panel.setMaxWidth(Integer.parseInt(PropertiesUtil.getInstance().getProperty("map.width", MAP_WIDTH)));
            return panel;
        } else {
            throw new Exception("Properties or CSS settings not found");
        }
    }

    /**
     * Assigns positions to the stops in the graph based on imported XY data.
     * This method retrieves stop positions and updates the graph view accordingly.
     */
    private void assignPositionsToStops() {
        Collection<Vertex<Stop>> vertices = transportMap.getTransportGraph().vertices();

        for (Vertex<Stop> vertex : vertices) {
            Stop stop = vertex.element();

            if (stop != null) {
                Point position = stop.getPosition();
                graphView.setVertexPosition(vertex, position.x, position.y);
            }
        }
    }

    /**
     * Lays out the components of the MapView, setting the graph view at the center.
     */
    private void doLayout() {
        setCenter(this.graphView);
    }

    /**
     * Sets all triggers that this view might trigger during its use.
     *
     * @param mapController The map controller that will activate the triggers.
     */
    public void setTriggers(MapController mapController) {
        graphView.setEdgeDoubleClickAction((routeEdge) -> {
            mapController.notifyObservers(routeEdge.getUnderlyingEdge());
        });

        graphView.setVertexDoubleClickAction((stopVertex) -> {
            mapController.notifyObservers(stopVertex.getUnderlyingVertex());
        });
    }

    /**
     * Applies a style class to the specified origin stop vertex.
     *
     * @param stopVertex The origin stop vertex to mark.
     */
    public void markOriginStop(Vertex<Stop> stopVertex) {
        if (graphView.getStylableVertex(stopVertex) == null) return;
        graphView.getStylableVertex(stopVertex).addStyleClass(ORIGIN_STOP_STYLE);
    }

    /**
     * Applies a style class to the specified destination stop vertex.
     *
     * @param stopVertex The destination stop vertex to mark
     */
    public void markDestinationStop(Vertex<Stop> stopVertex) {
        if (graphView.getStylableVertex(stopVertex) == null) return;
        graphView.getStylableVertex(stopVertex).addStyleClass(DESTINATION_STOP_STYLE);
    }

    /**
     * Marks a collection of stop vertices with a delay, using a specified marking method.
     *
     * @param vertexSupplier A supplier that provides the collection of vertices to mark.
     * @param markingMethod  A method reference or lambda to mark a single vertex.
     * @param delayDuration  The delay duration between marking each vertex.
     */
    public void markStopsWithDelay(Supplier<Collection<Vertex<Stop>>> vertexSupplier,
                                   Consumer<Vertex<Stop>> markingMethod,
                                   Duration delayDuration) {
        List<Runnable> runnables = new ArrayList<>();

        Collection<Vertex<Stop>> stopVertices = vertexSupplier.get();

        for (Vertex<Stop> stopVertex : stopVertices) {
            runnables.add(() -> markingMethod.accept(stopVertex));
        }

        runCollectionWithDelay(runnables, delayDuration);
    }

    /**
     * Marks a collection of routes with a delay, using a specified marking method.
     *
     * @param edgeSupplier  A supplier that provides the collection of edges to mark.
     * @param markingMethod A method reference or lambda to mark a single edge.
     * @param delayDuration The delay duration between marking each edge.
     */
    public void markRoutesWithDelay(Supplier<Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>>> edgeSupplier,
                                    Consumer<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> markingMethod,
                                    Duration delayDuration) {
        List<Runnable> runnables = new ArrayList<>();

        Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> routeEdges = edgeSupplier.get();

        for (Quartet<Edge<Route, Stop>, String, TransportColor, Double> routeEdge : routeEdges) {
            runnables.add(() -> markingMethod.accept(routeEdge));
        }

        runCollectionWithDelay(runnables, delayDuration);
    }

    /**
     * Applies a style class to the specified middle stop vertex.
     *
     * @param stopVertex The middle stop vertex to mark.
     */
    public void markMiddleStop(Vertex<Stop> stopVertex) {
        graphView.getStylableVertex(stopVertex).addStyleClass(MIDDLE_STOP_STYLE);
    }

    /**
     * Removes the origin stop style class from all stop vertices on the map.
     */
    public void unmarkOriginStops() {
        for (Vertex<Stop> stopVertex : transportMap.getStops()) {
            graphView.getStylableVertex(stopVertex).removeStyleClass(ORIGIN_STOP_STYLE);
        }
    }

    /**
     * Removes the destination stop style class from all stop vertices on the map.
     */
    public void unmarkDestinationStops() {
        for (Vertex<Stop> stopVertex : transportMap.getStops()) {
            graphView.getStylableVertex(stopVertex).removeStyleClass(DESTINATION_STOP_STYLE);
        }
    }

    /**
     * Sets the transport map background image with an inline style in the graph view.
     *
     * @param filePath The file path of the background image.
     */
    public void setTransportMapBackgroundImage(String filePath) {
        graphView.setStyle("-fx-background-image: url(" + filePath + ")");
    }

    /**
     * Removes the middle stops style class from all stop vertices on the map.
     */
    public void unmarkMiddleStops() {
        for (Vertex<Stop> stopVertex : transportMap.getStops()) {
            graphView.getStylableVertex(stopVertex).removeStyleClass(MIDDLE_STOP_STYLE);
        }
    }

    /**
     * Changes the opacity of the specified list of route edges.
     *
     * @param edges   The list of edges (routes) whose opacity should be changed.
     * @param opacity The new opacity value to be applied to the edges (0.0 is fully transparent, 1.0 is fully opaque).
     */
    public void changeRoutesOpacity(List<Edge<Route, Stop>> edges, Double opacity) {
        for (Edge<Route, Stop> edge : edges) {
            changeRouteOpacity(edge, opacity);
        }
    }

    /**
     * Changes the opacity of a specific route edge.
     *
     * @param edge    The edge (route) whose opacity should be changed.
     * @param opacity The new opacity value for the edge.
     */
    public void changeRouteOpacity(Edge<Route, Stop> edge, Double opacity) {
        graphView.getStylableEdge(edge).setStyleInline("-fx-opacity: " + opacity + ";");
    }

    private void setRouteLabel(Edge<Route, Stop> edgeRoute, String value) {
        SmartLabel smartLabel = (SmartLabel) graphView.getStylableLabel(edgeRoute);
        smartLabel.setText(String.valueOf(value));
    }

    /**
     * Marks a specific route by changing its visual style.
     *
     * @param routeInfo A Triplet containing the edge (route) to be marked,
     *                  the color to apply, and the stroke width (unused in this method).
     */
    public void markRoute(Quartet<Edge<Route, Stop>, String, TransportColor, Double> routeInfo) {
        Edge<Route, Stop> edgeRoute = routeInfo.getValue0();
        TransportColor transportColor = routeInfo.getValue2();
        Double transportCost = routeInfo.getValue3();

        String edgeStyle = "-fx-stroke: " + transportColor + ";  -fx-opacity: 1;";
        graphView.getStylableEdge(edgeRoute).setStyleInline(edgeStyle);

        setRouteLabel(edgeRoute, String.valueOf(transportCost));
    }

    /**
     * Unmarks all the routes by removing any applied styles.
     * <p>
     * This method iterates through all the routes in the transport map and clears any
     * previously applied styles, effectively resetting their visual appearance.
     * </p>
     */
    public void unmarkRoutes() {
        for (Edge<Route, Stop> edge : transportMap.getRoutes()) {
            graphView.getStylableEdge(edge).setStyleInline("");
            setRouteLabel(edge, "");
        }
    }

    @Override
    public void update(Object updatedModel) {}
}