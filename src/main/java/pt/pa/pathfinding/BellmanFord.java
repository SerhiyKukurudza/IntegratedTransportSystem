/******************************************************************************************************************************
 * File Name: BellmanFord.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.pathfinding;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.InvalidVertexException;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import pt.pa.exceptions.NegativeEdgeWeightException;
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.*;

import java.util.*;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a helper class that implements the Bellman-Ford algorithm to find the shortest paths in a transport map,
 * even in the presence of negative-weight edges and cycles.
 */
public class BellmanFord {

    /**
     * Performs the Bellman-Ford algorithm to compute the shortest paths from the origin stop vertex
     * to all other vertices in the transport map. This algorithm supports graphs with negative edge weights.
     *
     * @param transportMap       The transport map containing the graph of stops and routes.
     * @param originStopVertex   The starting vertex (origin stop) for the pathfinding process.
     * @param chosenTransports   A set of transport modes to consider during the search (e.g., bus, tram).
     * @param preferredCostType  The cost type to optimize for (e.g., time, distance, or custom metrics).
     * @return A map of vertices to their shortest path details, including predecessor, transport name, and metrics.
     * @throws IllegalArgumentException   If the transport map or origin vertex is null.
     * @throws InvalidVertexException     If the origin vertex is not part of the transport map.
     * @throws NegativeEdgeWeightException If a negative-weight cycle is detected in the graph.
     */
    public static Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> bellmanFord(
            TransportMap transportMap, Vertex<Stop> originStopVertex,
            Set<String> chosenTransports, CostType preferredCostType) throws NegativeEdgeWeightException {

        validateInputParameters(transportMap, originStopVertex);

        Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable = initializeCostTable(transportMap, originStopVertex);

        relaxEdges(transportMap, chosenTransports, preferredCostType, costTable);

        detectNegativeWeightCycles(transportMap, chosenTransports, preferredCostType, costTable);

        return convertCostTableToOutputFormat(costTable);
    }

    /**
     * Validates the input parameters for the Bellman-Ford algorithm.
     *
     * @param transportMap      The transport map representing the graph.
     * @param originStopVertex  The origin vertex to validate.
     * @throws IllegalArgumentException  If the transport map or origin vertex is null.
     * @throws InvalidVertexException    If the origin vertex is not part of the transport map.
     */
    private static void validateInputParameters(TransportMap transportMap, Vertex<Stop> originStopVertex) {
        if (transportMap == null || originStopVertex == null) {
            throw new IllegalArgumentException("Transport map and origin vertex cannot be null.");
        }

        if (!transportMap.getStops().contains(originStopVertex)) {
            throw new InvalidVertexException("Origin vertex is not part of the transport map.");
        }
    }

    /**
     * Initializes the cost table for all vertices in the transport map.
     *
     * @param transportMap      The transport map containing the vertices.
     * @param originStopVertex  The origin vertex to initialize with a cost of 0.
     * @return A map of vertices to their initial cost details.
     */
    private static Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> initializeCostTable(
            TransportMap transportMap, Vertex<Stop> originStopVertex) {

        Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable = new HashMap<>();

        for (Vertex<Stop> vertex : transportMap.getStops()) {
            costTable.put(vertex, new Quartet<>(null, null, Double.POSITIVE_INFINITY, null));
        }

        costTable.put(originStopVertex, new Quartet<>(null, null, 0.0, null));

        return costTable;
    }

    /**
     * Performs the edge relaxation step of the Bellman-Ford algorithm for |V| - 1 iterations.
     *
     * @param transportMap      The transport map containing the edges to relax.
     * @param chosenTransports  The set of usable transport modes.
     * @param preferredCostType The cost type to optimize for.
     * @param costTable         The current cost table.
     */
    private static void relaxEdges(
            TransportMap transportMap, Set<String> chosenTransports,
            CostType preferredCostType, Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable) {

        int vertexCount = transportMap.getStops().size();

        for (int i = 0; i < vertexCount - 1; i++) {
            for (Edge<Route, Stop> edge : transportMap.getRoutes()) {
                relaxEdge(edge.vertices()[0], edge.vertices()[1], edge, costTable, chosenTransports, preferredCostType);
                relaxEdge(edge.vertices()[1], edge.vertices()[0], edge, costTable, chosenTransports, preferredCostType);
            }
        }
    }

    /**
     * Checks for negative-weight cycles in the graph.
     *
     * @param transportMap      The transport map to analyze.
     * @param chosenTransports  The set of usable transport modes.
     * @param preferredCostType The cost type to optimize for.
     * @param costTable         The current cost table.
     * @throws NegativeEdgeWeightException If a negative-weight cycle is detected.
     */
    private static void detectNegativeWeightCycles(
            TransportMap transportMap, Set<String> chosenTransports,
            CostType preferredCostType, Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable) {

        for (Edge<Route, Stop> edge : transportMap.getRoutes()) {
            Vertex<Stop> u = edge.vertices()[0];
            Vertex<Stop> v = edge.vertices()[1];

            Triplet<String, Double, TransportMetrics> preferredCost = edge.element()
                    .getCheapestTransportWithCost(chosenTransports, preferredCostType);

            if (preferredCost != null && costTable.get(u).getValue2() + preferredCost.getValue1() < costTable.get(v).getValue2()) {
                throw new NegativeEdgeWeightException(getStringResource("exceptions.negativeWeightCycle"));
            }
        }
    }

    /**
     * Converts the cost table into the required output format for the shortest paths.
     *
     * @param costTable The cost table containing the computed shortest path details.
     * @return A map of vertices to their shortest path details.
     */
    private static Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> convertCostTableToOutputFormat(
            Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable) {

        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPaths = new HashMap<>();

        for (Map.Entry<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> entry : costTable.entrySet()) {
            Vertex<Stop> vertex = entry.getKey();
            Quartet<Vertex<Stop>, String, Double, TransportMetrics> details = entry.getValue();
            shortestPaths.put(vertex, new Triplet<>(details.getValue0(), details.getValue1(), details.getValue3()));
        }

        return shortestPaths;
    }

    /**
     * Relaxes an edge by updating the cost table if a cheaper path is found.
     *
     * @param u                   The source vertex.
     * @param v                   The destination vertex.
     * @param edge                The edge connecting the source and destination.
     * @param costTable           The current cost table.
     * @param usableTransports    A set of usable transport modes.
     * @param preferredCostType   The preferred cost type.
     */
    private static void relaxEdge(
            Vertex<Stop> u, Vertex<Stop> v, Edge<Route, Stop> edge,
            Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> costTable,
            Set<String> usableTransports, CostType preferredCostType) {

        Triplet<String, Double, TransportMetrics> preferredCost = edge.element()
                .getCheapestTransportWithCost(usableTransports, preferredCostType);

        if (preferredCost == null || preferredCost.getValue1() == Double.POSITIVE_INFINITY) {
            return;
        }

        double weight = preferredCost.getValue1();
        double currentCost = costTable.get(u).getValue2();

        if (currentCost + weight < costTable.get(v).getValue2()) {
            costTable.put(v, new Quartet<>(u, preferredCost.getValue0(), currentCost + weight, preferredCost.getValue2()));
        }
    }

    /**
     * Reconstructs the path from the destination to the origin vertex using the cost table.
     *
     * @param transportMap          The transport map containing the vertices and edges.
     * @param originStopVertex      The origin vertex.
     * @param destinationStopVertex The destination vertex.
     * @param verticesTable         The table of vertices with path details.
     * @return A list representing the reconstructed path from origin to destination.
     * @throws PathNotFoundException If the path cannot be reconstructed.
     */
    public static List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructPath(
            TransportMap transportMap, Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex,
            Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> verticesTable) throws PathNotFoundException {

        List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructedPath = new ArrayList<>();
        Vertex<Stop> currentVertex = destinationStopVertex;

        while (currentVertex != null) {
            Triplet<Vertex<Stop>, String, TransportMetrics> details = verticesTable.get(currentVertex);

            if (details == null) {
                throw new PathNotFoundException("Path to destination stop is not reachable.");
            }

            Vertex<Stop> previousVertex = details.getValue0();
            String transportName = details.getValue1();
            TransportMetrics transportMetrics = details.getValue2();

            if (currentVertex != originStopVertex && (transportName == null || transportMetrics == null)) {
                throw new PathNotFoundException("Invalid path due to missing transport information.");
            }

            reconstructedPath.add(new Triplet<>(currentVertex, transportName, transportMetrics));
            currentVertex = previousVertex;
        }

        if (reconstructedPath.isEmpty() || !reconstructedPath.get(reconstructedPath.size() - 1).getValue0().equals(originStopVertex)) {
            throw new PathNotFoundException("Origin stop is not reachable from the destination.");
        }

        Collections.reverse(reconstructedPath);
        return reconstructedPath;
    }
}