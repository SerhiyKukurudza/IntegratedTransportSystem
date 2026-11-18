/******************************************************************************************************************************
 * File Name: Route.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import org.javatuples.Triplet;

import java.util.*;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a specific route with its transportation modes stored in a Map.
 */
public class Route {

    /**
     * Represents a list of transport modes available for the route.
     * A route must have at least one transportation mode.
     */
    private final TransportModesMap transportModesMap;

    /**
     * Constructs a Route instance with specified transportation options.
     *
     * @param transportModesMap The map of transport modes available on this route.
     * @throws IllegalArgumentException If the route has no transportation modes.
     */
    public Route(TransportModesMap transportModesMap) {
        if (transportModesMap == null || transportModesMap.isEmpty()) {
            throw new IllegalArgumentException(getStringResource("exceptions.minimumRouteTransports"));
        }

        this.transportModesMap = transportModesMap;
    }

    /**
     * Gets all available transport names in the route.
     *
     * @return A set with all transport names.
     */
    public Set<String> getTransportNames() {
        return transportModesMap.keySet();
    }

    /**
     * Gets all available transports in the route.
     *
     * @return A collection of all transports.
     */
    public Collection<Transport> getTransports() {
        return transportModesMap.values();
    }

    /**
     * Checks if a specified transport mode exists in a route.
     *
     * @param transportName The transport mode name.
     * @return {@code True} if the transport mode exists in the route, {@code false}
     * otherwise.
     */
    public boolean hasTransportMode(String transportName) {
        return transportModesMap.containsKey(transportName);
    }

    /**
     * Retrieves a map of transport modes that are currently enabled.
     *
     * @return A TransportModesMap containing only the enabled transport modes.
     */
    public TransportModesMap getEnabledTransportModesMap() {
        TransportModesMap enabledTransportModesMap = new TransportModesMap();

        transportModesMap.values().stream()
                .filter(Transport::isEnabled)
                .forEach(transport -> enabledTransportModesMap.put(transport.getName(), transport));

        return enabledTransportModesMap;
    }

    /**
     * Returns a TransportModesMap with the intersection of a set of transport names and the ones available in the route.
     *
     * @param usableTransports A set of transport names to intersect with the route's transports.
     * @return A TransportModesMap with all the intersected transports.
     */
    public TransportModesMap chosenTransports(Set<String> usableTransports) {
        Set<String> chosenTransportNames = new HashSet<>(getEnabledTransportModesMap().keySet());
        chosenTransportNames.retainAll(usableTransports);

        TransportModesMap chosenTransports = new TransportModesMap();

        chosenTransportNames.forEach(name -> {
            Transport transport = transportModesMap.get(name);
            if (transport != null) {
                chosenTransports.put(name, transport);
            }
        });

        return chosenTransports;
    }

    /**
     * Returns the cheapest transport available in the route according to the provided usable transports and cost type.
     *
     * @param usableTransports The transports to consider in the calculation.
     * @param costType         The cost type to prioritize during calculation.
     * @return A Triplet with the cheapest transport's name, its cost, and its metrics.
     */
    public Triplet<String, Double, TransportMetrics> getCheapestTransportWithCost(Set<String> usableTransports, CostType costType) {
        if (usableTransports == null || usableTransports.isEmpty()) {
            throw new IllegalArgumentException(getStringResource("exceptions.usableTransportsNull"));
        }

        if (costType == null) {
            throw new IllegalArgumentException(getStringResource("exceptions.costTypeNull"));
        }

        TransportModesMap chosenTransports = chosenTransports(usableTransports);
        Triplet<String, Double, TransportMetrics> cheapestTransportWithCost = new Triplet<>(null, Double.MAX_VALUE, null);

        for (Transport transport : chosenTransports.values()) {
            double typeCost = transport.getTransportMetrics().getCostByType(costType);

            if (typeCost < cheapestTransportWithCost.getValue1()) {
                cheapestTransportWithCost = new Triplet<>(transport.getName(), typeCost, transport.getTransportMetrics());
            }
        }

        return cheapestTransportWithCost;
    }

    /**
     * Gets the cheapest transport cost among all enabled transport modes.
     *
     * @return The lowest cost of all enabled transport modes.
     */
    public double getCheapestTransportCost() {
        double cheapestTransportCost = Double.MAX_VALUE;

        for (Transport transport : getEnabledTransportModesMap().values()) {
            double typeCost = transport.getTransportMetrics().getCheapestCost();

            if (typeCost < cheapestTransportCost) {
                cheapestTransportCost = typeCost;
            }
        }

        return cheapestTransportCost;
    }

    /**
     * Checks whether there are any enabled transport modes for this route.
     *
     * @return {@code true} if at least one transport mode is enabled, {@code false} otherwise.
     */
    public boolean hasEnabledTransports() {
        return getTransports().stream().anyMatch(Transport::isEnabled);
    }

    /**
     * Retrieves the transport metrics of a given transport mode by its name.
     *
     * @param transportName The transport mode whose metrics are required.
     * @return The transport metrics of the specified transport.
     * @throws IllegalArgumentException If the transport mode does not exist in the route.
     */
    public TransportMetrics getTransportMetricsByTransportName(String transportName) {
        if (transportName == null || transportName.isEmpty()) {
            throw new IllegalArgumentException(getStringResource("exceptions.transportNameNull"));
        }

        Transport transport = transportModesMap.get(transportName);

        if (transport == null) {
            throw new IllegalArgumentException(getStringResource("exceptions.transportDoesNotExist"));
        }

        return transport.getTransportMetrics();
    }

    /**
     * Sets the enabled status of a specific transport mode.
     *
     * @param transportName The name of the transport mode to update.
     * @param enabled       {@code true} to enable the transport mode, {@code false} to disable it.
     */
    public void setTransportModeEnabled(String transportName, boolean enabled) {
        Transport transport = transportModesMap.get(transportName);

        if (transport != null) {
            transport.setEnabled(enabled);
        }
    }

    /**
     * Gets the average distance of all transport modes in a route.
     *
     * @return The calculated average distance.
     */
    public double getAverageDistance() {
        if (transportModesMap.isEmpty()) {
            return 0;
        }

        double totalDistance = transportModesMap.values().stream()
                .mapToDouble(transport -> transport.getTransportMetrics().getDistance())
                .sum();

        return totalDistance / transportModesMap.size();
    }

    /**
     * Retrieves the duration associated with the bicycle transport mode.
     *
     * @return The duration of the bicycle transport mode in minutes.
     * @throws NullPointerException If the "bicycle" transport mode is not found in the map
     *                              or if the transport metrics associated with the bicycle
     *                              transport mode are null.
     */
    public double getBicycleDuration() {
        return transportModesMap.get("Bicycle").getTransportMetrics().getDuration();
    }

    /**
     * Returns an empty string representation of the object.
     *
     * @return An empty string.
     */
    @Override
    public String toString() {
        return "";
    }
}