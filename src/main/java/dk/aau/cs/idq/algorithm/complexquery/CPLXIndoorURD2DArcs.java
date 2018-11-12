package dk.aau.cs.idq.algorithm.complexquery;

import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.DataGenConstant;

import java.util.ArrayList;
import java.util.List;

/**
 * CPLXIndoorUR
 * the uncertain region for a Sample Point during a walking period
 *
 * @author lihuan
 * @version 0.1 / 2014.10.21
 */
public class CPLXIndoorURD2DArcs {

    private SampledPoint mPoint;                                                    // the position of the Sample

    private int recordTime;                                                            // the record time of this Sample

    private int curTime;                                                            // the current time

    private int flag;                                                                // 0 means no needs to calculate MonteCarlo Points

    private Par curINPar;                                                            // current par of this Sample Point

    private List<Arc> arcs = new ArrayList<>();                                        // the sampling arcs

    private double weightSumTotal;

    private List<NextPossiblePar> uncertainPars;                                    // uncertain next possible pars


    /**
     * Constructor Function
     *
     * @param mPoint
     * @param curTime
     * @param flag
     */
    public CPLXIndoorURD2DArcs(SampledPoint mPoint, int curTime, int flag) {
        this.setmPoint(mPoint);
        this.arcs = new ArrayList<>();
        this.setRecordTime(IndoorSpace.OTT.get(mPoint.getObjID()));
        this.setCurTime(curTime);
        this.flag = flag;
        this.weightSumTotal = 0;
        if (flag == 1) {
            generateArcs();
        }
    }

    /**
     * generate the Arcs
     */
    private void generateArcs() {
        if (this.curTime > this.recordTime) {

            if (this.uncertainPars == null) {
                this.uncertainPars = this.mPoint.getUncertainParsD2D((this.curTime - this.recordTime));
            }

            for (NextPossiblePar uncertainPar : uncertainPars) {

//                Par unPar = uncertainPar.getPossibleNextPar();
//                Door unDoor = uncertainPar.getPossibleTroughDoor();
//                double onGoingDist = uncertainPar.getDistanceToContinue();
                double unDoorDist = (this.curTime - this.recordTime) * DataGenConstant.maxVelocity - uncertainPar.getDistanceToContinue();
                if (uncertainPar.getDistanceToContinue() > 0) {
                    Arc arc = new Arc(uncertainPar, unDoorDist);
                    this.arcs.add(arc);
                    this.weightSumTotal += arc.getWeightSumArc();
                }
            }

            if (this.curINPar == null) {
                this.curINPar = this.mPoint.getCurPar();
            }
            double onGoingDist = (this.curTime - this.recordTime)
                    * DataGenConstant.maxVelocity;
            Arc arc = new Arc(this.mPoint, this.curINPar, onGoingDist, 0);
            this.arcs.add(arc);
            this.weightSumTotal += arc.getWeightSumArc();

//            System.out.println("******************");
//            for(Arc arcme : this.arcs){
//                for(ArcPoint arcPoint: arcme.getArcPoints()){
//                    System.out.println(arcPoint.getWeight());
//                }
//            }
//            System.out.println("=====");
//            System.out.println(this.weightSumTotal);


        } else {
            Arc arc = new Arc(new Point(this.mPoint.getSampledX(), this.mPoint.getSampledY(), this.mPoint.getmFloor()), 0.0, 0);
        }
    }


    /**
     * get the intersection area(probability)
     *
     * @param query
     * @return probability
     */
    public double getUnionPart(Query query) {

        // System.out.println(this.curTime + " > >" + this.recordTime);
        // System.out.println(this.curTime + " > >" + this.recordTime + "||" + this.monteCarloPoints.size());

        double count = 0;

        for (Arc arc : this.arcs) {

            // System.out.println(query);
            // System.out.println(point);
            for (ArcPoint arcPoint : arc.getArcPoints())
                if (query.contain(arcPoint)) {
//                    System.out.println(arcPoint.getCurrentPar());
                    count += (arcPoint.getWeight() / this.weightSumTotal);
                }
        }

        return count;
    }


    /**
     * @return the mPoint
     */
    public SampledPoint getmPoint() {
        return mPoint;
    }

    /**
     * @param mPoint the mPoint to set
     */
    public void setmPoint(SampledPoint mPoint) {
        this.mPoint = mPoint;
    }

    /**
     * @return the recordTime
     */
    public int getRecordTime() {
        return recordTime;
    }

    /**
     * @param recordTime the recordTime to set
     */
    public void setRecordTime(int recordTime) {
        this.recordTime = recordTime;
    }

    /**
     * @return the curTime
     */
    public int getCurTime() {
        return curTime;
    }

    /**
     * @param curTime the curTime to set
     */
    public void setCurTime(int curTime) {
        this.curTime = curTime;
    }

    /**
     * @return the flag
     */
    public int getFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(int flag) {
        this.flag = flag;
        if (this.flag == 1) {
            generateArcs();
        }
    }

    /**
     * the topological relation with the Query's Uncertainty Region
     *
     * @param query
     * @return result 0.not in UR; 2. not fully in UR; 3. fully in UR
     */
    public int topoloyRelation(Query query) {
        // TODO Auto-generated method stub

        if (this.curTime > this.recordTime) {
            // System.out.println(this.mPoint);

            int nonInterset = 0;
            int fullcontained = 0;

            double maxGoing = (this.curTime - this.recordTime) * DataGenConstant.maxVelocity;
            this.curINPar = this.mPoint.getCurPar();

            Rect rect = new Rect(this.mPoint.getSampledX() - maxGoing, this.mPoint.getSampledX() + maxGoing, this.mPoint.getSampledY() - maxGoing, this.mPoint.getSampledY() + maxGoing);
            rect.setmFloor(this.mPoint.getmFloor());
            Rect intersect = this.curINPar.intersection(rect);

            if (!query.contain(intersect)) {
                if (query.intersection(intersect) != null) {
                    return 1;
                } else {
                    nonInterset++;
                }
            } else {
                fullcontained++;
            }

            this.uncertainPars = this.mPoint.getUncertainParsD2D((this.curTime - this.recordTime));

            for (NextPossiblePar uncertainPar : uncertainPars) {
                if (uncertainPar.isFullyCovered()) {
                    Par unPar = uncertainPar.getPossibleNextPar();
                    if (!query.contain(unPar)) {
                        if (query.intersection(unPar) != null) {
                            return 1;
                        } else {
                            nonInterset++;
                        }
                    } else {
                        fullcontained++;
                    }
                } else {
                    Rect mbr = uncertainPar.getMBR();
                    if (!query.contain(mbr)) {
                        if (query.intersection(mbr) != null) {
                            return 1;
                        } else {
                            nonInterset++;
                        }
                    } else {
                        fullcontained++;
                    }
                }
            }

            if (nonInterset == (uncertainPars.size() + 1)) {
                return 0;
            }
            if (fullcontained == (uncertainPars.size() + 1)) {
                return 2;
            } else {
                return 1;
            }

        } else {
            if (query.contain(new Point(this.mPoint.getSampledX(), this.mPoint.getSampledY(), this.mPoint.getmFloor()))) {
                return 2;
            } else
                return 0;
        }

    }

    public double getWeightSumTotal() {
        return weightSumTotal;
    }

    public void setWeightSumTotal(double weightSumTotal) {
        this.weightSumTotal = weightSumTotal;
    }
}
