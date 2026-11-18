/******************************************************************************************************************************
 * File Name: AbstractResourceBundleManager.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.ui.resources;

import java.util.Locale;

/**
 * Abstract base class for managing resource bundles with support for multiple locales.
 * Provides common functionality for updating locales, retrieving strings, and managing
 * resource bundles shared across different implementations (JavaFX and FXML).
 */
public abstract class AbstractResourceBundleManager {
    /**
     * The instance of (multiple) resource bundle.
     */
    private MultipleResourceBundle resourceBundle;

    /**
     * Constructs the resource bundle manager with a default locale.
     *
     * @param stringsFilePath The file path for the main resource bundle.
     * @param additionalPaths Additional file paths for supplemental resource bundles.
     */
    protected AbstractResourceBundleManager(String stringsFilePath, String... additionalPaths) {
        resourceBundle = new MultipleResourceBundle(stringsFilePath, additionalPaths);
    }

    /**
     * Retrieves the current resource bundle being managed.
     *
     * @return The current {@link MultipleResourceBundle}.
     */
    public MultipleResourceBundle getResourceBundle() {
        return resourceBundle;
    }

    /**
     * Retrieves the currently selected locale.
     *
     * @return The current {@link Locale}.
     */
    public Locale getCurrentLocale() {
        return resourceBundle.getLocale();
    }

    /**
     * Updates the locale of the resource bundle.
     * Re-initializes the resource bundle if the locale is different.
     *
     * @param newLocale the new locale to apply.
     * @return {@code True} if the locale was updated, {@code false} if the locale remains the same.
     */
    public boolean updateLocale(Locale newLocale) {
        if (newLocale.equals(getCurrentLocale())) {
            return false;
        }

        resourceBundle = new MultipleResourceBundle(
                newLocale,
                resourceBundle.getStringsFilePath(),
                resourceBundle.getAdditionalPaths()
        );
        return true;
    }

    /**
     * Retrieves a string value associated with the given key from the resource bundle.
     * Used later for translation purposes.
     *
     * @param key The key for the desired string.
     * @return The string value associated with the key.
     */
    public String getString(String key) {
        return resourceBundle.getString(key);
    }
}
