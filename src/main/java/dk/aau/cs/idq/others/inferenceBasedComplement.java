package dk.aau.cs.idq.others;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.others.hash;
import dk.aau.cs.idq.secondTest.outputToJson;
import dk.aau.cs.idq.secondTest.snptTest;
import dk.aau.cs.idq.update.Msemantic;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class inferenceBasedComplement {

    public static final double INF = 1e9;

    public static final double lenLimit = 30;

    public static double distR2R[][] = null;

    public static double distL[][] = null;

    public static ArrayList<Integer> link[] = null;

    public static final int stairPatID[] = {128, 129, 130, 131};

    public static final int perPar = 141;

    public boolean vis[] = null;

    public double spaceDis(double x1, double y1, double x2, double y2) {
        double ans = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        return Math.sqrt(ans);
    }

    public double getDisToStair(Par s) {
        double ans = 0;

        if (s.getmDoors().size() != 2) {
            System.out.println("error");
        }
        int doorID = s.getmDoors().get(0);

        ArrayList<Door> doors = ReadDoor.getDoor();

        double x = doors.get(doorID).getX();
        double y = doors.get(doorID).getY();

        double x1 = s.getX1();
        double y1 = s.getY1();
        double x2 = s.getX2();
        double y2 = s.getY2();

        ans = Math.max(ans, spaceDis(x, y, x1, y1));
        ans = Math.max(ans, spaceDis(x, y, x1, y2));
        ans = Math.max(ans, spaceDis(x, y, x2, y1));
        ans = Math.max(ans, spaceDis(x, y, x2, y2));

        return ans;
    }

    public double GRD(Par s, Par t) {
        double ans = 0;

        if (s.getmID() == t.getmID()) {
            return 0;
        }

        int flag = 0;
        for (int i = 0; i < stairPatID.length; i++) {
            if (s.getmID() % perPar == stairPatID[i]) {
                if ((t.getmID() == s.getmID() + perPar) || (t.getmID() == s.getmID() - perPar)) {
                    return getDisToStair(s);
                }
            }
        }

        ArrayList<Par> pars = ReadPar.getPar();
        ArrayList<Door> doors = ReadDoor.getDoor();

        for (Integer sID : s.getmDoors()) {
            for (Integer tID : t.getmDoors()) {
                if (sID == tID) {

                    double x = doors.get(sID).getX();
                    double y = doors.get(sID).getY();

                    double x1 = pars.get(sID).getX1();
                    double y1 = pars.get(sID).getY1();
                    double x2 = pars.get(sID).getX2();
                    double y2 = pars.get(tID).getY2();

                    ans = Math.max(ans, spaceDis(x, y, x1, y1));
                    ans = Math.max(ans, spaceDis(x, y, x1, y2));
                    ans = Math.max(ans, spaceDis(x, y, x2, y1));
                    ans = Math.max(ans, spaceDis(x, y, x2, y2));
                }
            }
        }

        return ans;
    }

    public double lengthOfPath(ArrayList<Integer> path) {
        double ans = 0;

        ArrayList<Par> pars = ReadPar.getPar();

        for (int i = 0; i < path.size() - 1; i++) {
            Par sPar = pars.get(path.get(i));
            Par tPar = pars.get(path.get(i+1));

            ans += GRD(sPar, tPar);
        }

        return ans;
    }

    public void dfs(int u, int tParID, double len, ArrayList<List<Integer>> ans, ArrayList<Integer> res) {

        System.out.println(u + " " + distL[u][tParID] + " " + len + "\n");
        if (distL[u][tParID] + len > lenLimit) {
            return;
        }

        if (u == tParID) {

            List<Integer> tmp = new LinkedList<Integer>();

            for (int i = 0; i < res.size(); i++) {
                tmp.add(res.get(i));
            }
            ans.add(tmp);

            return;
        }

        for (int i = 0; i < link[u].size(); i++) {
            int v = link[u].get(i);

            if (vis[v] == false) {
                vis[v] = true;

                res.add(v);

                dfs(v, tParID, len + distR2R[u][v], ans, res);

                res.remove(res.size() - 1);

                vis[v] = false;
            }
        }
    }

    public ArrayList<List<Integer>> Astar(int sPar, int tPar) {

        ArrayList<Par> pars = ReadPar.getPar();

        ArrayList<List<Integer>> ans = new ArrayList<>();
        ArrayList<Integer> pathAns = new ArrayList<Integer>();

        vis[sPar] = true;
        dfs(sPar, tPar, 0, ans, pathAns);
        vis[sPar] = true;

        return ans;
    }

    public void init() {

        ArrayList<Par> pars = ReadPar.getPar();

        int n = pars.size();

        distR2R = new double[n][n];
        distL = new double[n][n];
        link = new ArrayList[n];
        vis = new boolean[n];

        File regionDist = new File(System.getProperty("user.dir") + "/regionDist.txt");

        File regionDistLimit = new File(System.getProperty("user.dir") + "/regionDistLimit.txt");

        Scanner regionDistIn = null;

        Scanner regionDistLimitIn = null;

        try {
            regionDistIn = new Scanner(regionDist);
            regionDistLimitIn = new Scanner(regionDistLimit);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(regionDistIn.hasNext()) {
            int s = regionDistIn.nextInt();
            int t = regionDistIn.nextInt();
            double dist = regionDistIn.nextDouble();

            distR2R[s][t] = dist;
        }

        while(regionDistLimitIn.hasNext()) {
            int s = regionDistLimitIn.nextInt();
            int t = regionDistLimitIn.nextInt();
            double dist = regionDistLimitIn.nextDouble();

            distL[s][t] = dist;
        }

        for (int i = 0; i < n; i++) {
            link[i] = new ArrayList<Integer>();
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (Math.abs(distR2R[i][j] - INF) > 1e-3) {
                    link[i].add(j);
                }
            }
        }

    }

    public void initMatrix() {
        ArrayList<Par> pars = ReadPar.getPar();
        ArrayList<Door> doors = ReadDoor.getDoor();

        int n = pars.size();

        distR2R = new double[n][n];
        distL = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distR2R[i][j] = INF;
            }
        }

        for (int i = 0; i < doors.size(); i++) {
            for (Integer sParID : doors.get(i).getmPartitions()) {
                for (Integer tParID : doors.get(i).getmPartitions()) {
                    Par sPar = pars.get(sParID);
                    Par tPar = pars.get(tParID);
                    distR2R[sParID][tParID] = GRD(sPar, tPar);
                }
            }
        }

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                distL[i][j] = distR2R[i][j];
            }
        }

        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    distL[i][j] = Math.min(distL[i][j], distL[i][k] + distL[k][j]);
                }
            }

            System.out.println("init " + k);
        }

        File regionDist = new File(System.getProperty("user.dir") + "/regionDist.txt");

        File regionDistLimit = new File(System.getProperty("user.dir") + "/regionDistLimit.txt");

        FileWriter regionDistfw = null;
        FileWriter regionDistLimitfw = null;

        try {
            regionDistfw = new FileWriter(regionDist);
            regionDistLimitfw = new FileWriter(regionDistLimit);

            for (int i = 0; i < n; i++) {
                for (int j = 0; j < n; j++) {
                    regionDistfw.write(i + " " + j + " " + distR2R[i][j] + "\n");
                    regionDistLimitfw.write(i + " " + j + " " + distL[i][j] + "\n");
                }
            }

            regionDistfw.close();
            regionDistLimitfw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BitSet tranToBitSet(List<Integer> path) {

        BitSet bitSet = new BitSet();

        for (int i = 0; i < path.size(); i++) {
            bitSet.set(path.get(i));
        }

        return bitSet;
    }

    public ArrayList<List<Integer>> findSubSet(BitSet bitSet, ArrayList<List<Integer>> P) {
        ArrayList<List<Integer>> ans = new ArrayList<>();

        for (int i = 0; i < P.size(); i++) {
            BitSet t = tranToBitSet(P.get(i));
            BitSet tmp = (BitSet) bitSet.clone();
            tmp.and(t);

            if (tmp.hashCode() == bitSet.hashCode()) {
                ans.add(P.get(i));
            }
        }

        return ans;
    }

    public double getPathLength(int startRegion, List<Integer> path, int endRegion) {

        ArrayList<Par> pars = ReadPar.getPar();

        double ans = 0;

        int last = startRegion;
        for (int i = 0; i < path.size(); i++) {
            Par sPar = pars.get(last);
            Par tPar = pars.get(path.get(i));

            ans += GRD(sPar, tPar);
        }

        Par sPar = pars.get(last);
        Par tPar = pars.get(endRegion);

        ans += GRD(sPar, tPar);

        return ans;
    }

    public void debugAstart(ArrayList<List<Integer>> P) {
        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < P.get(i).size(); j++) {
                System.out.print(P.get(i).get(j) + " ");
            }
            System.out.println();
        }
    }

    public void constructForOnePair(ArrayList<Msemantic> sems, int startRegion, int endRegion) {

        ArrayList<Par> pars = ReadPar.getPar();

        int n = pars.size();

        ArrayList<List<Integer>> P = Astar(startRegion, endRegion);

        System.out.println("AAAAA");
        debugAstart(P);

        hash Hpt = new hash();

        for (int i = 0; i < sems.size(); i++) {
            for (int j = i+1; j < sems.size(); j++) {
                if (sems.get(i).getParID() == startRegion && sems.get(j).getParID() == endRegion) {
                    List<Integer> tmp = new LinkedList<Integer>();
                    for (int k = i+1; k < j; k++) {
                        tmp.add(sems.get(k).getParID());
                    }
                    Hpt.put(tmp, 1);
                }
            }
        }

        Map<Integer, Double> Hs = new HashMap<>();
        ArrayList<Integer> out[] = new ArrayList[n];

        for (Map.Entry<BitSet, Integer> entry : Hpt.map.entrySet()) {
            ArrayList<List<Integer>> Pt = findSubSet(entry.getKey(), P);

            int amount = Pt.size();

            double L[] = new double[amount];
            double sum = 0;

            for (int i = 0; i < amount; i++) {
                L[i] = getPathLength(startRegion, Pt.get(i), endRegion);
                sum += L[i];
            }

            for (int i = 0; i < amount; i++) {
                double w = sum / L[i];
                for (int j = 0; j < Pt.get(i).size() - 1; j++) {
                    int rk = Pt.get(i).get(j);
                    int rk2 = Pt.get(i).get(j+1);

                    out[rk].add(rk2);
                    if (Hs.get(rk * n + rk2) == null) {
                        Hs.put(rk * n + rk2, entry.getValue() * w);
                    }
                    else {
                        double t = Hs.get(rk * n + rk2);
                        Hs.put(rk * n + rk2, entry.getValue() * w + t);
                    }
                }
            }
        }

        Map<Integer, Double> TP = new HashMap<>();

        for (int i = 0; i < P.size(); i++) {
            for (int j = 0; j < P.get(i).size(); j++) {
                int r = P.get(i).get(j);

                double sum = 0;
                for (int k = 0; k < out[r].size(); k++) {
                    int v = out[r].get(k);
                    sum += Hs.get(r * n + v);
                }

                for (int k = 0; k < out[r].size(); k++) {
                    int v = out[r].get(k);

                    double res = Hs.get(r * n + v) / sum;

                    TP.put(r * n + v, res);
                }

                if (Math.abs(sum) < 1e-3) continue;
            }
        }
    }

    public ArrayList<Msemantic> readMsemantic(File file) {

        ArrayList<Msemantic> ans = new ArrayList<>();

        Scanner in = null;
        try {
            in = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(in.hasNext()) {
            int personID = in.nextInt();
            int startTime = in.nextInt();
            int endTime = in.nextInt();
            int isStay = in.nextInt();
            int parID = in.nextInt();

            Msemantic msemantic = new Msemantic(personID, startTime, endTime, isStay, parID);

            ans.add(msemantic);
        }

        return ans;
    }

    public void solve() {

        init();

        for (int i = 300; i < 301; i++) {
            File file = new File(System.getProperty("user.dir") + "/predictSemantics/person" + i + ".txt");

            ArrayList<Msemantic> msems = readMsemantic(file);

            constructForOnePair(msems, 1052, 1122);
        }
    }

    public static void main(String args[]) {
/*
        inferenceBasedComplement inferenceBasedComplement = new inferenceBasedComplement();
        //inferenceBasedComplement.initMatrix();
        inferenceBasedComplement.solve();
*/
        snptTest snptTest = new snptTest();
        snptTest.mergeConnection();

        outputToJson outputToJson = new outputToJson();
        outputToJson.output();

/*
        LRmodel lRmodel = new LRmodel();
        lRmodel.writeLabel();
*/

/*
        snptTest snptTest = new snptTest();
        snptTest.init();
        snptTest.test();
        snptTest.mergePassBy();
*/
/*
        snptTest snptTest = new snptTest();
        snptTest.init();
        snptTest.test();

        snptTest.mergePassBy();
        snptTest.mergeStay();
        snptTest.completion();
        snptTest.mergeConnection();

        outputToJson outputToJson = new outputToJson();
        outputToJson.output();
  */
    }

}
