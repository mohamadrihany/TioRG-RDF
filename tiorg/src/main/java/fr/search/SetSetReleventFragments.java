/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.search;

import edu.uci.ics.jung.graph.DirectedGraph;
import fr.search.SearchElements.ElementReleventFragmentEdges;
import fr.search.SearchElements.ElementReleventFragmentNodes;
import fr.search.SearchElements.KeywordsReleventFragment;
import fr.search.UsingPatternsNodes.TypeReleventFragment;
import net.rootdev.jenajung.JenaJungGraph;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import static fr.search.cart.Cart;

/**
 *
 * @author hanane
 */
public class SetSetReleventFragments {

    public SetSetReleventFragments() {
    }

    public static ArrayList<ArrayList<ModelNode>> main(DirectedGraph<RDFNode, Statement> graph, HashMap<String, KeywordsReleventFragment> releventElementList, ArrayList<ElementReleventFragmentEdges> ReleventFrangmentListForEdges, Model model) {
        Set<String> cles = releventElementList.keySet();

        ArrayList<ArrayList<ModelNode>> fragmentsList = new ArrayList<>();
        Iterator<String> it = cles.iterator();
        while (it.hasNext()) {
            ArrayList<ModelNode> fragments = new ArrayList<>();
            ArrayList<String> ReleventNodes = new ArrayList<>();
            ArrayList<TypeReleventFragment> ReleventNodesType = new ArrayList<>();
            String keyword = it.next();
            ArrayList<ElementReleventFragmentNodes> releventNodes = releventElementList.get(keyword).ReleventFragmentNodes;
            for (ElementReleventFragmentNodes nodes : releventNodes) {
                for (Model modelresult:nodes.ListModel){
                      DirectedGraph<RDFNode, Statement> releventGraph = ModelToGraph(modelresult);
                        RDFNode rdfNode = releventGraph.getVertices().iterator().next();
                    ModelNode fragment = new ModelNode(rdfNode, modelresult, "model", keyword);
                        fragments.add(fragment);
                }
                for (TypeReleventFragment node : nodes.ReleventFragment) {
                    if (!ReleventNodes.contains(node.ElementUriOrContent)) {
                        ReleventNodes.add(node.ElementUriOrContent);

                        ReleventNodesType.add(node);
                        String nodeUri = node.ElementUriOrContent;
                        RDFNode rdfNode;
                        if (!node.Type.equals("litteral")) {
                            rdfNode = model.asRDFNode(model.createResource(nodeUri).asNode());
                        } else {
                            rdfNode = model.asRDFNode(model.createLiteral(nodeUri).asNode());
                        }
                        Model modelForNode = ModelFactory.createDefaultModel();
                        //dans type je ferai "model" pour le model

                        ModelNode fragment = new ModelNode(rdfNode, modelForNode, node.Type, keyword);
                        fragments.add(fragment);
                    }
                }
            }
            for (ElementReleventFragmentEdges releventEdges : ReleventFrangmentListForEdges) {
                if (releventEdges.KeyWord.equals(keyword)) {
                    for (Model releventModel : releventEdges.ReleventFragment) {
                        releventModel.getGraph();
                        DirectedGraph<RDFNode, Statement> releventGraph = ModelToGraph(releventModel);
                        RDFNode rdfNode = releventGraph.getVertices().iterator().next();
                        ModelNode fragment = new ModelNode(rdfNode, releventModel, "model", keyword);
                        fragments.add(fragment);
                    }

                }
            }

            fragmentsList.add(fragments);
        }
        ArrayList<ArrayList<ModelNode>> cartesienProduct = Cart(fragmentsList);
        return cartesienProduct;

    }

    public static DirectedGraph ModelToGraph(Model model) {
        // Creation d'un modele d'ontologie 
        DirectedGraph<RDFNode, Statement> graph = new JenaJungGraph(model);

        return graph;
    }

    //pour récupérer pour chaque noeud, le model correspodant, si c'est un noeud uniquement, le model=null
    public static class ModelNode {

        String Type;
        RDFNode Node;
        Model Model;
        String Keyword;

        public RDFNode getNode() {
            return Node;
        }

        public Model getModel() {
            return Model;
        }

        public String getType() {
            return Type;
        }

        public String getKeyword() {
            return Keyword;
        }

        /**
         *
         *
         */
        public ModelNode(RDFNode node, Model model, String type, String keyword) {
            Model = model;
            Type = type;
            Keyword = keyword;
            Node = node;
        }
    }
}
