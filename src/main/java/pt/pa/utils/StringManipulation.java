/******************************************************************************************************************************
 * File Name: StringManipulation.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a class that contains static methods to manipulate Strings.
 */
public class StringManipulation {

    /**
     * Returns a String with its first character capitalized, and the rest of its characters always lower case.
     *
     * @param string the String to be formatted.
     * @return The formatted String.
     */
    public static String capitalizeFirstLetter(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    /**
     * Formats a LocalDateTime into a user-friendly string format.
     *
     * @param dateTime The LocalDateTime to be formatted.
     * @return A formatted string in the pattern "EEEE, MMMM dd, yyyy 'at' HH:mm:ss a".
     */
    public static String formatDateTime(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy 'at' HH:mm:ss a");
        return dateTime.format(formatter);
    }
}
