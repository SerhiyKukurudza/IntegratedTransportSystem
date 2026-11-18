/******************************************************************************************************************************
 * File Name: SelectTransportCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.calculateRoute;

import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that represents the selection of a transport mode.
 */
public class SelectTransportCommand extends UserActionCommand {
    /**
     * Constructs an instance of the SelectOriginStopCommand.
     *
     * @param transportName The cost type that the user selected.
     * @param selected Whether the transport mode was selected or not.
     */
    public SelectTransportCommand(String transportName, boolean selected) {
        super(selected
                ? getStringResource("userActionDescription.calculateRoute.selectTransport")
                : getStringResource("userActionDescription.calculateRoute.deselectTransport"),
                transportName
        );
    }

    @Override
    public void execute() {}
}
