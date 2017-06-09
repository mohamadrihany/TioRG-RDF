/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.clustering;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.Graph;
import java.util.*;

/**
 *
 * @author houk
 */
public class MCODEVertexWeiting {

    public ArrayList<Graph> VertexWeiting(Graph<RDFNode, Statement> graph, double param, double fluffThreshold) {
        System.out.println("temps debut" + System.currentTimeMillis());
          
        //l'ensemble des noeuds
        Collection<RDFNode> verticess = new ArrayList();
        verticess = graph.getVertices();
        Collection<RDFNode> vertices = new ArrayList(verticess);

        //couples noeud-poids
        ArrayList<PairNodeWeight> nodesWeight = new ArrayList<>();
        //couple noeud-degree(somme des poids des arcs adjacents)
        ArrayList<TupleNodeDegree> nodesDegree = new ArrayList<>();
        //couple noeud-l'ensemble d'adjacents sans litteraux
        ArrayList<TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux = new ArrayList<>();

        //parcourir tous les noeuds du graphe pour sauvegarder les degrees et les adjacents qui sont des ressources (sans literaux)
        for (RDFNode currentNode : vertices) {
            int currentDegree = 0;
            Collection<RDFNode> NeighborsNodewithoutliteral = new ArrayList<RDFNode>();
            Collection<RDFNode> currentAdjacents = graph.getNeighbors(currentNode);
            for (RDFNode nodes : currentAdjacents) {
                if (nodes.isResource()) {
                    currentDegree++;
                    NeighborsNodewithoutliteral.add(nodes);
                }
            }
            TupleNodeDegree degree = new TupleNodeDegree(currentNode, currentDegree);
            TupleNodeInternNeighbor neighborwithoutliterraux = new TupleNodeInternNeighbor(currentNode, NeighborsNodewithoutliteral);
            //noeud+degree
            nodesDegree.add(degree);
            // noeud+adjacents ressources
            nodesNeighborWithoutLiterraux.add(neighborwithoutliterraux);
        }


        //Vertex Weighting
        //1. N=find neighbors of v to depth 1 //*******
        for (TupleNodeDegree pair : nodesDegree) {
            RDFNode currentNode2 = pair.Node;//*******
            Collection<RDFNode> adjacentsNodes1 = getRessourcesNeighbors(nodesNeighborWithoutLiterraux, currentNode2);
            // former le sous graphe et compris le noeud courant
            Collection<RDFNode> adjacentsNodes = new ArrayList(adjacentsNodes1);
            adjacentsNodes.add(currentNode2);
            //2.K=Get highest k-core graph from N
            //pair==>densite of highest k-core and k

            Pair highestKcore = GetHighestKcore(adjacentsNodes, nodesNeighborWithoutLiterraux, currentNode2);
            float weight = highestKcore.Densite * highestKcore.K;
            PairNodeWeight nodeWeight = new PairNodeWeight(currentNode2, weight);

            nodesWeight.add(nodeWeight);

        }

        SortTupleWeight(nodesWeight);

        MCODEFindComplexes complexes = new MCODEFindComplexes(nodesWeight, param, nodesNeighborWithoutLiterraux, graph, fluffThreshold);
        ArrayList<Graph> finalClustersList = complexes.clustersList;

        return finalClustersList;
    }

    /**
     * Gets the highest K-core in a given sub graph. used to calculate the
     * weight of the nodes
     *
     * @param adjacentsNodes
     * @param nodesNeighborWithoutLiterraux
     * @param currentNode2
     * @return the density and k of the highest kcore
     */
    private Pair GetHighestKcore(Collection<RDFNode> adjacentsNodes, ArrayList<TupleNodeInternNeighbor> nodesNeighborWithoutLiterraux, RDFNode currentNode2) {

        ArrayList<TupleNodeDegree> nodesDegreeIntern = new ArrayList<TupleNodeDegree>();
        ArrayList<TupleNodeInternNeighbor> nodesNeighborsIntern = new ArrayList<TupleNodeInternNeighbor>();
        //toute la methode est pour le calcul du poids interne au sous graphe pour chaque noeuds ainsi que ses adjacents interne
        int size = adjacentsNodes.size();
        float coreDensity = 0;
        Hashtable K = new Hashtable();
        Collection<RDFNode> kCore = new ArrayList<RDFNode>();
        int kCoreDegree = 0;
        int kcurrentNode2 = 0;
        if (size > 2) {
            //2.1Compute degree G(v) (i.e., the degree) for all vertices v 2 V
            for (RDFNode node : adjacentsNodes) {

                // Collection<RDFNode> neighborsNode = graph.getNeighbors(node);
                Collection<RDFNode> neighborsNode = getRessourcesNeighbors(nodesNeighborWithoutLiterraux, node);
                Collection<RDFNode> internNeighborsNode = new ArrayList<>();//l'ensemble de noeuds adjacents internes
                int internNeighbors = 0;

                for (RDFNode nodeNeighbor : neighborsNode) {

                    if (adjacentsNodes.contains(nodeNeighbor)) {
                        internNeighbors++;
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

                }

            }
            //recuperation du plus grand k-core
            kcurrentNode2 = (Integer) K.get(currentNode2);
//ajouter une methode pour le calcul des degrees a l'interieur du kcore

            if (kcurrentNode2 > 1) {
                ArrayList<TupleNodeDegree> xxx = getKNeighbors(nodesDegreeIntern, nodesNeighborsIntern, kcurrentNode2);

                for (TupleNodeDegree tuplexx : xxx) {
                    if (tuplexx.Degrre >= kcurrentNode2) {
                        kCore.add(tuplexx.Node);
                        kCoreDegree = kCoreDegree + tuplexx.Degrre;
                    }
                }

                coreDensity = (float) kCoreDegree / (float) (kCore.size() * (kCore.size() - 1));
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

    private Collection<RDFNode> getRessourcesNeighbors(ArrayList<TupleNodeInternNeighbor> pairs, RDFNode node) {
        Collection<RDFNode> currentAdjacents = null;
        for (TupleNodeInternNeighbor pair : pairs) {
            if (pair.Node.equals(node)) {
                currentAdjacents = pair.InternNeighborNodes;
                break;
            }
        }
        return currentAdjacents;

    }

    /**
     *
     * nodesDegreeInternInit : le noeud avec son degree dans le sous graphe
     * internNeighborsNodeNode : le noeud avec ses adjacents dans le sous graphe
     * nodesDegreeIntern: le noeud avec son k dans le sous graphe
     *
     *
     *
     */
    private ArrayList<TupleNodeDegree> getKNeighbors(ArrayList<TupleNodeDegree> nodesDegreeIntern, ArrayList<TupleNodeInternNeighbor> nodesNeighborsIntern, int currentK) {
        Collection<RDFNode> currentAdjacents = new ArrayList();//l'ensemble des noeuds du Kcore
        ArrayList<TupleNodeDegree> nodesDegreeInternkcore = new ArrayList();//noeud+degree à l'interieur du kcore

        for (TupleNodeDegree degrenodeNeighbor : nodesDegreeIntern) {
            if (degrenodeNeighbor.Degrre >= currentK) {
                currentAdjacents.add(degrenodeNeighbor.Node);
            }
        }

        for (RDFNode node : currentAdjacents) {
            Collection<RDFNode> adjacentsInKCode = getRessourcesNeighbors(nodesNeighborsIntern, node);
            //adjacent dans le sous graphe 
            int degreeinternkcore = 0;
            for (RDFNode node2 : adjacentsInKCode) {
                if (currentAdjacents.contains(node2)) {
                    degreeinternkcore++;
                }
            }
            TupleNodeDegree x = new TupleNodeDegree(node, degreeinternkcore);
            nodesDegreeInternkcore.add(x);
        }

        return nodesDegreeInternkcore;
    }
}