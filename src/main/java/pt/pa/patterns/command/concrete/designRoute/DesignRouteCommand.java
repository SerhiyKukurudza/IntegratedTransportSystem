/******************************************************************************************************************************
 * File Name: DesignRouteCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.designRoute;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.model.*;
import pt.pa.patterns.command.UserActionUndoableCommand;

import java.util.List;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that designs a new stop in a route.
 * As it implements the {@code UserActionUndoableCommand} it also implements an undo method.
 */
public class DesignRouteCommand extends UserActionUndoableCommand {
    /**
     * Represents the base controller at which the user action will be performed.
     */
    private final BaseController baseController;

    /**
     * Represents the list of selected stop vertices.
     */
    private final List<Vertex<Stop>> selectedStops;

    /**
     * Represents the list of each route vertex and the preferred cost.
     */
    private final List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes;

    /**
     * Constructs an instance of the SelectOriginStopCommand.
     *
     * @param baseController The controller where the action will be performed.
     * @param selectedStops  The selected routes at the time of execution of the command.
     * @param selectedRoutes The selected routes and cost types at the time of execution of the command.
     */
    public DesignRouteCommand(BaseController baseController,
                              List<Vertex<Stop>> selectedStops,
                              List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        super(
                getStringResource("userActionDescription.designRoute.selectStop"),
                selectedStops.get(selectedStops.size() - 1).element().getName(),
                getStringResource("userActionDescription.designRoute.undoSelectStop"),
                getStringResource("userActionDescription.designRoute.undoStopDetails") +
                        selectedStops.get(selectedStops.size() - 1).element().getName()
        );

        this.baseController = baseController;
        this.selectedStops = selectedStops;
        this.selectedRoutes = selectedRoutes;
    }

    /**
     * Executes the command by notifying the base controller of the selected stops and routes for the designed path.
     */
    @Override
    public void execute() {
        baseController.notifyDesignedRoute(selectedStops, selectedRoutes);
    }

    /**
     * Undoes the last action of selecting a stop and its associated route.
     */
    @Override
    public void undo() {
        cleanExtraRoutes();

        selectedStops.remove(selectedStops.size() - 1);
        baseController.notifyDesignedRoute(selectedStops, selectedRoutes);
    }

    /**
     * Clears any extra routes that should not be painted.
     */
    private void cleanExtraRoutes(){
        if(selectedStops.size() > 1) {
            selectedRoutes.remove(selectedRoutes.size() - 1);
        }
    }
}
