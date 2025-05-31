import dungeon.engine.RangedMutant;
import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestRangedMutant {
    @Test
    void testGetSymbol() {
        RangedMutant mutant = new RangedMutant();
        assertEquals('R', mutant.getSymbol());
    }

    @Test
    void testOnPlayerEnter() {
        GameEngine engine = new GameEngine(5, 1);
        int oldScore = engine.getPlayer().getScore();
        String msg = new RangedMutant().onPlayerEnter(engine, 0, 0);
        assertEquals(oldScore + 2, engine.getPlayer().getScore());
        assertTrue(msg.contains("ranged mutant"));
    }

    @Test
    void testTryAttackPlayer() {
        GameEngine engine = new GameEngine(5, 1);
        engine.getPlayer().setHealth(10);
        RangedMutant mutant = new RangedMutant();
        // Should only attack if player is 2 cells away in line
        String result = mutant.tryAttackPlayer(engine, 0, 0, 2, 0);
        assertNotNull(result); // Should return a message (hit or miss)
    }
}

