/******************************************************************************************************************************
 * File Name: SelectPreferredCostCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.designRoute;

import pt.pa.model.CostType;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that represents the selection action of a cost.
 */
public class SelectPreferredCostCommand extends UserActionCommand {
    /**
     * Constructs an instance of the SelectOriginStopCommand.
     *
     * @param costType The cost type that the user selected.
     */
    public SelectPreferredCostCommand(CostType costType) {
        super(getStringResource("userActionDescription.designRoute.selectPreferredCost"), costType.getName());
    }

    /**
     * Executes the command.
     * Used for history registration.
     */
    @Override
    public void execute() {}
}
