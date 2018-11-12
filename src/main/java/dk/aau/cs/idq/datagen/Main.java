package dk.aau.cs.idq.datagen;

import dk.aau.cs.idq.clean.ArrangementData;
import dk.aau.cs.idq.clean.MultithreadClean;
import dk.aau.cs.idq.clean.p2pDis;
import dk.aau.cs.idq.secondTest.*;
import dk.aau.cs.idq.update.*;
import dk.aau.cs.idq.utilities.DestGen;
import dk.aau.cs.idq.utilities.ReadDoor;
import dk.aau.cs.idq.utilities.ReadPar;

import java.io.IOException;

public class Main {

    public static int option = 0;

    public static void main(String args[]) throws IOException, InterruptedException {

        if (option == 0) { //Initialization and generate true data
            Carry carry = new Carry();
            carry.carry();

            ReadDoor.doorList = null;
            DoorUpdate doorUpdate  = new DoorUpdate();
            doorUpdate.doorUpdate();

            ReadPar.parList = null;
            ParUpdate parUpdate = new ParUpdate();
            parUpdate.parUpdate();

            ReadDoor.doorList = null;
            ReadPar.parList = null;

            DestGen destGen = new DestGen();
            destGen.getDest();
            destGen.Dest2Dest();
            destGen.writeDest2DestProb();

            TraDataGen traDataGen = new TraDataGen();
            traDataGen.getTraDataGen();
        }
        else if (option == 1) { //generate noise data
            NoiseDataGen noiseDataGen = new NoiseDataGen();
            noiseDataGen.init();
            noiseDataGen.getNoiseData();
        }
        else if (option == 2) { //initialization for cleaning
            System.out.println("initialization for cleaning...");
            ArrangementData arrangementData = new ArrangementData();
            arrangementData.arrangement();
        }
        else if (option == 3) { //clean data
            p2pDis.init();
            System.out.println("cleaning...");
            MultithreadClean clean = new MultithreadClean();
            clean.init();
            clean.var_init();
            clean.dataClean();
        }
        else if (option == 4) { //logistic regression
            DataSet dataSet = new DataSet();
            dataSet.init();
            dataSet.createFeature();

            LRmodel lrmodel  = new LRmodel();
            lrmodel.init();
            lrmodel.model();

            evaluate evaluate = new evaluate();
            evaluate.work();
            LRmodel.writeLabel();
        }
    }

}
