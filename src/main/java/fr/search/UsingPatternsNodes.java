/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.search;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.DirectedGraph;
import fr.processings.GetEquivalentsFromXMLFile;
import java.util.ArrayList;
import org.jdom2.Element;

/**
 *
 * @author houk
 */
public class UsingPatternsNodes {

    private UsingPatternsNodes() {
    }

    public static TwoReleventFragment main(String uri, String type, String contentInit, String property, Model model, Element racine, DirectedGraph<RDFNode, Statement> graph) throws Exception {
        ArrayList<Model> modelList = new ArrayList<>();
        ArrayList<String> modelListString = new ArrayList<>();
        ArrayList<TypeReleventFragment> relevantFragment2 = new ArrayList();
        ArrayList<String> equivalents = new ArrayList<>();
        String queryString;
        boolean classe = false;
        //String queryStringProperties;
        ArrayList<TypeReleventFragment> relevantFragment = new ArrayList();
        boolean instance = false;
        switch (type) {
            case "class":
                equivalents = GetEquivalentsFromXMLFile.GetEquivalent(contentInit, "class", racine);
                classe = true;
                break;
            case "litteral":

                equivalents = GetEquivalentsFromXMLFile.GetEquivalent(contentInit, "litteral", racine);
                break;
            case "instance":

                equivalents = GetEquivalentsFromXMLFile.GetEquivalent(contentInit, "instance", racine);
                instance = true;
                break;

        }
        ArrayList<String> releventnodeClass = new ArrayList<String>();
//        String content1 = contentInit.replaceAll("\n", " ");
//        String content = content1.replaceAll("\"", " \\\\\" ");
        for (String equivalent : equivalents) {
            boolean litteral = false;
            ArrayList<String> properties = new ArrayList<>();
            if (equivalent.contains("?p")) {
                litteral = true;
                Model m = ModelFactory.createDefaultModel();
                for (RDFNode node : graph.getVertices()) {
                    if (node.isLiteral()) {
                        if (node.asLiteral().getString().contains(contentInit)) {
                            m.add(graph.getInEdges(node).iterator().next());
                            if (!m.isEmpty()) {
                                modelList.add(m);
                                TypeReleventFragment typeReleventFragment3 = new TypeReleventFragment("uri", graph.getNeighbors(node).iterator().next().asResource().getURI());
                                relevantFragment2.add(typeReleventFragment3);
                            }

                            break;
                        }
                    }
                }
                //Récuperation des propriétés utilisées pour que les ressources soient liées au littéral
                //                    queryStringProperties = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                //                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                //                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                //                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                //                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                //                            + "select distinct ?p  "
                //                            + "where { "
                //                            + "?x ?p  " + "\"" + content + "\" ."
                //                            + "} \n ";
                //
                //                    Query query = QueryFactory.create(queryStringProperties);
                //                    // Execute the query and obtain results
                //                    QueryExecution qe = QueryExecutionFactory.create(query, model);
                //                    org.apache.jena.query.ResultSet results = qe.execSelect();
                //
                //                    // Output query results    
                //                    while (results.hasNext()) {
                //                        //   System.out.println(results.next().get("y").toString());
                //
                //                        properties.add("<" + results.next().get("?p").asResource().getURI() + ">");
                //
                //                    }
                //
                //                    qe.close();

            }

//            if (litteral) {
//                //  for (String property : properties) {
//                String newequivalent = equivalent.replace("?p", property);
//                if (newequivalent.contains("abstract")) {
//                    queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
//                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
//                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
//                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
//                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
//                            + "Construct { "
//                            //  + " ?y   " + newequivalent + " ?z  "
//                            + " ?y   " + newequivalent + " \"" + contentInit + "\"  "
//                            + "} where { "
//                            + " ?y   " + newequivalent + " \"" + contentInit + "\"  "
//                            + "} \n ";
//                    Query query = QueryFactory.create(queryString);
//                    QueryExecution qexec = QueryExecutionFactory.create(query, model);
//
//                    Model constructModel = qexec.execConstruct();
//
//                    if (!constructModel.isEmpty()) {
//                        modelList.add(constructModel);
//
//                    }
//                }
//                // }
//            } else {
            //if class or instance
            if (instance) {
                boolean modelget = false;
                queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + " PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + " PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + " PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + " Construct { "
                        + "<" + uri + "> " + equivalent + " ?y .  "
                        + "} where { "
                        + "<" + uri + "> " + equivalent + " ?y .  "
                        + "} \n ";

                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, model);

                Model constructModel = qexec.execConstruct();
                if (!constructModel.isEmpty() && (!modelListString.contains(constructModel.toString()))) {
                    modelList.add(constructModel);
                    modelListString.add(constructModel.toString());
                    modelget = true;
                }

                String queryString3 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "select distinct ?y  "
                        + "where { "
                        + "<" + uri + "> " + equivalent + " ?y .  "
                        + "} \n ";

                Query query3 = QueryFactory.create(queryString3);
                QueryExecution qe3 = QueryExecutionFactory.create(query3, model);
                // try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                org.apache.jena.query.ResultSet results3 = qe3.execSelect();

                // Output query results    
                //  ResultSetFormatter.out(System.out, results, query);
                while (results3.hasNext()) {
                    //   System.out.println(results.next().get("y").toString());
                    TypeReleventFragment typeReleventFragment3 = new TypeReleventFragment("uri", results3.next().get("?y").asResource().getURI());
                    relevantFragment2.add(typeReleventFragment3);
                }
                TypeReleventFragment typeReleventFragment3 = new TypeReleventFragment("uri", uri);
                relevantFragment2.add(typeReleventFragment3);
                qe3.close();
//----
                String queryStringinv = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + " PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + " PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + " PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + " PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + " Construct { "
                        + " ?y  " + equivalent + "<" + uri + "> ."
                        + "} where { "
                        + " ?y  " + equivalent + "<" + uri + "> ."
                        + "} \n ";

                Query queryInv = QueryFactory.create(queryStringinv);
                QueryExecution qexecInv = QueryExecutionFactory.create(queryInv, model);

                Model constructModelInv = qexecInv.execConstruct();
                if (!constructModelInv.isEmpty() && (!modelListString.contains(constructModelInv.toString()))) {
                    modelList.add(constructModelInv);
                    modelListString.add(constructModelInv.toString());

                    modelget = true;
                }
                if (!modelget) {
                    TypeReleventFragment typeReleventFragment = new TypeReleventFragment("uri", uri);
                    relevantFragment.add(typeReleventFragment);
                }

                String queryString3Inv = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "select distinct ?y  "
                        + "where { "
                        + " ?y  " + equivalent + "<" + uri + "> ."
                        + "} \n ";

                Query query3Inv = QueryFactory.create(queryString3Inv);
                QueryExecution qe3Inv = QueryExecutionFactory.create(query3Inv, model);
                // try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                org.apache.jena.query.ResultSet results3Inv = qe3Inv.execSelect();

                // Output query results    
                //  ResultSetFormatter.out(System.out, results, query);
                while (results3Inv.hasNext()) {
                    //   System.out.println(results.next().get("y").toString());
                    TypeReleventFragment typeReleventFragment3Inv = new TypeReleventFragment("uri", results3Inv.next().get("?y").asResource().getURI());
                    relevantFragment2.add(typeReleventFragment3Inv);
                }

                qe3Inv.close();

            }
            if (classe) {

                queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "select distinct ?y  "
                        + "where { "
                        + "<" + uri + "> " + equivalent + " ?y .  "
                        + "} \n ";

                Query query = QueryFactory.create(queryString);
                QueryExecution qe = QueryExecutionFactory.create(query, model);
                // try (QueryExecution qe = QueryExecutionFactory.create(query, model)) {
                org.apache.jena.query.ResultSet results = qe.execSelect();

                // Output query results    
                //  ResultSetFormatter.out(System.out, results, query);
                while (results.hasNext()) {
                    //   System.out.println(results.next().get("y").toString());
                    String result = results.next().get("?y").asResource().getURI();
                    TypeReleventFragment typeReleventFragment = new TypeReleventFragment("uri", result);
                    if (!releventnodeClass.contains(result)) {
                        relevantFragment.add(typeReleventFragment);
                        releventnodeClass.add(result);
                    }
                }
                TypeReleventFragment typeReleventFragment = new TypeReleventFragment("uri", uri);
                if (!releventnodeClass.contains(uri)) {
                    relevantFragment.add(typeReleventFragment);
                    releventnodeClass.add(uri);
                }

                qe.close();
            }
            // }
        }
        TwoReleventFragment twoRelevent = new TwoReleventFragment(relevantFragment, relevantFragment2, modelList);
        return twoRelevent;
    }

    public static class TypeReleventFragment {

        String Type;
        String ElementUriOrContent;

        public String getType() {
            return Type;
        }

        public String getelementUriOrContent() {
            return ElementUriOrContent;
        }

        /**
         *
         *
         */
        public TypeReleventFragment(String type, String elementUriOrContent) {
            ElementUriOrContent = elementUriOrContent;

            Type = type;
        }
    }

    public static class TwoReleventFragment {

        ArrayList<TypeReleventFragment> ReleventFragment;
        ArrayList<TypeReleventFragment> ReleventFragmentModel;
        ArrayList<Model> ModelList;

        public ArrayList<TypeReleventFragment> getReleventFragment() {
            return ReleventFragment;
        }

        public ArrayList<Model> getModelList() {
            return ModelList;
        }

        public ArrayList<TypeReleventFragment> getReleventFragmentModel() {
            return ReleventFragmentModel;
        }

        /**
         *
         *
         */
        public TwoReleventFragment(ArrayList<TypeReleventFragment> releventFragment, ArrayList<TypeReleventFragment> releventFragmentModel, ArrayList<Model> modelList) {
            ReleventFragment = releventFragment;
            ModelList = modelList;
            ReleventFragmentModel = releventFragmentModel;
        }
    }
}
