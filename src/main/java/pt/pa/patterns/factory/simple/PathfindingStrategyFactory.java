/******************************************************************************************************************************
 * File Name: PathfindingStrategyFactory.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.factory.simple;

import pt.pa.model.CostType;
import pt.pa.patterns.strategy.NegativePathfindingStrategy;
import pt.pa.patterns.strategy.PathfindingStrategy;
import pt.pa.patterns.strategy.PositivePathfindingStrategy;

/**
 * Represents the simple factory of the PathfindingStrategy.
 */
public class PathfindingStrategyFactory {
    /**
     * Creates a new instance of a PathfindingStrategy according to the provided cost type.
     * <p>
     * The possible arguments are the different {@link CostType} options.
     * </p>
     *
     * @param costType The type of cost used in the pathfinding.
     * @return The best pathfinding strategy according to the provided cost type.
     */
    public static PathfindingStrategy create(CostType costType) {
        if (costType == null) {
            throw new IllegalArgumentException("CostType must not be null");
        }

        switch (costType) {
            case DISTANCE, DURATION -> {
                return new PositivePathfindingStrategy();
            }
            case COST -> {
                return new NegativePathfindingStrategy();
            }
        }

        return null;
    }
}
