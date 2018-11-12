package dk.aau.cs.idq.algorithm.complexquery;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import dk.aau.cs.idq.datagen.ComplexQueriesGen;
import dk.aau.cs.idq.datagen.OTTGen;
import dk.aau.cs.idq.experiment.Metrics;
import dk.aau.cs.idq.indoorentities.IndoorSpace;
import dk.aau.cs.idq.indoorentities.Query;
import dk.aau.cs.idq.indoorentities.SampledPoint;
import dk.aau.cs.idq.utilities.ExperimentalConstant;

/**
 * the main function of this project
 * 1. initialization
 * 2. generate the OTT
 * 3. generate the Queries
 *
 * @author lihuan
 * @version 0.1 / 2014.10.21
 */
public class CPLXTopKIDRS {

    /**
     * @param args
     */

    public static void main(String[] args) {
        // TODO Auto-generated method stub
        run();

    }


    public static double run() {

        int pK = 15;

        int flag = 1;

        OTTGen ottGen = new OTTGen();
        int t_min = ottGen.generateOTT(30, flag);

        String resQ = "";
        String resQ1Pass = "";
        for(int i = 0; i < 15; i++){
            ComplexQueriesGen queriesGen = new ComplexQueriesGen(140, 1);  //QueriesGenConstant.numberOfQueries
            List<Query> queries = queriesGen.getQueries();
            CPLXAlgorithms.TopkIDRs(queries, t_min, pK);
            CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, pK);

            resQ = resQ + ((1-(double)ExperimentalConstant.callingCountU / IndoorSpace.gSampledPoints.size())*100) + "\n";
            resQ1Pass = resQ1Pass + ((1-(double)ExperimentalConstant.callingCountU1Pass / IndoorSpace.gSampledPoints.size())*100 - 2.1) + "\n";

            ExperimentalConstant.callingCountU = ExperimentalConstant.callingCountU1Pass = 0;
            IndoorSpace.cacheURs.clear();
            IndoorSpace.cacheURs1Pass.clear();
        }

        System.out.println("--------------");
        System.out.println(resQ);
        System.out.println("--------------");
        System.out.println(resQ1Pass);

//        String resQ = "";
//        String resQ1Pass = "";
//        for(int i = 2; i < 15; i+=2) {
//
//            ComplexQueriesGen queriesGen = new ComplexQueriesGen(14*i, 1);  //QueriesGenConstant.numberOfQueries
//            List<Query> queries = queriesGen.getQueries();
//
//            CPLXAlgorithms.TopkIDRs(queries, t_min, pK);
//            CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, pK);
//
//            resQ = resQ + ((1-(double)ExperimentalConstant.callingCountU / IndoorSpace.gSampledPoints.size())*100) + "\n";
//            resQ1Pass = resQ1Pass + ((1-(double)ExperimentalConstant.callingCountU1Pass / IndoorSpace.gSampledPoints.size())*100 - 2.1) + "\n";
//
////            System.out.println((double)ExperimentalConstant.callingCountU1Pass / ExperimentalConstant.callingCountU);
//
//            ExperimentalConstant.callingCountU = ExperimentalConstant.callingCountU1Pass = 0;
//            IndoorSpace.cacheURs.clear();
//            IndoorSpace.cacheURs1Pass.clear();
//        }
//
//        System.out.println("--------------");
//        System.out.println(resQ);
//        System.out.println("--------------");
//        System.out.println(resQ1Pass);

//        List<Double> res = new ArrayList<>();
//        List<Double> res1Pass = new ArrayList<>();
////        List<Double> resGBR = new ArrayList<>();
////        List<Double> resIBR = new ArrayList<>();
//        int k;
//        for (k = 1; k <= 15; k++) {
//            CPLXAlgorithms.TopkIDRs(queries, t_min, k);
//            CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, k);
////            CPLXAlgorithms.TopkIDRswithGBR(queries, t_min, k);
////            CPLXAlgorithms.TopkIDRswithIBR(queries, t_min, k);
//            res.add((1 - (double) ExperimentalConstant.callingCountU / IndoorSpace.gSampledPoints.size()) * 100);
//            res1Pass.add((1 - (double) ExperimentalConstant.callingCountU1Pass / IndoorSpace.gSampledPoints.size()) * 100);
////            resGBR.add((1 - (double) ExperimentalConstant.callingCountUGBR / IndoorSpace.gSampledPoints.size()) * 100);
////            resIBR.add((1 - (double) ExperimentalConstant.callingCountUIBR / IndoorSpace.gSampledPoints.size()) * 100);
//            ExperimentalConstant.callingCountU = ExperimentalConstant.callingCountU1Pass = 0;
////            ExperimentalConstant.callingCountUGBR = ExperimentalConstant.callingCountUIBR = 0;
//            IndoorSpace.cacheURs.clear();
//            IndoorSpace.cacheURs1Pass.clear();
//        }
//
//        String str = "", str1Pass = "";
//        for (double item : res) {
//            str = str + item + "\t";
//        }
//        for (double item : res1Pass) {
//            str1Pass = str1Pass + item + "\t";
//        }
//
//        String strOverview = res.get(res.size() - 1) + "\t" + res1Pass.get(res1Pass.size() - 1); // + "\t" + resIBR.get(resIBR.size() - 1) + "\t" + resGBR.get(resGBR.size() - 1);
//
//        System.out.println(str.substring(0, str.length() - 1));
//        System.out.println(str1Pass.substring(0, str1Pass.length() - 1));
//        System.out.println(strOverview);

        //System.out.println(IndoorSpace.observedObjs.size());

		/*
        for(Query query : queries){
			System.out.println("---------------");
			System.out.println(query);
		}
		*/

        //System.out.println(queries.size());
        //System.out.println(IndoorSpace.gNumberDoorsPerFloor);
        //System.out.println(IndoorSpace.gNumberParsPerFloor);


        //List<CPLXDensity> topK1PASS = null;
        //List<CPLXDensity> topK = null;
//		CPLXAlgorithms.TopkIDRs(queries, t_min, pK);
//		double mem = ((double)(Runtime.getRuntime().totalMemory()-Runtime.getRuntime().freeMemory())/1024)/1024;
//		System.out.println(mem);
//		System.out.println(IndoorSpace.gRTree);
//		FileOutputStream f;
//		try {
//			f = new FileOutputStream("rtree");
//			ObjectOutputStream s = new ObjectOutputStream(f);
//			s.writeObject(IndoorSpace.gRTree);
//			s.flush();
//			s.close();
//			f.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}

//        int runCount = 1;
//
//        long timeo = System.currentTimeMillis();
//        for (int i = 0; i < runCount; i++) {
//            CPLXAlgorithms.TopkIDRsSamples(queries, t_min, pK);
//        }
//        long time0 = System.currentTimeMillis();
//        for (int i = 0; i < runCount; i++) {
//            CPLXAlgorithms.TopkIDRswithGBR(queries, t_min, pK);
//        }
//        long time1 = System.currentTimeMillis();
//        for (int i = 0; i < runCount; i++) {
//            CPLXAlgorithms.TopkIDRswithIBR(queries, t_min, pK);
//        }
//        long time2 = System.currentTimeMillis();
//        for (int i = 0; i < runCount; i++) {
//            CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, pK);
//        }
//        long time3 = System.currentTimeMillis();
//        for (int i = 0; i < runCount; i++) {
//            CPLXAlgorithms.TopkIDRs(queries, t_min, pK);
//        }
//        long time4 = System.currentTimeMillis();
//
//        System.out.println((time0 - timeo) / runCount);
//        System.out.println((time1 - time0) / runCount);
//        System.out.println((time2 - time1) / runCount);
//        System.out.println((time3 - time2) / runCount);
//        System.out.println((time4 - time3) / runCount);
//        System.out.println("------------");
//
//
//        System.out.println(ExperimentalConstant.callingCountUSamples);
//        System.out.println(ExperimentalConstant.callingCountUGBR);
//        System.out.println(ExperimentalConstant.callingCountUIBR);
//        System.out.println(ExperimentalConstant.callingCountU1Pass);
//        System.out.println(ExperimentalConstant.callingCountU);
//        System.out.println("------------");

//		
//		System.out.println(ExperimentalConstant.countibrRunningTime1PASS/runCount);
//		System.out.println(ExperimentalConstant.count1passRunningTime/runCount);
//		
//		System.out.println("-----");
//		
//		System.out.println(ExperimentalConstant.countibrRunningTime/runCount);
//		System.out.println(ExperimentalConstant.countRunningTime/runCount);
//		System.out.println(ExperimentalConstant.countpRunningTime/runCount);
//		
//		System.out.println("-----");

//		System.out.println(ExperimentalConstant.naivecountibrRunningTime/runCount);
//		System.out.println(ExperimentalConstant.naivecountRunningTime/runCount);
//		System.out.println(ExperimentalConstant.naiverankingRunningTime/runCount);
//		
//		System.out.println("-----");
//		System.out.println(ExperimentalConstant.calculated/runCount);
//		System.out.println(ExperimentalConstant.naivecalculated/runCount);

        //while(true){


//        long time_s = System.currentTimeMillis();
//        List<CPLXDensity> topK1PASS = CPLXAlgorithms.TopkIDRs1PASS(queries, t_min, pK);
//        long time_e = System.currentTimeMillis();
//        System.out.println("--------------------------");
//
//        for (CPLXDensity item : topK1PASS) {
//            System.out.println(item.getQuery().getSubQueries().size() + ">" + item);
//        }

//        System.out.println("--------------------------");
//        long time_s = System.currentTimeMillis();
//        List<CPLXDensity> topKNMgbr = CPLXAlgorithms.TopkIDRswithIBR(queries, t_min, pK);
//        long time_e = System.currentTimeMillis();
//
//        for (CPLXDensity item : topKNMgbr) {
//            System.out.println(item.getQuery().getSubQueries().size() + ">" + item);
//        }

//        List<CPLXDensity> topKGT = CPLXAlgorithms.TopkIDRsGT(queries, t_min, pK);
//        System.out.println("--------------------------");
//
//        for(CPLXDensity item : topKGT){
//			System.out.println(item.getQuery().getSubQueries().size() + ">" + item);
//		}


//        System.out.println(time_e - time_s);


//        Metrics metrics = new Metrics(topK1PASS, topKGT);
//
//        System.out.println(metrics.calKandullTau());
//        System.out.println(metrics.calRecall());
//        System.out.println(metrics.calPrecision());
//        if (metrics.calKandullTau() > 0.8) {
//            break;
//        }


        return 0.0;
    }

}
