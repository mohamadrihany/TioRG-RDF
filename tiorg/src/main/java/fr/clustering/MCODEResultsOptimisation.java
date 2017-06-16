/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.clustering;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.Graph;
import fr.clustering.MCODEVertexWeitingWeightedGraph.MyLink;
import fr.clustering.MCODEVertexWeitingWeightedGraph.PairNodeWeight;
import fr.processings.GraphConstruction;
import fr.processings.GraphFilter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 *
 * @author houk
 */
public class MCODEResultsOptimisation {

    /**
     *
     * @param graph
     * @param nodesWeight
     * @param fluffThreshold
     * @param clusters
     * @param seens
     */
    public static ArrayList<Graph> McodeFluffComplex(Graph graph, double fluffThreshold, ArrayList<Graph> clusters) {
       
        ArrayList<Graph> newClusters = new ArrayList();
        for (Graph cluster : clusters) {
            Collection<RDFNode> vertices = cluster.getVertices();
          
            Collection<RDFNode> vertices2 = new ArrayList(vertices);
            for (RDFNode node : vertices) {
               
                Collection<RDFNode> neighbors2 = graph.getNeighbors(node);
                Collection<RDFNode> neighbors = new ArrayList(neighbors2);
                neighbors.add(node);
                double weight;
                weight = getDensity(neighbors, graph);
                if (weight > (1 - fluffThreshold)) {
                    vertices2.addAll(neighbors);
                }
            }
            GraphConstruction subgraph = new GraphConstruction();
            Graph clusterr = subgraph.getSubGraph(vertices2, true, graph);

            newClusters.add(clusterr);

        }
        
           
        System.out.println("temps fin " + System.currentTimeMillis());
        
        return newClusters;

    }

    public static ArrayList<Graph> McodeFluffComplexWeightedGraph(Graph graph, ArrayList<PairNodeWeight> nodesWeight, double fluffThreshold, ArrayList<Graph> clusters, Hashtable seens) {
       
        ArrayList<Graph> newClusters = new ArrayList();
        for (Graph cluster : clusters) {
            Collection<RDFNode> vertices = cluster.getVertices();
            Collection<RDFNode> vertices2 = new ArrayList(vertices);
            for (RDFNode node : vertices) {
                Collection<RDFNode> neighbors2 = graph.getNeighbors(node);
                Collection<RDFNode> neighbors = new ArrayList(neighbors2);
                neighbors.add(node);
                double weight;
                weight = getDensityWeightedGraph(neighbors, graph);
                if (weight > (1 - fluffThreshold)) {
                    vertices2.addAll(neighbors);
                }
            }
            GraphConstruction subgraph = new GraphConstruction();
            Graph clusterr = subgraph.getSubGraph(vertices2, true, graph);

            newClusters.add(clusterr);
        }

        return newClusters;
    }

    private static double getDensityWeightedGraph(Collection<RDFNode> nodes, Graph<RDFNode, MyLink> graphe) {
        double weight = 0;
        Graph<RDFNode, MyLink> subGraph = GraphFilter.ConstructSubGraphMyLink(graphe, nodes);
        double eages = 0;
        for (RDFNode node : nodes) {

            for (RDFNode adj : nodes) {
                if (subGraph.containsEdge(subGraph.findEdge(adj, node))) {
                    eages = eages + subGraph.findEdge(adj, node).weight;
                } else if (subGraph.containsEdge(subGraph.findEdge(node, adj))) {
                    eages = eages + subGraph.findEdge(node, adj).weight;
                }
            }
        }

        int vertex = nodes.size();

        weight = eages / (double) (vertex * (vertex - 1));

        return weight;
    }

    private static double getDensity(Collection<RDFNode> nodes, Graph<RDFNode, Statement> graphe) {
        double weight = 0;
        Graph subGraph = GraphFilter.ConstructSubGraph(graphe, nodes);

        int eages = subGraph.getEdgeCount();

        int vertex = nodes.size();

        weight = (double) (2 * eages) / (double) (vertex * (vertex - 1));
        return weight;
    }
}