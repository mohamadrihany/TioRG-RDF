package fr.uvsq.adam.search;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;

import java.io.InputStream;

public class SearchWordNetAntonym {

    static final String inputFileName1 = "/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/antonym.rdf";
    public SearchWordNetAntonym(){
    }
    public static String main(String keyword) {
        String s = "";

        Model m = ModelFactory.createDefaultModel();
        InputStream inputStream = FileManager.get().open(inputFileName1);
        if (inputStream == null) {
            throw new IllegalArgumentException("File" + inputFileName1 + "not found");
        }
        m.read(inputStream, "");

        String querySelect = "SELECT ?z WHERE {<http://www.w3.org/2006/03/wn/wn20/instances/"+ keyword +"> <http://www.w3.org/2006/03/wn/wn20/schema/antonymOf> ?y." +
                                              "?y <http://www.w3.org/2006/03/wn/wn20/schema/antonymOf> ?z.}";
        //String querySelect = "SELECT ?z WHERE {<http://www.w3.org/2006/03/wn/wn20/instances/"+ keyword +"> <http://www.w3.org/2006/03/wn/wn20/schema/antonymOf> ?y." +
        //        "?y <http://www.w3.org/2006/03/wn/wn20/schema/antonymOf>  ?z.}"; //
        QueryExecution exe = QueryExecutionFactory.create(querySelect, m);
        ResultSet queryResult = exe.execSelect();
        while (queryResult.hasNext()) {
            QuerySolution sol = queryResult.nextSolution();
            //System.out.println(sol);
            s = s + sol.get("?z").asNode().getLocalName()+" ";
        }
        return s;
    }
}
