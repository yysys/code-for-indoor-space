package dk.aau.cs.idq.update;

import dk.aau.cs.idq.indoorentities.Point;

public class Msemantic {
    private int personID;
    private int startTime;
    private int endTime;
    private int isStay;
    private int parID;

    public Msemantic(int personID, int startTime, int endTime, int isStay, int parID) {
        this.personID = personID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.isStay = isStay;
        this.parID = parID;
    }

    public int getIsStay() {
        return isStay;
    }

    public int getPersonID() {
        return personID;
    }

    public int getParID() {
        return this.parID;
    }

    public int getDuration() {
        return endTime - startTime + 1;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getStartTime() {
        return startTime;
    }

    public String toString() {
        return new String(personID + " " + startTime + " " + endTime + " " + isStay + " " + parID);
    }
}
