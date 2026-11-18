/******************************************************************************************************************************
 * File Name: PathfindingStrategyTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.strategy;

import com.brunomnsilva.smartgraph.graph.Vertex;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.model.*;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PathfindingStrategyTest {
    PathfindingStrategy strategy;
    TransportMap transportMap;
    Vertex<Stop> originStopVertex;
    Vertex<Stop> destinationStopVertex;
    CostType preferredCost;
    Set<String> chosenTransports;

    @BeforeEach
    void setUp() {
        strategy = new PositivePathfindingStrategy();

        transportMap = new TransportMap();

        originStopVertex = transportMap.addStop(new Stop("STOP", "Stop", 0, 0, new Point(0, 0)));
        destinationStopVertex = transportMap.addStop(new Stop("STOP", "Stop", 0, 0, new Point(0, 0)));

        TransportModesMap transportModesMap = new TransportModesMap();

        TransportMetrics transportMetrics = new TransportMetrics(1.0, 1.0, 1.0);
        transportModesMap.put("Bus", new Transport("Bus", transportMetrics));

        transportMap.addRoute(originStopVertex, destinationStopVertex, new Route(transportModesMap));

        preferredCost = CostType.DURATION;

        chosenTransports = new HashSet<>();
        chosenTransports.add("Bus");
        chosenTransports.add("Walk");
        chosenTransports.add("Bicycle");
    }

    @Test
    void validateOptimalPathArguments_doesNotThrow_whenAllArgumentsAreValid() {
        PathfindingArguments arguments = new PathfindingArguments(transportMap, originStopVertex, destinationStopVertex, chosenTransports, preferredCost);

        assertDoesNotThrow(() -> {
            strategy.findOptimalPath(arguments);
        }, "Should not throw when all arguments are valid");
    }

    @Test
    void validateOptimalPathArguments_throwsIllegalArgumentException_whenTransportMapIsNull() {
        PathfindingArguments arguments = new PathfindingArguments(null, originStopVertex, destinationStopVertex, chosenTransports, preferredCost);
        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(arguments);
        }, "Should have thrown an exception when the transport map is null");
    }

    @Test
    void validateOptimalPathArguments_throwsIllegalArgumentException_whenStopsAreNotProvided() {
        PathfindingArguments argumentsWithNullOrigin = new PathfindingArguments(transportMap, null, destinationStopVertex, chosenTransports, preferredCost);

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(argumentsWithNullOrigin);
        }, "Should have thrown an exception when the origin stop is null");

        PathfindingArguments argumentsWithNullDestination = new PathfindingArguments(transportMap, originStopVertex, null, chosenTransports, preferredCost);

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(argumentsWithNullDestination);
        }, "Should have thrown an exception when the destination stop is null");
    }

    @Test
    void validateOptimalPathArguments_throwsIllegalArgumentException_whenOriginAndDestinationAreTheSameStop() {
        PathfindingArguments arguments = new PathfindingArguments(transportMap, originStopVertex, originStopVertex, chosenTransports, preferredCost);

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(arguments);
        }, "Should have thrown an exception when the origin and destination are the same");
    }

    @Test
    void validateOptimalPathArguments_throwsIllegalArgumentException_whenPreferredCostIsNotProvided() {
        PathfindingArguments arguments = new PathfindingArguments(transportMap, originStopVertex, destinationStopVertex, chosenTransports, null);

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(arguments);
        }, "Should have thrown an exception when the preferred cost is not provided");
    }

    @Test
    void validateOptimalPathArguments_throwsIllegalArgumentException_whenChosenTransportsAreNotValid() {
        PathfindingArguments argumentsWithNullTransports = new PathfindingArguments(transportMap, originStopVertex, destinationStopVertex, null, preferredCost);

        assertThrows(IllegalArgumentException.class, () -> {
            strategy.findOptimalPath(argumentsWithNullTransports);
        }, "Should have thrown an exception when the chosen transports are null");

        PathfindingArguments argumentsWithEmptyTransports = new PathfindingArguments(transportMap, originStopVertex, destinationStopVertex, chosenTransports, preferredCost);

        assertThrows(IllegalArgumentException.class, () -> {
            chosenTransports.clear();
            strategy.findOptimalPath(argumentsWithEmptyTransports);
        }, "Should have thrown an exception when the chosen transports are empty");
    }
}