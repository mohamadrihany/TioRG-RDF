/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.preprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import fr.clustering.MCODEVertexWeitingWeightedGraph.MyLink;
import fr.processings.FileToModelGraph;
import fr.processings.GraphFilter;
import fr.processings.ReadXMLFile;
import fr.processings.ReadXMLFile.MyElement;
import fr.processings.ReadXMLFile.MyElementWithString;
import fr.processings.ReadXMLFile.TripleValorisation;
import fr.processings.RessourcesToString;
import fr.processings.SharedFeature;
import fr.processings.SharedFeature.PairSourceSet;
import fr.views.ProjectManager;

/**
 *
 * @author houk
 */
public class GraphCreation {

    public static PairSourceSetGraph main(Graph<RDFNode, Statement> globalGraph, Properties predicates, List<String> queries) throws IOException, FileNotFoundException, ClassNotFoundException {

        Graph<RDFNode, Statement> intermediateGraph = globalGraph;
        DirectedSparseGraph<RDFNode, MyLink> finalGraph;
        // removal of litterals
        Collection<RDFNode> sourceVertex = new ArrayList();
        for (RDFNode vertex : globalGraph.getVertices()) {
            if (!vertex.isLiteral()) {
                sourceVertex.add(vertex);
            }
        }
        intermediateGraph = GraphFilter.ConstructSubGraph(intermediateGraph, sourceVertex);
          
        // grouping nodes
        TripleValorisation pairCaractéristique;


        //pairCaractéristique = ReadXMLFile.GetLinksWeight(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "properties" + File.separator + "linkCaracteristique.xml");
        //ArrayList<ReadXMLFile.MyElementWithString> fusionFeaturesList = pairCaractéristique.fusionList;
        ArrayList<MyElementWithString> fusionFeaturesList = new ArrayList<MyElementWithString>();
        ArrayList<MyElementWithString> regroupedFeaturesList = new ArrayList<MyElementWithString>();
        ArrayList<MyElementWithString> valueFeaturesList = new ArrayList<MyElementWithString>();
        ArrayList<MyElementWithString> queryFeaturesList = new ArrayList<MyElementWithString>();
        if( !predicates.isEmpty() )
		{
			for(Map.Entry<Object,Object> predicate : predicates.entrySet())
			{
				String name = predicate.getKey().toString();
				Properties vals = (Properties)predicate.getValue();
				String criteria = vals.getProperty(ProjectManager.PREDICATE_CRITERIA);
				String criteriaValue = vals.getProperty(ProjectManager.PREDICATE_VALUE);
				if( criteria.equalsIgnoreCase("Groupe a Set of Resources Having the Same Value") )
				{
					regroupedFeaturesList.add( new MyElementWithString("Ajout d'arcs", name) );
				}
				else if( criteria.equalsIgnoreCase("Groupe the Resources Related by this Property") )
				{
					fusionFeaturesList.add( new MyElementWithString("Fusion", name) );
				}
				else if( criteria.equalsIgnoreCase("Having value") && criteriaValue != null )
				{
					String[] arr = criteriaValue.split("[;]");
					for(String value : arr)
						valueFeaturesList.add( new MyElementWithString(value, name) );
				}
			}
		}
        
        if( !queries.isEmpty() )
        {
        	for(int i=0; i < queries.size(); i++)
        	{
        		queryFeaturesList.add( new MyElementWithString(queries.get(i), "query#"+i) );
        	}
        }

        ArrayList<PairSourceSet> listPairSourceSet = SharedFeature.CreateNewGraphWithFusion(intermediateGraph, fusionFeaturesList).getSet();

        intermediateGraph = SharedFeature.CreateNewGraphWithFusion(intermediateGraph, fusionFeaturesList).getGraph();
        
        intermediateGraph = SharedFeature.CreateNewGraphWithSharedFeature(intermediateGraph, regroupedFeaturesList);
        
        intermediateGraph = SharedFeature.CreateNewGraphWithValue(intermediateGraph, valueFeaturesList);
        
        intermediateGraph = SharedFeature.CreateNewGraphWithQuery(intermediateGraph, queryFeaturesList);
        
        //ArrayList<MyElementWithString> regroupedFeaturesList = pairCaractéristique.sharedList;

        //c'est pour l'affichage des noeuds a la fin
        finalGraph = WeightedGraphCreation(intermediateGraph);
        PairSourceSetGraph PairSourceSetWeightedGraph = new PairSourceSetGraph(listPairSourceSet, finalGraph);

        return PairSourceSetWeightedGraph;
    }

    public static Graph<RDFNode, Statement> graphToClusterWithStatement(Graph<RDFNode, Statement> globalGraph, ArrayList<MyElementWithString> fusionFeaturesList, ArrayList<MyElementWithString> regroupedFeaturesList, ArrayList<MyElementWithString> valueFeaturesList) throws IOException, FileNotFoundException, ClassNotFoundException {
        Graph<RDFNode, Statement> intermediateGraph =globalGraph;

        // removal of litterals
        Collection<RDFNode> globalVertex = new ArrayList();
        for (RDFNode vertex : globalGraph.getVertices()) {
            if (!vertex.isLiteral()) {
                globalVertex.add(vertex);
            }
        }
        intermediateGraph = GraphFilter.ConstructSubGraph(globalGraph, globalVertex);
        // grouping nodes

        intermediateGraph = SharedFeature.CreateNewGraphWithFusion(intermediateGraph, fusionFeaturesList).getGraph();
        // Creation of new edges for shared characteristics

        intermediateGraph = SharedFeature.CreateNewGraphWithSharedFeature(intermediateGraph, regroupedFeaturesList);

        intermediateGraph = SharedFeature.CreateNewGraphWithSharedFeature(intermediateGraph, valueFeaturesList);

        return intermediateGraph;
    }

    public static Graph<RDFNode, Statement> GraphCreation(Graph<RDFNode, Statement> globalGraph, String filePath, boolean graphwithoutLiterals, boolean fullGraph) throws IOException {

        Graph<RDFNode, Statement> finalGraph = globalGraph;

        if (!fullGraph) {
            finalGraph = GraphFilter.FilterModel(FileToModelGraph.FileToModel(filePath));
        }
        if (graphwithoutLiterals) {
            Collection<RDFNode> globalVertex = new ArrayList();
            for (RDFNode vertex : finalGraph.getVertices()) {
                if (!vertex.isLiteral()) {
                    globalVertex.add(vertex);
                }
            }
            finalGraph = GraphFilter.ConstructSubGraph(finalGraph, globalVertex);

        }

        return finalGraph;
    }

    public static DirectedSparseGraph<RDFNode, MyLink> WeightedGraphCreation(Graph<RDFNode, Statement> globalGraph) throws IOException {

        TripleValorisation pairValorisation;

        //pairValorisation = ReadXMLFile.GetLinksWeight(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "properties" + File.separator + "linkImportance.xml");

        //ArrayList<MyElement> weightsList = pairValorisation.weightsList;


        Collection<RDFNode> vertices = new ArrayList<>();   //l'ensemble des noeuds sans litereaux

        //le graphe construit en prenant en compte les poids
        DirectedSparseGraph<RDFNode, MyLink> weitingWeightedGraph = new DirectedSparseGraph();


        vertices = globalGraph.getVertices();  //l'ensemble des noeuds sans litereaux


        for (RDFNode node : vertices) {
            weitingWeightedGraph.addVertex(node);
        }

        Collection<Statement> edges = globalGraph.getEdges();
        for (Statement edge : edges) {

            if (vertices.contains(globalGraph.getSource(edge)) && vertices.contains(globalGraph.getDest(edge))) {
                int weight;
                weight = 1;//getWeight(weightsList, edge);

                MyLink myLink = new MyLink(weight, edge);
                weitingWeightedGraph.addEdge(myLink, globalGraph.getSource(edge), globalGraph.getDest(edge), EdgeType.DIRECTED);
            }
        }

        return weitingWeightedGraph;
    }

    private int getWeight(ArrayList<MyElement> pairs, ArrayList<ReadXMLFile.ElementImportance> intraPairs, ArrayList<ReadXMLFile.ElementImportance> interpairs, Statement stat, Graph<RDFNode, Statement> graphe, boolean weighted) {

        boolean found = false;
        int weight = 1;


        for (ReadXMLFile.ElementImportance pair : intraPairs) {

            if (stat.getPredicate().getLocalName().toString().equalsIgnoreCase(pair.link) && graphe.getSource(stat).asResource().getLocalName().toString().equalsIgnoreCase(pair.source) && graphe.getDest(stat).asResource().getLocalName().toString().equalsIgnoreCase(pair.dist) && pair.importance == true) {

                weight = 0;
                found = true;
                break;
            }
        }
        for (ReadXMLFile.ElementImportance pair : interpairs) {
            //Si on a choisi d'augmenter le poids pour un lien, on fera la meme chose pour tous les liens du meme type.
            if (RessourcesToString.transform(stat).equalsIgnoreCase(String.format("<%s>", pair.link)) && pair.importance == false) {
                weight = 3;
                found = true;
                break;
            }

        }
        if (found == false && weighted == true) {
            for (MyElement pair : pairs) {
                if (RessourcesToString.transform(stat).equalsIgnoreCase(String.format("<%s>", pair.link))) {
                    weight = pair.weight;
                    break;
                }
            }
        }
        return weight;

    }

    public static int getWeight(ArrayList<MyElement> pairs, Statement stat) {
        int weight = 1;

        for (MyElement pair : pairs) {
            if (RessourcesToString.transform(stat).equalsIgnoreCase(String.format("<%s>", pair.link))) {
                weight = pair.weight;
                break;
            }
        }
        return weight;

    }

    public static class PairSourceSetGraph {

        private ArrayList<PairSourceSet> set;
        private DirectedSparseGraph<RDFNode, MyLink> graph;

        public ArrayList<PairSourceSet> getSet() {
            return set;
        }

        public DirectedSparseGraph<RDFNode, MyLink> getGraph() {
            return graph;
        }

        public PairSourceSetGraph(ArrayList<PairSourceSet> Set, DirectedSparseGraph<RDFNode, MyLink> Graph) {
            set = Set;
            graph = Graph;
        }
    }
}
