/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.processings;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.Pair;
import net.rootdev.jenajung.JenaJungGraph;
import org.apache.jena.rdf.model.*;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFFormat;
import org.apache.jena.util.FileManager;

import java.io.FileOutputStream;
import java.util.Collection;

/**
 *
 * @author houk
 */
public class FileToModelGraph {

    public static Model FileToModel(String owlFile) {
        // Creation d'un modele d'ontologie 
        Model m = ModelFactory.createDefaultModel();
        // Lecture du fichier OWL. 
        FileManager.get().readModel(m, owlFile);
        return m;
    }

    public static DirectedGraph FileToGraph(String owlFile) {
        // Creation d'un modele d'ontologie 
        //  Model m = ModelFactory.createDefaultModel();

        // Lecture du fichier OWL.
        Model mapModel = FileManager.get().loadModel(owlFile);
        StmtIterator x = mapModel.listStatements();
        if (!x.hasNext()) {
            System.out.println("...rien");
        }
        while (x.hasNext()) {
            Statement sol = x.next();
            System.out.println("..." + sol.toString());
        }
        //  Model mm = FileManager.get().loadModel( owlFile, "TURTLE");
        System.out.println("j'ai eu le model   " + owlFile);
        DirectedGraph<RDFNode, Statement> g = new JenaJungGraph(mapModel);
        System.out.println(g.getEdgeCount() + "    " + g.getVertexCount());
        return g;
    }
     public static DirectedGraph<RDFNode, Statement> ModelToGraph(Model model) {
        // Creation d'un modele d'ontologie 
        DirectedGraph<RDFNode, Statement> graph = new JenaJungGraph(model);

        return graph;
    }

    public static Model GraphToModel(Graph<RDFNode, Statement> graph) {
        Model model = ModelFactory.createDefaultModel();
        Collection<Statement> statements = graph.getEdges();
        for (Statement stmt : statements) {
            Pair<RDFNode> pair = graph.getEndpoints(stmt);
            model.add(pair.getFirst().asResource(), stmt.getPredicate(), pair.getSecond());
        }

        return model;
    }
    
    public static void WriteOwlFile(Graph<RDFNode, Statement> graph, String owlFile) throws Exception
    {
    	FileOutputStream stream = null;
    	try
    	{
    		stream = new FileOutputStream(owlFile);
    		stream.write("<?xml version=\"1.0\"?>".getBytes());
    		stream.write(System.getProperty("line.separator").getBytes());
    		RDFDataMgr.write(stream, GraphToModel(graph), RDFFormat.RDFXML_ABBREV);
    	}
    	finally
    	{
    		if(stream != null)
    			try { stream.close(); }
    			catch(Exception e) {}
    	}
    }
}
