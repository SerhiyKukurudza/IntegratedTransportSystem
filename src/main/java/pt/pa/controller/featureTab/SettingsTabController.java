/******************************************************************************************************************************
 * File Name: SettingsTabController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureTab;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.Pane;
import javafx.util.StringConverter;
import org.javatuples.Pair;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionLogger;
import pt.pa.patterns.command.concrete.settings.SaveSettingsCommand;
import pt.pa.patterns.observer.Observer;
import pt.pa.ui.ControlNotification;

import static pt.pa.ui.DisplayHelper.addErrorStyling;
import static pt.pa.ui.DisplayHelper.removeErrorStyling;
import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents the controller class for the SettingsTab view.
 */
public class SettingsTabController extends TransportBaseController implements FeatureTabController, Observer {

    /**
     * Represents the different map modes, with the map file path and the String representing it in its respective locale.
     */
    public enum MapMode {
        /**
         * Represents the terrain map background image .
         */
        TERRAIN("images/terrain.png", getStringResource("settingsTabController.map.terrain")),

        /**
         * Represents the realistic map background image.
         */
        MAP("images/map.png", getStringResource("settingsTabController.map.realistic")),

        /**
         * Represents the satellite map background image.
         */
        SATELLITE("images/satellite.png", getStringResource("settingsTabController.map.satellite")),

        /**
         * Represents the dark map background image.
         */
        DARK("images/dark.png", getStringResource("settingsTabController.map.dark"));

        /**
         * Represents the file path of the corresponding map mode.
         */
        private final String path;
        /**
         * Represents the name of the corresponding map mode to show in the UI.
         */
        private final String name;

        /**
         * Constructs a map mode instance.
         *
         * @param path The file path of the map background image.
         * @param name The name of the map background image.
         */
        MapMode(String path, String name) {
            this.path = path;
            this.name = name;
        }

        /**
         * Gets the map mode path.
         *
         * @return The String path.
         */
        public String getPath() {
            return path;
        }

        /**
         * Gets the name of the map made.
         *
         * @return The name.
         */
        public String getName() {
            return name;
        }
    }

    /**
     * Represents the possible language options of the application.
     */
    public enum LanguageOption {
        /**
         * Represents the english language option.
         */
        ENGLISH(getStringResource("settingsTabController.language.en")),
        /**
         * Represents the portuguese language option.
         */
        PORTUGUESE(getStringResource("settingsTabController.language.pt"));

        /**
         * Represents the name that is shown in the UI.
         */
        private final String name;

        /**
         * Constructs a language option instance.
         *
         * @param name The name of the language.
         */
        LanguageOption(String name) {
            this.name = name;
        }

        /**
         * Gets the language option by name.
         *
         * @param name The name of the language option.
         * @return The found language option or {@code null}.
         */
        private static LanguageOption getLanguageOptionByName(String name) {
            for (LanguageOption languageOption : LanguageOption.values()) {
                if (name.equals(languageOption.name)) {
                    return languageOption;
                }
            }

            return null;
        }
    }

    /**
     * Represents the background pane of the settings tab.
     */
    @FXML
    private Pane tabSettingsPane;

    /**
     * Represents the combo box for the map mode.
     */
    @FXML
    private ComboBox<MapMode> comboBoxMapMode;

    /**
     * Represents the combo box for the application language.
     */
    @FXML
    private ComboBox<LanguageOption> comboBoxLanguage;

    /**
     * Represents the button to save the application settings.
     */
    @FXML
    private Button buttonUpdateSettings;

    /**
     * Constructs a SettingsTabController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public SettingsTabController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the settings tab view.
     * This method is executed during the initialization phase of the controller.
     * <p>
     * It will also observe the transport map.
     * </p>
     */
    @FXML
    private void initialize() {
        transportMap.addObservers(this);

        initializeComboBoxes();
        handleButtonClick();
    }

    /**
     * Initializes all combo boxes in the settings tab.
     */
    private void initializeComboBoxes() {
        setComboBoxValues();
        setDefaultComboBoxOptions();
        setComboBoxDisplayNames();
    }

    /**
     * Sets the values of every combo box in the settings tab.
     */
    private void setComboBoxValues() {
        comboBoxMapMode.getItems().addAll(MapMode.values());
        comboBoxLanguage.getItems().addAll(LanguageOption.values());
    }

    /**
     * Sets the default options of every combo box in the settings tab.
     */
    private void setDefaultComboBoxOptions() {
        comboBoxMapMode.getSelectionModel().select(MapMode.TERRAIN);
        comboBoxLanguage.getSelectionModel().select(LanguageOption.ENGLISH);
    }

    /**
     * Sets the display names with a converter for every combo box in the settings tab.
     */
    private void setComboBoxDisplayNames() {
        setMapComboBoxConverter();
        setLanguageComboBoxConverter();
    }

    /**
     * Sets the string name converter for the language combo box.
     */
    private void setLanguageComboBoxConverter() {
        comboBoxLanguage.setConverter(new StringConverter<>() {
            @Override
            public String toString(LanguageOption languageOption) {
                return languageOption.name;
            }

            @Override
            public LanguageOption fromString(String name) {
                return LanguageOption.getLanguageOptionByName(name);
            }
        });
    }

    /**
     * Sets the string name converter for the map combo box.
     */
    private void setMapComboBoxConverter() {
        comboBoxMapMode.setConverter(new StringConverter<>() {
            @Override
            public String toString(MapMode mapMode) {
                return mapMode.name;
            }

            @Override
            public MapMode fromString(String name) {
                return MapMode.valueOf(name);
            }
        });
    }

    /**
     * Handles the click action of the save changes button.
     */
    private void handleButtonClick() {
        buttonUpdateSettings.setOnAction(event -> {
            MapMode selectedMapMode = comboBoxMapMode.getSelectionModel().getSelectedItem();
            LanguageOption selectedLanguage = comboBoxLanguage.getSelectionModel().getSelectedItem();
            handleSaveChanges(selectedMapMode, selectedLanguage);
        });
    }

    /**
     * Handles the action of saving the application settings changes.
     *
     * @param selectedMapMode  The selected map mode.
     * @param selectedLanguage The selected language.
     */
    private void handleSaveChanges(MapMode selectedMapMode, LanguageOption selectedLanguage) {
        Pair<MapMode, LanguageOption> inputValues = validateInputValues(selectedMapMode, selectedLanguage);
        if (inputValues == null) return;

        MapMode mapMode = inputValues.getValue0();
        LanguageOption languageOption = inputValues.getValue1();

        UserActionLogger.getInstance().performUserAction(new SaveSettingsCommand(baseController, mapMode, languageOption));
    }

    /**
     * Validates the provided inputs values and shows a control notification if a problem was found.
     *
     * @param selectedMapMode  The selected map mode.
     * @param selectedLanguage The selected language.
     * @return The inputs values is they were valid, null otherwise.
     */
    private Pair<MapMode, LanguageOption> validateInputValues(MapMode selectedMapMode, LanguageOption selectedLanguage) {
        Pair<MapMode, LanguageOption> inputValues;

        try {
            inputValues = errorHandler(selectedMapMode, selectedLanguage);
        } catch (IllegalArgumentException e) {
            ControlNotification.show(
                    getStringResource("settingsTabController.notification.title"),
                    e.getMessage(),
                    ControlNotification.NotificationType.ERROR
            );
            return null;
        }

        return inputValues;
    }

    /**
     * Handles any error that the user might provoke when selecting the user settings.
     *
     * @param selectedMapMode  The selected map mode.
     * @param selectedLanguage The selected language.
     * @return A pair with the valid inputs.
     * @throws IllegalArgumentException If any of the inputs are null
     */
    private Pair<MapMode, LanguageOption> errorHandler(MapMode selectedMapMode, LanguageOption selectedLanguage) {
        checkSelectedMapMode(selectedMapMode);
        checkLanguage(selectedLanguage);

        return new Pair<>(selectedMapMode, selectedLanguage);
    }

    /**
     * Checks if the provided language is not null.
     * Removes any previously applied error styles.
     *
     * @param selectedLanguage The selected language.
     * @throws IllegalArgumentException If the language is null.
     */
    private void checkLanguage(LanguageOption selectedLanguage) {
        if (selectedLanguage == null) {
            addErrorStyling(comboBoxLanguage);
            throw new IllegalArgumentException(getStringResource("settingsTabController.notification.message2"));
        }

        removeErrorStyling(comboBoxLanguage);
    }

    /**
     * Checks if the selected map mode is null.
     * Removes any previously applied error styles.
     *
     * @param selectedMapMode The selected map mode.
     * @throws IllegalArgumentException If the selected map mode is null.
     */
    private void checkSelectedMapMode(MapMode selectedMapMode) {
        if (selectedMapMode == null) {
            addErrorStyling(comboBoxMapMode);
            throw new IllegalArgumentException(getStringResource("settingsTabController.notification.message1"));
        }

        removeErrorStyling(comboBoxMapMode);
    }

    /**
     * Updates the view every time the subject notifies its observers.
     *
     * @param transportMap - The updated transport map.
     */
    @Override
    public void update(Object transportMap) {
    }
}
