package ui;

import api.robots.Robots;
import api.robots.Ball;
import javax.swing.*;
import java.awt.*;

public class Field extends JPanel {
	//actual measurement of miroSot Middle league playground (in cm);
	final public static int OUTER_BOUNDARY_WIDTH = 220;
	final public static int OUTER_BOUNDARY_HEIGHT = 180;
	final public static int FREE_BALL_FROM_THE_CLOSEST_SIDE = 30;
	final public static int FREE_BALL_FROM_GOAL_LINE = 55;
	final public static int PENALTY_AREA_WIDTH = 35;
	final public static int PENALTY_AREA_HEIGHT = 80;
	final public static int GOAL_AREA_WIDTH = 15;
	final public static  int GOAL_AREA_HEIGHT = 50;
	final public static int CENTER_CIRCLE_DIAMETER = 50;
	final public static int FREE_BALL_DOTS_SPACE = 25;
	final public static int INNER_GOAL_AREA_WIDTH = 15;
	final public static int INNER_GOAL_AREA_HEIGHT = 40;
	final public static int SCALE_FACTOR = 2;
	final public static int CORNER_LENGTH = 7;
	final public static int ORIGIN_X = 5+INNER_GOAL_AREA_WIDTH*SCALE_FACTOR;
	final public static int ORIGIN_Y = 5;
	private Ball ball;
	private Robots bots;
	private Point startPoint;
	private Point endPoint;
	private boolean isMouseDrag;
    private boolean isManualMovement;
	private double predX = 0;
	private double predY = 0;
    private RobotSoccerMain main;
	public boolean drawAction;

    public Field(Robots bots, Ball ball, RobotSoccerMain main) {
		this.bots = bots;
		this.ball = ball;
        this.main = main;
		isMouseDrag = false;
		setLayout(null);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D)g.create();

		g2.setColor(Color.black);

		// Draw outer boundary
		//g.drawRect(5,5,390,190);

		g2.drawRect(ORIGIN_X,ORIGIN_Y,SCALE_FACTOR*OUTER_BOUNDARY_WIDTH, SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT);

		// Draw center line and center circle
		//g.drawLine(200,5,200,195);
		g2.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				ORIGIN_Y,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH/2+ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);

		//g.drawOval(200-50/2,100-50/2,50,50);
		g2.drawOval((OUTER_BOUNDARY_WIDTH/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT/2-CENTER_CIRCLE_DIAMETER/2)*SCALE_FACTOR+ORIGIN_Y,
				CENTER_CIRCLE_DIAMETER*SCALE_FACTOR,
				CENTER_CIRCLE_DIAMETER*SCALE_FACTOR);

		// Draw penalty areas
		g2.drawRect(ORIGIN_X,
				((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*PENALTY_AREA_WIDTH,
				SCALE_FACTOR*PENALTY_AREA_HEIGHT);

		g2.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*PENALTY_AREA_WIDTH,
				((OUTER_BOUNDARY_HEIGHT-PENALTY_AREA_HEIGHT)/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*PENALTY_AREA_WIDTH,
				SCALE_FACTOR*PENALTY_AREA_HEIGHT);


		// Draw corners
		// lower left
		g2.drawLine(ORIGIN_X,
				ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_Y);
		// lower right
		g2.drawLine(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH-CORNER_LENGTH)+ORIGIN_X,
				ORIGIN_Y,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				ORIGIN_Y+SCALE_FACTOR*CORNER_LENGTH);
		// upper right
		g2.drawLine(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);
		// upper left
		//g.drawLine(385,5,395,15);
		g2.drawLine(ORIGIN_X,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y-SCALE_FACTOR*CORNER_LENGTH,
				ORIGIN_X+SCALE_FACTOR*CORNER_LENGTH,
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+ORIGIN_Y);

		// Draw goals
		g2.drawRect(ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT/2-GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*GOAL_AREA_WIDTH,
				SCALE_FACTOR*GOAL_AREA_HEIGHT);
		//g.drawRect(395-15, 75, 15, 50);
		g2.drawRect(SCALE_FACTOR*OUTER_BOUNDARY_WIDTH+ORIGIN_X-SCALE_FACTOR*GOAL_AREA_WIDTH,
				(OUTER_BOUNDARY_HEIGHT/2-GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*GOAL_AREA_WIDTH,
				SCALE_FACTOR*GOAL_AREA_HEIGHT);

		//freeball dots
		//-------------------------------------------
		//|						|					|
		//|			1st		    |			2nd		|
		//|						|					|
		//|						|					|
		//-------------------------------------------
		//|						|					|
		//|		3rd				|		4th			|
		//|						|					|
		//|						|					|
		//-------------------------------------------

		//1st quarter
		//center
		g2.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//2nd quarter
		//center dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				FREE_BALL_FROM_THE_CLOSEST_SIDE*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//3rd quarter
		g2.fillRect(FREE_BALL_FROM_GOAL_LINE*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//4th quarter
		//center dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//right dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE+FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);
		//left dot
		g2.fillRect((OUTER_BOUNDARY_WIDTH-FREE_BALL_FROM_GOAL_LINE-FREE_BALL_DOTS_SPACE)*SCALE_FACTOR+ORIGIN_X,
				(OUTER_BOUNDARY_HEIGHT-FREE_BALL_FROM_THE_CLOSEST_SIDE)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR,
				SCALE_FACTOR);

		//inner goal area
		g2.drawRect(ORIGIN_X-(INNER_GOAL_AREA_WIDTH*SCALE_FACTOR),
				(OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);

		g2.drawRect(ORIGIN_X+(OUTER_BOUNDARY_WIDTH*SCALE_FACTOR),
				(OUTER_BOUNDARY_HEIGHT/2-INNER_GOAL_AREA_HEIGHT/2)*SCALE_FACTOR+ORIGIN_Y,
				SCALE_FACTOR*INNER_GOAL_AREA_WIDTH,
				SCALE_FACTOR*INNER_GOAL_AREA_HEIGHT);

		//draw ball
		ball.draw(g2);

		//draw robots
		bots.draw(g2);

		//predict ball
		g2.setColor(Color.red);
		g2.fillOval(
				(int) predX * Field.SCALE_FACTOR + Field.ORIGIN_X - (4 * Field.SCALE_FACTOR / 2),
				(int) predY * Field.SCALE_FACTOR + Field.ORIGIN_Y - (4 * Field.SCALE_FACTOR / 2),
				4 * Field.SCALE_FACTOR,
				4 * Field.SCALE_FACTOR
		);

		if (isMouseDrag) {
			// drawRect does not take negative values hence values need to be calculated so it doesn't fill the rectangle.
			// Rectangle co-ordinates.
			int x, y, w, h;
			x = Math.min(startPoint.x, endPoint.x);
			y = Math.min(startPoint.y, endPoint.y);
			w = Math.abs(endPoint.x - startPoint.x);
			h = Math.abs(endPoint.y - startPoint.y);
			g2.setColor(Color.BLUE);
			g2.drawRect(x, y, w, h);
		}
		g2.dispose();
	}

	@Override
	public Dimension getPreferredSize() {
		return new Dimension(SCALE_FACTOR*(OUTER_BOUNDARY_WIDTH+INNER_GOAL_AREA_WIDTH*2)+10, 
				SCALE_FACTOR*OUTER_BOUNDARY_HEIGHT+10); // appropriate constants
	}

	public double getBallX() {
		return ball.getXPosition();
	}

	public double getBallY() {
		return ball.getYPosition();
	}

	/**
	 * <p>Retrieves x value from field and returns x value to show on GUI field</p>
	 * @param x
	 * @return
	 */
	public static int fieldXValueToGUIValue(int x) {
		return x * SCALE_FACTOR + ORIGIN_X;
	}

	/**
	 * <p>Retrieves y value from field and returns y value to show on GUI field</p>
	 * @param y
	 * @return
	 */
	public static int fieldYValueToGUIValue(int y) {
		return y * SCALE_FACTOR + ORIGIN_X;
	}

	/**
	 * <p>Retrieves x value from gui and returns x value for field</p>
	 * @param x
	 * @return
	 */
	public static int GUIXValueToFieldValue(int x) {
		return (x - ORIGIN_X) / SCALE_FACTOR;
	}

	/**
	 * <p>Retrieves y value from gui and returns y value for field</p>
	 * @param y
	 * @return
	 */
	public static int GUIYValueToFieldValue(int y) {
		return (y - ORIGIN_Y) / SCALE_FACTOR;
	}

}