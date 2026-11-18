/******************************************************************************************************************************
 * File Name: SelectDestinationStopCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.calculateRoute;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.controller.BaseController;
import pt.pa.model.Stop;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that selects the destination stop at the calculate route feature tab.
 */
public class SelectDestinationStopCommand extends UserActionCommand {
    /**
     * Represents the base controller at which the user action will be performed.
     */
    private final BaseController baseController;

    /**
     * Represents the destination stop vertex that the user has selected.
     */
    private final Vertex<Stop> destinationStopVertex;

    /**
     * Constructs an instance of the SelectDestinationStopCommand.
     *
     * @param baseController   The controller where the action will be performed.
     * @param destinationStopVertex The destination stop vertex that the user selected.
     */
    public SelectDestinationStopCommand(BaseController baseController, Vertex<Stop> destinationStopVertex) {
        super(getStringResource("userActionDescription.calculateRoute.selectDestination"), destinationStopVertex.element().getName());
        this.baseController = baseController;
        this.destinationStopVertex = destinationStopVertex;
    }

    /**
     * Executes the command to notify the base controller about the selected destination stop.
     */
    @Override
    public void execute() {
        baseController.notifySelectedDestination(destinationStopVertex);
    }
}
