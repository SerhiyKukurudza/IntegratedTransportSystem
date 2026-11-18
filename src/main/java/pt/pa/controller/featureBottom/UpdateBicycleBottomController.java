/******************************************************************************************************************************
 * File Name: UpdateBicycleBottom.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller.featureBottom;

import javafx.fxml.FXML;
import javafx.scene.layout.Pane;
import pt.pa.controller.BaseController;
import pt.pa.controller.TransportBaseController;
import pt.pa.model.TransportMap;

/**
 * Represents the controller class for the UpdateBicycleBottom view.
 */
public class UpdateBicycleBottomController extends TransportBaseController implements FeatureBottomController {

    /**
     * Represents the background pane of the design route bottom tab.
     */
    @FXML
    private Pane bottomUpdateBicyclePane;

    /**
     * Constructs a UpdateBicycleBottomController instance with a transport map model and its base controller.
     *
     * @param transportMap The transport map instance.
     * @param baseController The base controller instance.
     */
    public UpdateBicycleBottomController(TransportMap transportMap, BaseController baseController) {
        super(transportMap, baseController);
    }

    /**
     * Initializes every aspect of the design route bottom tab.
     * This method is executed during the initialization phase of the controller.
     */
    @FXML
    private void initialize() {
    }
}

