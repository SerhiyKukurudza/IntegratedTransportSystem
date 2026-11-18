/******************************************************************************************************************************
 * File Name: NegativeEdgeWeightException.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.exceptions;

/**
 * Thrown when a negative weight cycle is detected during the Bellman-Ford algorithm.
 */
public class NegativeEdgeWeightException extends PathNotFoundException {
    /**
     * Creates an instance of the NegativeEdgeWeightException with a message.
     *
     * @param message The message to be shown.
     */
    public NegativeEdgeWeightException(String message) {
        super(message);
    }

    /**
     * Creates a non-parameterized instance of NegativeEdgeWeightException.
     * This will provide a default message indicating the presence of a negative weight cycle.
     */
    public NegativeEdgeWeightException() {
        this("A negative weight cycle was detected in the graph, preventing the calculation of the shortest paths.");
    }
}