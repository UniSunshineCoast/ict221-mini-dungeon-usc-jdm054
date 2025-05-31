package dungeon.engine;

public class HealthPotion implements MapItem {
    @Override
    public char getSymbol() {
        return 'H';
    }

    public String onPlayerEnter(GameEngine engine, int x, int y) {
        engine.getPlayer().changeHealth(4);
        return "You found a health potion! +4 Health Points.";
    }
}

