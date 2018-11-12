package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.indoorentities.*;

import javax.swing.plaf.synth.SynthTextAreaUI;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class PointToPoint {
    public static final double INF = 1e9;                          //the unreachable distance
    public static int staircase[] = {204, 206, 209, 211};           //the staircase
    public static int staircasePar[] = {128, 129, 130, 131};        //the par number touched by staircase
    public static final double stair2stair = 0;                     //the distance from staircase to staircase
    public static final int d2dNotPar = -512;                       //mean door to door not through par
    public static final int elevator = -256;                        //mean in staircase or elevator

    /**
     *
     * calculate the distance from door to door
     * the doorID1 and doorID2 must be adjacent
     *
     * @param doorID1
     * @param doorID2
     * @return the distance from door number doorID1 to door number doorID2
     */
    public static double Dis(int doorID1, int doorID2) {
        ArrayList<Door> doorList = ReadDoor.getDoor();

        double x1 = doorList.get(doorID1).getX();
        double y1 = doorList.get(doorID1).getY();
        double x2 = doorList.get(doorID2).getX();
        double y2 = doorList.get(doorID2).getY();

        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     *
     * judge a doorID is staircase or not
     *
     * @param doorID
     * @return
     */
    public static boolean isStaircase(int doorID) {
        doorID %= ReadDoor.perFloorDoorNum;
        for (int i = 0; i < staircase.length; i++) {
            if (doorID == staircase[i])
                return true;
        }

        return false;
    }

    /**
     *
     * calculate the distance from door to door
     * generate the path from sID to tID
     *
     * @param sID
     * @param tID
     * @param path
     * @return
     */
    public static double d2d(int sID, int tID, List<Pair> path) {
        if (path == null) {
            System.out.println("Error!");
            return 0;
        }

        if (sID == tID) {
            path.add(new Pair(sID, d2dNotPar));
            return 0;
        }

        ArrayList<Door> doorList = ReadDoor.getDoor();
        ArrayList<Par> parList = ReadPar.getPar();
        int doorNum = doorList.size();

        ArrayList<TargetDoor> mat[] = new ArrayList[doorNum];
        for (int i = 0; i < doorNum; i++) mat[i] = new ArrayList<TargetDoor>();
        boolean vis[] = new boolean[doorNum];
        TargetDoor dist[] = new TargetDoor[doorNum];

        for (Par par : parList) {
            List<Integer> tmp = par.getmDoors();
            for (int i = 0; i < tmp.size(); i++) {
                for (int j = 0; j < tmp.size(); j++) {
                    if (i == j) continue;
                    mat[tmp.get(i)].add(new TargetDoor(tmp.get(i), tmp.get(j), par.getmID(), Dis(tmp.get(i), tmp.get(j))));
                }
            }
        }

        SortedSet<TargetDoor> queue = new TreeSet<TargetDoor>();
        for(int i = 0; i < doorNum; i++) vis[i] = false;
        for(int i = 0; i < doorNum; i++) {
            dist[i] = new TargetDoor(-1, i, 0, INF);
        }

        dist[sID].setDistance(0);
        queue.add(dist[sID]);
        while(!queue.isEmpty()) {
            TargetDoor u = queue.first();
            queue.remove(queue.first());
            int curDoor = u.getDoorID();
            if(vis[curDoor]) continue;
            vis[curDoor] = true;
            dist[curDoor] = u;

            if(curDoor == tID) {
                List<Pair> path2 = new LinkedList<Pair>();
                while(u.getDoorID() != sID) {
                    path2.add(new Pair(u.getDoorID(), u.getParID()));
                    u = dist[u.getPreDoor()];
                }
                for (int i = path2.size() - 1; i >= 0; i--) {
                    path.add(path2.get(i));
                }

                return dist[tID].getDistance();
            }

            if (isStaircase(curDoor) == true) {
                if (curDoor + ReadDoor.perFloorDoorNum < doorNum) {
                    int nextDoor = curDoor + ReadDoor.perFloorDoorNum;
                    if (dist[nextDoor].getDistance() > dist[curDoor].getDistance() + stair2stair) {
                        queue.add(new TargetDoor(curDoor, nextDoor, elevator, dist[curDoor].getDistance() + stair2stair));
                    }
                }

                if(curDoor - ReadDoor.perFloorDoorNum >= 0) {
                    int nextDoor = curDoor - ReadDoor.perFloorDoorNum;
                    if (dist[nextDoor].getDistance() > dist[curDoor].getDistance() + stair2stair) {
                        queue.add(new TargetDoor(curDoor, nextDoor, elevator, dist[curDoor].getDistance() + stair2stair));
                    }
                }
            }

            for (TargetDoor next : mat[curDoor]) {
                int nextDoor = next.getDoorID();
                if (dist[nextDoor].getDistance() > dist[curDoor].getDistance() + next.getDistance()) {
                    queue.add(new TargetDoor(curDoor, nextDoor, next.getParID(), dist[curDoor].getDistance() + next.getDistance()));
                }
            }
        }

        return (double)INF;
    }

    /**
     *
     * calculate the distance from point s to point t
     *
     * @param s
     * @param t
     * @return the distance from s to t
     */
    public static double Dis(Point s, Point t) {
        double x1 = s.getX();
        double y1 = s.getY();
        double x2 = t.getX();
        double y2 = t.getY();

        return Math.sqrt((x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1));
    }

    /**
     *
     * calculate the distance from IdrObj to IdrObj
     * generate the path from s to tt
     *
     * @param s
     * @param t
     * @param path
     * @return
     */
    public static double move(IdrObj s, IdrObj t, List<Pair> path) {
        if (s.getCurPar().getmID() == t.getCurPar().getmID()) {
            path.clear();
            return Dis(s.getmTruePos(), t.getmTruePos());
        }

        ArrayList<Door> doorList = ReadDoor.getDoor();
        Par sPar = s.getCurPar();
        Par tPar = t.getCurPar();

        path.clear();
        double dis = (double)INF;
        for (int sDoorID : sPar.getmDoors()) {
            for (int tDoorID : tPar.getmDoors()) {
                double s2d = Dis(s.getmTruePos(), doorList.get(sDoorID));
                double t2d = Dis(t.getmTruePos(), doorList.get(tDoorID));

                List<Pair> tmpPath = new LinkedList<Pair>();
                double tmpDis = s2d + t2d + d2d(sDoorID, tDoorID, tmpPath);
                if(tmpDis < dis) {
                    dis = tmpDis;
                    //System.out.println(dis + " " +  sDoorID + " " + tDoorID);

                    path.clear();
                    if (sDoorID == tDoorID) {
                        path.add(new Pair(sDoorID, tPar.getmID()));
                    }
                    else {
                        int lastDoor = sDoorID;
                        for (Pair pair : tmpPath) {
                            path.add(new Pair(lastDoor, pair.getParID()));
                            lastDoor = pair.getDoorID();
                        }
                        path.add(new Pair(lastDoor, tPar.getmID()));
                    }
                }
            }
        }

        return dis;
    }

    /*
    public static IdrObj createIdrObj1() {
        int flag = 0;

        ArrayList<Par> parList = ReadPar.getPar();
        Random r = new Random();
        int parID;

        if(flag == 0) {
            parID = 31;
            flag++;
        }
        else {
            parID = 140;
        }

        Par par = parList.get(parID);
        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        double x = r.nextDouble() * (x2 - x1) + x1;
        double y = r.nextDouble() * (y2 - y1) + y1;

        return new IdrObj(0, new Point(x, y), par);
    }

    public static IdrObj createIdrObj2() {
        int flag = 0;

        ArrayList<Par> parList = ReadPar.getPar();
        Random r = new Random();
        int parID;

        if(flag == 0) {
            parID = 135;
            flag++;
        }
        else {
            parID = 140;
        }

        Par par = parList.get(parID);
        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        double x = r.nextDouble() * (x2 - x1) + x1;
        double y = r.nextDouble() * (y2 - y1) + y1;

        return new IdrObj(0, new Point(x, y), par);
    }

    public static void debug(List<Pair> path) {
        if (path.size() == 0) System.out.println("Start and End is in one par");

        if (path.size() == 1 && path.get(0).getParID() == PointToPoint.d2dNotPar)
            System.out.println("Start to End by doorID");

        for (Pair pair : path) {
            String parID = new String();
            if(pair.getParID() == PointToPoint.elevator) {
                System.out.println("from doorID: " + pair.getDoorID() + " in elevator");
            }
            else {
                System.out.println("from doorID: " + pair.getDoorID() + " to parID: " + pair.getParID());
            }
        }
    }
*/

    /**
     *
     * get the par number of staircase
     *
     * @param doorID the doorID must be staircase
     * @return
     */
    public static int getStaircaseParID(int doorID) {
        int floor = doorID / ReadDoor.perFloorDoorNum;
        doorID %= ReadDoor.perFloorDoorNum;
        int parID = 0;
        for (int i = 0; i < staircase.length; i++) {
            if (doorID == staircase[i]) {
                parID = staircasePar[i] + floor * ReadPar.perFloorParNum;
            }
        }

        return parID;
    }

    /*
    public static void test(Dest s, Dest t, List<Pair> path) {
        ArrayList<Par> parList  = ReadPar.getPar();
        ArrayList<Door> doorList = ReadDoor.getDoor();
        ArrayList<Dest> destList = ReadDest.getDest();

        if(path.size() == 0) {
            if(s.getCurPar() == t.getCurPar()) System.out.println("ok");
            else System.out.println("error!");
        }
        else if(path.size() == 1) {
            if(path.get(0).getParID() == -256) {
                return;
            }

            int flag = 0;

            int doorID = path.get(0).getDoorID();
            Door door = doorList.get(doorID);
            List<Integer> par = door.getmPartitions();
            for (int i = 0; i < par.size(); i++) {
                if (par.get(i) == s.getCurPar()) flag++;
                else if(par.get(i) == t.getCurPar()) flag++;
            }

            if(flag == 2) {
                System.out.println("ok");
            }
            else {
                System.out.println("error!");
            }
        }
        else {
            int lastpar = s.getCurPar();
            for (int i = 0; i < path.size(); i++) {
                int flag = 0;

                int doorID = path.get(i).getDoorID();
                int parID = path.get(i).getParID();
                Door door = doorList.get(doorID);

                List<Integer> tpar = door.getmPartitions();

                for (int j = 0; j < tpar.size(); j++) {
                    if(tpar.get(j) == lastpar) {
                        flag++;
                    }
                    else if(tpar.get(j) == parID) {
                        flag++;
                    }
                }

                if(flag == 2) {
                    System.out.println("ok");
                }
                else {
                    System.out.println("error!");
                }

                lastpar = parID;
            }

            if(lastpar == t.getCurPar()) {
                System.out.println("ok");
            }
            else {
                System.out.println("error");
            }

        }
    }
    */

    public static void printPath(List<Pair> dis) {
        System.out.println(dis);
    }

    public static void main(String arge[]) {

        List<Pair> dis[][][] = ReadDest2Dest.getDest2Dest();

        int flag = 0;

        for (int j = 0; j < ReadDest.destNum; j++)
            for (int k = 0; k < ReadDest.destNum; k++) {
                if (j == k) continue;
                String s[] = new String[5];
                for (int i = 0; i < 5; i++) {
                    s[i] = dis[i][j][k].toString();
                }

                for (int i1 = 0; i1 < 5; i1++)
                    for (int i2 = i1+1; i2 < 5; i2++) {
                        if(s[i1].compareTo(s[i2]) == 0) {
                            flag++;
                        }
                    }

            }

            System.out.println(flag);


        /*
        ArrayList<Door> doorList = ReadDoor.getDoor();
        Door door = doorList.get(211);

        System.out.println(door.getX() + " " + door.getY());

        */

        /*
        File file = new File(DataGen.outputPath + "/Trajectories_1000.txt");
        Scanner cin = null;
        try {
            cin = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int cnt = 70;
        while(cin.hasNext()) {
            if (cnt > 0) {
                String line = cin.nextLine();
                System.out.println(line);
            }

            cnt --;
        }
*/

/*
        List<Pair>[][][] dis = ReadDest2Dest.getDest2Dest();
        ArrayList<Dest> destList = ReadDest.getDest();

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 5; j++)
                for(int k = 0; k < 4; k++) {
                    System.out.println("par: " + destList.get(j).getCurPar() + " to " + "par: " + destList.get(k).getCurPar());
                    debug(dis[i][j][k]);
                }
*/

/*
        ArrayList<Dest> destList = ReadDest.getDest();
        ArrayList<Par> parList = ReadPar.getPar();

        Dest dest0 = destList.get(0);
        Dest dest1 = destList.get(1);

        IdrObj s = new IdrObj(0, dest0.getCenterPoint(), parList.get(dest0.getCurPar()));
        IdrObj t = new IdrObj(0, dest1.getCenterPoint(), parList.get(dest1.getCurPar()));


        PointToPoint p2p = new PointToPoint();
        ArrayList<Pair> path = new ArrayList<Pair>();
        //IdrObj s = createIdrObj1();
        //IdrObj t = createIdrObj2();
        //path.add(new Pair(0, 0));
        double distance = p2p.move(s, t, path);

        System.out.println(s.getmTruePos().getX() + " " + s.getmTruePos().getY() + " " + s.getCurPar().getmID());
        System.out.println(t.getmTruePos().getX() + " " + t.getmTruePos().getY() + " " + t.getCurPar().getmID());
        System.out.println("distance : " + distance);
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getDoorID() + " " + path.get(i).getParID());
        }
*/


/*
        PointToPoint p2p = new PointToPoint();
        ArrayList<Pair> path = new ArrayList<Pair>();
        double distance = p2p.d2d(154, 1875, path);

        System.out.println("distance : " + distance);
        for (int i = 0; i < path.size(); i++) {
            System.out.println(path.get(i).getParID() + "->" + path.get(i).getDoorID());
        }
*/
    }

}
