package pt.pa.patterns.factory.simple;

import pt.pa.controller.featureTab.SettingsTabController;
import pt.pa.model.CostType;

import java.util.Arrays;
import java.util.Locale;

/**
 * Represents a simple factory to create the valid locales for the UI.
 */
public class LocaleFactory {

    /**
     * Creates a new instance of a Locale according to the provided language option.
     * <p>
     * The possible arguments are the different {@link SettingsTabController.LanguageOption} options.
     * </p>
     *
     * @param languageOption The language option used to create the Locale.
     * @return The locale instance that corresponds to the provided language option.
     */
    public static Locale create(SettingsTabController.LanguageOption languageOption) {
        if (languageOption == null) {
            throw new IllegalArgumentException("Language option cannot be null");
        }

        switch (languageOption) {
            case ENGLISH -> {
                return Locale.ENGLISH;
            }
            case PORTUGUESE -> {
                return new Locale("pt", "PT");
            }
        }

        return Locale.ENGLISH;
    }
}
