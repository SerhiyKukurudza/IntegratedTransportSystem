/******************************************************************************************************************************
 * File Name: UserActionUndoableCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command;

/**
 * Represents the interface of a Command in the Command Pattern that can be undone.
 */
public abstract class UserActionUndoableCommand extends UserActionCommand {
    private final String undoDescription;
    private final String undoDetails;

    /**
     * Constructs an instance of an UndoableCommand with a given description.
     *
     * @param description     The description of what the command does.
     * @param details         The details of what the command did in that instance.
     * @param undoDescription The description of what the command does when undone.
     * @param undoDetails     The details of what the command undid in that instance.
     */
    public UserActionUndoableCommand(String description, String details, String undoDescription, String undoDetails) {
        super(description, details);
        this.undoDescription = undoDescription;
        this.undoDetails = undoDetails;
    }

    /**
     * Gets the description of the undo of the command.
     *
     * @return The description.
     */
    public String getUndoDescription() {
        return undoDescription;
    }

    /**
     * Gets the details of the undo of the command.
     *
     * @return The details.
     */
    public String getUndoDetails() {
        return undoDetails;
    }

    /**
     * Executes the inverse or undo action of a Concrete Command.
     */
    public abstract void undo();
}
