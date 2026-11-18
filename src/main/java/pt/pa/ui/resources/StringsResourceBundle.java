/******************************************************************************************************************************
 * File Name: StringsResourceBundle.java
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
 * Singleton class for managing string resources using a resource bundle.
 * Provides functionality to retrieve localized strings and update the locale.
 * Used in translatable dynamic UI content.
 */
public class StringsResourceBundle extends AbstractResourceBundleManager {
    /**
     * Represents the resources file path for the string translations in the JavaFX UI.
     */
    private static final String STRINGS_FILE_PATH = "properties.strings";

    /**
     * Represents the singleton instance.
     */
    private static StringsResourceBundle instance;

    /**
     * Private constructor to initialize the resource bundle with the default file path.
     */
    private StringsResourceBundle() {
        super(STRINGS_FILE_PATH);
    }

    /**
     * Provides access to the singleton instance of the StringsResourceBundle.
     *
     * @return The single instance of {@link StringsResourceBundle}.
     */
    public static StringsResourceBundle getInstance() {
        if (instance == null) {
            instance = new StringsResourceBundle();
        }
        return instance;
    }

    /**
     * Retrieves the localized string associated with the given key.
     *
     * @param key The key for the desired string.
     * @return The localized string.
     */
    public static String getStringResource(String key) {
        return getInstance().getString(key);
    }

    /**
     * Updates the locale of the resource bundle based on the specified language option.
     *
     * @param languageOption The new language option to apply.
     * @return {@code true} If the locale was updated, {@code false} if the locale remains the same.
     */
    public static boolean updateLocale(LanguageOption languageOption) {
        Locale newLocale = LocaleFactory.create(languageOption);
        return getInstance().updateLocale(newLocale);
    }
}
