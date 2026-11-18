/******************************************************************************************************************************
 * File Name: DijkstraTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.pathfinding;

import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.Main;
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.CostType;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;
import pt.pa.pathfinding.Dijkstra;
import pt.pa.utils.MainSceneLoader;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {

    TransportMap transportMap;
    Vertex<Stop> originStopVertex;
    Vertex<Stop> destinationStopVertex;
    CostType preferredCost;
    Set<String> chosenTransports;

    @BeforeEach
    void setUp() {
        transportMap = new TransportMap();

        MainSceneLoader.importRoutesAndStops(transportMap);

        preferredCost = CostType.DURATION;

        chosenTransports = new HashSet<>();
        chosenTransports.add("Bus");
        chosenTransports.add("Walk");
        chosenTransports.add("Bicycle");
    }

    @Test
    void dijkstra_createsTheCorrectTable() {
        for (Vertex<Stop> vertex : transportMap.getStops()) {
            String stopName = vertex.element().getName();
            switch (stopName) {
                case "Amadora" -> originStopVertex = vertex;
                case "Cacém" -> destinationStopVertex = vertex;
            }
        }

        Dijkstra dijkstra = new Dijkstra();

        Map<Vertex<Stop>, Triplet<Vertex<Stop>, String, TransportMetrics>> dijkstraTable = dijkstra.dijkstra(transportMap, originStopVertex, chosenTransports, preferredCost);
        Vertex<Stop> predecessorVertex = dijkstraTable.get(destinationStopVertex).getValue0();

        assertEquals(originStopVertex, dijkstraTable.get(predecessorVertex).getValue0(), "The stop that comes before the one that comes before Cacém should be the origin (Amadora)");
    }

    @Test
    void reconstructPath_reconstructsTheCorrectPath() {
        Vertex<Stop> middleVertex = null;

        for (Vertex<Stop> vertex : transportMap.getStops()) {
            String stopName = vertex.element().getName();

            switch (stopName) {
                case "Amadora" -> originStopVertex = vertex;
                case "Cacém" -> destinationStopVertex = vertex;
                case "Queluz" -> middleVertex = vertex;
            }
        }

        Dijkstra dijkstra = new Dijkstra();

        List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructedPath = dijkstra.reconstructPath(originStopVertex, destinationStopVertex, dijkstra.dijkstra(transportMap, originStopVertex, chosenTransports, preferredCost));

        Iterator<Triplet<Vertex<Stop>, String, TransportMetrics>> pathIterator = reconstructedPath.iterator();

        assertEquals(originStopVertex, pathIterator.next().getValue0(), "The first stop vertex should be the origin");
        assertEquals(middleVertex, pathIterator.next().getValue0(), "The second stop vertex should be the middle vertex");
        assertEquals(destinationStopVertex, pathIterator.next().getValue0(), "The third stop vertex should be the destination");

        assertFalse(pathIterator.hasNext(), "There should not be any other stop in the path");
    }

    @Test
    void dijkstraReconstructsTheOptimalPath() {
        Vertex<Stop> coina = null, sesimbra = null, palmela = null, setubal = null;

        for (Vertex<Stop> vertex : transportMap.getStops()) {
            String stopName = vertex.element().getName();

            switch (stopName) {
                case "Coina" -> coina = vertex;
                case "Sesimbra" -> sesimbra = vertex;
                case "Palmela" -> palmela = vertex;
                case "Setúbal" -> setubal = vertex;
            }
        }

        // Make sure all transports are used
        chosenTransports.add("Train");
        chosenTransports.add("Boat");

        Dijkstra dijkstra = new Dijkstra();

        // A path between Coina and Setubal
        List<Triplet<Vertex<Stop>, String, TransportMetrics>> reconstructedPath = dijkstra.reconstructPath(coina, setubal, dijkstra.dijkstra(transportMap, coina, chosenTransports, CostType.DISTANCE));

        Iterator<Triplet<Vertex<Stop>, String, TransportMetrics>> pathIterator = reconstructedPath.iterator();

        // Start at Coina
        Vertex<Stop> currentStop = pathIterator.next().getValue0();
        assertEquals(currentStop, coina, "The first stop should be the Coina");

        // Choose Palmela over Sesimbra because it is closer
        currentStop = pathIterator.next().getValue0();
        assertEquals(currentStop, palmela, "The second stop should be Palmela");
        assertNotEquals(currentStop, sesimbra, "The second stop should not be the Sesimbra");

        // End at Setubal
        currentStop = pathIterator.next().getValue0();
        assertEquals(currentStop, setubal, "The second stop should be the Setubal");
    }

    @Test
    void reconstructPath_throwsPathNotFoundException_whenAPathIsNotFound() {
        Vertex<Stop> cascais = null, oeiras = null;

        for (Vertex<Stop> vertex : transportMap.getStops()) {
            String stopName = vertex.element().getName();

            switch (stopName) {
                case "Cascais" -> cascais = vertex;
                case "Oeiras" -> oeiras = vertex;
            }
        }

        // Make sure all transports are used
        chosenTransports.add("Train");
        chosenTransports.add("Boat");

        Dijkstra dijkstra = new Dijkstra();

        Vertex<Stop> finalCascais = cascais;
        Vertex<Stop> finalOeiras = oeiras;
        assertThrows(PathNotFoundException.class, () -> {
            // A path between Cascais and Oeiras
            dijkstra.reconstructPath(finalCascais, finalOeiras, dijkstra.dijkstra(transportMap, finalOeiras, chosenTransports, CostType.DISTANCE));
        }, "Should not have been possible to find a path between Cascas and Oeiras");
    }
}