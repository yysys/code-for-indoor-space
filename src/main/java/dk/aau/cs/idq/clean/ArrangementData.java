package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.datagen.TraDataGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class ArrangementData {

    public static FileWriter obsFW[] = new FileWriter[TraDataGen.totalPerson];
    public static FileWriter trueFW[] = new FileWriter[TraDataGen.totalPerson];

    /**
     * initialization for obs data
     *
     */
    public void InitObsData() {

        File obsData = new File(System.getProperty("user.dir") + "/obsData");

        obsData.mkdir();

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File fwFile = new File(obsData + "/person" + i + ".txt");
            try {
                obsFW[i] = new FileWriter(fwFile);
                obsFW[i].write("");
                obsFW[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                obsFW[i] = new FileWriter(fwFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * initialization for true data
     */
    public void InitTrueData() {

        File trueData = new File(System.getProperty("user.dir") + "/trueData");

        trueData.mkdir();

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File fwFile = new File(trueData + "/person" + i + ".txt");
            try {
                trueFW[i] = new FileWriter(fwFile);
                trueFW[i].write("");
                trueFW[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                trueFW[i] = new FileWriter(fwFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * arrange observe data to dir
     */
    public void ArrangeObsData() {
        File noiseTraDataFile = new File(System.getProperty("user.dir") + "/NoiseTraData.txt");
        Scanner in = null;
        try {
            in = new Scanner(noiseTraDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        while(in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int curTime = Integer.valueOf(input[0]).intValue();
            int personID = Integer.valueOf(input[1]).intValue();
            double x = Double.valueOf(input[2]).doubleValue();
            double y = Double.valueOf(input[3]).doubleValue();
            int mFloor = Integer.valueOf(input[4]).intValue();
            int parID = Integer.valueOf(input[5]).intValue();
            int isStay = Integer.valueOf(input[6]).intValue();
            int isOutlier = Integer.valueOf(input[7]).intValue();

            //if (personID % 100 == 0) System.out.println(curTime + " " + personID);

            try {
                obsFW[personID].write(curTime + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + " " + isOutlier + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            File curPersonFile = new File(System.getProperty("user.dir") + "/obsData/person" + personID + ".txt");
            FileWriter fw = null;
            try {
                fw = new FileWriter(curPersonFile, true);
                fw.write(curTime + " " + x + " " + y + " " + mFloor + " " + parID + "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
        }
    }

    /**
     * arrange true data to dir
     */
    public void ArrangeTrueData() {
        File obsTraDataFile = new File(System.getProperty("user.dir") + "/ObsTraData.txt");
        Scanner in = null;
        try {
            in = new Scanner(obsTraDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        while(in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int curTime = Integer.valueOf(input[0]).intValue();
            int personID = Integer.valueOf(input[1]).intValue();
            double x = Double.valueOf(input[2]).doubleValue();
            double y = Double.valueOf(input[3]).doubleValue();
            int mFloor = Integer.valueOf(input[4]).intValue();
            int parID = Integer.valueOf(input[5]).intValue();
            int isStay = Integer.valueOf(input[6]).intValue();
            int isOutlier = Integer.valueOf(input[7]).intValue();

            //if (personID % 100 == 0) System.out.println(curTime + " " + personID);

            try {
                trueFW[personID].write(curTime + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + " " + isOutlier + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            /*
            File curPersonFile = new File(System.getProperty("user.dir") + "/trueData/person" + personID + ".txt");
            FileWriter fw = null;
            try {
                fw = new FileWriter(curPersonFile, true);
                fw.write(curTime + " " + x + " " + y + " " + mFloor + " " + parID + "\n");
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */
        }
    }

    /**
     * arrange
     */
    public void arrangement() {
        InitObsData();
        ArrangeObsData();

        for (FileWriter fw : obsFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        InitTrueData();
        ArrangeTrueData();

        for (FileWriter fw : trueFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String arge[]) {
        ArrangementData arrangementData = new ArrangementData();
        arrangementData.InitObsData();
        arrangementData.ArrangeObsData();

        for (FileWriter fw : obsFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        arrangementData.InitTrueData();
        arrangementData.ArrangeTrueData();

        for (FileWriter fw : trueFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
