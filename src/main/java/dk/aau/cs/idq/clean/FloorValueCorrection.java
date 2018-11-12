package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.rmi.server.RemoteRef;
import java.util.*;

public class FloorValueCorrection {

    public static final double durationLimit = 1600;
    public static final double distanceLimit = 3.0;
    public static final Point[] staircase = {new Point(5.0, 30.0), new Point(30.0, 5.0)
            , new Point(30.0, 55.0), new Point(55.0, 30.0)};

    public static final int tau = 10000;                                   //time diffierence
    public static final double eta = 1.0;                              //merge ratio
    public static final int ita = 2;                                    //floor diffierence
    public static final int validRecordSize = 30;

    /**
     * read one person data from disk
     * @param curPersonFile
     * @return
     */
    public List<Record> readPersonData(File curPersonFile) {
        List<Record> records = new ArrayList<Record>();

        Scanner in = null;
        try {
            in = new Scanner(curPersonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int curTime = Integer.valueOf(input[0]).intValue();
            double x = Double.valueOf(input[1]).doubleValue();
            double y = Double.valueOf(input[2]).doubleValue();
            int mFloor = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();
            int isStay = Integer.valueOf(input[5]).intValue();
            int isOutlier = Integer.valueOf(input[6]).intValue();

            records.add(new Record(curTime, x, y, mFloor, parID, isStay, isOutlier));
        }

        return records;
    }

    /**
     * judge section is enough or not
     * @param section
     * @return
     */
    public boolean isQulified(List<Record> section) {
        if (section.size() == 0) return false;
        else return true;
    }

    /**
     * merge qualified section to a segmentation
     * @param section
     * @return
     */
    public Segmentation primaryMerge(List<Record> section) {
        Record start = section.get(0);
        Record end = section.get(section.size() - 1);

        return new Segmentation(0, start.getTime(), end.getTime(), section.get(0).getFloor());
    }

    /**
     * the seq is qualified in time, we divide it to Us
     * @param seq
     * @param segmentations
     */
    public void handle(List<Record> seq, List<Segmentation> segmentations) {
        List<Record> section = new LinkedList<Record>();

        int pre_v = -1, cur_v;
        for (int i = 0; i < seq.size(); i++) {
            cur_v = seq.get(i).getFloor();

            if (pre_v == -1 || cur_v == pre_v) {
                section.add(seq.get(i));
            }
            else {
                if (isQulified(section)) {
                    Segmentation seg = primaryMerge(section);
                    segmentations.add(seg);
                }

                section.clear();
                section.add(seq.get(i));
            }

            pre_v = cur_v;
        }

        if (isQulified(section)) {
            Segmentation seg = primaryMerge(section);
            segmentations.add(seg);
        }
    }

    /**
     * divide position records to segmentations
     * @param records
     * @return
     */
    public  List<Segmentation> getSegmentation(List<Record> records) {

        List<Segmentation> segmentations = new LinkedList<Segmentation>();

        int pre_t = -1, cur_t;
        List<Record> StoreRecord = new LinkedList<Record>();
        List<Record> seq = new LinkedList<Record>();

        for (int i = 0; i < records.size(); i++) {
            cur_t = records.get(i).getTime();

            if (pre_t == -1 || cur_t - pre_t <= tau) {
                seq.add(records.get(i));
            }
            else {
                handle(seq, segmentations);
                seq.clear();
                seq.add(records.get(i));
            }

            pre_t = cur_t;
        }
        handle(seq, segmentations);

        return segmentations;
    }

    public double p2pDis(Point a, Point b) {
        double x1 = a.getX();
        double y1 = a.getY();
        double x2 = b.getX();
        double y2 = b.getY();

        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    public boolean checkTopology(int segID, Segmentation seg, PointInSeg pointInSegs[]) {

        double dis = Integer.MAX_VALUE;

        for (Point p : pointInSegs[segID].getPoint()) {
            for (int i = 0; i < staircase.length; i++) {
                dis = Math.min(dis, p2pDis(p, staircase[i]));
            }
        }

        if (dis < distanceLimit) return true;
        else return false;
    }

    public boolean checkDuration(Segmentation F) {

        if (F.getDuration() > durationLimit) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * judge the state of a segmentation is S or not
     * @param S
     * @param neighbors
     * @return
     */
    public boolean isUnstable(Segmentation S, List<Segmentation> neighbors, PointInSeg pointInSegs[], int segID) {

/*
        for (int i = 0; i < neighbors.size(); i++) {
            if (neighbors.get(i).getDuration() / S.getDuration() > eta) {
                return true;
            }
        }
*/

        if (!checkTopology(segID, S, pointInSegs) && !checkDuration(S)) return true;

        return false;
    }

    /**
     * merge the unstable segmentation
     * @param unStableSeg
     * @param seg
     */
    public void unStableSegMerge(ArrayList<Segmentation> unStableSeg, ArrayList<Segmentation> seg) {
        if (unStableSeg.size() == 0) return;

        //double mergeDuration = 0;
        int start = 0, end = 0;
        Segmentation maxTimeSeg = null;
        for (int i = 0; i < unStableSeg.size(); i++) {
            if (i == 0) {
                maxTimeSeg = unStableSeg.get(i);
                //mergeDuration = maxTimeSeg.getDuration();
                start = maxTimeSeg.getStart();
                end = maxTimeSeg.getEnd();
                continue;
            }

            if (Math.abs(unStableSeg.get(i).getmFloor() - unStableSeg.get(i-1).getmFloor()) <= ita) {
                if (unStableSeg.get(i).getDuration() > maxTimeSeg.getDuration()) {
                    maxTimeSeg = unStableSeg.get(i);
                }
                //mergeDuration += unStableSeg.get(i).getDuration();
                end = unStableSeg.get(i).getEnd();
            }
            else {
                seg.add(new Segmentation(Segmentation.intermediate, start, end, maxTimeSeg.getmFloor()));
                //seg.add(new Segmentation(Segmentation.intermediate, mergeDuration, maxTimeSeg.getmFloor()));
                //mergeDuration = unStableSeg.get(i).getDuration();
                maxTimeSeg = unStableSeg.get(i);
                start = unStableSeg.get(i).getStart();
                end = unStableSeg.get(i).getEnd();
            }
        }
        seg.add(new Segmentation(Segmentation.intermediate, start, end, maxTimeSeg.getmFloor()));
    }

    /**
     * check the M can be merged by the S or not
     * @param S
     * @param M
     * @return
     */
    public boolean check(Segmentation S, Segmentation M) {
        if (Math.abs(S.getmFloor() - M.getmFloor()) <= ita) {
            if (M.getDuration() / S.getDuration() < eta) {
                return true;
            }
            else return false;
        }
        else return false;
    }

    /**
     * floor value correction
     * @param segmentations
     * @return
     */
    public List<Segmentation> valueCorrection(List<Segmentation> segmentations, PointInSeg pointInSeg[]) {
        if (segmentations.size() <= 1) {
            return segmentations;
        }

        ArrayList<Segmentation> finalSeg = new ArrayList<Segmentation>();
        ArrayList<Segmentation> seg = new ArrayList<Segmentation>();
        ArrayList<Segmentation> unStableSeg = new ArrayList<Segmentation>();
        for (int i = 0; i < segmentations.size(); i++) {
            List<Segmentation> neighbors = new LinkedList<Segmentation>();
            if (i != 0) {
                neighbors.add(segmentations.get(i - 1));
            }

            if (i != segmentations.size() - 1) {
                neighbors.add(segmentations.get(i + 1));
            }

            if (isUnstable(segmentations.get(i), neighbors, pointInSeg, i)) {
                segmentations.get(i).setStatus(Segmentation.unstable);
                unStableSeg.add(segmentations.get(i));
            }
            else {
                unStableSegMerge(unStableSeg, seg);
                unStableSeg.clear();
                segmentations.get(i).setStatus(Segmentation.stable);
                seg.add(segmentations.get(i));
            }

            if (i == segmentations.size() - 1) {
                unStableSegMerge(unStableSeg, seg);
            }
        }

/*
        System.out.println("------>");
        for (int i = 0; i < seg.size(); i++) {
            System.out.println(seg.get(i).toString());
        }
*/

        int firstSID = -1;

        for (int i = 0; i < seg.size(); i++) {
            if(seg.get(i).getStatus() == Segmentation.stable) {
                firstSID = i;
                break;
            }
        }

        if (firstSID == -1) {
            Segmentation maxTimeSeg = null;
            for (int i = 0; i < seg.size(); i++) {
                if (maxTimeSeg == null) {
                    maxTimeSeg = seg.get(i);
                    firstSID = i;
                }
                else if (maxTimeSeg.getDuration() < seg.get(i).getDuration()) {
                    maxTimeSeg = seg.get(i);
                    firstSID = i;
                }
            }

            seg.get(firstSID).setStatus(Segmentation.stable);
            //maxTimeSeg.setStatus(Segmentation.stable);
        }

        Segmentation preS = seg.get(firstSID), preM = null;
        List<Segmentation> tmp = new LinkedList<Segmentation>();
        for (int i = firstSID - 1; i >= 0; i--) {
            if (check(preS, seg.get(i))) {
                int floor = preS.getmFloor();
                int start = seg.get(i).getStart();
                int end = preS.getEnd();
                //double duration = preS.getDuration() + seg.get(i).getDuration();
                preS = new Segmentation(Segmentation.stable, start, end, floor);
            }
            else {
                tmp.add(preS);
                preS = seg.get(i);
                preS.setStatus(Segmentation.stable);
            }
        }
        tmp.add(preS);

        for (int i = tmp.size() - 1; i > 0; i--) {
            finalSeg.add(tmp.get(i));
        }

        preS = tmp.get(0);
        preM = null;

        for (int i = firstSID + 1; i < seg.size(); i++) {
            if (seg.get(i).getStatus() == Segmentation.stable) {
                if (preM == null) {
                    finalSeg.add(preS);
                    preS = seg.get(i);
                }
                else {
                    if (preM.getmFloor() == preS.getmFloor() && preM.getmFloor() == seg.get(i).getmFloor()
                            && preS.getDuration() + seg.get(i).getDuration() >= preM.getDuration()) {
                        //double duration = preS.getDuration() + preM.getDuration() + seg.get(i).getmFloor();
                        int start = preS.getStart();
                        int end = seg.get(i).getEnd();
                        preS = new Segmentation(Segmentation.stable, start, end, preM.getmFloor());
                        preM = null;
                    }
                    else {
                        if (preS.getDuration() > seg.get(i).getDuration() && check(preS, preM)) {
                            int floor = preS.getmFloor();
                            int start = preS.getStart();
                            int end = preM.getEnd();
                            //double duration = preS.getDuration() + preM.getDuration();
                            preS = new Segmentation(Segmentation.stable, start, end, floor);
                            finalSeg.add(preS);
                            preS = seg.get(i);
                            preM = null;
                        }
                        else if (check(seg.get(i), preM)){
                            finalSeg.add(preS);
                            int floor = seg.get(i).getmFloor();
                            int start = preM.getStart();
                            int end = seg.get(i).getEnd();
                            //double duration = seg.get(i).getDuration() + preM.getDuration();
                            preS = new Segmentation(Segmentation.stable, start, end, floor);
                            preM = null;
                        }
                        else {
                            finalSeg.add(preS);
                            preM.setStatus(Segmentation.stable);
                            finalSeg.add(preM);
                            preS = seg.get(i);
                            preM = null;
                        }
                    }
                }
            }
            else {
                if (preM == null) {
                    preM = seg.get(i);
                }
                else {
                    if (check(preS, preM)) {
                        int floor = preS.getmFloor();
                        //double duration = preS.getDuration() + preM.getDuration();
                        int start = preS.getStart();
                        int end = preM.getEnd();
                        preS = new Segmentation(Segmentation.stable, start, end, floor);
                        preM = seg.get(i);
                    }
                    else {
                        finalSeg.add(preS);
                        preS = preM;
                        preS.setStatus(Segmentation.stable);
                        preM = seg.get(i);
                    }
                }
            }
        }

        if (preM != null) {
            if(check(preS, preM)) {
                int floor = preS.getmFloor();
                //double duration = preS.getDuration() + preM.getDuration();
                int start = preS.getStart();
                int end = preM.getEnd();
                preS = new Segmentation(Segmentation.stable, start, end, floor);
                finalSeg.add(preS);
            }
            else {
                finalSeg.add(preS);
                preM.setStatus(Segmentation.stable);
                finalSeg.add(preM);
            }
        }
        else {
            finalSeg.add(preS);
        }

        return finalSeg;
    }

    /*
    public void writeAns(String record) {
        File answerFile = new File(System.getProperty("user.dir") + "/Answer.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(answerFile, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            fw.write(record);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    */

    /*
    public static int dis = 0;
    public static int tot = 0;
    public static double aveAccuracy = 0;
    public static double totAccuracy = 0;
    public static double cntValueCorrect = 0;
    public void test(List<Segmentation> ans, int personID, File noiseFile) {
        File curPersonFile = new File(System.getProperty("user.dir") + "/data3/person" + personID + ".txt");

        List<Record> records = readPersonData(curPersonFile);

        List<Record> records2 = readPersonData(noiseFile);

        int cnt = 0;
        for (int i = 0; i < records.size(); i++) {
            int truefloor = records.get(i).getFloor();
            int trueTime = records.get(i).getTime();
            int flag = 0;
            //System.out.println(truefloor + " " + trueTime);
            for (int j = 0; j < ans.size(); j++) {
                if (trueTime >= ans.get(j).getStart() && trueTime <= ans.get(j).getEnd()) {
                    if (truefloor == ans.get(j).getmFloor()) cnt++;
                    dis += Math.abs(truefloor - ans.get(j).getmFloor());
                    flag = 1;
                }
            }

            if (flag == 0) {
                if (records.get(i).getFloor() == records2.get(i).getFloor()) cnt++;
                dis += Math.abs(records.get(i).getFloor() - records2.get(i).getFloor());
            }
            tot ++;
        }

/*
        if ((double)cnt / (double)records.size() < 0.3) {
            System.out.println("OOOOO O  " + personID + " " + (double)cnt / (double)records.size());
        }
*/

    /*
        cntValueCorrect += cnt;
        aveAccuracy = aveAccuracy + (double)cnt / (double)records.size();
        totAccuracy = totAccuracy + 1;
*/
/*
        if (cnt != 0) writeAns(new String((double)cnt / (double)records.size() + ","));
        else {
            System.out.println(personID + " " + cnt + " " + records.size());
            writeAns(new String(0 + ","));
        }
*/
//        writeAns(new String(personID + ","));
/*
        writeAns(new String((records.get(records.size() - 1).getTime() - records.get(0).getTime()) + ","));
*/
    //System.out.println(personID + " " + cnt + " " + records.size() + " " + (double)cnt / (double)records.size());
//    }

    public void addPointToSeg(PointInSeg pointInSeg[], List<Record> records, List<Segmentation> seg) {

        if (pointInSeg == null || pointInSeg.length == 0) return;

        int cur = 0;

        for (Record record : records) {
            if (record.getTime() >= seg.get(cur).getStart() && record.getTime() <= seg.get(cur).getEnd()) {
                pointInSeg[cur].addPoint(new Point(record.getX(), record.getY()));
            }
            else {
                cur++;
                pointInSeg[cur].addPoint(new Point(record.getX(), record.getY()));
            }
        }
    }

    public void writevalueCorrection(List<Segmentation> ans, int personID) {
        File noiseDataFile = new File(System.getProperty("user.dir") + "/obsData/person" + personID + ".txt");

        File wrtieDataFile = new File(System.getProperty("user.dir") + "/FloorCleanData/person" + personID + ".txt");

        FileWriter fw = null;

        try {
            fw = new FileWriter(wrtieDataFile);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Record> noiseRecords = readPersonData(noiseDataFile);

        for (int i = 0; i < noiseRecords.size(); i++) {

            int newfloor = -1;
            for (int j = 0; j < ans.size(); j++) {
                if (noiseRecords.get(i).getTime() >= ans.get(j).getStart() && noiseRecords.get(i).getTime() <= ans.get(j).getEnd()) {
                    newfloor = ans.get(j).getmFloor();
                    break;
                }
            }

            if (newfloor == -1) newfloor = noiseRecords.get(i).getFloor();

            int time = noiseRecords.get(i).getTime();
            double x = noiseRecords.get(i).getX();
            double y = noiseRecords.get(i).getY();
            int parID = noiseRecords.get(i).getParID();
            boolean isStay = noiseRecords.get(i).isStay();
            boolean isOutlier = noiseRecords.get(i).isOutlier();

            Record newRecord = new Record(time, x, y, newfloor, parID, isStay, isOutlier);

            try {
                fw.write(newRecord.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (personID % 997 == 0) System.out.println("person" + personID + " clean successful!");

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * clean data
     */
    public void dataClean() {

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File curPersonFile = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

            List<Record> records = readPersonData(curPersonFile);

            //if (records.size() < 30) continue;

            List<Segmentation> segmentations = getSegmentation(records);

            PointInSeg pointInSeg[] = new PointInSeg[segmentations.size()];

            for (int j = 0; j < segmentations.size(); j++) pointInSeg[j] = new PointInSeg();

            addPointToSeg(pointInSeg, records, segmentations);

/*
            for (int j = 0; j < segmentations.size(); j++) {
                System.out.println(segmentations.get(j).toString());
            }
*/
            List<Segmentation> ans = valueCorrection(segmentations, pointInSeg);



            File writeFile = new File(System.getProperty("user.dir") + "/FloorCleanData/person" + i + ".txt");
            writevalueCorrection(ans, i);


            //test(ans, i, curPersonFile);
/*
            System.out.println("PPPPPPPPPPPPPPPPPPP");
            for (int j = 0; j < ans.size(); j++) {
                System.out.println(ans.get(j).toString());
            }
*/
        }
    }

    public void dataClean2() {
        ArrayList<Par> parList = ReadPar.getPar();
        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File curPersonFile = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

            List<Record> records = readPersonData(curPersonFile);

            File wrtieDataFile = new File(System.getProperty("user.dir") + "/FloorCleanData/person" + i + ".txt");

            FileWriter fw = null;

            try {
                fw = new FileWriter(wrtieDataFile);
                fw.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (records.size() < validRecordSize) {
                for (int j = 0; j < records.size(); j++) {
                    try {
                        fw.write(records.get(j).toString() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                continue;
            }

            Record last = records.get(0);

            if (last != null) {
                int time = last.getTime();
                double x = last.getX();
                double y = last.getY();
                int parID = last.getParID();
                boolean isStay = last.isStay();
                boolean isOutlier = last.isOutlier();

                Record newRecord = new Record(time, x, y, last.getFloor(), parID, isStay, isOutlier);

                if (last != null) {
                    try {
                        fw.write(newRecord.toString() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            for (int j = 1; j < records.size(); j++) {

                IdrObj s = new IdrObj(0, new Point(last.getX(), last.getY(), last.getFloor()), parList.get(last.getParID()));
                IdrObj t = new IdrObj(0, new Point(records.get(j).getX(), records.get(j).getY(), records.get(j).getFloor()), parList.get(records.get(j).getParID()));
                List<Pair> path = new LinkedList<Pair>();

                //double dis = PointToPoint.move(s, t, path);

                double dis = p2pDis.getP2pDis(s, t);

                //System.out.println(dis + " " + tdis);

                //System.out.println(s.toString());
                //System.out.println(t.toString());

                double speed = (double)dis / (double)(records.get(j).getTime() - last.getTime());

                //System.out.println("time : " + records.get(j).getTime() +  " speed : " + speed);

                if (speed < 2.0) {
                    int time = records.get(j).getTime();
                    double x = records.get(j).getX();
                    double y = records.get(j).getY();
                    int parID = records.get(j).getParID();
                    boolean isStay = records.get(j).isStay();
                    boolean isOutlier = records.get(j).isOutlier();

                    Record newRecord = new Record(time, x, y, records.get(j).getFloor(), parID, isStay, isOutlier);

                    last = newRecord;
                    //System.out.println(records.get(j).getFloor());

                    if (last != null) {
                        try {
                            fw.write(newRecord.toString() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                else {
                    int time = records.get(j).getTime();
                    double x = records.get(j).getX();
                    double y = records.get(j).getY();
                    int parID = records.get(j).getParID();
                    boolean isStay = records.get(j).isStay();
                    boolean isOutlier = records.get(j).isOutlier();

                    parID = new Point(x, y, last.getFloor()).getCurrentPar().getmID();
                    Record newRecord = new Record(time, x, y, last.getFloor(), parID, isStay, isOutlier);

                    last = newRecord;
                    //System.out.println(last.getFloor());

                    if (last != null) {
                        try {
                            fw.write(newRecord.toString() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (i % 998 == 0) System.out.println("person" + i + " clean successful");

        }
    }

    public void init() {
        DataGen dataGen = new DataGen();

        dataGen.genIndoorSpace();

        for (int i = 0; i < IndoorSpace.gPartitions.size(); i++) {
            double x1 = IndoorSpace.gPartitions.get(i).getX1() * 2;
            double x2 = IndoorSpace.gPartitions.get(i).getX2() * 2;
            double y1 = IndoorSpace.gPartitions.get(i).getY1() * 2;
            double y2 = IndoorSpace.gPartitions.get(i).getY2() * 2;

            IndoorSpace.gPartitions.get(i).setX1(x1);
            IndoorSpace.gPartitions.get(i).setX2(x2);
            IndoorSpace.gPartitions.get(i).setY1(y1);
            IndoorSpace.gPartitions.get(i).setY2(y2);
        }

        dataGen.initRTree();

        dataGen.duplicateIndoorSpace(DataGenConstant.nFloor);

        File floorCleanData = new File(System.getProperty("user.dir") + "/FloorCleanData");

        floorCleanData.mkdir();
    }

    public static void main(String arge[]) {
        FloorValueCorrection floorValueCorrection = new FloorValueCorrection();
        floorValueCorrection.init();
        //floorValueCorrection.dataClean();
        floorValueCorrection.dataClean2();
    }
}
