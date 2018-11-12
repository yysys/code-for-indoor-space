package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.clean.Record;
import dk.aau.cs.idq.clean.p2pDis;
import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.IdrObj;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Point;
import dk.aau.cs.idq.semantics.Snpt;
import dk.aau.cs.idq.semantics.Splitting;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;
import dk.aau.cs.idq.utilities.ReadRecord;

import javax.print.attribute.standard.MediaSize;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class DataSet {

    public static int trainingSetStart = 0;
    public static int trainingSetEnd = 300;
    public static int testingSetStart = 300;
    public static int testingSetEnd = 10000;

    /**
     * judge a snpts is dense or not
     * @param snpts
     * @return
     */
    public int[] isDense(List<Snpt> snpts) {

        int[] dense = new int[snpts.size()];

        for (int i = 0; i < snpts.size(); i++) {
            dense[i] = snpts.get(i).isDense() ? 1 : 0;
        }

        return dense;
    }

    /**
     * get the id
     * @param records
     * @param snpts
     * @return
     */
    public int[] getIDX(ArrayList<Record> records, List<Snpt> snpts) {
        int ans[] = new int[records.size()];

        for (int i = 0, j = 0; i < records.size(); i++) {
            while(j+1 < snpts.size() && records.get(i).getTime() > snpts.get(j).getEndTime()) {
                j++;
            }

            ans[i] = j;
        }

        return ans;
    }

    /**
     * get the uniqueID
     * @param records
     * @param idx
     * @param n
     * @return
     */
    public int[] getUniqueID(ArrayList<Record> records, int[] idx, int n) {

        int ans[] = new int[n];

        HashMap<Integer, Integer> map[] = new HashMap[n];

        for (int i = 0; i < n; i++) {
            map[i] = new HashMap<Integer, Integer>();
        }

        for (int i = 0; i < records.size(); i++) {
            map[idx[i]].put(records.get(i).getParID(), 1);
        }

        for (int i = 0; i < n; i++) {
            ans[i] = map[i].size();
        }

        return ans;
    }

    /**
     * get the cnt feature
     * @param records
     * @param idx
     * @param n
     * @return
     */
    public int[] getCnt(ArrayList<Record> records, int[] idx, int n) {
        int cnt[] = new int[n];

        for (int i = 0; i < records.size(); i++) {
            cnt[idx[i]]++;
        }

        return cnt;
    }

    /**
     * get the speed feature
     * @param s
     * @param t
     * @param deltaT
     * @return
     */
    public double calcSpeed(Point s, Point t, double deltaT) {
        IdrObj ss = new IdrObj(0, s);
        IdrObj tt = new IdrObj(0, t);
        double dis = p2pDis.getP2pDis(ss, tt);

        return dis / deltaT;
    }

    /**
     * get the speed feature
     * @param points
     * @param times
     * @return
     */
    public double[] getSpeed3(List<Point> points, List<Integer> times) {

        double speed[] = new double[3];

        if (points.size() <= 1) {
            for (int i = 0; i < speed.length; i++) {
                speed[i] = 0;
            }
            return speed;
        }

        double minSpeed = 1000;
        double maxSpeed = -1;
        double avgSpeed = 0;


        for (int i = 1; i < points.size(); i++) {
            double deltaT = times.get(i) - times.get(i-1);
            double tmp = calcSpeed(points.get(i-1), points.get(i), deltaT);

            minSpeed = Math.min(minSpeed, tmp);
            maxSpeed = Math.max(maxSpeed, tmp);
            avgSpeed = avgSpeed + tmp;
        }
        avgSpeed /= points.size() - 1;

        speed[0] = minSpeed;
        speed[1] = maxSpeed;
        speed[2] = avgSpeed;

        return speed;
    }

    public double[][] getSpeed(ArrayList<Record> records, int[] idx, int n) {
        double ans[][] = new double[3][n];

        int id = 0;
        List<Point> points = new LinkedList<Point>();
        List<Integer> times = new LinkedList<Integer>();

        for (int i = 0; i < records.size(); i++) {
            if (i == 0) {
                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                times.add(records.get(i).getTime());
                continue;
            }

            if (idx[i] == id) {
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                times.add(records.get(i).getTime());
            }
            else {
                double tmp[] = getSpeed3(points, times);

                ans[0][id] = tmp[0];
                ans[1][id] = tmp[1];
                ans[2][id] = tmp[2];

                points.clear();
                times.clear();

                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                times.add(records.get(i).getTime());
            }
        }

        double tmp[] = getSpeed3(points, times);

        ans[0][id] = tmp[0];
        ans[1][id] = tmp[1];
        ans[2][id] = tmp[2];

        return ans;
    }

    public Point getVector(Point s, Point t) {
        double x1 = s.getX();
        double y1 = s.getY();

        double x2 = t.getX();
        double y2 = t.getY();

        return new Point(x2 - x1, y2 - y1, s.getmFloor());
    }

    public double calcAngle(Point a, Point b) {


        double res = (a.getX() * b.getX() + a.getY() * b.getY());
        res /= (Math.sqrt(a.getX() * a.getX() + a.getY() * a.getY()) * Math.sqrt(b.getX() * b.getX() + b.getY() * b.getY()));

//        System.out.println(res);
        res = Math.min(res, 0.9999999);
        res = Math.max(res, -0.9999999);

        res = Math.acos(res);
/*
        System.out.println(res);
        System.out.println((a.getX() * b.getX() + a.getY() * b.getY()));
        System.out.println((Math.sqrt(a.getX() * a.getX() + a.getY() * a.getY()) * Math.sqrt(b.getX() * b.getX() + b.getY() * b.getY())));
        //System.out.println(a.getX() + " " + a.getY());
        //System.out.println(b.getX() + " " + b.getY());
*/
        return Math.min(res, 2 * Math.PI - res);
    }

    public boolean coincidence(Point a, Point b, Point c) {
        double eps = 0.0001;

        if (Math.abs(a.getX() - b.getX()) < eps && Math.abs(a.getY() - b.getY()) < eps) {
            return true;
        }

        if (Math.abs(b.getX() - c.getX()) < eps && Math.abs(b.getY() - c.getY()) < eps) {
            return true;
        }

        if (Math.abs(a.getX() - c.getX()) < eps && Math.abs(a.getY() - c.getY()) < eps) {
            return true;
        }

        return false;
    }

    public double[] getAngle(List<Point> points) {

        double res[] = new double[2];
        res[0] = 0;
        res[1] = Math.PI;

        if (points.size() < 3) {
            return res;
        }

        double ans = 0;
        double miAngle = 100000;
        for (int i = 2; i < points.size(); i++) {

            if (points.get(i).getmFloor() != points.get(i-1).getmFloor()) continue;
            if (points.get(i).getmFloor() != points.get(i-2).getmFloor()) continue;
            if (points.get(i-1).getmFloor() != points.get(i-2).getmFloor()) continue;

            if (coincidence(points.get(i), points.get(i-1), points.get(i-2))) continue;

            Point vec1 = getVector(points.get(i-1), points.get(i));

            Point vec2 = getVector(points.get(i-2), points.get(i-1));

            double angle = calcAngle(vec1, vec2);
            ans += angle;
            miAngle = Math.min(miAngle, angle);
        }

        res[0] = ans;
        res[1] = miAngle;

        return res;
    }

    public double[][] turnAngle(ArrayList<Record> records, int[] idx, int n) {
        double ans[][] = new double[2][n];

        int id = 0;
        List<Point> points = new LinkedList<Point>();
        points.clear();

        for (int i = 0; i < records.size(); i++) {
            if (i == 0) {
                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                continue;
            }

            if (idx[i] == id) {
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
            }
            else {
                double tmp[] = getAngle(points);

                ans[0][id] = tmp[0];
                ans[1][id] = tmp[1];

                points.clear();

                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
            }
        }

        double tmp[] = getAngle(points);

        ans[0][id] = tmp[0];
        ans[1][id] = tmp[1];

        return ans;
    }

    public double Var(List<Point> points) {

        if (points.size() <= 1) {
            return 0;
        }

        double x = 0;
        double y = 0;
        double ans = 0;

        for (int i = 0; i < points.size(); i++) {
            x += points.get(i).getX();
            y += points.get(i).getY();
        }

        x /= points.size();
        y /= points.size();

        for (int i = 0; i < points.size(); i++) {
            double xx = x - points.get(i).getX();
            double yy = y - points.get(i).getY();

            double dis = Math.sqrt(xx * xx + yy * yy);

            ans += dis;
        }

        return ans / points.size();
    }

    public double[] getVar(ArrayList<Record> records, int[] idx, int n) {
        double ans[] = new double[n];

        int id = 0;
        List<Point> points = new LinkedList<Point>();

        for (int i = 0; i < records.size(); i++) {
            if (i == 0) {
                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                continue;
            }

            if (idx[i] == id) {
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
            }
            else {
                double tmp = Var(points);

                points.clear();

                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
            }
        }

        double tmp = Var(points);

        ans[id] = tmp;

        return ans;
    }

    /**
     * write
     * @param file
     * @param snpts
     */
    public void writeSnpts(File file, List<Snpt> snpts) {

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < snpts.size(); i++) {
            try {
                fw.write(snpts.get(i).toString() + "\n");
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
     * get the label
     * @param records
     * @param idx
     * @param n
     * @param num0
     * @param num1
     * @return
     */
    public int[] getLabel(ArrayList<Record> records, int[] idx, int n, int num0[], int num1[]){
        int ans[] = new int[n];

        int cnt0[] = new int[n];
        int cnt1[] = new int[n];

        for (int i = 0; i < n; i++) {
            ans[i] = 0;
            cnt0[i] = 0;
            cnt1[i] = 0;
        }

        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).isStay()) {
                cnt1[idx[i]]++;
            }
            else {
                cnt0[idx[i]]++;
            }
        }

        for (int i = 0; i < n; i++) {
            num0[i] = cnt0[i];
            num1[i] = cnt1[i];

            if (cnt1[i] > cnt0[i]) {
                ans[i] = 1;
            }
            else {
                ans[i] = 0;
            }
        }

        return ans;
    }

    /**
     * get the distance feature
     * @param points
     * @return
     */
    public double[] getDis3(List<Point> points) {

        double ans[] = new double[3];

        ans[0] = ans[1] = ans[2] = 0;

        if (points.size() <= 1) {
            return ans;
        }

        double miDis = 1000000;
        double mxDis = -1;
        double avgDis = 0;

        for (int i = 1; i < points.size(); i++) {
            IdrObj ss = new IdrObj(0, points.get(i-1));
            IdrObj tt = new IdrObj(0, points.get(i));
            double dis = p2pDis.getP2pDis(ss, tt);

            miDis = Math.min(miDis, dis);
            mxDis = Math.max(mxDis, dis);
            avgDis += dis;
        }

        avgDis /= points.size() - 1;

        ans[0] = miDis;
        ans[1] = mxDis;
        ans[2] = avgDis;

        return ans;
    }

    public double[][] getDis(ArrayList<Record> records, int[] idx, int n) {

        double ans[][] = new double[3][n];

        int id = 0;
        List<Point> points = new LinkedList<Point>();

        for (int i = 0; i < records.size(); i++) {
            if (i == 0) {
                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
                continue;
            }

            if (idx[i] == id) {
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));
            }
            else {
                double tmp[] = getDis3(points);

                ans[0][id] = tmp[0];
                ans[1][id] = tmp[1];
                ans[2][id] = tmp[2];

                points.clear();

                id = idx[i];
                points.add(new Point(records.get(i).getX(), records.get(i).getY(), records.get(i).getFloor()));

            }
        }

        double tmp[] = getDis3(points);

        ans[0][id] = tmp[0];
        ans[1][id] = tmp[1];
        ans[2][id] = tmp[2];

        return ans;
    }

    /**
     * get the feature
     */
    public void createFeature() {
        Splitting splitting = new Splitting();
        for (int i = 0; i < TraDataGen.totalPerson; i++) {
            File file = new File(System.getProperty("user.dir") + "/CleanData/person" + i + ".txt");
            File fwfile = new File(System.getProperty("user.dir") + "/Feature/person" + i + ".txt");
            File labelfile = new File(System.getProperty("user.dir") + "/Label/person" + i + ".txt");
            File numfile = new File(System.getProperty("user.dir") + "/Num/person" + i + ".txt");

            File snippetFile = new File(System.getProperty("user.dir") + "/Snippet/person" + i + ".txt");

            ArrayList<Record> records = ReadRecord.getRecord(file);

            List<Snpt> snpts = splitting.splitting(records, i);

            writeSnpts(snippetFile, snpts);

            if (records.size() == 0) {

                FileWriter fw1 = null;
                FileWriter fw2 = null;
                FileWriter fw3 = null;

                try {
                    fw1 = new FileWriter(numfile);
                    fw2 = new FileWriter(fwfile);
                    fw3 = new FileWriter(labelfile);

                    fw1.write("");
                    fw2.write("");
                    fw3.write("");

                    fw1.close();
                    fw2.close();
                    fw3.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }

            int dense[] = isDense(snpts);

            int idx[] = getIDX(records, snpts);

            int cnt[] = getCnt(records, idx, snpts.size());

            int uniqueID[] = getUniqueID(records, idx, snpts.size());

            //double turnAngle[][] = turnAngle(records, idx, snpts.size());

            double dis[][] = getDis(records, idx, snpts.size());

/*
            for (int j = 0; j < turnAngle.length; j++) {
                System.out.println(i + " " + turnAngle[i]);
            }
            */

            double speed[][] = getSpeed(records, idx, snpts.size());

            double variance[] = getVar(records, idx, snpts.size());

            int num0[] = new int[snpts.size()];
            int num1[] = new int[snpts.size()];

            int label[] = getLabel(records, idx, snpts.size(), num0, num1);

            writeNum(numfile, num0, num1);
            write(fwfile, cnt, dense, uniqueID, variance, speed, dis, snpts.size());

            write(labelfile, label);
            System.out.println(i);
        }
    }

    /**
     * write
     * @param file
     * @param num0
     * @param num1
     */
    public void writeNum(File file, int[] num0, int[] num1) {
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < num0.length; i++) {
            try {
                fw.write(num0[i] + " " + num1[i] + "\n");
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
     * write
     * @param file
     * @param label
     */
    public void write(File file, int[] label) {

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < label.length; i++) {
            try {
                fw.write(label[i] + "\n");
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

    public void write(File fwfile, int[] cnt, int[] dense, int[] uniqueID, double[] variance, double[][] speed, double dis[][], int n) {

        FileWriter fw = null;

        try {
            fw = new FileWriter(fwfile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //System.out.println(cnt.length + " " + dense.length + " " + uniqueID.length + " " + variance.length + " " + turnAngle.length);

        for (int i = 0; i < n; i++) {
            try {
                //fw.write(dense[i] + "\n");

                fw.write(cnt[i] + " " + dense[i] + " " + uniqueID[i] + " " + variance[i]
                        + " " + speed[0][i] + " " + speed[1][i] + " " + speed[2][i]
                        + " " + dis[0][i] + " " + dis[1][i] + " " + dis[2][i] + "\n");

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
     * initialization
     */
    public void init() {
        File dir = new File(System.getProperty("user.dir") + "/Feature");
        dir.mkdir();

        File sdir = new File(System.getProperty("user.dir") + "/Snippet");
        sdir.mkdir();

        File labeldir = new File(System.getProperty("user.dir") + "/Label");
        labeldir.mkdir();

        File numdir = new File(System.getProperty("user.dir") + "/Num");
        numdir.mkdir();

        p2pDis.init_D2dPath();
        ReadDoor.getDoor();
        ReadPar.getPar();
        p2pDis.getD2dDistance();

        DataGen dataGen = new DataGen();

        dataGen.genIndoorSpace();

        for (int i = 0; i < IndoorSpace.gPartitions.size(); i++) {
            double x1 = IndoorSpace.gPartitions.get(i).getX1() * 2;
            double x2 = IndoorSpace.gPartitions.get(i).getX2() * 2;
            double y1 = IndoorSpace.gPartitions.get(i).getY1() * 2;
            double y2 = IndoorSpace.gPartitions.get(i).getY2() * 2;

            IndoorSpace.gPartitions.get(i).setX1(x1);
            IndoorSpace.gPartitions.get(i).setX2(x2);
            IndoorSpace.gPartitions.get(i).setY1(y1);
            IndoorSpace.gPartitions.get(i).setY2(y2);
        }

        dataGen.initRTree();

        dataGen.duplicateIndoorSpace(DataGenConstant.nFloor);

    }

    public static void main(String args[]) {
        DataSet dataSet = new DataSet();
        dataSet.init();
        dataSet.createFeature();
    }
}
