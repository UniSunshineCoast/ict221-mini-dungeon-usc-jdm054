import dungeon.engine.MeleeMutant;
import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMeleeMutant {
    @Test
    void testGetSymbol() {
        MeleeMutant mutant = new MeleeMutant();
        assertEquals('M', mutant.getSymbol());
    }

    @Test
    void testOnPlayerEnter() {
        GameEngine engine = new GameEngine(5, 1);
        engine.getPlayer().setHealth(10);
        String msg = new MeleeMutant().onPlayerEnter(engine, 0, 0);
        assertEquals(8, engine.getPlayer().getHealth());
        assertTrue(msg.contains("mutant"));
    }
}

