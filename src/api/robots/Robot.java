package api.robots;

import api.data.Point;
import api.vision.Position;
import ui.Field;

import java.awt.*;

/**
 * Created by Wesley on 30/06/2016.
 */
public class Robot {
    private double linearVelocity;
    private double angularVelocity;
    private Point position;
    private int id;
    private double theta;
    final public static int ROBOT_WIDTH = 8;
    final public static int ROBOT_HEIGHT = 8;

    public Robot(int id) {
        this.id = id;
        position = new Point(-10, 10 + 10*id);
    }

    public void setVelocity(double linearVelocity, double angularVelocity) {
        this.angularVelocity = angularVelocity;
    }

    public double getLinearVelocity() {
        return linearVelocity;
    }

    public double getAngularVelocity() {
        return angularVelocity;
    }

    public Point getPosition() { return position; }

    public double getTheta() { return theta; }

    public void setPosition(Point p) {
        this.position = p;
    }


    /**
     * Renders the robot onto the field.
     * @param g
     */
    public void draw(Graphics2D g) {
        int xPos = (int) (position.x* Field.SCALE_FACTOR+Field.ORIGIN_X-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
        int yPos = (int) (position.y*Field.SCALE_FACTOR+Field.ORIGIN_Y-(ROBOT_WIDTH*Field.SCALE_FACTOR/2));
        int width = ROBOT_WIDTH*Field.SCALE_FACTOR;
        int height = ROBOT_HEIGHT*Field.SCALE_FACTOR;

        // Convert id of robot to string value. Add 1 because id starts from 0 but on display, it should start from 1.
        String id = String.valueOf(this.id + 1);

        // Get a copy of the current graphics object. Hence can manipulate the copy without affecting the original.
        g = (Graphics2D)g.create();

        // Set the color for the robot.
        // Assuming id is never less than 0, if id is between 0-4, black, 5-9 gray.
        if (this.id < 5) {
            g.setColor(Color.BLACK);
        } else {
            g.setColor(Color.GRAY);
        }

        // Concatenates current graphics object affinetransform with a translated rotation transform.
        g.rotate(Math.toRadians(360-theta), xPos + width/2, yPos + height/2);

        // Renders above transformation.
        g.fillRect(xPos, yPos, width, height);

        // Text rotate 90 deg. Not too sure.
        g.rotate(Math.toRadians(90), xPos + width/2, yPos + height/2);

        // Number colour is white. Red if the robot is selected.
        g.setColor(Color.WHITE);

        FontMetrics fm = g.getFontMetrics();
        int idX = (width - fm.stringWidth(id)) / 2;
        int idY = (fm.getAscent() + (height - (fm.getAscent() + fm.getDescent())) / 2);

        g.drawString(id, xPos + idX, yPos + idY);

        // Free up resources.
        g.dispose();
    }

}
