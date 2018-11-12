package dk.aau.cs.idq.indoorentities;

import java.util.Random;

public class ObservePersonInfo {
    private static final int obsTimeBot = 4;
    private static final int obsTimeTop = 7;

    private static final int deltaObsTime = 1;

    private boolean vis;
    private int obsTime;
    private int lastObsTime;
    private boolean isLastFloorDrift;

    private int oriFloor;
    private int lastFloorDriftTime;
    private int floorDriftDuration;
    private int lastFloorDrift;

    public void setOriFloor(int oriFloor) {
        this.oriFloor = oriFloor;
    }

    public int getOriFloor() {
        return oriFloor;
    }

    public boolean getVis() {
        return this.vis;
    }

    public void setVis(boolean vis) {
        this.vis = vis;
    }

    public int getObsTime() {
        return this.obsTime;
    }

    public void setObsTime(int obsTime) {
        this.obsTime = obsTime;
    }

    public boolean getIsLastFloorDrift() {
        return this.isLastFloorDrift;
    }

    public void setIsLastFloorDrift(boolean isLastFloorDrift) {
        this.isLastFloorDrift = isLastFloorDrift;
    }

    public int getLastObsTime() {
        return this.lastObsTime;
    }

    public void setLastObsTime(int lastObsTime) {
        this.lastObsTime = lastObsTime;
    }

    public void setLastFloorDrift(int lastFloorDrift) {
        this.lastFloorDrift = lastFloorDrift;
    }

    public void setFloorDriftDuration(int floorDriftDuration) {
        this.floorDriftDuration = floorDriftDuration;
    }

    public void setLastFloorDriftTime(int lastFloorDriftTime) {
        this.lastFloorDriftTime = lastFloorDriftTime;
    }

    public int getLastFloorDriftTime() {
        return lastFloorDriftTime;
    }

    public int getFloorDriftDuration() {
        return floorDriftDuration;
    }

    public int getLastFloorDrift() {
        return lastFloorDrift;
    }

    public void createObsTime() {
        Random r = new Random();
        int obsTime = obsTimeBot + (int)(r.nextDouble() * (obsTimeTop - obsTimeBot));
        setObsTime(obsTime);
    }

    public void changeObsTime() {
        Random r = new Random();
        int newObsTime = obsTime;
        while (true) {
            if (r.nextDouble() < 0.5) {
                newObsTime = obsTime + (int)(deltaObsTime * r.nextDouble());
            }
            else {
                newObsTime = obsTime - (int)(deltaObsTime * r.nextDouble());
            }

            if (newObsTime >= obsTimeBot && newObsTime < obsTimeTop) {
                this.obsTime = newObsTime;
                break;
            }
        }
    }

    public void setFirstObsTime(int curTime) {
        Random r = new Random();
        int time = 0;
        setLastObsTime(curTime + time - getObsTime());

    }

    public ObservePersonInfo() {
        createObsTime();
        setVis(false);
        setIsLastFloorDrift(false);

        setLastFloorDrift(0);
        setLastFloorDriftTime(0);
        setFloorDriftDuration(0);
    }
}
