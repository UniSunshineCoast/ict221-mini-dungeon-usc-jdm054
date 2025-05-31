package dungeon.engine;

public class Wall implements MapItem {
    @Override
    public char getSymbol() {
        return '#';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        return "You tried to move but it is a wall.";
    }
}
