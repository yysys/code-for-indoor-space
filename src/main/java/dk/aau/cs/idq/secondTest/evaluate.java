package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.indoorentities.Pair;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class evaluate {

    public static double accuracy;

    /**
     * genertate accuracy, precision. recall, F1
     */
    public void work() {
        ArrayList<Integer> cnt0 = new ArrayList<Integer>();
        ArrayList<Integer> cnt1 = new ArrayList<Integer>();

        for (int i = LRmodel.boundary; i < TraDataGen.totalPerson; i++) {
            File file = new File(System.getProperty("user.dir") + "/Num/person" + i + ".txt");

            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }

            while(in.hasNext()) {
                int tmp0 = in.nextInt();
                int tmp1 = in.nextInt();

                cnt0.add(tmp0);
                cnt1.add(tmp1);
            }
        }

        File predictFile = new File(System.getProperty("user.dir") + "/predict.txt");

        Scanner in = null;

        try {
            in = new Scanner(predictFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> predict = new ArrayList<Integer>();
        while(in.hasNext()) {
            int tmp = in.nextInt();
            predict.add(tmp);
        }

        System.out.println(cnt0.size() + " " + cnt1.size() + " " + predict.size());

        double amount = 0;
        double correct = 0;

        double TP = 0, TN = 0, FP = 0, FN = 0;

        for (int i = 0; i < predict.size(); i++) {
            if (predict.get(i) == 1) {
                correct += cnt1.get(i);
                TP += cnt1.get(i);
                FP += cnt0.get(i);
            }
            else {
                correct += cnt0.get(i);
                FN += cnt1.get(i);
                TN += cnt0.get(i);
            }
            amount += cnt0.get(i);
            amount += cnt1.get(i);
        }

        double precision = TP / (TP + FP);
        double recall = TP / (TP + FN);
        accuracy = (TP + TN) / (TP + TN + FP + FN);
        double F1 = 2 * precision * recall / (precision + recall);

        System.out.println("records level:");

        System.out.println("accuracy = " + accuracy);
        System.out.println("precision = " + precision);
        System.out.println("recall = " + recall);
        System.out.println("F1 = " + F1);

        File truthLabelFile = new File(System.getProperty("user.dir") + "/truthLabel.txt");

        Scanner scanner = null;
        try {
            scanner = new Scanner(truthLabelFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Integer> truthLabel = new ArrayList<Integer>();
        while(scanner.hasNext()) {
            int tmp = scanner.nextInt();
            truthLabel.add(tmp);
        }

        double snptsCorrect = 0;
        for (int i = 0; i < predict.size(); i++) {
            if (predict.get(i) == truthLabel.get(i)) {
                snptsCorrect += 1.0;
            }
        }

        System.out.println("Snpts level accuracy = " + snptsCorrect / predict.size());
    }

    public static void main(String args[]) {
        evaluate evaluate = new evaluate();
        evaluate.work();
    }
}
