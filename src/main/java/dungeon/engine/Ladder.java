package dungeon.engine;

public class Ladder implements MapItem {
    @Override
    public char getSymbol() {
        return 'L';
    }

    public String onPlayerEnter(GameEngine engine, int x, int y) {
        return "You found the ladder! Use it to advance to the next level.";
    }
}

