/******************************************************************************************************************************
 * File Name: FindStopsCommand.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.command.concrete.findStops;

import com.brunomnsilva.smartgraph.graph.Vertex;
import pt.pa.controller.BaseController;
import pt.pa.model.Stop;
import pt.pa.model.TransportMap;
import pt.pa.patterns.command.UserActionCommand;

import static pt.pa.ui.resources.StringsResourceBundle.getStringResource;

/**
 * Represents a concrete command of the command pattern that clicks on the button to find stops at the find stops feature tab.
 */
public class FindStopsCommand extends UserActionCommand {
    /**
     * Represents the base controller at which the user action will be performed.
     */
    private final BaseController baseController;

    /**
     * Represents the origin stop vertex that the user has selected.
     */
    private final Vertex<Stop> originStopVertex;

    /**
     * Represents the distance number the user has selected.
     */
    private final int distance;

    /**
     * Represents the transport map where the user has performed the search.
     */
    private final TransportMap transportMap;

    /**
     * Constructs an instance of the SelectOriginStopCommand.
     *
     * @param baseController   The controller where the action will be performed.
     * @param originStopVertex The origin stop vertex that the user selected.
     * @param distance         The distance number to find stops.
     * @param transportMap     The transport map where the update happens.
     */
    public FindStopsCommand(BaseController baseController, Vertex<Stop> originStopVertex, int distance, TransportMap transportMap) {
        super(getStringResource("userActionDescription.findStops.findStops"),
                originStopVertex.element().getName() + " : " + distance);

        this.baseController = baseController;
        this.originStopVertex = originStopVertex;
        this.distance = distance;
        this.transportMap = transportMap;
    }

    /**
     * Executes the command to find stops within the specified distance from the origin stop.
     */
    @Override
    public void execute() {
        baseController.notifyFoundStops(originStopVertex, distance, transportMap.findStopsByDistance(originStopVertex, distance));
    }
}
