package fr.uvsq.adam.search;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

public class SearchWordNetSynonyms {

    public SearchWordNetSynonyms(){
    }

    public static String main(String keyword) throws ParseException, IOException {
        String synonyme="";

        String index="/home/mohamad/Desktop/tioRG/TioRG-RDF/examples/test/indexWordNet/index";
        IndexReader reader = DirectoryReader.open(FSDirectory.open(Paths.get(index)));
        IndexSearcher searcher = new IndexSearcher(reader);

        Analyzer analyzer = new StandardAnalyzer();

        QueryParser queryParser = new QueryParser("content", analyzer);


        Query query = queryParser.parse(keyword);
        TopDocs topDocs = searcher.search(query, 100);
        //System.out.println("Number of documents for the Wordnet =  " + topDocs.totalHits);
        ScoreDoc[] scoreDocArray = topDocs.scoreDocs;
        for (ScoreDoc scoredoc : scoreDocArray) {

            Document doc = searcher.doc(scoredoc.doc);
            synonyme = doc.getField("synonyme").stringValue();


            System.out.println(keyword + " synonyme "+ synonyme);

        }

        return synonyme;
    }

}
