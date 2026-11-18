/******************************************************************************************************************************
 * File Name: Command.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command;

/**
 * Represents the interface for a Command in the Command Pattern.
 * In this implementation, it is actually an abstract class that will always have a description.
 */
public abstract class UserActionCommand {
    /**
     * Represents the description of a command executed by the user.
     */
    private final String description;

    /**
     * Represents the details of a command executed by the user.
     */
    private final String details;

    /**
     * Constructs an instance of a Command with a given description.
     *
     * @param description The description of what the command does.
     * @param details     The details of what the command did in that instance.
     */
    public UserActionCommand(String description, String details) {
        this.description = description;
        this.details = details;
    }

    /**
     * Gets the description of a command.
     *
     * @return The description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Gets the details of a command.
     *
     * @return The details.
     */
    public String getDetails() {
        return details;
    }

    /**
     * Executes the Concrete Command.
     */
    public abstract void execute();
}
