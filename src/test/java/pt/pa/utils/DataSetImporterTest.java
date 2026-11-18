/******************************************************************************************************************************
 * File Name: DataSetImporterTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import com.opencsv.exceptions.CsvValidationException;
import org.javatuples.Triplet;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.pa.model.Route;
import pt.pa.model.Stop;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class DataSetImporterTest {

    DataSetImporter importer;
    static final int NUMBER_OF_EXPECTED_ROUTES = 39;
    static final int NUMBER_OF_EXPECTED_STOPS = 31;
    static final int NUMBER_OF_EXPECTED_TRANSPORTS = 5;

    @BeforeEach
    void setUp() {
        importer = new DataSetImporter(DataSetImporter.ImportationMode.TEST);
    }

    @Test
    void importer_throwsIllegalArgumentException_whenImportationModeIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            new DataSetImporter(null);
        }, "Exception an exception since the importation mode is null");
    }

    @Test
    void importStops_hasTheExpectedNumberOfStops() throws IOException, CsvValidationException {
        assertDoesNotThrow(() -> importer.importStops(), "Unexpected problem found during stop importation");

        List<Stop> stops;
        stops = importer.importStops();

        assertEquals(NUMBER_OF_EXPECTED_STOPS, stops.size(), "The number of stops imported does not match the expected number of stops");
    }

    @Test
    void importXY_hasTheCorrectNumberOfPositions() throws IOException, CsvValidationException {
        assertDoesNotThrow(() -> importer.importXY(), "Unexpected problem found during position importation");

        Map<String, Point> stopPositions = importer.importXY();

        assertEquals(NUMBER_OF_EXPECTED_STOPS, stopPositions.size(), "The number of positions imported does not match the expected number of stops");
    }

    @Test
    void importRoutes_hasTheCorrectNumberOfRoutes() throws IOException, CsvValidationException {
        List<Stop> stops = importer.importStops();
        Map<String, Stop> stopMap = new HashMap<>();

        for (Stop stop : stops) {
            stopMap.put(stop.getCode(), stop);
        }

        assertDoesNotThrow(() -> importer.importRoutes(stopMap), "Unexpected problem found during route importation");

        List<Triplet<Stop, Stop, Route>> routes = importer.importRoutes(stopMap);

        assertEquals(NUMBER_OF_EXPECTED_ROUTES, routes.size(), "The number of routes imported does not match the expected number of routes");
    }

    @Test
    void importTransportColors_importsAtLeastTheExpectedTransports() throws IOException, CsvValidationException {
        assertDoesNotThrow(() -> importer.importTransportColors(), "Unexpected problem found during transport color importation");

        Map<String, TransportColor> transportModeColors = importer.importTransportColors();

        assertTrue(transportModeColors.size() >= NUMBER_OF_EXPECTED_TRANSPORTS);
    }
}