/******************************************************************************************************************************
 * File Name: DelayedUIRunner.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.ui;

import javafx.animation.PauseTransition;
import javafx.util.Duration;

import java.util.Collection;

/**
 * Represents a class with static helper methods to run runnables with some kind of delay.
 */
public class DelayedUIRunner {
    /**
     * Runs a given runnable with some delay.
     *
     * @param runnable The runnable to execute later.
     * @param duration The {@code Duration} instance for each runnable.
     */
    public static void runWithDelay(Runnable runnable, Duration duration) {
        if (runnable == null) return;

        PauseTransition delay = new PauseTransition(duration);
        delay.setOnFinished(event -> runnable.run());
        delay.play();
    }

    /**
     * Executes a collection of tasks sequentially, each with a specified delay.
     *
     * @param runnableCollection A collection of tasks to execute with delays. If null, no action is performed.
     * @param duration           The delay duration between each task.
     */
    public static void runCollectionWithDelay(Collection<Runnable> runnableCollection, Duration duration) {
        double milliseconds = duration.toMillis();
        double currentMilliseconds = milliseconds;

        for (Runnable runnable : runnableCollection) {
            runWithDelay(runnable, Duration.millis(currentMilliseconds));
            currentMilliseconds += milliseconds;
        }
    }
}
