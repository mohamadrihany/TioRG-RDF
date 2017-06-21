/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.tiorg.views;

import fr.uvsq.adam.processings.FileToModelGraph;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import fr.uvsq.adam.search.Ranking;
import fr.uvsq.adam.search.Ranking.TupleResultScore;
import fr.uvsq.adam.tiorg.views.QueriesTable.QueriesTableModel;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author houk
 */
public class QueriesVizualisation extends javax.swing.JInternalFrame {

	public static String NAME = "queriesVisualisation";
	public static String TITLE = "Keywords Search";
		
    static int openFrameCount = 1;
    private JTable queriestable;
    public final UndirectedSparseGraph<RDFNode, Statement> graph;
    private HashMap<String, Point> coordinates;
    public GraphVisualizationJPanel graphVisualization;
    public VisualizationViewer<RDFNode, Statement> vv;
    private GraphZoomScrollPane graphPanel;
    private JInternalFrame frame;
    private Dimension screenSize;
    private javax.swing.JDesktopPane jDesktopPane;
    private javax.swing.JSplitPane SplitPane;
    private javax.swing.JPanel glabolPanel;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel tablePanel;
    private javax.swing.JSplitPane splitGraph;
    private javax.swing.JMenuBar menuBar;
    
    static final int xOffset = 30, yOffset = 30;
    
    Graph<RDFNode, Statement> directedgraph;
    Model model;
	ArrayList<Ranking.TupleResultScore> resultListModel;

    /**
     * Creates new form
     *
     */
    public QueriesVizualisation(Graph<RDFNode, Statement> g, Model modeltoSearch, final UndirectedSparseGraph<RDFNode, Statement> GRAPH, HashMap<String, Point> coordinates, JDesktopPane jDesktopPANE, String name,  ArrayList<TupleResultScore> resultListModel1) throws IOException {
        super(name,
                true, //resizable
                true, //closable
                true, //maximizable
                true);//iconifiable
        directedgraph = g;
        resultListModel=resultListModel1;
        graph = GRAPH;
        this.coordinates = coordinates;
        jDesktopPane = jDesktopPANE;
        model = modeltoSearch;
        initComponents2();

        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;

    }
    // Variables declaration - do not modify                     

	// End of variables declaration

	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">
	private void initComponents2() throws IOException
	{
		frame = this;
		setName(NAME);
		screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
		glabolPanel = new javax.swing.JPanel();
		SplitPane = new javax.swing.JSplitPane();
		splitGraph = new javax.swing.JSplitPane();
		menuBar = new javax.swing.JMenuBar();
		tablePanel = new javax.swing.JPanel();
		jTabbedPane1 = new javax.swing.JTabbedPane();
		jPanel3 = new javax.swing.JPanel();

		jTabbedPane1.setAutoscrolls(true);
		QueriesTable queriesPanel = new QueriesTable();
		queriesPanel.setOpaque(true);

		JScrollPane queriesList = (JScrollPane) queriesPanel.getComponent(0);
		JViewport queriesViewport = queriesList.getViewport();
		queriestable = (JTable) queriesViewport.getView();

		queriestable.addMouseListener(new MouseAdapter()
		{

			@Override
			public void mousePressed(MouseEvent me)
			{

				JTable table = (JTable) me.getSource();
				Point p = me.getPoint();
				int row = table.rowAtPoint(p);
				if(me.getClickCount() == 2)
				{
					try
					{
						int num0 = (Integer) queriestable.getValueAt(row, 0);
						DirectedGraph<RDFNode,Statement> selgra = FileToModelGraph.ModelToGraph(resultListModel.get(num0).getResult().getModel());
						Graph cluster = selgra;
						String nname = "Selected Result";

						FirstGraphVisualization firstframeInt = new FirstGraphVisualization(cluster, TITLE + " : #" + num0 + " "+(String) queriestable.getValueAt(row, 1), true, coordinates);
						firstframeInt.setVisible(true);

						jDesktopPane.add(firstframeInt);
						firstframeInt.setMaximum(true);
						firstframeInt.setSelected(true);
					}
					catch(PropertyVetoException | IOException ex)
					{
						Logger.getLogger(QueriesVizualisation.class.getName()).log(Level.SEVERE,
								null, ex);
					}

				}
			}
		});

		queriestable.getColumnModel().getColumn(0).setPreferredWidth(40);
		queriestable.getColumnModel().getColumn(1).setPreferredWidth(500);
		queriestable.getColumnModel().getColumn(2).setPreferredWidth(80);
		queriesPanel.setAutoscrolls(true);

		jTabbedPane1.addTab("Set of results", queriesPanel);
		QueriesTableModel tableModel = (QueriesTableModel) queriestable.getModel();

		int num = tableModel.getRowCount() - 1;
		for(int i = 0; i <= num; i++)
		{
			tableModel.removeRow(0);
		}
		int index = 0;
		for(TupleResultScore model : resultListModel)
		{
			DirectedGraph<RDFNode,Statement> gr = FileToModelGraph.ModelToGraph(model.getResult().getModel());
			
			int i=0;
			String result = "";
			for(RDFNode nn : gr.getVertices())
			{
				if(i > 0)
					result = result + " , ";
				if(nn.isLiteral())
					result += nn.asLiteral().getString();
				else
					result += nn.asResource().getLocalName();
				
				i++;
				if(i == 3)
					break;
			}
			
			ArrayList column = new ArrayList();
			column.add(index);
			column.add(result);
			column.add(model.getScore());
			tableModel.addRow(column.toArray());
			
			index++;
		}

		queriestable.getSelectionModel().addListSelectionListener(new RowListener());
		queriestable.setRowMargin(5);
		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(tablePanel);
		tablePanel.setLayout(jPanel2Layout);
		jPanel2Layout.setHorizontalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE));
		jPanel2Layout.setVerticalGroup(
				jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
						.addComponent(jTabbedPane1, javax.swing.GroupLayout.Alignment.TRAILING,
								javax.swing.GroupLayout.DEFAULT_SIZE, 336, Short.MAX_VALUE));
		Dimension minimumSize = new Dimension(250, 500);
		tablePanel.setMinimumSize(minimumSize);

		SplitPane.setTopComponent(tablePanel);

		graphVisualization = new GraphVisualizationJPanel(graph, new ArrayList<RDFNode>(),
				new ArrayList<Statement>(), coordinates, false);
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
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
						glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		layout.setVerticalGroup(
				layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(
						glabolPanel, javax.swing.GroupLayout.DEFAULT_SIZE,
						javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

		pack();
	}// </editor-fold>

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

    private class RowListener implements ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent event)
		{
			if(event.getValueIsAdjusting())
			{
				return;
			}
			ListSelectionModel lsm = (ListSelectionModel) event.getSource();
			if(lsm.isSelectionEmpty())
			{
				System.out.println("No rows selected");
			}
			else
			{
				int selectedRow = (resultListModel.size() - 1) - lsm.getMinSelectionIndex();

				try
				{
					if("model".equals(resultListModel.get(selectedRow).getResult().getTypeResult()))
					{

						Model modelTosee = resultListModel.get(selectedRow).getResult().getModel();
						Collection<RDFNode> vertexToSee = new ArrayList<RDFNode>();
						ArrayList<Statement> statementToSee = new ArrayList<Statement>();
						DirectedGraph<RDFNode,Statement> graphToSee = FileToModelGraph
								.ModelToGraph(modelTosee);
						for(RDFNode node : graphToSee.getVertices())
						{
							vertexToSee.add(node);
						}
						for(Statement stat : graphToSee.getEdges())
						{
							statementToSee.add(stat);
						}
						graphVisualization.setVertexToSee(vertexToSee);
						graphVisualization.setEdgeToSee(statementToSee);
						//graphVisualization = new GraphVisualizationJPanel(graph, vertexToSee, StatementToSee, coordinates, false);
						//vv = graphVisualization.vv;
						//graphPanel = new GraphZoomScrollPane(vv);
						//graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
						//SplitPane.remove(graphPanel);
						//SplitPane.setRightComponent(graphPanel);
						frame.revalidate();
						frame.repaint();
						//frame.setVisible(true);
					}
					else
					{

						Collection<RDFNode> vertexToSee = new ArrayList<RDFNode>();
						vertexToSee.add(resultListModel.get(selectedRow).getResult().getNode());

						graphVisualization.setVertexToSee(vertexToSee);
						graphVisualization.setEdgeToSee(null);
						//graphVisualization = new GraphVisualizationJPanel(graph, vertexToSee, StatementToSee, coordinates, false);
						//vv = graphVisualization.vv;
						//graphPanel = new GraphZoomScrollPane(vv);
						//graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
						//SplitPane.remove(graphPanel);
						//SplitPane.setRightComponent(graphPanel);
						frame.revalidate();
						frame.repaint();
						//frame.setVisible(true);
					}

				}
				catch(Exception ex)
				{
					Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
				}
			}
		}
    }
}