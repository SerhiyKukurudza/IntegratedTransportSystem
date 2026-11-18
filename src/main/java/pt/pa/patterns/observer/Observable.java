/******************************************************************************************************************************
 * File Name: Observable.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.observer;

/**
 * Represents the interface all Observable classes will have to implement.
 * An Observable will be able to add, remove and notify all of its Observers.
 */
public interface Observable {
    /**
     * Attaches Observers to the subject.
     *
     * @param observers That will be attached.
     */
    void addObservers(Observer... observers);

    /**
     * Removes Observers from the subject.
     *
     * @param observer That will be removed.
     */
    void removeObservers(Observer observer);

    /**
     * Notifies all Observers.
     *
     * @param object Arguments of update method.
     */
    void notifyObservers(Object object);
}
