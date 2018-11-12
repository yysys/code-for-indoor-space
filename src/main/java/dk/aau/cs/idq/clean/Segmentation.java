package dk.aau.cs.idq.clean;

public class Segmentation {

    public static final int unknown = 0;
    public static final int stable = 1;
    public static final int unstable = 2;
    public static final int intermediate = 3;
    public static final int finish = 4;

    private int status;
    private int mFloor;
    private double duration;
    private int start;
    private int end;

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public double getDuration() {
        return duration;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus() {
        return status;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public int getStart() {
        return start;
    }

    public int getmFloor() {
        return mFloor;
    }

    public void setmFloor(int mFloor) {
        this.mFloor = mFloor;
    }

    public Segmentation(int status, int start, int end, int mFloor) {
        this.status = status;
        this.start = start;
        this.end = end;
        this.mFloor = mFloor;
        this.duration = end - start + 1;
    }
/*
    public Segmentation(int status, int start, int end, int mFloor) {
        this.status = status;
        this.start = start;
        this.end = end;
        this.mFloor = mFloor;
        this.duration = duration;
    }
*/
    public String toString() {
        return new String(status + " " + duration + " " + start + " " + end + " " + mFloor);
    }
}
