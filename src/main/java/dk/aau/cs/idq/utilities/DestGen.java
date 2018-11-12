package dk.aau.cs.idq.utilities;

import dk.aau.cs.idq.indoorentities.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class DestGen {
    public static final double inDoorParameter = 0.9;       //the probability of generated destination in door
    public static final double destNumParameter = 0.3;      //the rate of destination number
    public static final int pathNum = 5;                    //the amount of path
    public static int staircase[] = {204, 206, 209, 211};   //the staircase

    List<Pair> dis[][][] = null;                            //the path from destination to destination

    /**
     * Generate destination and write it to disk
     *
     */
    public void getDest() {
        ArrayList<Par> parList = ReadPar.getPar();
        ArrayList<Dest> destList = new ArrayList<Dest>();

        int parNum = (int)parList.size();
        int destNum = (int)(parNum * destNumParameter);
        int destRoomNum = (int)(destNum * inDoorParameter);
        int destNotRoomNum = destNum - destRoomNum;

        Random r = new Random();
        int vis[] = new int[parNum];

        for(int i = 0; i < parNum; i++) {
            vis[i] = 0;
        }

        for (int i = 0, cntDestRoom = 0, cntDestNotRoom = 0; i < destNum; ) {
            int id = (int)(parNum * r.nextDouble());
            //if(vis[id] != 0) continue;
            if(parList.get(id).getmType() == RoomType.ROOM) {
                if(cntDestRoom < destRoomNum) {
                    destList.add(creatDest(parList.get(id), i));
                    vis[id] = 1;
                    i++;
                    cntDestRoom++;
                }
            }
            else {
                if(cntDestNotRoom < destNotRoomNum) {
                    destList.add(creatDest(parList.get(id), i));
                    vis[id] = 1;
                    i++;
                    cntDestNotRoom++;
                }
            }
        }

        Write(destList);
    }

    /*
    public boolean IsStaircase(int doorID) {
        doorID %= ReadDoor.perFloorDoorNum;
        for (int i = 0; i < staircase.length; i++) {
            if (doorID == staircase[i])
                return true;
        }

        return false;
    }
    */

/*
    public void test(List<Pair> dis[][], List<Dest> destList) {
        int a = 0, b = 0, flag = 0;
        for (int i = 0; i < destList.size(); i++) {
            if(destList.get(i).getmFloor() == 0) {
                if(flag == 0) {
                    a = i;
                    flag++;
                }
                else if(flag == 1) {
                    b = i;
                    flag++;
                }
                else break;
            }
        }

        System.out.println("from parID: " + destList.get(a).getCurPar() + " to parID: " +
                destList.get(b).getCurPar());
        for (Pair i : dis[a][b]) {
            System.out.println("through doorID:" + i.getDoorID() + " to parID:" + i.getParID());
        }
    }
*/

    /**
     * write path from memory to disk
     *
     * @param dis the path
     */
    public void writeDest2Dest(List<Pair> dis[][][]) {
        File dest2DestFile = new File(outputPath + "/Dest2Dest.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(dest2DestFile);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < pathNum; i++) {
            for (int j = 0; j < ReadDest.destNum; j++) {
                for (int k = 0; k < ReadDest.destNum; k++) {
                    try {
                        fw.write(j + " " + k);
                        for (Pair pair : dis[i][j][k]) {
                            fw.write(" " + pair.getDoorID() + " " + pair.getParID());
                        }
                        fw.write("\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * write each probability of destination to destination to disk
     *
     */
    public void writeDest2DestProb() {
        File dest2DestProbFile = new File(outputPath + "/Dest2DestProb.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(dest2DestProbFile);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();
        for (int i = 0; i < ReadDest.destNum; i++) {
            for (int j = 0; j < ReadDest.destNum; j++) {

                while (true) {
                    double a = r.nextDouble();
                    double b = r.nextDouble();
                    if (a + b < 1) {
                        double t1 = r.nextDouble();
                        double t2 = r.nextDouble();
                        try {
                            fw.write(i + " " + j + " " + a * t1 + " " + a * (1 - t1) +
                                    " " + b * t2  + " " + b * (1 - t2) + " " + (1 - a - b) + "\n");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        break;
                    }
                }

            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generater the path's matrix of destination to destination
     *
     */
    public void Dest2Dest() {
        ArrayList<Dest> destList = ReadDest.getDest();
        ArrayList<Par> parList = ReadPar.getPar();
        dis = new List[pathNum][ReadDest.destNum][ReadDest.destNum];
        int destNum = destList.size();

        int idrObjID = 0;
        for (Dest sDest : destList) {
            for (Dest tDest : destList) {
                System.out.println(sDest.getmDestID() + " " + tDest.getmDestID());
                dis[0][sDest.getmDestID()][tDest.getmDestID()] = new LinkedList<Pair>();
                IdrObj s = new IdrObj(idrObjID++, sDest.getCenterPoint(), parList.get(sDest.getCurPar()));
                IdrObj t = new IdrObj(idrObjID++, tDest.getCenterPoint(), parList.get(tDest.getCurPar()));
                PointToPoint.move(s, t, dis[0][sDest.getmDestID()][tDest.getmDestID()]);
            }
        }

        for(int k = 1; k < pathNum; k++) {
            for (Dest sDest : destList) {
                for (Dest tDest : destList) {
                    System.out.println(sDest.getmDestID() + " " + tDest.getmDestID());
                    dis[k][sDest.getmDestID()][tDest.getmDestID()] = new LinkedList<Pair>();
                    IdrObj s = new IdrObj(idrObjID++, sDest.getCenterPoint(), parList.get(sDest.getCurPar()));
                    IdrObj t = new IdrObj(idrObjID++, tDest.getCenterPoint(), parList.get(tDest.getCurPar()));
                    IdrObj mid = creatIdrObj(s, t, idrObjID++);

                    List<Pair> path2 = new LinkedList<Pair>();

                    PointToPoint.move(s, mid, dis[k][sDest.getmDestID()][tDest.getmDestID()]);
                    PointToPoint.move(mid, t, path2);

                    for (Pair pair : path2) {
                        dis[k][sDest.getmDestID()][tDest.getmDestID()].add(pair);
                    }
                }
            }
        }

        writeDest2Dest(dis);
    }

    /**
     * Generate the point in par
     *
     * @param par
     * @return the point in par
     */
    public Point creatPoint(Par par) {
        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        Random r = new Random();
        double x = x1 + (x2 - x1) * r.nextDouble();
        double y = y1 + (y2 - y1) * r.nextDouble();

        return new Point(x, y);
    }

    /**
     * create IdrObj between s and t
     *
     * @param s
     * @param t
     * @param idrObjID
     * @return the IdrObj between s and t
     */
    public IdrObj creatIdrObj(IdrObj s, IdrObj t, int idrObjID) {
        int parID1 = s.getCurPar().getmID();
        int parID2 = t.getCurPar().getmID();
        Random r = new Random();
        if (parID1 > parID2) {
            int tmp = parID1;
            parID1 = parID2;
            parID2 = tmp;
        }

        int parID = parID1 + (int)((parID2 - parID1) * r.nextDouble());
        ArrayList<Par> parList = ReadPar.getPar();
        Point point = creatPoint(parList.get(parID));

        return new IdrObj(idrObjID, point, parList.get(parID));
    }

    /**
     *
     * write destination to disk
     *
     * @param destList
     */
    public void Write(List<Dest> destList) {
        File destFile = new File(outputPath + "/Dest.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(destFile);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < destList.size(); i++) {
            try {
                fw.write(destList.get(i).toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * create destination
     *
     * @param par
     * @param mID
     * @return the destination
     */
    public Dest creatDest(Par par, int mID) {
        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        double Dis = 0.0, xx1 = 0, yy1 = 0, xx2 = 0, yy2 = 0;
        while(Dis < 0.02) {
            Random r = new Random();
            xx1 = (x2 - x1) * r.nextDouble() + x1;
            xx2 = (x2 - x1) * r.nextDouble() + x1;
            yy1 = (y2 - y1) * r.nextDouble() + y1;
            yy2 = (y2 - y1) * r.nextDouble() + y1;
            Dis = (xx2 - xx1) * (xx2 - xx1) + (yy2 - yy1) * (yy2 - yy1);
        }
        return new Dest(Math.min(xx1, xx2), Math.max(xx1, xx2), Math.min(yy1, yy2), Math.max(yy1, yy2), par.getmFloor(), mID, par.getmID());
    }

    public static void main(String arge[]) throws IOException {
        DestGen destGen = new DestGen();
        destGen.Dest2Dest();
        destGen.writeDest2DestProb();
    }

    /*
    public void DestToDest(List<Dest> destList) {
        ArrayList<Door> door = new ArrayList<Door>();
        ArrayList<Par> par = new ArrayList<Par>();
        door = ReadDoor.getDoor();
        par = ReadPar.getPar();
        int destNum = destList.size();
        int doorNum = door.size();
        int parNum = par.size();

        List<Pair> dis[][] = new List[destNum][destNum];
        ArrayList<Integer> prePar = new ArrayList<Integer>();
        ArrayList<Integer> preDoor = new ArrayList<Integer>();
        Queue<Integer> queue = new LinkedList<Integer>();
        for (int i = 0; i < parNum; i++) prePar.add(-1);
        for (int i = 0; i < parNum; i++) preDoor.add(-1);

        for (int sID = 0; sID < destNum; sID++) {
            Dest s = destList.get(sID);
            for (int i = 0; i < parNum; i++) prePar.set(i, -1);
            for (int i = 0; i < parNum; i++) preDoor.set(i, -1);

            queue.add(s.getCurPar());
            prePar.set(s.getCurPar(), -2);
            while(queue.size() != 0) {
                int u = queue.remove();
                for (int i = 0; i < par.get(u).getmDoors().size(); i++) {
                    int doorID = par.get(u).getmDoors().get(i);

                    if (IsStaircase(doorID) == true) {
                        if (u + ReadPar.perFloorParNum < par.size()) {
                            int next = u + ReadPar.perFloorParNum;
                            if (prePar.get(next) == -1) {
                                prePar.set(next, u);
                                preDoor.set(next, doorID);
                                queue.add(next);
                            }
                        }

                        if(u - ReadPar.perFloorParNum >= 0) {
                            int next = u - ReadPar.perFloorParNum;
                            if (prePar.get(next) == -1) {
                                prePar.set(next, u);
                                preDoor.set(next, doorID);
                                queue.add(next);
                            }
                        }
                    }

                    for (int j = 0; j < door.get(doorID).getmPartitions().size(); j++) {
                        int next = door.get(doorID).getmPartitions().get(j);
                        if (prePar.get(next) == -1) {
                            prePar.set(next, u);
                            preDoor.set(next, doorID);
                            queue.add(next);
                        }
                    }
                }
            }

            for (int i = 0; i < destList.size(); i++) {
                int parID = destList.get(i).getCurPar();
                if (parID == s.getCurPar()) continue;

                List<Pair> tmp = new LinkedList<>();
                List<Pair> tmp2 = new LinkedList<>();
                while (prePar.get(parID) != -2) {
                    tmp.add(new Pair(preDoor.get(parID), parID));
                    parID = prePar.get(parID);
                }
                for (int j = tmp.size() - 1; j >= 0; j--) {
                    tmp2.add(tmp.get(j));
                }
                dis[sID][i] = tmp2;
            }
        }
    }
    */
}
