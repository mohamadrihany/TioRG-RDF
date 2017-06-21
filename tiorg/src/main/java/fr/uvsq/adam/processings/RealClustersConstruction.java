/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author houk
 */
public class RealClustersConstruction {

    public static ArrayList<Graph> main(Graph<RDFNode, Statement> globalGraph, ArrayList<Graph> clusterList, ArrayList<SharedFeature.PairSourceSet> listResourceRDFNodes) {
        ArrayList<Graph> finalClusterList = new ArrayList<>();
        finalClusterList = clusterListWithRealResources(globalGraph, clusterList, listResourceRDFNodes);
        finalClusterList = clusterListWithLiteralsCreation(globalGraph, finalClusterList);
        return finalClusterList;
    }

    public static ArrayList<Graph> clusterListWithRealResources(Graph<RDFNode, Statement> globalGraph, ArrayList<Graph> clusterList, ArrayList<SharedFeature.PairSourceSet> listResourceRDFNodes) {
        ArrayList<Graph> finalClusterList = new ArrayList<>();
        for (Graph<RDFNode, Statement> cluster : clusterList) {
            Collection<RDFNode> realResources = new ArrayList<>();
            for (RDFNode vertex : cluster.getVertices()) {
                realResources.addAll(getRessourcesSet(listResourceRDFNodes, vertex));
            }
            //realResources.addAll(cluster.getVertices());


            Graph subgraphs = createSubgraph(realResources, globalGraph);
            finalClusterList.add(subgraphs);
        }
        return finalClusterList;

    }

    public static ArrayList<Graph> clusterListWithLiteralsCreation(Graph<RDFNode, Statement> globalGraph, ArrayList<Graph> clusterList) {
        ArrayList<Graph> finalClusterList = new ArrayList<>();
        for (Graph<RDFNode, Statement> cluster : clusterList) {
            Collection<RDFNode> literalsNeighbors = new ArrayList<>();
            for (RDFNode vertex : cluster.getVertices()) {
                for (RDFNode neighbor : globalGraph.getNeighbors(vertex)) {
                    if (neighbor.isLiteral()) {
                        literalsNeighbors.add(neighbor);
                    }
                }
            }
            literalsNeighbors.addAll(cluster.getVertices());
            Graph subgraphs = createSubgraph(literalsNeighbors, globalGraph);
            finalClusterList.add(subgraphs);
        }
        return finalClusterList;

    }

    public static ArrayList<RDFNode> getRessourcesSet(ArrayList<SharedFeature.PairSourceSet> pairs, RDFNode node) {
        ArrayList<RDFNode> realRessourcesList = new ArrayList();
        for (SharedFeature.PairSourceSet pair : pairs) {
            if (pair.getSource().equals(node)) {
                realRessourcesList = pair.getSet();
                break;
            }
        }
        return realRessourcesList;

    }

    public static Graph createSubgraph(Collection<RDFNode> vertices, Graph graph) {
        Graph subgraph = null;

        subgraph = new SparseGraph();
        Collection<RDFNode> graphVertices1 = graph.getVertices();
        Collection<String> graphVertices = graph.getVertices();
        for (RDFNode vertex : graphVertices1) {
            if (vertex.isLiteral()) {
                graphVertices.add(vertex.asLiteral().getValue().toString());
            } else {
                graphVertices.add(vertex.asResource().getLocalName());
            }
        }
        for (RDFNode v : vertices) {
            if (v.isLiteral()) {
                if (!graphVertices.contains(v.asLiteral().getValue().toString())) {
                    throw new IllegalArgumentException("Vertex " + v
                            + " is not an element of " + graph);
                }
            }
            if (v.isResource()) {
                if (!graphVertices.contains(v.asResource().getLocalName())) {
                    throw new IllegalArgumentException("Vertex " + v
                            + " is not an element of " + graph);
                }
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
