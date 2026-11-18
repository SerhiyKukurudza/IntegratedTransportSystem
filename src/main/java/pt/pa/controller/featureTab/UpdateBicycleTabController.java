/******************************************************************************************************************************
 * File Name: UpdateBicycleTabController.java
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
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.controller.TransportMapFeature;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.updateBicycle.UpdateBicycleDurationCommand;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;
import pt.pa.ui.DisplayHelper;

import static pt.pa.ui.DisplayHelper.*;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the UpdateBicycleTab view.
 */
public class UpdateBicycleTabController extends TransportBaseController implements FeatureTabController, Observer, RouteDetailManager {

    /**
     * Represents the background pane of the design route tab.
     */
    @FXML
    private Pane tabUpdateBicyclePane;

    /**
     * Represents the default, unselected route VBox.
     */
    @FXML
    private VBox vBoxUpdateDurationUnselected;

    /**
     * Represents the VBox that is shown upon valid route selection.
     */
    @FXML
    private VBox vBoxUpdateDurationSelected;

    /**
     * Represents the text field in which the user will write the new bicycle duration.
     */
    @FXML
    private TextField textFieldDuration;

    /**
     * Represents the button which the user will click to update the bicycle duration.
     */
    @FXML
    private Button buttonUpdate;

    /**
     * Represents the button the user will click to undo the last bicycle update operation.
     */
    @FXML
    private Button buttonUndoUpdate;

    /**
     * Represents the currently selected edge route.
     */
    private Edge<Route, Stop> selectedEdgeRoute;

    /**
     * Constructs a UpdateBicycleTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public UpdateBicycleTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the design route tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        loadDynamicContent();

        DisplayHelper.setNodeVisibility(vBoxUpdateDurationSelected, false);
        setButtonTriggers();
    }

    /**
     * Selects a route edge and updates the state accordingly.
     * Does not notify the map if the route is non-bicycle or the tab is unselected.
     *
     * @param routeEdge The route edge to select.
     */
    private void selectRoute(Edge<Route, Stop> routeEdge) {
        selectedEdgeRoute = routeEdge;

        if (!checkTabIsSelected()) return;

        performRouteSelection(routeEdge);
    }

    /**
     * Handles route selection logic and updates the view.
     * Notifies the base controller and toggles the selected tab view.
     *
     * @param routeEdge The route edge to process.
     */
    private void performRouteSelection(Edge<Route, Stop> routeEdge) {
        if (routeCanNotBeTraversedByBicycle(routeEdge)) return;

        baseController.notifySelectedRoute(routeEdge);

        toggleNodesVisibility(vBoxUpdateDurationSelected, vBoxUpdateDurationUnselected);
        setTextFieldDuration();
    }

    /**
     * Unselects the route by updating the controller state and updating the view.
     */
    private void unselectRoute() {
        selectedEdgeRoute = null;

        if (checkTabIsSelected()) {
            baseController.notifyUnselectedRoute();
        }

        toggleNodesVisibility(vBoxUpdateDurationUnselected, vBoxUpdateDurationSelected);
    }

    /**
     * Checks if the current tab (update bicycle) is selected.
     *
     * @return True if is selected, false otherwise.
     */
    private boolean checkTabIsSelected() {
        return baseController.checkIfTabIsSelected(TransportMapFeature.UPDATE_BICYCLE);
    }

    /**
     * Checks if an edge route can be traversed by bicycle (if there is a bicycle option in the route).
     *
     * @param edgeRoute The edge route to check.
     * @return True if the route is valid for traversal by bicycle, false otherwise.
     */
    private boolean routeCanNotBeTraversedByBicycle(Edge<Route, Stop> edgeRoute) {
        if (edgeRoute == null || edgeRoute.element() == null) return true;

        try {
            edgeRoute.element().getTransportMetricsByTransportName("Bicycle");
            return false;
        } catch (IllegalArgumentException e) {
            if (checkTabIsSelected()) {
                ControlNotification.show(
                        getStringResource("updateBicycleTabController.notification.invalidTransport.title"),
                        getStringResource("updateBicycleTabController.notification.invalidTransport.content"),
                        ControlNotification.NotificationType.WARNING
                );
            }
        }

        return true;
    }

    /**
     * Gets the bicycle duration of the currently selected route.
     *
     * @return The bicycle duration.
     * @throws IllegalStateException If there is no route selected.
     */
    private double getBicycleDuration() {
        if (selectedEdgeRoute == null) {
            throw new IllegalStateException("A route must be selected first");
        }

        Route selectedRoute = selectedEdgeRoute.element();
        return selectedRoute.getBicycleDuration();
    }

    /**
     * Sets the value of the text field to the currently selected route bicycle transportation mode duration.
     */
    private void setTextFieldDuration() {
        try {
            textFieldDuration.setText(String.format("%.0f", getBicycleDuration()));
        } catch (IllegalStateException ignored) {
        }
    }

    /**
     * Sets the triggers for the update and undo buttons.
     */
    private void setButtonTriggers() {
        buttonUpdate.setOnAction(event -> {
            String duration = textFieldDuration.getText();
            handleUpdateBicycleDuration(duration);
        });

        buttonUndoUpdate.setOnAction(event -> {
            handleUndoUpdateBicycleDuration();
        });
    }

    /**
     * Handles The update of the bicycle duration.
     * Manages the error/success flow of the command's execution.
     *
     * @param textDuration The duration from the text field.
     */
    private void handleUpdateBicycleDuration(String textDuration) {
        Double duration = getBicycleDuration(textDuration);
        if (duration == null) return;

        try {
            UserActionLogger.getInstance().performUserAction(new UpdateBicycleDurationCommand(transportMap, selectedEdgeRoute, duration));

            ControlNotification.show(
                    getStringResource("updateBicycleTabController.notification.commandExecution.success.title"),
                    getStringResource("updateBicycleTabController.notification.commandExecution.success.message1")
                            + " " + String.format("%.0f", duration) + " " +
                            getStringResource("updateBicycleTabController.notification.commandExecution.success.message2"),
                    ControlNotification.NotificationType.SUCCESS);
        } catch (RuntimeException e) {
            ControlNotification.show(
                    getStringResource("updateBicycleTabController.notification.commandExecution.error.title"),
                    getStringResource("updateBicycleTabController.notification.commandExecution.error.message1"),
                    ControlNotification.NotificationType.ERROR);
        }
    }

    /**
     * Returns the bicycle duration if no error was found.
     *
     * @param textDuration The bicycle duration as a string.
     * @return The double valued bicycle duration if it is valid.
     * False otherwise.
     */
    private Double getBicycleDuration(String textDuration) {
        double duration;

        try {
            duration = errorHandler(textDuration);
        } catch (IllegalArgumentException e) {
            ControlNotification.show(getStringResource("updateBicycleTabController.notification.invalidDuration.title"),
                    e.getMessage(), ControlNotification.NotificationType.ERROR
            );
            return null;
        }
        return duration;
    }

    /**
     * Handles any errors the user might have commited while trying to update the bicycle duration.
     *
     * @param textDuration The text of the duration.
     * @return The double value of the duration.
     * @throws IllegalArgumentException If the duration is blank,
     *                                  the text is not parsable to double,
     *                                  the new duration is the same as the old one.
     */
    private double errorHandler(String textDuration) {
        checkIfDurationIsBlank(textDuration);
        double duration = checkIfDurationValueIsValid(textDuration);

        removeErrorStyling(textFieldDuration);

        return duration;
    }

    /**
     * Checks if the duration is a positive number, different from the previous bicycle duration.
     *
     * @param textDuration The string bicycle duration.
     * @return The bicycle double duration.
     * @throws IllegalStateException If the string duration was not valid.
     */
    private double checkIfDurationValueIsValid(String textDuration) {
        double duration;

        try {
            duration = Double.parseDouble(textDuration);
        } catch (NumberFormatException e) {
            addErrorStyling(textFieldDuration);
            throw new IllegalArgumentException(getStringResource("updateBicycleTabController.notification.invalidDuration.message2"));
        }

        if (duration < 0) {
            addErrorStyling(textFieldDuration);
            throw new IllegalArgumentException(getStringResource("updateBicycleTabController.notification.invalidDuration.message3"));
        }

        if (getBicycleDuration() == duration) {
            addErrorStyling(textFieldDuration);
            throw new IllegalArgumentException(getStringResource("updateBicycleTabController.notification.invalidDuration.message4"));
        }

        return duration;
    }

    /**
     * Checks if the string value is blank.
     *
     * @param textDuration The string text duration.
     * @throws IllegalArgumentException If the string was blank.
     */
    private void checkIfDurationIsBlank(String textDuration) {
        if (textDuration.isBlank()) {
            addErrorStyling(textFieldDuration);
            throw new IllegalArgumentException(getStringResource("updateBicycleTabController.notification.invalidDuration.message1"));
        }
    }

    /**
     * Handle the undo of a bicycle duration update.
     * Shows a different notification for the success and error cases.
     */
    private void handleUndoUpdateBicycleDuration() {
        try {
            UserActionLogger.getInstance().undoBicycleUpdate();

            ControlNotification.show(
                    getStringResource("updateBicycleTabController.notification.commandUndone.success.title"),
                    getStringResource("updateBicycleTabController.notification.commandUndone.success.message1"),
                    ControlNotification.NotificationType.INFORMATION);
        } catch (IllegalStateException e) {
            ControlNotification.show(
                    getStringResource("updateBicycleTabController.notification.commandUndone.error.title"),
                    getStringResource("updateBicycleTabController.notification.commandUndone.error.message1"),
                    ControlNotification.NotificationType.WARNING);
        }
    }

    @Override
    public void setupRouteManagement() {
        if (selectedEdgeRoute == null) return;

        performRouteSelection(selectedEdgeRoute);
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     * <p>
     * Only loads if the currently selected edge route can be traversed by bike.
     * </p>
     */
    private void loadDynamicContent() {
        if (routeCanNotBeTraversedByBicycle(selectedEdgeRoute)) {
            return;
        }

        setTextFieldDuration();
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
