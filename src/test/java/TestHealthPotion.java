import dungeon.engine.HealthPotion;
import dungeon.engine.GameEngine;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestHealthPotion {
    @Test
    void testGetSymbol() {
        HealthPotion hp = new HealthPotion();
        assertEquals('H', hp.getSymbol());
    }

    @Test
    void testOnPlayerEnter() {
        GameEngine engine = new GameEngine(5, 1);
        engine.getPlayer().setHealth(5);
        String msg = new HealthPotion().onPlayerEnter(engine, 0, 0);
        assertEquals(9, engine.getPlayer().getHealth());
        assertTrue(msg.contains("health potion"));
    }
}

