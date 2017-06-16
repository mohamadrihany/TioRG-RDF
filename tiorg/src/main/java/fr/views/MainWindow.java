package fr.views;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import fr.clustering.MCODEVertexWeitingWeightedGraph;
import fr.clustering.MCODEVertexWeitingWeightedGraph.MyLink;
import fr.preprocessing.GraphCreation;
import fr.processings.FileToModelGraph;
import fr.processings.GetGraphInfo;
import fr.processings.RealClustersConstruction;
import fr.processings.SharedFeature.PairSourceSet;

/**
 * The principal window
 *
 * @author houk
 */
@SuppressWarnings("serial")
public class MainWindow extends JFrame {

	private static String TITLE = "RDF Graphs Exploration";
	
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
    private JFileChooser fc = null;
    public static int proc;

    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenu ClusteringMenu;
    private javax.swing.JMenu FileMenu;
    private javax.swing.JMenu SearchMenu;
    private javax.swing.JMenuItem KeywordSearchMenuItem;
    private javax.swing.JMenuItem KeywordOptionMenuItem;
    private javax.swing.JMenu VisualizationMenu;
    private javax.swing.JMenuItem enregistrerMenuItem;
    public static MDIDesktopPane desktop;
    
    private javax.swing.JMenuItem lancerClusteringMenuItem;
    public static javax.swing.JCheckBoxMenuItem modeMenuItem;
    private javax.swing.JMenuItem nouveauMenuItem;
    private javax.swing.JMenuItem ouvrirMenuItem;
    private javax.swing.JMenuItem paramClassiqueMenuItem;
    private javax.swing.JMenuItem paramSemantiqueMenuItem;
    private javax.swing.JMenuItem saveMenuItem;
    private javax.swing.JMenuItem closeMenuItem;
    private javax.swing.JMenuItem exportMenuItem;
    private JScrollPane scrollPane;

    private ProjectManager manager = null;
    
    private boolean graphwithoutLiterals = true;
    private boolean first = true;
    private boolean fullGraph = PreprocessingFrame.fullGraph;
    private ArrayList<Graph> finalClustersList = new ArrayList<Graph>();
    private Collection<RDFNode> vertexToSee = new ArrayList<RDFNode>();
    private int view = 0;
    
    public static boolean weightedGraph = false;

    public MainWindow() {
        proc = 0;
        initComponents();
        setLocationRelativeTo(null);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screenSize.width, screenSize.height - 45);

    }
    
    public static MDIDesktopPane getDesktop()
	{
		return desktop;
	}
    
    private JFileChooser getFileChooser()
	{
		if(fc == null)
			fc = new JFileChooser( new File(".").getAbsolutePath() );
		return fc;
	}

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        desktop = new MDIDesktopPane();
        /*desktop.addContainerListener(
    		new ContainerListener()
			{
				public void componentRemoved(ContainerEvent e)
				{
				}
				public void componentAdded(ContainerEvent e)
				{
					if(e.getChild() instanceof JInternalFrame)
					{
						if(e.getChild().getName() != null && e.getChild().getName().equals(ClustersVizualisation.NAME_THEME))
							e.getChild().addPropertyChangeListener(
                        		new PropertyChangeListener()
								{
									public void propertyChange(PropertyChangeEvent evt)
									{
										
									}
								}
	                        );
					}
				}
			}
    	);*/
        scrollPane = new JScrollPane();
        menuBar = new javax.swing.JMenuBar();
        FileMenu = new javax.swing.JMenu();
        FileMenu.addMenuListener(
        	new MenuListener() 
	        {
	        	public void menuCanceled(MenuEvent arg0) 
	        	{
	        	}
	        	public void menuDeselected(MenuEvent arg0) 
	        	{
	        	}
	        	public void menuSelected(MenuEvent arg0) 
	        	{
	        		if(desktop.getSelectedFrame() != null
							&& desktop.getSelectedFrame() != mainFrame
							&& !desktop.getSelectedFrame().getName().equals(ClustersVizualisation.NAME)
							)
							exportMenuItem.setEnabled(true);
						else
							exportMenuItem.setEnabled(false);
	        	}
	        }
        );
        FileMenu.addComponentListener(
        	new ComponentAdapter() 
        	{
	        	public void componentShown(ComponentEvent arg0) 
	        	{
	        		if(desktop.getSelectedFrame() != null
							&& desktop.getSelectedFrame() != mainFrame
							&& !desktop.getSelectedFrame().getName().equals(ClustersVizualisation.NAME)
							)
							exportMenuItem.setEnabled(true);
						else
							exportMenuItem.setEnabled(false);
	        	}
	        }
        );
        nouveauMenuItem = new javax.swing.JMenuItem();
        ouvrirMenuItem = new javax.swing.JMenuItem();
        enregistrerMenuItem = new javax.swing.JMenuItem();
        closeMenuItem = new javax.swing.JMenuItem();
        exportMenuItem = new javax.swing.JMenuItem();
        ClusteringMenu = new javax.swing.JMenu();
        paramClassiqueMenuItem = new javax.swing.JMenuItem();
        paramSemantiqueMenuItem = new javax.swing.JMenuItem();
        lancerClusteringMenuItem = new javax.swing.JMenuItem();
        VisualizationMenu = new javax.swing.JMenu();
        modeMenuItem = new javax.swing.JCheckBoxMenuItem();
        saveMenuItem = new javax.swing.JMenuItem();
        SearchMenu = new javax.swing.JMenu();
        KeywordSearchMenuItem = new javax.swing.JMenuItem();
        KeywordOptionMenuItem = new javax.swing.JMenuItem();
        scrollPane.setViewportView(desktop);
        getContentPane().setLayout(new BorderLayout());
		getContentPane().add(scrollPane, BorderLayout.CENTER);

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle(TITLE);

        FileMenu.setText("File");
        FileMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FileMenuActionPerformed(evt);
            }
        });

        nouveauMenuItem.setText("New Project");
        nouveauMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nouveauMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(nouveauMenuItem);

        ouvrirMenuItem.setText("Open Project");
        ouvrirMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ouvrirMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(ouvrirMenuItem);
        
        closeMenuItem.setText("Close Project");
        closeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(closeMenuItem);
        
        FileMenu.addSeparator();
        
        enregistrerMenuItem.setText("Save Project");
        enregistrerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                enregistrerMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(enregistrerMenuItem);
        
        FileMenu.addSeparator();
        
        exportMenuItem.setText("Export Graph");
        exportMenuItem.setEnabled(false);
        exportMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exportMenuItemActionPerformed(evt);
            }
        });
        FileMenu.add(exportMenuItem);

        menuBar.add(FileMenu);

        ClusteringMenu.setText("Clustering");
/*
        paramClassiqueMenuItem.setText("Clustering Parameters");
        paramClassiqueMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paramClassiqueMenuItemActionPerformed(evt);
            }
        });
        ClusteringMenu.add(paramClassiqueMenuItem);
*/
        paramSemantiqueMenuItem.setText("Clustering Parameters");//setText("Semantic Criteria");
        paramSemantiqueMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                paramSemantiqueMenuItemActionPerformed(evt);
            }
        });
        ClusteringMenu.add(paramSemantiqueMenuItem);

        lancerClusteringMenuItem.setText("Start the Clustering");
        lancerClusteringMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lancerClusteringMenuItemActionPerformed(evt);
            }
        });
        ClusteringMenu.add(lancerClusteringMenuItem);

        menuBar.add(ClusteringMenu);

        VisualizationMenu.setText("Visualization");

        modeMenuItem.setText("Editing Mode");
        modeMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                modeMenuItemActionPerformed(evt);
            }
        });
        VisualizationMenu.add(modeMenuItem);

        //menuBar.add(VisualizationMenu);

        SearchMenu.setText("Search");
        KeywordSearchMenuItem.setText("Keywords Search");
        KeywordSearchMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                KeywordSearchMenuItemActionPerformed(evt);
            }
        });
        SearchMenu.add(KeywordSearchMenuItem);
        
        SearchMenu.addSeparator();
        
        KeywordOptionMenuItem.setText("Keywords Options");
        KeywordOptionMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ToolsMenuActionPerformed(evt);
            }
        });
        SearchMenu.add(KeywordOptionMenuItem);
        
        menuBar.add(SearchMenu);

        
        menuBar.add(new WindowMenu(desktop));
		setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktop, javax.swing.GroupLayout.DEFAULT_SIZE, 1093, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(desktop, javax.swing.GroupLayout.DEFAULT_SIZE, 709, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Permet d'ouvrir le menu file
     *
     * @param evt
     */
    private void FileMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FileMenuActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_FileMenuActionPerformed
    /**
     * Permet d'importer un graphe owl
     *
     * @param evt
     */
    
    private boolean closeCurrentProject() throws Exception
    {
    	if(manager != null)
    	{
    		int result = JOptionPane.showConfirmDialog(MainWindow.this, "Save the current project ?");
    		if(result == -1 || result == JOptionPane.CANCEL_OPTION)
    			return false;
    		
    		if(result == JOptionPane.OK_OPTION)
    			manager.save();
    		
    		manager = null;
    		
    		for(JInternalFrame frame : desktop.getAllFrames())
    		{
    			frame.hide();
    			frame.dispose();
    		}
    		
    		setTitle(TITLE);
    	}
    	return true;
    }
    
    private void nouveauMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nouveauMenuItemActionPerformed
        try 
        {
        	if( !closeCurrentProject() )
        		return;
        	
        	CreateProject chooser = new CreateProject();
        	chooser.setVisible(true);
        	if(chooser.getDialogResult() == JOptionPane.OK_OPTION)
        	{
        		File dir = new File( chooser.getProjectLocation() );
        		String name = chooser.getProjectName();
        		dir = new File(dir, name);
        		if( !dir.mkdir() )
        			throw new IOException("Project location error");
        		File fileSrc = new File( chooser.getGraphLocation() );
        		File fileDst = new File(dir, fileSrc.getName());
        		Files.copy(fileSrc.toPath(), fileDst.toPath(), StandardCopyOption.REPLACE_EXISTING);
        		if(fileDst.exists() && chooser.getMoveGraph())
        			fileSrc.delete();
        		
        		manager = new ProjectManager(dir, chooser.getProjectName(), fileDst);
        		
        		initGraph();
        	}
        	/*
            // To choose a new owl file 
            JFileChooser.setDefaultLocale(Locale.US);
            JFileChooser chooser = new JFileChooser();
            chooser.setCurrentDirectory(new File(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "Graphs" + File.separator));
            System.out.println("      " + new File(new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "Graphs" + File.separator).getAbsolutePath());

            int returnVal;
            returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                filePath = chooser.getSelectedFile().getCanonicalPath().toLowerCase().replace("\\", "/");
                //recuperate graph from the selected file
                initialGraph = FileToModelGraph.FileToGraph(filePath);

                //recuperate the statement of the selected graph
                initialGraphEages = GetGraphInfo.GetPredicatesList(initialGraph);

                //instances graph
                dataGraph = GraphCreation.GraphCreation(initialGraph, filePath, false, false);

                //visualize the initial graph

                mainFrame = new FirstGraphVisualization(initialGraph, "Initial Graphe", false);
                mainFrame.setVisible(true);
                desktop.add(mainFrame);

                try {
                    mainFrame.setSelected(true);
                } catch (PropertyVetoException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            */
        } 
        catch (Exception ex) 
        {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        	JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Project creation error", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_nouveauMenuItemActionPerformed
    
    private void initGraph() throws Exception
    {
    	if(manager != null)
    	{
    		//recuperate graph from the selected file
            initialGraph = manager.loadGraph();
            
            filePath = manager.getFileGraph().getAbsolutePath();
    		//recuperate the statement of the selected graph
            initialGraphEages = GetGraphInfo.GetPredicatesList(initialGraph);

            //instances graph
            dataGraph = GraphCreation.GraphCreation(initialGraph, filePath, false, false);

            //visualize the initial graph
            
            setTitle(TITLE+" : "+manager.getName());

            mainFrame = new FirstGraphVisualization(initialGraph, manager.getProperty(ProjectManager.GRAPH_FILE), false, manager.getXY());
            mainFrame.setVisible(true);
            mainFrame.setClosable(false);
            desktop.add(mainFrame);
            
            exportMenuItem.setEnabled(false);
            
            if( manager.getClusterDone() )
            	runCluster();

            try 
            {
                mainFrame.setSelected(true);
                mainFrame.setMaximum(true);
            } 
            catch (PropertyVetoException ex) 
            {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
    	}
    }
    /**
     * Pour enregistrer le projet en cours, pas encore implémenté
     *
     * @param evt
     */
    private void enregistrerMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	if(manager != null)
    		try
    		{
    			manager.addXY( mainFrame.graphVisualization.getGraphCoordinates() );
    			for(JInternalFrame frame : desktop.getAllFrames())
    			{
    				if(frame != mainFrame)
    				{
    					if(frame.getName().equals(ClustersVizualisation.NAME))
    						manager.setClusterDone(true);
    					else if(frame.getName().equals(ClustersVizualisation.NAME_THEME))
    					{
    						String title = frame.getTitle();
    						title = title.replaceFirst(ClustersVizualisation.TITLE_THEME+" ?[:] ?[#][0-9]+[ ]?", "");
    						manager.addXY(title, ((FirstGraphVisualization)frame).graphVisualization.getGraphCoordinates());
    					}
    				}
    			}
    			manager.save();
    		}
	    	catch (Exception ex) 
	        {
	            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	        	JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Project saving error", JOptionPane.ERROR_MESSAGE);
	        }
    }//GEN-LAST:event_enregistrerMenuItemActionPerformed
    
    private void closeMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
		try
		{
			if(manager != null)
				closeCurrentProject();
		}
    	catch (Exception ex) 
        {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        	JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Project closing error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_enregistrerMenuItemActionPerformed
    
    private void exportMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
		try
		{
			if(manager != null)
			{
				JInternalFrame frame = desktop.getSelectedFrame();
				if( frame.getName().equals(ClustersVizualisation.NAME_THEME) )
				{
					String name = frame.getTitle().replace(ClustersVizualisation.TITLE_THEME+" : ", "");
					
					getFileChooser().setFileSelectionMode(JFileChooser.FILES_ONLY);
					getFileChooser().setSelectedFile(new File(name+".owl"));
					int returnVal = fc.showDialog(MainWindow.this, "Save");
					if(returnVal == JFileChooser.APPROVE_OPTION)
					{
						FileToModelGraph.WriteOwlFile(
								((FirstGraphVisualization)frame).graph, getFileChooser().getSelectedFile().getAbsolutePath()
								);
					}
				}
			}
		}
    	catch (Exception ex) 
        {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        	JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Project closing error", JOptionPane.ERROR_MESSAGE);
        }
    }
    /**
     * Permet d'ouvrir une fenètre pour changer les paramètres de clustering
     *
     * @param evt
     */
    private void paramClassiqueMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paramClassiqueMenuItemActionPerformed
    	
    	ParametersFrame frame = new ParametersFrame(
    			String.valueOf( manager.getClusterThreshold() ), 
    			String.valueOf( manager.getClusterExpansion() ), 
    			initialGraph, desktop
    			);
    	frame.setModal(true);
    	frame.setVisible(true);
    	if(frame.getDialogResult() == JOptionPane.OK_OPTION)
    	{
	    	manager.setProperty(ProjectManager.CLUSTER_THRESHOLD, Double.toString(ParametersFrame.percentageParametre));
	    	manager.setProperty(ProjectManager.CLUSTER_EXPANSION, Double.toString(ParametersFrame.optimisationParametre));
    	}
    	frame.dispose();
    }//GEN-LAST:event_paramClassiqueMenuItemActionPerformed
    /**
     * Permet d'ouvrir une fenètre pour changer les préférences utilisateur
     *
     * @param evt
     */
    private void paramSemantiqueMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_paramSemantiqueMenuItemActionPerformed
        try {
        	PreprocessingFrame frame = new PreprocessingFrame(
        			initialGraph, manager.getPredicates(), manager.getQueries(),
        			String.valueOf( manager.getClusterThreshold() ), 
        			String.valueOf( manager.getClusterExpansion() )
        			);
        	frame.setModal(true);
        	frame.setVisible(true);
        	if(frame.getDialogResult() == JOptionPane.OK_OPTION)
        	{
        		manager.clearPredicates();
        		
        		for(Properties predicate : frame.getPredicates())
        			manager.addPredicate(predicate);
        		
        		manager.clearQueries();
        		manager.getQueries().addAll( frame.getQueries() );
         		
        		manager.setProperty(ProjectManager.CLUSTER_THRESHOLD, Double.toString(frame.getPoids()));
    	    	manager.setProperty(ProjectManager.CLUSTER_EXPANSION, Double.toString(frame.getOptimisations()));
        	}
        	frame.dispose();
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_paramSemantiqueMenuItemActionPerformed

    private void lancerClusteringMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
        try 
        {
        	ArrayList<JInternalFrame> list = desktop.getFramesLikeName(ClustersVizualisation.NAME);
        	if(list != null)
        	{
        		list.get(0).setSelected(true);
        		
        		int result = JOptionPane.showConfirmDialog(this, "Clustering is already executed.\nWould you like to close current and start a new clustering ?", "Start the Clustering", JOptionPane.YES_NO_OPTION);
        		if(result == JOptionPane.NO_OPTION)
        			return;
        		
        		for(JInternalFrame frame : list)
        		{
        			desktop.remove(frame);
        			frame.dispose();
        		}
        		
        		list = desktop.getFramesLikeName(ClustersVizualisation.NAME_THEME);
        		if(list != null)
        			for(JInternalFrame frame : list)
        			{
            			desktop.remove(frame);
            			frame.dispose();
        			}
        		
        		desktop.revalidate();
        		desktop.repaint();
        		
        		manager.setClusterDone(false);
        	}
        	
            runCluster();
        } 
        catch(Exception ex) 
        {
        	Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_lancerClusteringMenuItemActionPerformed
    private void runCluster() throws Exception
    {
    	// boolean to know if the user want to use only instancies or all the graph
        fullGraph = PreprocessingFrame.fullGraph;

        //graphToClusteing: graph after choosing datagraph or full graph (it can be initialGraph or dataGraph)
        graphToClustering = GraphCreation.GraphCreation(initialGraph, filePath, false, fullGraph);

        // the graph to clustering with all modifications specified in the semantique caracteristics 

        weightedGraphToClusteingWithoutLitterals = GraphCreation.main(graphToClustering, manager.getPredicates(), manager.getQueries()).getGraph();

        ////la liste qui va nous permettre l'affichage sur le graphe initial (vertex that was regrouped)
        listResourceRDFNodes = GraphCreation.main(graphToClustering, manager.getPredicates(), manager.getQueries()).getSet();

        // systematic parameters (clustering parametres )
        
        //The clustering class
        MCODEVertexWeitingWeightedGraph mcodevertexweitingweightedgraph = new MCODEVertexWeitingWeightedGraph();
        //the list of clusters with merged nodes
        finalClustersListwithoutLiterals = mcodevertexweitingweightedgraph.VertexWeiting(weightedGraphToClusteingWithoutLitterals, manager.getClusterThreshold(), manager.getClusterExpansion(), desktop);

        //add litterals to clusters and construct clusters with real vertex (initial vertex)
        finalClustersList = RealClustersConstruction.main(initialGraph, finalClustersListwithoutLiterals, listResourceRDFNodes);
        // Sort clusters according to their size
        SortGraphList(finalClustersList);
        

        // window showing clusters
        ClustersVizualisation frameInt = new ClustersVizualisation(
        		initialGraph, manager, finalClustersList, desktop, 
        		ClustersVizualisation.TITLE+" for \""+manager.getName()+"\""
        		);
        frameInt.setVisible(true);
        desktop.add(frameInt);
        frameInt.setMaximum(true);
        frameInt.setSelected(true);
    }
    /**
     * Pour ouvrir un nouveau projet, pas encore implémenté
     *
     * @param evt
     */
    private void ouvrirMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	try 
    	{
    		getFileChooser().setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
			int returnVal = fc.showDialog(MainWindow.this, "Project location");
			if(returnVal == JFileChooser.APPROVE_OPTION)
			{
				manager = new ProjectManager( fc.getSelectedFile().getAbsolutePath() );
				initGraph();
			}
        }
    	catch(Exception ex) 
        {
        	Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        	JOptionPane.showMessageDialog(MainWindow.this, ex.getMessage(), "Project loading error", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_ouvrirMenuItemActionPerformed
    /**
     * Pour enregistrer les positions des noeuds du graphe dans un .txt
     *
     * @param evt
     */
/**
 * Gestion du mode d'affichage, deux modes utilisés: mode PICKING pour faire bouger les noeuds et mode TRANSFORMING pour faire bouger le graphe entier 
 * @param evt 
 */
    private void modeMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_modeMenuItemActionPerformed
        if (modeMenuItem.isSelected()) {
            if ("firstVisualisation".equals(desktop.getSelectedFrame().getName())) {
                FirstGraphVisualization internalFram = (FirstGraphVisualization) desktop.getSelectedFrame();
                vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                vv2.setMode(ModalGraphMouse.Mode.PICKING);

            } else {
                if ("queriesVisualisation".equals(desktop.getSelectedFrame().getName())) {
                    QueriesVizualisation internalFram = (QueriesVizualisation) desktop.getSelectedFrame();
                    vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                    vv2.setMode(ModalGraphMouse.Mode.PICKING);
                } else {
                    ClustersVizualisation internalFram = (ClustersVizualisation) desktop.getSelectedFrame();
                    vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                    vv2.setMode(ModalGraphMouse.Mode.PICKING);
                }
            }
        } else {
            if ("firstVisualisation".equals(desktop.getSelectedFrame().getName())) {
                FirstGraphVisualization internalFram = (FirstGraphVisualization) desktop.getSelectedFrame();
                vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                vv2.setMode(ModalGraphMouse.Mode.TRANSFORMING);

            } else {
                if ("queriesVisualisation".equals(desktop.getSelectedFrame().getName())) {
                    QueriesVizualisation internalFram = (QueriesVizualisation) desktop.getSelectedFrame();
                    vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                    vv2.setMode(ModalGraphMouse.Mode.PICKING);
                } else {
                    ClustersVizualisation internalFram = (ClustersVizualisation) desktop.getSelectedFrame();
                    vv2 = (DefaultModalGraphMouse<RDFNode, Statement>) internalFram.vv.getGraphMouse();
                    vv2.setMode(ModalGraphMouse.Mode.TRANSFORMING);
                }
            }
        }
        this.repaint();
        this.revalidate();
    }//GEN-LAST:event_modeMenuItemActionPerformed
/**
 * Le recherche mots clés
 * @param evt 
 */
    private void ToolsMenuActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	try 
        {
    		if (Desktop.isDesktopSupported()) 
    		{
    			try
    			{
    				//Desktop.getDesktop().edit( manager.getKeywordsFile() );
    				ProcessBuilder pb = new ProcessBuilder("notepad", manager.getKeywordsFile().getAbsolutePath());
    				pb.start();
    			}
    			catch(Exception e)
    			{
    			}
        	} 
    		else 
        	{
        		JOptionPane.showMessageDialog(MainWindow.this, "Edit file : "+manager.getKeywordsFile().getAbsolutePath());
        	}
        } 
        catch(Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_ToolsMenuActionPerformed

    private void KeywordSearchMenuItemActionPerformed(java.awt.event.ActionEvent evt) 
    {
    	try 
        {
            new KeywordsSearch2(manager.getDirIndex().getAbsolutePath(), manager.getXY(), desktop, manager.getKeywordsOptions()).setVisible(true);
        } 
        catch(Exception ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_SearchMenuActionPerformed

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

	/**
	 * Creation of the principal window
	 *
	 * @param args the command line arguments
	 */
	public static void main()
	{
		/*try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}*/

		try
		{
			for(javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager
					.getInstalledLookAndFeels())
			{
				if("Nimbus".equals(info.getName()))
				{
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;

				}
			}
		}
		catch(ClassNotFoundException | InstantiationException | IllegalAccessException
				| javax.swing.UnsupportedLookAndFeelException ex)
		{
			java.util.logging.Logger.getLogger(MainWindow.class.getName())
					.log(java.util.logging.Level.SEVERE, null, ex);
		}

		// Create and display the principal window
		java.awt.EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				new MainWindow().setVisible(true);
			}
		});
	}

}