package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.indoorentities.Dest;
import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.Pair;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class ReadDest2Dest {
    public static final int pathNum = 5;
    public static List<Pair> dis[][][] = null;
    public static double[][][] disProb = null;

    /**
     *lujin
     * read the matrix of path from disk to memory
     *
     * @return List<Pair>[][][] the path
     */
    public static List<Pair>[][][] getDest2Dest() {
        if (dis != null) return dis;

        ArrayList<Dest> destList = ReadDest.getDest();
        dis = new List[pathNum][ReadDest.destNum][ReadDest.destNum];

        File dest2DestFile = new File(outputPath + "/Dest2Dest.txt");
        Scanner in = null;
        try {
            in = new Scanner(dest2DestFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int cnt = 0; cnt < pathNum; cnt++) {
            for (int j = 0; j < ReadDest.destNum; j++) {
                for (int k = 0; k < ReadDest.destNum; k++) {
                    String line = in.nextLine();
                    String[] input = line.split(" ");
                    int s = Integer.valueOf(input[0]).intValue();
                    int t = Integer.valueOf(input[1]).intValue();

                    dis[cnt][s][t] = new LinkedList<Pair>();
                    for (int i = 2; i < input.length; i += 2) {
                        dis[cnt][s][t].add(new Pair(Integer.valueOf(input[i]).intValue(), Integer.valueOf(input[i+1]).intValue()));
                    }
                }
            }
        }

        return dis;
    }

    /**
     *
     * @param cnt
     * @param s destID
     * @param t
     * @return the path from destID s to destID t
     */
    public static List<Pair> getDest2Dest(int cnt, int s, int t) {
        if (dis == null) dis = getDest2Dest();
        return dis[cnt][s][t];
    }

    /**
     *
     * get the probability of destination to destination
     *
     * @return
     */
    public static double[][][] getDest2DestProb() {
        if (disProb != null) return disProb;
        disProb = new double[pathNum][ReadDest.destNum][ReadDest.destNum];

        File parFile = new File(outputPath + "/Dest2DestProb.txt");
        Scanner in = null;
        try {
            in = new Scanner(parFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ReadDest.destNum; i++) {
            for (int j = 0; j < ReadDest.destNum; j++) {
                String line = in.nextLine();
                String[] input = line.split(" ");
                int s = Integer.valueOf(input[0]).intValue();
                int t = Integer.valueOf(input[1]).intValue();
                disProb[0][i][j] = Double.valueOf(input[2]).doubleValue();
                disProb[1][i][j] = Double.valueOf(input[3]).doubleValue();
                disProb[2][i][j] = Double.valueOf(input[4]).doubleValue();
                disProb[3][i][j] = Double.valueOf(input[5]).doubleValue();
                disProb[4][i][j] = Double.valueOf(input[6]).doubleValue();
            }
        }

        return disProb;
    }
}
