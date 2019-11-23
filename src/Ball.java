import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

/**
 * Class that implements a ball with a position and velocity.
 */
@SuppressWarnings("WeakerAccess")
public class Ball {
    // Constants
    /**
     * The radius of the ball.
     */
    @SuppressWarnings("WeakerAccess")
    public static final int BALL_RADIUS = 8;
    /**
     * The initial velocity of the ball in the x direction.
     */
    @SuppressWarnings("WeakerAccess")
    public static final double INITIAL_VX = 1e-7;
    /**
     * The initial velocity of the ball in the y direction.
     */
    @SuppressWarnings("WeakerAccess")
    public static final double INITIAL_VY = 1e-7;

    // Instance variables
    // (x,y) is the position of the center of the ball.
    private double x, y;
    private double vx, vy;
    private Circle circle;

    /**
     * @return the Circle object that represents the ball on the game board.
     */
    @SuppressWarnings("WeakerAccess")
    public Circle getCircle() {
        return circle;
    }

    /**
     * Constructs a new Ball object at the centroid of the game board
     * with a default velocity that points down and right.
     */
    @SuppressWarnings("WeakerAccess")
    public Ball() {
        x = GameImpl.WIDTH / 2.0;
        y = GameImpl.HEIGHT / 2.0;
        vx = INITIAL_VX;
        vy = INITIAL_VY;

        circle = new Circle(BALL_RADIUS, BALL_RADIUS, BALL_RADIUS);
        circle.setLayoutX(x - BALL_RADIUS);
        circle.setLayoutY(y - BALL_RADIUS);
        circle.setFill(Color.BLACK);
    }

    /**
     * Updates the position of the ball, given its current position and velocity,
     * based on the specified elapsed time since the last update.
     *
     * @param deltaNanoTime the number of nanoseconds that have transpired since the last update
     */
    @SuppressWarnings("WeakerAccess")
    public void updatePosition(long deltaNanoTime) {
        double dx = vx * deltaNanoTime;
        double dy = vy * deltaNanoTime;
        x += dx;
        y += dy;

        circle.setTranslateX(x - (circle.getLayoutX() + BALL_RADIUS));
        circle.setTranslateY(y - (circle.getLayoutY() + BALL_RADIUS));
    }

    /**
     * Reverses the ball's y velocity
     */
    void reverseYVelocity() {
        vy *= -1;
    }

    /**
     * Makes the x velocity negative
     */
    void makeXVelocityNegative() {
        if (vx > 0) vx *= -1;
    }

    /**
     * Makes the x velocity positive
     */
    void makeXVelocityPositive() {
        if (vx < 0) vx *= -1;
    }

    /**
     * Makes the y velocity negative
     */
    void makeYVelocityNegative() {
        if (vy > 0) vy *= -1;
    }

    /**
     * Makes the y velocity positive
     */
    void makeYVelocityPositive() {
        if (vy < 0) vy *= -1;
    }

    /**
     * Makes the velocity faster by 10%
     */
    void makeFaster() {
        vx *= 1.1;
        vy *= 1.1;
    }
}
