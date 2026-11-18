/******************************************************************************************************************************
 * File Name: SelectOriginStopCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.findStops;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.controller.BaseController;
import pt.pa.model.Stop;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that selects the stop origin at the find stops feature tab.
 */
public class SelectOriginStopCommand extends UserActionCommand {
    /**
     * Represents the base controller at which the user action will be performed.
     */
    private final BaseController baseController;

    /**
     * Represents the origin stop vertex that the user has selected.
     */
    private final Vertex<Stop> originStopVertex;

    /**
     * Constructs an instance of the SelectOriginStopCommand.
     *
     * @param baseController   The controller where the action will be performed.
     * @param originStopVertex The origin stop vertex that the user selected.
     */
    public SelectOriginStopCommand(BaseController baseController, Vertex<Stop> originStopVertex) {
        super(getStringResource("userActionDescription.findStops.selectOrigin"), originStopVertex.element().getName());

        this.baseController = baseController;
        this.originStopVertex = originStopVertex;
    }

    /**
     * Executes the command by notifying the controller about the selected origin stop.
     */
    @Override
    public void execute() {
        baseController.notifySelectedOrigin(originStopVertex, true);
    }
}
