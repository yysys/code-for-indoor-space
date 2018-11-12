package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Dest;
import dk.aau.cs.idq.indoorentities.Door;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class ReadDest {
    public static int destNum = 423;            // the amount of destination, default value is 423
    public static ArrayList<Dest> destList = null;

    /**
     *
     * read dest from disk to memory
     *
     * @return ArrayList<Dest>
     */
    public static ArrayList<Dest> getDest() {
        if(destList != null) return destList;

        destList = new ArrayList<Dest>();
        File destFile = new File(outputPath + "/Dest.txt");
        Scanner in = null;
        try {
            in = new Scanner(destFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {
            String line = in.nextLine();
            String[] input = line.split(" ");

            int mID = Integer.valueOf(input[0]).intValue();
            double x1 = Double.valueOf(input[1]).doubleValue();
            double x2 = Double.valueOf(input[2]).doubleValue();
            double y1 = Double.valueOf(input[3]).doubleValue();
            double y2 = Double.valueOf(input[4]).doubleValue();
            int mFloor = Integer.valueOf(input[5]).intValue();
            int curPair = Integer.valueOf(input[6]).intValue();

            destList.add(new Dest(x1, x2, y1, y2, mFloor, mID, curPair));
        }

        destNum = destList.size();

        return destList;
    }
}
