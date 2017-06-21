/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import fr.uvsq.adam.processings.FileToModelGraph;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.DirectedGraph;

import java.util.Hashtable;

/**
 *
 * @author houk
 */
public class LuceneRanking {

    public static float main(ResultConstruction.ResultNodeModel result, LuceneSearch.PairLuceneScore pairLuceneScore ) throws Exception {
        float size = 0;
        float totalScore = 0;
        float finalScore = 0;
        Hashtable<String, Float> luceneScore = pairLuceneScore.LuceneScore;
        Hashtable<String, Float> luceneScoreEdge = pairLuceneScore.LuceneScoreEdge;
        if (result.TypeResult.equals("model")) {
            DirectedGraph<RDFNode, Statement> graphResult = FileToModelGraph.ModelToGraph(result.getModel());
            for (RDFNode node : graphResult.getVertices()) {
                size++;
                Float score = 0.00f;
                if (node.isLiteral()) {
                    if (luceneScore.containsKey(node.asLiteral().getString())) {
                        score = luceneScore.get(node.asLiteral().getString());
                        totalScore = totalScore + score;
                    }
                }

                if (node.isURIResource()) {
                    if (luceneScore.containsKey(node.asResource().getURI())) {
                        score = luceneScore.get(node.asResource().getURI());
                        totalScore = totalScore + score;
                    }
                }
            }
            for (Statement stat : graphResult.getEdges()) {
                size++;
                 Float score = 0.00f;
                  if (luceneScoreEdge.containsKey(stat.getPredicate().getURI())) {
                        score = luceneScoreEdge.get(stat.getPredicate().getURI());
                        totalScore = totalScore + score;
                    }
            }
            finalScore = (size!=0 ? totalScore / size : 0);
        } else {
              size++;
                Float score = 0.00f;
            RDFNode nodeResult = result.Node;
             if (nodeResult.isLiteral()) {
                    if (luceneScore.containsKey(nodeResult.asLiteral().getString())) {
                        score = luceneScore.get(nodeResult.asLiteral().getString());
                        totalScore = totalScore + score;
                    }
                }

                if (nodeResult.isURIResource()) {
                    if (luceneScore.containsKey(nodeResult.asResource().getURI())) {
                        score = luceneScore.get(nodeResult.asResource().getURI());
                        totalScore = totalScore + score;
                    }
                }
        
        }


        return finalScore;

    }
}
