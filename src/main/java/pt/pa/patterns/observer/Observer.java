/******************************************************************************************************************************
 * File Name: Observer.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.observer;

/**
 * Represents the interface of an Observer.
 * The Observer will update itself when the Observable notifies it.
 */
public interface Observer {
    /**
     * Updates the Observer implemented instance based on the new version of its model.
     *
     * @param updatedModel - The updated model.
     */
    void update(Object updatedModel);
}
