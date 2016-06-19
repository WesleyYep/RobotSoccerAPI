package api.workers;

import api.robots.Robots;

/**
 *  This represents a worker that sets the robot positions on the board
 *  Workers are passed in a Robots object
 *  They are expected to set the position of each robot in each iteration
 */
public abstract class Worker {

    private final Robots robots;

    /**
     * @param robots The robots that the worker should set the position for
     */
    public Worker(Robots robots) {
        this.robots = robots;
    }

    /**
     * Starts the processing of working out robot positions, updated every iteration
     */
    public abstract void beginProcessing();

}
