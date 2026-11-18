/******************************************************************************************************************************
 * File Name: TransportColorTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportColorTest {

    @Test
    void newTransportColor_canGenerateRandomColor() {
        TransportColor transportColor = new TransportColor();
        Color color = transportColor.getColor();

        assertTrue(color.getRed() >= 0 && color.getRed() <= 1, "Red component is out of range");
        assertTrue(color.getGreen() >= 0 && color.getGreen() <= 1, "Green component is out of range");
        assertTrue(color.getBlue() >= 0 && color.getBlue() <= 1, "Blue component is out of range");

        assertEquals(1.0, color.getOpacity(), "Alpha value is not 1.0");
    }
}
