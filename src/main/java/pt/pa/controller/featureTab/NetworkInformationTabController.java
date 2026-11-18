/******************************************************************************************************************************
 * File Name: NetworkInformationTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.observer.Observer;

import java.util.List;
import java.util.Map;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the NetworkInformationTab view.
 */
public class NetworkInformationTabController extends TransportBaseController implements FeatureTabController, Observer {
    /**
     * Represents the background pane of the network information tab.
     */
    @FXML
    private Pane tabNetworkInformationPane;

    /**
     * Represents the number of stops label that will have its value updated.
     */
    @FXML
    private Label labelNumberOfStops;

    /**
     * Represents the number of isolated stops label that will have its value updated.
     */
    @FXML
    private Label labelNumberOfIsolatedStops;

    /**
     * Represents the number of not isolated stops label that will have its value updated.
     */
    @FXML
    private Label labelNumberOfNotIsolatedStops;

    /**
     * Represents the number of routes label that will have its value updated.
     */
    @FXML
    private Label labelNumberOfRoutes;

    /**
     * Represents the grid pane that shows the number of routes each transport mode is used at.
     */
    @FXML
    private GridPane gridPaneRoutesByTransport;

    /**
     * Table view that displays the full list of stops and their centralization values.
     */
    @FXML
    private TableView<Pair<Stop, Integer>> tableStops;

    /**
     * Column for displaying stop names in the stops table.
     */
    @FXML
    private TableColumn<Pair<Stop, Integer>, String> tableColumnStopName;

    /**
     * Column for displaying the number of connected routes in the stops table.
     */
    @FXML
    private TableColumn<Pair<Stop, Integer>, Integer> tableColumnConnectedRoutes;

    /**
     * Represents the bar chart for visualizing data.
     */
    @FXML
    private BarChart<String, Number> barChart;

    /**
     * Represents the category axis for the bar chart.
     */
    @FXML
    private CategoryAxis xAxis;

    /**
     * Represents the number axis for the bar chart.
     */
    @FXML
    private NumberAxis yAxis;

    /**
     * Represents the button that will toggle between the centralization table and graph.
     */
    @FXML
    private Button buttonToggleCentralization;

    /**
     * Constructs a NetworkInformationTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     */
    public NetworkInformationTabController(TransportMap transportMap) {
        super(transportMap);
    }

    /**
     * Initializes every aspect of the network information tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        loadDynamicContent();

        initializeBarChart();
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        setBasicNetworkInformation();

        loadNumberOfRoutesByTransport();
        loadCentralizationTable();
        loadStopCentralizationChart();

        handleButtonClick();
        turnTableVisible();
    }

    /**
     * Sets the basic network information on their respective labels.
     */
    private void setBasicNetworkInformation() {
        labelNumberOfStops.setText(String.valueOf(transportMap.numStops()));
        labelNumberOfIsolatedStops.setText(String.valueOf(transportMap.numIsolatedStops()));
        labelNumberOfNotIsolatedStops.setText(String.valueOf(transportMap.numStops() - transportMap.numIsolatedStops()));
        labelNumberOfRoutes.setText(String.valueOf(transportMap.numRoutes()));
    }

    /**
     * Loads a list of HBoxes with 2 labels each representing the transport mode name and the number of routes that transport is used.
     */
    private void loadNumberOfRoutesByTransport() {
        Map<String, Integer> numRoutesByTransport = transportMap.numRoutesByTransport();

        int rowIndex = 1;

        for (Map.Entry<String, Integer> transportCount : numRoutesByTransport.entrySet()) {
            if(!transportMap.transportModeIsUsed(transportCount.getKey())) continue;

            rowIndex = addTransportWithNumberOfRoutes(transportCount, rowIndex);
        }
    }

    /**
     * Adds a transport mode with the number of routes in the routes by transport grid.
     *
     * @param transportCount A map with the name of the transport mode and the number of routes.
     * @param rowIndex       The current row index for the grid.
     * @return The next row index.
     */
    private int addTransportWithNumberOfRoutes(Map.Entry<String, Integer> transportCount, int rowIndex) {
        String transportLabel = getStringResource("networkInformationTabController.numRouteByTransport.name");

        Label labelTransportName = new Label(transportLabel + " " + getStringResource("transportName." + transportCount.getKey().toLowerCase()) + ": ");
        labelTransportName.getStyleClass().add("tab-text");
        Label labelTransportCount = new Label(String.valueOf(transportCount.getValue()));
        labelTransportCount.getStyleClass().addAll("tab-text", "tab-text-bold");

        gridPaneRoutesByTransport.add(labelTransportName, 0, rowIndex);
        gridPaneRoutesByTransport.add(labelTransportCount, 1, rowIndex);

        return ++rowIndex;
    }

    /**
     * Loads a column graph with the centralization of the top 5 stops in the transportation network.
     */
    private void loadStopCentralizationChart() {
        List<Pair<Stop, Integer>> topStops = transportMap.topCentralizedStops();

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1.setName(getStringResource("networkInformationTabController.series1.name"));

        for (Pair<Stop, Integer> pair : topStops) {
            series1.getData().add(new XYChart.Data<>(pair.getValue0().getName(), pair.getValue1()));
        }

        barChart.getData().add(series1);
    }

    /**
     * Initializes the bar chart title and labels.
     */
    private void initializeBarChart() {
        barChart.setTitle(getStringResource("networkInformationTabController.barChar.title"));
        xAxis.setLabel(getStringResource("networkInformationTabController.xAxis.label"));
        yAxis.setLabel(getStringResource("networkInformationTabController.yAxis.label"));
    }

    /**
     * Loads a table with the centralization of every stop vertex in the transport map.
     */
    private void loadCentralizationTable() {
        List<Pair<Stop, Integer>> centralizationList = transportMap.stopCentralization();
        ObservableList<Pair<Stop, Integer>> tableData = FXCollections.observableArrayList(centralizationList);

        tableColumnStopName.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getValue0().getName()));
        tableColumnConnectedRoutes.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getValue1()).asObject());
        tableStops.setItems(tableData);
    }

    /**
     * Handles the click of the toggle button for the centralization graph and table.
     */
    private void handleButtonClick() {
        buttonToggleCentralization.setOnMouseClicked(event -> {
            if (tableStops.isVisible()) {
                turnChartVisible();
            } else {
                turnTableVisible();
            }
        });
    }

    /**
     * Turns the chart visible, the table invisible and the button text is updated.
     */
    private void turnChartVisible() {
        barChart.setVisible(true);
        barChart.setManaged(true);

        tableStops.setVisible(false);
        tableStops.setManaged(false);

        buttonToggleCentralization.setText(getStringResource("networkInformationTabController.button.showTable"));
    }

    /**
     * Turns the table visible, the graph invisible and the button text is updated.
     */
    private void turnTableVisible() {
        tableStops.setVisible(true);
        tableStops.setManaged(true);

        barChart.setVisible(false);
        barChart.setManaged(false);

        buttonToggleCentralization.setText(getStringResource("networkInformationTabController.button.showChart"));
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
