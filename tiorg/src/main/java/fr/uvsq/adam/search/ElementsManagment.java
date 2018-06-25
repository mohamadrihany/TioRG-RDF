/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.lucene.document.Document;
import org.jdom2.Element;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import fr.uvsq.adam.processings.FileToModelGraph;
import fr.uvsq.adam.search.LuceneSearch.PairLuceneScore;
import fr.uvsq.adam.search.Ranking.TupleResultScore;
import fr.uvsq.adam.search.ResultConstruction.ResultNodeModel;
import fr.uvsq.adam.search.SearchElements.ElementReleventFragmentEdges;
import fr.uvsq.adam.search.SearchElements.KeywordsReleventFragment;
import fr.uvsq.adam.search.SetSetReleventFragments.ModelNode;

import javax.xml.soap.Node;

/**
 *
 * @author houk
 */
public class ElementsManagment {

    public static Hashtable<String, Float> luceneScore= new Hashtable<>();
    public static Hashtable<String, Float> luceneScoreEdge= new Hashtable<>();

    public static HashMap<String,String> RankingMap = new HashMap<>();

    private ElementsManagment() {
    }

    public static ArrayList<TupleResultScore> main(String dirIndex, String keyWordsString, String[] keywords, Element keyWordsOptions, Model model, Graph g, UndirectedSparseGraph<RDFNode, Statement> undirectedGraph) throws Exception {
        DirectedGraph<RDFNode, Statement> graph = (DirectedGraph) g;
        HashMap<String, KeywordsReleventFragment> releventElementList = new HashMap<>();

        long start = System.currentTimeMillis();

        //je fais la recherhe pour récupérer les fragments pertinents pour les nodes et les arcs on les fait après 
        for (String keyword : keywords) {
            try {
                //relevantElement a aussi les elements recuperes par la recherche direct
                KeywordsReleventFragment releventElement = SearchElements.main(dirIndex, keyword, graph, model, keyWordsOptions);

                releventElementList.put(keyword, releventElement);

                //*
                //check if there is a releventElements in the data set related to the keyword // level 1
                if (releventElement.getReleventFragmentEdges().isEmpty() && releventElement.getReleventFragmentNodes().isEmpty()) {
                    //check if there is a synonyms is the wordNet synonyms
                    //synonyms
                    String synonyme = SearchWordNetSynonyms.main(keyword);
                    System.out.println("-------------------------");

                    KeywordsReleventFragment releventElements = new KeywordsReleventFragment();
                    releventElements.Keyword = keyword;
                    List<String> synonymeList;
                    synonymeList = Arrays.asList(synonyme.split(" "));
                    if(synonymeList.size()!=1) {
                        for (String syn : synonymeList) {
                            KeywordsReleventFragment releventElementSynonyme = SearchElements.main(dirIndex, syn, graph, model, keyWordsOptions);
                            if (!releventElementSynonyme.ReleventFragmentEdges.isEmpty() || !releventElementSynonyme.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementSynonyme.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementSynonyme.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "synonym";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "synonym";
                                    RankingMap.put(s,wordnet);
                                }


                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }

                                //releventElements.ReleventFragmentNodes=matchingNodes;
                                //releventElements.ReleventFragmentEdges=matchingEdges;

                            }
                        }

                    }

                    //Hyprenyms
                    String hypernyms = SearchWordNetHyper.main(keyword);
                    if (!hypernyms.equals("")) {
                        List<String> hypernymsList;
                        hypernymsList = Arrays.asList(hypernyms.split(" "));
                        for (String hyp : hypernymsList) {
                            KeywordsReleventFragment releventElementHyper = SearchElements.main(dirIndex, hyp, graph, model, keyWordsOptions);

                            if (!releventElementHyper.ReleventFragmentEdges.isEmpty() || !releventElementHyper.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementHyper.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementHyper.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "Hyprenyms";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "Hyprenyms";
                                    RankingMap.put(s,wordnet);
                                }

                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }

                            }
                        }
                    }

                    //Antonyms
                    String similar = SearchWordNetAntonym.main(keyword);
                    if(!similar.equals("")) {
                        List<String> similarList;
                        similarList = Arrays.asList(similar.split(" "));
                        for (String simi : similarList) {
                            KeywordsReleventFragment releventElementAntonym = SearchElements.main(dirIndex, simi, graph, model, keyWordsOptions);

                            if (!releventElementAntonym.ReleventFragmentEdges.isEmpty() || !releventElementAntonym.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementAntonym.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementAntonym.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "Antonyms";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "Antonyms";
                                    RankingMap.put(s,wordnet);
                                }

                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }
                            }
                        }
                    }

                    releventElementList.put(keyword, releventElements);

                }

                // level 2

                KeywordsReleventFragment releventElements;
                releventElements = releventElementList.get(keyword);
                if(releventElements.ReleventFragmentEdges.size()==0 && releventElements.ReleventFragmentNodes.size()==0){

                    //Hyponyms
                    String hyponyms = SearchWordNetHypo.main(keyword);
                    if(!hyponyms.equals("")) {
                        List<String> HypoList;
                        HypoList = Arrays.asList(hyponyms.split(" "));
                        for (String hypo : HypoList) {
                            KeywordsReleventFragment releventElementHypo = SearchElements.main(dirIndex, hypo, graph, model, keyWordsOptions);
                            if (!releventElementHypo.ReleventFragmentEdges.isEmpty() || !releventElementHypo.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementHypo.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementHypo.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "Hyponyms";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "Hyponyms";
                                    RankingMap.put(s,wordnet);
                                }

                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }

                            }
                        }
                    }

                    //Meronyms

                    String meronyms = SearchWordNetMero.main(keyword);
                    if(!meronyms.equals("")) {
                        List<String> MeroList;
                        MeroList = Arrays.asList(meronyms.split(" "));
                        for (String mero : MeroList) {

                            KeywordsReleventFragment releventElementMero = SearchElements.main(dirIndex, mero, graph, model, keyWordsOptions);
                            if (!releventElementMero.ReleventFragmentEdges.isEmpty() || !releventElementMero.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementMero.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementMero.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "Meronyms";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "Meronyms";
                                    RankingMap.put(s,wordnet);
                                }

                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }
                            }
                        }
                    }

                    //Holonyms
                    String holonyms = SearchWordNetHolo.main(keyword);
                    if(!holonyms.equals("")) {
                        List<String> HoloList;
                        HoloList = Arrays.asList(holonyms.split(" "));
                        for (String holo : HoloList) {
                            KeywordsReleventFragment releventElementHolo = SearchElements.main(dirIndex, holo, graph, model, keyWordsOptions);
                            if (!releventElementHolo.ReleventFragmentEdges.isEmpty() || !releventElementHolo.ReleventFragmentNodes.isEmpty()) {

                                ArrayList<Document> matchingEdges = releventElementHolo.ReleventFragmentEdges;
                                ArrayList<SearchElements.ElementReleventFragmentNodes> matchingNodes = releventElementHolo.ReleventFragmentNodes;
                                for(Document document : matchingEdges){
                                    String s =document.getField("uri").stringValue();
                                    String wordnet = "Holonyms";
                                    RankingMap.put(s,wordnet);
                                }
                                for(SearchElements.ElementReleventFragmentNodes nodes : matchingNodes){
                                    String s =nodes.URIelement;
                                    String wordnet = "Holonyms";
                                    RankingMap.put(s,wordnet);
                                }

                                ArrayList<SearchElements.ElementReleventFragmentNodes> releventNodes = new ArrayList<SearchElements.ElementReleventFragmentNodes>();
                                releventNodes = releventElements.ReleventFragmentNodes;
                                ArrayList<Document> releventEdges = new ArrayList<Document>();
                                releventEdges = releventElements.ReleventFragmentEdges;
                                if(matchingNodes.size()>=1){
                                    releventNodes.addAll(matchingNodes);
                                }
                                if(matchingEdges.size()>=1){
                                    releventEdges.addAll(matchingEdges);

                                }

                            }
                        }
                    }

                    releventElementList.put(keyword, releventElements);

                }


                //*

            } catch (Exception ex) {
                Logger.getLogger(ElementsManagment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //releventElementList.forEach((key, value) -> System.out.println(key + ": Nodes" + value.ReleventFragmentNodes.toString() +"---- Edges"+value.ReleventFragmentEdges.toString()  ));
        PairLuceneScore pairLuceneScore = LuceneSearch.main(dirIndex, keyWordsString, graph, model, keyWordsOptions);
        
        //je fais la recherhe pour récupérer les fragments pertinents pour les arcs en tenant en compte les fragments pertinents récupérés pour les nodes.
        ArrayList<ElementReleventFragmentEdges> ReleventFrangmentListForEdges = new ArrayList<>();
        Set<String> cles = releventElementList.keySet();
        Iterator<String> it = cles.iterator();
        while (it.hasNext()) {
            String keyword = it.next();
            ArrayList<Document> edgesList = releventElementList.get(keyword).ReleventFragmentEdges;

            for (Document doc : edgesList) {
                ArrayList<Model> modelList = UsingPatternsEdges.main(doc.getField("uri").stringValue(), doc.getField("type").stringValue(), doc.getField("content").stringValue(), keyword, releventElementList, model, keyWordsOptions);
                ElementReleventFragmentEdges elementReleventFragment = new ElementReleventFragmentEdges(modelList, doc.getField("uri").stringValue(), keyword);
                ReleventFrangmentListForEdges.add(elementReleventFragment);
            }
        }
//        UndirectedSparseGraph<RDFNode, Statement> undirectedGraph = FromDirectedToUndirectedGraph.FromDirectedToUndirectedGraph(graph);
        // MST.main(undirectedGraph, releventElementList,ReleventFrangmentListForEdges,model);
        ArrayList<ArrayList<ModelNode>> cartesienProduct = SetSetReleventFragments.main(graph, releventElementList, ReleventFrangmentListForEdges, model);
        ArrayList<ResultNodeModel> resultList = ResultConstruction.main(cartesienProduct, model, undirectedGraph, graph);
        ArrayList<TupleResultScore> finalResultList = Ranking.main(resultList, pairLuceneScore, keyWordsOptions);
        long end = System.currentTimeMillis();
            ArrayList<TupleResultScore> list = new ArrayList<>() ;
        if(finalResultList.size()>30){
                for (int index=finalResultList.size()-30; index <finalResultList.size(); ++index) {
                    list.add(finalResultList.get(index));
                }
    //list=   (ArrayList<TupleResultScore>) finalResultList.subList(finalResultList.size()-30, finalResultList.size()-1);
        }else{
         list = finalResultList;   
        }
        ArrayList<TupleResultScore> list2 = new ArrayList<>();
        for(TupleResultScore rr:list){
            DirectedGraph<RDFNode, Statement> xx = FileToModelGraph.ModelToGraph( rr.Result.Model);
            Model m = ModelFactory.createDefaultModel();
            m=   rr.Result.Model;
            for(RDFNode n:xx.getVertices()){
                
              for(Statement s:  graph.getOutEdges(n)){
                  if(s.getPredicate().getLocalName().equals("name")){
                    
                    m.add(s);
                      
                    break;
                  }
              }
                
            }
      ResultNodeModel  hh=new  ResultNodeModel  (m, rr.Result.Node, rr.Result.TypeResult);
           TupleResultScore vv =new TupleResultScore(hh,rr.Score); 
           list2.add(vv);
        }
        System.out.println((end - start) * 0.001);
        return list2;
    }
}
