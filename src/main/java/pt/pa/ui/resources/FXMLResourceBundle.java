/******************************************************************************************************************************
 * File Name: FXMLResourceBundle.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.ui.resources;

import pt.pa.controller.featureTab.SettingsTabController.LanguageOption;
import pt.pa.patterns.factory.simple.LocaleFactory;

import java.util.Locale;

/**
 * Singleton class for managing both string and dimension resources for the UI.
 * Provides functionality to update the locale and access localized resources.
 * Used in the FXML views.
 */
public class FXMLResourceBundle extends AbstractResourceBundleManager {
    /**
     * Represents the resources file for the content translation in the FXML views.
     */
    private static final String STRINGS_PATH = "views.properties.strings";

    /**
     * Represents the resources file for all dimensions in the FXML views.
     */
    private static final String DIMENSIONS_PATH = "views.properties.dimensions";

    /**
     * Represents the singleton instance.
     */
    private static FXMLResourceBundle instance;

    /**
     * Private constructor to initialize the resource bundle with string and dimension paths.
     */
    private FXMLResourceBundle() {
        super(STRINGS_PATH, DIMENSIONS_PATH);
    }

    /**
     * Provides access to the singleton instance of the FXMLResourceBundle.
     *
     * @return The single instance of {@link FXMLResourceBundle}.
     */
    public static FXMLResourceBundle getInstance() {
        if (instance == null) {
            instance = new FXMLResourceBundle();
        }
        return instance;
    }

    /**
     * Updates the locale of the resource bundle based on the specified language option.
     *
     * @param languageOption The new language option to apply.
     * @return {@code true} Tf the locale was updated, {@code false} if the locale remains the same.
     */
    public static boolean updateLocale(LanguageOption languageOption) {
        Locale newLocale = LocaleFactory.create(languageOption);
        return getInstance().updateLocale(newLocale);
    }
}

