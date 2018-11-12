package dk.aau.cs.idq.indoorentities;

public class MovingPoint extends Point {

    private Par curPar;

    public MovingPoint(double x, double y, int mFloor) {
        super(x, y, mFloor);
    }

    public MovingPoint(double x, double y, int mFloor, Par curPar) {
        super(x, y, mFloor);
        setCurPar(curPar);
    }

    public void setCurPar(Par curPar) {
        this.curPar = curPar;
    }

    public Par getCurPar() {
        return this.curPar;
    }
}
