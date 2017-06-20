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
    private static final String PROJECT_NAME = "tiorg-project-projectName";

    private static final String PROJECT_FILENAME = "project.xml";
    private static final String INDEX_DIRECTORY = "index";
    public static final String GRAPH_FILENAME = "graph-file";
    private static final String PREDICATES_FILENAME = "predicates.xml";
    private static final String QUERIES_FILENAME = "queries.xml";
    private static final String XY_FILENAME = "project_xy.xml";
    private static final String KEYWORDS_FILENAME = "keywords_search.xml";

    public static final String CLUSTER_THRESHOLD = "cluster-threshold";
    public static final String CLUSTER_EXPANSION = "cluster-expansion";
    public static final String CLUSTER_DONE = "cluster-done";

    public static final String PREDICATE_NAME = "name";
    public static final String PREDICATE_CRITERIA = "criteria";
    public static final String PREDICATE_VALUE = "value";


    private File projectDirectory;
    private File projectFile;
    private File indexDirectory;
    private File graphFile;
    private File predicatesFile;
    private File queriesFile;
    private File xyFile;
    private File keywordsFile;

    private String projectName;
    private Properties properties;
    private Properties predicates;
    private ArrayList<String> queries;
    private HashMap<String, HashMap<String, Point>> projectXY;

    public ProjectManager(File projectDirectory, String name, File graphFile) throws IOException, JAXBException {
        this.projectDirectory = projectDirectory;
        this.projectFile = new File(projectDirectory, PROJECT_FILENAME);
        this.indexDirectory = new File(projectDirectory, INDEX_DIRECTORY);
        this.predicatesFile = new File(projectDirectory, PREDICATES_FILENAME);
        this.queriesFile = new File(projectDirectory, QUERIES_FILENAME);
        this.xyFile = new File(projectDirectory, XY_FILENAME);
        this.keywordsFile = new File(projectDirectory, KEYWORDS_FILENAME);

        this.projectName = name;
        this.predicates = new Properties();
        this.queries = new ArrayList<String>();
        this.projectXY = new HashMap<String, HashMap<String, Point>>();

        this.properties = new Properties();
        properties.setProperty(PROJECT_NAME, name);
        properties.setProperty(GRAPH_FILENAME, graphFile.getName());
        properties.setProperty(CLUSTER_THRESHOLD, Double.toString(0.2));
        properties.setProperty(CLUSTER_EXPANSION, Double.toString(0.1));
        properties.setProperty(CLUSTER_DONE, Boolean.toString(false));

        clearPredicates();
        save();
        createIndex();
        checkFileKeywords();
    }

    public void clearPredicates() {
        predicates.clear();
    }

    public void save() throws IOException, JAXBException {
        try (FileOutputStream stream = new FileOutputStream(projectFile)) {
            properties.storeToXML(stream, null, "UTF-8");
        }

        try (FileOutputStream stream = new FileOutputStream(predicatesFile)) {
            ProjectPredicates predicateList = new ProjectPredicates();
            for (Map.Entry<Object, Object> predicate : predicates.entrySet()) {
                ProjectPredicate currentPredicate = new ProjectPredicate();
                currentPredicate.add(new MapElements(PREDICATE_NAME, predicate.getKey().toString()));
                for (Map.Entry<Object, Object> val : ((Properties) predicate.getValue()).entrySet())
                    currentPredicate.add(new MapElements(val.getKey().toString(), val.getValue().toString()));
                predicateList.add(currentPredicate);
            } //TODO why converting predicates before storing in file ?

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectPredicates.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(predicateList, xmlWriter);

            stream.write(xmlWriter.toString().getBytes());
        }

        try (FileOutputStream stream = new FileOutputStream(queriesFile)) {
            ProjectQueries array = new ProjectQueries(queries);

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectQueries.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(array, xmlWriter);

            stream.write(xmlWriter.toString().getBytes());
        }

        try (FileOutputStream stream = new FileOutputStream(xyFile)) {
            ProjectXY array = new ProjectXY();
            for (Map.Entry<String, HashMap<String, Point>> graph : projectXY.entrySet()) {
                GraphXY cur = new GraphXY();
                cur.coordinates = new ArrayList<NodeXY>();

                cur.name = graph.getKey();
                for (Map.Entry<String, Point> node : graph.getValue().entrySet())
                    cur.coordinates.add(new NodeXY(node.getKey(), node.getValue().x, node.getValue().y));

                array.add(cur);
            }

            StringWriter xmlWriter = new StringWriter();
            JAXBContext context = JAXBContext.newInstance(ProjectXY.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.marshal(array, xmlWriter);

            stream.write(xmlWriter.toString().getBytes());
        }
    }

    private void createIndex() throws IOException {
        checkGraphFile();
        new IndexElements(indexDirectory.getAbsolutePath(), getGraphFile().getAbsolutePath()).run();
    }

    private void checkGraphFile() throws IOException {
        if (graphFile == null) {
            graphFile = new File(projectDirectory, properties.getProperty(GRAPH_FILENAME));
            if (!graphFile.isFile() || !graphFile.exists())
                throw new IOException("Graph location error");
        }
    }

    private void checkFileKeywords() throws IOException {
        if (keywordsFile == null)
            keywordsFile = new File(projectDirectory, KEYWORDS_FILENAME);
        if (!keywordsFile.exists()) {
            InputStream input = null;
            OutputStream output = null;
            try {
                input = getClass().getResourceAsStream("/keywords_search.xml");
                output = new FileOutputStream(keywordsFile);
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

    public File getProjectDirectory() {
        return projectDirectory;
    }

    public File getGraphFile() {
        return graphFile;
    }

    public String getProjectName() {
        return projectName;
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

    public void setProperty(String property, String value) {
        properties.setProperty(property, value);
    }

    public String getProperty(String property) {
        return properties.getProperty(property);
    }

    public DirectedGraph<RDFNode, Statement> loadGraph() throws IOException {
        checkGraphFile();
        return FileToModelGraph.FileToGraph(graphFile.getAbsolutePath());
    }

    public File getIndexDirectory() {
        return indexDirectory;
    }

    public File getKeywordsFile() {
        return keywordsFile;
    }

    public Element getKeywordsOptions() {
        if (keywordsFile.exists()) {
            try {
                return ReadXML.main(keywordsFile.getAbsolutePath());
            } catch (Exception e) {

            }
        }
        return null;
    }

    public ProjectManager(File dirPath) throws IOException, XMLStreamException, JAXBException {
        projectDirectory = dirPath;
        if (!projectDirectory.exists()) throw new IOException("Project directory " + projectDirectory + " does not exist");
        if (!projectDirectory.isDirectory())
            throw new IOException("Project directory projectName " + projectDirectory + " exists but is not a directory");

        projectFile = new File(projectDirectory, PROJECT_FILENAME);
        if (!projectFile.exists()) throw new IOException("Project file " + projectFile + " does not exist");
        if (!projectFile.isFile())
            throw new IOException("Project file projectName " + projectFile + " exists but is not a file");

        properties = new Properties();
        try (FileInputStream stream = new FileInputStream(projectFile)) {
            properties.loadFromXML(stream);
        }

        projectName = properties.getProperty(PROJECT_NAME);
        checkGraphFile();

        this.predicates = new Properties();
        this.predicatesFile = new File(projectDirectory, PREDICATES_FILENAME);
        if (predicatesFile.exists()) {
            try (FileInputStream stream = new FileInputStream(predicatesFile)) {
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
        this.queriesFile = new File(projectDirectory, QUERIES_FILENAME);
        if (queriesFile.exists()) {
            try (FileInputStream stream = new FileInputStream(queriesFile)) {
                XMLStreamReader xmlReader = XMLInputFactory.newInstance().createXMLStreamReader(stream, "UTF-8");

                JAXBContext context = JAXBContext.newInstance(ProjectQueries.class);
                Unmarshaller unmarshaller = context.createUnmarshaller();
                queries = (ProjectQueries) unmarshaller.unmarshal(xmlReader);
            }
        }

        this.projectXY = new HashMap<>();
        this.xyFile = new File(projectDirectory, XY_FILENAME);
        if (xyFile.exists()) {
            try (FileInputStream stream = new FileInputStream(xyFile)) {
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

        this.indexDirectory = new File(projectDirectory, INDEX_DIRECTORY);
        if (!indexDirectory.exists())
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
