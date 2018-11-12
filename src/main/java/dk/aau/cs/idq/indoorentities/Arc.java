package dk.aau.cs.idq.indoorentities;

import java.util.HashMap;

/**
 * Created by Steven on 2016/7/6.
 */
public class Arc extends Ring {

    private double weightSumArc;

    public Arc(Point center, double radius, double unDoorDist) {
        super(center, radius, unDoorDist);
    }

    public Arc(NextPossiblePar nextPossiblePar, double unDoorDist) {
        super();
        Par carPar = nextPossiblePar.getPossibleNextPar();
        Door carDoor = nextPossiblePar.getPossibleTroughDoor();
        double onGoingDist = nextPossiblePar.getDistanceToContinue();
        Ring ring = new Ring(carDoor, onGoingDist, unDoorDist);
        weightSumArc = 0;
        for (ArcPoint arcPoint : ring.getArcPoints()) {
            if (carPar.contain(arcPoint)) {
                this.getArcPoints().add(arcPoint);
                this.weightSumArc += arcPoint.getWeight();
            }
        }

    }

    public Arc(SampledPoint mPoint, Par curINPar, double onGoingDist, double unDoorDist) {
        Ring ring = new Ring(new Point(mPoint.getSampledX(), mPoint.getSampledY(), mPoint.getmFloor()), onGoingDist, unDoorDist);
        weightSumArc = 0;
        for (ArcPoint arcPoint : ring.getArcPoints()) {
            if (curINPar.contain(arcPoint)) {
                this.getArcPoints().add(arcPoint);
                this.weightSumArc += arcPoint.getWeight();
            }
        }
    }

    public double getWeightSumArc() {
        return weightSumArc;
    }

    public void setWeightSumArc(double weightSumArc) {
        this.weightSumArc = weightSumArc;
    }
}
