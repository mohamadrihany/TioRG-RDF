package fr.views;

import edu.uci.ics.jung.graph.DirectedGraph;
import fr.indexation.IndexElements;
import fr.processings.FileToModelGraph;
import fr.processings.ReadXML;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.jdom2.Element;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.*;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.*;
import java.io.*;
import java.util.*;

public class ProjectManager {
    public static String PROJECT_NAME = "name";

    public static String GRAPH_FILE = "graph-file";
    public static String GRAPH_PREDICATES = "graph-predicates";
    public static String CLUSTER_THRESHOLD = "cluster-threshold";
    public static String CLUSTER_EXPANSION = "cluster-expansion";
    public static String CLUSTER_DONE = "cluster-done";

    public static String PREDICATE_NAME = "name";
    public static String PREDICATE_CRITERIA = "criteria";
    public static String PREDICATE_VALUE = "value";

    public static String PROJECT_FILE = "project.xml";
    public static String INDEX_DIR = "index";
    public static String PREDICATES_FILE = "predicates.xml";
    public static String QUERIES_FILE = "queries.xml";
    public static String XY_FILE = "project_xy.xml";
    public static String KEYWORDS_FILE = "keywords_search.xml";

    private File dir;
    private File fileProject;
    private File dirIndex;
    private File fileGraph;
    private File filePredicates;
    private File fileQueries;
    private File fileXY;
    private File fileKeywords;

    private String name;
    private Properties properties;
    private Properties predicates;
    private ArrayList<String> queries;
    private HashMap<String, HashMap<String, Point>> projectXY;


    public File getDir() {
        return dir;
    }

    public File getFileGraph() {
        return fileGraph;
    }

    public String getName() {
        return name;
    }

    public Properties getProperties() {
        return properties;
    }

    public double getClusterThreshold() {
        String txt = properties.getProperty(CLUSTER_THRESHOLD);
        if (txt != null) {
            try {
                return Double.parseDouble(txt);
            } catch (Exception e) {
            }
        }
        return 0.2;
    }

    public double getClusterExpansion() {
        String txt = properties.getProperty(CLUSTER_EXPANSION);
        if (txt != null) {
            try {
                return Double.parseDouble(txt);
            } catch (Exception e) {
            }
        }
        return 0.1;
    }

    public boolean getClusterDone() {
        String txt = properties.getProperty(CLUSTER_DONE);
        if (txt != null) {
            try {
                return Boolean.parseBoolean(txt);
            } catch (Exception e) {
            }
        }
        return false;
    }

    public void setClusterDone(boolean done) {
        properties.setProperty(CLUSTER_DONE, Boolean.toString(done));
        if (!done) {
            HashMap<String, Point> main = getXY();
            projectXY.clear();
            if (main != null)
                addXY(main);
        }
    }

    public void clearPredicates() {
        predicates.clear();
    }

    public void addPredicate(Properties predicate) {
        String name = (String) predicate.remove(PREDICATE_NAME);
        if (name != null) {
            if (predicate.containsKey(PREDICATE_CRITERIA))
                if (!predicate.isEmpty())
                    predicates.put(name, predicate);
        }
    }

    public Properties getPredicates() {
        return predicates;
    }

    public void clearQueries() {
        queries.clear();
    }

    public void addQuery(String query) {
        if (query != null && query.length() > 0)
            queries.add(query);
    }

    public ArrayList<String> getQueries() {
        return queries;
    }

    public void addXY(HashMap<String, Point> xy) {
        addXY("main", xy);
    }

    public void addXY(String graphName, HashMap<String, Point> xy) {
        projectXY.put(graphName, xy);
    }

    public HashMap<String, Point> getXY() {
        return getXY("main");
    }

    public HashMap<String, Point> getXY(String graphName) {
        return projectXY.get(graphName);
    }

    private void checkFileGraph() throws IOException {
        if (fileGraph == null) {
            fileGraph = new File(dir, properties.getProperty(GRAPH_FILE));
            if (!fileGraph.isFile() || !fileGraph.exists())
                throw new IOException("Graph location error");
        }
    }

    private void checkFileKeywords() throws IOException {
        if (fileKeywords == null)
            fileKeywords = new File(dir, KEYWORDS_FILE);
        if (!fileKeywords.exists()) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = getClass().getResourceAsStream("/keywords_search.xml");
                output = new FileOutputStream(fileKeywords);
                int read;
                byte[] bytes = new byte[1024];
                while ((read = input.read(bytes)) != -1) {
                    output.write(bytes, 0, read);
                }
            } finally {
                if (input != null)
                    try {
                        input.close();
                    } catch (Exception ex) {
                    }

                if (output != null)
                    try {
                        output.close();
                    } catch (Exception ex) {
                    }
            }
        }
    }

    public void setProperty(String property, String value) {
        properties.setProperty(property, value);
    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public DirectedGraph<RDFNode, Statement> loadGraph() throws IOException {
        checkFileGraph();
        return FileToModelGraph.FileToGraph(fileGraph.getAbsolutePath());
    }

    public File getDirIndex() {
        return dirIndex;
    }

    public File getKeywordsFile() {
        return fileKeywords;
    }

    public Element getKeywordsOptions() {
        if (fileKeywords.exists()) {
            try {
                return ReadXML.main(fileKeywords.getAbsolutePath());
            } catch (Exception e) {

            }
        }
        return null;
    }

    public void save() throws Exception {
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(fileProject);
            properties.storeToXML(stream, "UTF-8");
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e) {
                }
        }


        try {
            ProjectPredicates array = new ProjectPredicates();
            if (!predicates.isEmpty()) {
                for (Map.Entry<Object, Object> predicate : predicates.entrySet()) {
                    ProjectPredicate cur = new ProjectPredicate();
                    cur.add(new MapElements(PREDICATE_NAME, predicate.getKey().toString()));
                    for (Map.Entry<Object, Object> val : ((Properties) predicate.getValue()).entrySet())
                        cur.add(new MapElements(val.getKey().toString(), val.getValue().toString()));
                    array.add(cur);
                }
            }

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectPredicates.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(array, xmlWriter);

            stream = new FileOutputStream(filePredicates);
            stream.write(xmlWriter.toString().getBytes());
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e) {
                }
        }

        try {
            ProjectQueries array = new ProjectQueries(queries);

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectQueries.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(array, xmlWriter);

            stream = new FileOutputStream(fileQueries);
            stream.write(xmlWriter.toString().getBytes());
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e) {
                }
        }

        try {
            ProjectXY array = new ProjectXY();
            if (!projectXY.isEmpty()) {
                for (Map.Entry<String, HashMap<String, Point>> graph : projectXY.entrySet()) {
                    GraphXY cur = new GraphXY();
                    cur.coordinates = new ArrayList<NodeXY>();

                    cur.name = graph.getKey();
                    for (Map.Entry<String, Point> node : graph.getValue().entrySet())
                        cur.coordinates.add(new NodeXY(node.getKey(), node.getValue().x, node.getValue().y));

                    array.add(cur);
                }
            }

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectXY.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(array, xmlWriter);

            stream = new FileOutputStream(fileXY);
            stream.write(xmlWriter.toString().getBytes());
        } finally {
            if (stream != null)
                try {
                    stream.close();
                } catch (Exception e) {
                }
        }
    }

    private void createIndex() throws IOException {
        checkFileGraph();
        new IndexElements(dirIndex.getAbsolutePath(), getFileGraph().getAbsolutePath()).run();
    }

    public ProjectManager(File dirPath) throws IOException, XMLStreamException, JAXBException {
        dir = dirPath;
        if (!dir.exists()) throw new IOException("Project directory " + dir + " does not exist");
        if (!dir.isDirectory())
            throw new IOException("Project directory name " + dir + " exists but is not a directory");

        fileProject = new File(dir, PROJECT_FILE);
        if (!fileProject.exists()) throw new IOException("Project file " + fileProject + " does not exist");
        if (!fileProject.isFile())
            throw new IOException("Project file name " + fileProject + " exists but is not a file");

        properties = new Properties();
        try (FileInputStream stream = new FileInputStream(fileProject)) {
            properties.loadFromXML(stream);
        }

        name = properties.getProperty(PROJECT_NAME);
        checkFileGraph();

        this.predicates = new Properties();
        this.filePredicates = new File(dir, PREDICATES_FILE);
        if (filePredicates.exists()) {
            try (FileInputStream stream = new FileInputStream(filePredicates)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream, "UTF-8");

                JAXBContext context = JAXBContext.newInstance(ProjectPredicates.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                ProjectPredicates array = (ProjectPredicates) unmarshaller.unmarshal(xmlReader);
                for (ProjectPredicate predicate : array) {
                    String name = null;
                    Properties cur = new Properties();
                    for (MapElements val : predicate)
                        if (val.key.equals(PREDICATE_NAME))
                            name = val.value;
                        else
                            cur.setProperty(val.key, val.value);
                    predicates.put(name, cur);
                }
            }
        }

        this.queries = new ArrayList<>();
        this.fileQueries = new File(dir, QUERIES_FILE);
        if (fileQueries.exists()) {
            try (FileInputStream stream = new FileInputStream(fileQueries)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream, "UTF-8");

                JAXBContext context = JAXBContext.newInstance(ProjectQueries.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                queries = (ProjectQueries) unmarshaller.unmarshal(xmlReader);
            }
        }

        this.projectXY = new HashMap<>();
        this.fileXY = new File(dir, XY_FILE);
        if (fileXY.exists()) {
            try (FileInputStream stream = new FileInputStream(fileXY)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream, "UTF-8");

                JAXBContext context = JAXBContext.newInstance(ProjectXY.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                ProjectXY array = (ProjectXY) unmarshaller.unmarshal(xmlReader);
                for (GraphXY graph : array) {
                    HashMap<String, Point> cur = new HashMap<String, Point>();
                    for (NodeXY xy : graph.getCoordinates())
                        cur.put(xy.node, new Point(Integer.parseInt(xy.x), Integer.parseInt(xy.y)));

                    projectXY.put(graph.name, cur);
                }
            }
        }

        this.dirIndex = new File(dir, INDEX_DIR);
        if (!dirIndex.exists())
            createIndex();

        checkFileKeywords();
    }

    public ProjectManager(File dir, String name, File graphFile) throws Exception {
        this.dir = dir;
        this.fileProject = new File(dir, PROJECT_FILE);
        this.filePredicates = new File(dir, PREDICATES_FILE);
        this.fileQueries = new File(dir, QUERIES_FILE);
        this.fileXY = new File(dir, XY_FILE);
        this.dirIndex = new File(dir, INDEX_DIR);
        this.fileKeywords = new File(dir, KEYWORDS_FILE);
        this.name = name;
        this.properties = new Properties();
        this.predicates = new Properties();
        this.queries = new ArrayList<String>();
        this.projectXY = new HashMap<String, HashMap<String, Point>>();
        properties.setProperty(PROJECT_NAME, name);
        properties.setProperty(GRAPH_FILE, graphFile.getName());
        properties.setProperty(CLUSTER_THRESHOLD, Double.toString(0.2));
        properties.setProperty(CLUSTER_EXPANSION, Double.toString(0.1));
        properties.setProperty(CLUSTER_DONE, Boolean.toString(false));
        clearPredicates();
        save();
        createIndex();
        checkFileKeywords();
    }

    @XmlRootElement(name = "predicates")
    private static class ProjectPredicates extends ArrayList<ProjectPredicate> {
        @XmlElement(name = "predicate")
        public ArrayList<ProjectPredicate> getElement() {
            return this;
        }
    }

    @XmlType(name = "predicate")
    private static class ProjectPredicate extends ArrayList<MapElements> {
        @XmlElement(name = "entry")
        public ArrayList<MapElements> getElement() {
            return this;
        }
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class MapElements {
        @XmlAttribute(name = "key")
        public String key;

        @XmlValue
        public String value;

        @SuppressWarnings("unused")
        private MapElements() {
        } // Required by JAXB

        public MapElements(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }


    @XmlRootElement(name = "queries")
    private static class ProjectQueries extends ArrayList<String> {
        @XmlElement(name = "query")
        public ArrayList<String> getElement() {
            return this;
        }

        public ProjectQueries() {
        }

        public ProjectQueries(Collection<String> list) {
            super(list);
        }
    }

    @XmlRootElement(name = "project_xy")
    private static class ProjectXY extends ArrayList<GraphXY> {
        @XmlElement(name = "graph")
        public ArrayList<GraphXY> getGraph() {
            return this;
        }
    }

    @XmlType(name = "graph")
    private static class GraphXY {
        @XmlAttribute(name = "name")
        public String name;

        @XmlTransient
        public ArrayList<NodeXY> coordinates;

        @XmlElement(name = "node")
        public ArrayList<NodeXY> getCoordinates() {
            return coordinates;
        }

        public GraphXY() {
            coordinates = new ArrayList<NodeXY>();
        }
    }

    @XmlType
    @XmlAccessorType(XmlAccessType.FIELD)
    private static class NodeXY {
        @XmlAttribute(name = "name")
        public String node;

        @XmlAttribute(name = "x")
        public String x;

        @XmlAttribute(name = "y")
        public String y;

        @SuppressWarnings("unused")
        private NodeXY() {
        } // Required by JAXB

        public NodeXY(String node, int x, int y) {
            this.node = node;
            this.x = Integer.toString(x);
            this.y = Integer.toString(y);
        }
    }
}
