/******************************************************************************************************************************
 * File Name: NegativePathfindingStrategy.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.strategy;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.model.CostType;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;

import java.util.Set;

/**
 * Represents the arguments required for pathfinding algorithms.
 * <p>
 * This class encapsulates the necessary information to find the optimal path between two stops in a transport map,
 * including the transport map, the origin and destination stops, the chosen transport modes, and the preferred cost type.
 * </p>
 */
public class PathfindingArguments {
    /**
     * The transport map that contains all the routes and stops.
     */
    private final TransportMap transportMap;
    /**
     * The origin stop vertex from which the pathfinding starts.
     */
    private final Vertex<Stop> originStopVertex;
    /**
     * The destination stop vertex to which the pathfinding aims to reach.
     */
    private final Vertex<Stop> destinationStopVertex;
    /**
     * The set of transport modes that should be considered in the pathfinding.
     */
    private final Set<String> chosenTransports;
    /**
     * The preferred cost type to use when evaluating the optimal path (e.g., duration, distance, etc.).
     */
    private final CostType preferredCostType;

    /**
     * Constructs a new PathfindingArguments object with the provided parameters.
     *
     * @param transportMap       The transport map containing all the routes and stops.
     * @param originStopVertex   The origin stop vertex from which the pathfinding starts.
     * @param destinationStopVertex The destination stop vertex to which the pathfinding aims to reach.
     * @param chosenTransports   A set of chosen transport modes that should be considered.
     * @param preferredCostType  The preferred cost type to evaluate the path (e.g., duration, distance).
     */
    public PathfindingArguments(TransportMap transportMap, Vertex<Stop> originStopVertex,
                                Vertex<Stop> destinationStopVertex, Set<String> chosenTransports,
                                CostType preferredCostType) {
        this.transportMap = transportMap;
        this.originStopVertex = originStopVertex;
        this.destinationStopVertex = destinationStopVertex;
        this.chosenTransports = chosenTransports;
        this.preferredCostType = preferredCostType;
    }

    /**
     * Gets the transport map containing all routes and stops.
     *
     * @return The transport map.
     */
    public TransportMap getTransportMap() {
        return transportMap;
    }

    /**
     * Gets the origin stop vertex from which the pathfinding starts.
     *
     * @return The origin stop vertex.
     */
    public Vertex<Stop> getOriginStopVertex() {
        return originStopVertex;
    }

    /**
     * Gets the destination stop vertex to which the pathfinding aims to reach.
     *
     * @return The destination stop vertex.
     */
    public Vertex<Stop> getDestinationStopVertex() {
        return destinationStopVertex;
    }

    /**
     * Gets the set of chosen transport modes to be considered in the pathfinding.
     *
     * @return The set of chosen transport modes.
     */
    public Set<String> getChosenTransports() {
        return chosenTransports;
    }

    /**
     * Gets the preferred cost type for pathfinding (e.g., duration, distance).
     *
     * @return The preferred cost type.
     */
    public CostType getPreferredCostType() {
        return preferredCostType;
    }
}