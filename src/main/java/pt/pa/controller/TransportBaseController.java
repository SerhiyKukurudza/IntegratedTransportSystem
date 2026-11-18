/******************************************************************************************************************************
 * File Name: TransportBaseController.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.controller;

import pt.pa.model.TransportMap;

/**
 * Represents a class controller with its transport map model.
 * Used as an extension to the different controllers which model is a transport map.
 */
public class TransportBaseController {
    /**
     * Represents the transportation map used across the application.
     * It is used from both tabs and bottom menu controllers.
     */
    protected final TransportMap transportMap;

    /**
     * Represents the base controller instance that manages the controller that extends the TransportBaseController.
     */
    protected BaseController baseController;

    /**
     * Constructs a TransportBaseController instance that controls a transportation map.
     * This constructor should be used with dependency injection, where the base controller instance will be defined later.
     *
     * @param transportMap The transport map this controller will control.
     */
    public TransportBaseController(TransportMap transportMap) {
        this.transportMap = transportMap;
    }

    /**
     * Constructs a TransportBaseController instance that controls a transportation map.
     * This class will usually be only constructed inside its subclasses constructors.
     *
     * @param transportMap   The transport map this controller will control.
     * @param baseController The controller of the base controller where every subclass stands
     */
    public TransportBaseController(TransportMap transportMap, BaseController baseController) {
        this(transportMap);
        this.baseController = baseController;
    }

    /**
     * Sets the base controller after the construction of the instance.
     * Used for dependency injection.
     *
     * @param baseController The base/parent controller of this controller.
     */
    public void setBaseController(BaseController baseController) {
        this.baseController = baseController;
    }
}
