/******************************************************************************************************************************
 * File Name: LogsBottomController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureBottom;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import org.javatuples.Triplet;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionCommand;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.utils.StringManipulation;

import java.time.LocalDateTime;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;
import static pt.pa.ui.DisplayHelper.toggleNodesVisibility;

/**
 * Represents the controller class for the LogsBottom view.
 */
public class LogsBottomController extends TransportBaseController implements FeatureBottomController {

    /**
     * Represents the background pane of the logs bottom tab.
     */
    @FXML
    private Pane bottomLogsPane;

    /**
     * Represents the tips version of the bottom menu.
     */
    @FXML
    private VBox vBoxTips;

    /**
     * Represents the dynamic version of the bottom menu.
     */
    @FXML
    private VBox vBoxLogs;

    /**
     * Represents the label containing the date of the user action.
     */
    @FXML
    private Label labelUserActionDate;

    /**
     * Represents the label containing the command description.
     */
    @FXML
    private Label labelUserActionCommand;

    /**
     * Represents the label containing the command details.
     */
    @FXML
    private Label labelUserActionCommandDetails;

    /**
     * Constructs a LogsBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public LogsBottomController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the logs bottom tab view.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
        setNodeVisibility(vBoxLogs, false);
    }

    /**
     * Displays the details of a user action in the UI.
     *
     * <p>This method populates the labels with information about the provided user action.
     * It sets the action's execution date, description, and any additional details, formatting them for display.</p>
     *
     * @param userAction A {@link Triplet} containing:
     *                   <ul>
     *                     <li>A {@link UserActionCommand} representing the executed user action.</li>
     *                     <li>A {@link LocalDateTime} indicating when the action was executed.</li>
     *                     <li>A {@link UserActionLogger.ActionExecutionState} indicating the execution state of the action.</li>
     *                   </ul>
     */
    public void showCommandDetails(Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState> userAction) {
        UserActionCommand command = userAction.getValue0();
        LocalDateTime localDateTime = userAction.getValue1();

        updateCommandLabel(labelUserActionDate, StringManipulation.formatDateTime(localDateTime));
        updateCommandLabel(labelUserActionCommand, command.getDescription());

        if (command.getDetails() != null) {
            updateCommandLabel(labelUserActionCommandDetails, command.getDetails());
        }

        toggleNodesVisibility(vBoxLogs, vBoxTips);
    }

    /**
     * Updates a command information label with a new text and style.
     *
     * @param commandLabel The label what will be updated.
     * @param command      The new label information.
     */
    private void updateCommandLabel(Label commandLabel, String command) {
        commandLabel.setText(command);
        commandLabel.getStyleClass().add("bottom-text");
    }
}
