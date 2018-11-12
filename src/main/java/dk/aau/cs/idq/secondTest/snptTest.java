package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.semantics.Snpt;
import dk.aau.cs.idq.semantics.Splitting;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class snptTest {

    public double overLapLimit = 0.6;
    public double hit = 0;
    public double cnt = 0;
    public double all = 0;
    public double res1 = 0;
    public double res2 = 0;
    public double res3 = 0;
    public double snptscnt = 0;

    public int getOverlapTime(int startTime1, int endTime1, int startTime2, int endTime2) {

        if (startTime1 > endTime2) return 0;
        if (endTime1 < startTime2) return 0;

        //if (startTime1 < startTime2 && endTime1 > endTime2) return endTime2 - startTime2 + 1;
        //if (startTime1 > startTime2 && endTime1 < endTime2) return endTime1 - startTime1 + 1;

        return Math.min(endTime1, endTime2) - Math.max(startTime1, startTime2) + 1;

    }

    public void solve(ArrayList<Snpt> snpts, ArrayList<Integer> label, ArrayList<Msemantic> msemantics, ArrayList<Record> records) {

        int hitArray[] = new int[msemantics.size()];

        for (int i = 0; i < msemantics.size(); i++) {
            hitArray[i] = 0;
        }

        for (int i = 0; i < snpts.size(); i++) {
            //System.out.println(i + " " + label.size() + " " + snpts.size());
            if (label.get(i) == 1) {
                for (int j = 0; j < msemantics.size(); j++) {
                    if (msemantics.get(j).getIsStay() == 1) {

                        int duration = msemantics.get(j).getDuration();

                        int startTime1 = msemantics.get(j).getStartTime();
                        int endTime1 = msemantics.get(j).getEndTime();

                        int startTime2 = snpts.get(i).getStartTime();
                        int endTime2 = snpts.get(i).getEndTime();

                        int tmp = getOverlapTime(startTime1, endTime1, startTime2, endTime2);

                        if ((double)tmp / duration > overLapLimit) {
                            hit += 1.0;


                            int parID1 = getParID1(snpts.get(i), records);
                            int parID2 = getParID2(snpts.get(i), records);
                            int parID3 = getParID3(snpts.get(i), records);

                            all += 1;

                            if (parID1 == msemantics.get(j).getParID()) {
                                res1 += 1.0;
                            }

                            if (parID2 == msemantics.get(j).getParID()) {
                                res2 += 1.0;
                            }

                            if (parID3 == msemantics.get(j).getParID()) {
                                res3 += 1.0;
                            }
                        }
                    }
                }

                snptscnt += 1.0;
            }
        }

        for (int i = 0; i < msemantics.size(); i++) {
            if (msemantics.get(i).getIsStay() == 1) {
                /*
                if (hitArray[i] / (double)msemantics.get(i).getDuration() > overLapLimit) {
                    hit += 1.0;
                }
                */
                cnt += 1.0;
            }
        }
    }

    public List<Msemantic> getPassByMSem(int personID, Snpt snpt, ArrayList<Record> records) {

        List<Msemantic> ans = new LinkedList<Msemantic>();

        List<Record> tmp = new LinkedList<>();
        int parID = -1;
        int startTime = 0;
        int endTime = 0;

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {
            if (parID == -1) {
                parID = records.get(i).getParID();
                startTime = records.get(i).getTime();
                endTime = records.get(i).getTime();
            }
            else if (parID == records.get(i).getParID()) {
                endTime = records.get(i).getTime();
            }
            else {
                Msemantic msemantic = new Msemantic(personID, startTime, endTime, 0, parID);
                ans.add(msemantic);

                parID = records.get(i).getParID();
                startTime = endTime = records.get(i).getTime();
            }
        }

        Msemantic msemantic = new Msemantic(personID, startTime, endTime, 0, parID);
        ans.add(msemantic);

        return ans;
    }

    public void writePreSem(int personID, ArrayList<Record> records, ArrayList<Integer> label, ArrayList<Snpt> snpts, File file) {

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Msemantic> ans = new LinkedList<Msemantic>();

        for (int i = 0; i < snpts.size(); i++) {
            if (label.get(i) == 1) {
                int parID = getParID1(snpts.get(i), records);
                int startTime = snpts.get(i).getStartTime();
                int endTime = snpts.get(i).getEndTime();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, 1, parID);

                ans.add(msemantic);
            }
            else {

                List<Msemantic> tmp = getPassByMSem(personID, snpts.get(i), records);

                for (int j = 0; j < tmp.size(); j++) {
                    ans.add(tmp.get(j));
                }
            }
        }

        for (int i = 0; i < ans.size(); i++) {
            try {
                fw.write(ans.get(i).toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void test() {

        for (int i = LRmodel.boundary; i < TraDataGen.totalPerson; i++) {
            File labelfile = new File(System.getProperty("user.dir") + "/predictLabel/person" + i + ".txt");
            File snptfile = new File(System.getProperty("user.dir") + "/Snippet/person" + i + ".txt");
            //File msemanticsfile = new File(System.getProperty("user.dir") + "/Msemantics/person" + i + ".txt");

            File recordsFile = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");
            File predictSemanticsFile = new File(System.getProperty("user.dir") + "/predictSemantics/person" + i + ".txt");

            ArrayList<Record> records = ReadRecord.getRecord(recordsFile);

            ArrayList<Integer> label = new ArrayList<Integer>();

            Scanner in = null;

            try {
                in = new Scanner(labelfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(in.hasNext()) {
                int tmp = in.nextInt();
                label.add(tmp);
            }

            ArrayList<Snpt> snpts = new ArrayList<Snpt>();

            try {
                in = new Scanner(snptfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(in.hasNext()) {
                int startID = in.nextInt();
                int endID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isDense = in.nextInt();

                Snpt snpt = new Snpt(startID, endID, startTime, endTime, isDense == 1 ? true : false);

                snpts.add(snpt);
            }

            writePreSem(i, records, label, snpts, predictSemanticsFile);
            System.out.println(i);
/*
            try {
                in = new Scanner(msemanticsfile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> msemantics = new ArrayList<Msemantic>();

            while(in.hasNext()) {
                int personID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isStay = in.nextInt();
                int parID = in.nextInt();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

                msemantics.add(msemantic);
            }

            solve(snpts, label, msemantics, records);
            System.out.println(i);
*/
        }
    }

    public int getParID1(Snpt snpt, ArrayList<Record> records) {

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {

            int parID = records.get(i).getParID();
            int cnt = 0;
            if (map.get(parID) != null) {
                cnt = map.get(parID) + 1;
            }
            else {
                cnt = 1;
            }

            map.put(parID, cnt);
        }

        if (map.size() == 0) {
            System.out.println("error!!!!!");
        }

        int parID = 0, mx = 0;

        for (Map.Entry<Integer, Integer> entry : map.entrySet()) {
            if (entry.getValue() > mx) {
                mx = entry.getValue();
                parID = entry.getKey();
            }
        }

        return parID;
    }

    public int getParID2(Snpt snpt, ArrayList<Record> records) {

        double x = 0, y = 0;
        int cnt = 0;
        int floor[] = new int[10];

        for (int i = 0; i < 10; i++) {
            floor[i] = 0;
        }

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {
            x += records.get(i).getX();
            y += records.get(i).getY();
            floor[records.get(i).getFloor()]++;
            cnt++;
        }

        x /= cnt;
        y /= cnt;

        int ansFloor = 0, mx = 0;

        for (int i = 0; i < 10; i++) {
            if (floor[i] > mx) {
                mx = floor[i];
                ansFloor = i;
            }
        }

        Point point = new Point(x, y, ansFloor);

        if (point.getCurrentPar() == null) System.out.println(point.getX() + " " + point.getY());

        if (point.getCurrentPar() == null) return -1;
        else return point.getCurrentPar().getmID();
    }

    public List<Record> getNeighbor(int id, ArrayList<Record> records) {

        List<Record> ans = new LinkedList<Record>();

        double dis1 = 1000000;
        double dis2 = 1000000;
        double dis3 = 1000000;

        int ans1 = -1;
        int ans2 = -1;
        int ans3 = -1;

        for (int i = 0; i < records.size(); i++) {
            if (i != id) {
                Point s = new Point(records.get(id).getX(), records.get(id).getY(), records.get(id).getFloor());
                Point t = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());

                double dis = Splitting.euclidDis(s, t);

                if (dis < dis1) {
                    dis3 = dis2;
                    ans3 = ans2;

                    dis2 = dis1;
                    ans2 = ans1;

                    dis1 = dis;
                    ans1 = i;
                }
                else if (dis < dis2) {
                    dis3 = dis2;
                    ans3 = ans2;

                    dis2 = dis;
                    ans2 = i;
                }
                else if (dis < dis3){
                    dis3 = dis;
                    ans3 = i;
                }
            }
        }

        if (ans1 != -1) ans.add(records.get(ans1));
        if (ans2 != -1) ans.add(records.get(ans2));
        if (ans3 != -1) ans.add(records.get(ans3));

        return ans;
    }

    public int getParID3(Snpt snpt, ArrayList<Record> records) {

        double conf[] = new double[snpt.getEndID() - snpt.getStartID() + 1];
        double sum = 0;

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {

            List<Record> N = getNeighbor(i, records);

            double dis = 0;

            for (int j = 0; j < N.size(); j++) {
                Point s = new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor());
                Point t = new Point(records.get(j).getX(), records.get(j).getY(), records.get(j).getFloor());
                dis += Splitting.euclidDis(s, t);
            }

            conf[i - snpt.getStartID()] = N.size() / dis;
            sum += conf[i - snpt.getStartID()];
        }

        double x = 0, y = 0;

        int floor[] = new int[10];

        for (int i = 0; i < 10; i++) {
            floor[i] = 0;
        }

        for (int i = snpt.getStartID(); i <= snpt.getEndID(); i++) {
            double weight = conf[i - snpt.getStartID()] / sum;

            x += weight * records.get(i).getX();
            y += weight * records.get(i).getY();
            floor[records.get(i).getFloor()]++;
        }

        int ansFloor = 0, mx = 0;

        for (int i = 0; i < 10; i++) {
            if (floor[i] > mx) {
                mx = floor[i];
                ansFloor = i;
            }
        }

        Point point = new Point(x, y, ansFloor);

        if (point.getCurrentPar() == null) return -1;
        else return point.getCurrentPar().getmID();
    }

    public void init() {

        File dirFile = new File(System.getProperty("user.dir") + "/predictSemantics");
        dirFile.mkdir();

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
    }

    public ArrayList<Msemantic> solvePassBy(ArrayList<Msemantic> passby) {
        ArrayList<Msemantic> ans = new ArrayList<>();

        for (int i = 0; i < passby.size(); i++) {
            int j = i;
            while(j+1 < passby.size() && passby.get(j+1).getParID() == passby.get(i).getParID()) {
                j++;
            }

            int personID = passby.get(i).getPersonID();
            int startTime = passby.get(i).getStartTime();
            int endTime = passby.get(j).getEndTime();
            int isStay = 0;
            int parID = passby.get(i).getParID();
            i = j;

            Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);
            ans.add(msemantic);
        }

        return ans;
    }

    public ArrayList<Msemantic> solveStay(ArrayList<Msemantic> oriMse) {
        ArrayList<Msemantic> ans = new ArrayList<>();
        ArrayList<Msemantic> tmp = new ArrayList<>();

        for (int i = 0; i < oriMse.size(); i++) {

            if (oriMse.get(i).getIsStay() == 1) {
                tmp.add(oriMse.get(i));
            }
            else if (tmp.size() != 0) {

                int personID = tmp.get(0).getPersonID();
                int startTime = tmp.get(0).getStartTime();
                int endTime = tmp.get(tmp.size() - 1).getEndTime();
                int isStay = tmp.get(0).getIsStay();
                int parID = tmp.get(0).getParID();

                Msemantic newMse = new Msemantic(personID, startTime, endTime, isStay, parID);
                tmp.clear();
                ans.add(newMse);
                ans.add(oriMse.get(i));
            }
            else {
                ans.add(oriMse.get(i));
            }
        }

        if (tmp.size() != 0) {
            int personID = tmp.get(0).getPersonID();
            int startTime = tmp.get(0).getStartTime();
            int endTime = tmp.get(tmp.size() - 1).getEndTime();
            int isStay = tmp.get(0).getIsStay();
            int parID = tmp.get(0).getParID();

            Msemantic newMse = new Msemantic(personID, startTime, endTime, isStay, parID);
            tmp.clear();
            ans.add(newMse);
        }

        return ans;
    }

    public void mergeStay() {
        File dirFile = new File(System.getProperty("user.dir") + "/tempSemantics");

        dirFile.mkdir();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {
            //System.out.println("merge " + i);

            File file = new File(System.getProperty("user.dir") + "/tmpSemantics/person" + i + ".txt");
            File fwFile = new File(System.getProperty("user.dir") + "/tempSemantics/person" + i + ".txt");

            Scanner in = null;

            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> msemantics = new ArrayList<>();

            while(in.hasNext()) {
                int personID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isStay = in.nextInt();
                int parID = in.nextInt();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

                msemantics.add(msemantic);
            }

            FileWriter fw = null;

            try {
                fw = new FileWriter(fwFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> ans = solveStay(msemantics);

            for (int j = 0; j < ans.size(); j++) {
                try {
                    fw.write(ans.get(j).toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void mergePassBy() {

        File dirFile = new File(System.getProperty("user.dir") + "/tmpSemantics");

        dirFile.mkdir();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {
            //System.out.println("merge " + i);

            File file = new File(System.getProperty("user.dir") + "/predictSemantics/person" + i + ".txt");
            File fwFile = new File(System.getProperty("user.dir") + "/tmpSemantics/person" + i + ".txt");

            Scanner in = null;

            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> msemantics = new ArrayList<>();

            while(in.hasNext()) {
                int personID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isStay = in.nextInt();
                int parID = in.nextInt();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

                msemantics.add(msemantic);
            }

            FileWriter fw = null;

            try {
                fw = new FileWriter(fwFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> passby = new ArrayList<>();
            for (int j = 0; j < msemantics.size(); j++) {
                if (msemantics.get(j).getIsStay() == 1) {
                    ArrayList<Msemantic> nowPassBy = solvePassBy(passby);
                    for (int k = 0; k < nowPassBy.size(); k++) {
                        try {
                            fw.write(nowPassBy.get(k).toString() + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    passby.clear();

                    try {
                        fw.write(msemantics.get(j).toString() + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    passby.add(msemantics.get(j));
                }
            }
            ArrayList<Msemantic> nowPassBy = solvePassBy(passby);
            for (int j = 0; j < nowPassBy.size(); j++) {
                try {
                    fw.write(nowPassBy.get(j).toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public double spaceDis(double x1, double y1, double x2, double y2) {
        double ans = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        return Math.sqrt(ans);
    }

    public double GRD(Par s, Par t) {
        double ans = -1;

        if (s.getmID() == t.getmID()) {
            return -1;
        }

        ArrayList<Par> pars = ReadPar.getPar();
        ArrayList<Door> doors = ReadDoor.getDoor();

        for (Integer sID : s.getmDoors()) {
            for (Integer tID : t.getmDoors()) {
                if (sID == tID) {

                    double x = doors.get(sID).getX();
                    double y = doors.get(sID).getY();

                    double x1 = pars.get(sID).getX1();
                    double y1 = pars.get(sID).getY1();
                    double x2 = pars.get(sID).getX2();
                    double y2 = pars.get(tID).getY2();

                    ans = Math.max(ans, spaceDis(x, y, x1, y1));
                    ans = Math.max(ans, spaceDis(x, y, x1, y2));
                    ans = Math.max(ans, spaceDis(x, y, x2, y1));
                    ans = Math.max(ans, spaceDis(x, y, x2, y2));
                }
            }
        }

        return ans;
    }

    public void mergeConnection() {

        File dirFile = new File(System.getProperty("user.dir") + "/nowSemantics");

        dirFile.mkdir();

        ArrayList<Par> pars = ReadPar.getPar();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {
            //System.out.println("merge " + i);

            File file = new File(System.getProperty("user.dir") + "/ourSemantics/person" + i + ".txt");
            File fwFile = new File(System.getProperty("user.dir") + "/nowSemantics/person" + i + ".txt");

            Scanner in = null;

            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> msemantics = new ArrayList<>();

            while(in.hasNext()) {
                int personID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isStay = in.nextInt();
                int parID = in.nextInt();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

                msemantics.add(msemantic);
            }

            FileWriter fw = null;

            try {
                fw = new FileWriter(fwFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int lastTime = 0;
            for (int j = 0; j < msemantics.size(); j++) {
                Msemantic msemantic = null;
                if (j == msemantics.size() - 1) {
                    int personID = msemantics.get(j).getPersonID();
                    int startTime = msemantics.get(j).getStartTime();
                    int endTime = msemantics.get(j).getEndTime();
                    int isStay = msemantics.get(j).getIsStay();
                    int parID = msemantics.get(j).getParID();

                    startTime = startTime - lastTime;

                    msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);
                }
                else {
                    int personID = msemantics.get(j).getPersonID();
                    int startTime = msemantics.get(j).getStartTime();
                    int endTime = msemantics.get(j).getEndTime();
                    int isStay = msemantics.get(j).getIsStay();
                    int parID = msemantics.get(j).getParID();

                    int startTime2 = msemantics.get(j+1).getStartTime();
                    int parID2 = msemantics.get(j+1).getParID();

                    Par par1 = pars.get(parID);
                    Par par2 = pars.get(parID2);

                    double dis = GRD(par1, par2);
                    double dis2 = GRD(par2, par1);
                    double ratio = 0.5;

                    if (dis <= 0 || dis2 <= 0) {
                        ratio = 0.5;
                    }
                    else {
                        ratio = dis / (dis + dis2);
                    }

                    //System.out.println(dis + " " + dis2);
                    int nowTime = (int)((startTime2 - endTime - 1) * ratio + 0.5);

                    startTime = startTime - lastTime;
                    lastTime = startTime2 - endTime - 1 - nowTime;

                    endTime = endTime + nowTime;
                    msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);
                }

                try {
                    fw.write(msemantic.toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    boolean checkNotConnection(Msemantic s, Msemantic t) {

        int sParID = s.getParID();
        int tParID = t.getParID();

        if (s.getParID() / 141 != t.getParID() / 141) {
             return false;
        }

        ArrayList<Par> pars = ReadPar.getPar();

        Par sPar = pars.get(sParID);
        Par tPar = pars.get(tParID);

        //System.out.print(sPar.getmDoors());
        //System.out.print(tPar.getmDoors());
        for (Integer sDoorID : sPar.getmDoors()) {
            for (Integer tDoorID : tPar.getmDoors()) {
                if ((int)sDoorID == (int)tDoorID) {
                    return false;
                }
            }
        }

        return true;
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

    List<Msemantic> findPath(Msemantic s, Msemantic t) {

        List<Msemantic> ans = new LinkedList<>();

        int sParID = s.getParID();
        int tParID = t.getParID();

        ArrayList<Par> pars = ReadPar.getPar();

        Par sPar = pars.get(sParID);
        Par tPar = pars.get(tParID);

        Point sPoint = findCentroid(sPar);
        Point tPoint = findCentroid(tPar);

        IdrObj sIdr = new IdrObj(0, sPoint);
        IdrObj tIdr = new IdrObj(0, tPoint);

        List<Pair> path = new LinkedList<>();

        PointToPoint.move(sIdr, tIdr, path);

        System.out.println(sPar.getmID());
        System.out.println(tPar.getmID());

        System.out.println(path);

        System.out.println(path.size());

        int sTime = s.getEndTime() + 1;
        int eTime = t.getStartTime() - 1;

        int cnt = path.size() - 1;

        if (cnt <= 1) return ans;
        int interval = (eTime - sTime + 1) / cnt;
        for (int i = 0; i < path.size() - 1; i++) {
            Pair pair = path.get(i);

            int startTime = 0;
            int endTime = 0;

            if (i == path.size() - 2) {
                startTime = sTime;
                endTime = eTime;
            }
            else {
                startTime = sTime;
                endTime = sTime + interval;
                sTime += interval + 1;
            }

            int parID = pair.getParID();

            int personID = s.getPersonID();
            int isStay = 0;

            Msemantic newMse = new Msemantic(personID, startTime, endTime, isStay, parID);

            ans.add(newMse);
        }

        return ans;
    }

    public ArrayList<Msemantic> solveCompletion(ArrayList<Msemantic> msemantics) {

        ArrayList<Msemantic> ans = new ArrayList<>();

        ans.add(msemantics.get(0));

        for (int i = 1; i < msemantics.size(); i++) {
            if (checkNotConnection(msemantics.get(i-1), msemantics.get(i))) {
                List<Msemantic> path = findPath(msemantics.get(i-1), msemantics.get(i));
                for (int j = 0; j < path.size(); j++) {
                    ans.add(path.get(j));
                }
            }

            ans.add(msemantics.get(i));
        }

        return ans;

    }

    public void completion() {
        File dirFile = new File(System.getProperty("user.dir") + "/ourSemantics");

        dirFile.mkdir();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {
            //System.out.println("merge " + i);

            File file = new File(System.getProperty("user.dir") + "/tempSemantics/person" + i + ".txt");
            File fwFile = new File(System.getProperty("user.dir") + "/ourSemantics/person" + i + ".txt");

            Scanner in = null;

            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            ArrayList<Msemantic> msemantics = new ArrayList<>();

            while(in.hasNext()) {
                int personID = in.nextInt();
                int startTime = in.nextInt();
                int endTime = in.nextInt();
                int isStay = in.nextInt();
                int parID = in.nextInt();

                Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

                msemantics.add(msemantic);
            }

            FileWriter fw = null;

            try {
                fw = new FileWriter(fwFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            System.out.println("PPPPPPPP " + msemantics.size());
            ArrayList<Msemantic> ans = new ArrayList<>();
            if (msemantics.size() >= 2) {
                ans = solveCompletion(msemantics);
            }
            else {
                ans = msemantics;
            }

            for (int j = 0; j < ans.size(); j++) {
                try {
                    fw.write(ans.get(j).toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String args[]) {
/*
        DataSet dataSet = new DataSet();
        dataSet.init();
        dataSet.createFeature();

        LRmodel lrmodel  = new LRmodel();
        lrmodel.init();
        lrmodel.model();

        evaluate evaluate = new evaluate();
        evaluate.work();

        LRmodel.writeLabel();


        snptTest snptTest = new snptTest();
        snptTest.test();

        System.out.println("cnt = " + snptTest.cnt);
        System.out.println("hit = " + snptTest.hit);
        System.out.println("snptcnt = " + snptTest.snptscnt);

        System.out.println("ratio = "+ snptTest.hit / snptTest.cnt);
*/
/*
        snptTest snptTest = new snptTest();
        snptTest.init();
        snptTest.test();
*/
/*
        snptTest snptTest = new snptTest();
        snptTest.mergePassBy();
*/
/*
        outputToJson outputToJson = new outputToJson();
        outputToJson.output();
*/
/*
        inferenceBasedComplement inferenceBasedComplement = new inferenceBasedComplement();
        inferenceBasedComplement.init();
*/
    }

}
