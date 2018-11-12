package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Comp {

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

    public int TrueTrue = 0;
    public int TrueFalse = 0;
    public int FalseTrue = 0;
    public int FalseFalse = 0;
    public int firstError = 0;

    public double num1 = 0;
    public double num2 = 0;
    public double dis1 = 0;
    public double dis2 = 0;

    public void evaluate(int personID) {

        File trueDataFile = new File(System.getProperty("user.dir") + "/trueData/person" + personID + ".txt");

        File noiseDataFile = new File(System.getProperty("user.dir") + "/obsData/person" + personID + ".txt");

        File cleanedDataFile = new File(System.getProperty("user.dir") + "/CleanData/person" + personID + ".txt");

        List<Record> trueRecords = readPersonData(trueDataFile);

        List<Record> noiseRecords = readPersonData(noiseDataFile);

        List<Record> cleanedRecords = readPersonData(cleanedDataFile);

        int trueTrue = 0;
        int trueFalse = 0;
        int falseTrue = 0;
        int falseFalse = 0;

        for (int i = 0; i < trueRecords.size(); i++) {

            int trueFloor = trueRecords.get(i).getFloor();
            int obsFloor = noiseRecords.get(i).getFloor();
            int curFloor = cleanedRecords.get(i).getFloor();

            if (obsFloor == trueFloor) {
                if (trueFloor == curFloor) trueTrue++;
                else trueFalse++;
            }
            else {
                if (trueFloor == curFloor) falseTrue++;
                else falseFalse++;
            }

            Point truePoint = new Point(trueRecords.get(i).getX(), trueRecords.get(i).getY(),trueRecords.get(i).getFloor());
            Point obsPoint = new Point(noiseRecords.get(i).getX(), noiseRecords.get(i).getY(), noiseRecords.get(i).getFloor());
            Point curPoint = new Point(cleanedRecords.get(i).getX(), cleanedRecords.get(i).getY(), cleanedRecords.get(i).getFloor());

            IdrObj i1 = new IdrObj(0, truePoint, truePoint.getCurrentPar());
            IdrObj i2 = new IdrObj(0, obsPoint, obsPoint.getCurrentPar());
            IdrObj i3 = new IdrObj(0, curPoint, curPoint.getCurrentPar());

            if (obsFloor == trueFloor) {
                dis1 += p2pDis.getP2pDis(i1, i2);
                num1 += 1.0;
            }

            if (curFloor == trueFloor) {
                dis2 += p2pDis.getP2pDis(i1, i3);
                num2 += 1.0;
            }

        }

        TrueTrue += trueTrue;
        TrueFalse += trueFalse;
        FalseTrue += falseTrue;
        FalseFalse += falseFalse;
    }

    public void floorEvaluate() {

        TrueTrue = 0;
        TrueFalse = 0;
        FalseTrue = 0;
        FalseFalse = 0;

        num1 = 0;
        num2 = 0;
        dis1 = 0;
        dis2 = 0;

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            evaluate(i);
        }

        int tot = TrueTrue + TrueFalse + FalseTrue + FalseFalse;

        double OriAccuracy = (double)TrueTrue / (double)tot + (double)TrueFalse / (double)tot;
        double NowAccuracy = (double)TrueTrue / (double)tot + (double)FalseTrue / (double)tot;

        File file = new File(System.getProperty("user.dir") + "/floorEvaluate.txt");
        FileWriter fw = null;

        File file2 = new File(System.getProperty("user.dir") + "/errorEvaluate.txt");
        FileWriter fw2 = null;

        try {
            fw = new FileWriter(file, true);
            fw2 = new FileWriter(file2, true);
            fw.write(NowAccuracy + "\n");
            fw2.write(dis2 / num2 + "\n");
            fw.close();
            fw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("OriAccuracy = " + OriAccuracy + " NowAccuracy = " + NowAccuracy);
        System.out.println("Oridis = " + dis1 / num1 + " NowDis = " + dis2 / num2);

        System.out.println((double)TrueTrue / (double)tot);
        System.out.println((double)TrueFalse / (double)tot);
        System.out.println((double)FalseTrue / (double)tot);
        System.out.println((double)FalseFalse / (double)tot);

        System.out.println((double)TrueTrue / (double)tot + (double)FalseTrue / (double)tot);

        System.out.println(TrueTrue + TrueFalse + FalseTrue + FalseFalse);

        System.out.println(firstError + "PPPPPPPPPPPPP");
    }

    public double calcDis(IdrObj a, IdrObj b) {
        double x1 = a.getmTruePos().getX();
        double x2 = b.getmTruePos().getX();

        double y1 = a.getmTruePos().getY();
        double y2 = b.getmTruePos().getY();

        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

/*
    public void errorEvaluate() {

        double pre = 0;
        double now = 0;
        double preE = 0;
        double nowE = 0;
        double num = 0;

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File trueDataFile = new File(System.getProperty("user.dir") + "/trueData/person" + i + ".txt");

            File noiseDataFile = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

            File cleanedDataFile = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");

            List<Record> trueRecords = readPersonData(trueDataFile);

            List<Record> noiseRecords = readPersonData(noiseDataFile);

            List<Record> cleanedRecords = readPersonData(cleanedDataFile);

            for (int j = 0; j < trueRecords.size(); j++) {
                Point truePoint = new Point(trueRecords.get(j).getX(), trueRecords.get(j).getY(),trueRecords.get(j).getFloor());
                Point obsPoint = new Point(noiseRecords.get(j).getX(), noiseRecords.get(j).getY(), noiseRecords.get(j).getFloor());
                Point curPoint = new Point(cleanedRecords.get(j).getX(), cleanedRecords.get(j).getY(), cleanedRecords.get(j).getFloor());

                IdrObj i1 = new IdrObj(0, truePoint, truePoint.getCurrentPar());
                IdrObj i2 = new IdrObj(0, obsPoint, obsPoint.getCurrentPar());
                IdrObj i3 = new IdrObj(0, curPoint, curPoint.getCurrentPar());

                pre += p2pDis.getP2pDis(i1, i2);
                now += p2pDis.getP2pDis(i1, i3);

                preE += calcDis(i1, i2);
                nowE += calcDis(i1, i3);
                num += 1.0;
            }
        }

        File file = new File(System.getProperty("user.dir") + "/errorEvaluate.txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(file, true);
            fw.write(now / num + " " + nowE / num + "\n");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("obsE = " + preE / num + " curE = " + nowE / num);
        System.out.println("obs = " + pre / num + " cur = " + now / num);
    }
*/
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

        File CleanData = new File(System.getProperty("user.dir") + "/CleanData");

        CleanData.mkdir();
    }

    public void var_init()
    {
        ReadDoor.getDoor();
        ReadPar.getPar();
        p2pDis.getD2dDistance();
    }

    public static void main(String arge[]) {
        Comp comp = new Comp();
        comp.floorEvaluate();

        /*
        Comp comp = new Comp();
        comp.floorEvaluate();
        */

/*
        comp.init();
        comp.var_init();
        comp.errorEvaluate();
*/
    }
}
