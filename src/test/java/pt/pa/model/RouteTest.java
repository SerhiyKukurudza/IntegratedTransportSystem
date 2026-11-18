/******************************************************************************************************************************
 * File Name: RouteTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RouteTest {

    final String BOAT = "Boat";
    final String WALK = "Walk";
    final String BUS = "Bus";
    final String BICYCLE = "Bicycle";

    TransportModesMap transportModesMap;
    Route route;

    @BeforeEach
    void setUp() {
        transportModesMap = new TransportModesMap();

        TransportMetrics transportMetrics = new TransportMetrics(2.2, 3.3, 4.4);
        transportModesMap.put(BOAT, new Transport(BOAT, transportMetrics));

        TransportMetrics transportMetrics1 = new TransportMetrics(3.2, 1.3, 5.4);
        transportModesMap.put(WALK, new Transport(WALK, transportMetrics1));

        TransportMetrics transportMetrics2 = new TransportMetrics(4.2, 2.3, 7.4);
        transportModesMap.put(BUS, new Transport(BUS, transportMetrics2));

        TransportMetrics transportMetrics3 = new TransportMetrics(1.3, 6.5, 0.0);
        transportModesMap.put(BICYCLE, new Transport(BICYCLE, transportMetrics3));

        route = new Route(transportModesMap);
    }

    @Test
    void newRoutes_shouldNotBeEquals() {
        Route route2 = new Route(transportModesMap);

        assertNotEquals(route, route2, "Expected routes with identical values to be unique, but they are considered equal");
    }

    @Test
    void newRoute_throwsInvalidArgumentException_whenThereIsNotAtLeastOneTransportationMode() {
        assertThrows(IllegalArgumentException.class, () -> new Route(null), "Excepted an exception when a route is created with null transports");
        assertThrows(IllegalArgumentException.class, () -> new Route(new TransportModesMap()), "Excepted an exception when a route is created with empty transports");

        assertDoesNotThrow(() -> {
            new Route(transportModesMap);
        }, "Unexpected exception when a route with at least one transportation mode is created");
    }

    @Test
    void getCheapestTransportWithCost_returnsTheCorrectTransport() {
        Set<String> usableTransports = new HashSet<>();
        usableTransports.add(BOAT);
        usableTransports.add(WALK);

        Triplet<String, Double, TransportMetrics> cheapestTransport = route.getCheapestTransportWithCost(usableTransports, CostType.DURATION);

        assertEquals(WALK, cheapestTransport.getValue0(), "The cheapest transport should have been walk");
        assertEquals(1.3, cheapestTransport.getValue1(), "The cheapest duration cost of walk should have been 1.3");

        TransportMetrics walkMetrics = transportModesMap.get(WALK).getTransportMetrics();
        assertEquals(walkMetrics, cheapestTransport.getValue2(), "The transport metrics received should have been the ones from the walk");

        usableTransports.remove(WALK);
        cheapestTransport = route.getCheapestTransportWithCost(usableTransports, CostType.DURATION);

        assertEquals(BOAT, cheapestTransport.getValue0(), "The cheapest transport should have been now the boat");
        assertEquals(3.3, cheapestTransport.getValue1(), "The cheapest duration cost of boat should have been 3.3");

        TransportMetrics boatMetrics = transportModesMap.get(BOAT).getTransportMetrics();
        assertEquals(boatMetrics, cheapestTransport.getValue2(), "The transport metrics receives should have been the boat");
    }

    @Test
    void getCheapestTransportWithCost_shouldThrowIllegalArgumentException_whenChosenTransportsAreNotNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            route.getCheapestTransportWithCost(new HashSet<>(), CostType.DURATION);
        }, "Should have thrown when the chosen transports are empty");

        assertThrows(IllegalArgumentException.class, () -> {
            route.getCheapestTransportWithCost(null, CostType.DURATION);
        }, "Should have thrown when the chosen transports are null");
    }

    @Test
    void getCheapestTransportWithCost_shouldThrowIllegalArgumentException_whenCostIsNull() {
        Set<String> usableTransports = new HashSet<>();
        usableTransports.add(BOAT);
        usableTransports.add(WALK);

        assertThrows(IllegalArgumentException.class, () -> {
            route.getCheapestTransportWithCost(usableTransports, null);
        }, "Should have thrown when the cost type is null");
    }

    @Test
    void getTransportMetricsByTransportName_shouldReturnCorrectTransportMetrics() {
        TransportMetrics boat = transportModesMap.get(BOAT).getTransportMetrics();
        TransportMetrics boat2 = route.getTransportMetricsByTransportName(BOAT);

        TransportMetrics walk = transportModesMap.get(WALK).getTransportMetrics();
        TransportMetrics walk2 = route.getTransportMetricsByTransportName(WALK);

        assertEquals(boat, boat2, "The boat's transport metrics should have been equals");
        assertEquals(walk, walk2, "The walk's transport metrics should have been equals");
        assertNotEquals(boat, walk2, "The boat's and walk's transport metrics should not be equals");
    }

    @Test
    void getTransportMetricsByTransportName_shouldThrowIllegalArgumentException_whenTransportNameIsNullOrEmpty() {
        assertThrows(IllegalArgumentException.class, () -> {
            route.getTransportMetricsByTransportName(null);
        }, "Should have thrown when the transport name is null");

        assertThrows(IllegalArgumentException.class, () -> {
            route.getTransportMetricsByTransportName("");
        }, "Should have thrown when the transport name is empty");
    }

    @Test
    void getEnabledTransportModesMap_shouldReturnOnlyEnabledTransports() {
        assertEquals(4, route.getEnabledTransportModesMap().size(), "Should start with the 4 transport modes enabled");

        route.setTransportModeEnabled(BUS, false);
        assertEquals(3, route.getEnabledTransportModesMap().size(), "One transport should have been disabled");

        route.setTransportModeEnabled(BUS, true);
        assertEquals(4, route.getEnabledTransportModesMap().size(), "The transport was enabled again");
    }

    @Test
    void chosenTransports_shouldReturnTheCorrectTransports() {
        assertTrue(route.chosenTransports(Set.of()).isEmpty(), "If no transports were given, no transport should be returned");

        assertEquals(1, route.chosenTransports(Set.of(BUS)).size(), "Should have returned only the provided bus transport mode");

        assertEquals(1, route.chosenTransports(Set.of(BUS, "Bat Car", "Skate")).size(), "Only existing transports can be chosen");
    }

    @Test
    void getCheapestTransportCost_shouldReturnTheCheapestTransportCost() {
        assertEquals(0.0, route.getCheapestTransportCost(), "The cheapest transport cost should be 0.0 duration from bicycle");
        assertNotEquals(3.3, route.getCheapestTransportCost(), "The cheapest cost should be none else");
    }

    @Test
    void hasEnabledTransports_shouldCheckIfThereAreEnabledTransports() {
        assertTrue(route.hasEnabledTransports(), "Should have enabled transports");

        route.setTransportModeEnabled(BUS, false);
        route.setTransportModeEnabled(WALK, false);
        route.setTransportModeEnabled(BOAT, false);
        route.setTransportModeEnabled(BICYCLE, false);

        assertFalse(route.hasEnabledTransports(), "Should not have anymore enabled transports");
    }

    @Test
    void getAverageDistance_shouldReturnTheAverageDistanceInTheRoute(){
        double averageDistance = (2.2 + 3.2 + 4.2 + 1.3) / 4;

        assertEquals(averageDistance, route.getAverageDistance(), 0.001, "The average distance was not correct");
        assertNotEquals(1000, route.getAverageDistance(), 0.001, "The average distance should be none else");
    }

    @Test
    void getBicycleDuration_shouldReturnBicycleDuration() {
        assertEquals(6.5, route.getBicycleDuration());
    }

    @Test
    void hasTransportMode_returnsCorrectly(){
        assertFalse(route.hasTransportMode("Bat Car"), "Should not contain the Bat Car");
        assertTrue(route.hasTransportMode(BOAT), "Should contain the boat");
    }

    @Test
    void getTransportNames_returnsCorrectly(){
        assertEquals(transportModesMap.keySet(), route.getTransportNames(), "Should return the same key set of transport names");
        assertNotEquals(Set.of(), route.getTransportNames(), "Should not be equals to nothing else");
    }

    @Test
    void getTransports_returnsCorrectly(){
        assertEquals(transportModesMap.values(), route.getTransports(), "Should return the same collection of transports");
        assertNotEquals(Collections.emptyList(), route.getTransports(), "Should not be equals to nothing else");
    }
}