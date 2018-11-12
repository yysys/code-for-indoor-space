package dk.aau.cs.idq.others;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Dest;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.ReadDest;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class dataTest {

    public static final int duration1 = 1800;
    public static final int duration2 = 3600;
    public static final int duration3 = 5400;

    public void createDest() throws IOException {

        List<Dest> dests = ReadDest.getDest();
        List<Par> pars = ReadPar.getPar();

        Random r = new Random();

        int num1 = (int)(dests.size() * 0.3);
        int num2 = (int)(dests.size() * 0.5);
        int num3 = (int)(dests.size() * 0.7);

        int vis1[] = new int[pars.size()];
        int vis2[] = new int[pars.size()];
        int vis3[] = new int[pars.size()];

        for (int i = 0;i < pars.size(); i++) {
            vis1[i] = 0;
            vis2[i] = 0;
            vis3[i] = 0;
        }

        for (int i = 0; i < num3; i++) {
            while(true) {
                int tmp = (int) (r.nextDouble() * dests.size());
                int parID = dests.get(tmp).getCurPar();
                if (vis3[parID] == 0) {
                    vis3[parID] = 1;
                    if (i < num1) {
                        vis1[parID] = 1;
                    }
                    if (i < num2) {
                        vis2[parID] = 1;
                    }

                    break;
                }
            }
        }

        List<Integer> ans1 = new LinkedList<Integer>();
        List<Integer> ans2 = new LinkedList<Integer>();
        List<Integer> ans3 = new LinkedList<Integer>();

        for (int i = 0; i < pars.size(); i++) {
            if (vis1[i] == 1) {
                ans1.add(i);
            }
            if (vis2[i] == 1) {
                ans2.add(i);
            }
            if (vis3[i] == 1) {
                ans3.add(i);
            }
        }

        File file1 = new File(System.getProperty("user.dir") + "/region30.txt");
        File file2 = new File(System.getProperty("user.dir") + "/region50.txt");
        File file3 = new File(System.getProperty("user.dir") + "/region70.txt");

        FileWriter fw1 = new FileWriter(file1);
        FileWriter fw2 = new FileWriter(file2);
        FileWriter fw3 = new FileWriter(file3);

        for (int i = 0; i < ans1.size(); i++) {
            fw1.write(ans1.get(i) + "\n");
        }

        for (int i = 0; i < ans2.size(); i++) {
            fw2.write(ans2.get(i) + "\n");
        }

        for (int i = 0; i < ans3.size(); i++) {
            fw3.write(ans3.get(i) + "\n");
        }

        fw1.close();
        fw2.close();
        fw3.close();
    }
/*
    public void initRegion() throws IOException {
        List<Integer> region30 = createDest(0.3);
        List<Integer> region50 = createDest(0.5);
        List<Integer> region70 = createDest(0.7);

        File file30 = new File(System.getProperty("user.dir") + "/region30.txt");
        File file50 = new File(System.getProperty("user.dir") + "/region50.txt");
        File file70 = new File(System.getProperty("user.dir") + "/region70.txt");

        FileWriter fw30 = new FileWriter(file30);
        FileWriter fw50 = new FileWriter(file50);
        FileWriter fw70 = new FileWriter(file70);

        for (int i = 0; i < region30.size(); i++) {
            fw30.write(region30.get(i) + "\n");
        }

        for (int i = 0; i < region50.size(); i++) {
            fw50.write(region50.get(i) + "\n");
        }

        for (int i = 0; i < region70.size(); i++) {
            fw70.write(region70.get(i) + "\n");
        }

        fw30.close();
        fw50.close();
        fw70.close();
    }
*/
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

    public void initRecords() throws IOException {

        File file1 = new File(System.getProperty("user.dir") + "/records1.txt");
        File file2 = new File(System.getProperty("user.dir") + "/records2.txt");
        File file3 = new File(System.getProperty("user.dir") + "/records3.txt");

        FileWriter fw1 = new FileWriter(file1);
        FileWriter fw2 = new FileWriter(file2);
        FileWriter fw3 = new FileWriter(file3);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File file = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");

            List<Record> records = readPersonData(file);

            for (int j = 0; j < records.size(); j++) {
                if (records.get(j).getTime() < duration1) {
                    fw1.write(i + " " + records.get(j).toString() + "\n");
                }
                if (records.get(j).getTime() < duration2) {
                    fw2.write(i + " " + records.get(j).toString() + "\n");
                }
                if (records.get(j).getTime() < duration3) {
                    fw3.write(i + " " + records.get(j).toString() + "\n");
                }
            }
            System.out.println(i);
        }

        fw1.close();
        fw2.close();
        fw3.close();
    }

    public void initMsemantics() throws IOException {
        File file1 = new File(System.getProperty("user.dir") + "/msemantics1.txt");
        File file2 = new File(System.getProperty("user.dir") + "/msemantics2.txt");
        File file3 = new File(System.getProperty("user.dir") + "/msemantics3.txt");

        FileWriter fw1 = new FileWriter(file1);
        FileWriter fw2 = new FileWriter(file2);
        FileWriter fw3 = new FileWriter(file3);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File file = new File(System.getProperty("user.dir") + "/Msemantics/person" + i + ".txt");

            Scanner in = null;

            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<Msemantic> tmp = new LinkedList<Msemantic>();
            while(in.hasNext()) {
                String line = in.nextLine();
                String input[] = line.split(" ");

                int personID = Integer.valueOf(input[0]).intValue();
                int startTime = Integer.valueOf(input[1]).intValue();
                int endTime = Integer.valueOf(input[2]).intValue();
                int isStay = Integer.valueOf(input[3]).intValue();
                int parID = Integer.valueOf(input[4]).intValue();

                Msemantic msematics = new Msemantic(personID, startTime, endTime, isStay, parID);
                tmp.add(msematics);
            }

            for (int j = 0; j < tmp.size(); j++) {
                if (tmp.get(j).getEndTime() < duration1) {
                    fw1.write(tmp.get(j).toString() + "\n");
                }
                else if (tmp.get(j).getStartTime() < duration1){
                    int personID = tmp.get(j).getPersonID();
                    int startTime = tmp.get(j).getStartTime();
                    int endTime = tmp.get(j).getEndTime();
                    int isStay = tmp.get(j).getIsStay();
                    int parID = tmp.get(j).getParID();

                    Msemantic msematics = new Msemantic(personID, startTime, duration1-1, isStay, parID);

                    fw1.write(msematics.toString() + "\n");
                }

                if (tmp.get(j).getEndTime() < duration2) {
                    fw2.write(tmp.get(j).toString() + "\n");
                }
                else if (tmp.get(j).getStartTime() < duration2) {
                    int personID = tmp.get(j).getPersonID();
                    int startTime = tmp.get(j).getStartTime();
                    int endTime = tmp.get(j).getEndTime();
                    int isStay = tmp.get(j).getIsStay();
                    int parID = tmp.get(j).getParID();

                    Msemantic msematics = new Msemantic(personID, startTime, duration2-1, isStay, parID);

                    fw2.write(msematics.toString() + "\n");
                }

                if (tmp.get(j).getEndTime() < duration3) {
                    fw3.write(tmp.get(j).toString() + "\n");
                }
                else if (tmp.get(j).getStartTime() < duration3) {
                    int personID = tmp.get(j).getPersonID();
                    int startTime = tmp.get(j).getStartTime();
                    int endTime = tmp.get(j).getEndTime();
                    int isStay = tmp.get(j).getIsStay();
                    int parID = tmp.get(j).getParID();

                    Msemantic msematics = new Msemantic(personID, startTime, duration3-1, isStay, parID);

                    fw3.write(msematics.toString() + "\n");
                }
            }
        }

        fw1.close();
        fw2.close();
        fw3.close();
    }

    public void initRawRecords() throws IOException {

        File file1 = new File(System.getProperty("user.dir") + "/rawrecords1.txt");
        File file2 = new File(System.getProperty("user.dir") + "/rawrecords2.txt");
        File file3 = new File(System.getProperty("user.dir") + "/rawrecords3.txt");

        FileWriter fw1 = new FileWriter(file1);
        FileWriter fw2 = new FileWriter(file2);
        FileWriter fw3 = new FileWriter(file3);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File file = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

            List<Record> records = readPersonData(file);

            for (int j = 0; j < records.size(); j++) {
                if (records.get(j).getTime() < duration1) {
                    fw1.write(i + " " + records.get(j).toString() + "\n");
                }
                if (records.get(j).getTime() < duration2) {
                    fw2.write(i + " " + records.get(j).toString() + "\n");
                }
                if (records.get(j).getTime() < duration3) {
                    fw3.write(i + " " + records.get(j).toString() + "\n");
                }
            }
            System.out.println(i);
        }

        fw1.close();
        fw2.close();
        fw3.close();

    }

    public void initTruthSnippet() {



    }

    public static void main(String args[]) throws IOException {
        dataTest dataTest = new dataTest();
/*
        dataTest.createDest();
        dataTest.initRecords();
        dataTest.initMsemantics();

        */

        //dataTest.initRawRecords();
        dataTest.initTruthSnippet();
    }
}
