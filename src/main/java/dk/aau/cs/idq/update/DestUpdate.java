package dk.aau.cs.idq.update;

import dk.aau.cs.idq.indoorentities.Dest;
import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.utilities.ReadDest;
import dk.aau.cs.idq.utilities.ReadDoor;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DestUpdate {

    public void destUpdate() {
        List<Dest> destList = ReadDest.getDest();

        for (int i = 0; i < destList.size(); i++) {
            double x1 = destList.get(i).getX1() * 2;
            double x2 = destList.get(i).getX2() * 2;
            double y1 = destList.get(i).getY1() * 2;
            double y2 = destList.get(i).getY2() * 2;

            double x = destList.get(i).getCenterPoint().getX() * 2;
            double y = destList.get(i).getCenterPoint().getY() * 2;

            destList.get(i).setX1(x1);
            destList.get(i).setX2(x2);
            destList.get(i).setY1(y1);
            destList.get(i).setY2(y2);

            destList.get(i).getCenterPoint().setX(x);
            destList.get(i).getCenterPoint().setY(y);
        }

        File doorFile= new File(System.getProperty("user.dir") + "/Dest.txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(doorFile);
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < destList.size(); i++) {
            try {
                fw.write(destList.get(i).toString() + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
