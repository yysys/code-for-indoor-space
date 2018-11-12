package dk.aau.cs.idq.semantics;

public class Snpt {

    private int startID, endID;
    private int startTime, endTime;
    private boolean isDense;

    public Snpt(int startID, int endID, int startTime, int endTime, boolean isDense) {
        this.startID = startID;
        this.endID = endID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isDense = isDense;
    }

    public int getStartID() {
        return this.startID;
    }

    public int getEndID() {
        return this.endID;
    }

    public int getStartTime() {
        return this.startTime;
    }

    public void setDense(boolean isDense) {
        this.isDense = isDense;
    }

    public boolean isDense() {
        return this.isDense;
    }

    public int getEndTime() {
        return this.endTime;
    }

    public String toString() {
        return new String(startID + " " + endID + " " + startTime + " " + endTime + " " + (isDense ? 1 : 0));
    }
/*
    public String toString() {
        return new String("statrID = " + startID + " endID = " + endID + " startTime = " + startTime + " endTime = " + endTime + " isDense = " + isDense);
    }
*/

}
