/******************************************************************************************************************************
 * File Name: UpdateBicycleDurationCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.updateBicycle;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.model.CostType;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionUndoableCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that updates the bicycle duration of a route.
 * As it implements the {@code UserActionUndoableCommand} it also implements an undo method.
 */
public class UpdateBicycleDurationCommand extends UserActionUndoableCommand {
    /**
     * Represents the transport map where the command will be executed.
     */
    private final TransportMap transportMap;

    /**
     * Represents the edge route that will be updated.
     */
    private final Edge<Route, Stop> edgeRoute;

    /**
     * Represents the new duration of the bicycle in the specified route.
     */
    private final double duration;

    /**
     * Represents the old duration of the bicycle in that route.
     * Used at the undo method.
     */
    private final double oldDuration;

    /**
     * Constructs an instance of the UpdateBicycleDurationCommand.
     *
     * @param transportMap The transport map where the route is.
     * @param edgeRoute    The edge route which bicycle duration is being updated.
     * @param duration     The new duration which the route will take to be traversed by bicycle.
     */
    public UpdateBicycleDurationCommand(TransportMap transportMap, Edge<Route, Stop> edgeRoute, double duration) {
        super(getStringResource("userActionDescription.updateBicycleDuration.update1"),
                edgeRoute.vertices()[0].element() + " - " + edgeRoute.vertices()[1].element() +
                        " " + getStringResource("userActionDescription.updateBicycleDuration.update2") + " "
                        + String.format("%.0f", duration) + " " +
                        getStringResource("userActionDescription.updateBicycleDuration.update3"),
                getStringResource("userActionDescription.updateBicycleDuration.undo1"),
                edgeRoute.vertices()[0].element() + " - " + edgeRoute.vertices()[1].element() +
                        " " + getStringResource("userActionDescription.updateBicycleDuration.undo2") + " "
                        + String.format("%.0f", duration) + " " +
                        getStringResource("userActionDescription.updateBicycleDuration.undo3")
        );

        this.transportMap = transportMap;
        this.edgeRoute = edgeRoute;
        this.duration = duration;

        this.oldDuration = edgeRoute.element().getBicycleDuration();
    }

    /**
     * Executes the command to update the bicycle duration for the specified route.
     */
    @Override
    public void execute() {
        transportMap.updateRouteMetric(edgeRoute.element(), getStringResource("transportName.bicycle"), CostType.DURATION, duration);
    }

    /**
     * Undoes the command by reverting the bicycle duration to its original value.
     */
    @Override
    public void undo() {
        transportMap.updateRouteMetric(edgeRoute.element(), getStringResource("transportName.bicycle"), CostType.DURATION, oldDuration);
    }
}
