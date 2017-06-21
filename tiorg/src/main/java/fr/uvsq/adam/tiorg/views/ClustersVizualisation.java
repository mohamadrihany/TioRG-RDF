/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.tiorg.views;

import fr.uvsq.adam.clustering.MCODEVertexWeitingWeightedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author houk
 */
public class ClustersVizualisation extends javax.swing.JInternalFrame {
	
	public static String NAME = "clusterVisualisation";
	public static String NAME_THEME = "themeVisualisation";
	public static String TITLE = "Clustering Themes";
	public static String TITLE_THEME = "Clustering";

    static int openFrameCount = 1;
    private ArrayList<Graph> finalClustersList;
    private JTable clusterstable;
    public final Graph<RDFNode, Statement> graph;
    private ProjectManager projectManager;
    private HashMap<String,Point> coordinates;
    public GraphVisualizationJPanel graphVisualization;
    public VisualizationViewer<RDFNode, Statement> vv;
    private GraphZoomScrollPane graphPanel;
    private JInternalFrame frame;
    private Dimension screenSize;
    private MDIDesktopPane desktop;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JPanel glabolPanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JSplitPane splitGraph;
    private javax.swing.JMenuBar menuBar;
    
    static final int xOffset = 30, yOffset = 30;

    /**
     * Creates new form ClustersVizualisation
     *
     */
    public ClustersVizualisation(final Graph<RDFNode, Statement> GRAPH, ProjectManager projectManager, ArrayList<Graph> finalClustersLIST, MDIDesktopPane desktopPane, String name) throws IOException {
        super(name,
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable
        finalClustersList = finalClustersLIST;
        graph = GRAPH;
        this.projectManager = projectManager;
        this.coordinates = projectManager.getXY();
        desktop = desktopPane;
        initComponents2();

        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        glabolPanel = new javax.swing.JPanel();
        SplitPane = new javax.swing.JSplitPane();
        tablePanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();

        jTabbedPane1.setAutoscrolls(true);
        ClustersTable clustersPanel = new ClustersTable();
        clustersPanel.setOpaque(true);

        JScrollPane clustersList=(JScrollPane) clustersPanel.getComponent(0);
        JViewport clustersViewport = clustersList.getViewport();
        clusterstable = (JTable)clustersViewport.getView();
        clustersPanel.setAutoscrolls(true);
        jTabbedPane1.addTab("Liste des clusters", clustersPanel);
        ClustersTable.ClustersTableModel tableModel = (ClustersTable.ClustersTableModel) clusterstable.getModel();
        int num = tableModel.getRowCount() - 1;
        for (int i = 0; i <= num; i++) {
            tableModel.removeRow(0);
        }
        int i = 0;
        for (Graph cluster : finalClustersList) {

            ArrayList column = new ArrayList();
            column.add(Integer.toString(i));
            column.add(cluster.getVertexCount());
            column.add("");
            column.add("");
            tableModel.addRow(column.toArray());
            i++;
        }

        clusterstable.getSelectionModel().addListSelectionListener(new RowListener());

        javax.swing.GroupLayout tablePanelLayout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(tablePanelLayout);
        tablePanelLayout.setHorizontalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(tablePanelLayout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        tablePanelLayout.setVerticalGroup(
            tablePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 352, Short.MAX_VALUE)
        );

        SplitPane.setTopComponent(tablePanel);

        jPanel3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jPanel3MouseReleased(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 323, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 352, Short.MAX_VALUE)
        );

        SplitPane.setRightComponent(jPanel3);

        javax.swing.GroupLayout glabolPanelLayout = new javax.swing.GroupLayout(glabolPanel);
        glabolPanel.setLayout(glabolPanelLayout);
        glabolPanelLayout.setHorizontalGroup(
            glabolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane)
        );
        glabolPanelLayout.setVerticalGroup(
            glabolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(SplitPane)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jPanel3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPanel3MouseReleased
     
    }//GEN-LAST:event_jPanel3MouseReleased

    // Variables declaration - do not modify//GEN-BEGIN:variables

    // End of variables declaration//GEN-END:variables

    private void initComponents2() throws IOException {
        frame = this;
        setName(NAME);
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
        glabolPanel = new javax.swing.JPanel();
        SplitPane = new javax.swing.JSplitPane();
        tablePanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        splitGraph = new javax.swing.JSplitPane();
		menuBar = new javax.swing.JMenuBar();

        jTabbedPane1.setAutoscrolls(true);
        ClustersTable clustersPanel = new ClustersTable();
        clustersPanel.setOpaque(true);

        JScrollPane clustersList = (JScrollPane) clustersPanel.getComponent(0);
        JViewport clustersViewport = clustersList.getViewport();
        clusterstable = (JTable) clustersViewport.getView();
        
        int colIndex = 2;
        int colWidth = 500;
        TableColumn col = clusterstable.getColumnModel().getColumn(colIndex);         
        col.setPreferredWidth(colWidth);
        clustersPanel.setAutoscrolls(true);

        //**

        clusterstable.addMouseListener(new MouseAdapter() {
   
            @Override
            public void mousePressed(MouseEvent me) 
            {
                JTable table = (JTable) me.getSource();
                Point p = me.getPoint();
                int row = table.rowAtPoint(p);
                if (me.getClickCount() == 2) 
                {
                    try 
                    {
                        String clusterNum = (String) clusterstable.getValueAt(row, 0);
                        String nname = (String) clusterstable.getValueAt(row, 2);
                        String title = TITLE_THEME+" : #"+clusterNum+" "+ nname;
                        ArrayList<JInternalFrame> list = desktop.getFramesLikeTitle(title);
                        if(list != null && !list.isEmpty())
                        {
                        	list.get(0).setSelected(true);
                        	return;
                        }
                        Graph<RDFNode, Statement> cluster = finalClustersList.get(Integer.parseInt(clusterNum));
                  
                        FirstGraphVisualization frame = new FirstGraphVisualization(cluster, title, true, projectManager.getXY(nname));
                        frame.setName(NAME_THEME);
                        frame.setVisible(true);
                        desktop.add(frame);
                        frame.setMaximum(true);
                        frame.setSelected(true);
                    } 
                    catch (PropertyVetoException | IOException ex) 
                    {
                        Logger.getLogger(ClustersVizualisation.class.getName()).log(Level.SEVERE, null, ex);
                    }

                }
            }
        });


        jTabbedPane1.addTab("Set of themes", clustersPanel);
        ClustersTable.ClustersTableModel tableModel = (ClustersTable.ClustersTableModel) clusterstable.getModel();
        
        int num = tableModel.getRowCount() - 1;
        for (int i = 0; i <= num; i++) {
            tableModel.removeRow(0);
        }
        int i = 0;
        for (Graph<RDFNode, MCODEVertexWeitingWeightedGraph.MyLink> cluster : finalClustersList) {
            ArrayList<PairVertexWeight> setvrtex = new ArrayList<PairVertexWeight>();
            for (RDFNode node : cluster.getVertices()) {
                setvrtex.add(new PairVertexWeight(node, cluster.getNeighborCount(node)));
            }
            SortVertexWeight(setvrtex);

            ArrayList<Object> column = new ArrayList<Object>();
            column.add(Integer.toString(i));
            column.add(cluster.getVertexCount());
            if (cluster.getVertexCount() < 2) {
                column.add(setvrtex.get(0).Node.asResource().getLocalName());
            } else {
                if((setvrtex.get(0).Node.isLiteral())&(setvrtex.get(1).Node.isLiteral())){
                     column.add(setvrtex.get(0).Node.asLiteral().getString() + " , " + setvrtex.get(1).Node.asLiteral().getString());
                }
                 if((setvrtex.get(0).Node.isLiteral())&(setvrtex.get(1).Node.isResource())){
                     column.add(setvrtex.get(0).Node.asLiteral().getString() + " , " + setvrtex.get(1).Node.asResource().getLocalName());
                }
                  if((setvrtex.get(0).Node.isResource())&(setvrtex.get(1).Node.isResource())){
                    column.add(setvrtex.get(0).Node.asResource().getLocalName() + " , " + setvrtex.get(1).Node.asResource().getLocalName());
                }
                   if((setvrtex.get(0).Node.isResource())&(setvrtex.get(1).Node.isLiteral())){
                    column.add(setvrtex.get(0).Node.asResource().getLocalName() + " , " + setvrtex.get(1).Node.asLiteral().getString());
                }
                
            }
            tableModel.addRow(column.toArray());
            i++;
        }

        clusterstable.getSelectionModel().addListSelectionListener(new RowListener());
        clusterstable.setRowMargin(5);
        
        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(tablePanel);
        tablePanel.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE))
                ;
        jPanel2Layout.setVerticalGroup(
                jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE));
        Dimension minimumSize = new Dimension(250, 500);
        tablePanel.setMinimumSize(minimumSize);
        
        SplitPane.setTopComponent(tablePanel);
        
        graphVisualization = new GraphVisualizationJPanel(graph, new ArrayList<RDFNode>(), new ArrayList<Statement>(), coordinates, false);
        
        vv = graphVisualization.vv;
        
        graphPanel = new GraphZoomScrollPane(vv);
        
        
        graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
      
        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 287, Short.MAX_VALUE));
        jPanel3Layout.setVerticalGroup(
                jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGap(0, 280, Short.MAX_VALUE));
        graphPanel.setMinimumSize(minimumSize);

		menuBar.setFont(new Font("Arial", Font.PLAIN, 11));
		menuBar.setMinimumSize(new Dimension(0, 32));
		menuBar.setMaximumSize(new Dimension(0, 32));
		
		final JButton btnTarget = new JButton("");
		btnTarget.setToolTipText("Select Node");
		btnTarget.setIcon(new ImageIcon(getClass().getResource("/target-16px.png")));
		btnTarget.setSelectedIcon(new ImageIcon(getClass().getResource("/target-24px.png")));
		btnTarget.setBorder(null);
		btnTarget.setMinimumSize(new Dimension(30, 23));
		btnTarget.setPreferredSize(new Dimension(30, 23));
		btnTarget.setMaximumSize(new Dimension(30, 30));
		btnTarget.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				if( graphVisualization.getNodeTargetFilter() )
				{
					btnTarget.setIcon(new ImageIcon(getClass().getResource("/target-16px.png")));
					vv.setCursor( Cursor.getDefaultCursor() );
					graphVisualization.setNodeTargetFilter(false);
				}
				else
				{
					btnTarget.setIcon( btnTarget.getSelectedIcon() );
					vv.setCursor( Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR) );
					graphVisualization.setNodeTargetFilter(true);
				}
			}
		});
		menuBar.add(btnTarget);
		
		final JButton btnEdit = new JButton();
		btnEdit.setToolTipText("Editing Mode");
		btnEdit.setIcon(new ImageIcon(getClass().getResource("/hand-16px.png")));
		btnEdit.setSelectedIcon(new ImageIcon(getClass().getResource("/hand-24px.png")));
		btnEdit.setMinimumSize(new Dimension(30, 23));
		btnEdit.setPreferredSize(new Dimension(30, 23));
		btnEdit.setMaximumSize(new Dimension(30, 30));
		btnEdit.setBorder(null);
		btnEdit.addActionListener(new ActionListener() 
		{
			public void actionPerformed(ActionEvent arg0) 
			{
				if(btnEdit.getIcon() == btnEdit.getSelectedIcon())
				{
					btnEdit.setIcon(new ImageIcon(getClass().getResource("/hand-16px.png")));
		            ((DefaultModalGraphMouse<RDFNode, Statement>)vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.TRANSFORMING);
				}
				else
				{
					btnEdit.setIcon( btnEdit.getSelectedIcon() );
					((DefaultModalGraphMouse<RDFNode, Statement>)vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.PICKING);
				}
			}
		});
		menuBar.add(btnEdit);
		
		final ZoomBar zoomBar = new ZoomBar();
		zoomBar.setBackground( menuBar.getBackground() );
		zoomBar.setPreferredSize(new Dimension(300, 30));
		zoomBar.setMinimumSize(new Dimension(300, 41));
		zoomBar.setMaximumSize(new Dimension(300, 32767));
		zoomBar.addListenerChanged(
				new PropertyChangeListener()
				{
					public void propertyChange(PropertyChangeEvent evt)
					{
						if(evt.getPropertyName() == "zoomValue")
						{
							float zoom = (int)evt.getNewValue()/100f;
							try
							{
								if(zoom > 1)
									vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(zoom, zoom, vv.getCenter());
								else
									vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(zoom, zoom, vv.getCenter());
							}
							catch(Exception e)
							{
								
							}
						}
					}
				}
		);
		menuBar.add(zoomBar);
		
		splitGraph.setDividerSize(0);
		splitGraph.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitGraph.setTopComponent(menuBar);
		splitGraph.setBottomComponent(graphPanel);
		
		SplitPane.setBottomComponent(splitGraph);
		
		vv.addMouseWheelListener(new MouseWheelListener()
		{
			@Override
			public void mouseWheelMoved(MouseWheelEvent e)
			{
				double zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
				if(zoom > 1)
				{
					zoomBar.setZoomValue((int)(zoom*100));
				}
				else
				{
					zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
					if(zoom < 1)
						zoomBar.setZoomValue((int)(zoom*100));
				}
			}
		});

        javax.swing.GroupLayout glabolPanelLayout = new javax.swing.GroupLayout(glabolPanel);
        glabolPanel.setLayout(glabolPanelLayout);
        glabolPanelLayout.setHorizontalGroup(
                glabolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(SplitPane));
        glabolPanelLayout.setVerticalGroup(
                glabolPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(SplitPane));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addComponent(glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();
    }
    

    public class PairVertexWeight {

        private RDFNode Node;
        private int Weight;

        public RDFNode getNode() {
            return Node;
        }

        public float getWeight() {
            return Weight;
        }

        public PairVertexWeight(RDFNode node, int weight) {
            Node = node;
            Weight = weight;
        }
    }

    private void SortVertexWeight(ArrayList<PairVertexWeight> nodesDegree) {
        SortTupleVertexWeightComparator com = new SortTupleVertexWeightComparator();
        Collections.sort(nodesDegree, com);
    }

    public class SortTupleVertexWeightComparator implements Comparator<PairVertexWeight> {

        @Override
        public int compare(PairVertexWeight o1, PairVertexWeight o2) {
            int datarate1 = o1.Weight;
            int datarate2 = o2.Weight;

            if (datarate1 < datarate2) {
                return +1;
            } else if (datarate1 > datarate2) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    private class RowListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent event) {
            if (event.getValueIsAdjusting()) {
                return;
            }
            ListSelectionModel lsm = (ListSelectionModel) event.getSource();
            if (lsm.isSelectionEmpty()) {
                System.out.println("No rows selected");
            } else {
                int selectedRow = lsm.getMinSelectionIndex();

                try {
                    
                    String clustername = (String) clusterstable.getValueAt(selectedRow, 0);
                    Graph cluster = finalClustersList.get(Integer.parseInt(clustername));
                    Collection vertexToSee = cluster.getVertices();
                    graphVisualization.setVertexToSee(vertexToSee);
                    //   String coeffecient = coefText.getText();
                    //graphVisualization = new GraphVisualizationJPanel(graph, vertexToSee, new ArrayList<Statement>(), coordinates, false);
                    //vv = graphVisualization.vv;
                    //graphPanel = new GraphZoomScrollPane(vv);
                    //graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
                    //SplitPane.remove(graphPanel);
                    //SplitPane.setRightComponent(graphPanel);
                    frame.revalidate();
                    frame.repaint();
                    //frame.setVisible(true);
                } catch (Exception ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
}