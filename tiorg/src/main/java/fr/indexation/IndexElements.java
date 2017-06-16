package fr.indexation;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.FileManager;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import edu.uci.ics.jung.graph.DirectedGraph;
import fr.processings.FileToModelGraph;

/**
 * Index all elements of the given owl graph using Lucene API.
 */
public class IndexElements 
{
	private String dirTarget;
	private String fileSource;
    private ArrayList<EdgeTypeURI> listEdges = new ArrayList<>();
    private ArrayList<String> listEdgesURI = new ArrayList<>();

    public IndexElements(String dirTarget, String fileSource) 
    {
    	this.dirTarget = dirTarget;
    	this.fileSource = fileSource;
    }
    
    public void run() throws IOException
    {
    	Directory dir = FSDirectory.open(Paths.get(dirTarget));
    	if(dir == null) 
            throw new IOException("Target folder error");
    	
    	File file = new File(fileSource);
    	if(!file.isFile() || !file.exists())
    		throw new IOException("Source file error");
    	
        Analyzer analyzer = new StandardAnalyzer();
        IndexWriterConfig iwc = new IndexWriterConfig(analyzer);

        IndexWriter writer = null;
        try
        {
        	writer = new IndexWriter(dir, iwc);
        	indexDocs(writer, fileSource);
        }
        finally
        {
        	if(writer != null)
        	{
        		try { writer.close(); }
        		catch(Exception e) {}
        	}
        }
    }

    /**
     * Indexes the given rdf graph using the given writer,
     *
     * @param writer Writer to the index where the given file/dir info will be
     * stored
     * @param path The rdf graph to index,
     * @throws IOException If there is a low-level I/O error
     */
    private void indexNodes(IndexWriter writer, RDFNode node, DirectedGraph<RDFNode, Statement> graph) throws IOException {
        boolean edgeType = false;
        // make a new, empty document
        Document doc = new Document();
        String type = "instance";
        Collection<Statement> inEdges = graph.getInEdges(node);
        Collection<Statement> outEdges = graph.getOutEdges(node);

        for (Statement stat : outEdges) {
            if (("domain".equals(stat.getPredicate().getLocalName())) || ("range".equals(stat.getPredicate().getLocalName()))) {

                EdgeTypeURI edge = new EdgeTypeURI(node.asResource().getLocalName(), node.asResource().getURI());

                if (!listEdgesURI.contains(node.asResource().getURI())) {
                    listEdgesURI.add(node.asResource().getURI());
                    listEdges.add(edge);
                    edgeType = true;
                    break;
                }

            } else {
                if ("type".equals(stat.getPredicate().getLocalName())) {
                    if (stat.getObject().isResource()) {
                        if (stat.getObject().asResource().getLocalName().equals("ObjectProperty")) {
                            EdgeTypeURI edge = new EdgeTypeURI(node.asResource().getLocalName(), node.asResource().getURI());
                            if (!listEdgesURI.contains(node.asResource().getURI())) {
                                listEdgesURI.add(node.asResource().getURI());
                                listEdges.add(edge);
                                edgeType = true;
                                break;
                            }
                        }
                    } else {
                        if (stat.getObject().asLiteral().getString().equals("ObjectProperty")) {
                            EdgeTypeURI edge = new EdgeTypeURI(node.asResource().getLocalName(), node.asResource().getURI());
                            if (!listEdgesURI.contains(node.asResource().getURI())) {
                                listEdgesURI.add(node.asResource().getURI());
                                listEdges.add(edge);
                                edgeType = true;
                                break;
                            }
                        }
                    }
                }
            }
        }
        if (!edgeType) {
            for (Statement stat : inEdges) {
                if ("type".equals(stat.getPredicate().getLocalName()) || "subClassOf".equals(stat.getPredicate().getLocalName())) {
                    type = "class";
                    break;
                }
            }
            if (!"class".equals(type)) {
                for (Statement stat : outEdges) {
                    if ("subClassOf".equals(stat.getPredicate().getLocalName())) {
                        type = "class";
                        break;
                    }
                }
            }

            if (node.isResource()) {
                if (node.asResource().getLocalName() != null) {
                    Field contentField = new TextField("content", node.asResource().getLocalName(), Field.Store.YES);
                    Field typeField = new StringField("type", type, Field.Store.YES);
                    Field uriField = new StringField("uri", node.asResource().getURI(), Field.Store.YES);
                    Field propertyField = new StringField("property", "", Field.Store.YES);
                    doc.add(contentField);
                    doc.add(typeField);
                    doc.add(uriField);
                    doc.add(propertyField);
                }
            } else {

                Field contentField = new TextField("content", node.asLiteral().getString(), Field.Store.YES);
                Field typeField = new StringField("type", "litteral", Field.Store.YES);
                Field uriField = new StringField("uri", "", Field.Store.YES);
                Collection<Statement> inedge = graph.getInEdges(node);
                Iterator<Statement> iter = inedge.iterator();
                Field propertyField = new StringField("property", "<" + iter.next().getPredicate().getURI() + ">", Field.Store.YES);;
                doc.add(uriField);
                doc.add(contentField);
                doc.add(typeField);
                doc.add(propertyField);
            }

            writer.addDocument(doc);
        }
    }

    /**
     * Indexes a single edges, but added to the index only edges  with different URI
     */
    private void indexEdges(IndexWriter writer, EdgeTypeURI edge) throws IOException 
    {
        // make a new, empty document
        Document doc = new Document();
        String type = "edge";

        Field contentField = new TextField("content", edge.getEdge(), Field.Store.YES);
        Field typeField = new StringField("type", type, Field.Store.YES);
        Field uriField = new StringField("uri", edge.URI, Field.Store.YES);
        Field propertyField = new StringField("property", "", Field.Store.YES);
        doc.add(contentField);
        doc.add(typeField);
        doc.add(uriField);
        doc.add(propertyField);
        writer.addDocument(doc);

    }
	/**
	 * Index each element (edge or vertex)
	 */
	private void indexDocs(IndexWriter writer, String path) throws IOException 
	{
        Model model = FileManager.get().loadModel(path);

        DirectedGraph<RDFNode, Statement> graph = FileToModelGraph.ModelToGraph(model);
        Collection<RDFNode> vertices = graph.getVertices();
        StmtIterator statIter = model.listStatements();
        for (RDFNode node : vertices) {

            indexNodes(writer, node, graph);
        }

        while (statIter.hasNext()) {
            Statement stat = statIter.next();
            EdgeTypeURI edge = new EdgeTypeURI(stat.getPredicate().getLocalName(), stat.getPredicate().getURI());
            if (!listEdgesURI.contains(stat.getPredicate().getURI())) {
                listEdgesURI.add(stat.getPredicate().getURI());
                listEdges.add(edge);

            }

        }
        for (EdgeTypeURI edge : listEdges) {
            indexEdges(writer, edge);
        }

        writer.commit();
        writer.close();
    }
	
	/**
     * Index all element of the given owl file.
     */
    public static void main(String[] args) throws IOException 
    {
        String indexPath = "../TioRG/data/index";
        String docsPath = "../TioRG/data/Graphs/ontologyDemo.owl";

        Date start = new Date();
        try {
            System.out.println("Indexing to directory '" + indexPath + "'...");
            
            IndexElements index = new IndexElements(indexPath, docsPath);
            index.run();

            Date end = new Date();
            System.out.println(end.getTime() - start.getTime() + " total milliseconds");

        } catch (IOException e) {
            System.out.println(" caught a " + e.getClass()
                    + "\n with message: " + e.getMessage());
        }
    }

    public static class EdgeTypeURI {

        String Edge;
        String URI;

        public String getEdge() {
            return Edge;
        }

        public String getURI() {
            return URI;
        }

        public EdgeTypeURI(String edge, String uri) {
            Edge = edge;

            URI = uri;
        }
    }
}
