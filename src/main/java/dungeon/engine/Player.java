package dungeon.engine;

public class Player implements MapItem {
    private int x;
    private int y;
    private int health;
    private int score;
    int steps;

    private static final int MIN_HEALTH = 0;
    private static final int MAX_HEALTH = 10;

    public Player(int x, int y) {
        this.x = x;
        this.y = y;
        this.health = MAX_HEALTH;
        this.score = 0;
        this.steps = 0;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public char getSymbol() {
        return 'P';
    }

    public int getHealth() { return health; }
    public void setHealth(int health) {
        this.health = Math.max(MIN_HEALTH, Math.min(MAX_HEALTH, health));
    }
    public void changeHealth(int delta) {
        setHealth(this.health + delta);
    }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public void addScore(int delta) { this.score += delta; }

    public int getSteps() { return steps; }
    public void incrementSteps() { steps++; }
}
