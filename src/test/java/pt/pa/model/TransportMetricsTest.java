/******************************************************************************************************************************
 * File Name: TransportTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportMetricsTest {
    TransportMetrics transportMetrics;

    @BeforeEach
    void setUp() {
        transportMetrics = new TransportMetrics(2.5, 6.0, 0.53);
    }

    @Test
    void getDistance_returnsCorrectDistance() {
        assertEquals(2.5, transportMetrics.getDistance(), 0.001, "Expected transport metrics distance to be '2.5' ");
        assertNotEquals(2.4, transportMetrics.getDistance(), 0.001, "Expected transport metrics distance to not be '2.4' ");
    }

    @Test
    void getDuration_returnsCorrectDuration() {
        assertEquals(6.0, transportMetrics.getDuration(), 0.001, "Expected transport metrics duration to be '6.0' ");
        assertNotEquals(6.1, transportMetrics.getDuration(), 0.001, "Expected transport metrics duration to not be '6.1' ");
    }

    @Test
    void getCost_returnsCorrectCost() {
        assertEquals(0.53, transportMetrics.getCost(), 0.001, "Expected transport metrics cost to be '0.53' ");
        assertNotEquals(0.54, transportMetrics.getCost(), 0.001, "Expected transport metrics cost to not be '0.54' ");
    }

    @Test
    void setDistance_setsTheDistance() {
        assertEquals(2.5, transportMetrics.getDistance(), 0.001, "Expected transport metrics distance to be '2.5' ");

        transportMetrics.setDistance(2.9);
        assertNotEquals(2.5, transportMetrics.getDistance(), 0.001, "Expected transport metrics distance to have been updated");
        assertEquals(2.9, transportMetrics.getDistance(), 0.001, "Expected transport metrics distance to be '2.9' ");
    }

    @Test
    void setDuration_setsTheDuration() {
        assertEquals(6.0, transportMetrics.getDuration(), 0.001, "Expected transport metrics duration to be '6.0' ");

        transportMetrics.setDuration(13.0);
        assertNotEquals(6.0, transportMetrics.getDuration(), 0.001, "Expected transport metrics duration to have been updated");
        assertEquals(13.0, transportMetrics.getDuration(), 0.001, "Expected transport metrics duration to be '13.0' ");
    }

    @Test
    void setCost_setsTheCost() {
        assertEquals(0.53, transportMetrics.getCost(), 0.001, "Expected transport metrics cost to be '0.53' ");

        transportMetrics.setCost(2.0);
        assertNotEquals(0.53, transportMetrics.getCost(), 0.001, "Expected transport metrics cost to have been updated");
        assertEquals(2.0, transportMetrics.getCost(), 0.001, "Expected transport metrics cost to be '2.0' ");
    }

    @Test
    void settingNewTransportMetrics_shouldThrowIllegalArgumentException_whenValuesAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.setDistance(-3.0);
        }, "Expected exception since the distance cannot be negative");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.setDuration(-3.0);
        }, "Expected exception since the duration cannot be negative");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.setDistance(null);
        }, "Expected exception since the distance cannot be null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.setDuration(null);
        }, "Expected exception since the duration cannot be null");

        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.setCost(null);
        }, "Expected exception since the cost cannot be null");
    }

    @Test
    void setCostByType_setsTheCorrectCost() {
        assertNotEquals(10.0, transportMetrics.getDuration(), 0.001, "Expected the starting duration metric value not to be '10.0'");
        assertNotEquals(10.0, transportMetrics.getDistance(), 0.001, "Expected the starting distance metric value not to be '10.0'");
        assertNotEquals(10.0, transportMetrics.getCost(), 0.001, "Expected the starting cost metric value not to be '10.0'");

        transportMetrics.setCostByType(CostType.DURATION, 10.0);
        assertEquals(10.0, transportMetrics.getDuration(), 0.001, "Expected the duration metric to have been updated");

        transportMetrics.setCostByType(CostType.DISTANCE, 10.0);
        assertEquals(10.0, transportMetrics.getDistance(), 0.001, "Expected the distance metric to have been updated");

        transportMetrics.setCostByType(CostType.COST, 10.0);
        assertEquals(10.0, transportMetrics.getCost(), 0.001, "Expected the cost metric to have been updated");
    }

    @Test
    void newTransports_shouldNotBeEquals() {
        TransportMetrics transportMetrics1 = new TransportMetrics(2.5, 6.0, 0.53);
        TransportMetrics transportMetrics2 = new TransportMetrics(2.5, 6.0, 0.53);

        assertNotEquals(transportMetrics1, transportMetrics2, "Expected transport metrics with identical values to be unique, but they are considered equal");
    }

    @Test
    void newTransportMetricsThrowsIllegalArgumentException_whenAnyTypeOfCostIsNotProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(2.2, null, null);
        }, "Duration and cost should not be null");

        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(null, 20.2, null);
        }, "Distance and cost should not be null");

        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(null, null, 12.0);
        }, "Distance and duration should not be null");

        assertDoesNotThrow(() -> {
            new TransportMetrics(5.0, 12.0, 12.0);
        }, "With a valid distance, duration and cost, an error shouldn't be thrown");
    }

    @Test
    void newTransportMetrics_throwsInvalidArgumentException_whenDistanceIsNotBiggerThan0() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(-20.0, 5.0, 2.0);
        }, "Distance needs to be bigger than 0");

        assertDoesNotThrow(() -> {
            new TransportMetrics(20.0, 5.0, 2.0);
        }, "A distance can be bigger than 0");
    }

    @Test
    void newTransportMetrics_throwsInvalidArgumentException_whenDurationIsNotBiggerThan0() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(20.0, -5.0, 2.0);
        }, "A duration needs to be bigger than 0");

        assertDoesNotThrow(() -> {
            new TransportMetrics(0.0, 5.0, 2.0);
        }, "A duration can be bigger than 0");
    }

    @Test
    void newTransportMetricsClone_isDifferentFromClones() {
        TransportMetrics transportMetrics1 = new TransportMetrics(2.5, 6.0, 0.53);
        TransportMetrics transportMetrics2 = new TransportMetrics(transportMetrics1);

        assertNotEquals(transportMetrics1, transportMetrics2, "The cloned transport metrics should be different");
    }

    @Test
    void newTransportMetricsClone_throwsIllegalArgumentException_whenNullIsProvided() {
        assertThrows(IllegalArgumentException.class, () -> {
            new TransportMetrics(null);
        }, "A null transport metric cannot be cloned");

        assertDoesNotThrow(() -> {
            TransportMetrics transportMetrics1 = new TransportMetrics(2.5, 6.0, 0.53);
            new TransportMetrics(transportMetrics1);
        }, "When a valid transport metric is provided, it can be cloned");
    }

    @Test
    void sumTransportMetrics_shouldSum() {
        double distance = 5, duration = 2, cost = 1;
        TransportMetrics transportMetrics = new TransportMetrics(distance, duration, cost);

        transportMetrics.sumMetrics(transportMetrics);
        assertEquals(distance * 2, transportMetrics.getDistance(), 0.001, "The distance should have been summed up");
        assertEquals(duration * 2, transportMetrics.getDuration(), 0.001, "The duration should have been summed up");
        assertEquals(cost * 2, transportMetrics.getCost(), 0.001, "The cost should have been summed up");

        assertNotEquals(distance, transportMetrics.getDistance(), 0.001, "The distance should have been increased");
        assertNotEquals(duration, transportMetrics.getDuration(), 0.001, "The duration should have been increased");
        assertNotEquals(cost, transportMetrics.getCost(), 0.001, "The cost should have been increased");

        transportMetrics.sumMetrics(transportMetrics);
        assertEquals(distance * 4, transportMetrics.getDistance(), 0.001, "The distance should have been summed up again");
        assertEquals(duration * 4, transportMetrics.getDuration(), 0.001, "The duration should have been summed up again");
        assertEquals(cost * 4, transportMetrics.getCost(), 0.001, "The cost should have been summed up again");
    }

    @Test
    void sumTransportMetrics_throwsIllegalArgumentException_whenNullIsProvided() {
        TransportMetrics transportMetrics = new TransportMetrics(4.0, 3.0, 2.0);

        assertThrows(IllegalArgumentException.class, () -> {
            transportMetrics.sumMetrics(null);
        }, "A null transport metric cannot be used in a sum");

        assertDoesNotThrow(() -> {
            transportMetrics.sumMetrics(transportMetrics);
        }, "A valid transport metric can be used in a sum");
    }

    @Test
    void getCostByType_shouldReturnTheCorrectCost() {
        assertEquals(2.5, transportMetrics.getCostByType(CostType.DISTANCE), 0.001, "The distance should have been 2.5");
        assertEquals(6.0, transportMetrics.getCostByType(CostType.DURATION), 0.001, "The duration should have been 6.0");
        assertEquals(0.53, transportMetrics.getCostByType(CostType.COST), 0.001, "The cost should have been 0.53");

        assertEquals(transportMetrics.getDistance(), transportMetrics.getCostByType(CostType.DISTANCE), 0.001);
        assertEquals(transportMetrics.getDuration(), transportMetrics.getCostByType(CostType.DURATION), 0.001);
        assertEquals(transportMetrics.getCost(), transportMetrics.getCostByType(CostType.COST), 0.001);
    }

    @Test
    void getCheapestCost_shouldReturnTheCheapestCost() {
        assertEquals(0.53, transportMetrics.getCheapestCost(), 0.001, "The cheapest cost should have been 2.5");
        assertNotEquals(6.0, transportMetrics.getCheapestCost(), 0.001, "The cheapest cost should not have been 6.0");
    }

    @Test
    void getEmptyTransportMetrics_ShouldHaveReturnedAnEmptyTransportMetricsObject() {
        TransportMetrics transportMetrics = new TransportMetrics(0.0, 0.0, 0.0);

        assertEquals(0, transportMetrics.getDuration(), 0.001, "The duration should have been 0");
        assertEquals(0, transportMetrics.getDistance(), 0.001, "The distance should have been 0");
        assertEquals(0, transportMetrics.getCost(), 0.001, "The cost should have been 0");
    }
}