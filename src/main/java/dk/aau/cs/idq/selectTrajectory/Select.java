package dk.aau.cs.idq.selectTrajectory;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.update.Msemantic;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Select {

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

    public ArrayList<Msemantic> getMsemantics(File file) {

        ArrayList<Msemantic> ans = new ArrayList<Msemantic>();

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {

            int personID = in.nextInt();
            int startTime = in.nextInt();
            int endTime = in.nextInt();
            int isStay = in.nextInt();
            int parID = in.nextInt();

            Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);
            ans.add(msemantic);
        }

        return ans;
    }

    public boolean check(ArrayList<Record> records, ArrayList<Msemantic> msemantics) {

        int ok = 1;

        int valid = 0;
        int invalid = 0;

        for (int i = 0; i < msemantics.size(); i++) {
            int parID = msemantics.get(i).getParID();
            int hit = 0;
            int nothit = 0;
            for (int j = 0; j < records.size(); j++) {
                if (records.get(j).getTime() >= msemantics.get(i).getStartTime() && records.get(j).getTime() <= msemantics.get(i).getEndTime()) {
                    if (records.get(j).getParID() == parID) {
                        hit++;
                    }
                    else {
                        nothit++;
                    }
                }
            }

            if ((double)hit / (double)(nothit + hit) > 0.8) {
                valid++;
            }
            else {
                invalid++;
            }
        }

        if ((double)valid / (valid + invalid) < 0.8) {
            ok = 0;
        }

        if (ok == 1) return true;
        else return false;
    }

    public List<Integer> Select() {

        List<Integer> ans = new LinkedList<Integer>();

        for (int i = 300; i < TraDataGen.totalPerson; i++) {

            File groundTruthFile = new File(System.getProperty("user.dir") + "/groundTruth/person" + i + ".txt");

            ArrayList<Record> groundTruthRecords = getRecord(groundTruthFile);

            File msemanticsFile = new File(System.getProperty("user.dir") + "/nowSemantics/person" + i + ".txt");

            ArrayList<Msemantic> msemantics = getMsemantics(msemanticsFile);

            if (msemantics.size() < 50) continue;

            if (check(groundTruthRecords, msemantics)) {
                ans.add(i);
            }

            System.out.println("select " + i);
        }

        return ans;
    }
}
