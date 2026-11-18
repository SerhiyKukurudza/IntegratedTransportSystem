/******************************************************************************************************************************
 * File Name: Dijkstra.java
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
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.*;

import java.util.*;

/**
 * Implements Dijkstra's algorithm for finding the shortest path in a transport map.
 */
public class Dijkstra {

    private static final double WEIGHT_BONUS = 2;
    private final double weightAdjustment;

    /**
     * Default constructor that creates a Dijkstra instance without weight adjustment.
     */
    public Dijkstra() {
        this.weightAdjustment = 0;
    }

    /**
     * Constructs a Dijkstra instance with weight adjustment based on the smallest route cost.
     *
     * @param edgeRoutes The collection of routes to consider for weight adjustment.
     */
    public Dijkstra(Collection<Edge<Route, Stop>> edgeRoutes) {
        if (edgeRoutes == null || edgeRoutes.isEmpty()) {
            throw new IllegalArgumentException("The edge routes cannot be null or empty.");
        }
        this.weightAdjustment = calculateWeightAdjustment(edgeRoutes);
    }

    /**
     * Calculates the weight adjustment based on the smallest route cost and a predefined bonus.
     *
     * @param edgeRoutes The routes to analyze for the smallest cost.
     * @return The adjusted weight based on the smallest cost.
     */
    private double calculateWeightAdjustment(Collection<Edge<Route, Stop>> edgeRoutes) {
        double smallestCost = findSmallestRouteCost(edgeRoutes);
        return Math.abs(smallestCost) * WEIGHT_BONUS;
    }

    /**
     * Finds the smallest cost among all given routes.
     *
     * @param edgeRoutes The routes to analyze.
     * @return The smallest cost of all routes.
     */
    private double findSmallestRouteCost(Collection<Edge<Route, Stop>> edgeRoutes) {
        double smallestCost = Double.MAX_VALUE;

        for (Edge<Route, Stop> edgeRoute : edgeRoutes) {
            double routeCost = edgeRoute.element().getCheapestTransportCost();
            if (routeCost < smallestCost) {
                smallestCost = routeCost;
            }
        }
        return smallestCost;
    }

    /**
     * Executes Dijkstra's algorithm to compute the shortest paths from the origin vertex.
     *
     * @param transportMap      The transport map representing the graph.
     * @param originStopVertex  The starting vertex for the search.
     * @param usableTransports  A set of transport modes to consider.
     * @param preferredCostType The cost type to optimize for.
     * @return A map of vertices to their shortest path details, including the predecessor, transport, and metrics.
     */
    public Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> dijkstra(
            TransportMap transportMap,
            Vertex<Stop> originStopVertex,
            Set<String> usableTransports,
            CostType preferredCostType) {

        validateInputParameters(transportMap, originStopVertex);

        Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> dijkstraTable = initializeDijkstraTable(transportMap, originStopVertex);

        PriorityQueue<Vertex<Stop>> priorityQueue = initializePriorityQueue(dijkstraTable, originStopVertex);

        computeShortestPaths(transportMap, usableTransports, preferredCostType, dijkstraTable, priorityQueue);

        return convertToShortestPathMap(dijkstraTable);
    }

    /**
     * Validates the input parameters for Dijkstra's algorithm.
     *
     * @param transportMap     The transport map representing the graph.
     * @param originStopVertex The origin vertex to validate.
     */
    private void validateInputParameters(TransportMap transportMap, Vertex<Stop> originStopVertex) {
        if (transportMap == null || originStopVertex == null) {
            throw new IllegalArgumentException("Transport map and origin vertex cannot be null.");
        }
        if (!transportMap.getStops().contains(originStopVertex)) {
            throw new InvalidVertexException("Origin vertex is not part of the transport map.");
        }
    }

    /**
     * Initializes the Dijkstra table with default values.
     *
     * @param transportMap     The transport map containing the vertices.
     * @param originStopVertex The starting vertex with an initial cost of zero.
     * @return The initialized Dijkstra table.
     */
    private Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> initializeDijkstraTable(
            TransportMap transportMap, Vertex<Stop> originStopVertex) {

        Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> dijkstraTable = new HashMap<>();

        for (Vertex<Stop> vertex : transportMap.getStops()) {
            dijkstraTable.put(vertex, new Quartet<>(null, null, Double.POSITIVE_INFINITY, null));
        }

        dijkstraTable.put(originStopVertex, new Quartet<>(null, null, 0.0, null));

        return dijkstraTable;
    }

    /**
     * Initializes the priority queue with the origin vertex.
     *
     * @param dijkstraTable    The Dijkstra table with vertex costs.
     * @param originStopVertex The starting vertex to add to the queue.
     * @return The initialized priority queue.
     */
    private PriorityQueue<Vertex<Stop>> initializePriorityQueue(
            Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> dijkstraTable,
            Vertex<Stop> originStopVertex) {

        PriorityQueue<Vertex<Stop>> priorityQueue = new PriorityQueue<>(Comparator.comparingDouble(vertex -> dijkstraTable.get(vertex).getValue2()));
        priorityQueue.add(originStopVertex);

        return priorityQueue;
    }

    /**
     * Computes the shortest paths using Dijkstra's algorithm.
     *
     * @param transportMap      The transport map containing the graph.
     * @param usableTransports  A set of transport modes to consider.
     * @param preferredCostType The cost type to optimize for.
     * @param dijkstraTable     The table containing vertex costs and paths.
     * @param priorityQueue     The priority queue for selecting vertices.
     */
    private void computeShortestPaths(
            TransportMap transportMap,
            Set<String> usableTransports,
            CostType preferredCostType,
            Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> dijkstraTable,
            PriorityQueue<Vertex<Stop>> priorityQueue) {

        while (!priorityQueue.isEmpty()) {
            Vertex<Stop> currentVertex = priorityQueue.poll();

            for (Edge<Route, Stop> edge : transportMap.incidentRoutes(currentVertex)) {
                Vertex<Stop> neighbor = transportMap.oppositeStop(currentVertex, edge);

                Triplet<String, Double, TransportMetrics> preferredCost = edge.element()
                        .getCheapestTransportWithCost(usableTransports, preferredCostType);

                double newCost = dijkstraTable.get(currentVertex).getValue2() + preferredCost.getValue1() + weightAdjustment;

                if (newCost < dijkstraTable.get(neighbor).getValue2()) {
                    dijkstraTable.put(neighbor, new Quartet<>(currentVertex, preferredCost.getValue0(), newCost - weightAdjustment, preferredCost.getValue2()));
                    priorityQueue.add(neighbor);
                }
            }
        }
    }

    /**
     * Converts the Dijkstra table to a map with the shortest paths.
     *
     * @param dijkstraTable The Dijkstra table to convert.
     * @return A map containing the shortest paths for each vertex.
     */
    private Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> convertToShortestPathMap(
            Map<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> dijkstraTable) {

        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPaths = new HashMap<>();

        for (Map.Entry<Vertex<Stop>, Quartet<Vertex<Stop>, String, Double, TransportMetrics>> entry : dijkstraTable.entrySet()) {
            Vertex<Stop> vertex = entry.getKey();
            Quartet<Vertex<Stop>, String, Double, TransportMetrics> details = entry.getValue();

            shortestPaths.put(vertex, new Triplet<>(details.getValue0(), details.getValue1(), details.getValue3()));
        }

        return shortestPaths;
    }

    /**
     * Reconstructs the shortest path from the destination to the origin.
     *
     * @param originStopVertex      The origin vertex of the path.
     * @param destinationStopVertex The destination vertex of the path.
     * @param verticesTable         A map containing path details for each vertex.
     * @return A list representing the reconstructed path from origin to destination.
     * @throws PathNotFoundException If the destination is unreachable or invalid.
     */
    public List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructPath(
            Vertex<Stop> originStopVertex,
            Vertex<Stop> destinationStopVertex,
            Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> verticesTable) throws PathNotFoundException {

        List<Triplet<Vertex<Stop>, String, TransportMetrics>> path = new ArrayList<>();
        Vertex<Stop> currentVertex = destinationStopVertex;

        while (currentVertex != null && verticesTable.containsKey(currentVertex)) {
            Triplet<Vertex<Stop>, String, TransportMetrics> details = verticesTable.get(currentVertex);
            path.add(new Triplet<>(currentVertex, details.getValue1(), details.getValue2()));
            currentVertex = details.getValue0();
        }

        if (path.isEmpty() || !path.get(path.size() - 1).getValue0().equals(originStopVertex)) {
            throw new PathNotFoundException("Destination is unreachable from the origin.");
        }

        Collections.reverse(path);
        return path;
    }
}