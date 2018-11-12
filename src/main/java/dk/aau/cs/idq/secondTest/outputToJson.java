package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;
import dk.aau.cs.idq.utilities.ReadRecord;
import dk.aau.cs.idq.utilities.RoomType;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

public class outputToJson {

    /**
     * get records
     * @param curPersonFile
     * @return
     */
    public static ArrayList<Record> getRecord(File curPersonFile) {
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

    /**
     * get the json of floor
     * @param records
     * @param name
     * @param std
     * @return
     */
    public List<JSONObject> getFloorJson(List<Record> records, String name[], int std) {

        List<JSONObject> jsonObjects = new ArrayList<JSONObject>();

        int floor = records.get(0).getFloor();
        name[0] = Integer.toString(floor);

        for (int i = 0; i < records.size(); i++) {
            JSONObject json = new JSONObject();
            json.put("time", records.get(i).getTime() - std);
            json.put("x", records.get(i).getX());
            json.put("y", records.get(i).getY());
            jsonObjects.add(json);
        }

        return jsonObjects;
    }

    /**
     * get raw trace
     * @param records
     * @param startTime
     * @return
     */
    public List<JSONObject> getRawTrace(ArrayList<Record> records, int startTime) {
        List<JSONObject> rawTrace = new LinkedList<JSONObject>();

        List<Record> floorRecords = new LinkedList<Record>();
        int preFloor = -1;
        for (int j = 0; j < records.size(); j++) {
            if (records.get(j).getFloor() != preFloor) {
                if (floorRecords.size() != 0) {
                    String name[] = new String[1];
                    List<JSONObject> jsonObjects = getFloorJson(floorRecords, name, startTime);
                    JSONObject jsonTmp = new JSONObject();
                    jsonTmp.put("floor", name[0]);
                    jsonTmp.put("data", jsonObjects);
                    rawTrace.add(jsonTmp);
                    floorRecords.clear();
                }
                preFloor = records.get(j).getFloor();
                floorRecords.add(records.get(j));
            }
            else {
                floorRecords.add(records.get(j));
            }
        }
        if (floorRecords.size() != 0) {
            String name[] = new String[1];
            List<JSONObject> jsonObjects = getFloorJson(floorRecords, name, startTime);
            JSONObject jsonTmp = new JSONObject();
            jsonTmp.put("floor", name[0]);
            jsonTmp.put("data", jsonObjects);
            rawTrace.add(jsonTmp);
            floorRecords.clear();
        }

        return rawTrace;
    }

    public Point findCentroid(Par par) {

        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        double x = (x1 + x2) / 2;
        double y = (y1 + y2) / 2;

        int floor = par.getmFloor();

        return new Point(x, y, floor);
    }

    /**
     * get the id of door
     * @param a
     * @param b
     * @return
     */
    public int getDoorID(Msemantic a, Msemantic b) {

        ArrayList<Par> pars = ReadPar.getPar();

        int sParID = a.getParID();
        int tParID = b.getParID();

        Par s = pars.get(sParID);
        Par t = pars.get(tParID);

        for (Integer sDoorID : s.getmDoors()) {
            for (Integer tDoorID : t.getmDoors()) {
                if ((int)sDoorID == (int)tDoorID) {
                    return sDoorID;
                }
            }
        }

        return -1;
    }

    /**
     *
     * @param msem
     * @param records
     * @param msems
     * @param nowID
     * @return
     */
    public double getCentroidX(Msemantic msem, ArrayList<Record> records, List<Msemantic> msems, int nowID) {

        System.out.println("PPP " + records.size() + " PPP " + msems.size());


        JSONObject ans = new JSONObject();

        double cnt = 0, x = 0, y = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() >= msem.getStartTime() && records.get(i).getTime() <= msem.getEndTime() && msem.getParID() == records.get(i).getParID()) {
                x += records.get(i).getX();
                y += records.get(i).getY();
                cnt += 1.0;
            }
        }

        if (cnt == 0) {
            //ArrayList<Par> pars = ReadPar.getPar();
            //System.out.println("AAAAAA " + msem.getParID());

            int doorID1 = getDoorID(msem, msems.get(nowID - 1));
            int doorID2 = getDoorID(msem, msems.get(nowID + 1));

            ArrayList<Door> doors = ReadDoor.getDoor();

            x = (doors.get(doorID1).getX() + doors.get(doorID2).getX()) / 2.0;
            y = (doors.get(doorID1).getY() + doors.get(doorID2).getY()) / 2.0;

            return x;
        }

        x /= cnt;
        y /= cnt;

        ans.put("x", x);
        ans.put("y", y);

        return x;
    }

    /**
     *
     * @param msem
     * @param records
     * @param msems
     * @param nowID
     * @return
     */
    public double getCentroidY(Msemantic msem, ArrayList<Record> records, List<Msemantic> msems, int nowID) {

        JSONObject ans = new JSONObject();

        double cnt = 0, x = 0, y = 0;
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).getTime() >= msem.getStartTime() && records.get(i).getTime() <= msem.getEndTime() && msem.getParID() == records.get(i).getParID()) {
                x += records.get(i).getX();
                y += records.get(i).getY();
                cnt += 1.0;
            }
        }

        if (cnt == 0) {
            //ArrayList<Par> pars = ReadPar.getPar();
            //System.out.println("AAAAAA " + msem.getParID());

            int doorID1 = getDoorID(msem, msems.get(nowID - 1));
            int doorID2 = getDoorID(msem, msems.get(nowID + 1));

            ArrayList<Door> doors = ReadDoor.getDoor();

            x = (doors.get(doorID1).getX() + doors.get(doorID2).getX()) / 2.0;
            y = (doors.get(doorID1).getY() + doors.get(doorID2).getY()) / 2.0;

            return y;
        }

        x /= cnt;
        y /= cnt;

        ans.put("x", x);
        ans.put("y", y);

        return y;
    }

    /**
     *
     * @param tmp
     * @param records
     * @param stdTime
     * @return
     */
    public List<JSONObject> getRes(List<Msemantic> tmp, ArrayList<Record> records, int stdTime) {

        List<JSONObject> ans = new LinkedList<JSONObject>();
        ArrayList<Par> pars = ReadPar.getPar();

        for (int i = 0; i < tmp.size(); i++) {
            int personID = tmp.get(i).getPersonID();
            int startTime = tmp.get(i).getStartTime();
            int endTime = tmp.get(i).getEndTime();
            int isStay = tmp.get(i).getIsStay();
            int parID = tmp.get(i).getParID();
            Par par = pars.get(parID);
            int floor = par.getmFloor();

            JSONObject json = new JSONObject();
            json.put("startTime", startTime - stdTime);
            json.put("endTime", endTime - stdTime);

            String regionName = new String(par.getmFloor() + "F ");

            json.put("roomID", parID);
            json.put("x", getCentroidX(tmp.get(i), records, tmp, i));
            json.put("y", getCentroidY(tmp.get(i), records, tmp, i));

            if (par.getmType() == RoomType.ROOM) {
                regionName = regionName + new String("Room " + parID);
            }
            else if (par.getmType() == RoomType.HALLWAY){
                regionName = regionName + new String("Hallway " + parID);
            }
            else {
                regionName = regionName + new String("Staircase " + parID);
            }

            json.put("regionName", regionName);

            //json.put("floor", floor);
            if (isStay == 1) {
                json.put("event", "stay");
            }
            else {
                json.put("event", "pass-by");
            }

            ans.add(json);
        }

        return ans;
    }

    /**
     *
     * @param personID
     * @param records
     * @param stdTime
     * @return
     */
    public List<JSONObject> getMsemantics(int personID, ArrayList<Record> records, int stdTime) {
        List<JSONObject> ans = new LinkedList<JSONObject>();
        ArrayList<Par> pars = ReadPar.getPar();

        File file = new File(System.getProperty("user.dir") + "/nowSemantics/person" + personID + ".txt");

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Msemantic> tmp = new LinkedList<Msemantic>();
        int preFloor = -1;
        while (in.hasNext()) {
            personID = in.nextInt();
            int startTime = in.nextInt();
            int endTime = in.nextInt();
            int isStay = in.nextInt();
            int parID = in.nextInt();
            Par par = pars.get(parID);
            int floor = par.getmFloor();

            if (floor != preFloor) {
                if (tmp.size() != 0) {
                    List<JSONObject> res = getRes(tmp, records, stdTime);
                    JSONObject tmpJson = new JSONObject();
                    tmpJson.put("floor", preFloor);
                    tmpJson.put("data", res);
                    preFloor = floor;
                    tmp.clear();
                    ans.add(tmpJson);
                }

                preFloor = floor;
                tmp.add(new Msemantic(personID, startTime, endTime, isStay, parID));
            }
            else {
                tmp.add(new Msemantic(personID, startTime, endTime, isStay, parID));
            }
        }
        if (tmp.size() != 0) {
            List<JSONObject> res = getRes(tmp, records, stdTime);
            JSONObject tmpJson = new JSONObject();
            tmpJson.put("floor", preFloor);
            tmpJson.put("data", res);
            tmp.clear();
            ans.add(tmpJson);
        }

        return ans;
    }

    /**
     * generate the json of Trace
     */
    public void output() {

        File dirFile = new File(System.getProperty("user.dir") + "/jsonTrace");
        dirFile.mkdir();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {

            JSONObject json = new JSONObject();

            String file = new String(System.getProperty("user.dir") + "/jsonTrace/person" + i + ".js");

            FileWriter fw = null;
            try {
                fw = new FileWriter(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            File cleanedRawFile = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");

            File rawFile = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

            File groundTruthFile = new File(System.getProperty("user.dir") + "/groundTruth/person" + i + ".txt");

            ArrayList<Record> records = ReadRecord.getRecord(rawFile);

            ArrayList<Record> groundTruthRecords = getRecord(groundTruthFile);

            ArrayList<Record> cleandRawRecords = ReadRecord.getRecord(cleanedRawFile);

            //System.out.println(records.size() + " " + groundTruthRecords.size() + " " + cleandRawRecords.size());

            json.put("objectID", i);

            if (records.size() == 0) continue;

            int startTime = records.get(0).getTime();

            json.put("startTime", startTime);

            json.put("cleanedRawTraces", getRawTrace(cleandRawRecords, startTime));

            json.put("rawTraces", getRawTrace(records, startTime));

            json.put("groundTruthTraces", getRawTrace(groundTruthRecords, startTime));

            json.put("semanticTraces", getMsemantics(i, cleandRawRecords, startTime));


            try {
                fw.write(json.toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println(i);
        }
    }

    public static void main(String args[]) {

        mapJson mapJson = new mapJson();

        mapJson.outputMapJson();

        /*
        outputToJson outputToJson = new outputToJson();
        outputToJson.output();
*/

/*
        mapJson mapJson = new mapJson();
        mapJson.outputMapJson();
        */
    }

}
