package dk.aau.cs.idq.update;

import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class ParUpdate {


    public void parUpdate() {
        List<Par> parList = ReadPar.getPar();

        for (int i = 0; i < parList.size(); i++) {
            double x1 = parList.get(i).getX1() * 2;
            double x2 = parList.get(i).getX2() * 2;
            double y1 = parList.get(i).getY1() * 2;
            double y2 = parList.get(i).getY2() * 2;

            parList.get(i).setX1(x1);
            parList.get(i).setX2(x2);
            parList.get(i).setY1(y1);
            parList.get(i).setY2(y2);
        }

        File parFile= new File(System.getProperty("user.dir") + "/Par.txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(parFile);
            fw.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < parList.size(); i++) {
            try {
                fw.write(parList.get(i).toString() + "\n");
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
