/******************************************************************************************************************************
 * File Name: TransportMetrics.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents the transport metrics of a transport mode.
 * Each transport metric includes a distance, duration and cost.
 */
public class TransportMetrics {
    /**
     * Represents the distance of the transport mode.
     */
    private Double distance;
    /**
     * Represents the duration of the transport mode.
     */
    private Double duration;
    /**
     * Represents the environmental cost of the transport mode.
     */
    private Double cost;

    /**
     * Constructs a Transport instance.
     *
     * @param distance The distance covered by this transport.
     * @param duration The duration of the journey using this transport.
     * @param cost     The cost associated with using this transport.
     * @throws IllegalArgumentException If the transportation mode is null or empty.
     * @throws IllegalArgumentException If distance, duration or cost is null.
     * @throws IllegalArgumentException If distance of duration is negative.
     */
    public TransportMetrics(Double distance, Double duration, Double cost) {
        validateDistance(distance);
        validateDuration(duration);
        validateCost(cost);

        this.distance = distance;
        this.duration = duration;
        this.cost = cost;
    }

    /**
     * Constructs a clone of the provided transport metrics instance.
     *
     * @param transportMetrics The transport metrics to clone.
     * @throws IllegalArgumentException If the transport metrics to clone is null.
     */
    public TransportMetrics(TransportMetrics transportMetrics) {
        if (transportMetrics == null) {
            throw new IllegalArgumentException("The transport metrics cannot be null.");
        }

        this.distance = transportMetrics.distance;
        this.duration = transportMetrics.duration;
        this.cost = transportMetrics.cost;
    }

    /**
     * Gets the distance covered by the transport.
     *
     * @return The distance in relevant units (e.g., kilometers or miles).
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * Gets the duration of the journey using the transport.
     *
     * @return The duration in relevant units (e.g., hours or minutes).
     */
    public Double getDuration() {
        return duration;
    }

    /**
     * Gets the cost of using the transport.
     *
     * @return The cost associated with this transport.
     */
    public Double getCost() {
        return cost;
    }

    /**
     * Sets the distance metric of the transport.
     *
     * @param distance The distance.
     * @throws IllegalArgumentException If the distance is not valid.
     */
    public void setDistance(Double distance) {
        validateDistance(distance);
        this.distance = distance;
    }

    /**
     * Sets the duration metric of the transport.
     *
     * @param duration The duration.
     * @throws IllegalArgumentException If the duration is not valid.
     */
    public void setDuration(Double duration) {
        validateDuration(duration);
        this.duration = duration;
    }

    /**
     * Sets the cost metric of the transport.
     *
     * @param cost The cost.
     * @throws IllegalArgumentException If the cost is not valid.
     */
    public void setCost(Double cost) {
        validateCost(cost);
        this.cost = cost;
    }

    /**
     * Gets the corresponding transport metric attribute according to the cost type enum provided.
     *
     * @param costType The desired type of cost to be.
     * @return The corresponding attribute of the transport metrics.
     */
    public double getCostByType(CostType costType) {
        switch (costType) {
            case DISTANCE -> {
                return this.distance;
            }
            case DURATION -> {
                return this.duration;
            }
            case COST -> {
                return this.cost;
            }
            default -> {
                return 0.0;
            }
        }
    }

    /**
     * Retrieves the cheapest cost among the distance, duration, and cost attributes.
     * This method compares the values of distance, duration, and cost, sorts them in ascending
     * order, and returns the smallest value as the cheapest cost.
     * <p>
     * The costs are assumed to be stored as the `distance`, `duration`, and `cost` instance
     * variables of the object. The method is useful for determining the lowest cost option
     * based on these three criteria.
     * </p>
     *
     * @return The smallest of the three costs (distance, duration, and cost).
     */
    public double getCheapestCost() {
        List<Double> costs = new ArrayList<>();
        costs.add(this.distance);
        costs.add(this.duration);
        costs.add(this.cost);

        Collections.sort(costs);

        return costs.get(0);
    }

    /**
     * Sets the cost of a specific transport metric depending on the cost type.
     *
     * @param costType The cost type that will be updated.
     * @param newCost  The new value for the specified cost.
     * @throws IllegalArgumentException If the specified {@code newCost} is invalid.
     */
    public void setCostByType(CostType costType, Double newCost) {
        switch (costType) {
            case DISTANCE -> setDistance(newCost);
            case DURATION -> setDuration(newCost);
            case COST -> setCost(newCost);
        }
    }

    /**
     * Sums all transport metrics attributes of another transport metrics instance.
     *
     * @param transportMetrics The transport metrics to sum into the instance.
     * @throws IllegalArgumentException If the transport metrics to sum with is null.
     */
    public void sumMetrics(TransportMetrics transportMetrics) {
        if (transportMetrics == null) {
            throw new IllegalArgumentException("The transport metrics cannot be null.");
        }

        this.distance += transportMetrics.getDistance();
        this.duration += transportMetrics.getDuration();
        this.cost += transportMetrics.getCost();
    }

    /**
     * Validates the distance metric.
     *
     * @param distance The distance.
     * @throws IllegalArgumentException If the distance is null or smaller than zero.
     */
    private void validateDistance(Double distance) {
        if (distance == null) {
            throw new IllegalArgumentException("The distance metric is obligatory.");
        }

        if (distance < 0) {
            throw new IllegalArgumentException("Distance must be greater or equals to zero.");
        }
    }

    /**
     * Validates the duration metric.
     *
     * @param duration The duration.
     * @throws IllegalArgumentException If the duration is null or smaller than zero.
     */
    private void validateDuration(Double duration) {
        if (duration == null) {
            throw new IllegalArgumentException("The duration metric is obligatory.");
        }

        if (duration < 0) {
            throw new IllegalArgumentException("Duration must be greater or equals to zero.");
        }
    }

    /**
     * Validates the cost metric.
     *
     * @param cost The duration.
     * @throws IllegalArgumentException If the cost is null.
     */
    private void validateCost(Double cost) {
        if (cost == null) {
            throw new IllegalArgumentException("The cost metric os obligatory.");
        }
    }

    /**
     * Gets an empty transport metrics instance where all metrics are at 0.
     * Useful for statistical measures.
     *
     * @return A zero'd transport metrics instance.
     */
    public static TransportMetrics getEmptyTransportMetrics() {
        return new TransportMetrics(0.0, 0.0, 0.0);
    }
}