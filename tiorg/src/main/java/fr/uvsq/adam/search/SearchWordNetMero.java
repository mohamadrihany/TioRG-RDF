package fr.uvsq.adam.search;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class SearchWordNetMero {


    static final String inputFileName1 = "/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/membermeronym.rdf";
    static final String inputFileName2 = "/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/partmeronym.rdf";
    static final String inputFileName3 = "/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/substancemeronym.rdf";

    public SearchWordNetMero() {
    }

    public static String main(String keyword) {
        String s = "";

        Model m = ModelFactory.createDefaultModel();
        //memberMeronymOf
        InputStream inputStream = FileManager.get().open(inputFileName1);
        if (inputStream == null) {
            throw new IllegalArgumentException("File" + inputFileName1 + "not found");
        }
        m.read(inputStream, "");

        String querySelect = "SELECT * WHERE { <http://www.w3.org/2006/03/wn/wn20/instances/"+keyword+ ">  <http://www.w3.org/2006/03/wn/wn20/schema/memberMeronymOf> ?y}";   //x hypo y => y hyper x
        QueryExecution exe = QueryExecutionFactory.create(querySelect, m);
        ResultSet queryResult = exe.execSelect();
        while (queryResult.hasNext()) {
            QuerySolution sol = queryResult.nextSolution();
            //System.out.println(sol);
            s = s + sol.get("?y").asNode().getLocalName()+" ";
        }

        //partMeronymOf
        inputStream = FileManager.get().open(inputFileName2);
        if (inputStream == null) {
            throw new IllegalArgumentException("File" + inputFileName2 + "not found");
        }
        m.read(inputStream,"");
        querySelect = "SELECT * WHERE { <http://www.w3.org/2006/03/wn/wn20/instances/"+keyword+ ">  <http://www.w3.org/2006/03/wn/wn20/schema/partMeronymOf> ?y}";   //x hypo y => y hyper x
        exe = QueryExecutionFactory.create(querySelect, m);
        queryResult = exe.execSelect();
        while (queryResult.hasNext()) {
            QuerySolution sol = queryResult.nextSolution();
            //System.out.println(sol);
            s = s + sol.get("?y").asNode().getLocalName()+" ";
        }

        //substanceMeronymOf

        inputStream = FileManager.get().open(inputFileName3);
        if (inputStream == null) {
            throw new IllegalArgumentException("File" + inputFileName3 + "not found");
        }
        m.read(inputStream,"");
        querySelect = "SELECT * WHERE { <http://www.w3.org/2006/03/wn/wn20/instances/"+keyword+ ">  <http://www.w3.org/2006/03/wn/wn20/schema/substanceMeronymOf> ?y}";   //x hypo y => y hyper x
        exe = QueryExecutionFactory.create(querySelect, m);
        queryResult = exe.execSelect();
        while (queryResult.hasNext()) {
            QuerySolution sol = queryResult.nextSolution();
            //System.out.println(sol);
            s = s + sol.get("?y").asNode().getLocalName()+" ";
        }

        return s;
    }

}
