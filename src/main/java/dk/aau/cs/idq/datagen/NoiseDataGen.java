package dk.aau.cs.idq.datagen;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.timeRandom;

import javax.xml.crypto.Data;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class NoiseDataGen {

    public static List<String> rawDataList = new LinkedList<String>();
    public static List<String> noiseDataList = new LinkedList<String>();

    public static final double waveLength = 3;
    public static final double outlierProb = 0.03;
    public static final double floorDriftProb = 0.015;

    public static final double upwardDriftProb = 0.5;
    public static final double driftOneFloorProb = 0.7;

    public static final double outlierBot = 8;
    public static final double outlierTop = 60;

    public static ObservePersonInfo[] personInfo = null;

    /**
     *
     * initialization the Rtree and InDoorSpace
     *
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

        personInfo = new ObservePersonInfo[TraDataGen.totalPerson];

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            personInfo[i] = new ObservePersonInfo();
        }

        File noiseTraDataFile = new File(System.getProperty("user.dir") + "/NoiseTraData.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(noiseTraDataFile);
            fw.flush();
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        File obsTraDataFile = new File(System.getProperty("user.dir") + "/ObsTraData.txt");
        FileWriter fw2 = null;
        try {
            fw2 = new FileWriter(obsTraDataFile);
            fw2.flush();
            fw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * judge the time and the person is observe point or not
     *
     * @param curTime
     * @param personID
     * @return
     */
    public boolean isObserve(int curTime, int personID) {
        if(personInfo[personID].getLastObsTime() + personInfo[personID].getObsTime() <= curTime) {
            personInfo[personID].setLastObsTime(curTime);
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *
     * @param curTime
     * @param personID
     * @param x
     * @param y
     * @param mFloor
     * @return
     */
    public String wave(int curTime, int personID, double x, double y, int mFloor, int isStay, int isOutlier) {
        Random r = new Random();

        int parID = 0;
        double newx = x, newy = y;
        //System.out.println("---->" + x + " " + y);
        while (true) {
            if (r.nextDouble() < 0.5) newx = x + r.nextDouble() * waveLength;
            else newx = x - r.nextDouble() * waveLength;

            if (r.nextDouble() < 0.5) newy = y + r.nextDouble() * waveLength;
            else newy = y - r.nextDouble() * waveLength;

            //System.out.println(newx + " " + newy + " " +mFloor);
            Par par = new Point(newx, newy, mFloor).getCurrentPar();

            if (par != null) {
                parID = par.getmID();
                break;
            }
        }

        return new String(curTime + " " + personID + " " + newx + " " + newy + " " + mFloor + " " + parID + " " + isStay + " " + isOutlier + "\n");
    }

    /**
     * get the floor
     * @param personID
     * @param mFloor
     * @param curTime
     * @return
     */
    public static int getFloor(int personID, int mFloor, int curTime) {

        if (personInfo[personID].getIsLastFloorDrift()) {
            if (mFloor != personInfo[personID].getOriFloor()) {
                personInfo[personID].setIsLastFloorDrift(false);
                return mFloor;
            }
            else if (personInfo[personID].getLastFloorDriftTime() + personInfo[personID].getFloorDriftDuration() <= curTime) {
                personInfo[personID].setIsLastFloorDrift(false);
                return getFloor(personID, mFloor, curTime);
            }
            else {
                return personInfo[personID].getLastFloorDrift();
            }
        }
        else {
            Random r = new Random();

            if (r.nextDouble() < floorDriftProb) {
                personInfo[personID].setIsLastFloorDrift(true);
                personInfo[personID].setLastFloorDriftTime(curTime);
                personInfo[personID].setOriFloor(mFloor);
                personInfo[personID].setFloorDriftDuration(timeRandom.getDurationTime());
                int newFloor = mFloor;

                if (mFloor == 0) {
                    if (r.nextDouble() < driftOneFloorProb) {
                        newFloor += 1;
                    }
                    else {
                        newFloor += 2;
                    }
                }
                else if (mFloor == 9) {
                    if (r.nextDouble() < driftOneFloorProb) {
                        newFloor -= 1;
                    }
                    else {
                        newFloor -= 2;
                    }
                }
                else {

                    if (r.nextDouble() < upwardDriftProb) {

                        if (mFloor + 2 > 9) {
                            newFloor = mFloor + 1;
                        }
                        else if (r.nextDouble() < driftOneFloorProb) {
                            newFloor += 1;
                        }
                        else {
                            newFloor += 2;
                        }

                    }
                    else {

                        if (mFloor - 2 < 0) {
                            newFloor = mFloor - 1;
                        }
                        else if (r.nextDouble() < driftOneFloorProb) {
                            newFloor -= 1;
                        }
                        else {
                            newFloor -= 2;
                        }

                    }
                }

                personInfo[personID].setLastFloorDrift(newFloor);
                return newFloor;
            }
            else {
                return mFloor;
            }
        }
    }

    /**
     * write
     * @param records
     * @param file
     */
    public void write(List<String> records, File file) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(file, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            for (String record : records) {
                fw.write(record);
            }
            fw.close();
            records.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * wrtie the record to disk
     * @param record
     */
    public void writeNoiseData(String record, int flag)
    {
        if (flag == 0) {
            File obsTraDataFile = new File(System.getProperty("user.dir") + "/NoiseTraData.txt");
            write(noiseDataList, obsTraDataFile);
            return;
        }
        noiseDataList.add(record);

        if (rawDataList.size() > 2000) {
            File obsTraDataFile = new File(System.getProperty("user.dir") + "/NoiseTraData.txt");
            write(rawDataList, obsTraDataFile);
        }
    }

    /**
     * write observation data to disk
     * @param record
     */
    public void writeRawData(String record, int flag) {

        if (flag == 0) {
            File obsTraDataFile = new File(System.getProperty("user.dir") + "/ObsTraData.txt");
            write(rawDataList, obsTraDataFile);
            return;
        }

        rawDataList.add(record);

        if (rawDataList.size() > 2000) {
            File obsTraDataFile = new File(System.getProperty("user.dir") + "/ObsTraData.txt");
            write(rawDataList, obsTraDataFile);
        }
    }

    /**
     * get the noise trajectory data
     */
    public void getNoiseData() {
        File traDataFile = new File(System.getProperty("user.dir") + "/TrajectoryData.txt");
        Scanner in = null;
        try {
            in = new Scanner(traDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        System.out.println("generating noise data...");
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

            boolean isFirst = false;

            if (!personInfo[personID].getVis()) {
                personInfo[personID].setFirstObsTime(curTime);
                personInfo[personID].setVis(true);
                isFirst = true;
            }

            //System.out.println(curTime);
            if (!isObserve(curTime, personID)) continue;
            personInfo[personID].changeObsTime();
            System.out.println(curTime);
            writeRawData(new String(curTime + " " + personID + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + " 0\n"), 1);

            String record = null;

            if (!isFirst) {
                mFloor = getFloor(personID, mFloor, curTime);
            }
            /*
            if (personInfo[personID].getLastFloorDrift()) {
                if (r.nextDouble() < floorDriftProb1) {
                    personInfo[personID].setLastFloorDrift(true);
                    mFloor = floorDrift(personID, mFloor);
                }
                else {
                    personInfo[personID].setLastFloorDrift(false);
                }
            }
            else {
                if (r.nextDouble() < floorDriftProb2) {
                    personInfo[personID].setLastFloorDrift(true);
                    mFloor = floorDrift(personID, mFloor);
                }
                else {
                    personInfo[personID].setLastFloorDrift(false);
                }
            }
            */
            if (!isFirst && r.nextDouble() < outlierProb) {
                int isOutlier = 1;
                record = getOutlier(curTime, personID, x, y, mFloor, isStay, isOutlier);
            }
            else {
                int isOutlier = 0;
                record = wave(curTime, personID, x, y, mFloor, isStay, isOutlier);
            }

            writeNoiseData(record, 1);
        }

        writeRawData("", 0);
        writeNoiseData("", 0);
    }

    /**
     * 
     * @param curTime
     * @param personID
     * @param x
     * @param y
     * @param mFloor
     * @return
     */
    public String getOutlier(int curTime, int personID, double x, double y, int mFloor, int isStay, int isOutlier) {

        Random r = new Random();

        int parID;
        double newx = x, newy = y;
        while (true) {
            double theta = Math.acos(-1.0) * 2.0 * r.nextDouble();
            double len = outlierBot + (outlierTop - outlierBot) * r.nextDouble();

            if (r.nextDouble() < 0.5) newx = x + len * Math.cos(theta);
            else newx = x - len * Math.cos(theta);

            if (r.nextDouble() < 0.5) newy = y + len * Math.sin(theta);
            else newy = y - len * Math.sin(theta);

            Par par = new Point(newx, newy, mFloor).getCurrentPar();

            if (par != null) {
                parID = par.getmID();
                break;
            }
        }

        return new String(curTime + " " + personID + " " + newx + " " + newy + " " + mFloor + " " + parID + " " + isStay + " " + isOutlier + "\n");
    }

    public static void main(String arge[]) {
        NoiseDataGen noiseDataGen = new NoiseDataGen();
        noiseDataGen.init();
        noiseDataGen.getNoiseData();
    }
}
