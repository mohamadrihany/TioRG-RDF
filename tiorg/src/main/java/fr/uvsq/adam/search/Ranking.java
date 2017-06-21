/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
            TupleResultScore tuple = new TupleResultScore(result, finalScore);
            finalResult.add(tuple);
        }
        SortScore(finalResult);
       
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
            float datarate1 = o1.getScore();
            float datarate2 = o2.getScore();

            if (datarate1 < datarate2) {
                return -1;
            } else if (datarate1 > datarate2) {
                return +1;
            } else {
                return 0;
            }
        }
    }

    public static class TupleResultScore {

        ResultConstruction.ResultNodeModel Result;
        float Score;

        public ResultConstruction.ResultNodeModel getResult() {
            return Result;
        }

        public float getScore() {
            return Score;
        }

        public TupleResultScore(ResultConstruction.ResultNodeModel result, float score) {
            Result = result;
            Score = score;
        }
    }
}
