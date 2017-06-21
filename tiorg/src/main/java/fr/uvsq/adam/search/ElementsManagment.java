/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.search;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
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

/**
 *
 * @author houk
 */
public class ElementsManagment {

    public static Hashtable<String, Float> luceneScore= new Hashtable<>();;
    public static Hashtable<String, Float> luceneScoreEdge= new Hashtable<>();;

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
            } catch (Exception ex) {
                Logger.getLogger(ElementsManagment.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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
