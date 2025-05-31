import dungeon.engine.Gold;
import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestGold {
    @Test
    void testGetSymbol() {
        Gold gold = new Gold();
        assertEquals('G', gold.getSymbol());
    }

    @Test
    void testOnPlayerEnter() {
        GameEngine engine = new GameEngine(5, 1);
        int oldScore = engine.getPlayer().getScore();
        String msg = new Gold().onPlayerEnter(engine, 0, 0);
        assertEquals(oldScore + 2, engine.getPlayer().getScore());
        assertTrue(msg.contains("gold"));
    }
}

