/******************************************************************************************************************************
 * File Name: Main.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller;

/**
 * Represents all transport map features available in the app.
 * Each transport map feature has a tab index and the name of the tab and bottom view files.
 */
public enum TransportMapFeature {
    /**
     * The transport map feature that shows the network base information.
     */
    NETWORK_INFORMATION(0, "networkInformationTab", "networkInformationBottom"),
    /**
     * The transport map feature that calculates an optimal path between stops.
     */
    CALCULATE_ROUTE(1, "calculateRouteTab", "calculateRouteBottom"),
    /**
     * The transport map feature that finds all stops that are as far as selected from a stop.
     */
    FIND_STOPS(2, "findStopsTab", "findStopsBottom"),
    /**
     * The transport map feature that allows the design of a route.
     */
    DESIGN_ROUTE(3, "designRouteTab", "designRouteBottom"),
    /**
     * The transport map feature that updates the bicycle duration of a route.
     */
    UPDATE_BICYCLE(4, "updateBicycleTab", "updateBicycleBottom"),
    /**
     * The transport map feature that deactivates a specific transport mode in a route.
     */
    DEACTIVATE_ROUTE(5, "deactivateRouteTab", "deactivateRouteBottom"),
    /**
     * The transport map feature that shows the logs of the user actions.
     */
    LOGS(6, "logsTab", "logsBottom"),
    /**
     * The transport map feature that presents the application settings..
     */
    SETTINGS(7, "settingsTab", "settingsBottom");

    /**
     * Represents the index that each tab will have.
     */
    private final int index;
    /**
     * Represents the fxml name of the corresponding tab menu file.
     */
    private final String fxmlTabName;
    /**
     * Represents the fxml name of the corresponding tab menu file.
     */
    private final String fxmlBottomName;

    /**
     * Constructs a TransportMapFeature enum constant with a specific tab index and FXML file names for both the tab and bottom menu.
     *
     * @param index          The index of the tab.
     * @param fxmlTabName    The name of the FXML file associated with the tab.
     * @param fxmlBottomName The name of the FXML file associated with the bottom menu.
     */
    TransportMapFeature(int index, String fxmlTabName, String fxmlBottomName) {
        this.index = index;
        this.fxmlTabName = fxmlTabName;
        this.fxmlBottomName = fxmlBottomName;
    }

    /**
     * Returns the index of the tab.
     *
     * @return the index of the tab.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Returns the FXML file name associated with the tab of the feature.
     *
     * @return The FXML file name of the tab.
     */
    public String getFxmlTabName() {
        return fxmlTabName;
    }

    /**
     * Returns the FXML file name associated with the bottom menu of the feature.
     *
     * @return The FXML file name of the bottom menu.
     */
    public String getFxmlBottomName() {
        return fxmlBottomName;
    }

    /**
     * Returns the FXML file name for the tab corresponding to the specified index.
     *
     * @param index The index of the tab.
     * @return The FXML file name of the tab at the specified index.
     */
    public static String getFxmlTabName(int index) {
        TransportMapFeature transportMapFeature = getTransportMapFeature(index);

        return transportMapFeature.getFxmlTabName();
    }

    /**
     * Returns the FXML file name for the bottom menu corresponding to the specified index.
     *
     * @param index The index of the tab.
     * @return The FXML file name of the bottom menu at the specified index.
     */
    public static String getFxmlBottomName(int index) {
        TransportMapFeature transportMapFeature = getTransportMapFeature(index);

        return transportMapFeature.getFxmlBottomName();
    }

    /**
     * Returns the TransportMapFeature enum with a specified index.
     *
     * @param index The index of the Feature Tab.
     * @return The TransportMapFeature with the received index, or the default one.
     */
    public static TransportMapFeature getTransportMapFeature(int index) {
        TransportMapFeature[] transportMapFeatures = TransportMapFeature.values();

        if (index < 0 || index >= transportMapFeatures.length) {
            return getDefaultTransportMapFeature();
        }

        return transportMapFeatures[index];
    }

    /**
     * Gets the default transport map feature.
     *
     * @return The default {@code TransportMapFeature} entry.
     */
    public static TransportMapFeature getDefaultTransportMapFeature() {
        return TransportMapFeature.NETWORK_INFORMATION;
    }
}

