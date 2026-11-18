/******************************************************************************************************************************
 * File Name: LogsTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import org.javatuples.Triplet;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionCommand;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.observer.Observer;

import java.time.LocalDateTime;

/**
 * Represents the controller class for the LogsTab view.
 */
public class LogsTabController extends TransportBaseController implements FeatureTabController, Observer {

    /**
     * Represents the background pane of the logs tab.
     */
    @FXML
    private Pane tabLogsPane;

    /**
     * Represents the list of commands executed by the user.
     */
    @FXML
    private ListView<Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState>> listViewCommandList;

    /**
     * Constructs a LogsTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public LogsTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the logs tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);

        initializeListView();
        UserActionLogger.getInstance().addObservers(this);
    }

    /**
     * Initializes the ListView that displays the user action commands.
     * This method sets up how each command is represented in the ListView.
     */
    private void initializeListView() {
        setListViewConverter();

        setListViewTriggers();
    }

    /**
     * Sets the list view converter so that it shows the correct information.
     */
    private void setListViewConverter() {
        listViewCommandList.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState> item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String commandDescription = item.getValue0().getDescription();

                    setText(String.format(commandDescription));
                }
            }
        });
    }

    /**
     * Sets the list view triggers so that it notifies a selected command
     */
    private void setListViewTriggers() {
        listViewCommandList.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                baseController.notifySelectedCommand(newSelection);
            }
        });
    }

    /**
     * Adds a new user action to the command list in the ListView.
     * This method inserts the new action at the top of the list.
     *
     * @param userAction The user action to be added to the log, represented as a Triplet containing
     *                   the command, its execution time, and its execution state.
     */
    private void addNewUserActionToCommandList(Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState> userAction) {
        listViewCommandList.getItems().add(0, userAction);
    }

    /**
     * Updates the view every time the subject notifies its observers.
     * This method handles updates for user actions.
     *
     * @param object The updated object, preferably a user action.
     */
    @Override
    public void update(Object object) {
        if (object instanceof Triplet<?, ?, ?> userAction) {
            addNewUserActionToCommandList((Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState>) userAction);
        }
    }
}
