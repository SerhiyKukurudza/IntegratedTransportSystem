/******************************************************************************************************************************
 * File Name: updateTransportEnableStatus.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.deactivateRoute;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.model.*;
import pt.pa.patterns.command.UserActionUndoableCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that updates the enabled status of transport.
 * As it implements the {@code UserActionUndoableCommand} it also implements an undo method.
 */
public class UpdateTransportEnabledStatusCommand extends UserActionUndoableCommand {
    /**
     * Represents the transport map where the command will be executed.
     */
    private final TransportMap transportMap;

    /**
     * Represents the edge route that will be updated.
     */
    private final Edge<Route, Stop> edgeRoute;

    /**
     * Represents the transport object that will be updated.
     */
    private final String transportName;

    /**
     * Represents the new enabled status of the transport in the specified route.
     */
    private final boolean enabled;

    /**
     * Constructs the command to update the enabled status of a transport mode in a route.
     *
     * @param transportMap  The transport map where the update will be performed.
     * @param edgeRoute     The route (edge) where the transport mode is located.
     * @param transportName The name of the transport mode to update.
     * @param enabled       The new enabled status for the transport mode.
     */
    public UpdateTransportEnabledStatusCommand(TransportMap transportMap, Edge<Route, Stop> edgeRoute, String transportName, boolean enabled) {
        super(
                getStringResource("userActionDescription.updateTransportEnableStatus.execute"),
                getStringResource("userActionDescription.updateTransportEnableStatus.undo"),
                edgeRoute.vertices()[0].element() + " - " + edgeRoute.vertices()[1].element() + " " +
                        getStringResource("userActionDescription.updateTransportEnableStatus.details") + " " +
                        getStringResource(enabled ? "userActionDescription.updateTransportEnableStatus.status.enabled" : "userActionDescription.updateTransportEnableStatus.status.disabled"), ""
        );

        this.transportMap = transportMap;
        this.edgeRoute = edgeRoute;
        this.transportName = transportName;
        this.enabled = enabled;
    }

    /**
     * Executes the command to update the enabled status for the specified transport.
     */
    @Override
    public void execute() {
        transportMap.setRouteTransportModeEnabled(edgeRoute, transportName, enabled);
    }

    /**
     * Undoes the command by reverting the enabled status to its original value.
     */
    @Override
    public void undo() {
        transportMap.setRouteTransportModeEnabled(edgeRoute, transportName, !enabled);
    }
}