/******************************************************************************************************************************
 * File Name: MultipleResourceBundle.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.ui.resources;

import java.util.*;

/**
 * A resource bundle that combines multiple resource files into one.
 * Supports localization and provides access to resources from multiple paths.
 */
public class MultipleResourceBundle extends ResourceBundle {
    /**
     * Represents the combined resource bundles map.
     */
    private Map<String, Object> combinedMap;

    /**
     * Represents the current locale of this resource bundle.
     */
    private final Locale locale;

    /**
     * Represents the translated strings file path.
     */
    private final String stringsFilePath;

    /**
     * Represent all the other additional resource bundle file paths.
     * Dimensions is one example.
     */
    private final String[] additionalPaths;

    /**
     * Represents the default locale used as fall-back for every string translation.
     */
    private static final Locale DEFAULT_LOCALE = Locale.ENGLISH;

    /**
     * Creates a resource bundle with the default locale.
     *
     * @param stringsFilePath the main resource file path.
     * @param paths           additional resource file paths.
     */
    public MultipleResourceBundle(String stringsFilePath, String... paths) {
        this(DEFAULT_LOCALE, stringsFilePath, paths);
    }

    /**
     * Creates a resource bundle with a specified locale.
     *
     * @param locale          The locale to use for the resource bundle.
     * @param stringsFilePath The main resource file path.
     * @param paths           Additional resource file paths.
     */
    public MultipleResourceBundle(Locale locale, String stringsFilePath, String... paths) {
        this.stringsFilePath = stringsFilePath;
        this.additionalPaths = paths;
        this.locale = locale;
        initializeResourceBundles(stringsFilePath, locale, paths);
    }

    /**
     * Gets the current locale used by the resource bundle.
     *
     * @return The current {@link Locale}.
     */
    public Locale getLocale() {
        return locale;
    }

    /**
     * Gets the main file path of the resource bundle.
     *
     * @return The main resource file path.
     */
    public String getStringsFilePath() {
        return stringsFilePath;
    }

    /**
     * Gets the additional paths used by the resource bundle.
     *
     * @return An array of additional resource file paths.
     */
    public String[] getAdditionalPaths() {
        return additionalPaths;
    }

    /**
     * Retrieves the object associated with the specified key.
     *
     * @param key The key for the desired object.
     * @return The object associated with the key, or {@code null} if the key is not found.
     */
    @Override
    protected Object handleGetObject(String key) {
        return combinedMap.get(key);
    }

    /**
     * Retrieves all the keys available in the combined resource bundle.
     *
     * @return An enumeration of all keys in the resource bundle.
     */
    @Override
    public Enumeration<String> getKeys() {
        return Collections.enumeration(combinedMap.keySet());
    }

    /**
     * Initializes and combines resources from the specified file paths and locale.
     *
     * @param stringsFilePath The main resource file path.
     * @param locale          The locale to use, or {@code null} for the default locale.
     * @param paths           Additional resource file paths.
     */
    private void initializeResourceBundles(String stringsFilePath, Locale locale, String... paths) {
        List<ResourceBundle> bundles = getDefaultResourceBundle(stringsFilePath);

        addStringsFilePath(stringsFilePath, locale, bundles);
        addAdditionalFilePaths(paths, bundles);

        combinedMap = new HashMap<>();
        for (ResourceBundle bundle : bundles) {
            addBundleKeys(bundle);
        }
    }

    /**
     * Adds all bundle keys into a single bundle.
     *
     * @param bundle The bundle where the keys will be added to.
     */
    private void addBundleKeys(ResourceBundle bundle) {
        for (String key : bundle.keySet()) {
            combinedMap.put(key, bundle.getObject(key));
        }
    }

    /**
     * Adds the additional file paths, like the dimensions.
     *
     * @param paths   The rest of the paths.
     * @param bundles The bundles where the strings will be added to.
     */
    private static void addAdditionalFilePaths(String[] paths, List<ResourceBundle> bundles) {
        for (String path : paths) {
            bundles.add(ResourceBundle.getBundle(path));
        }
    }

    /**
     * Adds the main strings file path with a locale other than the default one.
     *
     * @param stringsFilePath The strings file path.
     * @param locale          The locale within the strings file path.
     * @param bundles         The bundles where the strings will be added to.
     */
    private static void addStringsFilePath(String stringsFilePath, Locale locale, List<ResourceBundle> bundles) {
        if (locale != null) {
            bundles.add(ResourceBundle.getBundle(stringsFilePath, locale));
        }
    }

    /**
     * Creates a resource bundle list and adds the default locale.
     *
     * @param stringsFilePath The strings file path.
     * @return The list of bundles.
     */
    private static List<ResourceBundle> getDefaultResourceBundle(String stringsFilePath) {
        List<ResourceBundle> bundles = new ArrayList<>();
        bundles.add(ResourceBundle.getBundle(stringsFilePath, DEFAULT_LOCALE));

        return bundles;
    }
}