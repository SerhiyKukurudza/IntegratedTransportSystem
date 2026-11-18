/******************************************************************************************************************************
 * File Name: PathfindingStrategy.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.strategy;

import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import pt.pa.exceptions.InvalidStopException;
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.CostType;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Represents the Pathfinding Strategy interface of the Strategy pattern.
 * This strategy allows the selection of the preferred pathfinding strategy.
 * Instead of an interface, it is an abstract class, so it also performs all necessary common work.
 */
public abstract class PathfindingStrategy {
    /**
     * Finds the optimal path between two vertices in the transport map using a specified strategy.
     * The method calculates the path based on the given parameters, returning detailed information about the stops,
     * transport modes, and associated metrics. If no path is found, an exception is thrown.
     *
     * @param arguments The arguments containing all necessary information for pathfinding, including the transport map,
     *                  the origin and destination stops, the chosen transport modes, and the preferred cost type.
     * @return A list of triplets, each containing:
     *         <ul>
     *         <li>The stop vertex at each step of the path.</li>
     *         <li>The transport mode used to traverse between the stops.</li>
     *         <li>The transport metrics (e.g., distance, time, cost) associated with that segment of the journey.</li>
     *         </ul>
     * @throws PathNotFoundException If no viable path can be found between the origin and destination vertices.
     */
    public abstract List<Triplet<Vertex<Stop>, String, TransportMetrics>> findOptimalPath(PathfindingArguments arguments);

    /**
     * Validates the arguments passed to the {@code findOptimalPath} method implementations.
     * This ensures that all necessary inputs are provided and meet the required conditions.
     *
     * @param arguments The arguments containing all necessary information for pathfinding validation.
     * @throws InvalidStopException If any of the stop vertices (origin or destination) are invalid or do not exist in the transport map.
     * @throws IllegalArgumentException If:
     *                                  <ul>
     *                                  <li>The transport map is null.</li>
     *                                  <li>The origin or destination vertex does not exist in the transport map.</li>
     *                                  <li>The origin and destination refer to the same stop.</li>
     *                                  <li>The preferred cost type is null.</li>
     *                                  <li>The chosen transports set is null or empty.</li>
     *                                  </ul>
     */
    public void validateOptimalPathArguments(PathfindingArguments arguments) {
        validateTransportMap(arguments.getTransportMap());
        validateStopVertex(arguments.getTransportMap(), arguments.getOriginStopVertex(), "Origin");
        validateStopVertex(arguments.getTransportMap(), arguments.getDestinationStopVertex(), "Destination");
        validateCostType(arguments.getPreferredCostType());
        validateChosenTransports(arguments.getChosenTransports());
        validateDifferentStops(arguments.getOriginStopVertex(), arguments.getDestinationStopVertex());
    }

    /**
     * Validates that the transport map is not null.
     *
     * @param transportMap The transport map to validate.
     * @throws IllegalArgumentException If the transport map is null.
     */
    private void validateTransportMap(TransportMap transportMap) {
        if (transportMap == null)
            throw new IllegalArgumentException("Transport map cannot be null");
    }

    /**
     * Validates that the stop vertex exists in the transport map and is not null.
     *
     * @param transportMap The transport map where the stop vertex should exist.
     * @param stopVertex   The stop vertex to validate.
     * @param vertexType   The type of vertex ("Origin" or "Destination") for error message context.
     * @throws InvalidStopException     If the stop vertex does not exist in the transport map.
     * @throws IllegalArgumentException If the stop vertex is null.
     */
    private void validateStopVertex(TransportMap transportMap, Vertex<Stop> stopVertex, String vertexType) {
        if (stopVertex == null)
            throw new IllegalArgumentException(vertexType + " stop vertex cannot be null");

        if (!transportMap.stopExists(stopVertex))
            throw new InvalidStopException(vertexType + " does not exist in transport map");
    }

    /**
     * Validates that the preferred cost type is not null.
     *
     * @param preferredCostType The preferred cost type to validate.
     * @throws IllegalArgumentException If the preferred cost type is null.
     */
    private void validateCostType(CostType preferredCostType) {
        if (preferredCostType == null)
            throw new IllegalArgumentException("Preferred cost type cannot be null");
    }

    /**
     * Validates that the chosen transports set is neither null nor empty.
     *
     * @param chosenTransports The set of chosen transport modes to validate.
     * @throws IllegalArgumentException If the chosen transports set is null or empty.
     */
    private void validateChosenTransports(Set<String> chosenTransports) {
        if (chosenTransports == null || chosenTransports.isEmpty())
            throw new IllegalArgumentException("Chosen transports cannot be null nor empty");
    }

    /**
     * Validates that the origin and destination stop vertices are not the same.
     *
     * @param originStopVertex      The origin stop vertex.
     * @param destinationStopVertex The destination stop vertex.
     * @throws IllegalArgumentException If the origin and destination vertices are the same.
     */
    private void validateDifferentStops(Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex) {
        if (originStopVertex.equals(destinationStopVertex))
            throw new IllegalArgumentException("Origin and destination vertex are the same");
    }
}
