/******************************************************************************************************************************
 * File Name: FileWriterUtil.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for writing data to a file in a structured format.
 */
public class FileWriterUtil {
    /**
     * Represents the path of the file where the data will be written.
     */
    private final String filePath;
    /**
     * Represents the header for the file, representing column names or titles.
     */
    private final String[] header;
    /**
     * Represents the widths of each column in the file.
     */
    private final int[] widths;

    /**
     * Constructor to initialize the file and write the header.
     *
     * @param filePath The path where the file will be written.
     * @param widths   The width of each column.
     * @param header   The headers of the file.
     * @throws IOException If an I/O error occurs.
     */
    public FileWriterUtil(String filePath, int[] widths, String... header) throws IOException {
        validateParameters(filePath, widths, header);

        this.filePath = filePath;
        this.widths = widths;
        this.header = header;

        initializeDocument();
    }

    /**
     * Validates the parameters required for creating a new instance of FileWriterUtil.
     *
     * @param filePath The path where the file will be written.
     * @param widths   An array representing the widths of each column.
     * @param header   An array containing the contents of each header.
     * @throws IllegalArgumentException If:
     *                                  <ul>
     *                                      <li>The file path is null, blank, or empty.</li>
     *                                      <li>The widths array is null, empty, or has a length different from the header array.</li>
     *                                      <li>Any width is negative.</li>
     *                                      <li>The header array is null or empty.</li>
     *                                  </ul>
     */
    private void validateParameters(String filePath, int[] widths, String[] header) {
        if (filePath.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or empty");
        }

        if (widths == null || widths.length == 0) {
            throw new IllegalArgumentException("Widths cannot be null or empty");
        }

        for(int width : widths) {
            if(width < 0) throw new IllegalArgumentException("All widths must be positive");
        }

        if (header == null || header.length == 0) {
            throw new IllegalArgumentException("Headers cannot be null or empty");
        }

        if (widths.length != header.length) {
            throw new IllegalArgumentException("Widths and headers must have the same length");
        }
    }

    /**
     * Formats a collection of columns into a single string with specified widths.
     *
     * @param columns The columns to format into a single string.
     * @return A formatted string with the columns padded and aligned based on widths.
     * @throws IllegalArgumentException If the number of columns does not match the number of widths.
     */
    private String stringColumnFormatter(String... columns) {
        if (columns.length != widths.length) {
            throw new IllegalArgumentException("Number of columns must match the specified widths.");
        }

        StringBuilder formatBuilder = new StringBuilder();
        for (int width : widths) {
            appendLeftAlignmentPadding(width, formatBuilder);
        }

        String format = formatBuilder.toString().trim();
        return String.format(format, (Object[]) columns);
    }

    /**
     * Appends left-aligned padding for a column width to the format string.
     *
     * @param width         The width to be applied to the column.
     * @param formatBuilder The StringBuilder used to construct the format string.
     */
    private static void appendLeftAlignmentPadding(int width, StringBuilder formatBuilder) {
        formatBuilder.append("%-").append(width).append("s ");
    }

    /**
     * Initializes the document by verifying if it already exists.
     * If it doesn't exist yet, headers are created.
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void initializeDocument() throws IOException {
        File file = new File(filePath);

        if (!file.exists()) {
            createHeader();
        }
    }

    /**
     * Adds the headers of a brand new logger file.
     *
     * @throws IOException If an I/O exception occurs.
     */
    private void createHeader() throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(stringColumnFormatter(header));
            writer.newLine();
            writer.write(createHeaderSeparator());
            writer.newLine();
        }
    }

    /**
     * Creates the header separator with hyphens that cover the whole header width.
     *
     * @return The dynamic separator as a String.
     */
    private String createHeaderSeparator() {
        int fullWidth = getFullWidth();
        return "-".repeat(fullWidth);
    }

    /**
     * Calculates the full width of the document by iterating over all column widths.
     *
     * @return The total width of the "table".
     */
    private int getFullWidth() {
        int fullWidth = 0;

        for (int width : widths) {
            fullWidth += width;
        }
        return fullWidth;
    }

    /**
     * Appends a new line at the end of the file.
     *
     * @param columnData The columns to append at the end of the file.
     * @throws IOException If an I/O error occurs.
     */
    public void appendLine(String... columnData) throws IOException {
        if (columnData.length != header.length) {
            throw new IllegalArgumentException("Number of data columns does not match the number of header");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(stringColumnFormatter(columnData));
            writer.newLine();
        }
    }

    /**
     * Formats a local date time instance into its proper String format.
     *
     * @param dateTime The local date time to convert into a String.
     * @return The converted String.
     */
    public static String FormatDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
