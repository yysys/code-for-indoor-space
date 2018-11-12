package dk.aau.cs.idq.indoorentities;

public class Dest extends Rect{
    private int mDestID;
    private int curPar;
    private Point centerPoint;

    private void centerPointInit() {
        double x = (this.getX1() + this.getX2()) / 2.0;
        double y = (this.getY1() + this.getY2()) / 2.0;
        centerPoint = new Point(x, y, this.getmFloor());
    }

    public Dest(double x1, double x2, double y1, double y2, int mFloor, int curPar) {
        super(x1, x2, y1, y2);
        this.setCurPar(curPar);
        this.setmFloor(mFloor);
        centerPointInit();
    }

    public Dest(double x1, double x2, double y1, double y2, int mFloor, int mDestID, int curPar) {
            super(x1, x2, y1, y2);
        this.setCurPar(curPar);
        this.setmFloor(mFloor);
        this.mDestID = mDestID;
        centerPointInit();
    }

    public void setmDestID(int mDestID) {
        this.mDestID = mDestID;
    }

    public int getmDestID() {
        return this.mDestID;
    }

    public void setCurPar(int curPar) {
        this.curPar = curPar;
    }

    public int getCurPar() {
        return this.curPar;
    }

    public Point getCenterPoint() {
        return centerPoint;
    }

    public String toString() {
        return new String(mDestID + " " + this.getX1() + " " + this.getX2() + " " + this.getY1() + " "
                + this.getY2() + " " + this.getmFloor() + " " + curPar + " " + centerPoint.getX() + " " + centerPoint.getY());
    }
}
