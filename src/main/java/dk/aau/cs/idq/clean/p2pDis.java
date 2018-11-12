package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.PointToPoint;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class p2pDis {

    public static double d2dDistance[][] = null;
    public static List<Point> d2dPath[][] = null;

    /**
     * get the distance from point to point
     * @param s
     * @param t
     * @return
     */
    public static double getP2pDis(IdrObj s, IdrObj t) {

        if (d2dDistance == null) {
            d2dDistance = getD2dDistance();
        }

        if (s.getCurPar().getmID() == t.getCurPar().getmID()) {
            return PointToPoint.Dis(s.getmTruePos(), t.getmTruePos());
        }

        ArrayList<Door> doorList = ReadDoor.getDoor();
        Par sPar = s.getCurPar();
        Par tPar = t.getCurPar();

        double dis = (double)PointToPoint.INF;
        for (int sDoorID : sPar.getmDoors()) {
            for (int tDoorID : tPar.getmDoors()) {
                double s2d = PointToPoint.Dis(s.getmTruePos(), doorList.get(sDoorID));
                double t2d = PointToPoint.Dis(t.getmTruePos(), doorList.get(tDoorID));

                double tmpDis = s2d + t2d + d2dDistance[sDoorID][tDoorID];
                if(tmpDis < dis) {
                    dis = tmpDis;
                }
            }
        }

        return dis;
    }

    /**
     * get the path of point to point
     * @param s
     * @param t
     * @return
     */
    public static List<Point> getP2PPath(IdrObj s, IdrObj t) {
        List<Door> doorList = ReadDoor.getDoor();
        Par parS = s.getCurPar();
        Par parT = t.getCurPar();
        Point pointS = new Point(s.getmTruePos());
        Point pointT = new Point(t.getmTruePos());

        List<Point> ans = new LinkedList<Point>();
        if (parS.getmID() == parT.getmID()) {
            ans.add(pointS);
            ans.add(pointT);
        }
        else {
            int ansi = 0;
            int ansj = 0;
            double dis = PointToPoint.INF;
            for (int sDoorID : parS.getmDoors()) {
                for (int tDoorID : parT.getmDoors()) {
                    double s2d = PointToPoint.Dis(s.getmTruePos(), doorList.get(sDoorID));
                    double t2d = PointToPoint.Dis(t.getmTruePos(), doorList.get(tDoorID));

                    double tmpDis = s2d + t2d + d2dDistance[sDoorID][tDoorID];
                    if(tmpDis < dis) {
                        dis = tmpDis;
                        ansi = sDoorID;
                        ansj = tDoorID;
                    }
                }
            }

            int sDoorID = ansi;
            int tDoorID = ansj;

            ans.add(pointS);

            if (sDoorID == tDoorID) {
                double x = doorList.get(sDoorID).getX();
                double y = doorList.get(sDoorID).getY();
                int floor = doorList.get(sDoorID).getmFloor();

                ans.add(new Point(x, y, floor));
            }
            else {
                List<Point> tmp = getD2DPath(sDoorID, tDoorID);
                for (int i = 0; i < tmp.size(); i++) {
                    ans.add(tmp.get(i));
                }
            }

            ans.add(pointT);
        }

        return ans;
    }

    /**
     * get the path from door to door
     * @param doorS
     * @param doorT
     * @return
     */
    public static List<Point> getD2DPath(int doorS, int doorT) {

        if (d2dPath == null) d2dPath = init_D2dPath();

        if (d2dPath[doorS][doorT] == null) return new LinkedList<Point>();
        return d2dPath[doorS][doorT];
    }

    /**
     * inialization of path
     * @return
     */
    public static List<Point>[][] init_D2dPath() {

        List<Door> doors = ReadDoor.getDoor();

        d2dPath = new List[doors.size()][doors.size()];

        File file = new File(System.getProperty("user.dir") + "/d2dPathMatrix.txt");
        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {
            String line = in.nextLine();
            String[] input = line.split(" ");

            int sID = Integer.valueOf(input[0]).intValue();
            int tID = Integer.valueOf(input[1]).intValue();

            List<Point> path = new LinkedList<Point>();

            for (int i = 2; i < input.length; i++) {
                int doorID = Integer.valueOf(input[i]).intValue();

                double x = doors.get(doorID).getX();
                double y = doors.get(doorID).getY();
                int floor = doors.get(doorID).getmFloor();

                path.add(new Point(x, y, floor));
            }

            System.out.println(sID + " to " + tID);
            //if (sID == tID) System.out.println(path + "LLL");
            d2dPath[sID][tID] = path;
        }

        for (int i = 0; i <doors.size(); i++)
            for (int j = 0; j < doors.size(); j++) {
                if(d2dPath[i][j] == null) {
                    System.out.println(i + " AAAAAAAAAAA " + j);
                }
            }

        return d2dPath;
    }

    /**
     * get the distance of door to door
     * @return
     */
    public static double[][] getD2dDistance() {

        ArrayList<Door> doorList = ReadDoor.getDoor();

        d2dDistance = new double[doorList.size()][doorList.size()];

        File file = new File(System.getProperty("user.dir") + "/d2dMatrix.txt");
        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {
            String line = in.nextLine();
            String[] input = line.split(" ");

            int sID = Integer.valueOf(input[0]).intValue();
            int tID = Integer.valueOf(input[1]).intValue();
            double dis = Double.valueOf(input[2]).doubleValue();

            System.out.println(sID + " " + tID);
            d2dDistance[sID][tID] = dis;
        }

        return d2dDistance;
    }

    public static int flag = 0;

    /**
     * inialization
     */
    public static void init() {
        ArrayList<Door> doorList = ReadDoor.getDoor();

        File file = new File(System.getProperty("user.dir") + "/d2dMatrix.txt");
        File pathFile = new File(System.getProperty("user.dir") + "/d2dPathMatrix.txt");

        FileWriter fw = null;
        FileWriter fw2 = null;

        try {
            fw = new FileWriter(file);
            fw.flush();

            fw2 = new FileWriter(pathFile);
            fw2.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < doorList.size(); i++) {
            for (int j = 0; j < doorList.size(); j++) {
                if (flag == 1) {
                    System.out.println("error!");
                }

                if (i == j) {
                    try {
                        fw.write(new String("0 0 0\n"));
                        fw2.write(new String("0 0\n"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    continue;
                }

                List<Pair> path = new LinkedList<Pair>();
                double dis = PointToPoint.d2d(i, j, path);

                try {
                    fw.write(new String(i + " " + j + " " + dis + "\n"));
                    fw2.write(i + " " + j + " ");
                    if (path.size() == 0) {
                        flag = 1;
                        System.out.println("error!\n");
                    }
                    else if (path.size() == 1) {
                        fw2.write(i + " " + j + "\n");
                    }
                    else {
                        fw2.write(Integer.toString(i));
                        for (int k = 0; k < path.size(); k++) {
                            fw2.write(" " + path.get(k).getDoorID());
                        }
                        fw2.write("\n");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

                System.out.println("the d2d from " + i + " to " + j + " initialization");
            }
        }

        try {
            fw.close();
            fw2.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String arge[]) {
        init();
    }
}
