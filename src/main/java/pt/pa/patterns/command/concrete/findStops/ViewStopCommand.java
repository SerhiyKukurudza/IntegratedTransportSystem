/******************************************************************************************************************************
 * File Name: ViewStopCommand.java
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
 * Represents a concrete command of the command pattern that tracks the action of viewing a stop's details.
 */
public class ViewStopCommand extends UserActionCommand {
    /**
     * Constructs an instance of the ViewStopCommand.
     *
     * @param stopVertex     The stop vertex that the user selected to view.
     */
    public ViewStopCommand(Vertex<Stop> stopVertex) {
        super(
                getStringResource("userActionDescription.viewStop.viewStop"),
                stopVertex.element().getName()
        );
    }

    /**
     * Executes the command.
     * Used for history registration.
     */
    @Override
    public void execute() {}
}