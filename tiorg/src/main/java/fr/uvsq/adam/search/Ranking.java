/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import org.apache.jena.rdf.model.NsIterator;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.jdom2.Element;

/**
 *
 * @author hanane
 */
public class Ranking {

    private Ranking() {
    }

    public static ArrayList<TupleResultScore> main(ArrayList<ResultConstruction.ResultNodeModel> resultList, LuceneSearch.PairLuceneScore pairLuceneScore, Element racine) throws Exception {
        ArrayList<TupleResultScore> finalResult = new ArrayList<>();
        float minScore = 100.0f;
          float maxScore = 0.0f;
 for (ResultConstruction.ResultNodeModel result : resultList) {
     float luceneScoreResult = LuceneRanking.main(result, pairLuceneScore);
     if(minScore>luceneScoreResult){
         minScore=luceneScoreResult;
     }
      if(maxScore<luceneScoreResult){
         maxScore=luceneScoreResult;
     }
 }
        float inter = maxScore-minScore;
        for (ResultConstruction.ResultNodeModel result : resultList) {

            float luceneScoreResult = LuceneRanking.main(result, pairLuceneScore);
            float inter2 = luceneScoreResult-minScore;
            float luceneScoreResultFinal = (inter!=0?inter2/inter:0);
            float sizeScore = SizeRanking.main(result, racine);
            float finalScore = (sizeScore + luceneScoreResultFinal) / 2;
            //TupleResultScore tuple = new TupleResultScore(result, finalScore);

            HashMap<String,String> RankingMap = ElementsManagment.RankingMap;
            int numSynonym = 0;
            int numAntonym = 0;
            int numHyponym = 0;
            int numHypernym = 0;
            int numHolonym = 0;
            int numMeronym = 0;
            StmtIterator iter;
            iter = result.Model.listStatements();
            while(iter.hasNext()){
                Statement statement = iter.next();
                String predicate =statement.getPredicate().toString();
                String subject = statement.getSubject().getLocalName();
                String object = statement.getObject().toString();
                if(RankingMap.containsKey(predicate)){
                    String wordNet = RankingMap.get(predicate);
                    if(wordNet.equals("synonym")){
                        numSynonym++;}
                    else if(wordNet.equals("Antonyms")){
                        numAntonym++;}
                    else if(wordNet.equals("Hyponyms")){
                        numHyponym++;}
                    else if(wordNet.equals("Hyprenyms")){
                        numHypernym++;}
                    else if(wordNet.equals("Holonyms")){
                        numHolonym++;}
                    else if(wordNet.equals("Meronyms")){
                        numMeronym++;}
                    }
                if(RankingMap.containsKey(subject)){
                    String wordNet = RankingMap.get(subject);
                    if(wordNet.equals("synonym")){
                        numSynonym++;}
                    if(wordNet.equals("Antonyms")){
                        numAntonym++;}
                    if(wordNet.equals("Hyponyms")){
                        numHyponym++;}
                    if(wordNet.equals("Hyprenyms")){
                        numHypernym++;}
                    if(wordNet.equals("Holonyms")){
                        numHolonym++;}
                    if(wordNet.equals("Meronyms")){
                        numMeronym++;}
                }
                if(RankingMap.containsKey(object)){
                    String wordNet = RankingMap.get(object);
                    if(wordNet.equals("synonym")){
                        numSynonym++;}
                    if(wordNet.equals("Antonyms")){
                        numAntonym++;}
                    if(wordNet.equals("Hyponyms")){
                        numHyponym++;}
                    if(wordNet.equals("Hyprenyms")){
                        numHypernym++;}
                    if(wordNet.equals("Holonyms")){
                        numHolonym++;}
                    if(wordNet.equals("Meronyms")){
                        numMeronym++;}
                }
            }
            int numLevel1 = numSynonym+numAntonym+numHypernym;
            int numLevel2 = numHyponym+numHolonym+numMeronym;

            //double total = numAntonym+numHolonym+numHypernym+numMeronym+numSynonym+numHyponym;

            double lastScore = numSynonym*0.25 + numAntonym*0.25 + numHypernym*0.20 + numHyponym*0.15 + numMeronym*0.10 + numHolonym*0.05;
            /*ArrayList<Double> Score = new ArrayList<>();
            Score.add(perSynonym);
            Score.add(perAntonym);
            Score.add(perHyponym);
            Score.add(perHypernym);
            Score.add(perHolonym);
            Score.add(perMerom);*/

            long size = result.Model.size();
            TupleResultScore tuple = new TupleResultScore(result, lastScore,size, numLevel1,  numLevel2,  numSynonym,  numAntonym,
             numHyponym,  numHypernym,  numHolonym,  numMeronym);
            finalResult.add(tuple);


        }
        SortScore(finalResult);
        //Collections.sort(finalResult);
        //int i=finalResult.size()+1    ;
        //for(TupleResultScore resu : finalResult){
        //    resu.Score=i--;
        //}
       
        for (TupleResultScore resu : finalResult) {
            System.out.println(resu.Score);
            System.out.println(resu.Result.Model.size());
            System.out.println(resu.Result.Model.toString());
        }
        return finalResult;

    }

    private static void SortScore(ArrayList<TupleResultScore> nodesDegree) {
        SortScore com = new SortScore();
        Collections.sort(nodesDegree, com);
    }


    public static class SortScore implements Comparator<TupleResultScore> {

        @Override
        public int compare(TupleResultScore o1, TupleResultScore o2) {
            double datarate1 = o1.getScore();
            double datarate2 = o2.getScore();

            if (datarate1 < datarate2) {
                return -1;
            } else if (datarate1 > datarate2) {
                return +1;
            } else {
                return 0;
            }
        }

    }

    public static class TupleResultScore implements Comparable<TupleResultScore> {

        ResultConstruction.ResultNodeModel Result;
        double Score;
        long Size;
        int numLevel1;
        int numLevel2;
        int numSynonym;
        int numAntonym;
        int numHyponym;
        int numHypernym;
        int numHolonym;
        int numMeronym;



        public ResultConstruction.ResultNodeModel getResult() {
            return Result;
        }

        public double getScore() {
            return Score;
        }
        public int getnumlevel1(){
            return numLevel1;
        }

        public int getNumSynonym() {
            return numSynonym;
        }

        public int getNumAntonym() {
            return numAntonym;
        }

        public int getNumHyponym() {
            return numHyponym;
        }

        public int getNumHypernym() {
            return numHypernym;
        }

        public int getNumHolonym() {
            return numHolonym;
        }

        public int getNumMeronym() {
            return numMeronym;
        }

        public int getnumlevel2(){

            return numLevel2;
        }



        public TupleResultScore(ResultConstruction.ResultNodeModel result, double score) {
            Result = result;
            Score = score;
        }
        //
        public TupleResultScore(ResultConstruction.ResultNodeModel result, double score, long size, int numLevel1, int numLevel2, int numSynonym, int numAntonym,
                                int numHyponym, int numHypernym, int numHolonym, int numMeronym) {
            Result = result;
            Score = score;
            Size = size;
            this.numLevel1 = numLevel1;
            this.numLevel2 = numLevel2;
            this.numSynonym = numSynonym;
            this.numAntonym = numAntonym;
            this.numHyponym = numHyponym;
            this.numHypernym = numHypernym;
            this.numHolonym = numHolonym;
            this.numMeronym = numMeronym;
        }

        @Override
        public int compareTo(TupleResultScore o) {
            return Comparator.comparing(TupleResultScore::getnumlevel1)
                    .thenComparingInt(TupleResultScore::getNumSynonym)
                    .thenComparingInt(TupleResultScore::getNumAntonym)
                    .thenComparingInt(TupleResultScore::getNumHypernym)
                    .thenComparingInt(TupleResultScore::getnumlevel2)
                    .thenComparingInt(TupleResultScore::getNumHyponym)
                    .thenComparingInt(TupleResultScore::getNumHolonym)
                    .thenComparingInt(TupleResultScore::getNumMeronym)
                    .compare(this, o);
        }
        //
    }
}
