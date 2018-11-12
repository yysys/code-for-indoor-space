package dk.aau.cs.idq.others;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class visit {

    public int encode(int parID1, int parID2) {
        int t1 = Math.min(parID1, parID2);
        int t2 = Math.max(parID1, parID2);
        return t1 * 1410 + t2;
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

    public HashMap<Integer, Integer> FPcalculate(List<Integer> region) {

        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();

        for (int i = 0; i < region.size(); i++) {
            for (int j = i + 1; j < region.size(); j++) {
                map.put(encode(region.get(i), region.get(j)), 1);
            }
        }
/*
        for (int i = 0; i < region.size(); i++) {
            map.put(region.get(i), 1);
        }
*/
        return map;
    }

    public HashMap<Integer, Integer> partition2region(int[] partition) {

        List<Integer> ans = new LinkedList<Integer>();

        for (int i = 0; i < partition.length; i++) {
            if (partition[i] != 0) {
                ans.add(i);
            }
        }

        return FPcalculate(ans);
    }

    public HashMap<Integer, Integer> batchOperation1(List<Record> in, int[] flag) {


        List<Par> parList = ReadPar.getPar();

        int partition[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            partition[i] = 0;
        }

        for (int i = 0; i < in.size(); i++) {
            if (flag[in.get(i).getParID()] == 1) {
                partition[in.get(i).getParID()] = 1;
            }
        }

        return  partition2region(partition);
    }

    public HashMap<Integer, Integer> batchOperation2(List<Msemantic> in, int[] flag) {

        List<Par> parList = ReadPar.getPar();

        int partition[] = new int[parList.size()];

        for (int i = 0; i < parList.size(); i++) {
            partition[i] = 0;
        }

        for (int i = 0; i < in.size(); i++) {
            if (flag[in.get(i).getParID()] == 1) {
                partition[in.get(i).getParID()] = 1;
            }
        }

        return  partition2region(partition);
    }

    public HashMap<Integer, Integer> recordsFP(File file, List<Integer> pars) {
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

        HashMap<Integer, Integer> ans = new HashMap<Integer, Integer>();
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
                HashMap<Integer, Integer> per_map = batchOperation1(batch, flag);

                Iterator<Integer> iter = per_map.keySet().iterator();
                while (iter.hasNext()) {
                    int fp = iter.next();
                    int count = 0;
                    if (ans.get(fp) != null) {
                        count = ans.get(fp) + 1;
                    }
                    else {
                        count = 1;
                    }
                    ans.put(fp, count);
                }

                pre = personID;
                batch.clear();
                batch.add(record);
            }
            else {
                batch.add(record);
            }
        }

        if (batch.size() != 0) {
            HashMap<Integer, Integer> per_map = batchOperation1(batch, flag);

            Iterator<Integer> iter = per_map.keySet().iterator();
            while (iter.hasNext()) {
                int fp = iter.next();
                int count = 0;
                if (ans.get(fp) != null) {
                    count = ans.get(fp) + 1;
                }
                else {
                    count = 1;
                }
                ans.put(fp, count);
            }
        }

        return ans;
    }

    public HashMap<Integer, Integer> msematicsFP(File file, List<Integer> pars) {

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

        HashMap<Integer, Integer> ans = new HashMap<Integer, Integer>();
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
                HashMap<Integer, Integer> per_map = batchOperation2(batch, flag);

                Iterator<Integer> iter = per_map.keySet().iterator();
                while (iter.hasNext()) {
                    int fp = iter.next();
                    int count = 0;
                    if (ans.get(fp) != null) {
                        count = ans.get(fp) + 1;
                    }
                    else {
                        count = 1;
                    }
                    ans.put(fp, count);
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
            HashMap<Integer, Integer> per_map = batchOperation2(batch, flag);

            Iterator<Integer> iter = per_map.keySet().iterator();
            while (iter.hasNext()) {
                int fp = iter.next();
                int count = 0;
                if (ans.get(fp) != null) {
                    count = ans.get(fp) + 1;
                }
                else {
                    count = 1;
                }
                ans.put(fp, count);
            }
        }

        return ans;
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
        HashMap<Integer, Integer> cnt1 = recordsFP(records1, region50);
        System.out.println("running");
        double t2 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt2 = recordsFP(records2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt3 = recordsFP(records3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt4 = recordsFP(records2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt5 = recordsFP(records2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
/*
        for (Integer key : cnt1.keySet()) {
            System.out.println("parID1 = " + (key / 1410) + " parID2 = " + (key % 1410) + " value = " + cnt1.get(key));
        }
*/

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
        HashMap<Integer, Integer> cnt1 = msematicsFP(msemantics1, region50);
        System.out.println("running");
        double t2 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt2 = msematicsFP(msemantics2, region50);
        System.out.println("running");
        double t3 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt3 = msematicsFP(msemantics3, region50);
        System.out.println("running");
        double t4 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt4 = msematicsFP(msemantics2, region30);
        System.out.println("running");
        double t5 = System.currentTimeMillis();
        HashMap<Integer, Integer> cnt5 = msematicsFP(msemantics2, region70);
        double t6 = System.currentTimeMillis();

        System.out.println("30 minute 50% region = " + (t2 - t1));

        System.out.println("60 minute 50% region = " + (t3 - t2));

        System.out.println("90 minute 50% region = " + (t4 - t3));

        System.out.println("60 minute 30% region = " + (t5 - t4));

        System.out.println("60 minute 70% region = " + (t6 - t5));
/*
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

    public void accuracyTest() {
        File region30file = new File(System.getProperty("user.dir") + "/region30.txt");
        File region50file = new File(System.getProperty("user.dir") + "/region50.txt");
        File region70file = new File(System.getProperty("user.dir") + "/region70.txt");

        File msemantics1 = new File(System.getProperty("user.dir") + "/msemantics1.txt");
        File msemantics2 = new File(System.getProperty("user.dir") + "/msemantics2.txt");
        File msemantics3 = new File(System.getProperty("user.dir") + "/msemantics3.txt");

        File records1 = new File(System.getProperty("user.dir") + "/records1.txt");
        File records2 = new File(System.getProperty("user.dir") + "/records2.txt");
        File records3 = new File(System.getProperty("user.dir") + "/records3.txt");

        File rawrecords1 = new File(System.getProperty("user.dir") + "/rawrecords1.txt");
        File rawrecords2 = new File(System.getProperty("user.dir") + "/rawrecords2.txt");
        File rawrecords3 = new File(System.getProperty("user.dir") + "/rawrecords3.txt");

        File truth30 = new File(System.getProperty("user.dir") + "/truth30.txt");
        File truth60 = new File(System.getProperty("user.dir") + "/truth60.txt");
        File truth90 = new File(System.getProperty("user.dir") + "/truth90.txt");

        List<Integer> region30 = readRegion(region30file);
        List<Integer> region50 = readRegion(region50file);
        List<Integer> region70 = readRegion(region70file);
/*
        System.out.println("running");
        HashMap<Integer, Integer> cnt1 = msematicsFP(msemantics3, region70);
        System.out.println("running");
*/
/*
        System.out.println("running");
        HashMap<Integer, Integer> cnt1 = recordsFP(rawrecords2, region70);
        System.out.println("running");
        HashMap<Integer, Integer> cnt2 = msematicsFP(msemantics2, region70);
        System.out.println("running");
        HashMap<Integer, Integer> cnt3 = recordsFP(truth60, region70);
        System.out.println("running");

        int cnt = 0;

        List<Map.Entry<Integer, Integer>> list1 = new LinkedList<Map.Entry<Integer, Integer>>(cnt1.entrySet());
        Collections.sort(list1, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        List<Map.Entry<Integer, Integer>> list2 = new LinkedList<Map.Entry<Integer, Integer>>(cnt2.entrySet());
        Collections.sort(list2, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        List<Map.Entry<Integer, Integer>> list3 = new LinkedList<Map.Entry<Integer, Integer>>(cnt3.entrySet());
        Collections.sort(list3, new Comparator<Map.Entry<Integer, Integer>>() {
            public int compare(Map.Entry<Integer, Integer> o1, Map.Entry<Integer, Integer> o2) {
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });
/*
        for (int i = 0; i < 60; i++) {
            System.out.println(list1.get(i).getKey() + " " + list1.get(i).getValue());
        }
*/
/*
        for (int i = 0; i < 60; i++) {
            System.out.println(list2.get(i).getKey() + " " + list2.get(i).getValue());
        }
*/
/*
        cnt = 0;
        for (int i = 0; i < 60; i++) {
            int target = list1.get(i).getKey();
            for (int j = 0; j < 60; j++) {
                int now = list3.get(j).getKey();
                //System.out.println(target + " " + now);
                if (target == now) {
                    cnt++;
                    break;
                }
            }
        }

        System.out.println(cnt / 60.0);
 */
    }

    public static void main(String args[]) {
        visit visit = new visit();
/*
        System.out.println("Records FP Test");
        visit.recordFPTest();

        System.out.println("M-semantics FP Test");
        visit.msemanticsFPTest();
*/

        visit.accuracyTest();
    }

}
