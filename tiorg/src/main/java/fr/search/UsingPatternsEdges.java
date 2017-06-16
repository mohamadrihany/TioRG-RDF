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
import fr.processings.GetEquivalentsFromXMLFile;
import fr.search.SearchElements.ElementReleventFragmentNodes;
import fr.search.SearchElements.KeywordsReleventFragment;
import fr.search.UsingPatternsNodes.TypeReleventFragment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.jdom2.Element;

/**
 *
 * @author houk
 */
public class UsingPatternsEdges {

    private UsingPatternsEdges() {
    }

    public static ArrayList<Model> main(String uri, String type, String content, String keyword, HashMap<String, KeywordsReleventFragment> releventElementList, Model model, Element racine) throws Exception {

        String queryString;
        ArrayList<String> equivalents = GetEquivalentsFromXMLFile.GetEquivalent(content, "edge", racine);
        ArrayList<Model> modelList = new ArrayList<>();
//chercher les fragments pertinents mais si un des voisins est un element pertinent qui repond à un mot clés non egale au mot clés de l'arc
        Set<String> cles = releventElementList.keySet();
        Iterator<String> it = cles.iterator();
//la liste des noeuds d'extremite
        ArrayList<TypeReleventFragment> nodesListeToUse = new ArrayList<>();

        while (it.hasNext()) {
            String keyword2 = it.next();

            if (!keyword2.equals(keyword)) {
                ArrayList<ElementReleventFragmentNodes> nodesList = releventElementList.get(keyword2).ReleventFragmentNodes;

                for (ElementReleventFragmentNodes list : nodesList) {
                    ArrayList<TypeReleventFragment> releventFragement = list.ReleventFragment;
                    ArrayList<TypeReleventFragment> releventFragementModel = list.ReleventFragmentModel;

                    for (TypeReleventFragment relevent : releventFragement) {
                        nodesListeToUse.add(relevent);
                    }
                    for (TypeReleventFragment relevent : releventFragementModel) {
                        nodesListeToUse.add(relevent);
                    }

                }
            }
        }
        ArrayList<String> modelListString = new ArrayList<String>();
        //recherche des triplets qui répondent directement à la requete (l'arc existe dans le graph sans utilisation de patterns)
        for (TypeReleventFragment extremity : nodesListeToUse) {
            if ("uri".equals(extremity.Type)) {

                queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "Construct    "
                        + "{ "
                        + "<" + extremity.ElementUriOrContent + "> <" + uri + "> ?y .  "
                        + " }  "
                        + "where { "
                        + "<" + extremity.ElementUriOrContent + "> <" + uri + "> ?y .  "
                        + "} \n ";
                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, model);

                Model constructModel = qexec.execConstruct();
                if (!constructModel.isEmpty() && !modelListString.contains(constructModel.toString())) {
                    modelList.add(constructModel);
                    modelListString.add(constructModel.toString());
                }
                //---
                String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "Construct    "
                        + "{ "
                        + "?x <" + uri + "> " + " <" + extremity.ElementUriOrContent + ">"
                        + " }  "
                        + "where { "
                        + "?x <" + uri + "> " + " <" + extremity.ElementUriOrContent + ">"
                        + "} \n ";

                Query query2 = QueryFactory.create(queryString2);
                QueryExecution qexec2 = QueryExecutionFactory.create(query2, model);

                Model constructModel2 = qexec2.execConstruct();

                if (!constructModel2.isEmpty() && !modelListString.contains(constructModel2.toString())) {
                    modelList.add(constructModel2);
                    modelListString.add(constructModel2.toString());
                }
                      //--
                // qexec.close();
                // constructModel.close();

                //  model.close();
            } else {
                String content1 = extremity.ElementUriOrContent.replaceAll("\n", " ");
                String content2 = content1.replaceAll("\"", " \\\\\" ");

                queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                        + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                        + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                        + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                        + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                        + "Construct    "
                        + "{ "
                        + "?x <" + uri + "> \"" + content2 + "\" "
                        + " }  "
                        + "where { "
                        + "?x <" + uri + "> \"" + content2 + "\" "
                        + "} \n ";

                Query query = QueryFactory.create(queryString);
                QueryExecution qexec = QueryExecutionFactory.create(query, model);

                Model constructModel = qexec.execConstruct();
                if ((!constructModel.isEmpty()) && (!modelListString.contains(constructModel.toString()))) {
                    modelList.add(constructModel);
                    modelListString.add(constructModel.toString());
                }

                // qexec.close();
                // constructModel.close();
                //  model.close();
            }
        }

        for (String equivalent : equivalents) {

            for (TypeReleventFragment extremity : nodesListeToUse) {
                //System.out.println("*************    "+ URLEncoder.encode("^"));
                if ("uri".equals(extremity.Type)) {

                    String[] splitEquivalent = equivalent.split("_");
                    queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                            // + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                            + "Construct    "
                            + "{ "
                            + "<" + extremity.ElementUriOrContent + "> " + splitEquivalent[0] + " .  "
                            + "?y " + splitEquivalent[1] + " ?z .  "
                            + " }  "
                            + "where { "
                            + "<" + extremity.ElementUriOrContent + "> " + splitEquivalent[0] + " .  "
                            + "?y " + splitEquivalent[1] + " ?z .  "
                            + "} \n ";
                    Query query = QueryFactory.create(queryString);
                    QueryExecution qexec = QueryExecutionFactory.create(query, model);
                    Model constructModel = qexec.execConstruct();
                    if (!constructModel.isEmpty() && !modelListString.contains(constructModel.toString())) {
                        modelList.add(constructModel);
                        modelListString.add(constructModel.toString());
                    }
                    //---
                    String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                            //  + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                            + "Construct    "
                            + "{ "
                            + " ?x " + splitEquivalent[0] + " .  "
                            + "<" + extremity.ElementUriOrContent + "> " + splitEquivalent[1] + " ?z .  "
                            + " }  "
                            + "where { "
                            + " ?x " + splitEquivalent[0] + " .  "
                            + "<" + extremity.ElementUriOrContent + "> " + splitEquivalent[1] + " ?z .  "
                            + "} \n ";

                    Query query2 = QueryFactory.create(queryString2);
                    QueryExecution qexec2 = QueryExecutionFactory.create(query2, model);
                    Model constructModel2 = qexec2.execConstruct();

                    if ((!constructModel2.isEmpty()) && (!modelListString.contains(constructModel2.toString()))) {
                        modelList.add(constructModel2);
                        modelListString.add(constructModel2.toString());
                    }
                      //--
                    // qexec.close();
                    // constructModel.close();

                    //  model.close();
                } else {
                    String content1 = extremity.ElementUriOrContent.replaceAll("\n", " ");
                    String content2 = content1.replaceAll("\"", " \\\\\" ");
                    String[] splitEquivalent = equivalent.split("_");
                    queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                            //   + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                            + "Construct    "
                            + "{ "
                            + "\"" + content2 + "\" " + splitEquivalent[0] + " .  "
                            + "?y " + splitEquivalent[1] + " ?z .  "
                            + " }  "
                            + "where { "
                            + "\"" + content2 + "\" " + splitEquivalent[0] + " .  "
                            + "?y " + splitEquivalent[1] + " ?z .  "
                            + "} \n ";

                    Query query = QueryFactory.create(queryString);
                    QueryExecution qexec = QueryExecutionFactory.create(query, model);

                    Model constructModel = qexec.execConstruct();
                    if ((!constructModel.isEmpty()) && (!modelListString.contains(constructModel.toString()))) {
                        modelList.add(constructModel);
                        modelListString.add(constructModel.toString());
                    }
                    //---
                    String queryString2 = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                            + "PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
                            + "PREFIX owl: <http://www.w3.org/2002/07/owl#>"
                            //   + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX swrc: <http://swrc.ontoware.org/ontology#>"
                            + "PREFIX aifb: <http://www.aifb.uni-karlsruhe.de/Personen/viewPersonOWL/> "
                            + "Construct    "
                            + "{ "
                            + " ?x " + splitEquivalent[0] + " .  "
                            + "\"" + content2 + "\" " + splitEquivalent[1] + " ?z .  "
                            + " }  "
                            + "where { "
                            + " ?x " + splitEquivalent[0] + " .  "
                            + "\"" + content2 + "\" " + splitEquivalent[1] + " ?z .  "
                            + "} \n ";

                    Query query2 = QueryFactory.create(queryString2);
                    QueryExecution qexec2 = QueryExecutionFactory.create(query2, model);

                    Model constructModel2 = qexec2.execConstruct();

                    if ((!constructModel2.isEmpty()) && (!modelListString.contains(constructModel2.toString()))) {
                        modelList.add(constructModel2);
                        modelListString.add(constructModel2.toString());
                    }
                      //--
                    // qexec.close();
                    // constructModel.close();

                    //  model.close();
                }
            }
        }
        return modelList;

    }

}
