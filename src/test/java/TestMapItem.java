import dungeon.engine.MapItem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMapItem {
    static class DummyItem implements MapItem {
        @Override
        public char getSymbol() { return 'X'; }
    }

    @Test
    void testDefaultOnPlayerEnter() {
        MapItem item = new DummyItem();
        String result = item.onPlayerEnter(null, 0, 0);
        assertEquals("", result);
    }

    @Test
    void testGetSymbol() {
        MapItem item = new DummyItem();
        assertEquals('X', item.getSymbol());
    }
}

