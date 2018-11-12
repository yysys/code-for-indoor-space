package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.clean.Record;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ReadRecord {

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
            int isOutlier = Integer.valueOf(input[6]).intValue();

            records.add(new Record(curID, curTime, x, y, mFloor, parID, isStay, isOutlier));
            curID++;
        }

        return records;
    }

}
