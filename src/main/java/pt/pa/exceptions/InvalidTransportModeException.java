/******************************************************************************************************************************
 * File Name: Stop.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.exceptions;

/**
 * Exception thrown when a transport mode is invalid or does not exist in the specified route.
 */
public class InvalidTransportModeException extends IllegalArgumentException {
    /**
     * Constructs an InvalidTransportModeException with the specified detail message.
     *
     * @param message The detail message (which is saved for later retrieval by the {@link #getMessage()} method).
     */
    public InvalidTransportModeException(String message) {
        super(message);
    }

    /**
     * Constructs an InvalidTransportModeException with the specified detail message and cause.
     *
     * @param message The detail message.
     * @param cause The cause (which is saved for later retrieval by the {@link #getCause()} method).
     */
    public InvalidTransportModeException(String message, Throwable cause) {
        super(message, cause);
    }
}