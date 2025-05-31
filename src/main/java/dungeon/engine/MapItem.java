package dungeon.engine;

public interface MapItem {
    char getSymbol();
    default String onPlayerEnter(GameEngine engine, int x, int y) {
        return "";
    }
}
