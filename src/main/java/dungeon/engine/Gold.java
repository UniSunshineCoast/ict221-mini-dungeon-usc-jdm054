package dungeon.engine;

public class Gold implements MapItem {
    @Override
    public char getSymbol() {
        return 'G';
    }

    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        engine.getPlayer().addScore(2);
        return "You found gold! +2 score.";
    }
}

