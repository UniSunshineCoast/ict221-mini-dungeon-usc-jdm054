package dungeon.engine;

import java.io.*;
import java.util.*;

public class GameEngine {
    private static final int DEFAULT_SIZE = 10;
    private static final int MAX_STEPS = 100;
    private static final int MAX_TOP_SCORES = 5;

    private Map map;
    private Player player;
    private int size;
    private int difficulty;
    private int level = 1;
    private int entryX, entryY;
    private int ladderX, ladderY;
    private int playerX, playerY;
    private boolean gameOver = false;
    private boolean win = false;

    // Item counts
    private int trapCount = 5;
    private int goldCount = 5;
    private int healthPotionCount = 2;
    private int meleeMutantCount = 3;
    private int rangedMutantCount;

    public GameEngine(int size, int difficulty) {
        this.size = size;
        this.difficulty = difficulty;
        this.rangedMutantCount = difficulty;
        this.level = 1;
        this.goldCount = 5;
        this.trapCount = 5;
        this.meleeMutantCount = 3;
        this.healthPotionCount = 2;
        initLevel();
    }

    private void initLevel() {
        map = new Map(size, level);
        // Set entry at bottom left for level 1, or where ladder was for level 2
        if (level == 1) {
            entryX = size - 1;
            entryY = 0;
        }
        playerX = entryX;
        playerY = entryY;
        player = new Player(playerX, playerY);
        map.getCell(playerX, playerY).setItem(player);

        // Place random ladder (not at entry, not on wall)
        do {
            ladderX = (int) (Math.random() * map.getSize());
            ladderY = (int) (Math.random() * map.getSize());
        } while ((ladderX == entryX && ladderY == entryY) || map.getCell(ladderX, ladderY).getItem() instanceof Wall);
        map.getCell(ladderX, ladderY).setItem(new Ladder());

        // Place all items, skipping entry and ladder cells, and not on walls
        placeRandomItems();
    }

    private void placeRandomItems() {
        placeRandom(Gold.class, goldCount);
        placeRandom(Trap.class, trapCount);
        placeRandom(MeleeMutant.class, meleeMutantCount);
        placeRandom(RangedMutant.class, rangedMutantCount);
        placeRandom(HealthPotion.class, healthPotionCount);
    }

    private void placeRandom(Class<? extends MapItem> clazz, int count) {
        int placed = 0;
        while (placed < count) {
            int x = (int) (Math.random() * map.getSize());
            int y = (int) (Math.random() * map.getSize());
            // Never place on entry, ladder, or wall
            if ((x == entryX && y == entryY) || (x == ladderX && y == ladderY)) continue;
            Cell cell = map.getCell(x, y);
            if (cell.getItem() == null && !(cell.getItem() instanceof Wall)) {
                try {
                    cell.setItem(clazz.getDeclaredConstructor().newInstance());
                    placed++;
                } catch (Exception ignored) {}
            }
        }
    }

    public int getSize() { return map.getSize(); }
    public Cell[][] getMap() { return map.getCells(); }
    public Player getPlayer() { return player; }
    public int getLevel() { return level; }
    public int getDifficulty() { return difficulty; }
    public boolean isGameOver() { return gameOver; }
    public boolean isWin() { return win; }
    public int getEntryX() { return entryX; }
    public int getEntryY() { return entryY; }
    public int getLadderX() { return ladderX; }
    public int getLadderY() { return ladderY; }

    public void setDifficulty(int d) {
        this.difficulty = d;
        this.rangedMutantCount = d;
    }

    public String movePlayer(String direction) {
        if (gameOver) return win ? "Game over! You win!" : "Game over! You lose!";
        int dx = 0, dy = 0;
        switch (direction.toLowerCase()) {
            case "up" -> dx = -1;
            case "down" -> dx = 1;
            case "left" -> dy = -1;
            case "right" -> dy = 1;
            default -> { return "Invalid direction."; }
        }
        int newX = playerX + dx;
        int newY = playerY + dy;
        if (newX < 0 || newX >= size || newY < 0 || newY >= size) {
            return "You can't move outside the map!";
        }
        // Don't move into a wall
        if (map.getCell(newX, newY).getItem() instanceof Wall) {
            return "You can't move into a wall!";
        }
        MapItem destItem = map.getCell(newX, newY).getItem();
        StringBuilder result = new StringBuilder();
        // Interact with item
        if (destItem != null && !(destItem instanceof Player)) {
            result.append(destItem.onPlayerEnter(this, newX, newY));
        }
        // Clear cell if not a trap
        clearCell(map.getCell(playerX, playerY));
        // Move player
        playerX = newX;
        playerY = newY;
        player.setPosition(playerX, playerY); // Added to update Player object's internal coordinates
        Cell destCell = map.getCell(playerX, playerY);
        if (!(destCell.getItem() instanceof Trap)) destCell.setItem(player);

        // Increment steps after moving
        player.incrementSteps();

        // Check if player stepped on ladder
        checkLadder();

        // Check for ranged mutant attacks
        for (int i = 0; i < size; i++)
            for (int j = 0; j < size; j++) {
                MapItem item = map.getCell(i, j).getItem();
                if (item instanceof RangedMutant) {
                    if (hasLineOfSight(i, j, playerX, playerY)) {
                        String attackMsg = ((RangedMutant) item).tryAttackPlayer(this, i, j, playerX, playerY);
                        if (attackMsg != null) result.append("\n").append(attackMsg);
                    }
                }
            }

        if (player.getHealth() <= 0) {
            gameOver = true;
            win = false;
            return result + "\nYou lost all your HP! Game over!";
        }
        if (player.getSteps() >= MAX_STEPS) {
            gameOver = true;
            win = false;
            return result + "\nYou ran out of steps! Game over!";
        }
        if (result.length() == 0) {
            return "You moved " + direction + ".";
        }
        return result.toString();
    }

    private boolean hasLineOfSight(int x1, int y1, int x2, int y2) {
        if (x1 == x2) {
            int min = Math.min(y1, y2), max = Math.max(y1, y2);
            for (int y = min + 1; y < max; y++)
                if (map.getCell(x1, y).getItem() instanceof Wall) return false;
            return Math.abs(y1 - y2) <= 2;
        } else if (y1 == y2) {
            int min = Math.min(x1, x2), max = Math.max(x1, x2);
            for (int x = min + 1; x < max; x++)
                if (map.getCell(x, y1).getItem() instanceof Wall) return false;
            return Math.abs(x1 - x2) <= 2;
        }
        return false;
    }

    private void clearCell(Cell cell) {
        MapItem item = cell.getItem();
        if (!(item instanceof Trap)) {
            cell.setItem(null);
        }
    }

    public String useLadder() {
        if (playerX == ladderX && playerY == ladderY) {
            if (level == 2) {
                win = true;
                gameOver = true;
                saveTopScore();
                return "You reached the ladder! You win!";
            } else {
                // Advance to level 2
                level = 2;
                difficulty += 2;
                rangedMutantCount = difficulty;
                entryX = ladderX;
                entryY = ladderY;
                initLevel();
                return "You advanced to Level 2!";
            }
        } else {
            return "You must be on the ladder to use it.";
        }
    }

    public void checkLadder() {
        if (playerX == ladderX && playerY == ladderY) {
            useLadder();
        }
    }

    public void saveGame() {
        try (PrintWriter out = new PrintWriter(new FileWriter("savegame.txt"))) {
            out.println(playerX + "," + playerY);
            out.println(player.getHealth() + "," + player.getScore() + "," + player.getSteps());
            out.println(level + "," + difficulty);
            out.println(entryX + "," + entryY);
            out.println(ladderX + "," + ladderY);
            for (int i = 0; i < map.getSize(); i++) {
                for (int j = 0; j < map.getSize(); j++) {
                    MapItem item = map.getCell(i, j).getItem();
                    if (item != null && !(item instanceof Player)) {
                        String type;
                        if (item instanceof Gold) type = "Gold";
                        else if (item instanceof Trap) type = "Trap";
                        else if (item instanceof MeleeMutant) type = "MeleeMutant";
                        else if (item instanceof RangedMutant) type = "RangedMutant";
                        else if (item instanceof HealthPotion) type = "HealthPotion";
                        else if (item instanceof Wall) type = "Wall";
                        else if (item instanceof Ladder) type = "Ladder";
                        else type = item.getClass().getSimpleName();
                        out.println(i + "," + j + "," + type);
                    }
                }
            }
            out.println("END");
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
        }
    }

    public void loadGame() {
        try (BufferedReader in = new BufferedReader(new FileReader("savegame.txt"))) {
            String[] pos = in.readLine().split(",");
            playerX = Integer.parseInt(pos[0]);
            playerY = Integer.parseInt(pos[1]);
            String[] stats = in.readLine().split(",");
            int health = Integer.parseInt(stats[0]);
            int score = Integer.parseInt(stats[1]);
            int steps = Integer.parseInt(stats[2]);
            String[] lvl = in.readLine().split(",");
            level = Integer.parseInt(lvl[0]);
            difficulty = Integer.parseInt(lvl[1]);
            String[] entry = in.readLine().split(",");
            entryX = Integer.parseInt(entry[0]);
            entryY = Integer.parseInt(entry[1]);
            String[] ladder = in.readLine().split(",");
            ladderX = Integer.parseInt(ladder[0]);
            ladderY = Integer.parseInt(ladder[1]);
            // Re-initialize map and player for the loaded level
            map = new Map(getSize(), level);
            // Set entry position for the loaded level
            // (entryX, entryY) and (ladderX, ladderY) are already set from file
            // Clear map
            for (int i = 0; i < map.getSize(); i++)
                for (int j = 0; j < map.getSize(); j++)
                    map.getCell(i, j).setItem(null);
            // Recreate player
            player = new Player(playerX, playerY);
            player.setHealth(health);
            player.setScore(score);
            player.steps = steps;
            map.getCell(playerX, playerY).setItem(player);
            String line;
            while (!(line = in.readLine()).equals("END")) {
                String[] parts = line.split(",");
                int i = Integer.parseInt(parts[0]);
                int j = Integer.parseInt(parts[1]);
                String type = parts[2];
                MapItem item = switch (type) {
                    case "Gold" -> new Gold();
                    case "Trap" -> new Trap();
                    case "MeleeMutant" -> new MeleeMutant();
                    case "RangedMutant" -> new RangedMutant();
                    case "HealthPotion" -> new HealthPotion();
                    case "Wall" -> new Wall();
                    case "Ladder" -> new Ladder();
                    default -> null;
                };
                if (item != null) {
                    map.getCell(i, j).setItem(item);
                    if (item instanceof Ladder) {
                        ladderX = i;
                        ladderY = j;
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading game: " + e.getMessage());
        }
    }

    public void saveTopScore() {
        try (PrintWriter out = new PrintWriter(new FileWriter("topscores.txt", true))) {
            out.println(player.getScore() + "," + new Date());
        } catch (IOException e) {
            System.err.println("Error saving score: " + e.getMessage());
        }
    }

    public List<String> getTopScores() {
        List<String> scores = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new FileReader("topscores.txt"))) {
            String line;
            while ((line = in.readLine()) != null) {
                scores.add(line);
            }
        } catch (IOException e) {
            // ignore if file doesn't exist
        }
        scores.sort((a, b) -> Integer.compare(
                Integer.parseInt(b.split(",")[0]),
                Integer.parseInt(a.split(",")[0])
        ));
        return scores.size() > MAX_TOP_SCORES ? scores.subList(0, MAX_TOP_SCORES) : scores;
    }

    public String getCellIcon(int x, int y) {
        if (playerX == x && playerY == y) return "/player.png";
        if (x == entryX && y == entryY) return "/entry.png";
        if (x == ladderX && y == ladderY) return "/ladder.png";
        MapItem item = map.getCell(x, y).getItem();
        if (item instanceof Gold) return "/gold.png";
        if (item instanceof Trap) return "/trap.png";
        if (item instanceof Wall) return "/wall.png";
        if (item instanceof HealthPotion) return "/healthpotion.png";
        if (item instanceof MeleeMutant) return "/meleemutant.png";
        if (item instanceof RangedMutant) return "/rangedmutant.png";
        return "";
    }

    // --- Text UI ---
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("This is a text based Mini Dungeon game.");
        System.out.print("Select difficulty (0-10): ");
        int difficulty;
        try {
            difficulty = Integer.parseInt(scanner.nextLine().trim());
            if (difficulty < 0) difficulty = 0;
            if (difficulty > 10) difficulty = 10;
        } catch (Exception e) {
            System.out.println("Invalid input. Using default difficulty 3.");
            difficulty = 3;
        }
        GameEngine engine = new GameEngine(DEFAULT_SIZE, difficulty);
        System.out.println("Game started! Use commands: up, down, left, right, help, quit");
        while (!engine.isGameOver()) {
            printMap(engine);
            System.out.print("Your move: ");
            String cmd = scanner.nextLine().trim().toLowerCase();
            switch (cmd) {
                case "quit" -> {
                    System.out.println("Quitting game.");
                    return;
                }
                case "help" -> System.out.println("Use up/down/left/right or Arrows to move. Collect gold, avoid traps, and reach the ladder to win!");
                case "use" -> System.out.println(engine.useLadder());
                case "up", "down", "left", "right" -> System.out.println(engine.movePlayer(cmd));
                default -> System.out.println("Unknown command. Type 'help' for options.");
            }
        }
        if (engine.isGameOver()) {
            if (engine.isWin()) {
                System.out.println("Congratulations! You win!");
            } else {
                System.out.println("Game over!");
            }
            engine.saveTopScore();
            System.out.println("Top Scores:");
            int rank = 1;
            for (String score : engine.getTopScores()) {
                System.out.println("#" + rank + ": " + score);
                rank++;
            }
        }
        scanner.close();
    }

    private static void printMap(GameEngine engine) {
        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                boolean isPlayer = (engine.playerX == i && engine.playerY == j);
                boolean isEntry = (i == engine.getEntryX() && j == engine.getEntryY());
                boolean isLadder = (i == engine.getLadderX() && j == engine.getLadderY());
                if (isPlayer && isLadder) {
                    System.out.print("PL ");
                } else if (isPlayer) {
                    System.out.print("P ");
                } else if (isLadder) {
                    System.out.print("L ");
                } else if (isEntry) {
                    System.out.print("E ");
                } else {
                    var item = engine.getMap()[i][j].getItem();
                    if (item == null) System.out.print(". ");
                    else if (item instanceof Trap) System.out.print("T ");
                    else if (item instanceof Gold) System.out.print("G ");
                    else if (item instanceof Wall) System.out.print("# ");
                    else if (item instanceof HealthPotion) System.out.print("H ");
                    else if (item instanceof MeleeMutant) System.out.print("M ");
                    else if (item instanceof RangedMutant) System.out.print("R ");
                    else System.out.print("? ");
                }
            }
            System.out.println();
        }
        System.out.printf("Level: %d  Difficulty: %d  HP: %d  Score: %d  Steps: %d\n",
                engine.getLevel(), engine.getDifficulty(), engine.getPlayer().getHealth(), engine.getPlayer().getScore(), engine.getPlayer().getSteps());
    }
}
