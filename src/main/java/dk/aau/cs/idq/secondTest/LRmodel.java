package dk.aau.cs.idq.secondTest;

import dk.aau.cs.idq.datagen.TraDataGen;
import dk.aau.cs.idq.semantics.Splitting;
import smile.classification.LogisticRegression;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class LRmodel {

    public static int boundary = 300;
    public static int p = 10;

    public double[][] getData(int n, int bot, int top) {

        double ans[][] = new double[n][p];

        int cnt = 0;
        for (int i = bot; i < top; i++) {
            File file = new File(System.getProperty("user.dir") + "/Feature/person" + i + ".txt");
            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(in.hasNext()) {
                for (int j = 0; j < p; j++) {
                    ans[cnt][j] = in.nextDouble();
                }
                cnt++;
            }
            //System.out.println(i);
        }

        return ans;
    }

    public int[] getLabel(int bot, int top) {
        ArrayList<Integer> ans = new ArrayList<Integer>();

        for (int i = bot; i < top; i++) {
            File file = new File(System.getProperty("user.dir") + "/Label/person" + i + ".txt");
            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(in.hasNext()) {
                int label = in.nextInt();
                ans.add(label);
            }
            //System.out.println(i);
        }

        int res[] = new int[ans.size()];

        for (int i = 0; i < ans.size(); i++) {
            res[i] = ans.get(i);
        }

        return res;
    }

    public void write(ArrayList<Integer> ans) {

        File file = new File(System.getProperty("user.dir") + "/predict.txt");

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ans.size(); i++) {
            try {
                fw.write(ans.get(i) + "\n");
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

    public ArrayList<Integer> predict(int bot, int top, LogisticRegression logisticRegression) {

        ArrayList<Integer> ans = new ArrayList<Integer>();
        double x[] = new double[p];
        for (int i = bot; i < top; i++) {
            File file = new File(System.getProperty("user.dir") + "/Feature/person" + i + ".txt");
            Scanner in = null;
            try {
                in = new Scanner(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(in.hasNext()) {
                for (int j = 0; j < p; j++) {
                    x[j] = in.nextDouble();
                }
                int predictLabel = logisticRegression.predict(x);
                ans.add(predictLabel);
            }
        }

        return ans;
    }

    public void model() {

        System.out.println("running");
        int label[] = getLabel(0, boundary);
        double trainingData[][] = getData(label.length, 0, boundary);

        System.out.println("AAAA");
        System.out.println(label.length);
        System.out.println(trainingData.length);
        System.out.println("BBBB");

        LogisticRegression logisticRegression = new LogisticRegression(trainingData, label, 0.1);

        ArrayList<Integer> ans = predict(boundary, TraDataGen.totalPerson, logisticRegression);

        System.out.println("running");

        write(ans);
    }

    public void init() {

        ArrayList<Integer> ans = new ArrayList<Integer>();
        for (int i = boundary; i < TraDataGen.totalPerson; i++) {
            File labelfile = new File(System.getProperty("user.dir") + "/Label/person" + i + ".txt");
            Scanner in = null;
            try {
                in = new Scanner(labelfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(in.hasNext()) {
                int label = in.nextInt();
                ans.add(label);
            }
            System.out.println(i);
        }

        File file = new File(System.getProperty("user.dir") + "/truthLabel.txt");

        FileWriter fw = null;

        try {
            fw = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < ans.size(); i++) {
            try {
                fw.write(ans.get(i) + "\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("---->" + i);
        }

        try {
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * write
     */
    public static void writeLabel() {
        File file = new File(System.getProperty("user.dir") + "/predict.txt");

        File dir = new File(System.getProperty("user.dir") + "/predictLabel");

        dir.mkdir();

        ArrayList<Integer> label = new ArrayList<Integer>();

        Scanner input = null;

        try {
            input = new Scanner(file);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while(input.hasNext()) {
            int tmp = input.nextInt();
            label.add(tmp);
        }

        int p = 0;

        for (int i = boundary; i < TraDataGen.totalPerson; i++) {

            File predictFile = new File(System.getProperty("user.dir") + "/predictLabel/person" + i + ".txt");

            File snptFile = new File(System.getProperty("user.dir") + "/Snippet/person" + i + ".txt");

            Scanner in = null;
            FileWriter out = null;

            try {
                in = new Scanner(snptFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                out = new FileWriter(predictFile);
            } catch (IOException e) {
                e.printStackTrace();
            }

            int cnt = 0;
            while(in.hasNext()) {
                for (int j = 0; j < 5; j++) {
                    int tmp = in.nextInt();
                }
                cnt++;
            }

            for (int j = 0; j < cnt; j++) {
                try {
                    out.write(label.get(p + j) + "\n");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            p += cnt;
        }
    }

    public static void main(String args[]) {
        DataSet dataSet = new DataSet();
        dataSet.init();
        dataSet.createFeature();

        LRmodel lrmodel  = new LRmodel();
        lrmodel.init();
        lrmodel.model();

        evaluate evaluate = new evaluate();
        evaluate.work();

        writeLabel();
/*
        lrmodel.init();

        lrmodel.model();

        evaluate evaluate = new evaluate();
        evaluate.work();
*/
/*
        evaluate evaluate = new evaluate();
        DataSet dataSet = new DataSet();
        dataSet.init();

        //dataSet.createFeature();
        double mxAcc = 0;
        double ansB = 0, ansP = 0, ansN = 0, ansEtaS = 0, ansDeltaS = 0;

        //for (int B = 1; B <= 2; B++) {
        //    for (int P = 2; P <= 6; P++) {
        //        for (int N = 24; N <= 40; N++) {
                    for (int etaS = 4; etaS <= 6; etaS++) {
                        for (int deltaS = 4; deltaS <= 8; deltaS++) {
                            //Splitting.constB = B;
                            //Splitting.constN = N;
                            //Splitting.constP = P;
                            Splitting.deltaS = deltaS;
                            Splitting.etaS = etaS;

                            dataSet.createFeature();
                            lrmodel.init();
                            lrmodel.model();
                            evaluate.work();

                            if (evaluate.accuracy > mxAcc) {
                                mxAcc = evaluate.accuracy;
                                //ansB = B;
                                //ansN = N;
                                //ansP = P;
                                ansEtaS = etaS;
                                ansDeltaS = deltaS;
                            }
                        }
                    }
         //       }
        //    }
        //}

        System.out.println("B = " + ansB + " P = " + ansP + " N = " + ansN);
        System.out.println("etaS = " + ansEtaS + "deltaS = " + ansDeltaS);
*/
    }
}



