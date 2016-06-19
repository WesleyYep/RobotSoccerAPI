package api.vision;


import api.data.Point;
import java.util.ArrayList;

/**
 * Created by chan743 on 31/07/2015.
 */
public class Patch {



    public ArrayList<Point> pixels = new ArrayList<Point>();
    public Point center;
    public Point revisionCenter;
    public Point realCenter;

    public boolean found;
    public int related_id;

    public Patch () {

        center = new Point(0,0);
        revisionCenter = new Point(0,0);
        realCenter = new Point(0,0);

        found = false;
        related_id = -1;
    }

    
}
