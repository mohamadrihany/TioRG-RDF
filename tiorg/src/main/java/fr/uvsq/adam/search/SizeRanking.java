/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.DirectedGraph;
import fr.uvsq.adam.processings.FileToModelGraph;
import fr.uvsq.adam.processings.GetAllEquivalenceFromXMLFile;
import fr.uvsq.adam.search.ResultConstruction.ResultNodeModel;
import java.util.ArrayList;
import org.jdom2.Element;

/**
 *
 * @author houk
 */
public class SizeRanking {

    public static float main(ResultNodeModel result,  Element racine) throws Exception {
        float size = 0.0f;
        float score = 0.0f;
        ArrayList<String> modelListString = new ArrayList<>();
        if (result.TypeResult.equals("model")) {
            DirectedGraph<RDFNode, Statement> graphResult = FileToModelGraph.ModelToGraph(result.getModel());
            
            size = graphResult.getVertexCount() + graphResult.getEdgeCount();
           // System.out.println("la taille initial  "+size);
            //Reduction de la taille dans le cas d'un sous graphe equivalent a un arc
            ArrayList<String> equivalentsEdge = GetAllEquivalenceFromXMLFile.GetEquivalent("edge", racine);
            
            for (String equivalent : equivalentsEdge) {
                int modelSize = 0;
                String[] splitEquivalent = equivalent.split("_");
                String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        // + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "Construct    "
                        + "{ "
                        + "?x " + splitEquivalent[0] + " .  "
                        + "?y " + splitEquivalent[1] + " ?z .  "
                        + " }  "
                        + "where { "
                        + "?x " + splitEquivalent[0] + " .  "
                        + "?y " + splitEquivalent[1] + " ?z .  "
                        + "} \n ";
                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, result.getModel());
                Model constructModel = qexec.execConstruct();
                if (!constructModel.isEmpty() && !modelListString.contains(constructModel.toString())) {
                    modelListString.add(constructModel.toString());
                    DirectedGraph<RDFNode, Statement> constructGraph = FileToModelGraph.ModelToGraph(constructModel);
                    modelSize = constructGraph.getVertexCount() + constructGraph.getEdgeCount();

                }
                //remplacer la taille des patterns par la taille d'un triplet
                float arcsNumber = modelSize / 5.0f;

                size = size - modelSize;
                size = size + (arcsNumber * 3);
            }
            for (RDFNode node : graphResult.getVertices()) {
                if (node.isLiteral()) {
                    //on reduit de la taille 2 (le litteral qui va avec la ressource et l'arc qui les relie 
                    size = size - 2;
                }
            }
if(size==1.0){
    System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>"+size);
}
            score = (size != 0 ? 1.0f / size : 0);
 
            
        } else {
            score = 1.0f;
        }
        return score;
    }
}
