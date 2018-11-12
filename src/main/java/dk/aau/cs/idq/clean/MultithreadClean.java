package dk.aau.cs.idq.clean;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Door;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Par;
import dk.aau.cs.idq.utilities.DataGenConstant;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MultithreadClean {

    /**
     * clean data
     * @throws InterruptedException
     */
    public void dataClean() throws InterruptedException {

        ExecutorService exec = Executors.newFixedThreadPool(20);

        for (int i = 0; i < TraDataGen.totalPerson; i++) {

            Clean tmp = new Clean();
            tmp.setFileID(i);

            exec.execute(tmp);
        }
        exec.shutdown();
        while (true) {
            if (exec.isTerminated()) {
                System.out.println("complete!!!");
                break;
            }
            Thread.sleep(10000);
        }
    }

    public void init() {
        DataGen dataGen = new DataGen();

        dataGen.genIndoorSpace();

        for (int i = 0; i < IndoorSpace.gPartitions.size(); i++) {
            double x1 = IndoorSpace.gPartitions.get(i).getX1() * 2;
            double x2 = IndoorSpace.gPartitions.get(i).getX2() * 2;
            double y1 = IndoorSpace.gPartitions.get(i).getY1() * 2;
            double y2 = IndoorSpace.gPartitions.get(i).getY2() * 2;

            IndoorSpace.gPartitions.get(i).setX1(x1);
            IndoorSpace.gPartitions.get(i).setX2(x2);
            IndoorSpace.gPartitions.get(i).setY1(y1);
            IndoorSpace.gPartitions.get(i).setY2(y2);
        }

        dataGen.initRTree();

        dataGen.duplicateIndoorSpace(DataGenConstant.nFloor);

        File CleanData = new File(System.getProperty("user.dir") + "/CleanData");

        CleanData.mkdir();
    }

    public void var_init()
    {
        p2pDis.init_D2dPath();
        ReadDoor.getDoor();
        ReadPar.getPar();
        p2pDis.getD2dDistance();

        File file = new File(System.getProperty("user.dir") + "/errorEvaluate.txt");
        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
            fw.write("");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        file = new File(System.getProperty("user.dir") + "/floorEvaluate.txt");
        fw = null;

        try {
            fw = new FileWriter(file);
            fw.write("");
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static void main(String arge[]) throws InterruptedException {
        MultithreadClean clean = new MultithreadClean();
        clean.init();

        clean.var_init();

        Comp comp = new Comp();
        Clean.getTime();
//        comp.floorEvaluate();

        for (int i = 3; i <= 3; i++) {
            Clean.speedLimit = 1.5 + 0.2 * i;

            clean.dataClean();
            comp.floorEvaluate();
        }
/*
            System.out.println("ReadTime = " + Clean.ReadTime / TraDataGen.totalPerson);
            System.out.println("HandleTime = " + Clean.HandleTime / TraDataGen.totalPerson);
            System.out.println("WriteTime = " + Clean.WriteTime / TraDataGen.totalPerson);
            System.out.println("AllTime = " + Clean.AllTime / TraDataGen.totalPerson);

        }
*/
        Clean.getTime();
    }

}
