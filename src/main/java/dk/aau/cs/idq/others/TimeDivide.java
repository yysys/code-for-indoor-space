package dk.aau.cs.idq.others;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.TraDataGen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class TimeDivide {

    public static List<Record>[] person = null;

    public void divide(int timeLimit, File file) {
        File traDataFile = new File(System.getProperty("user.dir") + "/TrajectoryData.txt");

        Scanner in = null;
        try {
            in = new Scanner(traDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        FileWriter out = null;
        try {
            out = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            if (curTime < timeLimit) {
                try {
                    out.write(curTime + " " + personID + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                break;
            }
        }

        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void tran(File file) {
        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            Record record = new Record(curTime, x, y, mFloor, parID, isStay, 0);

            person[personID].add(record);
        }

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            for (int j = 0; j < person[i].size(); j++) {
                try {
                    fw.write(i + " " + person[i].get(j).toString() + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void work() {
        File file1 = new File(System.getProperty("user.dir") + "/truth30.txt");
        File file2 = new File(System.getProperty("user.dir") + "/truth60.txt");
        File file3 = new File(System.getProperty("user.dir") + "/truth90.txt");

        person = new List[TraDataGen.totalPerson];
        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            person[i] = new LinkedList<Record>();
        }

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            person[i].clear();
        }
        tran(file1);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            person[i].clear();
        }
        tran(file2);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            person[i].clear();
        }
        tran(file3);

    }

    public void solve() {
        File file1 = new File(System.getProperty("user.dir") + "/truth30.txt");
        File file2 = new File(System.getProperty("user.dir") + "/truth60.txt");
        File file3 = new File(System.getProperty("user.dir") + "/truth90.txt");

        divide(1800, file1);
        divide(3600, file2);
        divide(5400, file3);

    }

    public static void main(String args[]) {

        TimeDivide timeDivide = new TimeDivide();
        timeDivide.solve();
        timeDivide.work();

    }

}
