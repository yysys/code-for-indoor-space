package dk.aau.cs.idq.indoorentities;

/**
 * Created by Steven on 2016/7/13.
 */
public class ArcPoint extends Point {

    private double weight;

    public ArcPoint() {
    }

    public ArcPoint(double x, double y) {
        super(x, y);
    }

    public ArcPoint(double x, double y, int mFloor) {
        super(x, y, mFloor);
    }

    public ArcPoint(double x, double y, int mFloor, double weight) {
        super(x, y, mFloor);
        this.weight = weight;
    }

    public ArcPoint(Point point, double weight) {
        super(point);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

}
