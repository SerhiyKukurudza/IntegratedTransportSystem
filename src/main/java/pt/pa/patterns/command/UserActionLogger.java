/******************************************************************************************************************************
 * File Name: UserActionLogger.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command;

import org.javatuples.Triplet;
import pt.pa.patterns.command.concrete.deactivateRoute.UpdateTransportEnabledStatusCommand;
import pt.pa.patterns.command.concrete.designRoute.DesignRouteCommand;
import pt.pa.patterns.command.concrete.updateBicycle.UpdateBicycleDurationCommand;
import pt.pa.patterns.observer.Subject;
import pt.pa.utils.FileWriterUtil;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the user action logger Singleton that will store all the user actions and write it on a file.
 * Also represents the Invoker in the Command pattern.
 */
public class UserActionLogger extends Subject {
    /**
     * Represents the possible action execution states a command entry can have.
     */
    public enum ActionExecutionState {
        /**
         * Represents that the command entry was executed.
         */
        EXECUTED,
        /**
         * Represents that the command entry was undone.
         */
        UNDONE;
    }

    /**
     * Singleton instance of the UserActionLogger.
     */
    private static UserActionLogger instance;

    /**
     * Represents the file writer util instance that will write the user action logs.
     */
    private static FileWriterUtil fileWriterUtil;

    /**
     * Represents the path where the user actions will be written.
     */
    private static final String LOGGER_FILE_PATH = "userActionLogs.txt";

    /**
     * The headers the user action logger file will have.
     */
    private static final String[] FILE_HEADER = new String[]{
            getStringResource("userActionLoggerHeader.first"),
            getStringResource("userActionLoggerHeader.second"),
            getStringResource("userActionLoggerHeader.third")
    };

    /**
     * The width each header of the user action logger file will have.
     */
    private static final int[] FILE_HEADER_WIDTHS = new int[]{22, 80, 40};

    /**
     * Represents all the actions the user has performed and the execution time.
     */
    private final List<Triplet<UserActionCommand, LocalDateTime, ActionExecutionState>> userActions;

    /**
     * Represents the bicycle updated performed by the user.
     */
    private final Stack<UpdateBicycleDurationCommand> bicycleUpdates;

    /**
     * Represents the transport enabled status updates performed by the user.
     */
    private final Stack<UpdateTransportEnabledStatusCommand> transportEnabledStatusUpdates;

    /**
     * Represents the route designs performed by the user.
     */
    private final Stack<DesignRouteCommand> routeDesigns;

    /**
     * Private constructor to prevent instantiation from outside.
     * Initializes the user actions collection and the file writer.
     *
     * @throws IOException If an I/O exception occurs.
     */
    private UserActionLogger() throws IOException {
        fileWriterUtil = new FileWriterUtil(LOGGER_FILE_PATH, FILE_HEADER_WIDTHS, FILE_HEADER);

        userActions = new ArrayList<>();
        bicycleUpdates = new Stack<>();
        transportEnabledStatusUpdates = new Stack<>();
        routeDesigns = new Stack<>();
    }

    /**
     * Provides access to the singleton instance of UserActionLogger.
     *
     * @return The single instance of UserActionLogger.
     */
    public static UserActionLogger getInstance() {
        try {
            if (instance == null) {
                instance = new UserActionLogger();
            }
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        return instance;
    }

    /**
     * Performs a user action by executing a command and storing it in the user actions list.
     *
     * @param command The command to execute and store.
     * @throws RuntimeException If one of the command throws an exception.
     */
    public void performUserAction(UserActionCommand command) {
        command.execute();

        Triplet<UserActionCommand, LocalDateTime, ActionExecutionState> executedCommand =
                new Triplet<>(command, LocalDateTime.now(), ActionExecutionState.EXECUTED);

        userActions.add(executedCommand);
        addUndoableActionToHistory(command);
        writeNewCommand(command, executedCommand.getValue1());
        notifyObservers(executedCommand);
    }

    /**
     * Checks if the executed user action is undoable, and if so, adds it to the respective history.
     *
     * @param userActionCommand The executed user action.
     */
    private void addUndoableActionToHistory(UserActionCommand userActionCommand) {
        if (userActionCommand instanceof UpdateBicycleDurationCommand updateBicycleDurationCommand) {
            bicycleUpdates.push(updateBicycleDurationCommand);
        } else if (userActionCommand instanceof UpdateTransportEnabledStatusCommand updateTransportEnableStatusCommand) {
            transportEnabledStatusUpdates.push(updateTransportEnableStatusCommand);
        } else if (userActionCommand instanceof DesignRouteCommand designRouteCommand) {
            routeDesigns.push(designRouteCommand);
        }
    }

    /**
     * Undoes the last update performed at the route designs.
     *
     * @throws IllegalStateException If there are no route designs at the {@code routeDesigns} stack.
     */
    public void undoRouteUpdate() {
        if (routeDesigns.isEmpty()) {
            throw new IllegalStateException(getStringResource("exceptions.routeDesignsEmpty"));
        }

        DesignRouteCommand lastUpdate = routeDesigns.pop();
        undoAction(lastUpdate);
    }

    /**
     * Undoes the last update performed at the bicycle durations.
     *
     * @throws IllegalStateException If there are no bicycle updates at the {@code bicycleUpdates} stack.
     */
    public void undoBicycleUpdate() {
        if (bicycleUpdates.isEmpty()) {
            throw new IllegalStateException(getStringResource("exceptions.bicycleUpdatesEmpty"));
        }

        UpdateBicycleDurationCommand lastUpdate = bicycleUpdates.pop();
        undoAction(lastUpdate);
    }

    /**
     * Undoes the last update performed at the bicycle durations.
     *
     * @throws IllegalStateException If there are no bicycle updates at the {@code bicycleUpdates} stack.
     */
    public void undoTransportEnabledStatusUpdate() {
        if (transportEnabledStatusUpdates.isEmpty()) {
            throw new IllegalStateException(getStringResource("exceptions.transportEnabledStatusUpdatesEmpty"));
        }

        UpdateTransportEnabledStatusCommand lastUpdate = transportEnabledStatusUpdates.pop();
        undoAction(lastUpdate);
    }

    /**
     * Undoes an undoable action from its history stack.
     *
     * @param lastUpdate The popped last update.
     */
    private void undoAction(UserActionUndoableCommand lastUpdate) {
        lastUpdate.undo();

        Triplet<UserActionCommand, LocalDateTime, ActionExecutionState> undoneCommand =
                new Triplet<>(lastUpdate, LocalDateTime.now(), ActionExecutionState.UNDONE);

        userActions.add(undoneCommand);
        writeUndidCommand((UserActionUndoableCommand) undoneCommand.getValue0(), undoneCommand.getValue1());
    }

    /**
     * Writes a new user action command at the user action logger file.
     *
     * @param executedCommand The command that was executed.
     * @param executedAt      The moment that the command was executed.
     */
    private void writeNewCommand(UserActionCommand executedCommand, LocalDateTime executedAt) {
        String localDate = FileWriterUtil.FormatDate(executedAt);
        String description = executedCommand.getDescription();
        String details = executedCommand.getDetails();

        appendLine(localDate, description, details);
    }

    /**
     * Writes a user action command that was undone at the user action logger file.
     *
     * @param undoneCommand The command that was undone.
     * @param undoneAt      The moment that the command was undone.
     */
    private void writeUndidCommand(UserActionUndoableCommand undoneCommand, LocalDateTime undoneAt) {
        String localDate = FileWriterUtil.FormatDate(undoneAt);
        String description = undoneCommand.getUndoDescription();
        String details = undoneCommand.getUndoDetails();

        appendLine(localDate, description, details);
    }

    /**
     * Appends a new line of different Strings in the user action logs file.
     *
     * @param line The line of the different Strings.
     */
    private void appendLine(String... line) {
        try {
            fileWriterUtil.appendLine(line);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Converts the history of user actions to a string.
     *
     * @return A string representing all the user actions.
     */
    @Override
    public String toString() {
        return userActions.toString();
    }
}
