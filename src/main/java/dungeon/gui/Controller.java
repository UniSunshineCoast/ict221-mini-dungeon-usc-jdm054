package dungeon.gui;

import dungeon.engine.GameEngine;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.io.InputStream;

public class Controller {
    @FXML
    private GridPane gridPane;
    @FXML
    private Label hpLabel;
    @FXML
    private Label scoreLabel;
    @FXML
    private Label stepsLabel;
    @FXML
    private Label levelLabel;
    @FXML
    private TextArea statusArea;
    @FXML
    private VBox topScoresBox;
    @FXML
    private ImageView upButtonImage;
    @FXML
    private ImageView downButtonImage;
    @FXML
    private ImageView leftButtonImage;
    @FXML
    private ImageView rightButtonImage;
    @FXML
    private TextField difficultyInput;
    @FXML
    private Button applyDifficultyButton;

    private GameEngine engine;
    private static final int DEFAULT_GAME_SIZE = 10;
    private static final int DEFAULT_DIFFICULTY = 3;

    private Image loadImage(String resourcePath, String description) {
        InputStream stream = getClass().getResourceAsStream(resourcePath);
        if (stream == null) {
            System.err.println("Failed to load image resource: " + resourcePath + " for " + description);
            if (statusArea != null) {
                statusArea.appendText("Error: Missing image resource " + resourcePath + "\n");
            }
            return null;
        }
        try {
            return new Image(stream);
        } catch (Exception e) {
            System.err.println("Error creating image from stream: " + resourcePath + " for " + description);
            e.printStackTrace();
            if (statusArea != null) {
                statusArea.appendText("Error: Could not load image " + resourcePath + "\n");
            }
            return null;
        }
    }

    @FXML
    public void initialize() {
        try {
            // Load images for directional buttons
            Image upImg = loadImage("/upButtonImage.png", "Up Button");
            if (upButtonImage != null && upImg != null) upButtonImage.setImage(upImg);
            else if (upButtonImage == null) System.err.println("upButtonImage ImageView is null. Check fx:id.");

            Image downImg = loadImage("/downButtonImage.png", "Down Button");
            if (downButtonImage != null && downImg != null) downButtonImage.setImage(downImg);
            else if (downButtonImage == null) System.err.println("downButtonImage ImageView is null. Check fx:id.");

            Image leftImg = loadImage("/leftButtonImage.png", "Left Button");
            if (leftButtonImage != null && leftImg != null) leftButtonImage.setImage(leftImg);
            else if (leftButtonImage == null) System.err.println("leftButtonImage ImageView is null. Check fx:id.");

            Image rightImg = loadImage("/rightButtonImage.png", "Right Button");
            if (rightButtonImage != null && rightImg != null) rightButtonImage.setImage(rightImg);
            else if (rightButtonImage == null) System.err.println("rightButtonImage ImageView is null. Check fx:id.");

            // Initialize the game engine with default size and difficulty
            engine = new GameEngine(DEFAULT_GAME_SIZE, DEFAULT_DIFFICULTY);
            if (difficultyInput != null) {
                difficultyInput.setText(String.valueOf(DEFAULT_DIFFICULTY));
            } else {
                System.err.println("difficultyInput TextField is null. Check fx:id.");
            }
            updateGui();
            updateStats();
        } catch (Exception e) {
            System.err.println("Critical error during Controller initialize: " + e.getMessage());
            e.printStackTrace();
            if (statusArea != null) {
                statusArea.appendText("Critical error during initialization: " + e.getMessage() + "\n");
            } else {
                System.err.println("statusArea is null, cannot display initialization error in GUI.");
            }
        }
    }

    private void updateGui() {
        if (engine == null || engine.getPlayer() == null) {
            if (statusArea != null) statusArea.appendText("Error: Game engine not properly initialized.\n");
            else System.err.println("Error: Game engine not properly initialized and statusArea is null.");
            return;
        }
        // Update stats
        hpLabel.setText(String.valueOf(engine.getPlayer().getHealth()));
        scoreLabel.setText(String.valueOf(engine.getPlayer().getScore()));
        stepsLabel.setText(String.valueOf(engine.getPlayer().getSteps()));
        // Update grid
        gridPane.getChildren().clear();
        // Show win/game over message if game is over
        if (engine.isGameOver()) {
            if (engine.isWin()) {
                statusArea.appendText("Congratulations! You win!\n");
            } else {
                statusArea.appendText("Game over!\n");
            }
        }
        for (int i = 0; i < engine.getSize(); i++) {
            for (int j = 0; j < engine.getSize(); j++) {
                String iconPath = engine.getCellIcon(i, j);
                Image imgObj = null;
                if (iconPath != null && !iconPath.isEmpty()) {
                    imgObj = loadImage(iconPath, "Cell item " + i + "," + j);
                }

                StackPane cellPane = new StackPane();
                cellPane.setPrefSize(48, 48);
                cellPane.setStyle("-fx-border-color: #000000; -fx-background-color: transparent;");

                if (imgObj != null) {
                    ImageView img = new ImageView(imgObj);
                    img.setFitWidth(48);
                    img.setFitHeight(48);
                    cellPane.getChildren().add(img);
                }
                gridPane.add(cellPane, j, i);
            }
        }
        loadAndDisplayTopScores();
    }

    private void loadAndDisplayTopScores() {
        if (topScoresBox == null) {
            System.err.println("topScoresBox is null. Check fx:id.");
            return;
        }
        topScoresBox.getChildren().clear();
        if (engine != null) {
            int rank = 1;
            for (String score : engine.getTopScores()) {
                Label label = new Label("#" + rank + ": " + score);
                topScoresBox.getChildren().add(label);
                rank++;
            }
        }
    }

    private void updateStats() {
        if (engine == null || engine.getPlayer() == null) return;
        hpLabel.setText("HP: " + engine.getPlayer().getHealth());
        scoreLabel.setText("Score: " + engine.getPlayer().getScore());
        stepsLabel.setText("Steps: " + engine.getPlayer().getSteps());
        levelLabel.setText("Level: " + engine.getLevel());
    }

    private void updateStatus(String msg) {
        if (statusArea != null) {
            statusArea.appendText(msg + "\n");
        } else {
            System.out.println("Status Update (statusArea is null): " + msg);
        }
    }

    private void handleAutoLadder() {
        if (engine == null || engine.getPlayer() == null) return;
        if (engine.getPlayer().getX() == engine.getLadderX() && engine.getPlayer().getY() == engine.getLadderY()) {
            String ladderMsg = engine.useLadder();
            updateStatus(ladderMsg);
            updateGui();
            updateStats();
        }
    }

    private void doMove(String direction) {
        if (engine == null) {
            updateStatus("Game engine not ready.");
            return;
        }
        if (engine.isGameOver()) {
            updateStatus("Game is over. No more moves allowed.");
            return;
        }
        String moveMsg = engine.movePlayer(direction);
        updateStatus(moveMsg);
        updateGui();
        updateStats();
        if (!engine.isGameOver()) {
            handleAutoLadder();
        }
    }

    @FXML
    private void moveUp() {
        doMove("up");
    }

    @FXML
    private void moveDown() {
        doMove("down");
    }

    @FXML
    private void moveLeft() {
        doMove("left");
    }

    @FXML
    private void moveRight() {
        doMove("right");
    }

    @FXML
    private void saveGame() {
        engine.saveGame();
        if (!engine.isGameOver()) {
            statusArea.appendText("Game saved successfully.");
        }
    }
    @FXML
    private void loadGame() {
        engine.loadGame();
        if (!engine.isGameOver()) {
            statusArea.appendText("Game loaded successfully. You can continue playing.");
        }
        updateGui();
    }

    @FXML
    private void showHelp() {
        if (!engine.isGameOver()) {
            statusArea.appendText("Avoid or kill mutants, avoid traps, collect gold, increase score and get to the ladder. Arrow buttons to move.  \n");
        }
    }


    @FXML
    private void applyDifficulty() {
        if (difficultyInput == null) {
            updateStatus("Error: Difficulty input field not available.");
            return;
        }
        String diffText = difficultyInput.getText();
        try {
            int newDifficulty = Integer.parseInt(diffText);
            if (newDifficulty < 0 || newDifficulty > 10) {
                updateStatus("Invalid difficulty. Please enter a number between 0 and 10.");
                difficultyInput.setText(String.valueOf(engine.getDifficulty())); // Reset to current engine difficulty
                return;
            }
            engine = new GameEngine(DEFAULT_GAME_SIZE, newDifficulty);
            updateStatus("Difficulty set to " + newDifficulty + ". Game restarted.");
            difficultyInput.setText(String.valueOf(newDifficulty));
            updateGui();
            updateStats();
        } catch (NumberFormatException e) {
            updateStatus("Invalid difficulty format. Please enter a number.");
            difficultyInput.setText(engine != null ? String.valueOf(engine.getDifficulty()) : String.valueOf(DEFAULT_DIFFICULTY));
        } catch (Exception e) {
            updateStatus("Error applying difficulty: " + e.getMessage());
            System.err.println("Error applying difficulty: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
