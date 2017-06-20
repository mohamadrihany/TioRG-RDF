package fr.views;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import fr.clustering.MCODEVertexWeitingWeightedGraph;
import fr.clustering.MCODEVertexWeitingWeightedGraph.MyLink;
import fr.preprocessing.GraphCreation;
import fr.processings.FileToModelGraph;
import fr.processings.GetGraphInfo;
import fr.processings.RealClustersConstruction;
import fr.processings.SharedFeature.PairSourceSet;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

/**
 * The main window of the TioRG application.
 *
 * @author houk
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {
    private static final Logger LOGGER = Logger.getLogger(MainWindow.class.getName());

    private static final String MAIN_WINDOW_TITLE = "RDF Graphs Exploration";
    private static final JFileChooser FILE_CHOOSER = new JFileChooser(new File(".").getAbsolutePath());

    private MDIDesktopPane desktop;

    public ArrayList<String> initialGraphEages = new ArrayList<String>();
    public DirectedGraph<RDFNode, Statement> initialGraph = new DirectedSparseGraph<>();
    public Graph<RDFNode, Statement> dataGraph = new DirectedSparseGraph<>();
    final ScalingControl scaler = new CrossoverScalingControl();
    private ArrayList<Graph> finalClustersListwithoutLiterals;
    private String filePath;
    private FirstGraphVisualization mainFrame;
    private DefaultModalGraphMouse<RDFNode, Statement> vv2;
    private DirectedSparseGraph<RDFNode, MyLink> weightedGraphToClusteingWithoutLitterals;
    private Graph<RDFNode, Statement> graphToClustering;
    private ArrayList<PairSourceSet> listResourceRDFNodes;

    private ProjectManager projectManager = null;

    private boolean fullGraph = PreprocessingFrame.fullGraph;
    private ArrayList<Graph> finalClustersList = new ArrayList<Graph>();
    private Collection<RDFNode> vertexToSee = new ArrayList<RDFNode>();
    private int view = 0;

    public MainWindow() {
        setTitle(MAIN_WINDOW_TITLE);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        desktop = new MDIDesktopPane();
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(desktop);

        Container contentPane = getContentPane();

        contentPane.setLayout(new BorderLayout());
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        contentPane.setPreferredSize(screenSize);
        contentPane.add(scrollPane, BorderLayout.CENTER);

        JMenuBar menuBar = new MainMenuBar(desktop);
        setJMenuBar(menuBar);

        pack();
    }

    MDIDesktopPane getDesktop() {
        return desktop;
    }

    void newProject(String projectLocation, String projectName, String graphLocation, boolean hasToMove) throws PropertyVetoException, ClassNotFoundException, JAXBException, IOException {
        Path parentDirectory = FileSystems.getDefault().getPath(projectLocation);
        if (!Files.exists(parentDirectory) || !Files.isDirectory(parentDirectory)) {
            String msg = "Project location " + parentDirectory + " does not exist or is not a directory";
            LOGGER.log(Level.SEVERE, msg);
            throw new IOException(msg);
        }

        try {
            Path projectDirectory = Files.createDirectory(parentDirectory.resolve(projectName));

            Path graphSourceFile = FileSystems.getDefault().getPath(graphLocation);
            Path graphDestinationFile = projectDirectory.resolve(graphSourceFile.getFileName());

            if (hasToMove) {
                Files.move(graphSourceFile, graphDestinationFile, REPLACE_EXISTING);
            } else {
                Files.copy(graphSourceFile, graphDestinationFile, REPLACE_EXISTING);
            }

            projectManager = new ProjectManager(projectDirectory.toFile(), projectName, graphDestinationFile.toFile());
            initGraph();
        } catch (IOException ex) {
            String msg = "Unable to create project directory " + parentDirectory.resolve(projectName);
            LOGGER.log(Level.SEVERE, msg, ex);
            throw new IOException(msg, ex);
        }
    }

    void openProject(File projectDirectory) throws JAXBException, XMLStreamException, IOException, PropertyVetoException, ClassNotFoundException {
        projectManager = new ProjectManager(projectDirectory);
        initGraph();
    }

    private void initGraph() throws IOException, PropertyVetoException, ClassNotFoundException {
        if (projectManager != null) {
            initialGraph = projectManager.loadGraph();
            filePath = projectManager.getGraphFile().getAbsolutePath();
            initialGraphEages = GetGraphInfo.GetPredicatesList(initialGraph);
            dataGraph = GraphCreation.GraphCreation(initialGraph, filePath, false, false);

            setTitle(MAIN_WINDOW_TITLE + " : " + projectManager.getProjectName());
            mainFrame = new FirstGraphVisualization(initialGraph, projectManager.getProperty(ProjectManager.GRAPH_FILENAME), false, projectManager.getXY());
            mainFrame.setVisible(true);
            mainFrame.setClosable(false);
            desktop.add(mainFrame);

            //exportMenuItem.setEnabled(false);

            if (projectManager.getClusterDone()) runCluster();

            mainFrame.setSelected(true);
            mainFrame.setMaximum(true);
        }
    }

    public void saveProject() {
        if (projectManager == null) return;
        try {
            projectManager.addXY(mainFrame.graphVisualization.getGraphCoordinates());
            for (JInternalFrame frame : desktop.getAllFrames()) {
                if (frame != mainFrame) {
                    if (frame.getName().equals(ClustersVizualisation.NAME))
                        projectManager.setClusterDone(true);
                    else if (frame.getName().equals(ClustersVizualisation.NAME_THEME)) {
                        String title = frame.getTitle();
                        title = title.replaceFirst(ClustersVizualisation.TITLE_THEME + " ?[:] ?[#][0-9]+[ ]?", "");
                        projectManager.addXY(title, ((FirstGraphVisualization) frame).graphVisualization.getGraphCoordinates());
                    }
                }
            }
            projectManager.save();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Project saving error", JOptionPane.ERROR_MESSAGE);
        }
    }

    boolean closeCurrentProject() {
        if (projectManager != null) {
            int result = JOptionPane.showConfirmDialog(MainWindow.this, "Save the current project ?");
            if (result == -1 || result == JOptionPane.CANCEL_OPTION) return false;

            if (result == JOptionPane.OK_OPTION) {
                try {
                    projectManager.save();
                } catch (IOException | JAXBException ex) {
                    LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error while saving project", JOptionPane.ERROR_MESSAGE);
                }
            }

            projectManager = null;

            for (JInternalFrame frame : desktop.getAllFrames()) {
                frame.hide();
                frame.dispose();
            }

            setTitle(MAIN_WINDOW_TITLE);
        }
        return true;
    }

    public void exportProject() {
        try {
            if (projectManager != null) {
                JInternalFrame frame = desktop.getSelectedFrame();
                if (frame.getName().equals(ClustersVizualisation.NAME_THEME)) {
                    String name = frame.getTitle().replace(ClustersVizualisation.TITLE_THEME + " : ", "");

                    FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    FILE_CHOOSER.setSelectedFile(new File(name + ".owl"));
                    int returnVal = FILE_CHOOSER.showDialog(MainWindow.this, "Save");
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        FileToModelGraph.WriteOwlFile(
                                ((FirstGraphVisualization) frame).graph, FILE_CHOOSER.getSelectedFile().getAbsolutePath()
                        );
                    }
                }
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Project closing error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setClusteringParameters() {
        try {
            PreprocessingFrame frame = new PreprocessingFrame(
                    initialGraph, projectManager.getPredicates(), projectManager.getQueries(),
                    String.valueOf(projectManager.getClusterThreshold()),
                    String.valueOf(projectManager.getClusterExpansion())
            );
            frame.setModal(true);
            frame.setVisible(true);
            if (frame.getDialogResult() == JOptionPane.OK_OPTION) {
                projectManager.clearPredicates();

                for (Properties predicate : frame.getPredicates())
                    projectManager.addPredicate(predicate);

                projectManager.clearQueries();
                projectManager.getQueries().addAll(frame.getQueries());

                projectManager.setProperty(ProjectManager.CLUSTER_THRESHOLD, Double.toString(frame.getPoids()));
                projectManager.setProperty(ProjectManager.CLUSTER_EXPANSION, Double.toString(frame.getOptimisations()));
            }
            frame.dispose();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }

    }

    private void lancerClusteringMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            ArrayList<JInternalFrame> list = desktop.getFramesLikeName(ClustersVizualisation.NAME);
            if (list != null) {
                list.get(0).setSelected(true);

                int result = JOptionPane.showConfirmDialog(this, "Clustering is already executed.\nWould you like to close current and start a new clustering ?", "Start the Clustering", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION)
                    return;

                for (JInternalFrame frame : list) {
                    desktop.remove(frame);
                    frame.dispose();
                }

                list = desktop.getFramesLikeName(ClustersVizualisation.NAME_THEME);
                if (list != null)
                    for (JInternalFrame frame : list) {
                        desktop.remove(frame);
                        frame.dispose();
                    }

                desktop.revalidate();
                desktop.repaint();

                projectManager.setClusterDone(false);
            }

            runCluster();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lancerClusteringMenuItemActionPerformed

    public void startClustering() {
        try {
            ArrayList<JInternalFrame> list = desktop.getFramesLikeName(ClustersVizualisation.NAME);
            if (list != null) {
                list.get(0).setSelected(true);

                int result = JOptionPane.showConfirmDialog(this, "Clustering is already executed.\nWould you like to close current and start a new clustering ?", "Start the Clustering", JOptionPane.YES_NO_OPTION);
                if (result == JOptionPane.NO_OPTION)
                    return;

                for (JInternalFrame frame : list) {
                    desktop.remove(frame);
                    frame.dispose();
                }

                list = desktop.getFramesLikeName(ClustersVizualisation.NAME_THEME);
                if (list != null)
                    for (JInternalFrame frame : list) {
                        desktop.remove(frame);
                        frame.dispose();
                    }

                desktop.revalidate();
                desktop.repaint();

                projectManager.setClusterDone(false);
            }

            runCluster();
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void runCluster() throws IOException, ClassNotFoundException, PropertyVetoException {
        fullGraph = PreprocessingFrame.fullGraph; // boolean to know if the user want to use only instancies or all the graph
        graphToClustering = GraphCreation.GraphCreation(initialGraph, filePath, false, fullGraph); // graph after choosing datagraph or full graph (it can be initialGraph or dataGraph)
        weightedGraphToClusteingWithoutLitterals = GraphCreation.main(graphToClustering, projectManager.getPredicates(), projectManager.getQueries()).getGraph(); // the graph to clustering with all modifications specified in the semantique caracteristics
        listResourceRDFNodes = GraphCreation.main(graphToClustering, projectManager.getPredicates(), projectManager.getQueries()).getSet(); //la liste qui va nous permettre l'affichage sur le graphe initial (vertex that was regrouped)

        // systematic parameters (clustering parameters )
        MCODEVertexWeitingWeightedGraph mcodevertexweitingweightedgraph = new MCODEVertexWeitingWeightedGraph();
        finalClustersListwithoutLiterals = mcodevertexweitingweightedgraph.VertexWeiting(weightedGraphToClusteingWithoutLitterals, projectManager.getClusterThreshold(), projectManager.getClusterExpansion(), desktop); //the list of clusters with merged nodes
        finalClustersList = RealClustersConstruction.main(initialGraph, finalClustersListwithoutLiterals, listResourceRDFNodes); //add litterals to clusters and construct clusters with real vertex (initial vertex)
        SortGraphList(finalClustersList); // Sort clusters according to their size

        ClustersVizualisation frameInt = new ClustersVizualisation(initialGraph, projectManager, finalClustersList, desktop,
                ClustersVizualisation.TITLE + " for \"" + projectManager.getProjectName() + "\""
        );
        frameInt.setVisible(true);
        desktop.add(frameInt);
        frameInt.setMaximum(true);
        frameInt.setSelected(true);
    }

    /**
     * Le recherche mots clés
     *
     * @param evt
     */
    private void ToolsMenuActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (Desktop.isDesktopSupported()) {
                try {
                    //Desktop.getDesktop().edit( projectManager.getKeywordsFile() );
                    ProcessBuilder pb = new ProcessBuilder("notepad", projectManager.getKeywordsFile().getAbsolutePath());
                    pb.start();
                } catch (Exception e) {
                }
            } else {
                JOptionPane.showMessageDialog(MainWindow.this, "Edit file : " + projectManager.getKeywordsFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ToolsMenuActionPerformed

    public void setSearchOptions() {
        try {
            if (Desktop.isDesktopSupported()) {
                try {
                    //Desktop.getDesktop().edit( projectManager.getKeywordsFile() );
                    ProcessBuilder pb = new ProcessBuilder("notepad", projectManager.getKeywordsFile().getAbsolutePath());
                    pb.start();
                } catch (Exception e) {
                }
            } else {
                JOptionPane.showMessageDialog(MainWindow.this, "Edit file : " + projectManager.getKeywordsFile().getAbsolutePath());
            }
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    private void KeywordSearchMenuItemActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            new KeywordsSearch2(projectManager.getIndexDirectory().getAbsolutePath(), projectManager.getXY(), desktop, projectManager.getKeywordsOptions()).setVisible(true);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    public void search() {
        try {
            new KeywordsSearch2(projectManager.getIndexDirectory().getAbsolutePath(), projectManager.getXY(), desktop, projectManager.getKeywordsOptions()).setVisible(true);
        } catch (Exception ex) {
            LOGGER.log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Orders the graph list by size
     *
     * @param clusterList the graph list to order by size
     */
    private void SortGraphList(ArrayList<Graph> clusterList) {
        SortTupleNodeWeightComparator com = new SortTupleNodeWeightComparator();
        Collections.sort(clusterList, com);
    }

    public class SortTupleNodeWeightComparator implements Comparator<Graph> {
        @Override
        public int compare(Graph o1, Graph o2) {
            int datarate1 = o1.getVertexCount();
            int datarate2 = o2.getVertexCount();

            if (datarate1 < datarate2) {
                return -1;
            } else if (datarate1 > datarate2) {
                return +1;
            } else {
                return 0;
            }
        }
    }

    boolean isExportMenuActivated() {
        return desktop.getSelectedFrame() != null
                && desktop.getSelectedFrame() != mainFrame
                && !desktop.getSelectedFrame().getName().equals(ClustersVizualisation.NAME);
    }
}
