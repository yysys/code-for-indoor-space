package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.indoorentities.*;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.PointToPoint;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Clean extends Thread{

    public static final int validRecordSize = 30;
    public static double speedLimit = 2.1;
    public static double ReadTime = 0;
    public static double HandleTime = 0;
    public static double WriteTime = 0;
    public static double AllTime = 0;

    private int fileID;

    public void setFileID(int fileID) {
        this.fileID = fileID;
    }

    public int getFileID() {
        return this.fileID;
    }

    /**
     * read one person data from disk
     * @param curPersonFile
     * @return
     */
    public List<Record> readPersonData(File curPersonFile) {
        List<Record> records = new ArrayList<Record>();

        Scanner in = null;
        try {
            in = new Scanner(curPersonFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Random r = new Random();

        while (in.hasNext()) {
            String line = in.nextLine();
            String input[] = line.split(" ");

            int curTime = Integer.valueOf(input[0]).intValue();
            double x = Double.valueOf(input[1]).doubleValue();
            double y = Double.valueOf(input[2]).doubleValue();
            int mFloor = Integer.valueOf(input[3]).intValue();
            int parID = Integer.valueOf(input[4]).intValue();
            int isStay = Integer.valueOf(input[5]).intValue();
            int isOutlier = Integer.valueOf(input[6]).intValue();

            records.add(new Record(curTime, x, y, mFloor, parID, isStay, isOutlier));
        }

        return records;
    }

    /**
     * get speed
     * @param s
     * @param t
     * @param deltaT
     * @return
     */
    public double getSpeed(Point s, Point t, double deltaT) {
        IdrObj ss = new IdrObj(0, s);
        IdrObj tt = new IdrObj(0, t);
        double dis = p2pDis.getP2pDis(ss, tt);
        //double dis = p2pEuclid(s, t);

        return dis / deltaT;
    }

    /**
     * judge speed is valid or not
     * @param last
     * @param now
     * @return
     */
    public boolean ValidSpeed(Record last, Record now) {
        Point s = new Point(now.getX(), now.getY(), now.getFloor());
        Point t = new Point(last.getX(), last.getY(), last.getFloor());

        double speed = getSpeed(s, t, now.getTime() - last.getTime());

        if (speed < speedLimit) return true;
        else return false;
    }

    /**
     * calculate the distance to staircase
     * @param s
     * @return
     */
    public double p2stairDis(Point s)
    {
        double dis = PointToPoint.INF;

        List<Door> doors = ReadDoor.getDoor();

        for (int i = 0; i < PointToPoint.staircase.length; i++) {
            double x = doors.get(PointToPoint.staircase[i]).getX();
            double y = doors.get(PointToPoint.staircase[i]).getY();

            Point tmp = new Point(x, y, s.getmFloor());
            dis = Math.min(dis, euclid(s, tmp));
        }

        return dis;
    }

    /**
     * the distance of euclid
     * @param s
     * @param t
     * @return
     */
    public double p2pEuclid(Point s, Point t)
    {
        if (s.getmFloor() == t.getmFloor()) return euclid(s, t);

        return p2stairDis(s) + p2stairDis(t) + PointToPoint.stair2stair * Math.abs(s.getmFloor() - t.getmFloor());
    }

    /**
     * the distance of euclid
     * @param s
     * @param t
     * @return
     */
    public double euclid(Point s, Point t) {
        double x1 = s.getX();
        double x2 = t.getX();

        double y1 = s.getY();
        double y2 = t.getY();

        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public List<Record> interpolation2(Record s, List<Record> invaild, Record t) {
        List<Record> ans = new LinkedList<Record>();

        Point ss = new Point(s.getX(), s.getY(), s.getFloor());
        Point tt = new Point(t.getX(), t.getY(), t.getFloor());

        for (int i = 0; i < invaild.size(); i++) {

            Record record = invaild.get(i);
            double ratio = (double) (invaild.get(i).getTime() - s.getTime()) / (double) (t.getTime() - s.getTime());

            while(true) {
                record = invaild.get(i);


                double x = s.getX();
                double y = s.getY();

                double vecx = t.getX() - s.getX();
                double vecy = t.getY() - s.getY();

                Point mid = new Point(x + vecx * ratio, y + vecy * ratio, (s.getFloor() + t.getFloor()) / 2);

                record.setX(mid.getX());
                record.setY(mid.getY());
                record.setmFloor(mid.getmFloor());
                //System.out.println("------------->" + res.getX() + " " + res.getY());
                if (mid.getCurrentPar() == null) {
                    ratio = ratio - 0.001;
                    if (ratio < 0) {
                        ratio = 0;
                    }
                } else {
                    record.setParID(mid.getCurrentPar().getmID());
                    break;
                }
            }

            ans.add(record);
        }

        return ans;
    }

    /**
     * interpolation
     * @param s
     * @param invaild
     * @param t
     * @return
     */
    public List<Record> interpolation(Record s, List<Record> invaild, Record t) {

        Point pointS = new Point(s.getX(), s.getY(), s.getFloor());
        Point pointT = new Point(t.getX(), t.getY(), t.getFloor());

        IdrObj ss = new IdrObj(0, pointS);
        IdrObj tt = new IdrObj(0, pointT);

        List<Pair> path = new LinkedList<Pair>();

        List<Point> pathPoint = p2pDis.getP2PPath(ss, tt);

        double dist[] = new double[pathPoint.size()];

        for (int i = 1; i < pathPoint.size(); i++) {
            dist[i] = euclid(pathPoint.get(i-1), pathPoint.get(i));
        }

        double sum = 0;

        for (int i = 1; i < pathPoint.size(); i++) {
            sum += dist[i];
        }

        List<Record> ans = new LinkedList<Record>();
        for (int i = 0; i < invaild.size(); i++) {
            double target = (double)(invaild.get(i).getTime() - s.getTime()) / (double)(t.getTime() - s.getTime()) * sum;

            Record record = invaild.get(i);
            Point res = null;
            for (int j = 1; j < pathPoint.size(); j++) {
                if (target > dist[j]) {
                    target -= dist[j];
                }
                else {
                    double ratio = target / dist[j];
                    double tx = pathPoint.get(j-1).getX() + (pathPoint.get(j).getX() - pathPoint.get(j-1).getX()) * ratio;
                    double ty = pathPoint.get(j-1).getY() + (pathPoint.get(j).getY() - pathPoint.get(j-1).getY()) * ratio;
                    int tfloor = pathPoint.get(j).getmFloor();

                    res = new Point(tx, ty, tfloor);
                    break;
                }
            }

            if (res == null) res = pointT;

            record.setX(res.getX());
            record.setY(res.getY());
            record.setmFloor(res.getmFloor());
            record.setParID(res.getCurrentPar().getmID());

            ans.add(record);
        }

        return ans;
    }

    /**
     * clean records
     * @param records
     * @return
     */
    public List<Record> cleanRecords(List<Record> records) {

        List<Record> ans = new LinkedList<Record>();
        List<Record> invaild = new LinkedList<Record>();

        if (records.size() == 0) return ans;

        Record last = records.get(0);
        ans.add(last);

        for (int i = 1; i < records.size(); i++) {
            boolean v = true;
            Record tmp = records.get(i);
            /*
            if (tmp.getTime() == 3008) {
                System.out.println(getSpeed(new Point(last.getX(), last.getY(), last.getFloor()), new Point(tmp.getX(), tmp.getY(), tmp.getFloor()), 1000000));
            }
            */

            if (!ValidSpeed(last, tmp)) {
                tmp.setmFloor(last.getFloor());
                Point tmpPoint = new Point(tmp.getX(), tmp.getY(), tmp.getFloor());
                tmp.setParID(tmpPoint.getCurrentPar().getmID());

                if (!ValidSpeed(last, tmp)) {
                    invaild.add(tmp);
                    v = false;
                }
            }
            if (v) {

                List<Record> vaild = interpolation(last, invaild, tmp);

                for (int j = 0; j < vaild.size(); j++) {
                    ans.add(vaild.get(j));
                }

                last = tmp;
                ans.add(last);

                invaild.clear();
            }
        }

        if (invaild.size() != 0) {
            for (int i = 0; i < invaild.size(); i++) {
                ans.add(invaild.get(i));
            }
            invaild.clear();
        }


        return ans;
    }

    /**
     * return the time
     */
    public static void getTime() {
        //func2
        Calendar cal = Calendar.getInstance();
        Date date = cal.getTime();
        //  2016/05/05-01:01:34:364
        System.out.println(new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss:SSS").format(date));
    }

    /**
     * write to disk
     * @param records
     * @param personID
     */
    public void write(List<Record> records, int personID) {

        File RecordFile = new File(System.getProperty("user.dir") + "/CleanData/person" + personID + ".txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(RecordFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < records.size(); i++) {
            try {
                fw.write(records.get(i).toString() + "\n");
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
     * run
     */
    public void run() {
        int i = getFileID();

        File RecordFile = new File(System.getProperty("user.dir") + "/obsData/person" + i + ".txt");

        double a = System.currentTimeMillis();
        List<Record> records = readPersonData(RecordFile);

        double b = System.currentTimeMillis();
        List<Record> ans = cleanRecords(records);

        double c = System.currentTimeMillis();
        write(ans, i);
        double d = System.currentTimeMillis();

        ReadTime += b - a;
        HandleTime += c - b;
        WriteTime += d - c;
        AllTime += d - a;

        System.out.println("Person" + i + " clean successful!");
    }

    public static void main(String arge[]) {
        Clean clean = new Clean();
    }
}
