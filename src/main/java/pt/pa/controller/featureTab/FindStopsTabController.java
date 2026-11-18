/******************************************************************************************************************************
 * File Name: FindStopsTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import com.brunomnsilva.smartgraph.graph.Vertex;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.findStops.FindStopsCommand;
import pt.pa.patterns.command.concrete.findStops.SelectOriginStopCommand;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;

import static pt.pa.ui.DisplayHelper.addErrorStyling;
import static pt.pa.ui.DisplayHelper.removeErrorStyling;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the FindStopsTab view.
 */
public class FindStopsTabController extends TransportBaseController implements FeatureTabController, Observer {
    /**
     * Represents the background pane of the find stops tab.
     */
    @FXML
    private Pane tabFindStopsPane;

    /**
     * Represents the Combo Box used to select the origin stop.
     */
    @FXML
    private ComboBox<Vertex<Stop>> comboBoxOriginStop;

    /**
     * Represents the Text Field to choose the number of stops.
     */
    @FXML
    private TextField textFieldNumberOfStops;

    /**
     * Represents the Button used to try to find stops from a distance.
     */
    @FXML
    private Button buttonFindStops;

    /**
     * Constructs a FindStopsTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public FindStopsTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the find stops tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);
        loadDynamicContent();

        initializeComboBox();
        handleButtonClick();
    }

    /**
     * Initialized the origin stop combo box.
     * Sets up a string converted for the stop name, and sets a trigger to notify the base controller when a new origin is selected.
     */
    private void initializeComboBox() {
        setOriginComboBoxConverter();
        setOriginComboBoxTriggers();
    }

    /**
     * Sets the origin combo box trigger to perform a user action upon selection.
     */
    private void setOriginComboBoxTriggers() {
        comboBoxOriginStop.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            UserActionLogger.getInstance().performUserAction(new SelectOriginStopCommand(baseController, newValue));
        });
    }

    /**
     * Sets the origin combo box string converter.
     */
    private void setOriginComboBoxConverter() {
        comboBoxOriginStop.setConverter(new StringConverter<>() {
            @Override
            public String toString(Vertex<Stop> stopVertex) {
                return stopVertex.element().toString();
            }

            @Override
            public Vertex<Stop> fromString(String string) {
                return comboBoxOriginStop.getItems().stream()
                        .filter(vertex -> vertex.element().toString()
                                .equals(string))
                        .findFirst()
                        .orElse(null);
            }
        });
    }

    /**
     * Handles the button click event by reading the inputs.
     */
    private void handleButtonClick() {
        buttonFindStops.setOnMouseClicked(event -> {
            Vertex<Stop> selectedOrigin = comboBoxOriginStop.getValue();
            String numberOfStops = textFieldNumberOfStops.getText();
            handleFindStops(selectedOrigin, numberOfStops);
        });
    }

    /**
     * Handles the find stops feature by receiving the selected origin and number of stops.
     * The inputs come from the errorHandler, and when an error is found, a control message is shown.
     *
     * @param selectedOrigin The origin selected by the user.
     * @param numberOfStops  The number of stops selected by the user.
     */
    private void handleFindStops(Vertex<Stop> selectedOrigin, String numberOfStops) {
        Pair<Vertex<Stop>, Integer> inputValues;

        inputValues = validateInputValues(selectedOrigin, numberOfStops);
        if (inputValues == null) return;

        Vertex<Stop> origin = inputValues.getValue0();
        int distance = inputValues.getValue1();

        UserActionLogger.getInstance().performUserAction(new FindStopsCommand(baseController, origin, distance, transportMap));
    }

    /**
     * Validates the input values, and shows a control notification message when a problem is found.
     *
     * @param selectedOrigin The selected origin stop.
     * @param numberOfStops  The number of stops to look for.
     * @return A pair with the processed data, or null if an error was found.
     */
    private Pair<Vertex<Stop>, Integer> validateInputValues(Vertex<Stop> selectedOrigin, String numberOfStops) {
        Pair<Vertex<Stop>, Integer> inputValues;
        try {
            inputValues = errorHandler(selectedOrigin, numberOfStops);
        } catch (IllegalArgumentException e) {
            ControlNotification.show(
                    getStringResource("findStopsTabController.notification.title"),
                    e.getMessage(), ControlNotification.NotificationType.ERROR);
            return null;
        }
        return inputValues;
    }

    /**
     * Handles any possible errors caused by the user, and applies style classes to the problematic inputs.
     *
     * @param selectedOrigin The origin selected by the user.
     * @param numberOfStops  The number of stops selected by the user.
     * @return A pair with the selected stop and the number of stops.
     */
    private Pair<Vertex<Stop>, Integer> errorHandler(Vertex<Stop> selectedOrigin, String numberOfStops) {
        checkOriginStop(selectedOrigin);
        checkNumberOfStopsIsEmpty(numberOfStops);
        int integerNumberOfStops = getIntegerNumberOfStops(numberOfStops);

        return new Pair<>(selectedOrigin, integerNumberOfStops);
    }

    /**
     * If the provided string number of stops is indeed an integer, returns the
     * integer value of the number of stops.
     *
     * @param numberOfStops The string value of the number of stops.
     * @return The converted number of stops.
     * @throws IllegalArgumentException If the string was not convertable to integer.
     */
    private int getIntegerNumberOfStops(String numberOfStops) {
        int integerNumberOfStops;

        try {
            integerNumberOfStops = Integer.parseInt(numberOfStops);
        } catch (NumberFormatException e) {
            addErrorStyling(textFieldNumberOfStops);
            throw new IllegalArgumentException(getStringResource("findStopsTabController.notification.message3"));
        }

        removeErrorStyling(textFieldNumberOfStops);
        return integerNumberOfStops;
    }

    /**
     * Checks if the number of stops string was, in fact, empty.
     *
     * @param numberOfStops The string number of stops.
     * @throws IllegalArgumentException If the string was empty.
     */
    private void checkNumberOfStopsIsEmpty(String numberOfStops) {
        if (numberOfStops.isEmpty()) {
            addErrorStyling(textFieldNumberOfStops);
            throw new IllegalArgumentException(getStringResource("findStopsTabController.notification.message2"));
        }
    }

    /**
     * Checks if the selected origin is not null.
     *
     * @param selectedOrigin The selected origin.
     * @throws IllegalArgumentException If the origin is null.
     */
    private void checkOriginStop(Vertex<Stop> selectedOrigin) {
        if (selectedOrigin == null) {
            addErrorStyling(comboBoxOriginStop);
            throw new IllegalArgumentException(getStringResource("findStopsTabController.notification.message1"));
        }

        removeErrorStyling(comboBoxOriginStop);
    }

    /**
     * Loads all the dynamic content from the transport map model into the view.
     * Should be called during initialization and update.
     */
    private void loadDynamicContent() {
        updateComboBoxOriginStops();
    }

    /**
     * Updated the origin stops present in the combo box with the stops sorted.
     */
    private void updateComboBoxOriginStops() {
        comboBoxOriginStop.getItems().clear();
        comboBoxOriginStop.getItems().addAll(transportMap.getStopsOrderedByName());
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
