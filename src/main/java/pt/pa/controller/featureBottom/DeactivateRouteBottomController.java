/******************************************************************************************************************************
 * File Name: FindStopsBottomController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureBottom;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.TransportMap;

/**
 * Represents the controller class for the FindStopsBottom view.
 */
public class DeactivateRouteBottomController extends TransportBaseController implements FeatureBottomController {
    /**
     * Represents the background pane of deactivate route bottom tab.
     */
    @FXML
    private Pane bottomDeactivateRoutePane;

    /**
     * Represents the tips version of the bottom menu.
     */
    @FXML
    private VBox vBoxTips;

    /**
     * Constructs a DeactivateRouteBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     */
    public DeactivateRouteBottomController(TransportMap transportMap) {
        super(transportMap);
    }

    /**
     * Initializes every aspect of deactivate route bottom tab.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
    }
}
