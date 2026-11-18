/******************************************************************************************************************************
 * File Name: CostType.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the types of cost a Transport Metric can have.
 */
public enum CostType {
    /**
     * The distance in kilometers of a transport metric.
     */
    DISTANCE(getStringResource("costType.distance")),

    /**
     * The duration in minutes of a transport metric.
     */
    DURATION(getStringResource("costType.duration")),

    /**
     * The environmental cost of a transport metric.
     */
    COST(getStringResource("costType.cost"));

    /**
     * The name of the cost type.
     */
    private final String name;

    /**
     * Constructs a CostType enum with the specified name.
     *
     * @param name The name representing the cost type.
     */
    CostType(String name) {
        this.name = name;
    }

    /**
     * Retrieves the name of the cost type.
     *
     * @return The name of the cost type.
     */
    public String getName() {
        return name;
    }
}