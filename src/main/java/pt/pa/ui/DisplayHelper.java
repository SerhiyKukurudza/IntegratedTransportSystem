package pt.pa.ui;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import pt.pa.utils.TransportColor;
import javafx.scene.layout.Region;

/**
 * A helper class for the JavaFX UI nodes.
 * It applies styles, hides and shows elements, generates colorful squares and others.
 */
public class DisplayHelper {
    /**
     * Represents the style class used in JavaFX elements with a problem.
     */
    private static final String ERROR_STYLE_CLASS = "input-error";

    /**
     * Adds an error styling to a node element if it isn't already wrong.
     *
     * @param node Any JavaFX node.
     */
    public static void addErrorStyling(Node node) {
        if (!node.getStyleClass().contains(ERROR_STYLE_CLASS)) {
            node.getStyleClass().add(ERROR_STYLE_CLASS);
        }
    }

    /**
     * Removes an error styling from a node element.
     *
     * @param node Any JavaFX node.
     */
    public static void removeErrorStyling(Node node) {
        node.getStyleClass().remove(ERROR_STYLE_CLASS);
    }

    /**
     * Sets a node as visible or invisible.
     *
     * @param node    The JavaFX node.
     * @param visible The logical visibility state.
     */
    public static void setNodeVisibility(Node node, boolean visible) {
        node.setVisible(visible);
        node.setManaged(visible);
    }

    /**
     * Shows one node, while hiding the other.
     *
     * @param node1 The JavaFX node that will be visible.
     * @param node2 The JavaFX node that will become invisible.
     */
    public static void toggleNodesVisibility(Node node1, Node node2) {
        setNodeVisibility(node1, true);
        setNodeVisibility(node2, false);
    }

    /**
     * Creates a visual representation of a color as a small square.
     *
     * @param transportColor The color to be displayed in the square.
     * @return A `Region` representing a small square with the specified color as its background.
     */
    public static Region getTransportColorSquare(TransportColor transportColor) {
        Region colorSquare = new Region();
        colorSquare.setPrefSize(24, 16);
        colorSquare.setStyle("-fx-background-color: " + transportColor.getColorName() + ";");
        colorSquare.getStyleClass().add("tab-text");

        return colorSquare;
    }
}
