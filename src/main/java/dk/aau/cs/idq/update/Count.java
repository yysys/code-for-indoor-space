package dk.aau.cs.idq.update;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Dest;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.utilities.ReadDest;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Count {

    public int cnt = 0;

    public int getDestID(Record tmp) {

        Point now = new Point(tmp.getX(), tmp.getY(), tmp.getFloor());

        List<Dest> dests = ReadDest.getDest();
        List<Par> pars = ReadPar.getPar();
        for (int i = 0; i < dests.size(); i++) {
            int floor = pars.get(dests.get(i).getCurPar()).getmFloor();
            if (floor == now.getmFloor()) {
                if (now.getX() >= dests.get(i).getX1() && now.getX() <= dests.get(i).getX2()) {
                    if (now.getY() >= dests.get(i).getY1() && now.getY() <= dests.get(i).getY2()) {
                        return i;
                    }
                }
            }
        }

        return -1;
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

    public List<Msemantic> solve(int i, List<Record> records) {
        int personID = i;
        int startTime = -1;
        int endTime = -1;
        int isStay = -1;
        int parID = -1;

        List<Msemantic> ans = new LinkedList<Msemantic>();


        for (int j = 0; j < records.size(); j++) {

            if (j == 0) {
                startTime = records.get(j).getTime();
                endTime = records.get(j).getTime();
                isStay = 0;
                if (records.get(j).isStay()) isStay = 1;
                parID = records.get(j).getParID();
                continue;
            }

            if (parID == records.get(j).getParID()) {
                endTime = records.get(j).getTime();
                if (records.get(j).isStay()) isStay = 1;
            }
            else {
                cnt++;
                ans.add(new Msemantic(personID, startTime, endTime, isStay, parID));

                startTime = records.get(j).getTime();
                endTime = records.get(j).getTime();
                isStay = 0;
                if (records.get(j).isStay()) isStay = 1;
                parID = records.get(j).getParID();
            }
        }
        cnt++;
        ans.add(new Msemantic(personID, startTime, endTime, isStay, parID));

        return ans;
    }

    public void write(List<Msemantic> ans, int i) throws IOException {
        File file = new File(System.getProperty("user.dir") + "/Msemantics/person" + i + ".txt");

        FileWriter fw = new FileWriter(file);

        for (int j = 0; j < ans.size(); j++) {
            fw.write(ans.get(j).toString() + "\n");
        }

        fw.close();
    }

    public void count() throws IOException {

        File dir = new File(System.getProperty("user.dir") + "/Msemantics");

        dir.mkdir();

        for (int i = 0; i < TraDataGen.totalPerson; i++) {

            File file = new File(System.getProperty("user.dir") + "/grandTruth/person" + i + ".txt");

            List<Record> records = readPersonData(file);

            List<Msemantic> ans = solve(i, records);

            write(ans, i);
            System.out.println(i);
        }

        System.out.println("total = " + cnt);
    }

    public static void main(String args[]) throws IOException {
        Count count = new Count();
        count.count();
    }

}
