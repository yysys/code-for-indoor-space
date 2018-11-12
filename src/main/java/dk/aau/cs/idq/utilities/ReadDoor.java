package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.indoorentities.Door;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class ReadDoor {
    public static int perFloorDoorNum = 220;        // the amount of destination, default value is 423
    public static ArrayList<Door> doorList = null;

    /**
     *
     * read door from disk to memory
     *
     * @return ArrayList<Door>
     */
    public static ArrayList<Door> getDoor() {
        if(doorList != null) return doorList;

        doorList = new ArrayList<Door>();
        File doorFile = new File(outputPath + "/Door.txt");
        Scanner in = null;
        try {
            in = new Scanner(doorFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {
            String line = in.nextLine();
            String[] input = line.split(" ");

            int mID = Integer.valueOf(input[0]).intValue();
            double x = Double.valueOf(input[1]).doubleValue();
            double y = Double.valueOf(input[2]).doubleValue();
            int mFloor = Integer.valueOf(input[3]).intValue();

            Door tmp = new Door(x, y, mFloor, mID);
            for (int i = 4; i < input.length; i++) {
                tmp.addPar(Integer.valueOf(input[i]).intValue());
            }
            doorList.add(tmp);
        }

        perFloorDoorNum = 0;
        for (int i = 0; i < doorList.size(); i++) {
            if (doorList.get(i).getmFloor() == 0)
                perFloorDoorNum++;
        }

        return doorList;
    }
}
