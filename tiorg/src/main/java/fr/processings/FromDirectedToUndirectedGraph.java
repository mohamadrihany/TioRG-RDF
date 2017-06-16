/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.processings;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.EdgeType;

/**
 *
 * @author houk
 */
public class FromDirectedToUndirectedGraph {
    public static UndirectedSparseGraph<RDFNode, Statement> FromDirectedToUndirectedGraph(Graph<RDFNode, Statement> graph) {
   UndirectedSparseGraph<RDFNode, Statement> g = new UndirectedSparseGraph<>();

        for (Statement stat:graph.getEdges())
        {
            g.addEdge(stat, stat.getSubject(), stat.getObject(), EdgeType.UNDIRECTED);
        }
        return g;
        
    } 
}
