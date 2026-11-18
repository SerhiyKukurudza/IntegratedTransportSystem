/******************************************************************************************************************************
 * File Name: CalculateRouteCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.calculateRoute;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Quartet;
import pt.pa.controller.BaseController;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMetrics;
import pt.pa.patterns.command.UserActionCommand;
import pt.pa.utils.TransportColor;

import java.util.Collection;
import java.util.List;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that calculates a route between selected stops.
 */
public class CalculateRouteCommand extends UserActionCommand {

    /**
     * The base controller where the user action will be executed.
     */
    private final BaseController baseController;

    /**
     * The origin stop vertex selected by the user.
     */
    private final Vertex<Stop> originStopVertex;

    /**
     * The destination stop vertex selected by the user.
     */
    private final Vertex<Stop> destinationStopVertex;

    /**
     * The list of middle stop vertices selected by the user for the route.
     */
    private final List<Vertex<Stop>> middleStopVertices;

    /**
     * The collection of path edges representing the calculated route.
     * Each edge contains a {@link Route}, a {@link TransportColor}, and a cost value.
     */
    private final Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges;

    private final TransportMetrics totalTransportMetrics;

    /**
     * Constructs an instance of the CalculateRouteCommand.
     *
     * @param baseController        The controller where the action will be performed.
     * @param originStopVertex      The origin stop vertex selected by the user.
     * @param destinationStopVertex The destination stop vertex selected by the user.
     * @param middleStopVertices    A list of middle stop vertices selected by the user for the route.
     * @param pathEdges             A collection of path edges representing the calculated route.
     * @param totalTransportMetrics A total transport metrics instance previously summed to become the total.
     */
    public CalculateRouteCommand(BaseController baseController, Vertex<Stop> originStopVertex,
                                 Vertex<Stop> destinationStopVertex, List<Vertex<Stop>> middleStopVertices,
                                 Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges,
                                 TransportMetrics totalTransportMetrics) {
        super(getStringResource("userActionDescription.calculateRoute.calculateRoute"), "");

        this.baseController = baseController;
        this.originStopVertex = originStopVertex;
        this.destinationStopVertex = destinationStopVertex;
        this.middleStopVertices = middleStopVertices;
        this.pathEdges = pathEdges;
        this.totalTransportMetrics = totalTransportMetrics;
    }

    /**
     * Executes the CalculateRouteCommand by notifying the controller with the selected stops and the calculated path edges.
     */
    @Override
    public void execute() {
        baseController.notifyCalculatedRoute(originStopVertex, destinationStopVertex, middleStopVertices, pathEdges, totalTransportMetrics);
    }
}