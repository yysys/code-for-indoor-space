package dk.aau.cs.idq.clean;

import java.security.PublicKey;

public class Record {

    private int id;
    private int time;
    private double x;
    private double y;
    private int mFloor;
    private int parID;
    private boolean isStay;
    private boolean isOutlier;

    public boolean isStay() {
        return isStay;
    }

    public boolean isOutlier() {
        return isOutlier;
    }

    public int getFloor() {
        return this.mFloor;
    }

    public void setmFloor(int mFloor) {
        this.mFloor = mFloor;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setParID(int parID) {
        this.parID = parID;
    }

    public void setY(double y) {
        this.y = y;
    }

    public int getParID() {
        return parID;
    }

    public int getTime() {
        return this.time;
    }

    public Record(Record record) {
        this.id = record.id;
        this.time = record.time;
        this.x = record.y;
        this.y = record.y;
        this.mFloor = record.mFloor;
        this.parID = record.parID;

        this.isStay = record.isStay;
        this.isOutlier = record.isOutlier;
    }

    public Record(int time, double x, double y, int  mFloor, int parID, int isStay, int isOutlier) {
        this.id = id;
        this.time = time;
        this.x = x;
        this.y = y;
        this.mFloor = mFloor;
        this.parID = parID;

        if (isStay == 0) this.isStay = false;
        else this.isStay = true;

        if(isOutlier == 0) this.isOutlier = false;
        else this.isOutlier = true;
    }

    public Record(int time, double x, double y, int  mFloor, int parID, boolean isStay, boolean isOutlier) {
        this.id = id;
        this.time = time;
        this.x = x;
        this.y = y;
        this.mFloor = mFloor;
        this.parID = parID;

        this.isStay = isStay;
        this.isOutlier = isOutlier;
    }

    public Record(int id, int time, double x, double y, int  mFloor, int parID, int isStay, int isOutlier) {
        this.id = id;
        this.time = time;
        this.x = x;
        this.y = y;
        this.mFloor = mFloor;
        this.parID = parID;

        if (isStay == 0) this.isStay = false;
        else this.isStay = true;

        if(isOutlier == 0) this.isOutlier = false;
        else this.isOutlier = true;
    }

    public void setID() {
        this.id = id;
    }

    public int getID() {
        return this.id;
    }

    public String toString() {
        int isStay = 0;
        if (this.isStay == true) isStay = 1;

        int isOutlier = 0;
        if(this.isOutlier == true) isOutlier = 1;

        return new String(time + " " + x + " " + y + " " + mFloor + " " + parID + " " + isStay + " " + isOutlier);
    }
}
