/******************************************************************************************************************************
 * File Name: PathfindingStrategyFactoryTest.java
 * Authors: Diogo Santos - 202200508 (PL6)
 *          Diogo Veigas - 202200879 (PL5)
 *          Serhiy Kukurudza - 202200521 (PL6)
 * Teacher: Luís Damas
 */

package pt.pa.patterns.factory.simple;

import org.junit.jupiter.api.Test;
import pt.pa.model.CostType;
import pt.pa.patterns.strategy.NegativePathfindingStrategy;
import pt.pa.patterns.strategy.PathfindingStrategy;
import pt.pa.patterns.strategy.PositivePathfindingStrategy;

import static org.junit.jupiter.api.Assertions.*;

class PathfindingStrategyFactoryTest {

    @Test
    void createPathfindingStrategy_returnsCorrectInstanceByType() {
        assertInstanceOf(PositivePathfindingStrategy.class, PathfindingStrategyFactory.create(CostType.DISTANCE), "Expected a positive pathfinding strategy for the distance cost type");

        assertInstanceOf(PositivePathfindingStrategy.class, PathfindingStrategyFactory.create(CostType.DURATION), "Expected a positive pathfinding strategy for the duration cost type");

        assertInstanceOf(NegativePathfindingStrategy.class, PathfindingStrategyFactory.create(CostType.COST), "Expected a negative pathfinding strategy for the cost cost type");
    }

    @Test
    void createPathfindingStrategy_throwsIllegalArgumentException_whenCostIsNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            PathfindingStrategyFactory.create(null);
        }, "Should have thrown an exception with a null cost type");

        assertDoesNotThrow(() -> {
            PathfindingStrategyFactory.create(CostType.COST);
        }, "Should not have thrown an exception with a valid cost type");
    }
}