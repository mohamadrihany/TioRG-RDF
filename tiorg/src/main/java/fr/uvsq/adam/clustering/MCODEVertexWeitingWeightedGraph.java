
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.clustering;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;
import java.util.List;

/**
 *
 * @author houk
 */
public class MCODEVertexWeitingWeightedGraph {

    public ArrayList<Graph> VertexWeiting(DirectedSparseGraph<RDFNode, MyLink> weitingWeightedGraph, double param, double fluffThreshold, JDesktopPane jDesktopPANE) throws ClassNotFoundException, HeadlessException, IOException, PropertyVetoException {
        Collection<RDFNode> vertices = weitingWeightedGraph.getVertices();
        //couples noeud-poids
        ArrayList<PairNodeWeight> nodesWeight = new ArrayList<>();
        //couple noeud-degree(somme des poids des arcs adjacents)
        ArrayList<TupleNodeDegree> nodesDegree = new ArrayList<>();
        //couple noeud-l'ensemble d'adjacents sans litteraux
        ArrayList<TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux = new ArrayList<>();

        //parcourir tous les noeuds du graphe pour sauvegarder les degrees et les adjacents qui sont des ressources (sans literaux)
        for (RDFNode currentNode : vertices) {
            int currentDegree = 0;
            Collection<RDFNode> NeighborsNodewithoutliteral = new ArrayList<>();
            Collection<RDFNode> currentAdjacents = weitingWeightedGraph.getNeighbors(currentNode);

            for (RDFNode nodes : currentAdjacents) {

                if (weitingWeightedGraph.containsEdge(weitingWeightedGraph.findEdge(currentNode, nodes))) {
                    currentDegree = currentDegree + weitingWeightedGraph.findEdge(currentNode, nodes).weight;
                } else {
                    currentDegree = currentDegree + weitingWeightedGraph.findEdge(nodes, currentNode).weight;
                }
                NeighborsNodewithoutliteral.add(nodes);

            }

            TupleNodeDegree degree = new TupleNodeDegree(currentNode, currentDegree);
            TupleNodeInternNeighbor neighborwithoutliterraux = new TupleNodeInternNeighbor(currentNode, NeighborsNodewithoutliteral);
            //noeud+degree
            nodesDegree.add(degree);

            // noeud+adjacents ressources
            nodesNeighborWithoutLiterraux.add(neighborwithoutliterraux);

        }

//        //Vertex Weighting
//        //1. N=find neighbors of v to depth 1 
        for (TupleNodeDegree pair : nodesDegree) {
            RDFNode currentNode2 = pair.Node;
            // former le sous graphe et compris le noeud courant stocké dans adjacentsNodes2
            Collection<RDFNode> adjacentsNodes = getRessourcesNeighbors(nodesNeighborWithoutLiterraux, currentNode2);
            Collection<RDFNode> adjacentsNodes2 = new ArrayList(adjacentsNodes);
            adjacentsNodes2.add(currentNode2);
            //2.K=Get highest k-core graph from N
            //pair==>densite of highest k-core and k
            Pair highestKcore = GetHighestKcore(adjacentsNodes2, nodesNeighborWithoutLiterraux, weitingWeightedGraph);

            float weight = highestKcore.Densite * highestKcore.K;

            PairNodeWeight nodeWeight = new PairNodeWeight(currentNode2, weight);
            nodesWeight.add(nodeWeight);
        }
        //On fait un ordre croissant mais dans la creation des clusters on fait un parcours du haut au bas.
        SortTupleWeight(nodesWeight);

        MCODEFindComplexesWeightedGraph complexes = new MCODEFindComplexesWeightedGraph(nodesWeight, param, nodesNeighborWithoutLiterraux, weitingWeightedGraph, fluffThreshold);
        ArrayList<Graph> finalClustersList = complexes.clustersList;

        return finalClustersList;
    }

//    /** 
    //toute la methode est pour le calcul du poids interne au sous graphe pour chaque noeuds ainsi que ses adjacents interne
//     * Gets the highest K-core in a given sub graph. used to calculate the
//     * weight of the nodes
//     *
//     * @param adjacentsNodes:les adjacents et le noeud lui meme
//     * @param nodesNeighborWithoutLiterraux les adjacents sans litteraux
//     * @param currentNode2 : le noeud pour lequel on calcul le poids
//     * @return the density and k of the highest kcore
//     */
    private Pair GetHighestKcore(Collection<RDFNode> adjacentsNodes, ArrayList<TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux, DirectedSparseGraph<RDFNode, MyLink> weitingWeightedGraph) {
        //couple noeud, degree dans le sous graphe  trouvé qui est l'ensemble des adjacents du noeud donné
        ArrayList<TupleNodeDegree> nodesDegreeIntern = new ArrayList<>();
        //couple noeud, adjacent dans le sous graphe  trouvé qui est l'ensemble des adjacents du noeud donné
        ArrayList<TupleNodeInternNeighbor> nodesNeighborsIntern = new ArrayList<>();

        int size = adjacentsNodes.size();
        float coreDensity = 0;
        ArrayList<TupleNodeDegree> K1 = new ArrayList<>();
        Hashtable K = new Hashtable();
        Collection<RDFNode> kCore = new ArrayList<>();
        int kCoreDegree = 0;
        int kcurrentNode2 = 0;
        //si le noeud n'est pas seul et qu'il a au moins un adjacent
        if (size > 2) {
            //2.1Compute degree G(v) (i.e., le degree interne) for all vertices v 2 V
            //pour chaque noeud du sous graphe
            for (RDFNode node : adjacentsNodes) {

                Collection<RDFNode> neighborsNode = getRessourcesNeighbors(nodesNeighborWithoutLiterraux, node);
                //l'ensemble de noeuds adjacents internes
                Collection<RDFNode> internNeighborsNode = new ArrayList<>();
                int internNeighbors = 0;
                //pour chaque adjacent
                for (RDFNode nodeNeighbor : neighborsNode) {

                    if (adjacentsNodes.contains(nodeNeighbor)) {
                        if (weitingWeightedGraph.containsEdge(weitingWeightedGraph.findEdge(node, nodeNeighbor))) {
                            internNeighbors = internNeighbors + weitingWeightedGraph.findEdge(node, nodeNeighbor).weight;

                        } else {
                            internNeighbors = internNeighbors + weitingWeightedGraph.findEdge(nodeNeighbor, node).weight;
                        }
                        internNeighborsNode.add(nodeNeighbor);
                    }
                }

                TupleNodeDegree degreeInterne = new TupleNodeDegree(node, internNeighbors);
                nodesDegreeIntern.add(degreeInterne);
                // sauvegarder pour chaque noeud du sous graphe ses adjacents internes
                TupleNodeInternNeighbor internNeighborsNodeNode = new TupleNodeInternNeighbor(node, internNeighborsNode);
                nodesNeighborsIntern.add(internNeighborsNodeNode);
            }
            //2.2 Order the set of vertices v in N in increasing order of subgraph of G(v)
            SortTuple(nodesDegreeIntern);

            Collection<RDFNode> currentAdjacents = null;

            for (TupleNodeDegree tri : nodesDegreeIntern) {  ///for each v in V
                K.put(tri.Node, tri.Degrre);//du degree le plus petit au plus grand
                int k = tri.Degrre;

                RDFNode node3 = tri.Node;

                //recuperation des adjacents du noeud en cours "tri"
                currentAdjacents = getRessourcesNeighbors(nodesNeighborsIntern, node3);
                //  for each (v; w) dans E do
                //if dgree(w) > degree(v) then
                //degree(w) <--degree(w)-1
                for (RDFNode node5 : currentAdjacents) {//for adjacents de v
                    int degre;
                    for (TupleNodeDegree tuplex : nodesDegreeIntern) {
                        if (tuplex.Node.equals(node5)) {
                            degre = tuplex.Degrre;

                            if (degre > k) {
                                int nouveaudegree = degre - 1;
                                TupleNodeDegree tupleremplacement = new TupleNodeDegree(node5, nouveaudegree);
                                int index = nodesDegreeIntern.indexOf(tuplex);
                                nodesDegreeIntern.set(index, tupleremplacement);
                                //creer une lite pour sauvgarder les noeuds parcourus et les eliminer par la suite 
                            }
                            break;
                        }
                    }
                }

                // Reorder the rest of V accordingly
                int index = nodesDegreeIntern.indexOf(tri) + 1;
                if (index < size - 1) {
                    SortTupleList(nodesDegreeIntern.subList(index, size));
                    List<TupleNodeDegree> sous = nodesDegreeIntern.subList(index, size);
                    SortTupleList(sous);
                }
            }

            //recuperation du plus grand k-core (le k)
            for (RDFNode nodeAdj : adjacentsNodes) {
                if (K.containsKey(nodeAdj)) {
                    TupleNodeDegree nodeK = new TupleNodeDegree(nodeAdj, (Integer) K.get(nodeAdj));
                    K1.add(nodeK);
                }
            }
            SortTupleList(K1);

            kcurrentNode2 = K1.get(K1.size() - 1).Degrre;
            if (kcurrentNode2 > 1) {

                ArrayList<TupleNodeDegree> degreeInternToKcore = getKNeighbors(nodesDegreeIntern, nodesNeighborsIntern, kcurrentNode2, weitingWeightedGraph);

                for (TupleNodeDegree tuplexx : degreeInternToKcore) {
                    if (tuplexx.Degrre >= kcurrentNode2) {
                        kCore.add(tuplexx.Node);
                        kCoreDegree = kCoreDegree + tuplexx.Degrre;
                    }
                }
                coreDensity = (float) kCoreDegree / (float) ((kCore.size() * (kCore.size() - 1)));
            } else {
                coreDensity = 1;
            }
        } else {
            kcurrentNode2 = 1;
            coreDensity = 1;
        }

        Pair KDensite = new Pair(kcurrentNode2, coreDensity);
        return KDensite;
    }

    private void SortTuple(ArrayList<TupleNodeDegree> nodesDegree) {
        SortTupleNodedegreeComparator com = new SortTupleNodedegreeComparator();
        Collections.sort(nodesDegree, com);
    }

    private void SortTupleWeight(ArrayList<PairNodeWeight> nodesDegree) {
        SortTupleNodeWeightComparator com = new SortTupleNodeWeightComparator();
        Collections.sort(nodesDegree, com);
    }

    private void SortTupleList(List<TupleNodeDegree> nodesDegree) {
        SortTupleNodedegreeComparator com = new SortTupleNodedegreeComparator();
        Collections.sort(nodesDegree, com);
    }

    public class TupleNodeDegree {

        RDFNode Node;
        int Degrre;

        public RDFNode getNode() {
            return Node;
        }

        public double getDegrre() {
            return Degrre;
        }

        public TupleNodeDegree(RDFNode node, int degree) {
            Node = node;
            Degrre = degree;
        }
    }

    public class PairNodeWeight {

        private RDFNode Node;
        private float Weight;

        public RDFNode getNode() {
            return Node;
        }

        public float getWeight() {
            return Weight;
        }

        public PairNodeWeight(RDFNode node, float weight) {
            Node = node;
            Weight = weight;
        }
    }

    public class Pair {

        int K;
        float Densite;

        public float getDensite() {
            return Densite;
        }

        public int getDegrre() {
            return K;
        }

        public Pair(int k, float densite) {
            Densite = densite;
            K = k;
        }
    }

    public class TupleNodeInternNeighbor {

        RDFNode Node;
        Collection<RDFNode> InternNeighborNodes;

        public RDFNode getNode() {
            return Node;
        }

        public Collection<RDFNode> getInternNeighborNodes() {
            return InternNeighborNodes;
        }

        public TupleNodeInternNeighbor(RDFNode node, Collection<RDFNode> InternNeighborNode) {
            Node = node;
            InternNeighborNodes = InternNeighborNode;
        }
    }

    public class SortTupleNodeWeightComparator implements Comparator<PairNodeWeight> {

        @Override
        public int compare(PairNodeWeight o1, PairNodeWeight o2) {
            float datarate1 = o1.Weight;
            float datarate2 = o2.Weight;

            if (datarate1 < datarate2) {
                return -1;
            } else if (datarate1 > datarate2) {
                return +1;
            } else {
                return 0;
            }
        }
    }

    public class SortTupleNodedegreeComparator implements Comparator<TupleNodeDegree> {

        @Override
        public int compare(TupleNodeDegree o1, TupleNodeDegree o2) {
            double datarate1 = o1.getDegrre();
            double datarate2 = o2.getDegrre();

            if (datarate1 < datarate2) {
                return -1;
            } else if (datarate1 > datarate2) {
                return +1;
            } else {
                return 0;
            }
        }
    }

    public static Collection<RDFNode> getRessourcesNeighbors(ArrayList<TupleNodeInternNeighbor> pairs, RDFNode node) {
        Collection<RDFNode> currentAdjacents = null;
        for (TupleNodeInternNeighbor pair : pairs) {
            if (pair.Node.equals(node)) {
                currentAdjacents = pair.InternNeighborNodes;
                break;
            }
        }
        return currentAdjacents;

    }

    public ArrayList<TupleNodeDegree> getKNeighbors(ArrayList<TupleNodeDegree> nodesDegreeIntern, ArrayList<TupleNodeInternNeighbor> nodesNeighborsIntern, int currentK, DirectedSparseGraph<RDFNode, MyLink> weitingWeightedGraph) {
        Collection<RDFNode> currentAdjacents = new ArrayList();//l'ensemble des noeuds du Kcore
        ArrayList<TupleNodeDegree> nodesDegreeInternkcore = new ArrayList();//noeud+degree à l'interieur du kcore

        for (TupleNodeDegree degrenodeNeighbor : nodesDegreeIntern) {
            if (degrenodeNeighbor.Degrre >= currentK) {
                currentAdjacents.add(degrenodeNeighbor.Node);
            }
        }

        for (RDFNode node : currentAdjacents) {
            //adjacent dans le sous graphe
            Collection<RDFNode> adjacentsInKCode = getRessourcesNeighbors(nodesNeighborsIntern, node);
            int degreeinternkcore = 0;
            for (RDFNode node2 : adjacentsInKCode) {
                if (currentAdjacents.contains(node2)) {

                    if (weitingWeightedGraph.containsEdge(weitingWeightedGraph.findEdge(node, node2))) {
                        degreeinternkcore = degreeinternkcore + weitingWeightedGraph.findEdge(node, node2).weight;
                    } else {
                        degreeinternkcore = degreeinternkcore + weitingWeightedGraph.findEdge(node2, node).weight;
                    }
                }
            }
            TupleNodeDegree x = new TupleNodeDegree(node, degreeinternkcore);
            nodesDegreeInternkcore.add(x);
        }

        return nodesDegreeInternkcore;
    }

    public class MyNode {

        RDFNode node;

        public MyNode(RDFNode node) {
            this.node = node;
        }

        @Override
        public String toString() {
            return "V" + node;
        }
    }

    public static class MyLink {

        int weight;
        Statement statement;

        public Statement GetStatement() {
            return statement;
        }

        public MyLink(int weight, Statement statement) {
            this.statement = statement;
            this.weight = weight;
        }

        @Override
        public String toString() {
            return "E" + statement;
        }
    }
}
