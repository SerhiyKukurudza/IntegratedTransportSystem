/******************************************************************************************************************************
 * File Name: DataSetImporter.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.javatuples.Pair;
import org.javatuples.Triplet;
import pt.pa.model.*;

import java.awt.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.pa.utils.StringManipulation.capitalizeFirstLetter;

/**
 * A utility class for importing datasets related to transport stops, their positions,
 * and routes.
 */
public class DataSetImporter {

    /**
     * Enum representing the index of stop data fields in the importation CSV file.
     */
    private enum StopDataIndex {
        /**
         * Index containing the code of the stop.
         */
        CODE(0),
        /**
         * Index containing the name of the stop.
         */
        NAME(1),
        /**
         * Index containing the latitude of the stop.
         */
        LATITUDE(2),
        /**
         * Index containing the longitude of the stop.
         */
        LONGITUDE(3);

        /**
         * Index that a particular field will have in the CSV file.
         */
        private final int index;

        /**
         * Constructor for StopDataIndex.
         *
         * @param index The index value associated with the stop data field.
         */
        StopDataIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Enum representing the index of xy data fields in the importation CSV file.
     */
    private enum XYDataIndex {
        /**
         * Index containing the code of the stop.
         */
        CODE(0),
        /**
         * Index containing the X position of a stop.
         */
        X(1),
        /**
         * Index containing the Y position of a stop.
         */
        Y(2);

        /**
         * Index that a particular field will have in the CSV file.
         */
        private final int index;

        /**
         * Constructor for XYDataIndex.
         *
         * @param index Ths index value associated with the xy data field.
         */
        XYDataIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Enum representing the index of transport data fields in the importation CSV file.
     */
    private enum TransportDataIndex {
        /**
         * Index containing the name a transport mode.
         */
        NAME(0),
        /**
         * Index containing the color of a transport mode.
         */
        COLOR(1);

        /**
         * Index that a particular field will have in the CSV file.
         */
        private final int index;

        /**
         * Constructor for TransportDataIndex.
         *
         * @param index Ths index value associated with the transport data field.
         */
        TransportDataIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Enum representing the index of route data fields in the importation CSV file.
     */
    private enum RouteDataIndex {
        /**
         * Index containing the code of the starting stop.
         */
        START_STOP_CODE(0),
        /**
         * Index containing the code of the ending stop.
         */
        END_STOP_CODE(1);

        /**
         * Index that a particular field will have in the CSV file.
         */
        private final int index;

        /**
         * Constructor for RouteDataIndex.
         *
         * @param index Ths index value associated with the route data field.
         */
        RouteDataIndex(int index) {
            this.index = index;
        }
    }

    /**
     * Represents the distance metric prefix in its csv file.
     */
    private static final String DISTANCE_METRIC_PREFIX = "distance";

    /**
     * Represents the duration metric prefix in its csv file.
     */
    private static final String DURATION_METRIC_PREFIX = "duration";

    /**
     * Represents the cost metric prefix in its csv file.
     */
    private static final String COST_METRIC_PREFIX = "cost";

    /**
     * Represents the transport metric separator in its csv file.
     */
    private static final String ROUTE_METRIC_DIVIDER = "_";

    /**
     * Represents the index part of each transport
     * in the route header for the metric part.
     */
    private static final int ROUTE_HEADER_METRIC = 0;

    /**
     * Represents the index part of each transport
     * in the route header for the transport part.
     */
    private static final int ROUTE_HEADER_TRANSPORT = 1;

    /**
     * Represents the type of importation being performed.
     */
    public enum ImportationMode {
        /**
         * Represents the importation used for the running application.
         */
        PRODUCTION("dataset/stops.csv",
                "dataset/xy.csv",
                "dataset/routes.csv",
                "dataset/transports.csv"
        ),
        /**
         * Represents the importation used for the unit testing.
         */
        TEST("dataset/test/stops.csv",
                "dataset/test/xy.csv",
                "dataset/test/routes.csv",
                "dataset/test/transports.csv"
        );

        /**
         * Represents the file path of the stops file.
         */
        private final String stopsFilePath;
        /**
         * Represents the file path of the xy positions file.
         */
        private final String xyFilePath;
        /**
         * Represents the file path for the routes file.
         */
        private final String routesFilePath;
        /**
         * Represents the file path to the transportation modes file path.
         */
        private final String transportsFilePath;

        /**
         * Constructs an ImportationMode instance with the file paths required for importing data.
         *
         * @param stopsFilePath      The path to the stops file.
         * @param xyFilePath         The path to the file containing the coordinates of the points.
         * @param routesFilePath     The path to the routes file.
         * @param transportsFilePath The path to the transports file.
         */
        ImportationMode(String stopsFilePath, String xyFilePath, String routesFilePath, String transportsFilePath) {
            this.stopsFilePath = stopsFilePath;
            this.xyFilePath = xyFilePath;
            this.routesFilePath = routesFilePath;
            this.transportsFilePath = transportsFilePath;
        }
    }

    /**
     * Represents the number of fields expected in the CSV file of the stops.
     */
    public final int STOP_CSV_EXPECTED_FIELDS = 4;

    /**
     * Represents the number of fields expected in the CSV file of the stops.
     */
    public final int XY_CSV_EXPECTED_FIELDS = 3;

    /**
     * Represents the number of fields expected in the CSV file of the transport modes and their respective colors.
     */
    public final int TRANSPORT_CSV_EXPECTED_FIELDS = 2;

    /**
     * Represents the importation mode of the importer instance.
     * Whether it is for testing or running the application.
     */
    private final ImportationMode importationMode;

    /**
     * Constructs an importer instance with the default {@code PRODUCTION} importation mode.
     */
    public DataSetImporter() {
        this.importationMode = ImportationMode.PRODUCTION;
    }

    /**
     * Constructs an importer instance that receives an importation mode.
     *
     * @param importationMode The importation mode for the importer.
     */
    public DataSetImporter(ImportationMode importationMode) {
        if (importationMode == null) {
            throw new IllegalArgumentException("Importation mode cannot be null");
        }

        this.importationMode = importationMode;
    }

    /**
     * Creates a CSVReader for the specified file path.
     *
     * @param path The relative path to the file to be read.
     * @return A CSVReader for the specified file.
     * @throws IOException If the file cannot be found or opened.
     */
    private CSVReader createCSVReader(String path) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("File not found: " + path);
        }
        return new CSVReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    /**
     * Skips the first csv reader line that represents the file header.
     * Only skips if no lines have been read.
     *
     * @param csvReader The csv reader with a header.
     * @throws IOException If an error occurs reading the file.
     */
    private void skipCSVReaderHeader(CSVReader csvReader) throws IOException {
        if (csvReader.getLinesRead() == 0) {
            csvReader.skip(1);
        }
    }

    /**
     * Imports stops from the CSV file using OpenCSV.
     *
     * @return A list of Stop objects.
     * @throws IOException            If an error occurs reading the file.
     * @throws CsvValidationException If CSV validation fails.
     */
    public List<Stop> importStops() throws IOException, CsvValidationException {
        List<Stop> stops = new ArrayList<>();
        Map<String, Point> XYList = importXY();

        try (CSVReader csvReader = createCSVReader(importationMode.stopsFilePath)) {
            skipCSVReaderHeader(csvReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processStopLine(line, stops, XYList);
            }
        }
        return stops;
    }

    /**
     * Processes the stop csv line while making sure every line has the correct format.
     *
     * @param line   The line that should be processed.
     * @param stops  The stops list to add the stop to.
     * @param XYList The list of xy position of the stops.
     * @throws IOException If an error occurs reading the file.
     */
    private void processStopLine(String[] line, List<Stop> stops, Map<String, Point> XYList) throws IOException {
        if (line.length < STOP_CSV_EXPECTED_FIELDS) {
            throw new IOException("Invalid format in the stops file.");
        }
        stops.add(getStopFromLine(line, XYList));
    }

    /**
     * Creates a new stop instance with the details present in a line.
     *
     * @param line   The line in the CSV document.
     * @param XYList The list of x and y positions for each stop.
     * @return The created stop instance.
     */
    private Stop getStopFromLine(String[] line, Map<String, Point> XYList) {
        String code = line[StopDataIndex.CODE.index];
        String name = line[StopDataIndex.NAME.index];
        double latitude = Double.parseDouble(line[StopDataIndex.LATITUDE.index]);
        double longitude = Double.parseDouble(line[StopDataIndex.LONGITUDE.index]);
        Point position = XYList.get(code);

        return new Stop(code, name, latitude, longitude, position);
    }

    /**
     * Imports XY positions of stops from the CSV file using OpenCSV.
     *
     * @return A map of stop codes to their corresponding Point positions.
     * @throws IOException            If an error occurs reading the file.
     * @throws CsvValidationException If CSV validation fails.
     */
    public Map<String, Point> importXY() throws IOException, CsvValidationException {
        Map<String, Point> stopPositions = new HashMap<>();

        try (CSVReader csvReader = createCSVReader(importationMode.xyFilePath)) {
            skipCSVReaderHeader(csvReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processXYLine(line, stopPositions);
            }
        }
        return stopPositions;
    }

    /**
     * Processes the xy csv line while making sure every line has the correct format.
     *
     * @param line          The line that should be processed.
     * @param stopPositions A map that will store each xy position related to each stop.
     * @throws IOException If an error occurs reading the file.
     */
    private void processXYLine(String[] line, Map<String, Point> stopPositions) throws IOException {
        if (line.length < XY_CSV_EXPECTED_FIELDS) {
            throw new IOException("Invalid format in the XY file.");
        }

        String stopCode = line[XYDataIndex.CODE.index];
        Point position = getPositionFromLine(line);
        stopPositions.put(stopCode, position);
    }

    /**
     * Gets a point instance representing the stop position from a line.
     *
     * @param line The csv document line.
     * @return A point object with the stop position.
     */
    private Point getPositionFromLine(String[] line) {
        int x = Integer.parseInt(line[XYDataIndex.X.index]);
        int y = Integer.parseInt(line[XYDataIndex.Y.index]);

        return new Point(x, y);
    }

    /**
     * Imports transport names and their colors from the CSV file using OpenCSV.
     *
     * @return A map of transport names with their corresponding colors.
     * @throws IOException            If an error occurs reading the file.
     * @throws CsvValidationException If the CSV validation fails.
     */
    public Map<String, TransportColor> importTransportColors() throws IOException, CsvValidationException {
        Map<String, TransportColor> transportColors = new HashMap<>();

        try (CSVReader csvReader = createCSVReader(importationMode.transportsFilePath)) {
            skipCSVReaderHeader(csvReader);
            String[] line;
            while ((line = csvReader.readNext()) != null) {
                processTransportLine(line, transportColors);
            }
        }

        return transportColors;
    }

    /**
     * Processes the transports csv line while making sure every line has the correct format.
     *
     * @param line            The line that should be processed.
     * @param transportColors The map where each color will be added associated
     *                        with the respective transport mode.
     * @throws IOException If an error occurs reading the file.
     */
    private void processTransportLine(String[] line, Map<String, TransportColor> transportColors) throws IOException {
        if (line.length < TRANSPORT_CSV_EXPECTED_FIELDS) {
            throw new IOException("Invalid format in the transports color file.");
        }

        String transportName = line[TransportDataIndex.NAME.index].trim();
        String formattedTransportName = capitalizeFirstLetter(transportName);

        String transportColorString = line[TransportDataIndex.COLOR.index].trim();

        if (formattedTransportName.isBlank()) {
            throw new IOException("Transport name cannot be blank.");
        }

        transportColors.put(formattedTransportName, new TransportColor(transportColorString));
    }

    /**
     * Imports routes from the CSV file using OpenCSV.
     *
     * @param stopMap A map of stop codes to Stop objects to resolve origins and destinations.
     * @return A list of Triplets with origin, destination and the route.
     * @throws IOException            If an error occurs reading the file.
     * @throws CsvValidationException If CSV validation fails.
     */
    public List<Triplet<Stop, Stop, Route>> importRoutes(Map<String, Stop> stopMap) throws IOException, CsvValidationException {
        List<Triplet<Stop, Stop, Route>> routes = new ArrayList<>();

        try (CSVReader csvReader = createCSVReader(importationMode.routesFilePath)) {
            String[] headers = csvReader.readNext();
            if (headers == null) {
                throw new IOException("The routes file is empty or missing headers.");
            }

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                line = adjustRouteLine(line, headers);
                routes.add(parseRoute(line, stopMap, headers));
            }
        }
        return routes;
    }

    /**
     * If any of the route fields is incomplete, the field is adjusted.
     *
     * @param line    The route line being read.
     * @param headers The headers of the route csv file.
     * @return The updated, adjusted route line.
     */
    private String[] adjustRouteLine(String[] line, String[] headers) {
        if (line.length < headers.length) {
            line = adjustRouteData(line, headers.length);
        }
        return line;
    }

    /**
     * Adjusts route data to ensure it has the expected number of fields by padding empty strings.
     *
     * @param routeData The original route data array.
     * @return The adjusted route data array.
     */
    private String[] adjustRouteData(String[] routeData, int expectedFields) {
        String[] completeRoute = new String[expectedFields];
        System.arraycopy(routeData, 0, completeRoute, 0, routeData.length);

        for (int i = routeData.length; i < expectedFields; i++) {
            completeRoute[i] = "";
        }

        return completeRoute;
    }

    /**
     * Parses a line in the routes.csv dataset.
     * Gets all 3 types of cost associated with each type of transportation for each route
     * and associates the origin and destination stop codes with each corresponding Stop object.
     *
     * @param line    A String line in the routes.csv dataset.
     * @param stopMap A map with the code and Stop object of the stops.csv dataset.
     * @param headers The headers labeling each attribute of the csv line.
     * @return A Triplet with the origin and destination stops and the route itself.
     * @throws IllegalArgumentException When the code of one of the stops in a route is invalid.
     */
    private Triplet<Stop, Stop, Route> parseRoute(String[] line, Map<String, Stop> stopMap, String[] headers) {
        Stop startStop = getStop(line, stopMap, RouteDataIndex.START_STOP_CODE);
        Stop endStop = getStop(line, stopMap, RouteDataIndex.END_STOP_CODE);

        Map<String, Integer> distanceIndexes = new HashMap<>();
        Map<String, Integer> durationIndexes = new HashMap<>();
        Map<String, Integer> costIndexes = new HashMap<>();
        populateMetricIndices(headers, distanceIndexes, durationIndexes, costIndexes);

        List<Pair<String, Transport>> routeTransports = new ArrayList<>();
        populateRouteTransports(line, distanceIndexes, durationIndexes, costIndexes, routeTransports);

        return formatRouteWithTransportsAndStops(routeTransports, startStop, endStop);
    }

    /**
     * Gets a stop of a route with the code present in the route line.
     *
     * @param line           The csv route line.
     * @param stopMap        The map with the stop code and the stop object.
     * @param routeDataIndex The index of the stop in the route line.
     * @return The found stop instance.
     */
    private Stop getStop(String[] line, Map<String, Stop> stopMap, RouteDataIndex routeDataIndex) {
        String stopCode = line[routeDataIndex.index];

        if (stopCode == null || !stopMap.containsKey(stopCode)) {
            throw new IllegalArgumentException("Invalid stop code: " + stopCode);
        }

        return stopMap.get(stopCode);
    }

    /**
     * Formats each route by adding the transport modes of each route with it starting and ending stop.
     *
     * @param routeTransports The route transports list pair.
     * @param startStop       The starting stop of the route.
     * @param endStop         The ending stop of the route.
     * @return A triplet with both stops and the route.
     */
    private Triplet<Stop, Stop, Route> formatRouteWithTransportsAndStops(
            List<Pair<String, Transport>> routeTransports, Stop startStop, Stop endStop) {
        TransportModesMap transportModes = new TransportModesMap();

        for (Pair<String, Transport> transport : routeTransports) {
            transportModes.put(transport.getValue0(), transport.getValue1());
        }

        return new Triplet<>(startStop, endStop, new Route(transportModes));
    }

    /**
     * Populate all route transports of the line to their each respective transport mode.
     *
     * @param line            The line of the route csv file.
     * @param distanceIndexes The transport distance indices map.
     * @param durationIndexes The transport duration indices map.
     * @param costIndexes     The transport cost indices map.
     * @param transports      The transports pair list of transport names with their transport.
     */
    private void populateRouteTransports(String[] line, Map<String, Integer> distanceIndexes,
                                         Map<String, Integer> durationIndexes,
                                         Map<String, Integer> costIndexes,
                                         List<Pair<String, Transport>> transports) {
        for (String transportName : distanceIndexes.keySet()) {
            Integer distanceIdx = distanceIndexes.get(transportName);
            Integer durationIdx = durationIndexes.get(transportName);
            Integer costIdx = costIndexes.get(transportName);

            Double distance = parseNullableDouble(line[distanceIdx]);
            Double duration = parseNullableDouble(line[durationIdx]);
            Double cost = parseNullableDouble(line[costIdx]);

            try {
                routeHasValidMetricValues(transportName, distance, duration, cost);
            } catch (IOException e) {
                continue;
            }

            Transport newTransport = new Transport(transportName, new TransportMetrics(distance, duration, cost));
            transports.add(new Pair<>(transportName, newTransport));
        }
    }

    /**
     * Throws an exception when the route does not have valid metric values.
     *
     * @param transportName The name of the transport.
     * @param distance      The distance metric.
     * @param duration      The duration metric.
     * @param cost          The cost metric.
     * @throws IOException If some of the metrics are null.
     */
    private void routeHasValidMetricValues(String transportName, Double distance,
                                           Double duration, Double cost) throws IOException {
        boolean routeHasAnyNullValue = distance == null || duration == null || cost == null;

        if (routeHasAnyNullValue) {
            throw new IOException("Incomplete data for transport mode: " + transportName +
                    ". All values (distance, duration, cost) must be non-null if any is provided.");
        }
    }

    /**
     * Populates the metric maps for each index by iterating over the file header line.
     *
     * @param headers         The header line of the csv routes file.
     * @param distanceIndexes The map with the transport mode and index of all distances.
     * @param durationIndexes The map with the transport mode and index of all durations.
     * @param costIndexes     The map with the transport mode and the index of all costs.
     */
    private void populateMetricIndices(String[] headers, Map<String, Integer> distanceIndexes, Map<String, Integer> durationIndexes, Map<String, Integer> costIndexes) {
        for (int i = 2; i < headers.length; i++) {
            String[] parts = headers[i].split(ROUTE_METRIC_DIVIDER);
            if (parts.length != 2) continue;

            String metric = parts[ROUTE_HEADER_METRIC];
            String mode = capitalizeFirstLetter(parts[ROUTE_HEADER_TRANSPORT]);

            switch (metric) {
                case DISTANCE_METRIC_PREFIX:
                    distanceIndexes.put(mode, i);
                    break;
                case DURATION_METRIC_PREFIX:
                    durationIndexes.put(mode, i);
                    break;
                case COST_METRIC_PREFIX:
                    costIndexes.put(mode, i);
                    break;
            }
        }
    }

    /**
     * Returns a double or null value from a String instance.
     *
     * @param value the String representing a double value.
     * @return The Double value of a String.
     */
    private Double parseNullableDouble(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return Double.parseDouble(value);
    }
}