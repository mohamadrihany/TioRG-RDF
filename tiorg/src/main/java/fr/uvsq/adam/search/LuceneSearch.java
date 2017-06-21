/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.DirectedGraph;
import java.util.Hashtable;
import org.jdom2.Element;

/**
 *
 * @author houk
 */
public class LuceneSearch {

    public static PairLuceneScore main(String dirIndex, String keywords, DirectedGraph<RDFNode, Statement> graph, Model model, Element racine) throws Exception {

        SearchElements.main(dirIndex, keywords, graph, model, racine);
        PairLuceneScore pairLuceneScore = new PairLuceneScore(SearchElements.luceneScore,SearchElements.luceneScoreEdge);
        
        return pairLuceneScore;
    }

    public static class PairLuceneScore {

        Hashtable<String, Float> LuceneScore;
        Hashtable<String, Float> LuceneScoreEdge;

        public Hashtable<String, Float> getLuceneScore() {
            return LuceneScore;
        }

        public Hashtable<String, Float> getLuceneScoreEdge() {
            return LuceneScoreEdge;
        }

        public PairLuceneScore(Hashtable<String, Float> luceneScore, Hashtable<String, Float> luceneScoreEdge) {
            LuceneScoreEdge = luceneScoreEdge;

            LuceneScore = luceneScore;
        }
    }
}
