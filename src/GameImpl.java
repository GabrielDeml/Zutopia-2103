import javafx.animation.AnimationTimer;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

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
    public static final int WIDTH = 400;
    /**
     * The height of the game board.
     */
    public static final int HEIGHT = 600;
    /**
     * Allowed number of hits on bottom of screen before losing
     */
    public static final int BOTTOM_HITS_TO_LOSE = 5;

    /**
     * Images we need to load
     */
    private static final LinkedList<String> ImagesToLoad = new LinkedList(Arrays.asList("duck", "goat", "horse"));

    /**
     * Images that are loaded and ready to display
     */
    private LinkedList<Label> loadedImages = new LinkedList<Label>();

    /**
     * Number of pixels between animal blocks on the y axis
     */
    private static final int distance_y = 80;

    // Instance variables
    private Ball ball;
    private Paddle paddle;
    private double bottomScreenHitCounter = 0;

    /**
     * Constructs a new GameImpl.
     */
    public GameImpl() {
        setStyle("-fx-background-color: white;");
//        loadImages();
        restartGame(GameState.NEW);
    }

    public String getName() {
        return "Zutopia";
    }

    public Pane getPane() {
        return this;
    }

    /**
     * Loads images from disk
     */
//    private void loadImages() {
//        for (String n : ImagesToLoad) {
//
//            loadedImages.put(n, new LinkedList(Arrays.asList(imageLabel, image.getWidth(), image.getHeight())));
//        }
//    }

    /**
     * Display animals blocks
     *
     * @param animals is the list that we want to display
     * @param x       how many animals on the x axis
     */
    private void displayGridOfAnimals(LinkedList<String> animals, int x) {
        int distance_x = WIDTH / x;
        int yi = 0, xi = 0;
        for (String animal : animals) {
            final Image image = new Image(this.getClass().getResourceAsStream(animal + ".jpg"));
            Label imageLabel = new Label("", new ImageView(image));
            imageLabel.setLayoutX(( image.getWidth() / 2) + (distance_x * xi));
            imageLabel.setLayoutY(( image.getHeight() / 2) + (distance_y * yi));
            getChildren().add(imageLabel);
            loadedImages.add(imageLabel);
            if(xi++ == x){
                xi = 0;
                yi++;
            }
        }
    }

    private LinkedList randomlyGenerateAListOfAnimals(int n) {
        LinkedList listOfImages = new LinkedList();
        Random random = new Random();
        for (int i = 0; i <= n; i++) {
            int randomInt = random.nextInt(ImagesToLoad.size());
            listOfImages.add(ImagesToLoad.get(randomInt));
        }
        return listOfImages;
    }

    private void restartGame(GameState state) {
        getChildren().clear();  // remove all components from the game
        bottomScreenHitCounter = 0; // reset hits on bottom

        displayGridOfAnimals(randomlyGenerateAListOfAnimals(6), 3);
        // Create and add ball
        ball = new Ball();
        getChildren().add(ball.getCircle());  // Add the ball to the game board

        // Create and add paddle
        paddle = new Paddle();
        getChildren().add(paddle.getRectangle());  // Add the paddle to the game board

        // Add start message
        final String message;
        if (state == GameState.LOST) {
            message = "Game Over\n";
        } else if (state == GameState.WON) {
            message = "You won!\n";
        } else {
            message = "";
        }
        final Label startLabel = new Label(message + "Click mouse to start");
        startLabel.setLayoutX(WIDTH / 2 - 50);
        startLabel.setLayoutY(HEIGHT / 2 + 100);
        getChildren().add(startLabel);

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
     *
     * @param deltaNanoTime how much time (in nanoseconds) has transpired since the last update
     * @return the current game state
     */
    public GameState runOneTimestep(long deltaNanoTime) {
        ball.updatePosition(deltaNanoTime);
        final Bounds ballBounds = ball.getCircle().getBoundsInParent(),
                paddleBounds = paddle.getRectangle().getBoundsInParent();
        // todo cleanup the following mess once the stuck issue is resolved
        if (paddleBounds.intersects(ballBounds)) {
            ball.reverseYVelocity();
        }
        if (ballBounds.getMaxX() > WIDTH || ballBounds.getMinX() < 0) {
            ball.reverseXVelocity();
        }
        if (ballBounds.getMinY() < 0) {
            ball.reverseYVelocity();
        } else if (ballBounds.getMaxY() > HEIGHT) {
            ball.reverseYVelocity();
            if (++bottomScreenHitCounter >= BOTTOM_HITS_TO_LOSE) {
                // todo game over (put anything else here?)
                return GameState.LOST;
            }
        }
        for(int i = 0; i < loadedImages.size(); i++){
            Bounds imageBounds = loadedImages.get(i).getBoundsInParent();
            if(ballBounds.intersects(imageBounds)){
                ball.reverseYVelocity();
                getChildren().remove(loadedImages.get(i));
                loadedImages.remove(i);
            }
        }
        // todo check collisions with animals
        // todo fix the "sticking" issue in Canvas discussions
        // todo   perhaps by just moving the ball here until it is unstuck?
        // while(ball intersecting edge or other object) moveBallInNewDir() // to get unstuck todo
        return GameState.ACTIVE;
    }
}
