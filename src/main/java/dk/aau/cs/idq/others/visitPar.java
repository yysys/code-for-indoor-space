package dk.aau.cs.idq.others;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class visitPar {

    public int encode(int parID1, int parID2) {
        int t1 = Math.min(parID1, parID2);
        int t2 = Math.max(parID1, parID2);
        return t1 * 1410 + t2;
    }

    public int[] recordVisit(File file, List<Integer> pars) {

        List<Par> parList = ReadPar.getPar();

        int cnt[] = new int[parList.size()];
        int per_cnt[] = new int[parList.size()];
        int flag[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            cnt[i] = 0;
            flag[i] = 0;
            per_cnt[i] = 0;
        }

        for (int i = 0; i < pars.size(); i++) {
            flag[pars.get(i)] = 1;
        }

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pre = -1;
        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int curTime = Integer.valueOf(input[1]).intValue();
            double x = Double.valueOf(input[2]).doubleValue();
            double y = Double.valueOf(input[3]).doubleValue();
            int mFloor = Integer.valueOf(input[4]).intValue();
            int parID = Integer.valueOf(input[5]).intValue();
            int isStay = Integer.valueOf(input[6]).intValue();
            int isOutlier = Integer.valueOf(input[7]).intValue();

            if (personID != pre) {
                for (int i = 0; i < parList.size(); i++) {
                    if (per_cnt[i] != 0) {
                        cnt[i]++;
                    }
                    per_cnt[i] = 0;
                }
                pre = personID;
            }

            if (flag[parID] == 1) {
                per_cnt[parID]++;
            }
        }

        return cnt;
    }

    public HashMap<Integer, Integer> recordFPVisit(File file, List<Integer> pars) {

        List<Par> parList = ReadPar.getPar();

        int flag[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            flag[i] = 0;
        }

        for (int i = 0; i < pars.size(); i++) {
            flag[pars.get(i)] = 1;
        }

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> per_map = new HashMap<Integer, Integer>();
        int pre = -1;
        List<Record> batch = new LinkedList<Record>();
        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int curTime = Integer.valueOf(input[1]).intValue();
            double x = Double.valueOf(input[2]).doubleValue();
            double y = Double.valueOf(input[3]).doubleValue();
            int mFloor = Integer.valueOf(input[4]).intValue();
            int parID = Integer.valueOf(input[5]).intValue();
            int isStay = Integer.valueOf(input[6]).intValue();
            int isOutlier = Integer.valueOf(input[7]).intValue();

            Record record = new Record(curTime, x, y, mFloor, parID, isStay, isOutlier);

            if (personID != pre) {
                for (int i = 0; i < batch.size(); i++) {
                    if (flag[batch.get(i).getParID()] == 1) {
                        for (int j = i + 1; j < batch.size(); j++) {
                            if (flag[batch.get(j).getParID()] == 1) {
                                int parID1 = batch.get(i).getParID();
                                int parID2 = batch.get(j).getParID();
                                int fp = encode(parID1, parID2);
                                per_map.put(fp, 1);
                            }
                        }
                    }
                }

                Iterator<Integer> iter = per_map.keySet().iterator();
                while (iter.hasNext()) {
                    int fp = iter.next();
                    int count = 0;
                    if (map.get(fp) != null) {
                        count = map.get(fp) + 1;
                    }
                    else {
                        count = 1;
                    }
                    map.put(fp, count);
                }

                pre = personID;
                per_map.clear();
                batch.clear();
                batch.add(record);
            }
            else {
                batch.add(record);
            }
        }

        if (batch.size() != 0) {
            for (int i = 0; i < batch.size(); i++) {
                if (flag[batch.get(i).getParID()] == 1) {
                    for (int j = i + 1; j < batch.size(); j++) {
                        if (flag[batch.get(j).getParID()] == 1) {
                            int parID1 = batch.get(i).getParID();
                            int parID2 = batch.get(j).getParID();
                            int fp = encode(parID1, parID2);
                            per_map.put(fp, 1);
                        }
                    }
                }
            }

            Iterator<Integer> iter = per_map.keySet().iterator();
            while (iter.hasNext()) {
                int fp = iter.next();
                int count = 0;
                if (map.get(fp) != null) {
                    count = map.get(fp) + 1;
                }
                else {
                    count = 1;
                }
                map.put(fp, count);
            }
        }

        return map;
    }

    public int[] msematicsVisit(File file, List<Integer> pars) {

        List<Par> parList = ReadPar.getPar();

        int cnt[] = new int[parList.size()];
        int per_cnt[] = new int[parList.size()];
        int flag[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            cnt[i] = 0;
            flag[i] = 0;
            per_cnt[i] = 0;
        }

        for (int i = 0; i < pars.size(); i++) {
            flag[pars.get(i)] = 1;
        }

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pre = -1;
        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int startTime = Integer.valueOf(input[1]).intValue();
            int endTime = Integer.valueOf(input[2]).intValue();
            int isStay = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();

            if (personID != pre) {
                for (int i = 0; i < parList.size(); i++) {
                    if (per_cnt[i] != 0) {
                        cnt[i]++;
                    }
                    per_cnt[i] = 0;
                }
                pre = personID;
            }

            if (parID != -1 && flag[parID] == 1) {
                per_cnt[parID]++;
            }
        }

        return cnt;
    }

    public HashMap<Integer, Integer> msematicsFPVisit(File file, List<Integer> pars) {

        List<Par> parList = ReadPar.getPar();

        int flag[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            flag[i] = 0;
        }

        for (int i = 0; i < pars.size(); i++) {
            flag[pars.get(i)] = 1;
        }

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> per_map = new HashMap<Integer, Integer>();
        int pre = -1;
        List<Msemantic> batch = new LinkedList<Msemantic>();

        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int startTime = Integer.valueOf(input[1]).intValue();
            int endTime = Integer.valueOf(input[2]).intValue();
            int isStay = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();

            Msemantic msematics = new Msemantic(personID, startTime, endTime, isStay, parID);

            if (personID != pre) {
                for (int i = 0; i < batch.size(); i++) {
                    if (batch.get(i).getParID() != -1 && flag[batch.get(i).getParID()] == 1) {
                        for (int j = i + 1; j < batch.size(); j++) {
                            if (batch.get(i).getParID() != -1 && flag[batch.get(j).getParID()] == 1) {
                                int parID1 = batch.get(i).getParID();
                                int parID2 = batch.get(j).getParID();
                                int fp = encode(parID1, parID2);
                                per_map.put(fp, 1);
                            }
                        }
                    }
                }

                Iterator<Integer> iter = per_map.keySet().iterator();
                while (iter.hasNext()) {
                    int fp = iter.next();
                    int count = 0;
                    if (map.get(fp) != null) {
                        count = map.get(fp) + 1;
                    }
                    else {
                        count = 1;
                    }
                    map.put(fp, count);
                }

                pre = personID;
                per_map.clear();
                batch.clear();
                batch.add(msematics);
            }
            else {
                batch.add(msematics);
            }
        }
        if (batch.size() != 0) {
            for (int i = 0; i < batch.size(); i++) {
                if (batch.get(i).getParID() != -1 && flag[batch.get(i).getParID()] == 1) {
                    for (int j = i + 1; j < batch.size(); j++) {
                        if (batch.get(i).getParID() != -1 && flag[batch.get(j).getParID()] == 1) {
                            int parID1 = batch.get(i).getParID();
                            int parID2 = batch.get(j).getParID();
                            int fp = encode(parID1, parID2);
                            per_map.put(fp, 1);
                        }
                    }
                }
            }

            Iterator<Integer> iter = per_map.keySet().iterator();
            while (iter.hasNext()) {
                int fp = iter.next();
                int count = 0;
                if (map.get(fp) != null) {
                    count = map.get(fp) + 1;
                }
                else {
                    count = 1;
                }
                map.put(fp, count);
            }
        }

        return map;
    }

    public List<Integer> getRegion(File file) {
        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Integer> ans = new LinkedList<Integer>();
        while(scanner.hasNext()) {
            int tmp = scanner.nextInt();
            ans.add(tmp);
        }

        return ans;
    }

    public List<Integer> readRegion(File file) {

        Scanner scanner = null;

        try {
            scanner = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        List<Integer> ans = new LinkedList<Integer>();
        while(scanner.hasNext()) {
            int tmp = scanner.nextInt();
            ans.add(tmp);
        }

        return ans;
    }

    public HashMap<Integer, Integer> recordFPVisit2(File file, List<Integer> pars) {

        List<Par> parList = ReadPar.getPar();

        int flag[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            flag[i] = 0;
        }

        for (int i = 0; i < pars.size(); i++) {
            flag[pars.get(i)] = 1;
        }

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        HashMap<Integer, Integer> per_map = new HashMap<Integer, Integer>();
        int pre = -1;
        List<Record> batch = new LinkedList<Record>();
        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int curTime = Integer.valueOf(input[1]).intValue();
            double x = Double.valueOf(input[2]).doubleValue();
            double y = Double.valueOf(input[3]).doubleValue();
            int mFloor = Integer.valueOf(input[4]).intValue();
            int parID = Integer.valueOf(input[5]).intValue();
            int isStay = Integer.valueOf(input[6]).intValue();
            int isOutlier = Integer.valueOf(input[7]).intValue();

            Record record = new Record(curTime, x, y, mFloor, parID, isStay, isOutlier);

            if (personID != pre) {

                int partition[] = new int[parList.size()];

                for (int i = 0; i < parList.size(); i++) {
                    partition[i] = 0;
                }

                for (int i = 0; i < batch.size(); i++) {
                    if (flag[batch.get(i).getParID()] == 1) {
                        partition[batch.get(i).getParID()] = 1;
                    }
                }

                List<Integer> temp = new LinkedList<Integer>();
                for (int i = 0; i < parList.size(); i++) {
                    if (partition[i] == 1) {
                        temp.add(i);
                    }
                }

                for (int i = 0; i < temp.size(); i++) {
                    for (int j = i + 1; j < temp.size(); j++) {
                        int fp = encode(temp.get(i), temp.get(j));
                        per_map.put(fp, 1);
                    }
                }
/*
                for (int i = 0; i < batch.size(); i++) {
                    if (flag[batch.get(i).getParID()] == 1) {
                        for (int j = i + 1; j < batch.size(); j++) {
                            if (flag[batch.get(j).getParID()] == 1) {
                                int parID1 = batch.get(i).getParID();
                                int parID2 = batch.get(j).getParID();
                                int fp = encode(parID1, parID2);
                                per_map.put(fp, 1);
                            }
                        }
                    }
                }
*/
                Iterator<Integer> iter = per_map.keySet().iterator();
                while (iter.hasNext()) {
                    int fp = iter.next();
                    int count = 0;
                    if (map.get(fp) != null) {
                        count = map.get(fp) + 1;
                    }
                    else {
                        count = 1;
                    }
                    map.put(fp, count);
                }

                pre = personID;
                per_map.clear();
                batch.clear();
                batch.add(record);
            }
            else {
                batch.add(record);
            }
        }

        if (batch.size() != 0) {
            for (int i = 0; i < batch.size(); i++) {
                if (flag[batch.get(i).getParID()] == 1) {
                    for (int j = i + 1; j < batch.size(); j++) {
                        if (flag[batch.get(j).getParID()] == 1) {
                            int parID1 = batch.get(i).getParID();
                            int parID2 = batch.get(j).getParID();
                            int fp = encode(parID1, parID2);
                            per_map.put(fp, 1);
                        }
                    }
                }
            }

            Iterator<Integer> iter = per_map.keySet().iterator();
            while (iter.hasNext()) {
                int fp = iter.next();
                int count = 0;
                if (map.get(fp) != null) {
                    count = map.get(fp) + 1;
                }
                else {
                    count = 1;
                }
                map.put(fp, count);
            }
        }

        return map;
    }

    public void recordTest() {

        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File records1 = new File(System.getProperty("user.dir") + "/records1.txt");
        File records2 = new File(System.getProperty("user.dir") + "/records2.txt");
        File records3 = new File(System.getProperty("user.dir") + "/records3.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);

        System.out.println("running");
        double t1 = System.currentTimeMillis();
        int cnt1[] = recordVisit(records1, region50);
        System.out.println("running");
        double t2 = System.currentTimeMillis();
        int cnt2[] = recordVisit(records2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        int cnt3[] = recordVisit(records3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        int cnt4[] = recordVisit(records2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        int cnt5[] = recordVisit(records2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
    }

    public void recordFPTest2() {

        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File records1 = new File(System.getProperty("user.dir") + "/records1.txt");
        File records2 = new File(System.getProperty("user.dir") + "/records2.txt");
        File records3 = new File(System.getProperty("user.dir") + "/records3.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);

        System.out.println("running");
        double t1 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt1 = recordFPVisit2(records1, region50);
        System.out.println("running");
        double t2 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt2 = recordFPVisit2(records2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt3 = recordFPVisit2(records3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt4 = recordFPVisit2(records2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt5 = recordFPVisit2(records2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));

    }

    public void recordFPTest() {

        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File records1 = new File(System.getProperty("user.dir") + "/records1.txt");
        File records2 = new File(System.getProperty("user.dir") + "/records2.txt");
        File records3 = new File(System.getProperty("user.dir") + "/records3.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);

        System.out.println("running");
        double t1 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt1 = recordFPVisit(records1, region50);
        System.out.println("running");
        /*
        double t2 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt2 = recordFPVisit(records2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt3 = recordFPVisit(records3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt4 = recordFPVisit(records2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt5 = recordFPVisit(records2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
        */

        for (Integer key : cnt1.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt1.get(key));
        }


    }

    public void msemanticsFPTest() {

        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File msemantics1 = new File(System.getProperty("user.dir") + "/msemantics1.txt");
        File msemantics2 = new File(System.getProperty("user.dir") + "/msemantics2.txt");
        File msemantics3 = new File(System.getProperty("user.dir") + "/msemantics3.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);

        System.out.println("running");
        double t1 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt1 = msematicsFPVisit(msemantics1, region50);
        System.out.println("running");
        /*
        double t2 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt2 = msematicsFPVisit(msemantics2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt3 = msematicsFPVisit(msemantics3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt4 = msematicsFPVisit(msemantics2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt5 = msematicsFPVisit(msemantics2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
*/
        for (Integer key : cnt1.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt1.get(key));
        }
/*
        for (Integer key : cnt2.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt2.get(key));
        }

        for (Integer key : cnt3.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt3.get(key));
        }

        for (Integer key : cnt4.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt4.get(key));
        }

        for (Integer key : cnt5.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt5.get(key));
        }
*/
    }

    public void msemanticsTest() {

        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File msemantics1 = new File(System.getProperty("user.dir") + "/msemantics1.txt");
        File msemantics2 = new File(System.getProperty("user.dir") + "/msemantics2.txt");
        File msemantics3 = new File(System.getProperty("user.dir") + "/msemantics3.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);

        System.out.println("running");
        double t1 = System.currentTimeMillis();
        int cnt1[] = msematicsVisit(msemantics1, region50);
        System.out.println("running");
        double t2 = System.currentTimeMillis();
        int cnt2[] = msematicsVisit(msemantics2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        int cnt3[] = msematicsVisit(msemantics3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        int cnt4[] = msematicsVisit(msemantics2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        int cnt5[] = msematicsVisit(msemantics2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
    }

    public static void main(String args[]) {
        visitPar visitPar = new visitPar();
/*
        System.out.println("Records Test:");
        visitPar.recordTest();
*/
/*
        System.out.println("Msemantics Test:");
        visitPar.msemanticsTest();
*/

/*
        System.out.println("Records FP Test:");
        visitPar.recordFPTest();

        System.out.println("Msemantics FP Test:");
        visitPar.msemanticsFPTest();
*/

        System.out.println("Records FP Test:");
        visitPar.recordFPTest2();

    }

}
