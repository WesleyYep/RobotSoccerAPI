package ui;

import api.data.BoardProperties;
import api.data.Point;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

public class BoardAreaGlassPanel extends JPanel implements MouseListener, MouseMotionListener {
	final public static int NONE = 0;
	final public static int TOP_LEFT = 1;
	final public static int TOP_RIGHT = 2;
	final public static int BOT_LEFT = 3;
	final public static int BOT_RIGHT = 4;
    final public static int LEFT_GOAL_TOP_LEFT = 5;
    final public static int LEFT_GOAL_TOP_RIGHT = 6;
    final public static int LEFT_GOAL_BOTTOM_LEFT = 7;
    final public static int LEFT_GOAL_BOTTOM_RIGHT = 8;
    final public static int RIGHT_GOAL_TOP_LEFT = 9;
    final public static int RIGHT_GOAL_TOP_RIGHT = 10;
    final public static int RIGHT_GOAL_BOTTOM_LEFT = 11;
    final public static int RIGHT_GOAL_BOTTOM_RIGHT = 12;
	private int pointMoving = NONE;
	private int errorMargin = 10;
	private BoardProperties boardProperties;

	public BoardAreaGlassPanel() {
		addMouseListener(this);
		addMouseMotionListener(this);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponents(g);
		
		g.setColor(Color.red);

		g.drawLine((int)Math.round(boardProperties.topLeft.x)
				, (int)Math.round(boardProperties.topLeft.y)
				, (int)Math.round(boardProperties.topRight.x)
				, (int)Math.round(boardProperties.topRight.y));
		g.drawLine((int)Math.round(boardProperties.topLeft.x)
				,(int)Math.round(boardProperties.topLeft.y)
                ,(int)Math.round(boardProperties.leftGoalTopRight.x)
                ,(int)Math.round(boardProperties.leftGoalTopRight.y));
        g.drawLine((int)Math.round(boardProperties.leftGoalTopRight.x)
                ,(int)Math.round(boardProperties.leftGoalTopRight.y)
				,(int)Math.round(boardProperties.leftGoalTopLeft.x)
				,(int)Math.round(boardProperties.leftGoalTopLeft.y));
		g.drawLine((int)Math.round(boardProperties.leftGoalTopLeft.x)
				,(int)Math.round(boardProperties.leftGoalTopLeft.y)
				,(int)Math.round(boardProperties.leftGoalBottomLeft.x)
				,(int)Math.round(boardProperties.leftGoalBottomLeft.y));
		g.drawLine((int)Math.round(boardProperties.leftGoalBottomLeft.x)
				,(int)Math.round(boardProperties.leftGoalBottomLeft.y)
				,(int)Math.round(boardProperties.leftGoalBottomRight.x)
				,(int)Math.round(boardProperties.leftGoalBottomRight.y));
        g.drawLine((int)Math.round(boardProperties.leftGoalBottomRight.x)
                ,(int)Math.round(boardProperties.leftGoalBottomRight.y)
                ,(int)Math.round(boardProperties.bottomLeft.x)
                ,(int)Math.round(boardProperties.bottomLeft.y));
        g.drawLine((int)Math.round(boardProperties.topRight.x)
                ,(int)Math.round(boardProperties.topRight.y)
                ,(int)Math.round(boardProperties.rightGoalTopLeft.x)
                ,(int)Math.round(boardProperties.rightGoalTopLeft.y));
        g.drawLine((int)Math.round(boardProperties.rightGoalTopLeft.x)
                ,(int)Math.round(boardProperties.rightGoalTopLeft.y)
                ,(int)Math.round(boardProperties.rightGoalTopRight.x)
                ,(int)Math.round(boardProperties.rightGoalTopRight.y));
        g.drawLine((int)Math.round(boardProperties.rightGoalTopRight.x)
                ,(int)Math.round(boardProperties.rightGoalTopRight.y)
                ,(int)Math.round(boardProperties.rightGoalBottomRight.x)
                ,(int)Math.round(boardProperties.rightGoalBottomRight.y));
        g.drawLine((int)Math.round(boardProperties.rightGoalBottomRight.x)
                ,(int)Math.round(boardProperties.rightGoalBottomRight.y)
                ,(int)Math.round(boardProperties.rightGoalBottomLeft.x)
                ,(int)Math.round(boardProperties.rightGoalBottomLeft.y));
        g.drawLine((int)Math.round(boardProperties.rightGoalBottomLeft.x)
                ,(int)Math.round(boardProperties.rightGoalBottomLeft.y)
                ,(int)Math.round(boardProperties.bottomRight.x)
                ,(int)Math.round(boardProperties.bottomRight.y));
        g.drawLine((int)Math.round(boardProperties.bottomLeft.x)
                ,(int)Math.round(boardProperties.bottomLeft.y)
                ,(int)Math.round(boardProperties.bottomRight.x)
                ,(int)Math.round(boardProperties.bottomRight.y));
		
		g.drawString("Top Left", (int)Math.round(boardProperties.topLeft.x)-1, (int)Math.round(boardProperties.topLeft.y)-1);
		g.drawString("Top Right", (int)Math.round(boardProperties.topRight.x)-1, (int)Math.round(boardProperties.topRight.y)-1);
		g.drawString("BottomRight", (int)Math.round(boardProperties.bottomRight.x)-1,(int)Math.round(boardProperties.bottomRight.y)-1);
		g.drawString("BottomLeft", (int)Math.round(boardProperties.bottomLeft.x)-1,(int)Math.round(boardProperties.bottomLeft.y)-1);
		
	}


	@Override
	public void mouseClicked(MouseEvent e) {}


	@Override
	public void mouseEntered(MouseEvent e) {}


	@Override
	public void mouseExited(MouseEvent e) {}


	@Override
	public void mousePressed(MouseEvent e) {
		if (isTopLeft(e)) {
			pointMoving = TOP_LEFT;
		} else if (isTopRight(e)) {
			pointMoving = TOP_RIGHT;
		} else if (isBotRight(e)) {
			pointMoving = BOT_RIGHT;
			
		} else if (isBotLeft(e)) {
			pointMoving = BOT_LEFT;
		} else if (isLeftGoalTopLeft(e)) {
            pointMoving = LEFT_GOAL_TOP_LEFT;
        }else if (isLeftGoalTopRight(e)) {
            pointMoving = LEFT_GOAL_TOP_RIGHT;
        }else if (isLeftGoalBottomLeft(e)) {
            pointMoving = LEFT_GOAL_BOTTOM_LEFT;
        }else if (isLeftGoalBottomRight(e)) {
            pointMoving = LEFT_GOAL_BOTTOM_RIGHT;
        }else if (isRightGoalTopLeft(e)) {
            pointMoving = RIGHT_GOAL_TOP_LEFT;
        }else if (isRightGoalTopRight(e)) {
            pointMoving = RIGHT_GOAL_TOP_RIGHT;
        }else if (isRightGoalBottomLeft(e)) {
            pointMoving = RIGHT_GOAL_BOTTOM_LEFT;
        }else if (isRightGoalBottomRight(e)) {
            pointMoving = RIGHT_GOAL_BOTTOM_RIGHT;
        }else {
			pointMoving = NONE;
			if (e.getButton() == MouseEvent.BUTTON1) {
				boardProperties.rotatePointAntiClockwise();
			} else if (e.getButton() == MouseEvent.BUTTON3) {
				boardProperties.rotatePointClockwise();
			}
			this.repaint();
		}
		boardProperties.createTransformMatrix();
	}


	@Override
	public void mouseReleased(MouseEvent e) {
		pointMoving = NONE;
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		if (pointMoving == TOP_LEFT) {
			boardProperties.topLeft = new Point(e.getPoint().x, e.getPoint().y);
		} else if (pointMoving == TOP_RIGHT) {
			boardProperties.topRight = new Point(e.getPoint().x, e.getPoint().y);
		} else if (pointMoving == BOT_LEFT) {
			boardProperties.bottomLeft = new Point(e.getPoint().x, e.getPoint().y);
		} else if (pointMoving == BOT_RIGHT) {
			boardProperties.bottomRight = new Point(e.getPoint().x, e.getPoint().y);
			boardProperties.bottomRight = new Point(e.getPoint().x, e.getPoint().y);
		} else if (pointMoving == LEFT_GOAL_TOP_LEFT) {
            boardProperties.leftGoalTopLeft = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == LEFT_GOAL_TOP_RIGHT) {
            boardProperties.leftGoalTopRight = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == LEFT_GOAL_BOTTOM_LEFT) {
            boardProperties.leftGoalBottomLeft = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == LEFT_GOAL_BOTTOM_RIGHT) {
            boardProperties.leftGoalBottomRight = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == RIGHT_GOAL_TOP_LEFT) {
            boardProperties.rightGoalTopLeft = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == RIGHT_GOAL_TOP_RIGHT) {
            boardProperties.rightGoalTopRight = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == RIGHT_GOAL_BOTTOM_LEFT) {
            boardProperties.rightGoalBottomLeft = new Point(e.getPoint().x, e.getPoint().y);
        } else if (pointMoving == RIGHT_GOAL_BOTTOM_RIGHT) {
            boardProperties.rightGoalBottomRight = new Point(e.getPoint().x, e.getPoint().y);
        }
		this.repaint();
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		if (isTopLeft(e) || isTopRight(e) || isBotLeft(e) || isBotRight(e) || isLeftGoalTopLeft(e) || isLeftGoalTopRight(e) || isLeftGoalBottomLeft(e) || isLeftGoalBottomRight(e)
                || isRightGoalTopLeft(e) || isRightGoalTopRight(e) || isRightGoalBottomLeft(e) || isRightGoalBottomRight(e)) {
			setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
		} else {
			setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
		}
	}
	
	private boolean isTopLeft(MouseEvent e) {
		return (e.getX() < (boardProperties.topLeft.x+errorMargin) && e.getX() > (boardProperties.topLeft.x-errorMargin) && e.getY() < (boardProperties.topLeft.y+errorMargin) && e.getY() > (boardProperties.topLeft.y-errorMargin));
	}
	
	private boolean isTopRight(MouseEvent e) {
		return (e.getX() < (boardProperties.topRight.x+errorMargin) && e.getX() > (boardProperties.topRight.x-errorMargin) && e.getY() < (boardProperties.topRight.y+errorMargin) && e.getY() > (boardProperties.topRight.y-errorMargin));
	}
	
	private boolean isBotLeft(MouseEvent e) {
		return (e.getX() < (boardProperties.bottomLeft.x+errorMargin) && e.getX() > (boardProperties.bottomLeft.x-errorMargin) && e.getY() < (boardProperties.bottomLeft.y+errorMargin) && e.getY() > (boardProperties.bottomLeft.y-errorMargin));
	}
	
	private boolean isBotRight(MouseEvent e) {
		return (e.getX() < (boardProperties.bottomRight.x+errorMargin) && e.getX() > (boardProperties.bottomRight.x-errorMargin) && e.getY() < (boardProperties.bottomRight.y+errorMargin) && e.getY() > (boardProperties.bottomRight.y-errorMargin));
	}

    private boolean isLeftGoalTopLeft(MouseEvent e) {
        return (e.getX() < (boardProperties.leftGoalTopLeft.x+errorMargin) && e.getX() > (boardProperties.leftGoalTopLeft.x-errorMargin) && e.getY() < (boardProperties.leftGoalTopLeft.y+errorMargin) && e.getY() > (boardProperties.leftGoalTopLeft.y-errorMargin));
    }

    private boolean isLeftGoalTopRight(MouseEvent e) {
        return (e.getX() < (boardProperties.leftGoalTopRight.x+errorMargin) && e.getX() > (boardProperties.leftGoalTopRight.x-errorMargin) && e.getY() < (boardProperties.leftGoalTopRight.y+errorMargin) && e.getY() > (boardProperties.leftGoalTopRight.y-errorMargin));
    }

    private boolean isLeftGoalBottomLeft(MouseEvent e) {
        return (e.getX() < (boardProperties.leftGoalBottomLeft.x+errorMargin) && e.getX() > (boardProperties.leftGoalBottomLeft.x-errorMargin) && e.getY() < (boardProperties.leftGoalBottomLeft.y+errorMargin) && e.getY() > (boardProperties.leftGoalBottomLeft.y-errorMargin));
    }

    private boolean isLeftGoalBottomRight(MouseEvent e) {
        return (e.getX() < (boardProperties.leftGoalBottomRight.x+errorMargin) && e.getX() > (boardProperties.leftGoalBottomRight.x-errorMargin) && e.getY() < (boardProperties.leftGoalBottomRight.y+errorMargin) && e.getY() > (boardProperties.leftGoalBottomRight.y-errorMargin));
    }

    private boolean isRightGoalTopLeft(MouseEvent e) {
        return (e.getX() < (boardProperties.rightGoalTopLeft.x+errorMargin) && e.getX() > (boardProperties.rightGoalTopLeft.x-errorMargin) && e.getY() < (boardProperties.rightGoalTopLeft.y+errorMargin) && e.getY() > (boardProperties.rightGoalTopLeft.y-errorMargin));
    }

    private boolean isRightGoalTopRight(MouseEvent e) {
        return (e.getX() < (boardProperties.rightGoalTopRight.x+errorMargin) && e.getX() > (boardProperties.rightGoalTopRight.x-errorMargin) && e.getY() < (boardProperties.rightGoalTopRight.y+errorMargin) && e.getY() > (boardProperties.rightGoalTopRight.y-errorMargin));
    }

    private boolean isRightGoalBottomLeft(MouseEvent e) {
        return (e.getX() < (boardProperties.rightGoalBottomLeft.x+errorMargin) && e.getX() > (boardProperties.rightGoalBottomLeft.x-errorMargin) && e.getY() < (boardProperties.rightGoalBottomLeft.y+errorMargin) && e.getY() > (boardProperties.rightGoalBottomLeft.y-errorMargin));
    }

    private boolean isRightGoalBottomRight(MouseEvent e) {
        return (e.getX() < (boardProperties.rightGoalBottomRight.x + errorMargin) && e.getX() > (boardProperties.rightGoalBottomRight.x - errorMargin) && e.getY() < (boardProperties.rightGoalBottomRight.y + errorMargin) && e.getY() > (boardProperties.rightGoalBottomRight.y-errorMargin));
    }


}
