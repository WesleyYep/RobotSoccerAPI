package api.data;

import javax.media.jai.PerspectiveTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;

public class BoardProperties {
    //actual measurement of miroSot Middle league playground (in cm);
    public static final int OUTER_BOUNDARY_WIDTH = 220;
    public static final int OUTER_BOUNDARY_HEIGHT = 180;

	public static double mapLeft = 121;
	public static double mapRight = 517;
	public static double mapTop = 48;
	public static double mapBot = 372;
	private static double rotationAngle = 0;
	private static PerspectiveTransform t;
	private static PerspectiveTransform tInverse;

    public Point topRight;
    public Point topLeft;
    public Point bottomRight;
    public Point bottomLeft;
    public Point leftGoalTopLeft;
    public Point rightGoalTopLeft;
    public Point rightGoalTopRight;
    public Point leftGoalTopRight;
    public Point leftGoalBottomRight;
    public Point leftGoalBottomLeft;
    public Point rightGoalBottomRight;
    public Point rightGoalBottomLeft;

    private int fieldFacing = 0; // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
                                //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn

    public BoardProperties() {
		topRight = new Point(450,50);
		topLeft = new Point(50,50);
		bottomLeft = new Point(50,450);
		bottomRight = new Point(450,450);

        leftGoalTopLeft = new Point(30,230);
        leftGoalTopRight = new Point(50,230);
        leftGoalBottomRight = new Point(50,270);
        leftGoalBottomLeft = new Point(30,270);

        rightGoalTopLeft = new Point(450,230);
        rightGoalTopRight = new Point(470,230);
        rightGoalBottomRight = new Point(470,270);
        rightGoalBottomLeft = new Point(450,270);
		this.createTransformMatrix();
	}
	
	public void createTransformMatrix() {
		//x y: point that u want to map to
		//xp yp: orginal points
		tInverse = PerspectiveTransform.getQuadToQuad(
				mapLeft,
				mapTop,
				mapLeft,
				mapBot,
				mapRight,
				mapBot,
				mapRight,
				mapTop,
				topLeft.x,
				topLeft.y,
				bottomLeft.x,
				bottomLeft.y,
				bottomRight.x,
				bottomRight.y,
				topRight.x,
				topRight.y
				);

		try {
			t = tInverse.createInverse();
		} catch (NoninvertibleTransformException | CloneNotSupportedException e) {
			e.printStackTrace();
		}
    }
	
	public static Point imagePosToActualPos (Point p) {
        double x = p.x;
        double y = p.y;

		if (t != null ) {
			Point2D selectedPoint = new Point2D.Double();
			t.transform(new Point2D.Double(x,y), selectedPoint);
			double actualX = (selectedPoint.getX() - mapLeft) / ((mapRight-mapLeft)/(double)OUTER_BOUNDARY_WIDTH);
			double actualY = (selectedPoint.getY() - mapTop) / ((mapBot-mapTop)/(double)OUTER_BOUNDARY_HEIGHT);
			
			return new Point(actualX,actualY);
		} else {
			return null;
		}
		
	}


    public static Point FlatToScreen(Point p) {
        double x = p.x;
        double y = p.y;

        if (t!=null) {
            Point2D selectedPoint = new Point2D.Double();
            t.transform(new Point2D.Double(x,y), selectedPoint);
            return new Point(selectedPoint.getX(), selectedPoint.getY());
        } else {
            return null;
        }
    }

    public static Point ScreenToGround(Point p) {
        double x = p.x;
        double y = p.y;

        x -= 121;
        y -= 48;

        x /= 180.00;
        y /= 180.00;

        return new Point(x,y);
    }


    public static Point actualPosToimagePos(Point p) {
        try {
            double x = p.x;
            double y = p.y;


            if (tInverse != null) {
                x = (x * ((mapRight - mapLeft) / (double) OUTER_BOUNDARY_WIDTH)) + mapLeft;
                y = (y * ((mapBot - mapTop) / (double) OUTER_BOUNDARY_HEIGHT)) + mapTop;

                Point2D selectedPoint = new Point2D.Double();
                tInverse.transform(new Point2D.Double(x, y), selectedPoint);

                return new Point(selectedPoint.getX(), selectedPoint.getY());

            } else {
                return null;
            }
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public static double imageThetaToActualTheta (double theta) {
        double result = theta + rotationAngle;

        if (result > Math.PI) result -= 2*Math.PI;
        if (result < -Math.PI) result += 2*Math.PI;

        return result;
    }

	public void rotatePointAntiClockwise() {
		Point tempBottomLeft = bottomLeft;
		Point tempTopLeft = topLeft;
		
		Point tempTopRight = topRight;
		Point tempBottomRight = bottomRight;
        rotationAngle += Math.PI/2;
        if (rotationAngle > Math.PI) rotationAngle -= 2*Math.PI;
		
		bottomLeft = tempBottomRight;
		topLeft = tempBottomLeft;
		topRight = tempTopLeft;
		bottomRight = tempTopRight;
        //swap if needed
        if (fieldFacing == 1 || fieldFacing == 4) {
            swapGoals();
        }
        //for fieldFacing:
        // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
        //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn
        if (fieldFacing == 0 || fieldFacing == 3) {
            fieldFacing++;
        } else if (fieldFacing == 1 || fieldFacing == 5) {
            fieldFacing = 3;
        } else if (fieldFacing == 2 || fieldFacing == 4) {
            fieldFacing = 0;
        }
        //swap if needed
	}
	
	public void rotatePointClockwise() {
        rotationAngle -= Math.PI/2;
        if (rotationAngle < -Math.PI) rotationAngle += 2*Math.PI;
		Point tempBottomLeft = bottomLeft;
		Point tempTopLeft = topLeft;
		
		Point tempTopRight = topRight;
		Point tempBottomRight = bottomRight;
		
		bottomLeft = tempTopLeft;
		topLeft = tempTopRight;
		
		topRight = tempBottomRight;
		bottomRight = tempBottomLeft;
        //swap if needed
        if (fieldFacing == 2 || fieldFacing == 5) {
            swapGoals();
        }
        //for fieldFacing:
        // (0 is nonflipped - 0 turns, 1 is nonflipped - 1 ACW turn, 2 is nonflipped - 1 CW turn
        //   3 is flipped - 0 turns, 4 is flipped - 1 ACW turn, 5 is flipped - 1 CW turn
        if (fieldFacing == 0 || fieldFacing == 3) {
            fieldFacing += 2;
        } else if (fieldFacing == 1 || fieldFacing == 5) {
            fieldFacing = 0;
        } else if (fieldFacing == 2 || fieldFacing == 4) {
            fieldFacing = 3;
        }
	}

    private void swapGoals() {
        //temps
        Point a = rightGoalTopLeft;
        Point b = rightGoalTopRight;
        Point c = rightGoalBottomLeft;
        Point d = rightGoalBottomRight;

        rightGoalTopLeft = leftGoalBottomRight;
        rightGoalTopRight = leftGoalBottomLeft;
        rightGoalBottomLeft = leftGoalTopRight;
        rightGoalBottomRight = leftGoalTopLeft;

        leftGoalTopLeft = d;
        leftGoalTopRight = c;
        leftGoalBottomLeft = b;
        leftGoalBottomRight = a;
    }

    public static double getRotateAngle() {
        return rotationAngle;
    }

    public boolean pointInPoly(int nvert, double[] vertx, double[] verty, double testx, double testy) {
        int i,j = 0;
        boolean c = false;

        for (i = 0, j = nvert-1; i < nvert; j = i++) {
            if ( ((verty[i]>testy) != (verty[j]>testy)) &&
                    (testx < (vertx[j]-vertx[i]) * (testy-verty[i]) / (verty[j]-verty[i]) + vertx[i]) )
                c = !c;
        }
        return c;
    }

    public void updateProcessingArea() {
        int vert = 12;

        double[] x = {topLeft.x,topRight.x,rightGoalTopLeft.x,rightGoalTopRight.x,rightGoalBottomRight.x
                        ,rightGoalBottomLeft.x,bottomRight.x,bottomLeft.x,leftGoalBottomRight.x,leftGoalBottomLeft.x
                        ,leftGoalTopLeft.x,leftGoalTopRight.x};

        double[] y = {topLeft.y,topRight.y,rightGoalTopLeft.y,rightGoalTopRight.y,rightGoalBottomRight.y
                ,rightGoalBottomLeft.y,bottomRight.y,bottomLeft.y,leftGoalBottomRight.y,leftGoalBottomLeft.y
                ,leftGoalTopLeft.y,leftGoalTopRight.y};
    }

}
