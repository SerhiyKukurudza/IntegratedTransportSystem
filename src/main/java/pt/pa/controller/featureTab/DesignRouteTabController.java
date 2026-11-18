/******************************************************************************************************************************
 * File Name: DesignRouteTabController.java
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
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.controller.TransportMapFeature;
import pt.pa.exceptions.InvalidRouteException;
import pt.pa.exceptions.InvalidStopException;
import pt.pa.model.*;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.designRoute.DesignRouteCommand;
import pt.pa.patterns.command.concrete.designRoute.SelectPreferredCostCommand;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;
import pt.pa.utils.TransportColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static pt.pa.ui.DisplayHelper.*;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the DesignRouteTab view.
 */
public class DesignRouteTabController extends TransportBaseController implements FeatureTabController, Observer {

    /**
     * Represents the background pane of the design route tab.
     */
    @FXML
    private Pane tabDesignRoutePane;

    /**
     * ComboBox that allows users to select the preferred cost type for the route.
     */
    @FXML
    private ComboBox<CostType> comboBoxPreferredCost;

    /**
     * GridPane that displays the available transportation modes.
     */
    @FXML
    private GridPane gridPaneTransportationModes;

    /**
     * VBox container used to show or hide the undo button.
     */
    @FXML
    private VBox vBoxUndo;

    /**
     * Button that triggers the undo action for route selection.
     */
    @FXML
    private Button buttonUndoSelection;

    /**
     * List of selected stops for the route.
     */
    private final List<Vertex<Stop>> selectedStops;

    /**
     * List of selected routes, each paired with a cost type.
     */
    private final List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes;

    /**
     * Constructs a DesignRouteTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public DesignRouteTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);

        this.selectedStops = new ArrayList<>();
        this.selectedRoutes = new ArrayList<>();
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

        initializeCostTypeComboBox();

        hideUndo();
        handleButtonUndoClick();
    }

    /**
     * Clears the selections of stops and routes.
     */
    public void clearRouteAndStopSelection() {
        this.selectedStops.clear();
        this.selectedRoutes.clear();
    }

    /**
     * Shows the undo option in the view.
     */
    public void showUndo() {
        setNodeVisibility(vBoxUndo, true);
    }

    /**
     * Hides the undo option in the view.
     */
    public void hideUndo() {
        setNodeVisibility(vBoxUndo, false);
    }

    /**
     * Sets up the action handler for the undo button.
     */
    private void handleButtonUndoClick() {
        buttonUndoSelection.setOnAction((event) -> {
            UserActionLogger.getInstance().undoRouteUpdate();
        });
    }

    /**
     * If the tab design feature tab is selected.
     * Handles the selection of a stop, adding it to the route and updating the view accordingly.
     *
     * @param stopVertex The stop that was selected.
     */
    private void selectedStop(Vertex<Stop> stopVertex) {
        if (!checkTabIsSelected()) {
            return;
        }

        CostType selectedCostType = comboBoxPreferredCost.getSelectionModel().getSelectedItem();

        try {
            addStopHandler(selectedCostType, stopVertex);
        } catch (IllegalArgumentException e) {
            ControlNotification.show(
                    getStringResource("designRouteTabController.controlNotification.selectStop.error.title"),
                    e.getMessage(),
                    ControlNotification.NotificationType.ERROR
            );
            return;
        }

        UserActionLogger.getInstance().performUserAction(new DesignRouteCommand(baseController, selectedStops, selectedRoutes));
    }

    /**
     * Adds a stop to the route, ensuring that it is valid based on the current selections.
     *
     * @param costType   The cost type associated with the stop.
     * @param stopVertex The stop to be added.
     */
    private void addStopHandler(CostType costType, Vertex<Stop> stopVertex) {
        checkSelectedCost(costType);

        if (addFirstStop(stopVertex)) {
            return;
        }

        addNextStop(stopVertex);
        addNextRoute(costType);
    }

    /**
     * Checks if the selected cost type is valid.
     * Clears the error styling if it was previously applied.
     *
     * @param costType The selected cost type.
     * @throws IllegalArgumentException If the cost type is null.
     */
    private void checkSelectedCost(CostType costType) {
        if (costType == null) {
            addErrorStyling(comboBoxPreferredCost);
            throw new IllegalArgumentException(
                    getStringResource("designRouteTabController.controlNotification.errors.selectedCost")
            );
        }

        removeErrorStyling(comboBoxPreferredCost);
    }

    /**
     * Adds the first stop to the route.
     *
     * @param stopVertex The stop to be added.
     * @return True if the first stop was added, false otherwise.
     */
    private boolean addFirstStop(Vertex<Stop> stopVertex) {
        if (!selectedStops.isEmpty()) {
            return false;
        }

        selectedStops.add(stopVertex);
        return true;
    }

    /**
     * Adds a subsequent stop to the route.
     *
     * @param stopVertex The stop to be added.
     */
    private void addNextStop(Vertex<Stop> stopVertex) {
        checkStopAdjacency(stopVertex);
        checkPossibleTransports(stopVertex);
        checkDifferentStop(stopVertex);

        selectedStops.add(stopVertex);
    }

    /**
     * Checks if the selected vertex is not the same as the previously selected.
     * It should not be possible to select the same stop twice.
     *
     * @param stopVertex The last selected stop vertex.
     * @throws IllegalArgumentException If the new stop was already selected.
     */
    private void checkDifferentStop(Vertex<Stop> stopVertex) {
        if (selectedStops.contains(stopVertex)) {
            throw new IllegalArgumentException(
                    getStringResource("designRouteTabController.controlNotification.errors.differentStop")
            );
        }
    }

    /**
     * Checks if the last selected stop vertex has possible transport modes to navigate through.
     *
     * @param stopVertex The last selected stop vertex.
     * @throws IllegalArgumentException If there are no possible transports in the route that connect the last two stops.
     */
    private void checkPossibleTransports(Vertex<Stop> stopVertex) {
        if (!transportMap.routeHasEnabledTransports(stopVertex, getLastSelectedStop())) {
            throw new IllegalArgumentException(
                    getStringResource("designRouteTabController.controlNotification.errors.possibleTransports")
            );
        }
    }

    /**
     * Checks if the new stop vertex is not adjacent to the last selected one.
     *
     * @param stopVertex The last selected stop vertex.
     * @throws IllegalArgumentException If the new stop is not adjacent to the last stop.
     */
    private void checkStopAdjacency(Vertex<Stop> stopVertex) {
        if (!transportMap.stopsAreAdjacent(stopVertex, getLastSelectedStop())) {
            throw new IllegalArgumentException(
                    getStringResource("designRouteTabController.controlNotification.errors.stopAdjacency")
            );
        }
    }


    /**
     * Adds a route between two stops, along with the selected cost type.
     *
     * @param costType The cost type of the route.
     * @throws InvalidRouteException If no route exists between the two stops.
     * @throws InvalidStopException  If the provided stops are invalid.
     */
    private void addNextRoute(CostType costType) {
        if (selectedStops.size() < 2) {
            throw new InvalidStopException("At least two stops are required to add a route.");
        }

        Vertex<Stop> addedVertex = selectedStops.get(selectedStops.size() - 1);
        Vertex<Stop> previousVertex = selectedStops.get(selectedStops.size() - 2);

        Optional<Edge<Route, Stop>> optionalRoute = transportMap.getRouteBetweenStops(addedVertex, previousVertex);

        Edge<Route, Stop> edgeRoute = optionalRoute.orElseThrow(() ->
                new InvalidRouteException(getStringResource("exceptions.routeDoesNotExist"))
        );

        selectedRoutes.add(new Pair<>(edgeRoute, costType));
    }

    /**
     * Retrieves the last selected stop.
     *
     * @return The last selected stop, or null if no stops have been selected.
     */
    private Vertex<Stop> getLastSelectedStop() {
        if (selectedStops.isEmpty()) return null;

        return selectedStops.get(selectedStops.size() - 1);
    }

    /**
     * Retrieves the first selected stop (origin stop).
     *
     * @return The origin stop, or null if no stops have been selected.
     */
    private Vertex<Stop> getOriginStop() {
        if (selectedStops.isEmpty()) return null;

        return selectedStops.get(0);
    }

    /**
     * Checks if the current tab (design route) is selected.
     *
     * @return True if is selected, false otherwise.
     */
    private boolean checkTabIsSelected() {
        return baseController.checkIfTabIsSelected(TransportMapFeature.DESIGN_ROUTE);
    }

    /**
     * Initializes the combo box for selecting the preferred cost type.
     */
    private void initializeCostTypeComboBox() {
        setCostComboBoxConverter();
        setCostComboBoxValues();
        setCostComboBoxTriggers();
    }

    /**
     * Sets the cost combo box trigger to perform a user action upon selection.
     */
    private void setCostComboBoxTriggers() {
        comboBoxPreferredCost.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            UserActionLogger.getInstance().performUserAction(new SelectPreferredCostCommand(newValue));
        });
    }

    /**
     * Sets the possible cost values at the cost combo box.
     */
    private void setCostComboBoxValues() {
        comboBoxPreferredCost.getItems().clear();
        comboBoxPreferredCost.getItems().addAll(CostType.values());
    }

    /**
     * Sets the String converter for the cost combo box.
     */
    private void setCostComboBoxConverter() {
        comboBoxPreferredCost.setConverter(new StringConverter<>() {
            @Override
            public String toString(CostType costType) {
                return costType.getName();
            }

            @Override
            public CostType fromString(String string) {
                return comboBoxPreferredCost.getItems().stream()
                        .filter(costType -> costType.getName()
                                .equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    /**
     * Initializes the labels for the transportation modes grid.
     */
    private void initializeTransportGridLabels() {
        int rowIndex = 0;
        for (Map.Entry<String, TransportColor> transport : transportMap.getTransportNamesAndColors().entrySet()) {
            Label labelTransport = new Label(transport.getKey());
            labelTransport.getStyleClass().add("tab-text");

            Region transportColorSquare = getTransportColorSquare(transport.getValue());

            gridPaneTransportationModes.add(transportColorSquare, 0, rowIndex);
            gridPaneTransportationModes.add(labelTransport, 1, rowIndex);

            rowIndex++;
        }
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        initializeTransportGridLabels();
    }

    /**
     * Updates the view every time the subject notifies its observers.
     *
     * @param object - The updated object.
     */
    @Override
    public void update(Object object) {
        if (object instanceof TransportMap) {
            loadDynamicContent();
        } else if (object instanceof Vertex<?> stopVertex) {
            selectedStop((Vertex<Stop>) stopVertex);
        }
    }
}
