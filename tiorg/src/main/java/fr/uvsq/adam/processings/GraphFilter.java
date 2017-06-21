/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import fr.uvsq.adam.clustering.MCODEVertexWeitingWeightedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseGraph;
import net.rootdev.jenajung.JenaJungGraph;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import java.util.ArrayList;
import java.util.Collection;

/**
 *
 * @author houk
 */
public class GraphFilter {

    /**
 *
 * Get instances from global graph
 */
    public static Graph FilterModel(Model model) {
        Graph<RDFNode, Statement> globalGraph = new JenaJungGraph(model);
        Model tboxModel = GetRDFSchema(model);
        Graph<RDFNode, Statement> schemaGraph = new JenaJungGraph(tboxModel);
        Collection<RDFNode> globalesVertices = globalGraph.getVertices();
        Collection<RDFNode> instancesVertices = new ArrayList<>();
        for (RDFNode vertex : globalesVertices) {
            if (!schemaGraph.getVertices().contains(vertex)) {
                instancesVertices.add(vertex);
            }
        }
        Graph dataGraph = ConstructSubGraph(globalGraph, instancesVertices);
        return dataGraph;
    }

    public static Model GetRDFSchema(Model model) {
        String queryContrut = "CONSTRUCT "
                + "{?x  ?prop ?class ;"
                + "?pred ?obj ."
                + "} WHERE"
                + "  {  ?x  ?prop ?class ;"
                + "?pred ?obj ."
                + "FILTER ("
                + "(isIRI(?prop) &&"
                + "!((regex(str(?prop), \"http://www.w3\") && ("
                + "regex(str(?prop), \".org/1999/02/22-rdf-syntax-ns#\") ||"
                + "regex(str(?prop), \".org/2000/01/rdf-schema#\") ||"
                + "regex(str(?prop), \".org/2001/XMLSchema#\") ||"
                + "regex(str(?prop), \".org/2002/07/owl#\")"
                + "))) )  )"
                + "} ";


        Query query = QueryFactory.create(queryContrut);
        QueryExecution qExe = QueryExecutionFactory.create(query, model);
        // executing the CONSTRUCT gives us a new mdoel with triples 
        // that satisfy the WHERE clause 
        Model propertiesModel = qExe.execConstruct();
        String queryClassesContrut = "CONSTRUCT"
                + " {       ?x  a  ?class ;"
                + " ?pred ?obj . "
                + "} WHERE"
                + "{    ?x  a  ?class ;"
                + " ?pred ?obj ."
                + "FILTER ("
                + "!((isIRI(?class) && (regex(str(?class), \"http://www.w3\") && ("
                + "  regex(str(?class), \".org/1999/02/22-rdf-syntax-ns#\") ||"
                + " regex(str(?class), \".org/2000/01/rdf-schema#\") ||"
                + "regex(str(?class), \".org/2001/XMLSchema#\") ||"
                + " regex(str(?class), \".org/2002/07/owl#\")"
                + " )))))}";

        Query queryclasses = QueryFactory.create(queryClassesContrut);
        QueryExecution qCExe = QueryExecutionFactory.create(queryclasses, model);
        // executing the CONSTRUCT gives us a new mdoel with triples 
        // that satisfy the WHERE clause 
        Model classesModel = qCExe.execConstruct();
        Model tboxModel;
        tboxModel = model.difference(classesModel).difference(propertiesModel);

        return tboxModel;
    }

    /**
     * construit un sous graphe Ã  partir d'un graphe global.
     *
     * @param globalGraph le graphe a partir duquel on construit un sous graphe
     * @param vertices les noeuds du sous graphe
     * @return la sous graphe contenant les noeuds prÃ©cisÃ©s
     */
    public static Graph ConstructSubGraph(Graph<RDFNode, Statement> globalGraph, Collection<RDFNode> vertices) {
        Graph subgraph = new SparseGraph();

        for (RDFNode v : vertices) {
            if (!globalGraph.containsVertex(v)) {
                throw new IllegalArgumentException("Vertex " + v
                        + " is not an element of " + globalGraph);
            }
            subgraph.addVertex(v);
        }

        for (Statement e : globalGraph.getEdges()) {
            Collection<RDFNode> incident;
            incident = globalGraph.getIncidentVertices(e);
            if (vertices.containsAll(incident)) {
                subgraph.addEdge(e, incident, globalGraph.getEdgeType(e));
            }
        }

        return subgraph;
    }

    public static Graph ConstructSubGraphMyLink(Graph<RDFNode, MCODEVertexWeitingWeightedGraph.MyLink> globalGraph, Collection<RDFNode> vertices) {
        Graph subgraph = new SparseGraph(); // Graph.class.newInstance();//(G)graph.getClass().newInstance();

        for (RDFNode v : vertices) {
            if (!globalGraph.containsVertex(v)) {
                throw new IllegalArgumentException("Vertex " + v
                        + " is not an element of " + globalGraph);
            }
            subgraph.addVertex(v);
        }

        for (MCODEVertexWeitingWeightedGraph.MyLink e : globalGraph.getEdges()) {
            Collection<RDFNode> incident;
            incident = globalGraph.getIncidentVertices(e);
            if (vertices.containsAll(incident)) {
                subgraph.addEdge(e, incident, globalGraph.getEdgeType(e));
            }
        }

        return subgraph;
    }
}
