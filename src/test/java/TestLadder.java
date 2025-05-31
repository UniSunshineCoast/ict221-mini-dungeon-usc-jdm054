import dungeon.engine.Ladder;
import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestLadder {
    @Test
    void testGetSymbol() {
        Ladder ladder = new Ladder();
        assertEquals('L', ladder.getSymbol());
    }

    @Test
    void testOnPlayerEnter() {
        Ladder ladder = new Ladder();
        GameEngine engine = new GameEngine(5, 1);
        String msg = ladder.onPlayerEnter(engine, 0, 0);
        assertTrue(msg.contains("ladder"));
    }
}

