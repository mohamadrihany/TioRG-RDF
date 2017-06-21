/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import fr.uvsq.adam.processings.ReadXMLFile.MyElementWithString;
import net.rootdev.jenajung.JenaJungGraph;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.*;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

/**
 *
 * @author houk globalGraph graph without elimination of literals initialGraph
 * traited graph
 *
 */
public class SharedFeature {

    public static Graph CreateNewGraphWithSharedFeature(Graph<RDFNode, Statement> initialGraph, ArrayList<MyElementWithString> SharedFeaturesList) throws FileNotFoundException, ClassNotFoundException, HeadlessException, IOException {
        //  Model newModel = ModelFactory.createDefaultModel();
        HashMap<String, ArrayList<RDFNode>> ressourcesWithSameFeature = new HashMap<>();
        for (MyElementWithString element : SharedFeaturesList) {
//
            for (Statement stmt : initialGraph.getEdges()) {
                //Parcourir les valeurs de la propriete en cours pour stocker dans la meme liste ceux qui ont la meme valeur 
                if (element.link.equals(stmt.getPredicate().getLocalName())) {
                    if (ressourcesWithSameFeature.containsKey(stmt.getPredicate().getLocalName() + initialGraph.getDest(stmt).asResource().getLocalName())) {
                        ressourcesWithSameFeature.get(stmt.getPredicate().getLocalName() + initialGraph.getDest(stmt).asResource().getLocalName()).add(initialGraph.getSource(stmt));
                    } else {
                        ArrayList<RDFNode> set = new ArrayList();
                        set.add(initialGraph.getSource(stmt));
                        ressourcesWithSameFeature.put(stmt.getPredicate().getLocalName() + initialGraph.getDest(stmt).asResource().getLocalName(), set);
                    }
                }
            }

        }

        //creation du model a partir du graphe 
        Model model = ModelFactory.createDefaultModel();
        Collection<Statement> statements = initialGraph.getEdges();
        for (Statement stmt : statements) {
            Pair<RDFNode> pair = initialGraph.getEndpoints(stmt);
            model.add(pair.getFirst().asResource(), stmt.getPredicate(), pair.getSecond());
        }

        //Add new links between nodes with same caracteristics 
        Iterator<String> keySetIterator = ressourcesWithSameFeature.keySet().iterator();
        Property predecat = model.createProperty("http://dbpedia.org/ontology/", "sameFeature");
        while (keySetIterator.hasNext()) {
            String key = keySetIterator.next();
            if (ressourcesWithSameFeature.get(key).size() > 1) {
                int i, j;
                int size = ressourcesWithSameFeature.get(key).size() - 1;
                for (i = 0; i < size; i++) {
                    RDFNode source = ressourcesWithSameFeature.get(key).get(i);
                    for (j = i + 1; j < ressourcesWithSameFeature.get(key).size(); j++) {

                        RDFNode dist = ressourcesWithSameFeature.get(key).get(j);
                        boolean exist = false;
                        for (Statement v : initialGraph.getEdges()) {
                            if ((initialGraph.getSource(v).equals(source)) && (initialGraph.getDest(v).equals(dist))) {
                                exist = true;
                                break;
                            }
                            if ((initialGraph.getSource(v).equals(dist)) && (initialGraph.getDest(v).equals(source))) {
                                exist = true;
                                break;
                            }
                        }
                        if (!exist) {
                            model.add(source.asResource(), predecat, dist);
                        }
                    }
                }

            }
        }

      
      
        Graph<RDFNode, Statement> enrichedGraph = new JenaJungGraph(model);

//        RDFWriter fasterWriter = model.getWriter("RDF/XML");
//        fasterWriter.setProperty("allowBadURIs", "true");
//        fasterWriter.setProperty("relativeURIs", "");
//        fasterWriter.setProperty("tab", "8");
//        FileOutputStream clusterFile = new FileOutputStream(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "Graphs" + File.separator + "exempledbpedia" + File.separator + "model.owl");
//        model.write(clusterFile);
        return enrichedGraph;
    }

    public static PairSourceSetGraph CreateNewGraphWithFusion(Graph<RDFNode, Statement> initialGraph, ArrayList<MyElementWithString> SharedFeaturesList) throws FileNotFoundException, ClassNotFoundException, HeadlessException, IOException {

//        ArrayList<RDFNode> nodesTofusion = new ArrayList<>();//list of nodes to groupe
        ArrayList<ArrayList<RDFNode>> nodes = new ArrayList<>();//list of set of node to regroupe togther
        ArrayList<String> sharedFeatureLink = new ArrayList<>();
        for (RDFNode node : initialGraph.getVertices()) {
            if (node.isResource()) {
                
                ArrayList<RDFNode> set = new ArrayList<>();
                set.add(node);
                nodes.add(set);
            }
        }
       
        // fusion des noeuds et construction des listes
        for (MyElementWithString element : SharedFeaturesList) {
            sharedFeatureLink.add(element.link);
        }
 
        for (Statement stmt : initialGraph.getEdges()) {
            for (String element : sharedFeatureLink) {
               
                if (element.equals(stmt.getPredicate().getLocalName())) {
                    ArrayList<RDFNode> dist = new ArrayList<>();
                    ArrayList<RDFNode> source = new ArrayList<>();
                    for (ArrayList<RDFNode> set : nodes) {

                        if (set.contains(initialGraph.getSource(stmt))) {
                            source = set;
                        }
                        if (set.contains(initialGraph.getDest(stmt))) {
                            dist = set;
                        }

                    }
                    if (!dist.equals(source)) {
                        nodes.remove(dist);
                        nodes.remove(source);
                        source.addAll(dist);
                        nodes.add(source);
                    }
                }
            }
        }

      
        //creation du model a partir du graphe 
        Model model = ModelFactory.createDefaultModel();
        HashMap<RDFNode, Collection<Statement>> newNodeWithOutStatements = new HashMap<>();
        HashMap<RDFNode, Collection<Statement>> newNodeWithInStatements = new HashMap<>();
        ArrayList<RDFNode> creatednodes = new ArrayList<>();
        //creation des noeuds 
        ArrayList<PairSourceSet> listSourceSet = new ArrayList<>();
       
        for (ArrayList<RDFNode> set : nodes) {
            String name = "";
            ArrayList<Statement> statementOut = new ArrayList<>();
            ArrayList<Statement> statementIn = new ArrayList<>();
            int i = 0;
             
            for (RDFNode node : set) {
                if (i == 0) {
                    name = name + node.asResource().getURI();
                } else {
                    name = name + "++" + node.asResource().getURI();

                }
                i++;
                for (Statement st : initialGraph.getOutEdges(node)) {
                    //delete edges selected by user
                    if (!sharedFeatureLink.contains(st.getPredicate().getLocalName())) {
                        statementOut.add(st);
                    }
                }
                for (Statement st : initialGraph.getInEdges(node)) {
                    if (!sharedFeatureLink.contains(st.getPredicate().getLocalName())) {
                        statementIn.add(st);
                    }
                }
            }
            //create ressources with there out and in statements
            RDFNode resource = model.createResource(name);
            creatednodes.add(resource);
            newNodeWithOutStatements.put(resource, statementOut);
            newNodeWithInStatements.put(resource, statementIn);
            PairSourceSet pairSourceSet = new PairSourceSet(set, resource);
            listSourceSet.add(pairSourceSet);
        }
         
        // creation des triples et les rajouter au model
        Iterator<RDFNode> keySetIterator = newNodeWithOutStatements.keySet().iterator();
     
        while (keySetIterator.hasNext()) {
            RDFNode key = keySetIterator.next();
        
            for (Statement state : newNodeWithOutStatements.get(key)) {
                
                if (initialGraph.getDest(state).isLiteral()) {
                    model.add(key.asResource(), state.getPredicate(), initialGraph.getDest(state).asLiteral());
           
                } else {

                    Set<Entry<RDFNode, Collection<Statement>>> stockSet = newNodeWithInStatements.entrySet();

                    // Making use of Iterator to loop Map in Java, here Map implementation is Hashtable
                    Iterator<Entry<RDFNode, Collection<Statement>>> i = stockSet.iterator();
                    //Iterator begins
                    
                    while (i.hasNext()) {
                        Map.Entry<RDFNode, Collection<Statement>> m = i.next();
                        RDFNode keyIn = m.getKey();
                        for (Statement stateIn :m.getValue()) {
                          
if ((initialGraph.getSource(state).equals(initialGraph.getSource(stateIn)) && (initialGraph.getDest(state).equals(initialGraph.getDest(stateIn))) && (state.getPredicate().getLocalName().equals(stateIn.getPredicate().getLocalName())))) {

                                model.add(key.asResource(), state.getPredicate(), keyIn);
                            }
                        }
                    }
                   
                }
                
            }
        }
 
        Graph<RDFNode, Statement> enrichedGraph = new JenaJungGraph(model);
        
//        RDFWriter fasterWriter = model.getWriter("RDF/XML");
//        fasterWriter.setProperty("allowBadURIs", "false");
//        fasterWriter.setProperty("relativeURIs", "");
//        fasterWriter.setProperty("tab", "8");
//        FileOutputStream clusterFile = new FileOutputStream(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "Graphs" + File.separator + "exempledbpedia" + File.separator + "model.owl");
//        model.write(clusterFile);
        PairSourceSetGraph PairSourceSetGraph = new PairSourceSetGraph(listSourceSet, enrichedGraph);
        return PairSourceSetGraph;
    }
    
    public static Graph CreateNewGraphWithValue(Graph<RDFNode, Statement> initialGraph, ArrayList<MyElementWithString> valueFeaturesList) throws FileNotFoundException, ClassNotFoundException, HeadlessException, IOException {
    	//creation du model a partir du graphe 
        Model model = ModelFactory.createDefaultModel();
        Collection<Statement> statements = initialGraph.getEdges();
        for (Statement stmt : statements) {
            Pair<RDFNode> pair = initialGraph.getEndpoints(stmt);
            model.add(pair.getFirst().asResource(), stmt.getPredicate(), pair.getSecond());
        }
        
        ArrayList<RDFNode> nodes = new ArrayList<RDFNode>();
        for(MyElementWithString element : valueFeaturesList) 
        {
            for(Statement stmt : initialGraph.getEdges()) 
            {
                if( element.link.equals(stmt.getPredicate().getLocalName()) ) 
                {
                	boolean ok = false;
                	RDFNode source = initialGraph.getSource(stmt);
                	if( !source.isLiteral() )
                		ok = source.asResource().getLocalName().equalsIgnoreCase(element.weight);
					else
						ok = source.asLiteral().toString().equalsIgnoreCase(element.weight);
                	
                	RDFNode dest = initialGraph.getDest(stmt);
                	if( !ok )
                	{
	                	if( !dest.isLiteral() )
	                		ok = dest.asResource().getLocalName().equalsIgnoreCase(element.weight);
						else
							ok = dest.asLiteral().toString().equalsIgnoreCase(element.weight);
                	}
                	
                	if(ok)
                	{
                		model.add(source.asResource(), stmt.getPredicate(), dest);
                		nodes.add(source);
                		nodes.add(dest);
                	}
                }
            }
        }
        
        Property predecat = model.createProperty("http://dbpedia.org/ontology/", "sameValue");
        for(RDFNode source : nodes) 
        {
        	for(RDFNode dist : nodes)
        	{
        		if(source == dist)
        			continue;
        		
                boolean exist = false;
                for (Statement v : initialGraph.getEdges()) 
                {
                    if((initialGraph.getSource(v).equals(source)) && (initialGraph.getDest(v).equals(dist))) 
                    {
                        exist = true;
                        break;
                    }
                    if ((initialGraph.getSource(v).equals(dist)) && (initialGraph.getDest(v).equals(source))) 
                    {
                        exist = true;
                        break;
                    }
                }
                if( !exist )
                    model.add(source.asResource(), predecat, dist);
            }
        }
      
        Graph<RDFNode, Statement> enrichedGraph = new JenaJungGraph(model);

        return enrichedGraph;
    }
    
    public static Graph CreateNewGraphWithQuery(Graph<RDFNode, Statement> initialGraph, ArrayList<MyElementWithString> queryFeaturesList) throws FileNotFoundException, ClassNotFoundException, HeadlessException, IOException {
    	//creation du model a partir du graphe 
        Model model = ModelFactory.createDefaultModel();
        Collection<Statement> statements = initialGraph.getEdges();
        for (Statement stmt : statements) {
            Pair<RDFNode> pair = initialGraph.getEndpoints(stmt);
            model.add(pair.getFirst().asResource(), stmt.getPredicate(), pair.getSecond());
        }
        
        ArrayList<RDFNode> nodes = new ArrayList<RDFNode>();
        for(MyElementWithString element : queryFeaturesList) 
        {
        	QueryExecution exe = QueryExecutionFactory.create(element.weight, FileToModelGraph.GraphToModel(initialGraph));
    		ResultSet set = exe.execSelect();
    		while( set.hasNext() )
    		{
    			QuerySolution solution = set.next();
    			for(String name : set.getResultVars())
    			{
    				if( !nodes.contains(solution.get(name)) )
    					nodes.add( solution.get(name) );
    			}
    		}
        }
        
        if( !nodes.isEmpty() )
        {
            for(Statement stmt : initialGraph.getEdges()) 
            {
            	RDFNode source = initialGraph.getSource(stmt);
            	RDFNode dest = initialGraph.getDest(stmt);
            	
            	if(nodes.contains(source) && nodes.contains(dest))
            		model.add(source.asResource(), stmt.getPredicate(), dest);
            }
        }
      
        Graph<RDFNode, Statement> enrichedGraph = new JenaJungGraph(model);

        return enrichedGraph;
    }

    public static class PairSourceSet {

        private ArrayList<RDFNode> set;
        private RDFNode source;

        public ArrayList<RDFNode> getSet() {
            return set;
        }

        public RDFNode getSource() {
            return source;
        }

        public PairSourceSet(ArrayList<RDFNode> Set, RDFNode Source) {
            set = Set;
            source = Source;
        }
    }

    public static class PairSourceSetGraph {

        private ArrayList<PairSourceSet> set;
        private Graph<RDFNode, Statement> graph;

        public ArrayList<PairSourceSet> getSet() {
            return set;
        }

        public Graph<RDFNode, Statement> getGraph() {
            return graph;
        }

        public PairSourceSetGraph(ArrayList<PairSourceSet> Set, Graph<RDFNode, Statement> Graph) {
            set = Set;
            graph = Graph;
        }
    }
}
