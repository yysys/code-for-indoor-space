package dk.aau.cs.idq.others;

public class FP {

    private int parID1;
    private int parID2;

    public FP(int parID1, int parID2) {
        this.parID1 = Math.min(parID1, parID2);
        this.parID2 = Math.max(parID1, parID2);
    }

    public int getParID1() {
        return this.parID1;
    }

    public int getParID2() {
        return this.parID2;
    }


}
