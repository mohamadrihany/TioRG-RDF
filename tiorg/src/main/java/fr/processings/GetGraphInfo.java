/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.processings;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.Graph;
import java.util.ArrayList;

/**
 *
 * @author houk
 */
public class GetGraphInfo {

    public static ArrayList<String> GetPredicatesList(Graph<RDFNode, Statement> globalGraph) {
        ArrayList<String> existingdEdgesList = new ArrayList();
        //  model creation

        for (Statement edge : globalGraph.getEdges()) {
            if (existingdEdgesList.isEmpty()) {
                if (!globalGraph.getDest(edge).isLiteral()) {
                    existingdEdgesList.add(edge.getPredicate().getLocalName());
                }
            } else {
                boolean exist = false;
                for (String statement : existingdEdgesList) {
                    if (statement.equals(edge.getPredicate().getLocalName())) {
                        exist = true;
                        break;
                    }
                }
                if (!exist) {
                    if (!globalGraph.getDest(edge).isLiteral()) {
                        existingdEdgesList.add(edge.getPredicate().getLocalName());
                    }
                }
            }
        }
        return existingdEdgesList;
    }
}
