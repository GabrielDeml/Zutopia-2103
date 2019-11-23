import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

public class GameImpl extends Pane implements Game {
    /**
     * Defines different states of the game.
     */
    public enum GameState {
        WON, LOST, ACTIVE, NEW
    }

    // Constants
    /**
     * The width of the game board.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int WIDTH = 400;
    /**
     * The height of the game board.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int HEIGHT = 600;
    /**
     * Allowed number of hits on bottom of screen before losing
     */
    private static final int BOTTOM_HITS_TO_LOSE = 5;
    /**
     * Animals to display per line
     */
    private static final int ANIMALS_PER_ROW = 4;
    /**
     * Starting number of animals
     */
    private static final int ANIMALS_TO_START = 16;
    /**
     * Animal image filenames that can be loaded
     */
    private static final String[] ANIMAL_IMAGES = {"duck.jpg", "goat.jpg", "horse.jpg"};
    /**
     * Number of pixels between animal blocks on the y axis
     */
    private static final int ANIMAL_IMAGE_Y_SEPARATION = 70;

    // Instance variables
    private Ball ball;
    private Paddle paddle;
    /**
     * The amount of times the ball his hit the bottom of the window
     */
    private int bottomScreenHitCounter = 0;
    /**
     * Images that are loaded and ready to display
     */
    private List<Label> animalImages = new LinkedList<>();

    /**
     * Constructs a new GameImpl.
     */
    @SuppressWarnings("WeakerAccess")
    public GameImpl() {
        setStyle("-fx-background-color: white;");
        restartGame(GameState.NEW);
    }

    /**
     * @return the name of the game
     */
    public String getName() {
        return "Zutopia";
    }

    /**
     * @return the pane to display (this object)
     */
    public Pane getPane() {
        return this;
    }

    /**
     * Removes all current animal images from the screen (if any) and fills screen with new images
     */
    private void resetAnimalImages() {
        // Clear the screen and reset vars
        getChildren().removeAll(animalImages);
        animalImages = new LinkedList<>();
        //For the number of animals that we want
        for (int i = 0; i < ANIMALS_TO_START; ++i) {
            // Load image from disk
            final String animalImageFilename = ANIMAL_IMAGES[i % ANIMAL_IMAGES.length];
            final Image image = new Image(this.getClass().getResourceAsStream(animalImageFilename));
            final Label imageLabel = new Label("", new ImageView(image));
            // Calculate position to place to image
            // xFactor represents at what percent of the screen's width the image should be placed
            final double xFactor = (double) ((i % ANIMALS_PER_ROW) + 1) / (ANIMALS_PER_ROW + 1);
            final double xPos = xFactor * WIDTH - (image.getWidth() / 2);
            // (i / ANIMALS_PER_ROW) finds the proper row to place the image in (due to integer division)
            final double yPos = (double) (i / ANIMALS_PER_ROW) * ANIMAL_IMAGE_Y_SEPARATION + (image.getHeight() / 2);
            // Set x and y locations of the animal
            imageLabel.setLayoutX(xPos);
            imageLabel.setLayoutY(yPos);
            getChildren().add(imageLabel); // add image to pane
            animalImages.add(imageLabel); // add image to list of animals
        }
    }

    /**
     * Restarts/starts the game
     * @param state the state of last game, if applicable (NEW if a new game)
     */
    private void restartGame(GameState state) {
        getChildren().clear();  // remove all components from the game
        bottomScreenHitCounter = 0; // reset hits on bottom
        ball = new Ball();
        getChildren().add(ball.getCircle());  // Add the ball to the game board
        paddle = new Paddle();
        getChildren().add(paddle.getRectangle());  // Add the paddle to the game board
        // Add start message
        final String message;
        switch (state) {
            case LOST:
                message = "Game Over with " + animalImages.size() + " animal(s) left\n";
                break;
            case WON:
                message = "You won!\n";
                break;
            default:
                message = "";
        }
        final Label startLabel = new Label(message + "Click mouse to start");
        startLabel.setLayoutX((double) WIDTH / 2 - 50);
        startLabel.setLayoutY((double) HEIGHT / 2 + 100);
        getChildren().add(startLabel);
        resetAnimalImages(); // resets all the animal images
        // Add event handler to start the game
        setOnMouseClicked(e -> {
            GameImpl.this.setOnMouseClicked(null);
            // As soon as the mouse is clicked, remove the startLabel from the game board
            getChildren().remove(startLabel);
            run();
        });
        // Add another event handler to steer paddle
        setOnMouseMoved(mouseEvent -> paddle.moveTo(mouseEvent.getX(), mouseEvent.getY()));
    }

    /**
     * Begins the game-play by creating and starting an AnimationTimer.
     */
    @SuppressWarnings("WeakerAccess")
    public void run() {
        // Instantiate and start an AnimationTimer to update the component of the game.
        new AnimationTimer() {
            private long lastNanoTime = -1;
            public void handle(long currentNanoTime) {
                if (lastNanoTime >= 0) {  // Necessary for first clock-tick.
                    GameState state;
                    if ((state = runOneTimestep(currentNanoTime - lastNanoTime)) != GameState.ACTIVE) {
                        // Once the game is no longer ACTIVE, stop the AnimationTimer.
                        stop();
                        // Restart the game, with a message that depends on whether
                        // the user won or lost the game.
                        restartGame(state);
                    }
                }
                // Keep track of how much time actually transpired since the last clock-tick.
                lastNanoTime = currentNanoTime;
            }
        }.start();
    }

    /**
     * Updates the state of the game at each timestep. In particular, this method should
     * move the ball, check if the ball collided with any of the animals, walls, or the paddle, etc.
     * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
     * @return the current game state
     */
    @SuppressWarnings("WeakerAccess")
    public GameState runOneTimestep(long deltaNanoTime) {
        ball.updatePosition(deltaNanoTime);
        final Bounds ballBounds = ball.getCircle().getBoundsInParent(),
                paddleBounds = paddle.getRectangle().getBoundsInParent();
        // Check for intersections with the paddle
        if (paddleBounds.intersects(ballBounds)) ball.reverseYVelocity();
        // Check for intersections with sides of the window
        if (ballBounds.getMaxX() > WIDTH) ball.makeXVelocityNegative();
        if (ballBounds.getMinX() < 0) ball.makeXVelocityPositive();
        if (ballBounds.getMinY() < 0) ball.makeYVelocityPositive();
        else if (ballBounds.getMaxY() > HEIGHT) {
            ball.makeYVelocityNegative();
            if (++bottomScreenHitCounter >= BOTTOM_HITS_TO_LOSE) return GameState.LOST;
        }
        // Check for intersections with animals
        for (Iterator<Label> i = animalImages.iterator(); i.hasNext(); ) {
            final Label image = i.next();
            final Bounds imageBounds = image.getBoundsInParent();
            if (ballBounds.intersects(imageBounds)) {
                ball.makeFaster();
                getChildren().remove(image); // remove from screen
                i.remove(); // remove from linked list
            }
        }
        if (animalImages.isEmpty()) return GameState.WON; // all animals have been "transported elsewhere"
        return GameState.ACTIVE;
    }
}
