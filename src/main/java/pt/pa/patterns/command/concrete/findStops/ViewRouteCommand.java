/******************************************************************************************************************************
 * File Name: ViewRouteCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.findStops;

import com.brunomnsilva.smartgraph.graph.Edge;
import pt.pa.controller.BaseController;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;


/**
 * Represents a concrete command for tracking the action of viewing route details.
 */
public class ViewRouteCommand extends UserActionCommand {
    /**
     * Constructs an instance of the ViewRouteCommand.
     *
     * @param routeEdge      The route edge that the user visualized.
     */
    public ViewRouteCommand( Edge<Route, Stop> routeEdge) {
        super(
                getStringResource("userActionDescription.viewRoute.viewRoute"),
                routeEdge.element().toString()
        );
    }

    /**
     * Executes the command.
     * Used for history registration.
     */
    @Override
    public void execute() {}
}