package dk.aau.cs.idq.indoorentities;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * Created by Steven on 2016/7/6.
 */
public class Ring {

    private static double deltaRingAngle = 10;

    private static double deltaRingLen = 0.5;

    public static Hashtable<Double, Double> decayDistTable = new Hashtable<>();

    private Point center;

    private double radius;

    private List<ArcPoint> arcPoints = new ArrayList<>();

    public Ring() {
    }

    public Ring(Point center, double radius, double unDoorDist) {
        this.center = center;
        this.radius = radius;
        this.arcPoints = new ArrayList<>();
        if (radius != 0) {
            for (double varRadius = deltaRingLen; varRadius < this.radius; varRadius += deltaRingLen) {
                for (int i = 0; i < 360; i += deltaRingAngle) {
                    double x = this.center.getX() + varRadius * Math.cosh(i);
                    double y = this.center.getY() + varRadius * Math.sinh(i);
                    double weight = distanceDecay(unDoorDist + varRadius);
                    ArcPoint arcPoint = new ArcPoint(x, y, this.center.getmFloor(), weight);
                    this.arcPoints.add(arcPoint);
                }
            }

        } else {
            this.arcPoints.add(new ArcPoint(this.center, 1));
        }
    }

    public Point getCenter() {
        return center;
    }

    public void setCenter(Point center) {
        this.center = center;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }

    public List<ArcPoint> getArcPoints() {
        return arcPoints;
    }

    public void setArcPoints(List<ArcPoint> arcPoints) {
        this.arcPoints = arcPoints;
    }


    public static double distanceDecay(double distance) {
        distance = new BigDecimal(distance).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        if (decayDistTable.containsKey(distance)) {
            return decayDistTable.get(distance);
        } else {
            double constValue = 1;
            double decay = constValue * Math.exp(-distance);
            decayDistTable.put(distance, decay);
//            System.out.println(distance + " = " + decay);
            return decay;
        }
    }

}
