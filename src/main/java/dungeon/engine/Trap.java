package dungeon.engine;

public class Trap implements MapItem {
    @Override
    public char getSymbol() {
        return 'T';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        engine.getPlayer().changeHealth(-2);
        // Trap remains active, do not remove from cell
        return "You stepped in acid! You lost 2 HP.";
    }
}
