package api.robots;

import ui.Field;
import api.data.Point;
import java.awt.*;

public class Ball {
	//actual ball diameter is 42.7mm;
	final public static int BALL_DIAMETER = 4;
	private Point position;

	public Ball(Point position) {
		this.position = position;
	}

	public double getXPosition() {
		return position.x;
	}

	public double getYPosition() {
		return position.y;
	}

	public void setPosition(Point position) {
		this.position = position;
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.RED);
		g.fillOval(
				(int)position.x* Field.SCALE_FACTOR+ Field.ORIGIN_X-(BALL_DIAMETER* Field.SCALE_FACTOR/2),
				(int)position.y* Field.SCALE_FACTOR+ Field.ORIGIN_Y-(BALL_DIAMETER* Field.SCALE_FACTOR/2),
				BALL_DIAMETER* Field.SCALE_FACTOR,
				BALL_DIAMETER* Field.SCALE_FACTOR
				);  
	}

	public Dimension getPreferredSize() {
		return new Dimension(BALL_DIAMETER* Field.SCALE_FACTOR, BALL_DIAMETER* Field.SCALE_FACTOR); // appropriate constants
	}
}
