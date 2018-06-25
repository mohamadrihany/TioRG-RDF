package fr.uvsq.adam.search;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class SearchWordNetHyper {

    static final String inputFileName1 = "/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/hypo.rdf";

    public SearchWordNetHyper() {
    }

    public static String main(String keyword) {
        String s = "";

        Model m = ModelFactory.createDefaultModel();
        InputStream inputStream = FileManager.get().open(inputFileName1);
        if (inputStream == null) {
            throw new IllegalArgumentException("File" + inputFileName1 + "not found");
        }
        m.read(inputStream, "");

        String querySelect = "SELECT * WHERE { ?y  <http://www.w3.org/2006/03/wn/wn20/schema/hyponymOf> <http://www.w3.org/2006/03/wn/wn20/instances/"+keyword+">}"; //x hypo y => y hyper x
        QueryExecution exe = QueryExecutionFactory.create(querySelect, m);
        ResultSet queryResult = exe.execSelect();
        while (queryResult.hasNext()) {
            QuerySolution sol = queryResult.nextSolution();
            //System.out.println(sol);
            s = s + sol.get("?y").asNode().getLocalName()+" ";
        }
        return s;
    }
}


