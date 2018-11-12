package dk.aau.cs.idq.datagen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Scanner;

import org.khelekore.prtree.PRTree;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.indoorentities.SampledPoint;
import dk.aau.cs.idq.ptree.SampledPointConverter;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.OTTGenConstant;

/**
 * Generate the OTT for a given current time
 *
 * @author lihuan
 * @version 0.1 / 2014.09.06
 */
public class OTTGen {


    public String idrObjDir = System.getProperty("user.dir")
            + "/IdrObj";

    public List<List<SampledPoint>> samplesPointsInFloors = new ArrayList<>();


    public OTTGen() {

        for (int i = 0; i < DataGenConstant.nFloor; i++) {
            List<SampledPoint> samplesPointsInOneFloor = new ArrayList<>();
            samplesPointsInFloors.add(samplesPointsInOneFloor);
        }

    }


    /**
     * generate the OTT for a given current time
     *
     * @param curTime the current time
     */
    public void genOTTbyTime(int curTime, int flag) {
        // System.out.println(curTime);


        idrObjDir = idrObjDir + "_" + curTime + ".txt";
        File dir = new File(idrObjDir);
        if (dir.exists()) {

            try {

                FileReader frTimeTable = new FileReader(DataGen.outputPath + "/timetable_" + curTime + ".txt");
                BufferedReader brTimeTable = new BufferedReader(frTimeTable);
                String readOneLine;
                while ((readOneLine = brTimeTable.readLine()) != null) {
                    String[] items = readOneLine.split("\t");
                    IndoorSpace.OTT.put(Integer.valueOf(items[0]), Integer.valueOf(items[1]));
                }
                frTimeTable.close();
                brTimeTable.close();

                FileReader frIdrObj = new FileReader(idrObjDir);
                BufferedReader brIdrObj = new BufferedReader(frIdrObj);
                while ((readOneLine = brIdrObj.readLine()) != null) {
                    IdrObj newObj = IdrObj.parse(readOneLine);
                    IndoorSpace.observedObjs.put(newObj.getmID(), newObj);
                    if (flag == 1) {
                        newObj.getFloorPointSampledProb();
                    }
                }
                frIdrObj.close();
                brIdrObj.close();

                if (flag == 0) {
                    FileReader frSampledPoint = new FileReader(DataGen.outputPath + "/sampledpoint_" + curTime + ".txt");
                    BufferedReader brSampledPoint = new BufferedReader(frSampledPoint);
                    while ((readOneLine = brSampledPoint.readLine()) != null) {
                        SampledPoint sp = SampledPoint.parse(readOneLine);
                        IndoorSpace.gSampledPoints.add(sp);
                        samplesPointsInFloors.get(sp.getmFloor()).add(sp);
                    }
                    frSampledPoint.close();
                    brSampledPoint.close();
                } else {
                    FileWriter fwSampledPoint = new FileWriter(DataGen.outputPath + "/sampledpoint_" + curTime + ".txt");

                    for (SampledPoint sp : IndoorSpace.gSampledPoints) {
                        fwSampledPoint.write(sp.toString() + "\n");
                        samplesPointsInFloors.get(sp.getmFloor()).add(sp);

                    }
                    fwSampledPoint.flush();
                    fwSampledPoint.close();
                }

            } catch (NumberFormatException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }


        } else {
            int curSizeObjs = IndoorSpace.sizeObjsTable.get(curTime);
            Random random = new Random();
            List<String> findStringLists = new ArrayList<>();
            for (int index = 0; index < curSizeObjs; index++) {
                int pickTime = curTime
                        - random.nextInt(OTTGenConstant.minSamplingPeriod);
                if (pickTime >= 0) {
                    findStringLists.add(new String(pickTime + "#" + index));
                }
            }
            getRecord(curTime, findStringLists);
        }

        for (int i = 0; i < DataGenConstant.nFloor; i++) {
            PRTree<SampledPoint> prt = new PRTree<>(new SampledPointConverter(), DataGenConstant.RTree_BranchFactor);
            prt.load(samplesPointsInFloors.get(i));
            //System.out.println(i +"-th floor has objects :" + prt.getNumberOfLeaves());
            IndoorSpace.gSPRTree.add(prt);
        }
    }

    /**
     * read and retrieve useful records for a given current time and a given
     * findStringLists
     *
     * @param curTime         the current time
     * @param findStringLists which time for which object
     */
    private void getRecord(int curTime, List<String> findStringLists) {
        // TODO Auto-generated method stub
        try {
            FileReader frT = new FileReader(DataGen.outputPath
                    + "/Trajectories.txt");
            BufferedReader brT = new BufferedReader(frT);
            FileWriter fwOTT = new FileWriter(DataGen.outputPath + "/OTT_"
                    + curTime + ".txt");
            FileWriter fwIdrObj = new FileWriter(idrObjDir);
            FileWriter fwTimeRecord = new FileWriter(DataGen.outputPath + "/timetable_" + curTime + ".txt");
            FileWriter fwSampledPoint = new FileWriter(DataGen.outputPath + "/sampledpoint_" + curTime + ".txt");
            String readOneLine;
            while ((readOneLine = brT.readLine()) != null) {
                String[] items = readOneLine.split("\t");
                if (Integer.valueOf(items[0])
                        + OTTGenConstant.minSamplingPeriod > curTime) {
                    if (findStringLists.contains(items[0] + "#" + items[1])) {
                        // fwOTT.write(items[1] + "\t" + items[5] + "\t" +
                        // items[0] + "\n");
                        if ((new String("#")).equals(items[2])) {

                            int recordTime = Integer.valueOf(items[0]);
                            int mID = Integer.valueOf(items[1]);
                            double x = Double.valueOf(items[3]);
                            double y = Double.valueOf(items[4]);
                            int mFloor = Integer.valueOf(items[5]);
                            int curParID = Integer.valueOf(items[6]);
                            IdrObj newObj = new IdrObj(mID, new Point(x, y,
                                    mFloor),
                                    IndoorSpace.gPartitions.get(curParID));
                            IndoorSpace.observedObjs.put(mID, newObj);
                            IndoorSpace.OTT.put(mID, recordTime);
                            fwOTT.write(items[1] + "\t" + items[0] + "\t"
                                    + newObj.getCurrentUncertainRecord() + "\n");
                            fwIdrObj.write(newObj.toString() + "\n");
                        }
                    }

                    if (Integer.valueOf(items[0]) > curTime)
                        break;
                }

            }
            fwOTT.flush();
            fwOTT.close();
            fwIdrObj.flush();
            fwIdrObj.close();

            for (Entry<Integer, Integer> entry : IndoorSpace.OTT.entrySet()) {
                fwTimeRecord.write(entry.getKey() + "\t" + entry.getValue() + "\n");
            }
            fwTimeRecord.flush();
            fwTimeRecord.close();

            for (SampledPoint sp : IndoorSpace.gSampledPoints) {

                fwSampledPoint.write(sp.toString() + "\n");
                samplesPointsInFloors.get(sp.getmFloor()).add(sp);

            }
            fwSampledPoint.flush();
            fwSampledPoint.close();

            brT.close();
            frT.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * load Objects Size Table
     */
    private static void loadSizeObjsTable() {
        // TODO Auto-generated method stub
        try {
            FileReader frSOT = new FileReader(DataGen.outputPath
                    + "/sizeObjsTable.txt");
            BufferedReader brSOT = new BufferedReader(frSOT);
            String readOneLine;
            while ((readOneLine = brSOT.readLine()) != null) {
                String[] items = readOneLine.split(" ");
                IndoorSpace.sizeObjsTable.put(Integer.parseInt(items[0]),
                        Integer.parseInt(items[1]));
            }
            brSOT.close();
            frSOT.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * to generate the OTT
     */
    @SuppressWarnings("resource")
    public int generateOTT(int cTime, int flag) {
        // TODO Auto-generated method stub

        loadSizeObjsTable();


        DataGen dataGen = new DataGen();
        dataGen.genIndoorSpace();
        dataGen.initRTree();
        dataGen.saveDP();
        dataGen.duplicateIndoorSpace(DataGenConstant.nFloor);
        for (Door curDoor : IndoorSpace.gDoors) {
            curDoor.genLeaveablePar();
        }
        int curTime;

        if (cTime == -1) {
            System.out
                    .println("please input an integer number as current time ( range[ 0, "
                            + DataGenConstant.totalLifecycle + " ) )");
            curTime = new Scanner(System.in).nextInt();
        } else {
            curTime = cTime;
        }
        System.out.println("to get the OTT for current time: " + curTime);

        dataGen.loadD2DMatrix();

        OTTGen ottGen = new OTTGen();
        if (curTime >= DataGenConstant.totalLifecycle) {
            System.out.println("Wrong Parameter!");
            return -1;
        } else {
            ottGen.genOTTbyTime(curTime, flag);

            System.out.println("OTT DONE!");
            int t_min = curTime - OTTGenConstant.minSamplingPeriod + 1;
            if (t_min > 0) {
                return t_min;
            } else
                return 0;


        }


    }

}
