/******************************************************************************************************************************
 * File Name: TransportTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TransportTest {

    Transport transport;

    String name = "Batcar";
    TransportMetrics transportMetrics = new TransportMetrics(1.0, 1.0, 1.0);

    @BeforeEach
    void setUp() {
        transport = new Transport(name, transportMetrics);
    }

    @Test
    void getName() {
        assertEquals(name, transport.getName(),
                "The name of the transport should be the same as the first one"
        );
        assertNotEquals("", transport.getName(),
                "The name of the transport should be not equals to anything else"
        );
    }

    @Test
    void isEnabled() {
        assertTrue(transport.isEnabled(), "The transport should be enabled by default");
    }

    @Test
    void setEnabled() {
        assertTrue(transport.isEnabled(), "The transport should start enabled");

        transport.setEnabled(false);
        assertFalse(transport.isEnabled(), "The transport should be disabled now");
    }

    @Test
    void getTransportMetrics() {
        assertEquals(transportMetrics, transport.getTransportMetrics(),
                "The transport metrics should be always the same."
        );
    }
}