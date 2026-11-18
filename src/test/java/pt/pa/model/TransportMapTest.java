/******************************************************************************************************************************
 * File Name: TransportMapTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import com.brunomnsilva.smartgraph.graph.*;
import com.opencsv.exceptions.CsvValidationException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.exceptions.InvalidRouteException;
import pt.pa.utils.DataSetImporter;
import pt.pa.utils.TransportColor;

import java.awt.*;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TransportMapTest {

    final String BOAT = "boat";
    final String BUS = "bus";
    final String WALK = "walk";

    TransportMap transportMap;
    List<Stop> stops;
    List<TransportMetrics> transportMetrics;
    List<String> transportNames;
    List<TransportModesMap> transportModesMaps;
    List<Route> routes;
    List<Triplet<Stop, Stop, Route>> tripletRoutes;
    List<Transport> transports;

    @BeforeEach
    void setUp() {
        transportMap = new TransportMap();

        Point position = new Point(0, 0);

        stops = new ArrayList<>();
        stops.add(new Stop("CODE0", "Name0", 0, 0, position));
        stops.add(new Stop("CODE1", "Name1", 0, 0, position));
        stops.add(new Stop("CODE2", "Name2", 0, 0, position));

        transportMetrics = new ArrayList<>();
        transportMetrics.add(new TransportMetrics(2.2, 3.3, 4.4));
        transportMetrics.add(new TransportMetrics(3.2, 1.3, 5.4));
        transportMetrics.add(new TransportMetrics(4.2, 2.3, 7.4));

        transportNames = new ArrayList<>();
        transportNames.add(BOAT);
        transportNames.add(BUS);
        transportNames.add(WALK);

        transports = new ArrayList<>();
        transports.add(new Transport(transportNames.get(0), transportMetrics.get(0)));
        transports.add(new Transport(transportNames.get(1), transportMetrics.get(1)));
        transports.add(new Transport(transportNames.get(2), transportMetrics.get(2)));

        TransportModesMap firstTransportModesMap = new TransportModesMap();
        firstTransportModesMap.put(transportNames.get(0), transports.get(0));
        firstTransportModesMap.put(transportNames.get(1), transports.get(1));

        TransportModesMap secondTransportModesMap = new TransportModesMap();
        secondTransportModesMap.put(transportNames.get(0), transports.get(0));
        secondTransportModesMap.put(transportNames.get(2), transports.get(2));

        TransportModesMap thirdTransportModesMap = new TransportModesMap();
        thirdTransportModesMap.put(transportNames.get(2), transports.get(2));
        thirdTransportModesMap.put(transportNames.get(1), transports.get(1));

        this.transportModesMaps = new ArrayList<>();
        transportModesMaps.add(firstTransportModesMap);
        transportModesMaps.add(secondTransportModesMap);
        transportModesMaps.add(thirdTransportModesMap);

        routes = new ArrayList<>();
        routes.add(new Route(firstTransportModesMap));
        routes.add(new Route(secondTransportModesMap));
        routes.add(new Route(thirdTransportModesMap));

        tripletRoutes = new ArrayList<>();
        tripletRoutes.add(new Triplet<>(stops.get(0), stops.get(1), routes.get(0)));
        tripletRoutes.add(new Triplet<>(stops.get(0), stops.get(2), routes.get(1)));
        tripletRoutes.add(new Triplet<>(stops.get(1), stops.get(2), routes.get(2)));
    }

    @Test
    void whenNewStopsAdded_numOfStopsChanges() {
        assertEquals(0, transportMap.numStops(), "Number of stops should start at 0");

        transportMap.addStops(stops);
        assertEquals(3, transportMap.numStops(), "Number of stops should be already 3");
    }

    @Test
    void whenRoutesAdded_numOfRoutesChanges() {
        assertEquals(0, transportMap.numRoutes(), "Number of routes should start at 0");

        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertEquals(3, transportMap.numRoutes(), "Number of routes should be already 3");
    }

    @Test
    void whenStopAdded_numOfStopsChanges() {
        assertEquals(0, transportMap.numStops(), "Number of stops should start at 0");

        transportMap.addStop(stops.get(0));
        transportMap.addStop(stops.get(1));

        assertEquals(2, transportMap.numStops(), "Number of stops should be already 2");
    }

    @Test
    void stopAdded_returnsTheCorrectVertex() {
        Stop addStop = stops.get(0);

        Vertex<Stop> returnedStop = transportMap.addStop(addStop);
        assertEquals(addStop, returnedStop.element(), "The added stop should return the same element");

        assertNotEquals(stops.get(1), returnedStop.element(), "The added stop unexpectedly returned the same element");
    }

    @Test
    void addStop_throwsInvalidVertexException_whenAddedDuplicateStop() {
        assertDoesNotThrow(() -> transportMap.addStop(stops.get(0)), "A non duplicated stop should not return an exception");
        assertThrows(InvalidVertexException.class, () -> transportMap.addStop(stops.get(0)), "A duplicated stop should throw an InvalidVertexException");
    }

    @Test
    void whenRouteAdded_numOfRoutesChanges() {
        assertEquals(0, transportMap.numRoutes(), "Number of routes should start at 0");

        transportMap.addStops(stops);

        transportMap.addRoute(stops.get(0), stops.get(1), routes.get(0));
        transportMap.addRoute(stops.get(0), stops.get(2), routes.get(1));

        assertEquals(2, transportMap.numRoutes(), "Number of routes should be already 2");
    }

    @Test
    void routeAdded_returnsTheCorrectEdge() {
        Route addRoute = routes.get(0);

        transportMap.addStops(stops);

        Edge<Route, Stop> returnedRoute = transportMap.addRoute(stops.get(0), stops.get(1), addRoute);
        assertEquals(addRoute, returnedRoute.element(), "The added route should return the same element");

        assertNotEquals(routes.get(1), returnedRoute.element(), "The added route unexpectedly returned the same element when it shouldn't");
    }

    @Test
    void addRoute_throwsInvalidEdgeException_whenAddedDuplicateRoute() {
        transportMap.addStops(stops);

        Triplet<Stop, Stop, Route> tripletRoute = tripletRoutes.get(0);

        Stop origin = tripletRoute.getValue0();
        Stop destination = tripletRoute.getValue1();
        Route route = tripletRoute.getValue2();

        assertDoesNotThrow(() -> transportMap.addRoute(origin, destination, route), "A non duplicated route should not throw an exception");
        assertThrows(InvalidEdgeException.class, () -> transportMap.addRoute(origin, destination, route), "Duplicated route in transport map should throw InvalidEdgeException");
    }

    @Test
    void whenRoutesAdded_throwsInvalidVertexException_whenStopsNotInGraph() {
        Stop outsideStop = new Stop("aCode", "aName", 0.0, 0.0, new Point(0, 2));
        Stop insideStop = stops.get(0);

        assertThrows(InvalidVertexException.class, () -> {
            transportMap.addRoute(outsideStop, insideStop, new Route(transportModesMaps.get(0)));
        }, "Expected an exception of not found origin stop in graph");

        assertThrows(InvalidVertexException.class, () -> {
            transportMap.addRoute(insideStop, outsideStop, new Route(transportModesMaps.get(0)));
        }, "Expected an exception of not found destination stop in graph");
    }

    @Test
    void getRouteEdge_returnsTheCorrectEdge() {
        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        Edge<Route, Stop> foundRoute = transportMap.getRouteEdge(routes.get(0));
        assertEquals(routes.get(0), foundRoute.element(), "Expected the found route to be the first one");

        foundRoute = transportMap.getRouteEdge(routes.get(1));
        assertEquals(routes.get(1), foundRoute.element(), "Expected the found route to be the second one");

        foundRoute = transportMap.getRouteEdge(new Route(transportModesMaps.get(0)));
        assertNull(foundRoute, "Expected the found route edge to be null, since it does not exist in the transport map");
    }

    @Test
    void getRouteEdge_throwsIllegalArgumentException_whenProvidedRouteIsNull() {
        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalArgumentException.class, () -> transportMap.getRouteEdge(null), "Expected an exception since the provided route is null");
    }

    @Test
    void updateRouteMetric_updatedTheRouteMetric() {
        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        Route route = routes.get(0);
        String transportName = transportNames.get(0);

        transportMap.updateRouteMetric(route, transportName, CostType.DURATION, 5.5);
        double currentDuration = route.getTransportMetricsByTransportName(transportName).getDuration();
        assertEquals(5.5, currentDuration, 0.001, "Expected the duration to have been updated");

        transportMap.updateRouteMetric(route, transportName, CostType.DURATION, 7.7);
        currentDuration = route.getTransportMetricsByTransportName(transportName).getDuration();
        assertEquals(7.7, currentDuration, 0.001, "Expected the duration to have been updated again");

        transportMap.updateRouteMetric(route, transportName, CostType.DISTANCE, 2.5);
        double currentDistance = route.getTransportMetricsByTransportName(transportName).getDistance();
        assertEquals(2.5, currentDistance, 0.001, "Expected the distance to have been updated");

        transportMap.updateRouteMetric(route, transportName, CostType.COST, 3.5);
        double currentCost = route.getTransportMetricsByTransportName(transportName).getCost();
        assertEquals(3.5, currentCost, 0.001, "Expected the cost to have been updated");
    }

    @Test
    void updateRouteMetric_throwsIllegalArgumentException_whenInvalidParametersArePassed() {
        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.updateRouteMetric(null, transportNames.get(0), CostType.DURATION, 95.0);
        }, "Expected an exception when the route is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.updateRouteMetric(routes.get(0), "helicopter", CostType.DURATION, 95.0);
        }, "Expected an exception when the transport mode does not exist in the route");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.updateRouteMetric(routes.get(0), "", CostType.DURATION, 95.0);
        }, "Expected an exception when the transport mode name is blank");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.updateRouteMetric(routes.get(0), transportNames.get(0), null, 95.0);
        }, "Expected an exception when the cost type is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.updateRouteMetric(routes.get(0), transportNames.get(0), CostType.DURATION, -5.0);
        }, "Expected an exception when the new cost is invalid");
    }

    @Test
    void updateRouteMetric_throwsInvalidEdgeException_whenRouteIsNotPartOfTheTransportMap() {
        Route outsideRoute = routes.get(0);

        assertThrows(InvalidEdgeException.class, () -> {
            transportMap.updateRouteMetric(outsideRoute, transportNames.get(0), CostType.DURATION, 95.0);
        }, "Expected an exception when the provided route is not part of the transport map");
    }

    @Test
    void numOfStopsChanges_whenStopsAdded() {
        assertEquals(0, transportMap.numStops(), "Number of stops should start at 0");

        for (int i = 0; i < stops.size(); i++) {
            transportMap.addStop(stops.get(i));
            assertEquals(i + 1, transportMap.numStops(), "Number of stops should be already 1");
        }
    }

    @Test
    void numRoutesChanges_whenRoutesAdded() {
        assertEquals(0, transportMap.numRoutes(), "Number of stops should start at 0");

        transportMap.addStops(stops);

        for (int i = 0; i < stops.size(); i++) {
            transportMap.addRoute(stops.get(0), stops.get(1), routes.get(i));
            assertEquals(i + 1, transportMap.numRoutes(), "Number of stops should be already 1");
        }
    }

    @Test
    void getStops_containsAllAddedStops() {
        transportMap.addStops(stops);
        Collection<Vertex<Stop>> stopsFromTransportMap = transportMap.getStops();
        List<Vertex<Stop>> stopList = stopsFromTransportMap.stream().toList();

        for (int i = 0; i < stops.size(); i++) {
            assertTrue(stops.contains(stopList.get(i).element()), "The stops in the transportation map need to be the same as the ones added");
        }
    }

    @Test
    void getRoutes_containsAllAddedRoutes() {
        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        Collection<Edge<Route, Stop>> routesFromTransportMap = transportMap.getRoutes();
        List<Edge<Route, Stop>> routeList = routesFromTransportMap.stream().toList();

        for (int i = 0; i < stops.size(); i++) {
            assertTrue(routes.contains(routeList.get(i).element()), "The routes in the transportation map need to be the same as the ones added");
        }
    }

    @Test
    void getGraph_returnsUpToDateGraph_AfterAddStop_numStopChanges() {
        assertEquals(0, transportMap.numStops(), "Number of stops should start at 0");

        Graph<Stop, Route> graph = transportMap.getTransportGraph();
        graph.insertVertex(stops.get(0));

        assertEquals(1, transportMap.numStops(), "Number of stops need to be 1");
    }

    @Test
    void stopExists_returnsCorrectly() {
        Vertex<Stop> stopVertex = transportMap.addStop(stops.get(0));
        assertTrue(transportMap.stopExists(stopVertex), "The added stop vertex should exist on the transport map");

        TransportMap transportMap2 = new TransportMap();
        Vertex<Stop> stopVertex2 = transportMap2.addStop(stops.get(1));
        assertFalse(transportMap.stopExists(stopVertex2), "The stop vertex added to another transport map should not exist in the original transport map");

        Vertex<Stop> stopVertex3 = transportMap.addStop(stops.get(2));
        assertTrue(transportMap.stopExists(stopVertex3), "The stop vertex added again to the original transport map should exist in it");
    }

    @Test
    void routeExists_returnsCorrectly() {
        transportMap.addStops(stops);

        Edge<Route, Stop> routeEdge = transportMap.addRoute(stops.get(0), stops.get(1), routes.get(0));
        assertTrue(transportMap.routeExists(routeEdge), "The added edge route should exist on the transport map");

        TransportMap transportMap2 = new TransportMap();
        transportMap2.addStops(stops);
        Edge<Route, Stop> routeEdge2 = transportMap2.addRoute(stops.get(0), stops.get(1), routes.get(1));
        assertFalse(transportMap.routeExists(routeEdge2), "The edge route added in another transport map should not exist on the original transport map");

        Edge<Route, Stop> routeEdge3 = transportMap.addRoute(stops.get(1), stops.get(2), routes.get(2));
        assertTrue(transportMap.routeExists(routeEdge3), "The newly added edge route should exist in the transport map");
    }

    @Test
    void uniqueTransportModesAreAdded_whenNewTransportModeIsAdded() {
        transportMap.addStops(stops);

        assertEquals(0, transportMap.getTransportModeNames().size(), "The number of unique transportation modes should be 0");

        transportMap.addRoute(stops.get(0), stops.get(1), routes.get(0));
        assertEquals(2, transportMap.getTransportModeNames().size(), "The number of unique transportation modes should be 2");

        transportMap.addRoute(stops.get(0), stops.get(1), routes.get(1));
        assertEquals(3, transportMap.getTransportModeNames().size(), "The number of unique transportation modes should be 3");

        transportMap.addRoute(stops.get(0), stops.get(1), routes.get(2));
        assertEquals(3, transportMap.getTransportModeNames().size(), "The number of unique transportation modes should still be 3");
    }

    @Test
    void addedRouteWithStopsNotInGraph_throwsInvalidVertexException() {
        assertThrows(InvalidVertexException.class, () -> {
            transportMap.addRoute(stops.get(0), stops.get(1), routes.get(0));
        }, "Expected an exception when a stop that is not in the graph is used to form an edge");
    }

    @Test
    void findStopsByDistance_findsStops() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        Vertex<Stop> originVertex = stopVertices.get(0);
        transportMap.addRoutes(tripletRoutes);

        assertEquals(1, transportMap.findStopsByDistance(originVertex, 0).size(), "Expected the distance 0 to be the origin");
        assertEquals(2, transportMap.findStopsByDistance(originVertex, 1).size(), "Expected the distance 1 to have the 2 adjacent stops");
        assertEquals(0, transportMap.findStopsByDistance(originVertex, 2).size(), "Expected the distance 2 to have the 0 adjacent stops");

        assertNotEquals(3, transportMap.findStopsByDistance(originVertex, 3).size(), "Didn't expect the distance of 3 to have any adjacent stops");

        Stop stopA = new Stop("A", "A", 0.0, 0.0, new Point(0, 0));
        transportMap.addStop(stopA);
        transportMap.addRoute(stops.get(2), stopA, new Route(transportModesMaps.get(0)));
        assertEquals(1, transportMap.findStopsByDistance(originVertex, 2).size(), "Expected the distance 2 to have now 1 adjacent stop");

        Stop stopB = new Stop("B", "B", 0.0, 0.0, new Point(0, 0));
        transportMap.addStop(stopB);
        transportMap.addRoute(stops.get(1), stopB, new Route(transportModesMaps.get(0)));
        assertEquals(2, transportMap.findStopsByDistance(originVertex, 2).size(), "Expected the distance 2 to have now 2 adjacent stops");

        Stop stopC = new Stop("C", "C", 0.0, 0.0, new Point(0, 0));
        transportMap.addStop(stopC);
        transportMap.addRoute(stopB, stopC, new Route(transportModesMaps.get(0)));
        assertEquals(1, transportMap.findStopsByDistance(originVertex, 3).size(), "Expected the distance 3 to have now 1 adjacent stops");
    }

    @Test
    void findStopsByDistance_throwsIllegalArgumentException_whenDistanceIsNegative() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        Vertex<Stop> originVertex = stopVertices.get(0);
        transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.findStopsByDistance(originVertex, -1);
        }, "Expected to throw an InvalidArgumentException when the number of stops distance is negative");

        assertDoesNotThrow(() -> {
            transportMap.findStopsByDistance(originVertex, 3);
        }, "Did not except to throw an Exception when the number is positive");
    }

    @Test
    void findStopsByDistance_throwsInvalidVertexException_whenOriginIsNotPartOfTheGraph() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertDoesNotThrow(() -> {
            transportMap.findStopsByDistance(stopVertices.get(0), 3);
        }, "Should not throw in a stop vertex already in the graph");

        Graph<Stop, Route> newGraph = new GraphEdgeList<>();
        Vertex<Stop> newStopVertex = newGraph.insertVertex(new Stop("B", "B", 0.0, 0.0, new Point(0, 0)));

        assertThrows(InvalidVertexException.class, () -> {
            transportMap.findStopsByDistance(newStopVertex, 3);
        }, "A vertex outside of the graph should throw InvalidVertexException");
    }

    @Test
    void stopCentralization_returnsOrderedStops() {
        transportMap.addStops(stops);
        transportMap.addRoute(stops.get(0), stops.get(1), routes.get(0));

        // Perform the test and validate stop centralization
        validateStopCentralizationOrder();

        transportMap.addRoute(stops.get(0), stops.get(2), routes.get(1));

        // Validate the order again after adding the second route
        validateStopCentralizationOrder();

        Vertex<Stop> addedStop = transportMap.addStop(new Stop("code 3", "Name 3", 0, 0, new Point(0, 0)));
        transportMap.addRoute(stops.get(0), addedStop.element(), routes.get(2));

        // Validate the order after adding a third stop and route
        validateStopCentralizationOrder();
    }

    void validateStopCentralizationOrder() {
        List<Pair<Stop, Integer>> stopCentralization = transportMap.stopCentralization();
        for (int i = 0; i < stopCentralization.size() - 1; i++) {
            int currentNumberOfConnections = stopCentralization.get(i).getValue1();
            int nextNumberOfConnections = stopCentralization.get(i + 1).getValue1();
            assertTrue(currentNumberOfConnections >= nextNumberOfConnections, "The number of connections in the current stop should be greater than in the next one");
        }
    }

    @Test
    void stopCentralizedStops_returnsOnlyTheDefinedStops() {
        transportMap.addStops(stops);
        assertEquals(3, transportMap.topCentralizedStops().size(), "The number of top centralized stops should be 3 at this point");

        for (int i = 0; i < 50; i++) {
            transportMap.addStop(new Stop("code 1", "Name 1", 0, 0, new Point(0, 0)));
        }
        assertEquals(TransportMap.NUMBER_OF_TOP_CENTRALIZED_STOPS, transportMap.topCentralizedStops().size(), "The number of top centralized stops should be what was chosen in the constant");
    }

    @Test
    void incidentRoutes_shouldReturnCorrectRoutes() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertTrue(transportMap.incidentRoutes(stopVertices.get(0)).isEmpty(), "Incident routes should be initially empty");

        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);
        Set<Edge<Route, Stop>> incidentRoutes = new HashSet<>(transportMap.incidentRoutes(stopVertices.get(0)));

        // Verify that the expected routes are present
        assertEquals(2, incidentRoutes.size(), "There should be exactly 2 incident routes");
        assertTrue(incidentRoutes.contains(routeEdges.get(0)), "The first route should be present in the incident routes");
        assertTrue(incidentRoutes.contains(routeEdges.get(1)), "The second route should be present in the incident routes");
    }

    @Test
    void oppositeStop_returnsCorrectStop() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);

        assertEquals(stopVertices.get(0), transportMap.oppositeStop(stopVertices.get(1), routeEdges.get(0)), "The opposite vertex of the vertex with index 1 should be the one with index 0");
        assertEquals(stopVertices.get(1), transportMap.oppositeStop(stopVertices.get(0), routeEdges.get(0)), "Opposite vertices should oppose one another");

        assertEquals(stopVertices.get(0), transportMap.oppositeStop(stopVertices.get(2), routeEdges.get(1)), "The opposite vertex of the vertex with index 2 should be the one with index 0");
        assertEquals(stopVertices.get(2), transportMap.oppositeStop(stopVertices.get(0), routeEdges.get(1)), "Again, opposite vertices should oppose one another");
    }

    @Test
    void stopDegree_shouldReturnCorrectSize() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertEquals(0, transportMap.stopDegree(stopVertices.get(0)), "The first stop should still have a stop degree of 0");

        transportMap.addRoutes(tripletRoutes);
        assertEquals(2, transportMap.stopDegree(stopVertices.get(0)), "The first stop should have a stop degree of 2");

        Vertex<Stop> newStopVertex = transportMap.addStop(new Stop("code 1", "Name 1", 0, 0, new Point(0, 0)));
        transportMap.addRoute(stopVertices.get(0), newStopVertex, new Route(transportModesMaps.get(0)));
        assertEquals(3, transportMap.stopDegree(stopVertices.get(0)), "The first stop should now have a stop degree of 3");
    }

    @Test
    void numIsolatedStops_shouldReturnTheCorrectNumberOfIsolatedStops() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertEquals(stops.size(), transportMap.numIsolatedStops(), "When there are no routes, all stops are isolated");

        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);
        assertEquals(0, transportMap.numIsolatedStops(), "When all stops are of at least one route, there should be no isolated stops");

        transportMap.addStop(new Stop("code 1", "Name 1", 0, 0, new Point(0, 0)));
        assertEquals(1, transportMap.numIsolatedStops(), "When a new stop is added, it will always be isolated");
    }

    @Test
    void numRoutesByTransport_shouldReturnTheCorrectTransportCount() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertEquals(0, transportMap.numRoutesByTransport().size(), "When there are no routes, there are no transports either");

        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);
        for (Map.Entry<String, Integer> transportCount : transportMap.numRoutesByTransport().entrySet()) {
            assertEquals(2, transportCount.getValue(), "Each transport should be used twice");
        }

        Stop newStop1 = new Stop("A", "A", 0, 0, new Point(0, 0));
        Stop newStop2 = new Stop("B", "B", 0, 0, new Point(0, 0));

        transportMap.addStop(newStop1);
        transportMap.addStop(newStop2);
        transportMap.addRoute(newStop1, newStop2, new Route(transportModesMaps.get(0)));

        assertEquals(3, transportMap.numRoutesByTransport().get(transportNames.get(0)), "The number of routes in the newly added transport mode should be 3");
    }

    @Test
    void addTransportModes_shouldAddTransportNamesAndColors() throws CsvValidationException, IOException {
        assertTrue(transportMap.getTransportNamesAndColors().isEmpty(), "Transport modes should start empty");

        DataSetImporter importer = new DataSetImporter(DataSetImporter.ImportationMode.TEST);
        transportMap.addTransportModes(importer.importTransportColors());

        assertFalse(transportMap.getTransportNamesAndColors().isEmpty(), "There should be at least the imported transport modes");
    }

    @Test
    void findingAnOptimalWith_withoutSettingAStrategy_throwsNotSetStrategyException() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalStateException.class, () -> {
            transportMap.findOptimalPath(stopVertices.get(0), stopVertices.get(1), new HashSet<>(transportNames), CostType.DURATION);
        });
    }

    @Test
    void stopsAreAdjacent_verifiesStopAdjacency() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertTrue(transportMap.stopsAreAdjacent(stopVertices.get(0), stopVertices.get(1)), "These stops should be adjacent");
        assertTrue(transportMap.stopsAreAdjacent(stopVertices.get(0), stopVertices.get(2)), "These stops should be adjacent");

        Vertex<Stop> newStopVertex = transportMap.addStop(new Stop("code 1", "Name 1", 0, 0, new Point(0, 0)));
        assertFalse(transportMap.stopsAreAdjacent(stopVertices.get(0), newStopVertex), "The first and new stop should not be adjacent");
    }

    @Test
    void stopsAreAdjacent_selfAdjacencyShouldBeFalse() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertFalse(transportMap.stopsAreAdjacent(stopVertices.get(0), stopVertices.get(0)), "The same stop should not be adjacent with itself");
    }

    @Test
    void routeHasEnabledTransports_returnsCorrectly() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);

        Vertex<Stop> firstStop = stopVertices.get(0);
        Vertex<Stop> secondStop = stopVertices.get(1);
        Edge<Route, Stop> route = routeEdges.get(0);

        assertTrue(transportMap.routeHasEnabledTransports(firstStop, secondStop), "There should be enabled transports at the start");

        transportMap.setRouteTransportModeEnabled(route, transportNames.get(0), false);
        assertTrue(transportMap.routeHasEnabledTransports(firstStop, secondStop), "There should still be one transport enabled");

        transportMap.setRouteTransportModeEnabled(route, transportNames.get(1), false);
        assertFalse(transportMap.routeHasEnabledTransports(firstStop, secondStop), "Now, there should not be any more transports enabled");
    }

    @Test
    void routeHasEnabledTransports_throwsInvalidRouteException_whenThereIsNoRouteBetweenStops() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);

        assertThrows(InvalidRouteException.class, () -> {
            transportMap.routeHasEnabledTransports(stopVertices.get(0), stopVertices.get(1));
        });
    }

    @Test
    void setRouteTransportModeEnabled_shouldThrowIllegalArgumentException_whenTheRouteOrTransportAreInvalid() {
        transportMap.addStops(stops);
        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.setRouteTransportModeEnabled(null, BOAT, true);
        }, "Should have thrown an exception since the route is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.setRouteTransportModeEnabled(routeEdges.get(0), null, true);
        }, "Should have thrown an exception since the transport name is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.setRouteTransportModeEnabled(routeEdges.get(0), "", true);
        }, "Should have thrown an exception since the transport is empty");
    }

    @Test
    void setRouteTransportModeEnabled_shouldThrowIllegalStateException_whenTheTransportModeDoesNotExistInTheRoute() {
        transportMap.addStops(stops);
        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);

        assertThrows(IllegalStateException.class, () -> {
            transportMap.setRouteTransportModeEnabled(routeEdges.get(0), "Bat Car", true);
        }, "Should have thrown an exception since the route does not have the Bat Car transport mode");
    }

    @Test
    void getRouteBetweenStops_shouldReturnTheOptionalRoute() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        List<Edge<Route, Stop>> routeEdges = transportMap.addRoutes(tripletRoutes);

        Vertex<Stop> firstStop = stopVertices.get(0);
        Vertex<Stop> secondStop = stopVertices.get(1);
        Edge<Route, Stop> expectedRoute = routeEdges.get(0);

        Optional<Edge<Route, Stop>> optionalRoute = transportMap.getRouteBetweenStops(firstStop, secondStop);

        assertEquals(expectedRoute, optionalRoute.orElse(null));
    }

    @Test
    void getRouteBetweenStops_throwsIllegalArgumentException_ifOneOfTheStopsIsNull() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.getRouteBetweenStops(null, stopVertices.get(1));
        }, "Should have thrown an exception since the first stop is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.getRouteBetweenStops(stopVertices.get(0), null);
        }, "Should have thrown an exception since the second stop is null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.getRouteBetweenStops(null, null);
        }, "Should have thrown an exception since both stops are null");
    }

    @Test
    void getRouteBetweenStops_shouldReturnOptionalEmpty_whenTheSameStopIsProvidedTwice() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        Optional<Edge<Route, Stop>> optionalRoute = transportMap.getRouteBetweenStops(stopVertices.get(0), stopVertices.get(0));

        assertTrue(optionalRoute.isEmpty(), "Should have been empty since the same stop was provided");
    }

    @Test
    void getRouteBetweenStops_shouldReturnOptionalEmpty_whenNoRouteIsFound() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        Optional<Edge<Route, Stop>> optionalRoute = transportMap.getRouteBetweenStops(stopVertices.get(0), stopVertices.get(1));

        assertTrue(optionalRoute.isEmpty(), "Should have been empty since there are not routes in the transport map");
    }

    @Test
    void hasIncidentRoutes_shouldReturnCorrectly() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertFalse(transportMap.hasIncidentRoutes(stopVertices.get(0)), "Should not have incident routes yet");

        transportMap.addRoutes(tripletRoutes);
        assertTrue(transportMap.hasIncidentRoutes(stopVertices.get(0)), "Should have 2 incident routes already");
    }

    @Test
    void getNumAdjacentStops_shouldReturnCorrectly() {
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        assertEquals(0, transportMap.getNumAdjacentStops(stopVertices.get(0)), "Should not have incident routes yet");

        transportMap.addRoutes(tripletRoutes);
        assertEquals(2, transportMap.getNumAdjacentStops(stopVertices.get(0)), "Should have 2 incident routes already");
    }

    @Test
    void topCentralizedStops_shouldThrowIllegalArgumentException_whenProvidedListIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            transportMap.topCentralizedStops(null);
        }, "Should have thrown since the provided list is null");
    }

    @Test
    void getStopsOrderedByName_returnTheStopsOrderedByName(){
        List<Vertex<Stop>> stopVertices = transportMap.addStops(stops);
        Collection<Vertex<Stop>> orderedStops = transportMap.getStopsOrderedByName();

        int i = 0;
        for(Vertex<Stop> orderedStop : orderedStops){
            assertEquals(stopVertices.get(i++), orderedStop);
        }
    }

    @Test
    void getTransportModeColor_returnsTheCorrectColor(){
        String colorName = "#e3e3e323";

        Map<String, TransportColor> transportModes = new HashMap<>();
        transportModes.put(BOAT, new TransportColor(colorName));

        transportMap.addTransportModes(transportModes);

        assertEquals(colorName, transportMap.getTransportModeColor(BOAT).toString(), "The color should have been the same");
        assertNotEquals("#e3e3e3", transportMap.getTransportModeColor(BOAT).toString(), "The color should not have been the same");
    }

    @Test
    void getUniqueTransports_returnsCorrectly(){
        assertTrue(transportMap.getUniqueTransports().isEmpty(), "The transport map should yet have not transport modes");

        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);

        assertEquals(3, transportMap.getUniqueTransports().size(), "The transport map should already have 3 different transport modes");
    }

    @Test
    void transportModeIsUsed_shouldReturnCorrectly(){
        assertFalse(transportMap.transportModeIsUsed(BOAT), "The transport map should not have transport modes");

        Map<String, TransportColor> transportModes = new HashMap<>();
        transportModes.put(BOAT, new TransportColor("#eee"));
        transportMap.addTransportModes(transportModes);

        assertTrue(transportMap.getTransportModeNames().contains(BOAT), "The boat should have been added to the transport map");
        assertFalse(transportMap.transportModeIsUsed(BOAT), "Even though the transport map has the boat, no routes use it");

        transportMap.addStops(stops);
        transportMap.addRoutes(tripletRoutes);
        assertTrue(transportMap.transportModeIsUsed(BOAT), "The boat is finally used by a route");
    }
}