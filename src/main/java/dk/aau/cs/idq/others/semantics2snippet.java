package dk.aau.cs.idq.others;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.semantics.Snpt;
import dk.aau.cs.idq.update.Msemantic;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class semantics2snippet {

    public static int trainingSetNum = 300;

    public Msemantic merge(List<Msemantic> msemantics) {

        if (msemantics.size() == 0) {
            return null;
        }

        int personID = msemantics.get(0).getPersonID();
        int startTime = msemantics.get(0).getStartTime();
        int endTime = msemantics.get(msemantics.size()-1).getEndTime();
        int isStay = msemantics.get(0).getIsStay();
        int parID = -1;

        Msemantic ans = new Msemantic(personID, startTime, endTime, isStay, parID);

        return ans;
    }

    public List<Snpt> tran(List<Record> records, List<Msemantic> msemantics) {

        List<Snpt> ans = new LinkedList<Snpt>();

        List<Msemantic> tmp = new LinkedList<Msemantic>();

        List<Msemantic> res = new LinkedList<Msemantic>();

        for (int i = 0; i < msemantics.size(); i++) {
            if (msemantics.get(i).getIsStay() == 1) {
                Msemantic msemantic = merge(msemantics);
                if (msemantic != null) {
                    res.add(msemantic);
                }
                tmp.clear();
                res.add(msemantics.get(i));
            }
            else {
                tmp.add(msemantics.get(i));
            }
        }
        Msemantic msemantic = merge(msemantics);
        if (msemantic != null) {
            res.add(msemantic);
        }

        for (int i = 0; i < res.size(); i++) {
            int startTime = res.get(i).getStartTime();
            int endTime = res.get(i).getEndTime();
            int isStay = res.get(i).getIsStay();


        }

        return ans;
    }

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

            records.add(new Record(curTime, x, y, mFloor, parID, isStay, 0));
        }

        return records;
    }

    public List<Msemantic> readMsemantics(File file) {
        List<Msemantic> ans = new LinkedList<Msemantic>();

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int personID = Integer.valueOf(input[0]).intValue();
            int startTime = Integer.valueOf(input[1]).intValue();
            int endTime = Integer.valueOf(input[2]).intValue();
            int isStay = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();


            Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

            ans.add(msemantic);
        }

        return ans;
    }

    public void work() {

        for (int i = 0; i < trainingSetNum; i++) {
            File file = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");
            File file2 = new File(System.getProperty("user.dir") + "/Msemantics/person" + i + ".txt");

            List<Record> records = readPersonData(file);

            List<Msemantic> msemantics = readMsemantics(file2);

            List<Snpt> snpts = tran(records, msemantics);

        }

    }

    public static void main(String args[]) {
        semantics2snippet semantics2snippet = new semantics2snippet();
        semantics2snippet.work();
    }
}
