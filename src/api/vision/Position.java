package api.vision;

import api.data.Point;

import java.awt.geom.Point2D;

public class Position {

	public boolean valid;
	public int id;
	public Point pixelPos;
	public Point revisionPos;
	public Point realPos;
	public double direction;
	public double pixelDirection;
	
	public Position() {
		pixelPos = new Point(0,0);
		revisionPos = new Point(0,0);
		realPos = new Point(0,0);
	}
}
