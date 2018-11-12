package dk.aau.cs.idq.selectTrajectory;

import dk.aau.cs.idq.datagen.DataGen;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.secondTest.*;
import dk.aau.cs.idq.utilities.DataGenConstant;

import java.util.List;

public class CleaningDataToTra {

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
    }

    public void solve() {
/*
        DataSet dataSet = new DataSet();
        dataSet.init();
        dataSet.createFeature();

        LRmodel lrmodel  = new LRmodel();
        lrmodel.init();
        lrmodel.model();

        evaluate evaluate = new evaluate();
        evaluate.work();

        LRmodel.writeLabel();
*/
        snptTest snptTest = new snptTest();
//        snptTest.test();
        snptTest.mergePassBy();
        snptTest.mergeStay();
        snptTest.completion();
        snptTest.mergeConnection();

        outputToJson outputToJson = new outputToJson();
        outputToJson.output();
        /*
        Select select = new Select();
        List<Integer> ans = select.Select();
        */


    }

    public void select() {
        Select select = new Select();
        List<Integer> ans = select.Select();

        System.out.println(ans.size());
        System.out.println(ans);
    }

    public static void main(String args[]) {
        CleaningDataToTra cleaningDataToTra = new CleaningDataToTra();

        cleaningDataToTra.init();
        cleaningDataToTra.solve();
        //cleaningDataToTra.select();
    }
}
