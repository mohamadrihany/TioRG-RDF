/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.clustering;

import org.apache.jena.rdf.model.RDFNode;
import edu.uci.ics.jung.graph.Graph;
import fr.uvsq.adam.processings.GraphConstruction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Hashtable;

/**
 *
 * @author houk
 */
public class MCODEFindComplexes {

    public ArrayList<Graph> clustersList;

    /**
     * *
     * nodesWeight: les noeuds avec le poids calculé vwp le parametre d'entrée
     * (pourcentage) nodesNeighborWithoutLiterraux noeuds avec adjacents sans
     * les litereaux graph le graphe initial 
     */
    public MCODEFindComplexes(ArrayList<MCODEVertexWeiting.PairNodeWeight> nodesWeight, double vwp, ArrayList<MCODEVertexWeiting.TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux, Graph graph, double fluffThreshold) {

        Hashtable seens = new Hashtable();//pour vérifier si les noeuds ont deja été ajoutés a un cluster

        for (MCODEVertexWeiting.PairNodeWeight vInitiation : nodesWeight) {
            seens.put(vInitiation.getNode(), false);//initialisation
        }

        ArrayList<Collection<RDFNode>> clusters = new ArrayList<Collection<RDFNode>>();//la liste des clusters
        int j = 0;
        for (int i = nodesWeight.size() - 1; i >= 0; i--) {
            MCODEVertexWeiting.PairNodeWeight v = nodesWeight.get(i);
            if (seens.get(v.getNode()).equals(false)) {

                j++;

                Collection<RDFNode> cluster = new ArrayList<>();
                cluster.add(v.getNode());

                FindComplex(nodesWeight, vwp, seens, v.getNode(), cluster, nodesNeighborWithoutLiterraux, v.getWeight());

                clusters.add(cluster);
            }
        }

        //la construction des sous graphes à partir des collections de noeuds
        clustersList = new ArrayList();
        for (Collection<RDFNode> clusterr : clusters) {
            GraphConstruction subgraph = new GraphConstruction();
            Graph subgraphs = subgraph.getSubGraph(clusterr, true, graph);
            clustersList.add(subgraphs);
        }
        clustersList = MCODEResultsOptimisation.McodeFluffComplex(graph, fluffThreshold, clustersList);

    }

    /**
     * *
     * nodesWeight: les noeuds avec le poids calculé vwp le parametre d'entrée
     * (pourcentage) seens la list pour voir si les noeuds sont rajoutés à un
     * cluster ou non s le noeud de départ cluster le cluster à construire
     * nodesNeighborWithoutLiterraux les adjacents sans les litereaux weightS le
     * poids du noeuds de départ *
     */
    private void FindComplex(ArrayList<MCODEVertexWeiting.PairNodeWeight> nodesWeight, double vwp, Hashtable seens, RDFNode s, Collection<RDFNode> cluster, ArrayList<MCODEVertexWeiting.TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux, double weightS) {


        //recuperer les adjacents a rajouter au cluster s'ils ont les conditions nécéssaires (poids>pourcentage)
        seens.put(s, true);
        cluster.add(s);
        Collection<RDFNode> currentAdjacents = getRessourcesNeighbors(nodesNeighborWithoutLiterraux, s);
        for (RDFNode v : currentAdjacents) {
            double weight = 0;
            //récupérer le poid de l'adjacent en cours
        
            for (MCODEVertexWeiting.PairNodeWeight vertex : nodesWeight) {
                if (vertex.getNode().equals(v)) {
                    weight = vertex.getWeight();
                    break;
                }
            }
            if (!seens.get(v).equals(true)) {
                if (weight > weightS * (1 - vwp)) {
                    cluster.add(v);
                    seens.put(v, true);
                    FindComplex(nodesWeight, vwp, seens, v, cluster, nodesNeighborWithoutLiterraux, weight);
                }
            }
        }

    }

    private Collection<RDFNode> getRessourcesNeighbors(ArrayList<MCODEVertexWeiting.TupleNodeInternNeighbor> pairs, RDFNode node) {
        Collection<RDFNode> currentAdjacents = null;
        for (MCODEVertexWeiting.TupleNodeInternNeighbor pair : pairs) {
            if (pair.Node.equals(node)) {
                currentAdjacents = pair.InternNeighborNodes;
                break;
            }
        }
        return currentAdjacents;

    }
}
