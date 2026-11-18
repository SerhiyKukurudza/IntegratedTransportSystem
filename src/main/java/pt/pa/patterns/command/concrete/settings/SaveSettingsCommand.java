/******************************************************************************************************************************
 * File Name: SaveSettingsCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.settings;

import pt.pa.controller.BaseController;
import pt.pa.controller.featureTab.SettingsTabController;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that selects the stop origin at the find stops feature tab.
 */
public class SaveSettingsCommand extends UserActionCommand {
    /**
     * Represents the base controller at which the user action will be performed.
     */
    private final BaseController baseController;

    /**
     * Represents the map mode the user chose.
     */
    private final SettingsTabController.MapMode mapMode;

    /**
     * Represents the new language option the user chose.
     */
    private final SettingsTabController.LanguageOption languageOption;

    /**
     * Constructs an instance of the SaveSettingsCommand.
     *
     * @param baseController The controller where the action will be performed.
     * @param mapMode        The map mode to use to update the application's settings.
     * @param languageOption The new language option to update the application's settings.
     */
    public SaveSettingsCommand(BaseController baseController, SettingsTabController.MapMode mapMode, SettingsTabController.LanguageOption languageOption) {
        super(getStringResource("userActionDescription.saveSettings.saveSettings"), mapMode.getName());

        this.baseController = baseController;
        this.mapMode = mapMode;
        this.languageOption = languageOption;
    }

    /**
     * Executes the command to save the updated settings.
     */
    @Override
    public void execute() {
        baseController.notifyUpdatedSettings(mapMode, languageOption);
    }
}
