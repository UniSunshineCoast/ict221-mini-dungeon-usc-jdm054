import dungeon.engine.GameEngine;
import dungeon.engine.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class TestGameEngine {
    @Test
    void testGetSize() {
        GameEngine ge = new GameEngine(10, 1); // Added difficulty
        assertEquals(10, ge.getSize());
    }

    @Test
    void testGameEngineInitialization() {
        GameEngine ge = new GameEngine(10, 3);
        assertEquals(10, ge.getSize());
        assertEquals(3, ge.getDifficulty());
        assertEquals(1, ge.getLevel());
        assertNotNull(ge.getPlayer());
        assertFalse(ge.isGameOver());
        assertFalse(ge.isWin());
        // Check if player is at entry
        assertEquals(ge.getPlayer().getX(), ge.getEntryX());
        assertEquals(ge.getPlayer().getY(), ge.getEntryY());
        // Check if ladder is placed within bounds
        assertTrue(ge.getLadderX() >= 0 && ge.getLadderX() < ge.getSize());
        assertTrue(ge.getLadderY() >= 0 && ge.getLadderY() < ge.getSize());
    }

    @Test
    void testPlayerInitialPosition() {
        GameEngine ge = new GameEngine(10, 1);
        Player player = ge.getPlayer();
        assertNotNull(player);
        // Entry is at (size-1, 0) for level 1
        assertEquals(9, player.getX());
        assertEquals(0, player.getY());
        assertEquals(9, ge.getEntryX());
        assertEquals(0, ge.getEntryY());
    }

    @Test
    void testMovePlayerValid() {
        // For this test, we need to ensure the player doesn't immediately hit a wall or an item
        // This is tricky with random generation. We'll assume a simple move is possible.
        // A more robust test would involve mocking the map or finding a clear path.
        GameEngine ge = new GameEngine(10, 0); // Difficulty 0 for fewer mutants initially
        // Player starts at (9,0). A move "up" should be to (8,0) if no wall.
        // We can't guarantee (8,0) is not a wall without more control.
        // Let's try to move and check if position changes or a message is returned.
        String result = ge.movePlayer("up"); // Moves from (9,0) to (8,0) if possible
        Player player = ge.getPlayer();

        // We can't assert exact position due to random walls/items.
        // But we can check if the game is not over and player's steps increased.
        if (!result.contains("wall") && !result.contains("can't move")) {
            assertTrue(player.getX() < 10 && player.getX() >= 0); // Check X is within bounds
            assertTrue(player.getY() < 10 && player.getY() >= 0); // Check Y is within bounds
            assertEquals(1, player.getSteps());
        } else {
            // If move was blocked, player position should not change
            assertEquals(9, player.getX());
            assertEquals(0, player.getY());
            assertEquals(0, player.getSteps());
        }
    }

    @Test
    void testMovePlayerOutOfBounds() {
        GameEngine ge = new GameEngine(10, 1);
        // Player starts at (9,0)
        String result = ge.movePlayer("left"); // Try to move from (9,0) to (9,-1)
        assertEquals("You can't move outside the map!", result);
        assertEquals(9, ge.getPlayer().getX());
        assertEquals(0, ge.getPlayer().getY());
        assertEquals(0, ge.getPlayer().getSteps());

        result = ge.movePlayer("down"); // Try to move from (9,0) to (10,0)
        assertEquals("You can't move outside the map!", result);
        assertEquals(9, ge.getPlayer().getX());
        assertEquals(0, ge.getPlayer().getY());
        assertEquals(0, ge.getPlayer().getSteps());
    }

    @Test
    void testMovePlayerIntoWall() {
        GameEngine ge = new GameEngine(5, 0); // Small map, no ranged mutants
        // Manually place a wall next to the player for a predictable test
        // Player starts at (4,0)
        // Place a wall at (3,0)
        ge.getMap()[3][0].setItem(new dungeon.engine.Wall());

        String result = ge.movePlayer("up"); // Try to move from (4,0) to (3,0)
        assertEquals("You can't move into a wall!", result);
        assertEquals(4, ge.getPlayer().getX());
        assertEquals(0, ge.getPlayer().getY());
        assertEquals(0, ge.getPlayer().getSteps());
    }

    @Test
    void testGameOverByHealth() {
        GameEngine ge = new GameEngine(10, 1);
        Player player = ge.getPlayer();
        player.setHealth(0); // Directly set health to 0
        // A move is needed to trigger the game over check in movePlayer
        ge.movePlayer("up"); // Any move will do
        assertTrue(ge.isGameOver());
        assertFalse(ge.isWin());
    }

    @Test
    void testGameOverBySteps() {
        GameEngine ge = new GameEngine(10, 1);
        Player player = ge.getPlayer();
        for (int i = 0; i < 100; i++) { // MAX_STEPS is 100
            player.incrementSteps();
        }
        // A move is needed to trigger the game over check in movePlayer
        String result = ge.movePlayer("up"); // Any move will do
        assertTrue(result.contains("You ran out of steps! Game over!"));
        assertTrue(ge.isGameOver());
        assertFalse(ge.isWin());
    }

    @Test
    void testSetDifficulty() {
        GameEngine ge = new GameEngine(10, 1);
        assertEquals(1, ge.getDifficulty());
        ge.setDifficulty(5);
        assertEquals(5, ge.getDifficulty());
        // RangedMutantCount should also be updated
        // This requires inspecting private fields or adding a getter,
        // for now, we trust it's set as per GameEngine logic.
    }

    // Note: Testing interactions with randomly placed items (Gold, Trap, Mutants)
    // is more complex and would ideally involve:
    // 1. Mocking the Map and item placement.
    // 2. Iterating through the map to find an item and then moving the player to it.
    // These are more advanced tests.
}
