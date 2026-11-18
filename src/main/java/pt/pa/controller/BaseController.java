/******************************************************************************************************************************
 * File Name: BaseController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller;

import com.brunomnsilva.smartgraph.graph.Edge;
import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;
import org.javatuples.Quartet;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import pt.pa.controller.bottomDetails.BottomDetailsController;
import pt.pa.controller.bottomDetails.BottomRouteDetailsController;
import pt.pa.controller.bottomDetails.BottomStopDetailsController;
import pt.pa.controller.featureBottom.*;
import pt.pa.controller.featureTab.*;
import pt.pa.model.*;
import pt.pa.model.Route;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.model.TransportMetrics;
import pt.pa.patterns.command.UserActionCommand;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;
import pt.pa.ui.resources.FXMLResourceBundle;
import pt.pa.ui.resources.StringsResourceBundle;
import pt.pa.utils.MainSceneLoader;
import pt.pa.utils.TransportColor;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class of the base layout with the map, left tab menu, and bottom menu.
 */
public class BaseController {
    /**
     * Represents the name of the file where the bottom route details view is.
     */
    public static final String BOTTOM_ROUTE_DETAILS_NAME = "bottomRouteDetails.fxml";

    /**
     * Represents the name of the file where the bottom stop details view is.
     */
    public static final String BOTTOM_STOP_DETAILS_NAME = "bottomStopDetails.fxml";

    /**
     * Represents the fxml element of the tab pane.
     */
    @FXML
    private TabPane tabPane;

    /**
     * Represents the list of panes from each tab.
     */
    private List<Pane> tabPanes;

    /**
     * Represents the fxml element that will be occupied by the map view.
     */
    @FXML
    private Pane paneMap;

    /**
     * Represents the fxml element of the bottom menu.
     */
    @FXML
    private Pane paneBottom;

    /**
     * Represents the pane that will be used to show the details of a specified route.
     */
    @FXML
    private Pane paneBottomRouteDetails;

    /**
     * Represents the pane that will be used to show the details of a specified stop.
     */
    @FXML
    private Pane paneBottomStopDetails;

    /**
     * Represents the transport map used in the application.
     */
    private final TransportMap transportMap;

    /**
     * Represents the controller of the map view integrated with the SmartGraph.
     * Used to retrieve the map view and communicate between the base controller components.
     */
    private final MapController mapController;

    /**
     * Represents a list of every feature tab controller.
     * Used to communicate between the base controller components.
     */
    private final List<FeatureTabController> featureTabControllers;

    /**
     * Represents a list of every feature bottom controller.
     * Used to communicate between the base controller components.
     */
    private final Map<Integer, FeatureBottomController> featureBottomControllers;

    /**
     * Represents the controller of the bottom route details menu.
     */
    private BottomRouteDetailsController bottomRouteDetailsController;

    /**
     * Represents the controller of the bottom stop details menu.
     */
    private BottomStopDetailsController bottomStopDetailsController;

    /**
     * Constructs a BaseController instance with a map view.
     *
     * @param mapController The controller of the map view.
     * @param transportMap  The transport map model.
     */
    public BaseController(MapController mapController, TransportMap transportMap) {
        this.mapController = mapController;
        this.transportMap = transportMap;
        this.featureTabControllers = new ArrayList<>();
        this.featureBottomControllers = new HashMap<>();
    }

    /**
     * Initializes every aspect of the base view.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    public void initialize() {
        initializeBaseComponents();

        Platform.runLater(this::executeDelayedActions);
    }

    /**
     * Initialized all base components of the base controller.
     */
    private void initializeBaseComponents() {
        initializeMapView();
        initializeTabPanes();
        initializeBottomPane();
        initializeBottomDetailsPane();

        initializeTriggers();
    }

    /**
     * Executes all actions that need to be executed only after the base components
     * have completely loaded.
     */
    private void executeDelayedActions() {
        updateTabPanes();
        showNotificationOfTheDay();
        initializeClickObservers();
    }

    /**
     * Sets a new pane over an old pane in the fxml file.
     *
     * @param oldPane The old pane that will be overlapped.
     * @param newPane The new pane that will now be visible.
     */
    private void setPane(Pane oldPane, Pane newPane) {
        if (oldPane == null || newPane == null || oldPane.getParent() == null) {
            return;
        }

        Pane parent = (Pane) oldPane.getParent();
        int index = parent.getChildren().indexOf(oldPane);
        parent.getChildren().set(index, newPane);
    }

    /**
     * Initializes the pane map by putting the map view in its place.
     */
    private void initializeMapView() {
        setPane(paneMap, mapController.getMapView());
    }

    /**
     * Initializes the list of the tab panes that will hold the different features.
     * This method finds the feature pane from each tab in the tab pane.
     * The search condition is the style class "tab-feature-pane"
     */
    private void initializeTabPanes() {
        tabPanes = new ArrayList<>();

        for (Tab tab : tabPane.getTabs()) {
            if (tab.getContent() instanceof Pane pane) {
                if (pane.getStyleClass().contains("tab-feature-pane")) {
                    tabPanes.add(pane);
                }
            }
        }
    }

    /**
     * Initializes the bottom pane by loading the default bottom FXML view.
     * Currently, loads the "networkInformationBottom.fxml" file.
     */
    private void initializeBottomPane() {
        updateBottomPane(0);
    }

    /**
     * Initializes the route and stop details bottom panes.
     */
    private void initializeBottomDetailsPane() {
        bottomRouteDetailsController = new BottomRouteDetailsController(transportMap, this);
        initializeBottomDetailsPane(bottomRouteDetailsController, paneBottomRouteDetails, BOTTOM_ROUTE_DETAILS_NAME);

        bottomStopDetailsController = new BottomStopDetailsController(transportMap, this);
        initializeBottomDetailsPane(bottomStopDetailsController, paneBottomStopDetails, BOTTOM_STOP_DETAILS_NAME);
    }

    /**
     * Initialize a bottom details pane with its controller and its pane.
     *
     * @param controller The controller that will manage that pane component.
     * @param pane       The pane which will show some sort of details.
     * @param name       The name of the view file.
     */
    private void initializeBottomDetailsPane(BottomDetailsController controller, Pane pane, String name) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + name));
            loader.setResources(FXMLResourceBundle.getInstance().getResourceBundle());

            loader.setController(controller);

            setPane(pane, loader.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the correct controller according to the selected Feature Tab.
     *
     * @param featureTabIndex The index of the Feature Tab.
     */
    private FeatureTabController getFeatureTabPaneController(int featureTabIndex) {
        TransportMapFeature transportMapFeature = TransportMapFeature.getTransportMapFeature(featureTabIndex);

        switch (transportMapFeature) {
            case NETWORK_INFORMATION -> {
                return new NetworkInformationTabController(transportMap);
            }
            case CALCULATE_ROUTE -> {
                return new CalculateRouteTabController(transportMap, this);
            }
            case FIND_STOPS -> {
                return new FindStopsTabController(transportMap, this);
            }
            case DESIGN_ROUTE -> {
                return new DesignRouteTabController(transportMap, this);
            }
            case UPDATE_BICYCLE -> {
                return new UpdateBicycleTabController(transportMap, this);
            }
            case DEACTIVATE_ROUTE -> {
                return new DeactivateRouteTabController(transportMap, this);
            }
            case LOGS -> {
                return new LogsTabController(transportMap, this);
            }
            case SETTINGS -> {
                return new SettingsTabController(transportMap, this);
            }
        }

        return new NetworkInformationTabController(transportMap);
    }

    /**
     * Returns the corresponding bottom pane controller for the selected feature tab.
     *
     * @param featureTabIndex The index of the selected feature tab.
     * @return The corresponding FeatureBottomController instance.
     */
    private FeatureBottomController getBottomPaneController(int featureTabIndex) {
        TransportMapFeature transportMapFeature = TransportMapFeature.getTransportMapFeature(featureTabIndex);

        switch (transportMapFeature) {
            case NETWORK_INFORMATION -> {
                return new NetworkInformationBottomController(transportMap, this);
            }
            case CALCULATE_ROUTE -> {
                return new CalculateRouteBottomController(transportMap, this);
            }
            case FIND_STOPS -> {
                return new FindStopsBottomController(transportMap);
            }
            case DESIGN_ROUTE -> {
                return new DesignRouteBottomController(transportMap, this);
            }
            case UPDATE_BICYCLE -> {
                return new UpdateBicycleBottomController(transportMap, this);
            }
            case DEACTIVATE_ROUTE -> {
                return new DeactivateRouteBottomController(transportMap);
            }
            case LOGS -> {
                return new LogsBottomController(transportMap, this);
            }
            case SETTINGS -> {
                return new SettingsBottomController(transportMap, this);
            }
        }

        return new NetworkInformationBottomController(transportMap, this);
    }

    /**
     * Updates the current feature tab pane by the tab index.
     *
     * @param tabIndex The tab index to switch to.
     */
    private void updateFeatureTabPane(int tabIndex) {
        String fxmlFile = TransportMapFeature.getFxmlTabName(tabIndex) + ".fxml";
        Pane currentFeaturePane = tabPanes.get(tabIndex);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + fxmlFile));
            loader.setResources(FXMLResourceBundle.getInstance().getResourceBundle());
            FeatureTabController featureTabController = getFeatureTabPaneController(tabIndex);

            featureTabControllers.add(featureTabController);
            loader.setController(featureTabController);
            Pane loadedPane = loader.load();

            setPane(currentFeaturePane, loadedPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates the bottom pane content based on the selected tab index.
     *
     * @param tabIndex The index of the tab to update the bottom pane for.
     */
    private void updateBottomPane(int tabIndex) {
        String bottomFxmlFile = TransportMapFeature.getFxmlBottomName(tabIndex) + ".fxml";
        FeatureBottomController bottomPaneController = getBottomPaneController(tabIndex);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/" + bottomFxmlFile));
            loader.setResources(FXMLResourceBundle.getInstance().getResourceBundle());
            loader.setController(bottomPaneController);
            Pane loadedPane = loader.load();

            featureBottomControllers.put(tabIndex, bottomPaneController);
            setPane(paneBottom, loadedPane);
            paneBottom = loadedPane;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Initializes all triggers of the controlled view.
     */
    private void initializeTriggers() {
        tabPane.getSelectionModel().selectedIndexProperty().addListener((observable, oldTab, newTab) -> {
            updateTabSelection(newTab);
        });
    }

    /**
     * Performs all updates that depend on the change of the currently selected tab
     * in the tab pane.
     *
     * @param newTab The newly selected tab pane.
     */
    private void updateTabSelection(Number newTab) {
        int selectedTabIndex = newTab.intValue();
        updateBottomPane(selectedTabIndex);

        clearMapMarks();
        manageRouteIfManageable(selectedTabIndex);
        clearDesignRouteSelections();
    }

    /**
     * Clears the design route selections by finding its controller and casting it
     * to execute the related method.
     */
    private void clearDesignRouteSelections() {
        DesignRouteTabController designRouteTabController = (DesignRouteTabController)
                getFeatureTabController(TransportMapFeature.DESIGN_ROUTE);

        designRouteTabController.clearRouteAndStopSelection();
    }

    /**
     * Clears all stop and route marks from the map.
     */
    private void clearMapMarks() {
        mapController.doUnmarkStops();
        mapController.doUnmarkRoutes();
    }

    /**
     * Adds all controllers that need to be notified by the map controller click notification system.
     */
    private void initializeClickObservers() {
        mapController.addObservers((Observer) getFeatureTabController(TransportMapFeature.DESIGN_ROUTE));
        mapController.addObservers((Observer) getFeatureTabController(TransportMapFeature.UPDATE_BICYCLE));
        mapController.addObservers((Observer) getFeatureTabController(TransportMapFeature.DEACTIVATE_ROUTE));

        mapController.addObservers(bottomRouteDetailsController);
        mapController.addObservers(bottomStopDetailsController);
    }

    /**
     * Updates the static placeholder panes with the dynamic loaded view panes.
     */
    private void updateTabPanes() {
        for (int i = 0; i < tabPanes.size(); i++) {
            updateFeatureTabPane(i);
        }
    }

    /**
     * Shows a welcome notification with the ControlNotification.
     */
    private void showNotificationOfTheDay() {
        ControlNotification.show(
                getStringResource("baseController.notification.title"),
                getStringResource("baseController.notification.content"),
                ControlNotification.NotificationType.GREETING
        );
    }

    /**
     * Checks if a specified tab is selected.
     *
     * @param transportMapFeature The feature tab to check.
     * @return True if the specified tab is selected, false otherwise.
     */
    public boolean checkIfTabIsSelected(TransportMapFeature transportMapFeature) {
        return tabPane.getSelectionModel().getSelectedIndex() == transportMapFeature.getIndex();
    }

    /**
     * Performs some management operations if the selected feature tab is a route manager.
     *
     * @param tabIndex The index of the selected tab.
     */
    public void manageRouteIfManageable(int tabIndex) {
        FeatureTabController selectedTabController = featureTabControllers.get(tabIndex);

        if (selectedTabController instanceof RouteDetailManager selectionTab) {
            selectionTab.setupRouteManagement();
        }
    }

    /**
     * Notifies the map controller that an origin was selected.
     *
     * @param originStopVertex       The selected origin stop vertex.
     * @param unmarkDestinationStops A flag to unmark the destination stops if {@code true}.
     */
    public void notifySelectedOrigin(Vertex<Stop> originStopVertex, boolean unmarkDestinationStops) {
        mapController.doMarkOriginStop(originStopVertex, unmarkDestinationStops);
    }

    /**
     * Notifies the map controller that a destination was selected.
     *
     * @param destinationStopVertex The selected destination stop vertex.
     */
    public void notifySelectedDestination(Vertex<Stop> destinationStopVertex) {
        mapController.doMarkDestinationStop(destinationStopVertex);
    }

    /**
     * Notifies the map controller to visually update the map based on the calculated route details.
     * Marks the origin stop, destination stop, middle stops, and the path edges on the map.
     * This method is typically used after a route calculation is performed to display the route on the map.
     *
     * @param originStopVertex      the vertex representing the origin stop.
     * @param destinationStopVertex the vertex representing the destination stop.
     * @param middleStops           a list of vertices representing intermediate stops along the route.
     * @param pathEdges             a collection of triplets, where each triplet contains:
     *                              - The edge representing a route segment,
     *                              - A color associated with that segment (for display purposes),
     *                              - A double representing the segment thickness or weight.
     * @param totalTransportMetrics A transport metrics instance that was previously summed to become the total.
     */
    public void notifyCalculatedRoute(Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex,
                                      List<Vertex<Stop>> middleStops,
                                      Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges,
                                      TransportMetrics totalTransportMetrics) {
        CalculateRouteBottomController calculateRouteBottomController = (CalculateRouteBottomController)
                getFeatureBottomController(TransportMapFeature.CALCULATE_ROUTE);

        calculateRouteBottomController.showCalculatedRoute(originStopVertex, destinationStopVertex, middleStops, pathEdges, totalTransportMetrics);
        markCalculatedRouteOnMap(originStopVertex, destinationStopVertex, middleStops, pathEdges);
    }

    /**
     * Marks on the map all details of the last calculated route.
     *
     * @param originStopVertex      The origin stop vertex.
     * @param destinationStopVertex The destination stop vertex.
     * @param middleStops           The middle stop vertices.
     * @param pathEdges             All path edges between each pair of stops.
     */
    private void markCalculatedRouteOnMap(Vertex<Stop> originStopVertex, Vertex<Stop> destinationStopVertex, List<Vertex<Stop>> middleStops, Collection<Quartet<Edge<Route, Stop>, String, TransportColor, Double>> pathEdges) {
        mapController.doMarkOriginStop(originStopVertex, true);
        mapController.doMarkDestinationStop(destinationStopVertex);
        mapController.doMarkMiddleStopsWithDelay(middleStops);
        mapController.doMarkRoutesWithDelay(pathEdges);
    }

    /**
     * Notifies the map and bottom menu that a collection of destination stop vertices were found.
     *
     * @param originStopVertex        The origin stop vertex from which the destinations were calculated.
     * @param distance                The distance from the origin stop to each destination stop.
     * @param destinationStopVertices The destination stop vertices found.
     */
    public void notifyFoundStops(Vertex<Stop> originStopVertex, int distance, Collection<Vertex<Stop>> destinationStopVertices) {
        FindStopsBottomController findStopsBottomController = (FindStopsBottomController)
                getFeatureBottomController(TransportMapFeature.FIND_STOPS);

        findStopsBottomController.showFoundStops(originStopVertex.element().getName(), distance, destinationStopVertices);
        mapController.doMarkDestinationStops(destinationStopVertices);
    }

    /**
     * Notifies the map controller that a route has been selected.
     *
     * @param routeEdge The selected route edge.
     */
    public void notifySelectedRoute(Edge<Route, Stop> routeEdge) {
        Vertex<Stop>[] stopVertices = routeEdge.vertices();

        mapController.doMarkOriginStop(stopVertices[0], true);
        mapController.doMarkDestinationStop(stopVertices[1]);
    }

    /**
     * Notifies the map controller that a route has been unselected.
     */
    public void notifyUnselectedRoute() {
        mapController.doUnmarkStops();
    }

    /**
     * Notifies that the user as selected a command in the commands list.
     *
     * @param userAction The detailed user action.
     */
    public void notifySelectedCommand(Triplet<UserActionCommand, LocalDateTime, UserActionLogger.ActionExecutionState> userAction) {
        LogsBottomController logsBottomController = (LogsBottomController)
                getFeatureBottomController(TransportMapFeature.LOGS);

        logsBottomController.showCommandDetails(userAction);
    }

    /**
     * Notifies that the application settings were updated.
     * If the language was changed, the stage is reloaded.
     *
     * @param mapMode        The new map mode to update the transport map background image.
     * @param languageOption The new language option to use in the UI.
     */
    public void notifyUpdatedSettings(SettingsTabController.MapMode mapMode, SettingsTabController.LanguageOption languageOption) {
        mapController.doSetTransportMapBackgroundImage(mapMode.getPath());
        boolean languageUpdated = StringsResourceBundle.updateLocale(languageOption);
        languageUpdated = FXMLResourceBundle.updateLocale(languageOption) || languageUpdated;

        if (languageUpdated) {
            MainSceneLoader.setStage();
        }
    }

    /**
     * Notifies the map controller of all currently selected stops and the corresponding edges with the chosen transport.
     *
     * @param selectedStops  The list of selected stops.
     * @param selectedRoutes The list of selected edges with the preferred cost.
     */
    public void notifyDesignedRoute(List<Vertex<Stop>> selectedStops, List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes) {
        DesignRouteBottomController designRouteBottomController = (DesignRouteBottomController)
                getFeatureBottomController(TransportMapFeature.DESIGN_ROUTE);
        DesignRouteTabController designRouteTabController = (DesignRouteTabController)
                getFeatureTabController(TransportMapFeature.DESIGN_ROUTE);

        if (selectedStops.isEmpty()) {
            hideEmptyDesignedRoutes(designRouteTabController, designRouteBottomController);
            return;
        }

        showNotEmptyDesignedRoutes(selectedStops, selectedRoutes, designRouteBottomController, designRouteTabController);
    }

    /**
     * Gets the corresponding feature tab controller by its enum value.
     *
     * @param transportMapFeature The enum that represents the tab feature controller.
     * @return The feature tab controller.
     */
    private FeatureTabController getFeatureTabController(TransportMapFeature transportMapFeature) {
        return featureTabControllers.get(transportMapFeature.getIndex());
    }

    /**
     * Gets the corresponding feature bottom controller by its enum value.
     *
     * @param transportMapFeature The enum that represents the bottom feature controller.
     * @return The feature bottom controller.
     */
    private FeatureBottomController getFeatureBottomController(TransportMapFeature transportMapFeature) {
        return featureBottomControllers.get(transportMapFeature.getIndex());
    }

    /**
     * Hides all features that should be only shown when the design route is not empty.
     *
     * @param designRouteTabController    The design route feature tab controller.
     * @param designRouteBottomController The design route feature bottom controller.
     */
    private void hideEmptyDesignedRoutes(DesignRouteTabController designRouteTabController, DesignRouteBottomController designRouteBottomController) {
        clearMapMarks();

        designRouteTabController.hideUndo();
        designRouteBottomController.hideDesignedRoute();
    }

    /**
     * Shows all features of the design route and paints the map with the corresponding details.
     *
     * @param selectedStops               The selected stops in the design route feature.
     * @param selectedRoutes              The selected routes in the design route feature.
     * @param designRouteTabController    The design route feature tab controller.
     * @param designRouteBottomController The design route feature bottom controller.
     */
    private void showNotEmptyDesignedRoutes(List<Vertex<Stop>> selectedStops, List<Pair<Edge<Route, Stop>, CostType>> selectedRoutes, DesignRouteBottomController designRouteBottomController, DesignRouteTabController designRouteTabController) {
        Vertex<Stop> originStopVertex = selectedStops.get(0);
        List<Vertex<Stop>> followingStopVertices = selectedStops.subList(1, selectedStops.size());

        mapController.doMarkOriginStop(originStopVertex, true);
        mapController.doMarkMiddleStops(followingStopVertices);

        mapController.doMarkRoutes(selectedRoutes);

        designRouteBottomController.updateDynamicRouteDesign(selectedStops, selectedRoutes);
        designRouteTabController.showUndo();
    }
}
