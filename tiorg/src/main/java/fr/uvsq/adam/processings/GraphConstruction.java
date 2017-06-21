/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import org.apache.jena.rdf.model.RDFNode;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import java.util.Collection;

/**
 *
 * @author houk
 */
public class GraphConstruction {

    /**
     * Get the subgraph from given nodes (all interconnected nodes will be added
     * if enabled).
     *
     * @param nodes the subgraph nodes.
     * @param isInterConnectedNodes if true all interconnected nodes will be
     * added.
     */
    public Graph getSubGraph(Collection<RDFNode> nodes,
            boolean isInterConnectedNodes, Graph graph) {


        return createInducedSubgraph(nodes, graph);
    }

    public static Graph createInducedSubgraph(Collection<RDFNode> vertices, Graph graph) {
        Graph subgraph = null;

        subgraph = new SparseGraph(); // Graph.class.newInstance();//(G)graph.getClass().newInstance();

        for (RDFNode v : vertices) {
            if (!graph.containsVertex(v)) {
                throw new IllegalArgumentException("Vertex " + v
                        + " is not an element of " + graph);
            }
            subgraph.addVertex(v);
        }

        for (Object e : graph.getEdges()) {
            Collection<RDFNode> incident = graph.getIncidentVertices(e);
            if (vertices.containsAll(incident)) {
                subgraph.addEdge(e, incident, graph.getEdgeType(e));
            }
        }

        return subgraph;
    }
}
