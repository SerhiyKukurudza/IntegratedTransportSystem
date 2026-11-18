/******************************************************************************************************************************
 * File Name: Transport.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

/**
 * Represents transport with a name, enabled status, and transport metrics.
 */
public class Transport {
    /**
     * Represents the name of the transport.
     */
    private final String name;

    /**
     * Represents the current state of the transport.
     * Whether it should be taken into consideration during route planning or not.
     */
    private boolean enabled;

    /**
     * Represents the transport metrics of the current transport.
     */
    private final TransportMetrics transportMetrics;

    /**
     * Constructs a Transport instance.
     *
     * @param name             The name of the transport.
     * @param transportMetrics The metrics associated with the transport.
     */
    public Transport(String name, TransportMetrics transportMetrics) {
        validateTransportParameters(name, transportMetrics);

        this.name = name;
        this.enabled = true;
        this.transportMetrics = transportMetrics;
    }

    /**
     * Validates a new transport parameters.
     *
     * @param name             The name of the transport.
     * @param transportMetrics The metrics of that same transport.
     * @throws IllegalStateException If:
     *                               <ul>
     *                                   <li>The transport name is null or blank.</li>
     *                                   <li>The transport metrics are null.</li>
     *                               </ul>
     */
    private static void validateTransportParameters(String name, TransportMetrics transportMetrics) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Transport name cannot be null or blank");
        }

        if (transportMetrics == null) {
            throw new IllegalArgumentException("Transport metrics cannot be null");
        }
    }

    /**
     * Gets the name of the transport.
     *
     * @return The transport name.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the transport is enabled.
     *
     * @return True if the transport is enabled; otherwise, false.
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Sets the enabled status of the transport.
     *
     * @param enabled The new enabled status.
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the transport metrics.
     *
     * @return The transport metrics.
     */
    public TransportMetrics getTransportMetrics() {
        return transportMetrics;
    }
}