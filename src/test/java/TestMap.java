import dungeon.engine.Map;
import dungeon.engine.Wall;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestMap {
    @Test
    void testIsPath() {
        Map map = new Map(10, 1);
        // Out of bounds
        assertFalse(map.isPath(0, 0, -1, 0));
        // Path cell
        assertTrue(map.isPath(0, 0, 0, 0));
        // Wall cell
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                if (map.getCell(i, j).getItem() instanceof Wall) {
                    assertFalse(map.isPath(0, 0, i, j));
                }
            }
        }
    }
}

