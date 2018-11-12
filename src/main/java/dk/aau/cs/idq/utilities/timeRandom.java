package dk.aau.cs.idq.utilities;

import org.omg.Messaging.SYNC_WITH_TRANSPORT;

import java.util.Random;

public class timeRandom {

    public static final double max = 1e7;
    public static int durationLimit = 2000;
    public static Random r = new Random();

    public static int getDurationTime() {

        int t = -1;

        while (t < 0 || t > 1000) {
            t = durationLimit - (int) (Math.log(r.nextDouble() * max + 1) / Math.log(max) * durationLimit);
        }

        return  t;
    }
}
