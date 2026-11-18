/******************************************************************************************************************************
 * File Name: Stop.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.exceptions;

/**
 * Exception thrown when an operation involves an invalid stop.
 */
public class InvalidStopException extends RuntimeException {

  /**
   * Constructs an InvalidStopException with a specified message.
   *
   * @param message The detail message.
   */
  public InvalidStopException(String message) {
    super(message);
  }

  /**
   * Constructs an InvalidStopException with a specified message and cause.
   *
   * @param message The detail message.
   * @param cause   The cause of the exception.
   */
  public InvalidStopException(String message, Throwable cause) {
    super(message, cause);
  }
}