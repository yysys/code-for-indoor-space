package dk.aau.cs.idq.update;

import dk.aau.cs.idq.datagen.TraDataGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class groundTruthGen {

    public static FileWriter truthFW[] = new FileWriter[TraDataGen.totalPerson];

    public void InitGroundTruthData() {

        File truthData = new File(System.getProperty("user.dir") + "/groundTruth");

        truthData.mkdir();

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File fwFile = new File(truthData + "/person" + i + ".txt");
            try {
                truthFW[i] = new FileWriter(fwFile);
                truthFW[i].write("");
                truthFW[i].close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                truthFW[i] = new FileWriter(fwFile, true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void dataGen() {
        File obsTraDataFile = new File(System.getProperty("user.dir") + "/TrajectoryData.txt");
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
            double x = Double.valueOf(input[3]).doubleValue();
            double y = Double.valueOf(input[4]).doubleValue();
            int mFloor = Integer.valueOf(input[5]).intValue();
            int parID = Integer.valueOf(input[6]).intValue();
            int isStay = Integer.valueOf(input[7]).intValue();

            if (personID % 100 == 0) System.out.println(curTime + " " + personID);

            try {
                truthFW[personID].write(curTime + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void Gen() {
        InitGroundTruthData();
        dataGen();

        for (FileWriter fw : truthFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
        groundTruthGen groundTruthGen = new groundTruthGen();
        groundTruthGen.InitGroundTruthData();
        groundTruthGen.dataGen();

        for (FileWriter fw : truthFW) {
            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
