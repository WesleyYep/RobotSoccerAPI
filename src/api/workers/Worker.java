package api.workers;

import api.data.Point;
import api.robots.Robots;

/**
 *  This represents a worker that sets the robot positions on the board
 *  Workers are passed in a Robots object
 *  They are expected to set the position of each robot in each iteration
 */
public abstract class Worker {

    protected final Robots robots;
    protected Point ball;
    protected final Robots opponents;

    /**
     * @param robots The robots that the worker should set the position for
     */
    public Worker(Robots robots, Robots opponents) {
        this.robots = robots;
        this.opponents = opponents;
    }

    public Point getBallPosition(){
        return ball;
    }

    /**
     * Starts the processing of working out robot positions, updated every iteration
     */
    public abstract void beginProcessing();

}
