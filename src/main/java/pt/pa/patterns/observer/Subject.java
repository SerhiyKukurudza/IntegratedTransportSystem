/******************************************************************************************************************************
 * File Name: Subject.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.observer;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents an abstract class of a Subject in the Observer Pattern.
 * It includes a list of Observers and the implementation of the Observable Interface.
 */
public abstract class Subject implements Observable {
    /**
     * Represents the list of all the current subject instance observers.
     */
    private final Set<Observer> observers;

    /**
     * Constructs a Subject instance.
     */
    public Subject() {
        this.observers = new HashSet<>();
    }

    /**
     * One or more Observers that are not yet in the observerList.
     *
     * @param observers That will be attached.
     */
    @Override
    public void addObservers(Observer... observers) {
        this.observers.addAll(Set.of(observers));
    }

    /**
     * Removes one Observer from the list.
     *
     * @param observer That will be removed.
     */
    @Override
    public void removeObservers(Observer observer) {
        this.observers.remove(observer);
    }

    /**
     * Notifies all Observers in the list.
     *
     * @param obj Arguments of update method.
     */
    @Override
    public void notifyObservers(Object obj) {
        for (Observer observer : this.observers)
            observer.update(obj);
    }
}

