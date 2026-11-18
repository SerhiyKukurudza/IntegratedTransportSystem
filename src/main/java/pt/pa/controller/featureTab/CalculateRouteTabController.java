/******************************************************************************************************************************
 * File Name: CalculateRouteTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.StringConverter;
import org.javatuples.Quartet;
import org.javatuples.Triplet;
import pt.pa.controller.BaseController;
import pt.pa.controller.MapController;
import pt.pa.controller.TransportBaseController;
import pt.pa.exceptions.PathNotFoundException;
import pt.pa.model.*;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.calculateRoute.*;
import pt.pa.patterns.factory.simple.PathfindingStrategyFactory;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;
import pt.pa.utils.TransportColor;

import java.util.*;

import static pt.pa.ui.DisplayHelper.getTransportColorSquare;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;


/**
 * Represents the controller class for the CalculateRouteTab view.
 */
public class CalculateRouteTabController extends TransportBaseController implements FeatureTabController, Observer {

    /**
     * Represents the background pane of the calculate route tab.
     */
    @FXML
    private Pane tabCalculateRoutePane;

    /**
     * Represents the Combo Box used to select the origin stop.
     */
    @FXML
    private ComboBox<Vertex<Stop>> comboBoxOriginStop;

    /**
     * Represents the Combo Box used to select the destination stop.
     */
    @FXML
    private ComboBox<Vertex<Stop>> comboBoxDestinationStop;

    /**
     * Represents the Combo Box used to select the cost type.
     */
    @FXML
    private ComboBox<CostType> comboBoxPreferredCost;

    /**
     * Represents the grid pane that will hold the usable transports checkboxes.
     */
    @FXML
    private VBox vBoxUsableTransports;

    /**
     * Represents the button that will find the shortest path.
     */
    @FXML
    private Button buttonFindShortestPath;

    /**
     * A reference to the TimerTask
     */
    private TimerTask routeButtonTask;

    /**
     * Constructs a CalculateRouteController instance with a transport map model and its base controller.
     *
     * @param transportMap   The transport map instance.
     * @param baseController The base controller instance.
     */
    public CalculateRouteTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the calculate route tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        loadDynamicContent();
        initializeTriggers();

        initializeComponents();
    }

    /**
     * Initializes all UI components.
     */
    private void initializeComponents() {
        initializeStopComboBox(comboBoxOriginStop);
        initializeStopComboBox(comboBoxDestinationStop);
        initializeCostTypeComboBox();
        initializeVBox();
    }

    /**
     * Initialized a stop combo box.
     * Sets up a string converter for the stop name.
     */
    private void initializeStopComboBox(ComboBox<Vertex<Stop>> comboBox) {
        comboBox.setConverter(new StringConverter<>() {
            @Override
            public String toString(Vertex<Stop> stopVertex) {
                return stopVertex.element().toString();
            }

            @Override
            public Vertex<Stop> fromString(String string) {
                return comboBox.getItems().stream()
                        .filter(vertex -> vertex.element().toString()
                                .equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    /**
     * Initialized the cost type combo box.
     * Sets up a string converter for the cost type.
     */
    private void initializeCostTypeComboBox() {
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
     * Initialized the vertical box for the usable Transports.
     * Sets up the checkbox and the transport name associated with it.
     */
    private void initializeVBox() {
        vBoxUsableTransports.setSpacing(10);
        Set<String> uniqueTransports = transportMap.getUniqueTransports();

        for (Map.Entry<String, TransportColor> transportNameAndColor : transportMap.getTransportNamesAndColors().entrySet()) {
            if (!uniqueTransports.contains(transportNameAndColor.getKey())) continue;

            HBox transportHBox = getTransportCheckBoxWithLabel(transportNameAndColor);

            vBoxUsableTransports.getChildren().add(transportHBox);
        }
    }

    /**
     * Gets a HBox with a square, name and checkbox for each transport mode.
     *
     * @param transportNameAndColor The transport mode color entry with the name of the transport and the color.
     * @return The HBox for the transport mode.
     */
    private HBox getTransportCheckBoxWithLabel(Map.Entry<String, TransportColor> transportNameAndColor) {
        HBox transportHBox = new HBox(5);

        String transportName = transportNameAndColor.getKey();
        TransportColor transportColor = transportNameAndColor.getValue();

        CheckBox newCheckbox = getTransportCheckBox(transportName);
        Label transportNameLabel = getTransportLabel(transportName);
        Region colorSquare = getTransportColorSquare(transportColor);

        transportHBox.getChildren().addAll(newCheckbox, colorSquare, transportNameLabel);

        return transportHBox;
    }

    /**
     * Gets the transport mode label with the correspondent color.
     *
     * @param transportName The transport mode name.
     * @return The transport mode name label.
     */
    private static Label getTransportLabel(String transportName) {
        Label transportNameLabel = new Label(transportName);
        transportNameLabel.getStyleClass().add("tab-text");

        return transportNameLabel;
    }

    /**
     * Gets a transport check box.
     *
     * @param transportName The transport mode name.
     * @return The newly created checkbox.
     */
    private CheckBox getTransportCheckBox(String transportName) {
        CheckBox newCheckbox = new CheckBox();
        newCheckbox.setSelected(true);

        newCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            UserActionLogger.getInstance().performUserAction(
                    new SelectTransportCommand(transportName, newValue)
            );
        });

        return newCheckbox;
    }

    /**
     * Updated the stops present in the combo box with the stops sorted.
     */
    private void updateStopComboBox(ComboBox<Vertex<Stop>> comboBoxStop) {
        comboBoxStop.getItems().clear();
        comboBoxStop.getItems().addAll(transportMap.getStopsOrderedByName());
    }

    /**
     * Updated the cost types present in the combo box.
     */
    private void updateCostTypeComboBox() {
        comboBoxPreferredCost.getItems().clear();
        comboBoxPreferredCost.getItems().addAll(CostType.values());
    }

    /**
     * Initializes the event triggers for combo box selections, including origin and destination stop selections.
     */
    private void initializeTriggers() {
        setOriginComboBoxTriggers();
        setDestinationComboBoxTriggers();

        setCostComboBoxTriggers();
    }

    /**
     * Sets the cost combo box triggers to perform a user action upon selection.
     */
    private void setCostComboBoxTriggers() {
        comboBoxPreferredCost.getSelectionModel().selectedItemProperty().addListener((observable, oldCost, newCost) -> {
            UserActionLogger.getInstance().performUserAction(new SelectPreferredCostCommand(newCost));
        });
    }

    /**
     * Sets the destination stop combo box triggers to perform a user action upon selection.
     * It also hides the same stop on the other combo box.
     */
    private void setDestinationComboBoxTriggers() {
        comboBoxDestinationStop.getSelectionModel().selectedItemProperty().addListener((observable, oldStop, newStop) -> {
            if (newStop == null) return;

            UserActionLogger.getInstance().performUserAction(new SelectDestinationStopCommand(baseController, newStop));
            hideOppositeComboBoxSelection(oldStop, newStop, comboBoxOriginStop);
        });
    }

    /**
     * Sets the origin stop combo box triggers to perform a user action upon selection.
     * It also hides the same stop on the other combo box.
     */
    private void setOriginComboBoxTriggers() {
        comboBoxOriginStop.getSelectionModel().selectedItemProperty().addListener((observable, oldStop, newStop) -> {
            if (newStop == null) return;

            UserActionLogger.getInstance().performUserAction(new SelectOriginStopCommand(baseController, newStop));
            hideOppositeComboBoxSelection(oldStop, newStop, comboBoxDestinationStop);
        });
    }

    /**
     * Updates the stop selection and hides the same option on the other combo box.
     *
     * @param oldStop               The old stop.
     * @param newStop               The new stop.
     * @param hideSelectionComboBox The other combo box which value should be hidden.
     */
    private void hideOppositeComboBoxSelection(Vertex<Stop> oldStop, Vertex<Stop> newStop, ComboBox<Vertex<Stop>> hideSelectionComboBox) {
        if (oldStop != null) {
            hideSelectionComboBox.getItems().add(oldStop);
            hideSelectionComboBox.getItems().sort(Comparator.comparing((Vertex<Stop> o) -> o.element().getName()));
        }

        hideSelectionComboBox.getItems().remove(newStop);
    }

    /**
     * Handles the "Find Shortest Path" button click.
     * - Validates selected transports.
     * - Sets up the pathfinding strategy.
     * - Calculates the shortest path.
     * - Displays the result or shows an error if the calculation fails.
     * - Disables the button during the calculation and re-enables it afterward.
     */
    private void handleButtonClick() {
        buttonFindShortestPath.setOnMouseClicked(event -> {
            Set<String> usableTransports = getSelectedTransports();

            try {
                updateCalculationStrategy(usableTransports);
                calculateRoute(usableTransports);
            } catch (IllegalStateException e) {
                ControlNotification.show(
                        getStringResource("calculateRouteTabController.controlNotification.illegalState.title"),
                        e.getMessage(), ControlNotification.NotificationType.ERROR
                );
            } catch (PathNotFoundException e) {
                ControlNotification.show(
                        getStringResource("calculateRouteTabController.controlNotification.default.title"),
                        getStringResource("calculateRouteTabController.controlNotification.pathfinding.text"),
                        ControlNotification.NotificationType.ERROR
                );
            } catch (IllegalArgumentException e) {
                ControlNotification.show(
                        getStringResource("calculateRouteTabController.controlNotification.default.title"),
                        e.getMessage(), ControlNotification.NotificationType.ERROR
                );
            }
        });
    }

    /**
     * Validates the selected transports and sets up the pathfinding strategy.
     *
     * @param usableTransports the set of selected transport modes.
     * @throws IllegalArgumentException if no transport mode is selected.
     */
    private void updateCalculationStrategy(Set<String> usableTransports) {
        if (usableTransports.isEmpty()) {
            throw new IllegalArgumentException(
                    getStringResource("calculateRouteTabController.controlNotification.transportMode.text")
            );
        }

        setupPathfindingStrategy();
    }

    /**
     * Calculates the shortest path using the selected transports.
     *
     * @param usableTransports the set of selected transport modes.
     * @throws PathNotFoundException if no valid path is found.
     */
    private void calculateRoute(Set<String> usableTransports) {
        List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath = calculateShortestPath(usableTransports);

        if (shortestPath == null || shortestPath.isEmpty()) {
            throw new PathNotFoundException(
                    getStringResource("calculateRouteTabController.controlNotification.noPath.text")
            );
        }

        buttonClickDisableAnimation(shortestPath);
        processPath(shortestPath);
    }

    /**
     * Disables the button and introduces a delay based on the route stops.
     *
     * @param shortestPath the calculated shortest path.
     */
    private void buttonClickDisableAnimation(List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath) {
        buttonFindShortestPath.setDisable(true);
        long totalDelayTime = shortestPath.size() * MapController.MARK_MIDDLE_STOPS_DELAY_MS;
        disableCalculateRouteButton(totalDelayTime);
    }


    /**
     * Disables the "Find Shortest Path" button for a specified amount of time and re-enables it afterward.
     *
     * @param milliseconds the delay time in milliseconds for which the button should remain disabled.
     *                     Once the delay time passes, the button will be re-enabled.
     */
    public void disableCalculateRouteButton(long milliseconds) {
        cleanTask();

        routeButtonTask = new TimerTask() {
            @Override
            public void run() {
                buttonFindShortestPath.setDisable(false);
            }
        };

        new Timer().schedule(routeButtonTask, milliseconds);
    }

    /**
     * Cancels the currently scheduled task, if any.
     * This helps avoid warnings in the console caused by running tasks.
     */
    private void cleanTask() {
        if (routeButtonTask != null) {
            routeButtonTask.cancel();
        }
    }

    /**
     * Extracts the selected transport modes from the checkboxes.
     *
     * @return A set of selected transport mode names.
     */
    private Set<String> getSelectedTransports() {
        Set<String> usableTransports = new HashSet<>();

        for (Node node : vBoxUsableTransports.getChildren()) {
            if (node instanceof HBox transportHBox) {
                checkSelectedCheckBoxes(transportHBox, usableTransports);
            }
        }

        return usableTransports;
    }

    /**
     * Adds all transport modes with a selected checkbox.
     *
     * @param transportHBox    The HBox where the checkboxes and labels are.
     * @param usableTransports The usable transports set where the transports are added.
     */
    private static void checkSelectedCheckBoxes(HBox transportHBox, Set<String> usableTransports) {
        CheckBox associatedCheckbox = null;
        Label associatedLabel = null;

        for (Node hboxNode : transportHBox.getChildren()) {
            if (hboxNode instanceof CheckBox checkbox) {
                associatedCheckbox = checkbox;
            } else if (hboxNode instanceof Label label) {
                associatedLabel = label;
            }
        }

        if (associatedCheckbox != null && associatedLabel != null && associatedCheckbox.isSelected()) {
            usableTransports.add(associatedLabel.getText());
        }
    }

    /**
     * Sets up the pathfinding strategy based on the user selection.
     */
    private void setupPathfindingStrategy() {
        try {
            transportMap.setPathfindingStrategy(PathfindingStrategyFactory.create(comboBoxPreferredCost.getValue()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    getStringResource("calculateRouteTabController.controlNotification.noSelectedCost.text")
            );
        }
    }

    /**
     * Calculates the shortest path based on user inputs.
     *
     * @param usableTransports The set of selected transport modes.
     * @return The shortest path, or null if an error occurs.
     */
    private List<Triplet<Vertex<Stop>, String, TransportMetrics>> calculateShortestPath(Set<String> usableTransports) {
        return transportMap.findOptimalPath(
                comboBoxOriginStop.getValue(),
                comboBoxDestinationStop.getValue(),
                usableTransports,
                comboBoxPreferredCost.getValue()
        );
    }

    /**
     * Processes the calculated path, logs the user action, and prepares visualization.
     *
     * @param shortestPath The calculated shortest path as a list of triplets containing
     *                     the vertex of a stop, the transport name, and associated transport metrics.
     */
    private void processPath(List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath) {
        List<Vertex<Stop>> middleStops = new ArrayList<>();
        TransportMetrics totalTransportMetrics = TransportMetrics.getEmptyTransportMetrics();
        List<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges = new ArrayList<>();

        for (int i = 0; i < shortestPath.size() - 1; i++) {
            Vertex<Stop> currentStop = getCurrentStopVertex(shortestPath, i);
            String transportName = shortestPath.get(i + 1).getValue1();

            addMiddleStop(currentStop, middleStops);

            Edge<Route, Stop> routeEdge = getRouteEdge(shortestPath, i, currentStop);
            TransportColor color = transportMap.getTransportModeColor(transportName);
            Double cost = getTransportCost(shortestPath, i);

            sumMetrics(totalTransportMetrics, routeEdge, transportName);
            pathEdges.add(new Quartet<>(routeEdge, transportName, color, cost));
        }

        UserActionLogger.getInstance().performUserAction(
                new CalculateRouteCommand(baseController, comboBoxOriginStop.getValue(),
                        comboBoxDestinationStop.getValue(), middleStops,
                        pathEdges, totalTransportMetrics)
        );
    }

    /**
     * Gets the current stop vertex to be processed.
     *
     * @param shortestPath The shortest path triplet list with the path details.
     * @param index        The index of the current stop vertex being processed.
     * @return The current stop vertex.
     */
    private static Vertex<Stop> getCurrentStopVertex(List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath, int index) {
        Triplet<Vertex<Stop>, String, TransportMetrics> step = shortestPath.get(index);
        return step.getValue0();
    }

    /**
     * Gets the transport cost according to its transport metrics and the selected cost type.
     *
     * @param shortestPath The shortest path triplet list with the path details.
     * @param index        The index of the current stop being processed.
     * @return The double value of the preferred cost in the transport metrics..
     */
    private Double getTransportCost(List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath, int index) {
        TransportMetrics nextTransportMetrics = shortestPath.get(index + 1).getValue2();
        CostType selectedCostType = comboBoxPreferredCost.getValue();

        return nextTransportMetrics.getCostByType(selectedCostType);
    }

    /**
     * Sums the transport metrics of the specified transport mode in the route.
     *
     * @param totalTransportMetrics The total transport metrics where the sum will be performed.
     * @param routeEdge             The route where the transport may be found.
     * @param transportName         The transport name whose metrics should be summed.
     */
    private void sumMetrics(TransportMetrics totalTransportMetrics, Edge<Route, Stop> routeEdge, String transportName) {
        Route route = routeEdge.element();
        TransportMetrics transportMetrics = route.getTransportMetricsByTransportName(transportName);

        totalTransportMetrics.sumMetrics(transportMetrics);
    }

    /**
     * Gets the route edge between the current and next stop.
     *
     * @param shortestPath The shortest path triplet list where the next stop can be found.
     * @param index        The current stop's index.
     * @param currentStop  The current stop vertex.
     * @return The found edge route between the current and next stop.
     */
    private Edge<Route, Stop> getRouteEdge(List<Triplet<Vertex<Stop>, String, TransportMetrics>> shortestPath,
                                           int index, Vertex<Stop> currentStop) {
        Vertex<Stop> nextStop = shortestPath.get(index + 1).getValue0();
        Optional<Edge<Route, Stop>> optionalRouteEdge = transportMap.getRouteBetweenStops(currentStop, nextStop);
        return optionalRouteEdge.orElseThrow();
    }

    /**
     * Adds a stop to the middle stops list when it is indeed a middle stop.
     *
     * @param currentStop The current stop being checked.
     * @param middleStops The middle stops list where the stop may be added.
     */
    private void addMiddleStop(Vertex<Stop> currentStop, List<Vertex<Stop>> middleStops) {
        boolean currentStopIsNotOrigin = currentStop != comboBoxOriginStop.getValue();
        boolean currentStopIsNotDestination = currentStop != comboBoxDestinationStop.getValue();

        if (currentStopIsNotOrigin && currentStopIsNotDestination) {
            middleStops.add(currentStop);
        }
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        updateStopComboBox(comboBoxOriginStop);
        updateStopComboBox(comboBoxDestinationStop);
        updateCostTypeComboBox();

        handleButtonClick();
    }

    /**
     * Updates the view every time the subject notifies its observers.
     *
     * @param transportMap - The updated transport map.
     */
    @Override
    public void update(Object transportMap) {
        loadDynamicContent();
    }
}
