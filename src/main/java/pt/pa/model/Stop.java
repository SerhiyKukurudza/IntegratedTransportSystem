/******************************************************************************************************************************
 * File Name: Stop.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import java.awt.*;

/**
 * Represents a stop in a transport system.
 * Each stop has a unique code, a name, latitude, and longitude.
 */
public class Stop implements Comparable<Stop> {
    /**
     * Represents the code of a stop.
     * Can't be null or empty.
     */
    private final String code;

    /**
     * Represents the name of a stop.
     * Can't be null or empty.
     */
    private final String name;

    /**
     * Represents the latitude of a stop.
     * Must be between -90.0 and 90.0 (inclusive).
     */
    private final double latitude;

    /**
     * Represents the longitude of a stop.
     * Must be between -180.0 and 180.0 (include).
     */
    private final double longitude;

    /**
     * Represents the position in the map shown with the SmartGraph.
     * It mustn't be null.
     */
    private final Point position;

    /**
     * Constructs a Stop object with the specified details.
     *
     * @param code      The unique code of the stop (must not be null or empty).
     * @param name      The name of the stop (must not be null or empty).
     * @param latitude  The latitude of the stop.
     * @param longitude The longitude of the stop.
     * @param position  The position of the stop in the SmartGraph.
     * @throws IllegalArgumentException If code or name is null or empty.
     * @throws IllegalArgumentException If the latitude is lower than -90.0 or bigger than 90.0.
     * @throws IllegalArgumentException If the longitude is lower than -180.0 or bigger than 180.0.
     * @throws IllegalArgumentException If the position is null.
     */
    public Stop(String code, String name, double latitude, double longitude, Point position) throws IllegalArgumentException {
        checkParameters(code, name, latitude, longitude, position);

        this.code = code;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.position = position;
    }

    /**
     * Validates the parameters used to define a stop or location.
     * Ensures that the provided values meet the expected constraints.
     *
     * @param code The unique code identifying the stop or location. Cannot be null or empty.
     * @param name The name of the stop or location. Cannot be null or empty.
     * @param latitude The latitude of the stop or location. Must be between -90.0 and 90.0 inclusive.
     * @param longitude The longitude of the stop or location. Must be between -180.0 and 180.0 inclusive.
     * @param position The position (Point) representing the stop or location's coordinates. Cannot be null.
     *
     * @throws IllegalArgumentException If any of the parameters are invalid:
     *                                   <ul>
     *                                       <li>If the code is null or empty.</li>
     *                                       <li>If the name is null or empty.</li>
     *                                       <li>If the latitude is not within the range [-90.0, 90.0].</li>
     *                                       <li>If the longitude is not within the range [-180.0, 180.0].</li>
     *                                       <li>If the position is null.</li>
     *                                   </ul>
     */
    private void checkParameters(String code, String name, double latitude, double longitude, Point position) {
        if (code == null || code.isEmpty()) {
            throw new IllegalArgumentException("Code cannot be null or empty.");
        }

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty.");
        }

        if (latitude > 90.0) {
            throw new IllegalArgumentException("Latitude cannot be greater than 90.");
        }

        if (latitude < -90.0) {
            throw new IllegalArgumentException("Latitude cannot be smaller than 90.");
        }

        if (longitude > 180.0) {
            throw new IllegalArgumentException("Longitude cannot be greater than 180.");
        }

        if (longitude < -180.0) {
            throw new IllegalArgumentException("Longitude cannot be smaller than 180.");
        }

        if (position == null) {
            throw new IllegalArgumentException("Position cannot be null.");
        }
    }

    /**
     * Gets the unique code of the stop.
     *
     * @return the code of the stop.
     */
    public String getCode() {
        return code;
    }

    /**
     * Gets the name of the stop.
     *
     * @return the name of the stop.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the latitude of the stop.
     *
     * @return the latitude of the stop.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude of the stop.
     *
     * @return the longitude of the stop.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Gets the position of the stop in the SmartGraph.
     *
     * @return the position of the stop in the SmartGraph.
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns a string representation of the stop, which is its name.
     *
     * @return the name of the stop.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Adds a comparison option by route name
     *
     * @param stop The other stop object being compared.
     * @return The integer result of the comparison.
     */
    @Override
    public int compareTo(Stop stop) {
        return stop.name.compareTo(this.name);
    }
}