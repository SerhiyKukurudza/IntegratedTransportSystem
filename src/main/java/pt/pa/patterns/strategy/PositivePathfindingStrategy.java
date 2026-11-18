/******************************************************************************************************************************
 * File Name: PositivePathfindingStrategy.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.strategy;

import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Triplet;
import pt.pa.model.CostType;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;
import pt.pa.pathfinding.Dijkstra;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Represents a concrete implementation of the pathfinding strategy that uses positive edge weights.
 * <p>
 * This strategy uses Dijkstra's algorithm to find the optimal path between two stops in a transport map.
 * It works with positive edge weights and calculates the shortest path based on the selected transport modes and preferred cost type.
 * </p>
 */
public class PositivePathfindingStrategy extends PathfindingStrategy {

    /**
     * Finds the optimal path from the origin stop vertex to the destination stop vertex using Dijkstra's algorithm.
     * <p>
     * The method calculates the shortest path considering only positive edge weights, taking into account the chosen transport
     * modes and preferred cost type. The result is a reconstructed path with the associated transport metrics.
     * </p>
     *
     * @param arguments The arguments containing all necessary information for pathfinding, including:
     *                  - The transport map containing all routes and stops.
     *                  - The origin and destination stop vertices.
     *                  - The set of chosen transport modes to consider.
     *                  - The preferred cost type (e.g., duration, distance, etc.).
     * @return A list of triplets, each containing:
     *         <ul>
     *         <li>A stop vertex at each step of the path.</li>
     *         <li>The transport mode used to traverse between the stops.</li>
     *         <li>The transport metrics (e.g., duration, distance, cost) associated with that segment of the journey.</li>
     *         </ul>
     * @throws IllegalArgumentException If the arguments are invalid, such as null values or non-existent stops.
     */
    @Override
    public List<Triplet<Vertex<Stop>, String, TransportMetrics>> findOptimalPath(PathfindingArguments arguments) {
        validateOptimalPathArguments(arguments);

        return reconstructOptimalPath(arguments);
    }

    /**
     * Executes Dijkstra's algorithm to calculate the optimal path.
     * <p>
     * This method uses Dijkstra's algorithm to compute the shortest path, considering the transport map, chosen
     * transport modes, and the preferred cost type.
     * </p>
     *
     * @param arguments The arguments containing all necessary information for pathfinding.
     * @return A map containing the results of Dijkstra's algorithm.
     */
    private Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> executeDijkstraAlgorithm(PathfindingArguments arguments) {
        Dijkstra dijkstra = new Dijkstra();

        // Execute Dijkstra's algorithm to compute the shortest path.
        return dijkstra.dijkstra(arguments.getTransportMap(), arguments.getOriginStopVertex(),
                arguments.getChosenTransports(), arguments.getPreferredCostType());
    }

    /**
     * Reconstructs the optimal path from the results of Dijkstra's algorithm.
     * <p>
     * This method rebuilds the optimal path using the computed results from Dijkstra's algorithm.
     * </p>
     *
     * @param arguments The arguments containing the origin and destination stops.
     * @return A list of triplets, each containing a stop vertex, the transport mode used, and the associated transport metrics.
     */
    private List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructOptimalPath(PathfindingArguments arguments) {
        // Execute Dijkstra's algorithm to get the results.
        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> dijkstraTable = executeDijkstraAlgorithm(arguments);

        // Reconstruct the optimal path from the Dijkstra results.
        Dijkstra dijkstra = new Dijkstra();
        return dijkstra.reconstructPath(arguments.getOriginStopVertex(), arguments.getDestinationStopVertex(), dijkstraTable);
    }
}
