/******************************************************************************************************************************
 * File Name: StopTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class StopTest {

    Stop stop;
    String STOP_NAME = "Test Stop";
    String STOP_CODE = "TST";
    double STOP_LATITUDE = 48.8566;
    double STOP_LONGITUDE = -118.2437;
    Point position;

    @BeforeEach
    void setUp() {
        position = new Point(0, 0);
        stop = new Stop(STOP_CODE, STOP_NAME, STOP_LATITUDE, STOP_LONGITUDE, position);
    }

    @Test
    void getCode_returnsCorrectCode() {
        assertEquals(STOP_CODE, stop.getCode(), "Stop code does not match with what was expected");
        assertNotEquals("STOP CODE", stop.getCode(), "Stop code should not match");
    }

    @Test
    void getName_returnsCorrectName() {
        assertEquals(STOP_NAME, stop.getName(), "Stop name does not match with what was expected");
        assertNotEquals("One Stop", stop.getName(), "Stop name should not match");
    }

    @Test
    void getLatitude_returnsCorrectLatitude() {
        assertEquals(STOP_LATITUDE, stop.getLatitude(), 0.0001, "Stop latitude does not match with what was expected");
        assertNotEquals(50, stop.getLatitude(), 0.0001, "Stop latitude should not match");
    }

    @Test
    void getLongitude_returnsCorrectLongitude() {
        assertEquals(STOP_LONGITUDE, stop.getLongitude(), 0.0001, "Stop longitude does not match with what was expected");
        assertNotEquals(23, stop.getLongitude(), 0.0001, "Stop should not match");
    }

    @Test
    void getPosition_returnsCorrectPosition() {
        assertEquals(position, stop.getPosition(), "Position does not match with what was expected");
        assertNotEquals(new Point(2, 1), stop.getPosition(), "Position should not match");
    }

    @Test
    void toString_returnsCorrectString() {
        assertEquals(STOP_NAME, stop.toString(), "Expected toString() to return 'Test Stop' but got a different value");
        assertNotEquals("A String", stop.toString(), "Expected toString() to be equal with only 'Test Stop' but it is equals with anything");
    }

    @Test
    void newStop_throwsInvalidArgumentException_whenInvalidCodeOrNameIsUsed() {
        assertThrows(IllegalArgumentException.class, () -> new Stop("", "Valid Name", 0.0, 0.0, position), "Expected exception for empty code");
        assertThrows(IllegalArgumentException.class, () -> new Stop("Valid Code", "", 0.0, 0.0, position), "Expected exception for empty code");
        assertThrows(IllegalArgumentException.class, () -> new Stop(null, "Valid Name", 0.0, 0.0, position), "Expected exception for empty code");
        assertThrows(IllegalArgumentException.class, () -> new Stop("Valid Code", null, 0.0, 0.0, position), "Expected exception for empty code");

        assertDoesNotThrow(() -> {
            new Stop("Valid Code", "Valid Name", 0.0, 0.0, position);
        }, "Should not have thrown a stop creation with a valid name");
    }

    @Test
    void newStops_shouldNotBeEquals() {
        Stop stop1 = new Stop("404", "West Point", 35.6895, 139.6917, position);
        Stop stop2 = new Stop("404", "West Point", 35.6895, 139.6917, position);

        assertNotEquals(stop1, stop2, "Expected stops with identical values to be unique, but they are considered equal");
    }

    @Test
    void newStop_throwsIllegalArgumentException_whenIllegalLatitudeValuesAreUsed() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Stop("505", "Boundary Test", -91.0, 0.0, position);
        }, "Expected exception for an invalid lower latitude boundary");

        assertDoesNotThrow(() -> {
            new Stop("505", "Boundary Test", -90.0, 0.0, position);
        }, "Did not expect an exception for a valid lower latitude boundary");

        assertThrows(IllegalArgumentException.class, () -> {
            new Stop("505", "Boundary Test", 91.0, 0.0, position);
        }, "Expected exception for an invalid upper latitude boundary");

        assertDoesNotThrow(() -> {
            new Stop("505", "Boundary Test", 90.0, 0.0, position);
        }, "Did not expect an exception for a valid upper latitude boundary");
    }

    @Test
    void newStop_throwsIllegalArgumentException_whenIllegalLongitudeValuesAreUsed() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Stop("505", "Boundary Test", 0.0, -181.0, position);
        }, "Expected exception for an invalid lower longitude boundary");

        assertDoesNotThrow(() -> {
            new Stop("505", "Boundary Test", 0.0, -180.0, position);
        }, "Did not expect an exception for a valid lower longitude boundary");

        assertThrows(IllegalArgumentException.class, () -> {
            new Stop("505", "Boundary Test", 0.0, 181.0, position);
        }, "Expected exception for an invalid upper longitude boundary");

        assertDoesNotThrow(() -> {
            new Stop("505", "Boundary Test", 0.0, 180.0, position);
        }, "Did not expect an exception for a valid upper longitude boundary");
    }

    @Test
    void newStop_throwsIllegalArgumentException_whenNullPositionIsUsed() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Stop("code", "name", 0, 0, null);
        }, "Expected exception for a null position");

        assertDoesNotThrow(() -> {
            new Stop("code", "name", 0, 0, position);
        }, "Did not expect an exception for a valid position");
    }
}
