package dk.aau.cs.idq.indoorentities;

import dk.aau.cs.idq.utilities.ReadDest;

import java.util.ArrayList;

public class PerFloorDest {
    private static int floorNum = 10;
    public static ArrayList<Integer> perFloorDest[] = null;

    public static ArrayList<Integer>[] getPerFloorDest() {
        if (perFloorDest != null) return perFloorDest;

        perFloorDest = new ArrayList[floorNum];
        for (int i = 0; i < floorNum; i++) perFloorDest[i] = new ArrayList<Integer>();

        ArrayList<Dest> destList = ReadDest.getDest();
        for (Dest dest : destList) {
            perFloorDest[dest.getmFloor()].add(dest.getmDestID());
        }

        return perFloorDest;
    }
}
