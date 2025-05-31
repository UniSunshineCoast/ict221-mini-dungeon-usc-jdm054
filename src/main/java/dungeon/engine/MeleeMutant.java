package dungeon.engine;

public class MeleeMutant implements MapItem {
    @Override
    public char getSymbol() {
        return 'M';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        engine.getPlayer().changeHealth(-2);
        return "You encountered a mutant and lost 2 health, but gained 2 score.";
    }
}

