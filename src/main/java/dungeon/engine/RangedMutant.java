package dungeon.engine;

import java.util.Random;

public class RangedMutant implements MapItem {
    private static final Random random = new Random();

    @Override
    public char getSymbol() {
        return 'R';
    }
    @Override
    public String onPlayerEnter(GameEngine engine, int x, int y) {
        Player player = engine.getPlayer();
        player.setScore(player.getScore() + 2);
        engine.getMap()[x][y].setItem(null); // Remove mutant from cell
        return "You defeated a ranged mutant and gained 2 points.";
    }

    /**
     * Called by GameEngine to check if the player is in range and attack.
     */
    public String tryAttackPlayer(GameEngine engine, int mutantX, int mutantY, int playerX, int playerY) {
        if ((Math.abs(mutantX - playerX) == 2 && mutantY == playerY) ||
                (Math.abs(mutantY - playerY) == 2 && mutantX == playerX)) {
            if (random.nextBoolean()) {
                engine.getPlayer().setHealth(engine.getPlayer().getHealth() - 2);
                return "Lost 2 health points from a ranged mutant attack!";
            } else {
                return "Attack missed from the ranged mutant!";
            }
        }
        return null;
    }
}
