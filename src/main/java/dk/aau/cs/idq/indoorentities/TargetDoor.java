package dk.aau.cs.idq.indoorentities;


public class TargetDoor extends Pair implements Comparable<Object> {

    private int preDoor;
    private double distance;

    public TargetDoor(int doorID, int parID, double distance) {
        super(doorID, parID);
        this.distance = distance;
    }

    public TargetDoor(int preDoor, int doorID, int parID, double distance) {
        super(doorID, parID);
        this.preDoor = preDoor;
        this.distance = distance;
    }

    public void setPreDoor(int preDoor) {
        this.preDoor = preDoor;
    }

    public int getPreDoor() {
        return this.preDoor;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return this.distance;
    }

    @Override
    public int compareTo(Object o) {
        TargetDoor another = (TargetDoor) o;

        if (this.distance < another.distance){
            return -1;
        } else if (this.distance == another.distance){
            if (this.getDoorID() < another.getDoorID()){
                return -1;
            }else if (this.getDoorID() == another.getDoorID()){
                if (this.getParID() < another.getParID()) {
                    return -1;
                } else if (this.getParID() == another.getParID()) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }
}
