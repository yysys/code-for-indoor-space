package dk.aau.cs.idq.indoorentities;

import dk.aau.cs.idq.utilities.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.MonitorInfo;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class Person extends IdrObj{
    public static final int totalTime = 14400;                                //total time

    private static final int floorNum = 10;                                 //total floor
    private static final int bot = 3;                                       //the least destination each time produce
    private static final int top = 8;                                       //the most destination each time produce
    private static final int notInDest = -1;                                //the person not in destination
    private static final double oneStep = 1.7;                              //the distance of one step
    private static final double gapToDoor = 0.3;                            //the distance of this person to door

    private static final double portraitPara = 0.5;
    private static final double hasStartTimeHasEndTime = 0.5;               //the person has startTime and has endTime
    private static final double hasStartTimeNotHasEndTime = 0.15;           //the person has startTime and not has endTime
    private static final double notHasStartTimeHasEndTime = 0.15;           //the person not has startTime and has endTime

    private static final int elevatorTimeLimit = 6;                         //the limit of person in elevator
    private static final int stayTimeLimit = 1800;                                  //the time stay in destination

    private static int cntPersonID = 0;

    private int startTime = 0;                                              //the start time of this person
    private int endTime = 0;                                                //the end time of this person
    private int personID;                                                   //the ID of person
    private int curDestID;

    private boolean isStay;
    private int stayTime = 0;                                               //the time stay in destination
    private int inElevatorTime = 0;                                         //the time in elevator

    private List<Dest> dest;                                                //the destination
    private List<MovingPoint> pathPoint;                                    //the point in path

    public Person(Point mTruePos) {
        super(mTruePos);
        this.isStay = false;
        pathPoint = new LinkedList<MovingPoint>();
        setLifeCycle();
    }

    public Person(int personID, Point mTruePos) {
        super(personID, mTruePos);
        this.isStay = false;
        this.personID = personID;
        pathPoint = new LinkedList<MovingPoint>();
        setLifeCycle();
    }

    public Person(int personID, Point mTruePos, Par curPar) {
        super(personID, mTruePos, curPar);
        this.isStay = false;
        this.personID = personID;
        pathPoint = new LinkedList<MovingPoint>();
        setLifeCycle();
    }

    public Person(int personID, Point mTruePos, Par curPar, int curDestID) {
        super(personID, mTruePos, curPar);
        this.isStay = false;
        this.personID = personID;
        this.curDestID = curDestID;
        pathPoint = new LinkedList<MovingPoint>();
        dest = new LinkedList<Dest>();

        stayTime = 0;
        inElevatorTime = 0;

        setLifeCycle();
        go();
    }

    public boolean isStay() {
        return this.isStay;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

    public int getPersonID() {
        return this.personID;
    }

    public void setCurDestID(int curDestID) {
        this.curDestID = curDestID;
    }

    public int getCurDestID() {
        return this.curDestID;
    }

    public void setInElevatorTime(int inElevatorTime) {
        this.inElevatorTime = inElevatorTime;
    }

    public int getInElevatorTime() {
        return this.inElevatorTime;
    }

    public void addDest(Dest nextDest) {
        dest.add(nextDest);
    }

    public boolean isDestEmpty() {
        return dest.isEmpty();
    }

    /**
     * set person's startTime and endTime
     *
     */
    public void setLifeCycle() {
        Random r = new Random();
        double p = r.nextDouble();

        if (p < hasStartTimeHasEndTime) {
            startTime = (int)(totalTime * r.nextDouble());
            endTime = (int)(totalTime * r.nextDouble());

            if (startTime > endTime) {
                int tmp = startTime;
                startTime = endTime;
                endTime = tmp;
            }
        }
        else if (p < hasStartTimeHasEndTime + hasStartTimeNotHasEndTime) {
            startTime = (int)(totalTime * r.nextDouble());
            endTime = totalTime - 1;
        }
        else if (p < hasStartTimeHasEndTime + hasStartTimeNotHasEndTime + notHasStartTimeHasEndTime){
            startTime = 0;
            endTime = (int)(totalTime * r.nextDouble());
        }
        else {
            startTime = 0;
            endTime = totalTime - 1;
        }
    }

    /**
     *
     * add point in path
     *
     * @param path
     */
    public void addPathPoint(List<Pair> path) {
        ArrayList<Door> doorList = ReadDoor.getDoor();
        ArrayList<Par> parList = ReadPar.getPar();

        for (int i = 0; i < path.size(); i++) {
            Pair pair = path.get(i);
            int doorID = pair.getDoorID();
            int parID = pair.getParID();
            Door door = doorList.get(doorID);

            if(parID == PointToPoint.elevator) {
                parID = PointToPoint.getStaircaseParID(doorID);
            }
            Par nextPar = parList.get(parID);

            pathPoint.add(new MovingPoint(door.getX(), door.getY(), door.getmFloor(), nextPar));
        }
    }

    /**
     *
     * delete destination
     *
     */
    public void delDest() {
        if (isDestEmpty()) System.out.println("Delete destination error!");
        else dest.remove(0);
    }

    /**
     *
     * get next destination
     *
     * @return the next destination
     */
    public Dest getNextDest() {
        if (isDestEmpty()) {
            System.out.println("Get destination error!");
            return new Dest(0, 0, 0, 0, 0, 0);
        }

        return dest.get(0);
    }

    /**
     *
     * remove destination
     *
     * @return
     */
    public Dest remove() {
        if (isDestEmpty()) {
            System.out.println("Remove destination error!");
            return new Dest(0, 0, 0, 0, 0, 0);
        }

        return dest.remove(0);
    }

    /**
     *
     * create destination
     *
     */
    public void createDest() {
        List<Dest> destList = ReadDest.getDest();
        Random r = new Random();
        int destNum = destList.size();

        int floorID = (int)(r.nextDouble() * floorNum);
        int perGetDest = (int)r.nextDouble() * (top - bot) + bot;

        ArrayList<Integer>[] perFloorDest = PerFloorDest.getPerFloorDest();
        for (int i = 0; i < perGetDest; i++) {
            int ID = (int) (r.nextDouble() * perFloorDest[floorID].size());
            addDest(destList.get(perFloorDest[floorID].get(ID)));
        }
    }

    /**
     *
     * create person
     *
     * @return person
     */
    public static Person createPerson() {
        ArrayList<Par> parList = ReadPar.getPar();
        Random r = new Random();
        int parID = (int) (r.nextDouble() * parList.size());

        double x1 = parList.get(parID).getX1();
        double x2 = parList.get(parID).getX2();
        double y1 = parList.get(parID).getY1();
        double y2 = parList.get(parID).getY2();

        double x = x1 + (x2 - x1) * r.nextDouble();
        double y = y1 + (y2 - y1) * r.nextDouble();

        return new Person(cntPersonID++, new Point(x, y, parList.get(parID).getmFloor()), parList.get(parID), notInDest);
    }

    /**
     *
     * create a point in destination
     *
     * @param dest
     * @return
     */
    public MovingPoint creatDestPoint(Dest dest) {
        ArrayList<Par> parList = ReadPar.getPar();
        double x1 = dest.getX1();
        double x2 = dest.getX2();
        double y1 = dest.getY1();
        double y2 = dest.getY2();
        int parID = dest.getCurPar();
        int mFloor = dest.getmFloor();
        Random r = new Random();

        double x = x1 + (x2 - x1) * r.nextDouble();
        double y = y1 + (y2 - y1) * r.nextDouble();

        return new MovingPoint(x, y, mFloor, parList.get(parID));
    }

    /**
     * calculate the distance from point s to point t
     *
     * @param s
     * @param t
     * @return the distance
     */
    public double distance(Point s, Point t)
    {
        double x2 = (s.getX() - t.getX()) * (s.getX() - t.getX());
        double y2 = (s.getY() - t.getY()) * (s.getY() - t.getY());

        return Math.sqrt(x2 + y2);
    }
/*
    public Point arriveDestPos(Dest dest) {
        double x1 = dest.getX1();
        double x2 = dest.getX2();
        double y1 = dest.getY1();
        double y2 = dest.getY2();

        Random r = new Random();
        double x = x1 + (x2 - x1) * r.nextDouble();
        double y = y1 + (y2 - y1) * r.nextDouble();

        return new Point(x, y);
    }

    public void debug(List<Pair> path) {
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
     * find the path to next destination
     * if not has next destination, this function will get next destination
     *
     */
    public void go() {
        if (isDestEmpty()) createDest();
        Dest nextDest = remove();
        ArrayList<Par> parList = ReadPar.getPar();
        ArrayList<Door> doorList = ReadDoor.getDoor();
        double disProb[][][] = ReadDest2Dest.getDest2DestProb();

        List<Pair> path = null;

        if (curDestID == notInDest) {
            IdrObj t = new IdrObj(personID, nextDest.getCenterPoint(), parList.get(nextDest.getCurPar()));
            IdrObj s = new IdrObj(personID, this.getmTruePos(), this.getCurPar());
            path = new LinkedList<Pair>();
            PointToPoint.move(s, t, path);
            //System.out.println("------path---->" + path);
        } else {
            Random r = new Random();
            int cntID = 0;
            double tmp = r.nextDouble(), tmp2 = 0;

            for (int i = 0; i < ReadDest2Dest.pathNum; i++) {
                tmp2 += disProb[i][curDestID][nextDest.getmDestID()];
                if (tmp < tmp2) {
                    cntID = i;
                    break;
                }
            }

            //xuanze lujin
            path = ReadDest2Dest.getDest2Dest(cntID, curDestID, nextDest.getmDestID());
        }

        curDestID = nextDest.getmDestID();

        if (path.size() > 1) {
            addPathPoint(path);
        } else {
            if (path.size() == 1) {
                int doorID = path.get(0).getDoorID();
                double x = doorList.get(doorID).getX();
                double y = doorList.get(doorID).getY();
                int mFloor = doorList.get(doorID).getmFloor();
                pathPoint.add(new MovingPoint(x, y, mFloor, parList.get(nextDest.getCurPar())));
            }
        }
        pathPoint.add(creatDestPoint(nextDest));
    }

    /**
     *
     * judge the point is in par or not
     *
     * @param point
     * @param par
     * @return
     */
    public boolean pointInPar(Point point, Par par) {
        double x1 = par.getX1();
        double x2 = par.getX2();
        double y1 = par.getY1();
        double y2 = par.getY2();

        double x = point.getX();
        double y = point.getY();

        if (x > x1 && x < x2 && y > y1 && y < y2) return true;

        return false;
    }

    /**
     *
     * find the point of next step
     *
     * @param s
     * @param t
     * @param par
     * @return
     */
    public Point aStep(Point s, Point t, Par par)
    {
        double vecx = t.getX() - s.getX();
        double vecy = t.getY() - s.getY();
        double len = Math.sqrt(vecx * vecx + vecy * vecy);

        if (len < 1) len = 1.0;

        Point vec = new Point((t.getX() - s.getX()) / len, (t.getY() - s.getY()) / len);    //the vector from s to t
        Random r = new Random();

        Point vec2 = new Point(- vec.getY(), vec.getX());     //the normal vector from s to t

        Point ans = new Point();
        //System.out.println("AAA " + distance(s, t));
        while (true) {
            double tmp = r.nextDouble() * oneStep;
            double tmp2 = r.nextDouble() * oneStep * portraitPara;

            if (r.nextDouble() < 0.5) tmp2 = -tmp2;

            Point tmpVec = new Point(vec.getX() * tmp + vec2.getX() * tmp2, vec.getY() * tmp + vec2.getY() * tmp2);

            double length = Math.sqrt(tmpVec.getX() * tmpVec.getX() + tmpVec.getY() * tmpVec.getY());

            ans.setX(s.getX() + tmpVec.getX());
            ans.setY(s.getY() + tmpVec.getY());
            ans.setmFloor(s.getmFloor());

            //System.out.println(pointInPar(ans, par));
            /*
            {
                System.out.println(par.getX1() + " " + par.getY1() + " " + par.getX2() + " " + par.getY2());
                System.out.println("--->" + ans.getX() + " " + ans.getY());
                System.out.println("----<" + s.getX() + " " + s.getY());
                System.out.println(vec.getX() + " " + vec.getY());
                System.out.println(vec2.getX() + " " + vec2.getY());
            }
            */
            if(length < oneStep && pointInPar(ans, par)) {
                break;
            }
        }
        //System.out.println("BBB");

        return ans;
    }

    /**
     *
     * judge the point is in destination or not
     *
     * @param point
     * @param dest
     * @return
     */
    public boolean pointInDest(Point point, Dest dest) {
        double x1 = dest.getX1();
        double x2 = dest.getX2();
        double y1 = dest.getY1();
        double y2 = dest.getY2();

        double x = point.getX();
        double y = point.getY();

        if(x > x1 && x < x2 && y > y1 && y < y2) return true;

        return false;
    }

    /**
     *
     * stay in destination and find next point which is in this destination
     *
     */
    public void stay()
    {
        if (curDestID == -1) {
            System.out.println("stay function error!");
            return;
        }

        ArrayList<Dest> destList = ReadDest.getDest();
        Dest dest = destList.get(curDestID);

        Random r = new Random();

        while (true) {
            double x = this.getmTruePos().getX();
            double y = this.getmTruePos().getY();
            int mFloor = this.getmTruePos().getmFloor();

            if(r.nextDouble() > 0.5) x += r.nextDouble();
            else x -= r.nextDouble();

            if(r.nextDouble() > 0.5) y += r.nextDouble();
            else y -= r.nextDouble();

            Point point = new Point(x, y, mFloor);

            if(pointInDest(point, dest) && distance(point, this.getmTruePos()) < oneStep) {
                this.setmTruePosOnly(point);
                break;
            }
        }
    }

    public int getStayTime() {
        Random r = new Random();
        return (int)(r.nextDouble() * stayTimeLimit);
    }

    /**
     *
     * step forword
     *
     */
    public void step()
    {
        Random r = new Random();
        if (pathPoint.isEmpty()) {
            if (stayTime == -1) {
                stayTime = getStayTime();
                this.isStay = true;
                stay();
                return;
            }
            else if (stayTime != 0) {
                stayTime--;
                this.isStay = true;
                stay();
                return;
            }
            else {
                stayTime = -1;
                this.isStay = false;
                go();
            }
        }

        MovingPoint nextPoint = pathPoint.get(0);

        if (nextPoint.getmFloor() != this.getmTruePos().getmFloor()) {
            if (inElevatorTime > elevatorTimeLimit) {
                this.setmTruePosOnly(nextPoint);
                this.setCurPar(nextPoint.getCurPar());
                pathPoint.remove(0);
                inElevatorTime = 0;
            }
            else {
                inElevatorTime++;
            }
        }
        else {
            if (distance(this.getmTruePos(), nextPoint) < gapToDoor) {
                if (this.getCurPar().getmID() == nextPoint.getCurPar().getmID()) {
                    this.setmTruePosOnly(nextPoint);
                    this.setCurPar(nextPoint.getCurPar());
                    pathPoint.remove(0);
                }
                else {
                    this.setCurPar(nextPoint.getCurPar());

                    Point point = aStep(this.getmTruePos(), nextPoint, this.getCurPar());

                    this.setmTruePosOnly(point);
                    pathPoint.remove(0);
                }
            }
            else {
                Point point = aStep(this.getmTruePos(), nextPoint, this.getCurPar());
                this.setmTruePosOnly(point);
            }
        }
    }

    public void debug()
    {
        System.out.println("AAAAAAAAA");
        System.out.println(this.getmTruePos().getX() + " " + this.getmTruePos().getY() + " " + this.getmTruePos().getmFloor());
        System.out.println("BBBBBBBBB");
    }

    public static boolean equal(double x, double y)
    {
        if (Math.abs(x - y) < 1e-5) return true;
        else return false;
    }

    public void test(MovingPoint prepoint, MovingPoint point) {

        double X[] = {5.0, 30.0, 30.0, 55.0};
        double Y[] = {30.0, 5.0, 55.0, 30.0};

        if (prepoint.getmFloor() != point.getmFloor()) {

            if (equal(prepoint.getX(), point.getX()) && equal(prepoint.getY(), point.getY())) {

                int flag = 0;

                for (int i = 0; i < 4; i++) {
                    if (equal(prepoint.getX(), X[i]) && equal(prepoint.getY(), Y[i])) {
                        flag++;
                    }
                }

                if(flag == 1) ;//System.out.println("ok!");
                else System.out.println("error1!");
            }
            else {
                System.out.println("----->>>" + prepoint);
                System.out.println("-----<<<" + point);

                System.out.println("error2!");
            }
        }
        else {

            if (prepoint.getCurPar().getmID() == point.getCurPar().getmID()) {
                if (distance(new Point(prepoint.getX(), prepoint.getY()), new Point(point.getX(), point.getY())) < oneStep) {

                }
                else {
                    System.out.println("error!");
                }
               // System.out.println("ok!");
            }
            else {
                int parID1 = prepoint.getCurPar().getmID();
                int parID2 = point.getCurPar().getmID();

                ArrayList<Par> parList = ReadPar.getPar();

                Par par1 = parList.get(parID1);
                Par par2 = parList.get(parID2);

                List<Integer> door1 = par1.getmDoors();
                List<Integer> door2 = par2.getmDoors();

                int flag = 0;
                for (int i : door1) {
                    for (int j : door2) {
                        if (i == j) {
                            flag = 1;
                        }
                    }
                }

                if (flag == 1) {
                    //System.out.println("ok!");
                }
                else {
                    System.out.println("----->>>" + parID1);
                    System.out.println("-----<<<" + parID2);
                    System.out.println("----->>>" + prepoint);
                    System.out.println("-----<<<" + point);
                    System.out.println("error3!");
                }
            }
        }

    }

    public static void main(String arge[]) {


        Person person = createPerson();

        MovingPoint lastpoint = new MovingPoint(person.getmTruePos().getX(), person.getmTruePos().getY(), person.getmTruePos().getmFloor(), person.getCurPar());
        for (int i = 0; i < 100000000; i++) {
            if(i % 1000000 == 0) {
                System.out.println(i);
            }
            person.step();
            MovingPoint point = new MovingPoint(person.getmTruePos().getX(), person.getmTruePos().getY(), person.getmTruePos().getmFloor(), person.getCurPar());

            person.test(lastpoint, point);
            lastpoint = point;
        }

/*
        ArrayList<Dest> destList = ReadDest.getDest();

        for (Dest dest : destList) {
            if(dest.getmArea() < 0.0005)
            System.out.println("Area ");
        }
*/
    }
}