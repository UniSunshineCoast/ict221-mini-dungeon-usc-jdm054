import dungeon.engine.Cell;
import dungeon.engine.Gold;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TestCell {
    @Test
    void testGetSymbolWithNullItem() {
        Cell cell = new Cell();
        assertEquals('.', cell.getSymbol());
    }

    @Test
    void testGetSymbolWithItem() {
        Cell cell = new Cell();
        cell.setItem(new Gold());
        assertEquals('G', cell.getSymbol());
    }
}

