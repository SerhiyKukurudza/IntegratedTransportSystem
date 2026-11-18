/******************************************************************************************************************************
 * File Name: TransportMap.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import java.util.*;

import org.javatuples.Pair;
import org.javatuples.Triplet;

import com.brunomnsilva.smartgraph.graph.*;

import pt.pa.exceptions.InvalidRouteException;
import pt.pa.exceptions.InvalidStopException;
import pt.pa.exceptions.InvalidTransportModeException;
import pt.pa.patterns.observer.Subject;
import pt.pa.patterns.strategy.PathfindingArguments;
import pt.pa.patterns.strategy.PathfindingStrategy;
import pt.pa.utils.TransportColor;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a transportation network with stops and routes linking them.
 */
public class TransportMap extends Subject {
    /**
     * Represents the transportation network in a graph data structure.
     * Holds stops and routes in the transportation network.
     */
    private final Graph<Stop, Route> transportGraph;

    /**
     * Represents all the transportation mode names available in the transportation
     * network routes.
     */
    private final Map<String, TransportColor> transportModes;

    /**
     * Represents the current strategy used in the transport map to find a path
     * between two stop vertices.
     */
    private PathfindingStrategy pathfindingStrategy;

    /**
     * The number of top centralized stops to be retrieved.
     * This constant defines the maximum number of stops that will be considered
     * as the most centralized, based on the highest number of connected routes.
     */
    public static final int NUMBER_OF_TOP_CENTRALIZED_STOPS = 5;

    /**
     * Constructs a TransportMap instance.
     * Initializes the graph responsible for the whole transportation network.
     */
    public TransportMap() {
        this.transportGraph = new GraphEdgeList<>();
        this.transportModes = new HashMap<>();
    }

    /**
     * Adds a list of stops into the transportation network.
     *
     * @param stops A list of stops to add.
     * @return The list of stop vertices added.
     * @throws InvalidVertexException If the stop is duplicate inside the
     *                                transportation network.
     */
    public List<Vertex<Stop>> addStops(List<Stop> stops) throws InvalidVertexException {
        List<Vertex<Stop>> vertices = new ArrayList<>();

        for (Stop stop : stops) {
            vertices.add(addStop(stop));
        }

        return vertices;
    }

    /**
     * Adds a list of routes into the transportation network.
     *
     * @param routes A list of routes to add.
     * @return The list of route edges added.
     */
    public List<Edge<Route, Stop>> addRoutes(List<Triplet<Stop, Stop, Route>> routes) {
        List<Edge<Route, Stop>> edges = new ArrayList<>();

        for (Triplet<Stop, Stop, Route> route : routes) {
            Stop origin = route.getValue0();
            Stop destination = route.getValue1();

            edges.add(addRoute(origin, destination, route.getValue2()));
        }

        return edges;
    }

    /**
     * Adds a stop into the transportation network.
     * Also notifies all transport map observers with the update.
     *
     * @param stop A stop instance.
     * @return The added stop on the transportation map graph.
     */
    public Vertex<Stop> addStop(Stop stop) {
        Vertex<Stop> stopVertex = transportGraph.insertVertex(stop);
        notifyObservers(this);

        return stopVertex;
    }

    /**
     * Adds a route between two stops into the transportation network.
     * The stops don't need to follow a specific order.
     * Updates the unique transport modes present in the transport map.
     * And notifies all transport map observers with the update.
     *
     * @param origin      The destination stop.
     * @param destination The origin stop.
     * @param route       The route to add.
     * @return The added edge on the transportation map graph.
     * @throws InvalidVertexException If either stop is not part of the transport
     *                                graph.
     * @throws InvalidEdgeException   If the provided route already exists in the
     *                                transport graph.
     */
    public Edge<Route, Stop> addRoute(Stop origin, Stop destination, Route route) {
        Edge<Route, Stop> newRouteEdge = transportGraph.insertEdge(origin, destination, route);
        updateTransportModes(route);
        notifyObservers(this);

        return newRouteEdge;
    }

    /**
     * Adds a route between two stop vertices in the transportation network.
     * The stop vertices don't need to follow a specific order.
     * Updates the unique transport modes present in the transport map.
     * And notifies all transport map observers with the update.
     *
     * @param originVertex      The destination stop vertex.
     * @param destinationVertex The origin stop vertex.
     * @param route             The route to add.
     * @return The added edge on the transportation map graph.
     * @throws InvalidVertexException If either stop vertex is not part of the
     *                                transport graph.
     * @throws InvalidEdgeException   If the provided route already exists in the
     *                                transport graph.
     */
    public Edge<Route, Stop> addRoute(Vertex<Stop> originVertex, Vertex<Stop> destinationVertex, Route route) {
        Edge<Route, Stop> newRouteEdge = transportGraph.insertEdge(originVertex, destinationVertex, route);
        updateTransportModes(route);
        notifyObservers(this);

        return newRouteEdge;
    }

    /**
     * Gets a route vertex in the transport map graph with a route object.
     *
     * @param route The route that is being searched.
     * @return The found Route or {@code null}.
     * @throws InvalidRouteException If the provided route is null.
     */
    public Edge<Route, Stop> getRouteEdge(Route route) {
        if (route == null) {
            throw new InvalidRouteException(getStringResource("exceptions.routeNull"));
        }

        for (Edge<Route, Stop> routeEdge : getRoutes()) {
            if (routeEdge.element().equals(route)) {
                return routeEdge;
            }
        }

        return null;
    }

    /**
     * Checks whether two stop vertices are adjacent in the transport graph.
     * Stops are considered adjacent if there is a direct route between them.
     * Also ensures that the two stop vertices are not the same.
     *
     * @param stopVertex1 The first stop vertex to check.
     * @param stopVertex2 The second stop vertex to check.
     * @return True if the stops are adjacent, false otherwise.
     */
    public boolean stopsAreAdjacent(Vertex<Stop> stopVertex1, Vertex<Stop> stopVertex2) {
        if (stopVertex1.equals(stopVertex2)) return false;

        return transportGraph.areAdjacent(stopVertex1, stopVertex2);
    }

    /**
     * Checks whether there are any enabled transport modes on the route
     * between two given stops.
     *
     * @param stopVertex1 the first stop represented as a vertex.
     * @param stopVertex2 the second stop represented as a vertex.
     * @return {@code true} if there is at least one enabled transport mode
     * on the route between the two stops, {@code false} otherwise.
     * @throws InvalidRouteException if no route exists between the specified stops.
     */
    public boolean routeHasEnabledTransports(Vertex<Stop> stopVertex1, Vertex<Stop> stopVertex2) {
        Optional<Edge<Route, Stop>> routeOptional = getRouteBetweenStops(stopVertex1, stopVertex2);

        if (routeOptional.isEmpty()) {
            throw new InvalidRouteException(getStringResource("exceptions.routeDoesNotExist"));
        }

        return routeOptional.get().element().hasEnabledTransports();
    }

    /**
     * Sets the enabled status of a transport mode in a specific route.
     *
     * @param route         The route where the transport mode will be updated. Must not be null.
     * @param transportName The name of the transport mode to update. Must not be null or empty.
     * @param enabled       {@code true} to enable the transport mode, {@code false} to disable it.
     * @throws IllegalArgumentException If the route or transportName is null or empty.
     * @throws IllegalStateException    If the transport mode does not exist in the route.
     */
    public void setRouteTransportModeEnabled(Edge<Route, Stop> route, String transportName, boolean enabled) {
        if (route == null || transportName == null || transportName.isBlank()) {
            throw new IllegalArgumentException(getStringResource("exceptions.routeAndTransportNameNull"));
        }

        Route routeElement = route.element();
        if (!routeElement.hasTransportMode(transportName)) {
            throw new IllegalStateException("Transport mode '" + transportName + "' does not exist in the route.");
        }

        routeElement.setTransportModeEnabled(transportName, enabled);

        notifyObservers(this);
    }

    /**
     * Retrieves the route (edge) between two stop vertices if one exists.
     * The method searches for a route connecting the two stops, and if found,
     * returns the edge representing the route between them.
     *
     * @param stopVertex1 The first stop vertex. Must not be null.
     * @param stopVertex2 The second stop vertex. Must not be null.
     * @return An {@code Optional<Edge<Route, Stop>>} representing the route
     * between the two stops, or an empty {@code Optional} if no route exists.
     * @throws IllegalArgumentException If either of the stop vertices is null.
     */
    public Optional<Edge<Route, Stop>> getRouteBetweenStops(Vertex<Stop> stopVertex1, Vertex<Stop> stopVertex2) {
        if (stopVertex1 == null || stopVertex2 == null) {
            throw new IllegalArgumentException(getStringResource("exceptions.stopVerticesNull"));
        }

        if (stopVertex1.equals(stopVertex2)) {
            return Optional.empty();
        }

        for (Edge<Route, Stop> routeEdge : incidentRoutes(stopVertex1)) {
            Vertex<Stop> oppositeStopVertex = oppositeStop(stopVertex1, routeEdge);

            if (stopVertex2.equals(oppositeStopVertex)) {
                return Optional.of(routeEdge);
            }
        }

        return Optional.empty();
    }

    /**
     * Updates a transport metric for a specific transport mode on a route within a transport map.
     * This method updates the metric value (cost) for a specified transport mode on a given route.
     *
     * @param route         The route where the transport metric should be updated. This must not be {@code null}.
     * @param transportName The name of the transport mode whose metric should be updated. This must not be {@code null} or blank.
     * @param costType      The type of metric (cost) that should be updated. This must not be {@code null}.
     * @param newCost       The new cost value to be set. This value must be non-negative.
     * @throws IllegalArgumentException      If any of the following conditions are met:
     *                                       <ul>
     *                                         <li>The provided {@code route} is {@code null}.</li>
     *                                         <li>The provided {@code transportName} is {@code null} or blank.</li>
     *                                         <li>The provided {@code costType} is {@code null}.</li>
     *                                       </ul>
     * @throws InvalidEdgeException          If no edge corresponding to the provided {@code route} exists in the transport map.
     * @throws InvalidTransportModeException If the transport mode with the given {@code transportName} does not exist in the specified {@code route}.
     */
    public void updateRouteMetric(Route route, String transportName, CostType costType, double newCost) throws InvalidTransportModeException {
        if (route == null) {
            throw new InvalidRouteException(getStringResource("exceptions.routeNull"));
        }

        if (transportName == null || transportName.isBlank()) {
            throw new IllegalArgumentException("Transport name cannot be blank");
        }

        if (costType == null) {
            throw new IllegalArgumentException("Cost type cannot be null");
        }

        Edge<Route, Stop> routeEdge = getRouteEdge(route);

        if (routeEdge == null) {
            throw new InvalidEdgeException("An edge with that route does not exist in the transport map graph");
        }

        Route edgeRoute = routeEdge.element();
        TransportMetrics specifiedMetrics = edgeRoute.getTransportMetricsByTransportName(transportName);

        if (specifiedMetrics == null) {
            throw new InvalidTransportModeException("The specified transport mode does not exist in the route");
        }

        specifiedMetrics.setCostByType(costType, newCost);

        notifyObservers(this);
    }

    /**
     * Sets the path finding strategy to be used in the transport map.
     *
     * @param pathfindingStrategy The concrete path finding strategy to use.
     */
    public void setPathfindingStrategy(PathfindingStrategy pathfindingStrategy) {
        this.pathfindingStrategy = pathfindingStrategy;
    }

    /**
     * Calculates the degree (number of connections) of a given stop in the
     * transportation network.
     *
     * @param stop The stop (vertex) for which the degree is to be calculated.
     * @return The degree of the stop.
     * @throws InvalidVertexException If the stop does not exist in the graph.
     */
    public int stopDegree(Vertex<Stop> stop) {
        return transportGraph.incidentEdges(stop).size();
    }

    /**
     * Returns the current number of stops in the transportation network.
     *
     * @return The number of stops.
     */
    public int numStops() {
        return transportGraph.numVertices();
    }

    /**
     * Returns the current number of isolated stops in the transportation network.
     *
     * @return The number of isolated stops.
     */
    public int numIsolatedStops() {
        int isolatedCount = 0;

        for (Vertex<Stop> vertex : transportGraph.vertices()) {
            if (stopDegree(vertex) == 0) {
                isolatedCount++;
            }
        }

        return isolatedCount;
    }

    /**
     * Returns the current number of routes in the transportation network.
     *
     * @return The number of routes.
     */
    public int numRoutes() {
        return transportGraph.numEdges();
    }

    /**
     * Returns a Map where the keys are transport mode names and the values are the number of routes
     * where each transport mode is available.
     *
     * @return A Map containing transport mode names as keys and the count of routes they appear in as values.
     */
    public Map<String, Integer> numRoutesByTransport() {
        Map<String, Integer> routesByTransport = new HashMap<>();

        Collection<Edge<Route, Stop>> routes = getRoutes();

        for (Edge<Route, Stop> edge : routes) {
            Route route = edge.element();

            for (String transportName : route.getTransportNames()) {
                routesByTransport.put(transportName, routesByTransport.getOrDefault(transportName, 0) + 1);
            }
        }

        return routesByTransport;
    }

    /**
     * Retrieves all routes incident to a specific stop (vertex) in the transport
     * map.
     *
     * @param stopVertex The vertex representing a stop in the graph.
     * @return A collection of edges representing the routes connected to the given
     * stop.
     */
    public Collection<Edge<Route, Stop>> incidentRoutes(Vertex<Stop> stopVertex) {
        return transportGraph.incidentEdges(stopVertex);
    }

    /**
     * Checks if a stop vertex has any incident routes at all.
     *
     * @param stopVertex The stop vertex whose incident routes are being tested.
     * @return {@code True} if there is at least one incident route, {@code false} otherwise.
     */
    public boolean hasIncidentRoutes(Vertex<Stop> stopVertex) {
        return !incidentRoutes(stopVertex).isEmpty();
    }

    /**
     * Retrieves the number of adjacent stops connected to the given stop vertex
     *
     * @param stopVertex the vertex representing the stop for which the number of adjacent stops is to be calculated.
     * @return the number of adjacent stops connected to the given stop vertex.
     */
    public int getNumAdjacentStops(Vertex<Stop> stopVertex) {
        return incidentRoutes(stopVertex).size();
    }

    /**
     * Retrieves the opposite stop vertex in a route edge.
     *
     * @param stopVertex The stop vertex to look from.
     * @param routeEdge  The route edge to look from.
     * @return The opposite stop vertex.
     * @throws InvalidVertexException If the provided stop vertex is not part of the
     *                                transport graph.
     * @throws InvalidEdgeException   If the provided route edge is not part of the
     *                                transport graph.
     */
    public Vertex<Stop> oppositeStop(Vertex<Stop> stopVertex, Edge<Route, Stop> routeEdge) {
        return transportGraph.opposite(stopVertex, routeEdge);
    }

    /**
     * Calculates the centralization of each stop in the transport map.
     * The centralization is defined as the number of routes (edges) connected to a
     * stop (vertex).
     * <p>
     * The method returns a list of pairs where each pair consists of a stop and its
     * centralization value, sorted in descending order of centralization.
     *
     * @return A list of pairs where each pair consists of a stop and its
     * centralization value, sorted in descending order of centralization.
     */
    public List<Pair<Stop, Integer>> stopCentralization() {
        List<Pair<Stop, Integer>> centralizationList = new ArrayList<>();

        for (Vertex<Stop> vertex : getStops()) {
            centralizationList.add(new Pair<>(vertex.element(), incidentRoutes(vertex).size()));
        }

        centralizationList.sort((pair1, pair2) -> Integer.compare(pair2.getValue1(), pair1.getValue1()));

        return centralizationList;
    }

    /**
     * Retrieves the top 5 most centralized stops from the transport map.
     * Centralized stops are those connected to the highest number of routes.
     * <p>
     * If the provided centralization list is null, the method throws an
     * {@link IllegalArgumentException}. The method returns a list of the top 5
     * pairs (or fewer if the list contains less than 5 stops) where each pair consists
     * of a stop and its centralization value (connected routes).
     *
     * @param centralizationList A precomputed list of pairs where each pair
     *                           consists of a stop and its centralization value,
     *                           sorted in descending order of centralization. If
     *                           null, the method calculates the list.
     * @return A list of the top 5 pairs where each pair consists of a stop and its
     * centralization value (connected routes).
     * @throws IllegalArgumentException If the provided centralization list is null.
     */
    public List<Pair<Stop, Integer>> topCentralizedStops(List<Pair<Stop, Integer>> centralizationList) {
        if (centralizationList == null) {
            throw new IllegalArgumentException("Centralization list cannot be null");
        }

        return centralizationList.subList(0, Math.min(NUMBER_OF_TOP_CENTRALIZED_STOPS, centralizationList.size()));
    }

    /**
     * Retrieves the top 5 most centralized stops from the transport map.
     * If no list is provided, it calculates the centralization list.
     * Centralized stops are those connected to the highest number of routes.
     *
     * @return A list of the top 5 pairs where each pair consists of a stop and its
     * centralization value (connected routes).
     */
    public List<Pair<Stop, Integer>> topCentralizedStops() {
        List<Pair<Stop, Integer>> centralizationList = stopCentralization();
        return topCentralizedStops(centralizationList);
    }

    /**
     * Returns a collection of all stops in the transportation network.
     *
     * @return The collection containing all stops.
     */
    public Collection<Vertex<Stop>> getStops() {
        return transportGraph.vertices();
    }

    /**
     * Returns a collection of all routes in the transportation network.
     *
     * @return The collection containing all routes.
     */
    public Collection<Edge<Route, Stop>> getRoutes() {
        return transportGraph.edges();
    }

    /**
     * Returns the graph of the transportation network.
     *
     * @return The graph.
     */
    public Graph<Stop, Route> getTransportGraph() {
        return transportGraph;
    }

    /**
     * Returns true if the stop vertex exists in the transport map.
     *
     * @param stopVertex The stop vertex to check if exists in the map.
     * @return True if the stop does exist in the transport map.
     */
    public boolean stopExists(Vertex<Stop> stopVertex) {
        return getStops().contains(stopVertex);
    }

    /**
     * Returns true if the route edge exists in the transport map.
     *
     * @param routeEdge The route edge to check if exists in the map.
     * @return True if the route edge does exist in the transport map.
     */
    public boolean routeExists(Edge<Route, Stop> routeEdge) {
        return getRoutes().contains(routeEdge);
    }

    /**
     * Gets a collection with all the stop vertices in the transport map sorted by
     * their name in descending order.
     *
     * @return A collection with the sorted stop vertices.
     */
    public Collection<Vertex<Stop>> getStopsOrderedByName() {
        List<Vertex<Stop>> stopList = new ArrayList<>(this.getStops());

        stopList.sort((v1, v2) -> v2.element().compareTo(v1.element()));

        return stopList;
    }

    /**
     * Gets a Set of transport mode names in the transport map.
     *
     * @return The Set of unique transport mode names.
     */
    public Set<String> getTransportModeNames() {
        return new HashSet<>(transportModes.keySet());
    }

    /**
     * Gets a map of the transport names and their respective colors.
     *
     * @return A map with each entry containing the transport name and its color.
     */
    public Map<String, TransportColor> getTransportNamesAndColors() {
        return new HashMap<>(transportModes);
    }

    /**
     * Gets the color of a specific transport mode.
     *
     * @param transportModeName The transport mode name.
     * @return The color mapped to the provided transport name.
     */
    public TransportColor getTransportModeColor(String transportModeName) {
        return transportModes.get(transportModeName);
    }

    /**
     * Adds new transport modes into the transport map.
     *
     * @param transportModes A Map with the transport mode name and its color.
     */
    public void addTransportModes(Map<String, TransportColor> transportModes) {
        this.transportModes.putAll(transportModes);
    }

    /**
     * Updates the transport modes present in the transport map with the available
     * transports in the new route.
     * If the Map of transports does not yet have the specified transport, it will
     * be added with a random color.
     *
     * @param route A route added into the transport map.
     */
    private void updateTransportModes(Route route) {
        for (String transportName : route.getTransportNames()) {
            if (!transportModes.containsKey(transportName)) {
                transportModes.put(transportName, new TransportColor());
            }
        }
    }

    /**
     * Finds the optimal path between two stop vertices, considering the specific
     * preferred cost type and the chosen transports. This method uses a strategy
     * for pathfinding to calculate the best route.
     *
     * @param origin           The origin stop vertex from which the path calculation begins.
     * @param destination      The destination stop vertex to which the path is calculated.
     * @param chosenTransports A set of transport names that are considered for the path.
     * @param preferredCost    The preferred cost type to minimize or optimize for.
     * @return A list of triplets, each containing a stop vertex, the transport mode used,
     * and the transport metrics cost for that leg of the journey. The list represents
     * the complete path from origin to destination, including all stops and routes.
     * @throws IllegalStateException    If no pathfinding strategy has been set for the transport map.
     * @throws InvalidStopException     If any of the stop vertices (origin or destination) are invalid.
     * @throws IllegalArgumentException If any other arguments are invalid,
     *                                  such as empty chosen transports, or null cost type.
     */
    public List<Triplet<Vertex<Stop>, String, TransportMetrics>> findOptimalPath(
            Vertex<Stop> origin,
            Vertex<Stop> destination,
            Set<String> chosenTransports,
            CostType preferredCost) {

        if (this.pathfindingStrategy == null) {
            throw new IllegalStateException(getStringResource("exceptions.pathFindingNull"));
        }

        PathfindingArguments arguments = new PathfindingArguments(this, origin, destination, chosenTransports, preferredCost);

        return pathfindingStrategy.findOptimalPath(arguments);
    }

    /**
     * Finds a set of stop vertices that are at a specific distance (in terms of stops)
     * from the origin vertex.
     * <p>
     * Uses a Breadth-First Search (BFS) algorithm to explore the graph, keeping track of the
     * current distance (breadth level). If the distance of 0 is provided, the origin vertex is returned.
     * </p>
     *
     * @param origin                The stop vertex to start counting from.
     * @param numberOfStopsDistance The number of stops the set must be distant from the origin.
     * @return A set of stop vertices found at the specified distance from the origin.
     * @throws IllegalArgumentException If the number of stops distance is negative.
     * @throws InvalidVertexException   If the origin vertex is not part of the transport map graph.
     */
    public Set<Vertex<Stop>> findStopsByDistance(Vertex<Stop> origin, int numberOfStopsDistance) {
        findStopsByDistanceCheckParameters(origin, numberOfStopsDistance);

        if (numberOfStopsDistance == 0) {
            return createOriginSet(origin);
        }

        Queue<Vertex<Stop>> queue = new LinkedList<>();
        Set<Vertex<Stop>> visited = new HashSet<>();
        Set<Vertex<Stop>> foundStops = new HashSet<>();

        queue.add(origin);
        visited.add(origin);

        int distance = 0;

        while (!queue.isEmpty()) {
            processStopsAtCurrentDistance(queue, visited, foundStops, distance, numberOfStopsDistance);

            if (distance == numberOfStopsDistance) {
                break;
            }

            distance++;
        }

        return foundStops;
    }

    /**
     * Creates a set containing only the origin stop.
     *
     * @param origin The origin vertex to add to the set.
     * @return A set containing only the origin stop.
     */
    private Set<Vertex<Stop>> createOriginSet(Vertex<Stop> origin) {
        Set<Vertex<Stop>> originSet = new HashSet<>();
        originSet.add(origin);
        return originSet;
    }

    /**
     * Processes the stops at the current distance level and adds them to the found stops set.
     *
     * @param queue                 The queue to process.
     * @param visited               The set of visited stops.
     * @param foundStops            The set to store found stops at the target distance.
     * @param distance              The current distance level.
     * @param numberOfStopsDistance The target number of stops distance.
     */
    private void processStopsAtCurrentDistance(Queue<Vertex<Stop>> queue, Set<Vertex<Stop>> visited,
                                               Set<Vertex<Stop>> foundStops, int distance, int numberOfStopsDistance) {
        int numberOfStopsAtCurrentDistance = queue.size();

        for (int i = 0; i < numberOfStopsAtCurrentDistance; i++) {
            Vertex<Stop> currentVertex = queue.poll();

            if (distance == numberOfStopsDistance) {
                foundStops.add(currentVertex);
                continue;
            }

            for (Edge<Route, Stop> edge : transportGraph.incidentEdges(currentVertex)) {
                Vertex<Stop> neighbour = oppositeStop(currentVertex, edge);

                if (!visited.contains(neighbour)) {
                    visited.add(neighbour);
                    queue.offer(neighbour);
                }
            }
        }
    }

    /**
     * Validates the parameters for finding stops by distance.
     *
     * @param origin                The origin vertex (starting stop) to check against the transport map.
     * @param numberOfStopsDistance The number of stops to check for, based on the origin stop.
     * @throws InvalidVertexException   If the origin vertex is null or not part of the transport map.
     * @throws IllegalArgumentException If the number of stops is negative.
     */
    private void findStopsByDistanceCheckParameters(Vertex<Stop> origin, int numberOfStopsDistance) {
        if (origin == null || !getStops().contains(origin)) {
            throw new InvalidVertexException(getStringResource("exceptions.originVertexDoesNotExist"));
        }

        if (numberOfStopsDistance < 0) {
            throw new IllegalArgumentException(getStringResource("exceptions.stopsCannotBeNegative"));
        }
    }

    /**
     * Retrieves a set of unique transport modes present in the routes of the transport map.
     * This method iterates through all the routes and collects the transport modes associated
     * with each route, ensuring no duplicates by using a set.
     * <p>
     * The method uses a {@link HashSet} to accumulate the transport modes. If a route has no transport modes,
     * it is skipped.
     * </p>
     *
     * @return A set of unique transport mode names, or an empty set if no transport modes are found.
     */
    public Set<String> getUniqueTransports() {
        Set<String> uniqueTransports = new HashSet<>();

        for (Edge<Route, Stop> route : getRoutes()) {
            uniqueTransports.addAll(route.element().getTransportNames());
        }

        return uniqueTransports;
    }

    /**
     * Checks if a transport mode is used in the transport map.
     *
     * @param transportMode The transport mode name.
     * @return True if the transport mode is used in at least one route.
     */
    public boolean transportModeIsUsed(String transportMode) {
        return getUniqueTransports().contains(transportMode);
    }
}
