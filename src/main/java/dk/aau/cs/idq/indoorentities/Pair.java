package dk.aau.cs.idq.indoorentities;

public class Pair {
    private int doorID, parID;      //door number and par number

    public Pair(int doorID, int parID) {
        this.doorID = doorID;
        this.parID = parID;
    }

    public void setDoorID(int doorID) {
        this.doorID = doorID;
    }

    public void setParID(int parID) {
        this.parID = parID;
    }

    public int getDoorID() {
        return this.doorID;
    }

    public int getParID() {
        return this.parID;
    }

    public String toString() {
        return new String(doorID + " " + parID);
    }
}
