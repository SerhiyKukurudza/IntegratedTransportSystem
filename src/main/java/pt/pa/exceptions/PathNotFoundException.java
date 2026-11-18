/******************************************************************************************************************************
 * File Name: PathNotFoundException.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.exceptions;

/**
 * Thrown when a path finding operation was performed, yet, no valid path was found.
 */
public class PathNotFoundException extends RuntimeException {
    /**
     * Creates an instance of the PathNotFoundException with a message.
     *
     * @param message The message to be shown.
     */
    public PathNotFoundException(String message) {
        super(message);
    }

    /**
     * Creates a non parameterized instance of PathNotFoundException.
     */
    public PathNotFoundException() {
        this("A path with the specified parameters could not be found");
    }
}
