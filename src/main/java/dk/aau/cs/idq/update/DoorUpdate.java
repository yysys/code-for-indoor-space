package dk.aau.cs.idq.update;

import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class DoorUpdate {

    public void doorUpdate() {
        List<Door> doorList = ReadDoor.getDoor();

        for (int i = 0; i < doorList.size(); i++) {
            double x = doorList.get(i).getX() * 2;
            double y = doorList.get(i).getY() * 2;

            doorList.get(i).setX(x);
            doorList.get(i).setY(y);
        }

        File doorFile= new File(System.getProperty("user.dir") + "/Door.txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(doorFile);
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < doorList.size(); i++) {
            try {
                fw.write(doorList.get(i).toString() + "\n");
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
