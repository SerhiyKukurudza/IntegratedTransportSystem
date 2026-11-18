/******************************************************************************************************************************
 * File Name: NegativePathfindingStrategy.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.strategy;

import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Triplet;
import pt.pa.exceptions.NegativeEdgeWeightException;
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.CostType;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;
import pt.pa.pathfinding.BellmanFord;
import pt.pa.pathfinding.Dijkstra;

import java.util.List;
import java.util.Set;
import java.util.Map;

/**
 * Represents a concrete implementation of the pathfinding strategy that uses a negative cost approach.
 * <p>
 * This strategy uses Bellman-Ford's algorithm to find the optimal path between two stops in a transport map.
 * The strategy is expected to handle cases with negative edge weights, which is not supported by simpler algorithms
 * like Dijkstra's algorithm.
 * </p>
 */
public class NegativePathfindingStrategy extends PathfindingStrategy {

    /**
     * Finds the optimal path from the origin stop vertex to the destination stop vertex using Bellman-Ford's algorithm.
     * This method calculates the shortest path considering negative costs and returns the path with the associated metrics.
     *
     * @param arguments The pathfinding arguments containing the transport map, origin and destination stop vertices,
     *                  chosen transport modes, and the preferred cost type.
     * @return A list of triplets, each containing a stop vertex, the transport mode used, and the associated transport metrics.
     * @throws IllegalArgumentException If the arguments are invalid or if no path is found.
     */
    @Override
    public List<Triplet<Vertex<Stop>, String, TransportMetrics>> findOptimalPath(PathfindingArguments arguments) {

        validateOptimalPathArguments(arguments);

        return findPathUsingAlgorithm(arguments);
    }

    /**
     * Determines the optimal path using the appropriate algorithm.
     * <p>
     * Attempts to use Bellman-Ford's algorithm first. If negative edge weights cause an exception,
     * Dijkstra's algorithm is used as a fallback.
     * </p>
     *
     * @param arguments The pathfinding arguments containing the transport map, origin and destination stop vertices,
     *                  chosen transport modes, and the preferred cost type.
     * @return A list of triplets with the path details, including stop vertices, transport modes, and transport metrics.
     * @throws IllegalArgumentException If no path is found or the input is invalid.
     */
    private List<Triplet<Vertex<Stop>, String, TransportMetrics>> findPathUsingAlgorithm(PathfindingArguments arguments) {

        try {
            return runBellmanFord(arguments);
        } catch (NegativeEdgeWeightException e) {
            return runDijkstra(arguments);
        } catch (PathNotFoundException e) {
            throw new IllegalArgumentException("Optimal path not found: " + e.getMessage());
        }
    }

    /**
     * Executes Bellman-Ford's algorithm to find the optimal path.
     * <p>
     * This method is used to handle cases with negative edge weights.
     * </p>
     *
     * @param arguments The pathfinding arguments containing the transport map, origin and destination stop vertices,
     *                  chosen transport modes, and the preferred cost type.
     * @return A list of triplets with the path details, including stop vertices, transport modes, and transport metrics.
     * @throws NegativeEdgeWeightException If negative edge weights cause computational issues.
     * @throws PathNotFoundException If no path exists between the origin and destination.
     */
    private List<Triplet<Vertex<Stop>, String, TransportMetrics>> runBellmanFord(PathfindingArguments arguments) throws NegativeEdgeWeightException, PathNotFoundException {

        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> bellmanFordTable =
                BellmanFord.bellmanFord(arguments.getTransportMap(), arguments.getOriginStopVertex(), arguments.getChosenTransports(), arguments.getPreferredCostType());

        return BellmanFord.reconstructPath(arguments.getTransportMap(), arguments.getOriginStopVertex(), arguments.getDestinationStopVertex(), bellmanFordTable);
    }

    /**
     * Executes Dijkstra's algorithm as a fallback for pathfinding.
     * <p>
     * This method is used if Bellman-Ford's algorithm fails due to negative edge weights.
     * </p>
     *
     * @param arguments The pathfinding arguments containing the transport map, origin and destination stop vertices,
     *                  chosen transport modes, and the preferred cost type.
     * @return A list of triplets with the path details, including stop vertices, transport modes, and transport metrics.
     */
    private List<Triplet<Vertex<Stop>, String, TransportMetrics>> runDijkstra(PathfindingArguments arguments) {

        Dijkstra dijkstra = new Dijkstra(arguments.getTransportMap().getRoutes());

        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> dijkstraTable =
                dijkstra.dijkstra(arguments.getTransportMap(), arguments.getOriginStopVertex(), arguments.getChosenTransports(), arguments.getPreferredCostType());

        return dijkstra.reconstructPath(arguments.getOriginStopVertex(), arguments.getDestinationStopVertex(), dijkstraTable);
    }
}
