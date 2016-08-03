package api.robots;

import api.data.Point;

import java.awt.*;

/**
 * Created by Wesley on 19/06/2016.
 */
public class Robots {

    private Robot[] bots;

    public Robots(int number) {
        bots = new Robot[number];
        for (int i = 0; i < number; i++) {
            bots[i] = new Robot(i);
        }
    }

    public void setPosition(int index, Point p) {
        bots[index].setPosition(p);
    }

    public void setVelocity(int index, double linearVelocity, double angularVelocity) {
        bots[index].setVelocity(linearVelocity, angularVelocity);
    }

    public void draw(Graphics2D g) {
        for (Robot r : bots) {
            r.draw(g);
        }
    }

    public Point getPosition(int index) {
        return bots[index].getPosition();
    }

    public double getTheta(int index) {
        return bots[index].getTheta();
    }

}
