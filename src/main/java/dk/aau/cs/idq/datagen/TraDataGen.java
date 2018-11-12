package dk.aau.cs.idq.datagen;

import dk.aau.cs.idq.indoorentities.Person;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import static dk.aau.cs.idq.datagen.DataGen.outputPath;

public class TraDataGen {
    public static final int totalPerson = 10000;             //the amount of person

    /**
     * generate trajectory data
     */
    public void getTraDataGen() {
        File traFile = new File(outputPath + "/TrajectoryData.txt");
        FileWriter fw = null;
        try {
            fw = new FileWriter(traFile);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Person person[] = new Person[totalPerson];

        for (int i = 0; i < totalPerson; i++) {
            System.out.println("create person " + i + " successful!");
            person[i] = Person.createPerson();
        }

        for (int i = 0; i < Person.totalTime; i++) {
            for (int j = 0; j < totalPerson; j++) {
                person[j].step();
                if(i >= person[j].getStartTime() && i <= person[j].getEndTime()) {
                    int isStay = 0;
                    if (person[j].isStay()) {
                        isStay = 1;
                    }
                    String record = new String(i + " " + person[j].getPersonID() + " # "
                            + person[j].getmTruePos().getX() + " " + person[j].getmTruePos().getY() + " "
                            + person[j].getmTruePos().getmFloor() + " " + person[j].getCurPar().getmID() + " "
                            + isStay);
                    try {
                        fw.write(record + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            System.out.println("time: " + i + " generate successful!");
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String arge[]) {
        TraDataGen traDataGen = new TraDataGen();
        traDataGen.getTraDataGen();
    }

}
