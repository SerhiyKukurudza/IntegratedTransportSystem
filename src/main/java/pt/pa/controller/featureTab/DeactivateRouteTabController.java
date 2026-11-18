/******************************************************************************************************************************
 * File Name: DeactivateRouteTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.controller.TransportMapFeature;
import pt.pa.model.*;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.deactivateRoute.UpdateTransportEnabledStatusCommand;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;

import java.util.Map;

import static pt.pa.ui.DisplayHelper.setNodeVisibility;
import static pt.pa.ui.DisplayHelper.toggleNodesVisibility;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the DeactivateRouteTab view.
 */
public class DeactivateRouteTabController extends TransportBaseController implements FeatureTabController, Observer, RouteDetailManager {
    /**
     * Represents the background pane of the deactivate route tab.
     */
    @FXML
    private Pane tabDeactivateRoutePane;

    /**
     * Represents the default, unselected route VBox.
     */
    @FXML
    private VBox vBoxRouteSelectionUnselected;

    /**
     * Represents the VBox that is shown upon valid route selection.
     */
    @FXML
    private VBox vBoxRouteSelectionSelected;

    /**
     * Represents the GridPane where the transport modes will be displayed.
     */
    @FXML
    private GridPane gridPaneTransportModes;

    /**
     * Represents the currently selected edge route.
     */
    private Edge<Route, Stop> selectedEdgeRoute;

    /**
     * Represents the button the user will click to undo the last bicycle update operation.
     */
    @FXML
    private Button buttonUndoUpdate;

    /**
     * Constructs a DeactivateRouteTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public DeactivateRouteTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the deactivate route tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        loadDynamicContent();

        setNodeVisibility(vBoxRouteSelectionSelected, false);
        buttonUndoUpdate.setOnAction(event -> handleUndoDeactivate());
    }

    /**
     * Checks if the current tab (deactivate route) is selected.
     *
     * @return True if is selected, false otherwise.
     */
    private boolean checkTabIsSelected() {
        return baseController.checkIfTabIsSelected(TransportMapFeature.DEACTIVATE_ROUTE);
    }

    /**
     * Populates the transport modes grid with labels and checkboxes.
     * - Each row represents a transport mode.
     * - Checkboxes reflect and update the enabled status of transport modes.
     */
    private void printRouteStatus() {
        int rowIndex = 0;

        gridPaneTransportModes.getChildren().clear();

        for (Transport transport : selectedEdgeRoute.element().getTransports()) {
            rowIndex = putTransportModeLabelAndCheckbox(transport, rowIndex);
        }
    }

    /**
     * Adds a transport mode's label and checkbox to the grid.
     *
     * @param transport The transport.
     * @param rowIndex  The current row index.
     * @return The next row index.
     */
    private int putTransportModeLabelAndCheckbox(Transport transport, int rowIndex) {
        String transportName = transport.getName();

        Label labelTransportMode = getTransportLabel(transportName);
        CheckBox checkBoxEnable = getTransportCheckBox(transportName, transport);

        gridPaneTransportModes.add(checkBoxEnable, 0, rowIndex);
        gridPaneTransportModes.add(labelTransportMode, 1, rowIndex++);
        return rowIndex;
    }

    /**
     * Creates a checkbox for enabling or disabling a transport mode.
     *
     * @param transportName the name of the transport mode.
     * @param transport     the transport mode object.
     * @return the created checkbox.
     */
    private CheckBox getTransportCheckBox(String transportName, Transport transport) {
        CheckBox checkBoxEnable = new CheckBox();
        checkBoxEnable.setId(transportName);
        checkBoxEnable.getStyleClass().add("tab-checkbox");
        checkBoxEnable.setSelected(transport.isEnabled());

        checkBoxEnable.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == transport.isEnabled()) return;

            handleUpdateEnabledStatus(transportName, newValue);
        });
        return checkBoxEnable;
    }

    /**
     * Creates a label for a transport mode.
     *
     * @param transportName the name of the transport mode.
     * @return the created label.
     */
    private static Label getTransportLabel(String transportName) {
        Label labelTransportMode = new Label(transportName);
        labelTransportMode.getStyleClass().addAll("tab-text", "tab-text-wrap");
        return labelTransportMode;
    }

    /**
     * Updates the enabled status of a transport mode.
     *
     * @param transportName the name of the transport mode.
     * @param newValue      the new enabled status.
     */
    private void handleUpdateEnabledStatus(String transportName, boolean newValue) {
        try {
            UserActionLogger.getInstance().performUserAction(
                    new UpdateTransportEnabledStatusCommand(transportMap, selectedEdgeRoute, transportName, newValue)
            );

            ControlNotification.show(
                    getStringResource("deactivateRouteTabController.controlNotification.success.title"),
                    getSuccessNotificationContent(transportName, newValue),
                    ControlNotification.NotificationType.SUCCESS
            );
        } catch (Exception e) {
            ControlNotification.show(
                    getStringResource("deactivateRouteTabController.controlNotification.error.title"),
                    getErrorNotificationContent(transportName),
                    ControlNotification.NotificationType.ERROR
            );
        }
    }

    /**
     * Inserts the transport mode name into the respective translation strings.
     *
     * @param transportName The transport name that would be updated.
     * @return The transformed error message.
     */
    private String getErrorNotificationContent(String transportName) {
        return getStringResource("deactivateRouteTabController.controlNotification.error.text1") +
                " " +
                transportName +
                getStringResource("deactivateRouteTabController.controlNotification.error.text2");
    }

    /**
     * Inserts the transport mode name into the respective translation strings.
     *
     * @param transportName  The transport name that was updated.
     * @param transportState The state the transport mode was put into.
     * @return The transformed success message.
     */
    private String getSuccessNotificationContent(String transportName, boolean transportState) {
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(getStringResource("deactivateRouteTabController.controlNotification.success.text1"));

        stringBuilder.append(" ");
        stringBuilder.append(transportName);
        stringBuilder.append(" ");

        stringBuilder.append(getStringResource("deactivateRouteTabController.controlNotification.success.text2"));

        stringBuilder.append(" ");
        if (transportState) {
            stringBuilder.append(getStringResource("deactivateRouteTabController.controlNotification.success.text3"));
        } else {
            stringBuilder.append(getStringResource("deactivateRouteTabController.controlNotification.success.text4"));
        }
        stringBuilder.append(" ");

        stringBuilder.append(getStringResource("deactivateRouteTabController.controlNotification.success.text5"));

        return stringBuilder.toString();
    }

    /**
     * Undoes the last action for transport enable/disable status update.
     */
    private void handleUndoDeactivate() {
        try {
            UserActionLogger.getInstance().undoTransportEnabledStatusUpdate();
            ControlNotification.show(
                    getStringResource("deactivateRouteTabController.controlNotification.undo.success.title"),
                    getStringResource("deactivateRouteTabController.controlNotification.undo.success.text"),
                    ControlNotification.NotificationType.INFORMATION
            );
        } catch (IllegalStateException e) {
            ControlNotification.show(
                    getStringResource("deactivateRouteTabController.controlNotification.undo.error.title"),
                    getStringResource("deactivateRouteTabController.controlNotification.undo.error.text"),
                    ControlNotification.NotificationType.WARNING
            );
        }
    }

    /**
     * Selects a route edge and updates the view.
     *
     * @param routeEdge the selected route edge.
     */
    private void selectRoute(Edge<Route, Stop> routeEdge) {
        selectedEdgeRoute = routeEdge;

        if (!checkTabIsSelected()) return;

        performRouteSelection(routeEdge);
    }

    /**
     * Notifies the base controller of the route selection and toggles the selected
     * view of the tab.
     *
     * @param routeEdge The selected route edge.
     */
    private void performRouteSelection(Edge<Route, Stop> routeEdge) {
        baseController.notifySelectedRoute(routeEdge);
        printRouteStatus();

        toggleNodesVisibility(vBoxRouteSelectionSelected, vBoxRouteSelectionUnselected);
    }

    /**
     * Unselects the current route and updates the view.
     */
    private void unselectRoute() {
        selectedEdgeRoute = null;

        if (checkTabIsSelected()) {
            baseController.notifyUnselectedRoute();
        }

        toggleNodesVisibility(vBoxRouteSelectionUnselected, vBoxRouteSelectionSelected);
    }

    /**
     * Highlights the origin and destination stops of the selected route.
     */
    @Override
    public void setupRouteManagement() {
        if (selectedEdgeRoute == null) return;

        performRouteSelection(selectedEdgeRoute);
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        if (selectedEdgeRoute == null) return;

        updateTransportModeCheckboxState();
    }

    /**
     * Updates the checkboxes values for each transport mode of the selected route.
     */
    private void updateTransportModeCheckboxState() {
        for (Transport transport : selectedEdgeRoute.element().getTransports()) {
            CheckBox checkBox = getTransportCheckbox(transport.getName());

            if (checkBox != null) {
                checkBox.setSelected(transport.isEnabled());
            }
        }
    }

    /**
     * Gets the transport mode checkbox by looking through its node ID.
     *
     * @param transportName The transport mode name.
     * @return The checkbox of the respective transport mode.
     */
    private CheckBox getTransportCheckbox(String transportName) {
        return (CheckBox) gridPaneTransportModes.lookup("#" + transportName);
    }

    /**
     * Updates the view every time the subject notifies its observers.
     *
     * @param object An updated object sent from its subject.
     */
    @Override
    public void update(Object object) {
        if (object instanceof TransportMap) {
            loadDynamicContent();
        } else if (object instanceof Edge<?, ?> routeEdge) {
            selectRoute((Edge<Route, Stop>) routeEdge);
        } else if (object instanceof Vertex<?>) {
            unselectRoute();
        }
    }
}