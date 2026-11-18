/******************************************************************************************************************************
 * File Name: TransportColor.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import javafx.scene.paint.Color;

import java.util.Random;

/**
 * Represents a utility class that allows to perform operations over JavaFX Colors.
 */
public class TransportColor {

    /**
     * Represents the RGB color generation upper limit.
     */
    private static final int RGB_UPPER_LIMIT = 256;

    /**
     * Represents the RGB color generation lower limit.
     */
    private static final double RGB_LOWER_LIMIT = 255.0;

    /**
     * Represents the RGB color generation opacity.
     * In this case, opacity of 1 means completely opaque.
     */
    private static final double RGB_OPACITY = 1.0;

    /**
     * Represents the JavaFX color of a transport mode.
     */
    private final Color color;

    /**
     * Constructs a transport mode color with a specified string color.
     *
     * @param color The string color.
     * @throws IllegalArgumentException If the provided color doesn't represent a valid color.
     */
    public TransportColor(String color) {
        this.color = Color.web(color);
    }

    /**
     * Constructs a transport mode color with a randomly generated string color.
     */
    public TransportColor() {
        this.color = generateRandomColor();
    }

    /**
     * Getter for the transport color.
     *
     * @return The color of the transport.
     */
    public Color getColor() {
        return color;
    }

    /**
     * Gets the transport color name with the JavaFX Color default toString.
     * It removes the hexadecimal reference {@code 0x} and adds a {@code #}.
     *
     * @return The color name that can be used in CSS, an example.
     */
    public String getColorName() {
        String hexadecimalColorValue = color.toString();
        String colorName = hexadecimalColorValue.replaceFirst("0x", "");

        return "#" + colorName;
    }

    /**
     * Generates a random color with RGB values based on the {@link Random} class.
     * The RGB values are normalized to the range [0, 1] as required by JavaFX {@link Color}.
     *
     * @return A random {@link Color} instance with full opacity (alpha = 1.0).
     */
    private Color generateRandomColor() {
        Random random = new Random();

        double red = generateRandomRGBValue(random);
        double green = generateRandomRGBValue(random);
        double blue = generateRandomRGBValue(random);

        return new Color(red, green, blue, RGB_OPACITY);
    }

    /**
     * Generates a random RGB value in the range of [0, 1].
     *
     * @param random The random object used in the generation.
     * @return The random RGB color value in the range of [0, 1].
     */
    private static double generateRandomRGBValue(Random random) {
        return random.nextInt(RGB_UPPER_LIMIT) / RGB_LOWER_LIMIT;
    }

    /**
     * Overrides the default to string to return the color name function's value.
     * @return The name of the transport color.
     */
    @Override
    public String toString() {
        return getColorName();
    }
}
