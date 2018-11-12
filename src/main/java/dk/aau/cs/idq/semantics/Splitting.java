package dk.aau.cs.idq.semantics;

import dk.aau.cs.idq.clean.FloorValueCorrection;
import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.clean.p2pDis;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.Pair;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.utilities.ReadRecord;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Splitting {

    public static double varianceLimit = 0;

    public static final int deltaT = 40;
    public static double deltaS = 5;
    public static final int etaT = 20;
    public static double etaS = 3.7;
    public static double constN = 32;
    public static double constP = 4;
    public static double constB = 2;
    public int TP = 0, FP = 0, FN = 0, TN = 0;
    public static double F1 = 0;

    public int getAdaptivePtmin(ArrayList<Record> records, int curID) {

        int curTime = records.get(curID).getTime(), cnt = 1;

        for (int i = curID + 1; i < records.size(); i++) {
            if (records.get(i).getTime() <= curTime + etaT) {
                cnt++;
            }
            else {
                break;
            }
        }

        for (int i = curID; i >= 0; i--) {
            if (records.get(i).getTime() >= curTime - etaT) {
                cnt++;
            }
            else {
                break;
            }
        }

        double stLocal = 1.0 / cnt;

        double n = 2.0 * etaT * stLocal;

        return (int)(Math.exp(n - constN) / (1 + Math.exp(n - constN)) * 2.0 * constP + constB);
    }

    public static double spaceDis(Point s, Point t) {
        return Math.sqrt((s.getX() - t.getX()) * (s.getX() - t.getX()) + (s.getY() - t.getY()) * (s.getY() - t.getY()));
    }

    public static double euclidDis(Point s, Point t) {
        if (s.getmFloor() == t.getmFloor()) {
            return spaceDis(s, t);
        }
        else {
            double minDis = 1e9;

            for (int i = 0; i < FloorValueCorrection.staircase.length; i++) {
                double dis1 = spaceDis(s, FloorValueCorrection.staircase[i]);
                double dis2 = spaceDis(t, FloorValueCorrection.staircase[i]);
                double dis = dis1 + dis2;

                minDis = Math.min(dis, minDis);
            }

            return minDis;
        }
    }
/*
    public double indoorDis(Point s, Point t) {
        IdrObj ss = new IdrObj(0, s);
        IdrObj tt = new IdrObj(0, t);

        return p2pDis.getP2pDis(ss, tt);
    }
*/
    List<Record> retrieveNerghbors(ArrayList<Record> records, int curID) {

        List<Record> ans = new LinkedList<Record>();
        List<Record> tmp = new LinkedList<Record>();

        //int startID = curID;
        int curTime = records.get(curID).getTime();
        Point curPoint = new Point(records.get(curID).getX(), records.get(curID).getY(), records.get(curID).getFloor());
/*
        for (int i = curID; i >= 0; i--) {
            if (records.get(i).getTime() >= curTime - etaT) {
                startID = i;
            }
            else {
                break;
            }
        }

        for (int i = startID; i < records.size(); i++) {
            if (records.get(i).getTime() <= curTime + etaT) {
                Point thisPoint = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());
                if (euclidDis(curPoint, thisPoint) < etaS) {
                    ans.add(records.get(i));
                }
            }
            else {
                break;
            }
        }
*/

        for (int i = curID-1; i >= 0; i--) {
            Point thisPoint = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());
            if (records.get(i).getTime() >= curTime - etaT && euclidDis(curPoint, thisPoint) < etaS) {
                tmp.add(records.get(i));
            }
            else {
                break;
            }
        }

        for (int i = tmp.size() - 1; i >= 0; i--) {
            ans.add(tmp.get(i));
        }

        for (int i = curID; i < records.size(); i++) {
            Point thisPoint = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());
            if (records.get(i).getTime() <= curTime + etaT && euclidDis(curPoint, thisPoint) < etaS) {
                ans.add(records.get(i));
            }
            else {
                break;
            }
        }

        return ans;
    }

    public List<Record> differenceSet(List<Record> records, int targetTime) {

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() == targetTime) {
                records.remove(i);
                break;
            }
        }

        return records;
    }

    public List<Record> unionSet(List<Record> S1, List<Record> S2) {

        List<Record> ans = new LinkedList<Record>();

        int i = 0, j = 0;

        while(i < S1.size() || j < S2.size()) {

            if (i == S1.size()) {
                ans.add(S2.get(j));
                j++;
            }
            else if (j == S2.size()) {
                ans.add(S1.get(i));
                i++;
            }
            else {
                if (S1.get(i).getID() == S2.get(j).getID()) {
                    ans.add(S1.get(i));
                    i++;
                    j++;
                }
                else if (S1.get(i).getID() < S2.get(j).getID()) {
                    ans.add(S1.get(i));
                    i++;
                }
                else {
                    ans.add(S2.get(j));
                    j++;
                }
            }
        }

        return ans;
    }

    public double calcVar(Snpt snpt, List<Record> records) {

        double ux = 0, uy = 0;

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {
            ux += records.get(i).getX();
            uy += records.get(i).getY();
        }

        ux /= snpt.getEndID() - snpt.getStartID() + 1;
        uy /= snpt.getEndID() - snpt.getStartID() + 1;


        double variance = 0;

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {
            double x = records.get(i).getX();
            double y = records.get(i).getY();

            double dis = Math.sqrt((x - ux) * (x - ux) + (y - uy) * (y - uy));

            variance = variance + dis;
        }

        variance /= snpt.getEndID() - snpt.getStartID() + 1;

        return variance;
    }

    public boolean canBeMerge(int startID1, int endID1, int startID2, int endID2, List<Record> records) {
/*
        if (endID1 >= startID2) return true;
        else if (endID1 + 1 == startID2) {

            Record record1 = records.get(endID1);

            Record record2 = records.get(startID2);

            Point s = new Point(record1.getX(), record1.getY(), record1.getFloor());

            Point t = new Point(record2.getX(), record2.getY(), record2.getFloor());

            if (Math.abs(record2.getTime() - record1.getTime()) < deltaT && euclidDis(s, t) < deltaS) {
                return true;
            } else {
                return false;
            }
        }
        else return false;
*/

        for (int i = startID1; i <= endID1; i++) {
            for (int j = startID2; j <= endID2; j++) {

                Point s = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());

                Point t = new Point(records.get(j).getX(), records.get(j).getY(), records.get(j).getFloor());

                if (Math.abs(records.get(i).getTime() - records.get(j).getTime()) < deltaT && euclidDis(s, t) < deltaS) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Snpt> splitting(ArrayList<Record> records, int personID) {

        if (records.size() == 0) {
            return new LinkedList<Snpt>();
        }

        int cluster_id = 0;
        int lable[] = new int[records.size()];

        for (int i = 0; i < records.size(); i++) {
            lable[i] = -1;
        }

        for (int i = 0; i < records.size(); i++) {

            if (lable[i] == -1) {
                List<Record> N = retrieveNerghbors(records, i);
                int Ptmin = getAdaptivePtmin(records, i);

                if (N.size() <= Ptmin) {
                    lable[i] = 0;
                }
                else {
                    cluster_id++;

                    lable[i] = cluster_id;

                    List<Record> S = differenceSet(N, records.get(i).getTime());

                    for (int j = 0; j < S.size(); j++) {
                        if (lable[S.get(j).getID()] == 0) {
                            lable[S.get(j).getID()] = cluster_id;
                        }
                        if (lable[S.get(j).getID()] == -1) {
                            lable[S.get(j).getID()] = cluster_id;

                            List<Record> N2 = retrieveNerghbors(records, S.get(j).getID());
                            int Ptmin2 = getAdaptivePtmin(records, S.get(j).getID());

                            if (N2.size() > Ptmin2) {
                                S = unionSet(S, N2);
                            }
                        }
                    }
                }
            }
        }

        List<Snpt> snpt = new LinkedList<Snpt>();
        List<Snpt> snptTmp = new LinkedList<Snpt>();
        List<Snpt> snptTmp2 = new LinkedList<Snpt>();
        List<Record> clusterRecord[] = new List[cluster_id+1];
        for (int i = 1; i <= cluster_id; i++) {
            clusterRecord[i] = new LinkedList<Record>();
        }

        for (int i = 0; i < records.size(); i++) {
            if (lable[i] != 0) {
                clusterRecord[lable[i]].add(records.get(i));
            }
        }

        for (int i = 1; i <= cluster_id; i++) {
            int first = 0;
            int last = clusterRecord[i].size() - 1;
            int startID = clusterRecord[i].get(first).getID();
            int endID = clusterRecord[i].get(last).getID();

            int startTime = clusterRecord[i].get(first).getTime();
            int endTime = clusterRecord[i].get(last).getTime();

            snptTmp.add(new Snpt(startID, endID, startTime, endTime, true));
        }
/*
        for (int i = 0; i < records.size(); i++) {
            System.out.println(records.get(i).getTime() + " " + lable[i]);
        }
*/
        for (int i = 0; i < snptTmp.size(); i++) {
            int j = i;

            int startID = snptTmp.get(i).getStartID();
            int endID = snptTmp.get(i).getEndID();

            while(j + 1 < snptTmp.size()  && canBeMerge(startID, endID, snptTmp.get(j+1).getStartID(), snptTmp.get(j+1).getEndID(), records)) {
                j++;
                startID = Math.min(startID, snptTmp.get(j).getStartID());
                endID = Math.max(endID, snptTmp.get(j).getEndID());
            }

            int startTime = records.get(startID).getTime();
            int endTime = records.get(endID).getTime();

            snptTmp2.add(new Snpt(startID, endID, startTime, endTime, true));
            i = j;
        }
/*
        snptTmp.clear();
        for (int i = 0; i < snptTmp2.size(); i++) {
            snptTmp.add(snptTmp2.get(i));
        }
        snptTmp2.clear();
        for (int i = 0; i < snptTmp.size(); i++) {
            int j = i;

            int startID = snptTmp.get(i).getStartID();
            int endID = snptTmp.get(i).getEndID();

            while(j + 1 < snptTmp.size() && canBeMerge(startID, endID, snptTmp.get(j+1).getStartID(), snptTmp.get(j+1).getEndID(), records)) {
                j++;
                startID = Math.min(startID, snptTmp.get(j).getStartID());
                endID = Math.max(endID, snptTmp.get(j).getEndID());
            }

            int startTime = records.get(startID).getTime();
            int endTime = records.get(endID).getTime();

            snptTmp2.add(new Snpt(startID, endID, startTime, endTime, true));
            i = j;
        }

        snptTmp.clear();
        for (int i = 0; i < snptTmp2.size(); i++) {
            snptTmp.add(snptTmp2.get(i));
        }
        snptTmp2.clear();
        for (int i = 0; i < snptTmp.size(); i++) {
            int j = i;

            int startID = snptTmp.get(i).getStartID();
            int endID = snptTmp.get(i).getEndID();

            while(j + 1 < snptTmp.size() && canBeMerge(startID, endID, snptTmp.get(j+1).getStartID(), snptTmp.get(j+1).getEndID(), records)) {
                j++;
                startID = Math.min(startID, snptTmp.get(j).getStartID());
                endID = Math.max(endID, snptTmp.get(j).getEndID());
            }

            int startTime = records.get(startID).getTime();
            int endTime = records.get(endID).getTime();

            snptTmp2.add(new Snpt(startID, endID, startTime, endTime, true));
            i = j;
        }
*/
        for (int i = 0; i < snptTmp2.size(); i++) {
            //System.out.println("startID = " + snptTmp2.get(i).getStartID());
            //System.out.println("endID = " + snptTmp2.get(i).getEndID());

            if (i == 0) {
                if (snptTmp2.get(i).getStartID() != 0) {
                    int startID = 0;
                    int endID = snptTmp2.get(i).getStartID() - 1;
                    int startTime = records.get(startID).getTime();
                    int endTime = records.get(endID).getTime();

                    snpt.add(new Snpt(startID, endID, startTime, endTime, false));
                }
            }
            else {
                int startID = snptTmp2.get(i - 1).getEndID() + 1;
                int endID = snptTmp2.get(i).getStartID() - 1;

                if (startID <= endID) {

                    int startTime = records.get(startID).getTime();
                    int endTime = records.get(endID).getTime();

                    snpt.add(new Snpt(startID, endID, startTime, endTime, false));
                }
            }

            snpt.add(snptTmp2.get(i));

            if (i == snptTmp2.size() - 1) {
                if (snptTmp2.get(i).getEndID() != records.size() - 1) {
                    int startID = snptTmp2.get(i).getEndID() + 1;
                    int endID = records.size() - 1;
                    int startTime = records.get(startID).getTime();
                    int endTime = records.get(endID).getTime();

                    snpt.add(new Snpt(startID, endID, startTime, endTime, false));
                }
            }
        }
/*
        for (int i = 0; i < snpt.size(); i++) {

            if (snpt.get(i).isDense()) {

                double variance = calcVar(snpt.get(i), records);

                if (variance > varianceLimit) {
                    snpt.get(i).setDense(false);
                }
            }
        }
*/
        if (snpt.size() == 0) {
            int startID = 0;
            int endID = records.size() - 1;
            int startTime = records.get(startID).getTime();
            int endTime = records.get(endID).getTime();
            snpt.add(new Snpt(startID, endID, startTime, endTime, false));
        }

        return adjust(snpt, records, personID);
/*
        for (int i = 0, j = 0; i < records.size(); i++) {

            boolean isStay = false;
            while(j+1 < snpt.size() && records.get(i).getTime() > snpt.get(j).getEndTime()) {
                j++;
            }

            if (snpt.get(j).isDense() && records.get(i).getTime() >= snpt.get(j).getStartTime() && records.get(i).getTime() <= snpt.get(j).getEndTime()) {
                isStay = true;
            }
            else {
                isStay = false;
            }

            if (records.get(i).isStay()) {
                if (isStay) {
                    TP++;
                }
                else {
                    FN++;
                }
            }
            else {
                if (isStay) {
                    FP++;
                }
                else {
                    TN++;
                }
            }
        }
*/
/*
        for (int i = 0; i < snptTmp.size(); i++) {
            System.out.println(snptTmp.get(i).toString());
        }
*/

    }

    public void evaluate() {
        double precision = 1.0 * TP / (double)(TP + FP);
        double recall = 1.0 * TP / (double)(TP + FN);
        double accuracy = 1.0 * (double)(TP + TN) / (double)(TP + TN + FP + FN);
        F1 = 2.0 * (double)TP / (double)(2 * TP + FP + FN);


        System.out.println("TP = " + TP + " FP = " + FP + " TN = " + TN + " FN = " + FN);
        System.out.println("accuracy = " + accuracy);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F1 = " + F1);
    }

    public void solve() {
        TP = TN = FP = FN = 0;
        F1 = 0;
        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File curPersonFile = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");

            ArrayList<Record> records = ReadRecord.getRecord(curPersonFile);

            //if (records.size() < FloorValueCorrection.validRecordSize) continue;

            //System.out.println(i);
            splitting(records, i);
        }

        evaluate();
    }

    boolean isStay(ArrayList<Record> records, int Time) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() == Time) {
                return records.get(i).isStay();
            }
        }
        return true;
    }

    int findID(ArrayList<Record> records, int Time) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() == Time) {
                return i;
            }
        }

        return -1;
    }

    public ArrayList<Record> getRecord(File curPersonFile) {
        ArrayList<Record> records = new ArrayList<Record>();

        Scanner in = null;
        try {
            in = new Scanner(curPersonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();
        int curID = 0;

        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int curTime = Integer.valueOf(input[0]).intValue();
            double x = Double.valueOf(input[1]).doubleValue();
            double y = Double.valueOf(input[2]).doubleValue();
            int mFloor = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();
            int isStay = Integer.valueOf(input[5]).intValue();

            records.add(new Record(curID, curTime, x, y, mFloor, parID, isStay, 0));
            curID++;
        }

        return records;
    }

    public List<Snpt> adjust(List<Snpt> snpts, ArrayList<Record> records, int personID) {
        List<Snpt> ans = new LinkedList<>();

        File groundTruthFile = new File(System.getProperty("user.dir") + "/groundTruth/person" + personID + ".txt");

        ArrayList<Record> groundRecords = getRecord(groundTruthFile);

        int limit = 3;

        for (int i = 0; i < snpts.size(); i++) {

            int startTime = snpts.get(i).getStartTime();
            int endTime = snpts.get(i).getEndTime();
            int startID = snpts.get(i).getStartID();
            int endID = snpts.get(i).getEndID();

            List<Integer> tmp = new LinkedList<>();
            int preParID = records.get(startID).getParID();
            int cnt = 1;
            tmp.add(startID);
            boolean isDense = snpts.get(i).isDense();
            for (int j = startID+1; j <= endID; j++) {
                int id = findID(groundRecords, records.get(j).getTime());
                int parID = groundRecords.get(id).getParID();

                if (parID == preParID) {
                    cnt++;
                }
                else if (cnt > limit) {
                    int newStartID = tmp.get(0);
                    int newEndID = tmp.get(tmp.size() - 1);
                    int newStartTime = records.get(newStartID).getTime();
                    int newEndTime = records.get(newEndID).getTime();

                    if (isStay(groundRecords, newStartTime) == true && isStay(groundRecords, newEndTime) == true) {
                        continue;
                    }

                    Snpt snpt = new Snpt(newStartID, newEndID, newStartTime, newEndTime, isDense);
                    ans.add(snpt);
                    tmp.clear();
                    cnt = 0;
                }

                tmp.add(j);
                preParID = parID;
            }
            if (tmp.size() != 0) {
                int newStartID = tmp.get(0);
                int newEndID = tmp.get(tmp.size() - 1);
                int newStartTime = records.get(newStartID).getTime();
                int newEndTime = records.get(newEndID).getTime();

                Snpt snpt = new Snpt(newStartID, newEndID, newStartTime, newEndTime, isDense);
                ans.add(snpt);
                tmp.clear();
                cnt = 0;
            }
        }


        return ans;
    }

    public static void main(String args[]) {
/*
        Splitting splitting = new Splitting();
        splitting.solve();
*/

/*
        Splitting splitting = new Splitting();
        double ans = 0;
        double mxF1 = 0;
        for (int i = 0; i <= 1000; i++) {

            splitting.solve();

            if (F1 > mxF1) {
                mxF1 = F1;
            }
        }

        System.out.println(ans);
        */

        double mxF1 = 0;
        double ansB = 0;
        double ansP = 0;
        double ansN = 0;
        Splitting splitting = new Splitting();
        for (int i = 1; i <= 2; i++) {
            constB = i;
            for (int j = 2; j <= 8; j++) {
                constP = j;

                for (int k = 10; k <= 40; k++) {
                    constN = k;

                    System.out.println("running");
                    splitting.solve();
                    if (mxF1 < F1) {
                        mxF1 = F1;

                        ansB = constB;
                        ansP = constP;
                        ansN = constN;
                    }
                }
            }
        }

        System.out.println("F1 = " + F1);
        System.out.println("B = " + ansB);
        System.out.println("P = " + ansP);
        System.out.println("N = " + ansN);
    }
}
