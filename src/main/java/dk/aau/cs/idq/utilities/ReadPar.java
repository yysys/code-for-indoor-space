package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.indoorentities.Par;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class ReadPar {
    public static int perFloorParNum = 141;
    public static ArrayList<Par> parList = null;

    /**
     *
     * read par from disk to memory
     *
     * @return ArrayList<Par>
     */
    public static ArrayList<Par> getPar() {
        if(parList != null) return parList;

        parList = new ArrayList<Par>();
        File parFile = new File(outputPath + "/Par.txt");
        Scanner in = null;
        try {
            in = new Scanner(parFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (in.hasNext()) {
            String line = in.nextLine();
            String[] input = line.split(" ");

            int mID = Integer.valueOf(input[0]).intValue();
            double x1 = Double.valueOf(input[1]).doubleValue();
            double x2 = Double.valueOf(input[2]).doubleValue();
            double y1 = Double.valueOf(input[3]).doubleValue();
            double y2 = Double.valueOf(input[4]).doubleValue();
            int mFloor = Integer.valueOf(input[5]).intValue();
            int mType = Integer.valueOf(input[6]).intValue();
            Par tmp = new Par(x1, x2, y1, y2, mID, mType);
            tmp.setmFloor(mFloor);

            for (int i = 7; i < input.length; i++) {
                tmp.addDoor(Integer.valueOf(input[i]).intValue());
            }
            parList.add(tmp);
        }

        perFloorParNum = 0;
        for (int i = 0; i < parList.size(); i++) {
            if (parList.get(i).getmFloor() == 0)
                perFloorParNum++;
        }

        return parList;
    }
}
