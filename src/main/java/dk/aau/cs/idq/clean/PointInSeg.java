package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.indoorentities.Point;

import java.util.LinkedList;
import java.util.List;

public class PointInSeg {

    private List<Point> point = null;

    public List<Point> getPoint() {
        return point;
    }

    public PointInSeg() {
        point = new LinkedList<Point>();
    }

    public void addPoint(Point p) {
        point.add(p);
    }
}
