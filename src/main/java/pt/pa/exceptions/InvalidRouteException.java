/******************************************************************************************************************************
 * File Name: Stop.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.exceptions;

/**
 * Exception thrown when an invalid route is encountered.
 */
public class InvalidRouteException extends IllegalArgumentException {
  /**
   * Constructs a new InvalidRouteException with the specified detail message.
   *
   * @param message The detail message.
   */
  public InvalidRouteException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidRouteException with the specified detail message and cause.
   *
   * @param message The detail message.
   * @param cause The cause of the exception.
   */
  public InvalidRouteException(String message, Throwable cause) {
    super(message, cause);
  }
}