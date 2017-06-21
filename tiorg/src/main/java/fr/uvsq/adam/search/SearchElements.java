/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import edu.uci.ics.jung.graph.DirectedGraph;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.jdom2.Element;

/**
 *
 * @author houk
 */
public class SearchElements {

   
 public static Hashtable<String, Float> luceneScore;
    public static Hashtable<String, Float> luceneScoreEdge;
    private SearchElements() {
    }

    public static KeywordsReleventFragment main(String dirIndex, String keyword, DirectedGraph<RDFNode, Statement> graph, Model model, Element racine) throws Exception {

        ArrayList< ElementReleventFragmentNodes> ReleventFrangementListForNodes = new ArrayList<>();
        //  ArrayList< ElementReleventFragmentEdges> ReleventFrangementListForEdges = new ArrayList<>();
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(dirIndex)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer();

        QueryParser queryParser = new QueryParser("content", analyzer);

        Query query = queryParser.parse(keyword);


//return the result

        TopDocs topDocs = searcher.search(query, 100);
        System.out.println("Nombre total de documents  " + topDocs.totalHits);
        ScoreDoc[] scoreDocArray = topDocs.scoreDocs;
//        System.out.println("Les elements trouvés:");
//
//
////        //à supprimer car juste affichage
//        for (ScoreDoc scoredoc : scoreDocArray) {
////            // Retourne le document et affiche les détails
//           Document doc = searcher.doc(scoredoc.doc);
//scoredoc.
////
////            System.out.println("\ncontent: " + doc.getField("content").stringValue());
////            System.out.println("uri: " + doc.getField("uri").stringValue());
//            System.out.println("type: " + doc.getField("type").stringValue());
////
//       }
//        System.out.println();
//        System.out.println("------------------Utilisation des patterns 'nodes'");
//        System.out.println();
        //Utilisation des patterns pour les noeuds
        luceneScore = new Hashtable<>();
        luceneScoreEdge = new Hashtable<>();
        ArrayList<Document> edgeDocList = new ArrayList<>();
        for (ScoreDoc scoredoc : scoreDocArray) {

            Document doc = searcher.doc(scoredoc.doc);
            //la recuperation de fragments pertinents pour les noeuds 
            if (!"edge".equals(doc.getField("type").stringValue())) {

                UsingPatternsNodes.TwoReleventFragment relevent = UsingPatternsNodes.main(doc.getField("uri").stringValue(), doc.getField("type").stringValue(), doc.getField("content").stringValue(), doc.getField("property").stringValue(), model, racine, graph);
                ArrayList<UsingPatternsNodes.TypeReleventFragment> releventFragments = relevent.ReleventFragment;
                ArrayList<UsingPatternsNodes.TypeReleventFragment> releventFragmentModel = relevent.ReleventFragmentModel;
                ArrayList<Model> listModel = relevent.getModelList();
                //separer le cas de litteral et uri ressource pour recuperer le contenu (saved in uri for ressources and in content for litterals 
                if ("litteral".equals(doc.getField("type").stringValue())) {
                    luceneScore.put(doc.getField("content").stringValue(), scoredoc.score);
                    ElementReleventFragmentNodes elementReleventFragment = new ElementReleventFragmentNodes(releventFragments, doc.getField("content").stringValue(), doc.getField("type").stringValue(), releventFragmentModel, listModel);

                    ReleventFrangementListForNodes.add(elementReleventFragment);
                } else {
                    luceneScore.put(doc.getField("uri").stringValue(), scoredoc.score);
                    ElementReleventFragmentNodes elementReleventFragment = new ElementReleventFragmentNodes(releventFragments, doc.getField("uri").stringValue(), doc.getField("type").stringValue(), releventFragmentModel, listModel);

                    ReleventFrangementListForNodes.add(elementReleventFragment);
                }
            } else {
                edgeDocList.add(doc);
                luceneScoreEdge.put(doc.getField("uri").stringValue(), scoredoc.score);
            }
        }

        //Affichage des element recuperes par les patterns 
//        for (ElementReleventFragmentNodes relev : ReleventFrangementListForNodes) {
//            System.out.println("======les elements recuperés pour " + relev.URIelement + " de type " + relev.TypeElement);
//            for (TypeReleventFragment elem : relev.getReleventFragment) {
//                System.out.println(elem.ElementUriOrContent);
//            }
//        }

        //   ArrayList<Document> edgeDocList = new ArrayList<>();
//        for (ScoreDoc scoredoc : scoreDocArray) {
//            //la liste des documents arc
//            Document doc = searcher.doc(scoredoc.doc);
//
//            if ("edge".equals(doc.getField("type").stringValue())) {
//                edgeDocList.add(doc);
////                ArrayList<Model> modelList = UsingPatternsEdges.main(doc.getField("uri").stringValue(), doc.getField("type").stringValue(), doc.getField("content").stringValue());
////                ElementReleventFragmentEdges elementReleventFragment = new ElementReleventFragmentEdges(modelList, doc.getField("uri").stringValue());
////                ReleventFrangementListForEdges.add(elementReleventFragment);
////
//            }
//        }

        KeywordsReleventFragment releventFragment = new KeywordsReleventFragment(keyword, edgeDocList, ReleventFrangementListForNodes);


        return releventFragment;
    }

    public static class KeywordsReleventFragment {

        String Keyword;
        ArrayList<Document> ReleventFragmentEdges;
        ArrayList<ElementReleventFragmentNodes> ReleventFragmentNodes;

        public ArrayList<Document> getReleventFragmentEdges() {
            return ReleventFragmentEdges;
        }

        public String getKeyword() {
            return Keyword;
        }

        public ArrayList<ElementReleventFragmentNodes> getReleventFragmentNodes() {
            return ReleventFragmentNodes;
        }

        /**
         *
         *
         */
        public KeywordsReleventFragment(String keyword, ArrayList<Document> releventFragmentEdges, ArrayList<ElementReleventFragmentNodes> releventFragmentNodes) {
            ReleventFragmentEdges = releventFragmentEdges;

            ReleventFragmentNodes = releventFragmentNodes;
            Keyword = keyword;
        }
    }
//urielement and TypeElement are for the element before patterns (l'element de recuperation)

    public static class ElementReleventFragmentNodes {

        ArrayList<UsingPatternsNodes.TypeReleventFragment> ReleventFragment;
        String URIelement;
        String TypeElement;
        ArrayList<UsingPatternsNodes.TypeReleventFragment> ReleventFragmentModel;
        ArrayList<Model> ListModel;

        public ArrayList<UsingPatternsNodes.TypeReleventFragment> getReleventFragment() {
            return ReleventFragment;
        }

        public ArrayList<Model> getListModel() {
            return ListModel;
        }

        public ArrayList<UsingPatternsNodes.TypeReleventFragment> getReleventFragmentModel() {
            return ReleventFragmentModel;
        }

        public String getURIelement() {
            return URIelement;
        }

        public String getTypeelement() {
            return TypeElement;
        }

        /**
         * param getReleventFragment
         *
         * @
         *
         */
        public ElementReleventFragmentNodes(ArrayList<UsingPatternsNodes.TypeReleventFragment> releventFragment, String uRIelement, String typeElement, ArrayList<UsingPatternsNodes.TypeReleventFragment> releventFragmentModel, ArrayList<Model> listModel) {
            ReleventFragment = releventFragment;
            ReleventFragmentModel = releventFragmentModel;
            URIelement = uRIelement;
            TypeElement = typeElement;
            ListModel = listModel;
        }
    }

    public static class ElementReleventFragmentEdges {

        ArrayList<Model> ReleventFragment;
        String URIelement;
        String KeyWord;

        public ArrayList<Model> getReleventFragment() {
            return ReleventFragment;
        }

        public String getKeyWord() {
            return KeyWord;
        }

        public String getURIelement() {
            return URIelement;
        }

        /**
         * param getReleventFragment
         *
         * @
         *
         */
        public ElementReleventFragmentEdges(ArrayList<Model> releventFragment, String uRIelement, String keyWord) {
            ReleventFragment = releventFragment;
            KeyWord = keyWord;
            URIelement = uRIelement;

        }
    }
}
