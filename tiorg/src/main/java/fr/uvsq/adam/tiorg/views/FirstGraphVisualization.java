/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.tiorg.views;

import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.uvsq.adam.tiorg.TioRG.TIORG_APP;

/**
 * @author houk
 */
public class FirstGraphVisualization extends JInternalFrame {
    public static final String NAME = "firstVisualisation";

    public final Graph<RDFNode, Statement> graph;
    private HashMap<String, Point> coordinates;
    public VisualizationViewer<RDFNode, Statement> vv;
    public GraphVisualizationJPanel graphVisualization;
    static int openFrameCount = 0;
    static final int xOffset = 30, yOffset = 30;
    boolean clusterview;
    private javax.swing.JMenuBar menuBar;
    private PatternSearch patternSearch;

    private ActionListener actionNodeFilter;
    private ActionListener actionLinkFilter;
    private ActionListener actionSelectAll;


    public FirstGraphVisualization(Graph<RDFNode, Statement> graph, String nom, boolean clusterView, HashMap<String, Point> coordinates) throws IOException {

        super(nom, true, true, true, true);
        this.graph = graph;
        this.clusterview = clusterView;
        this.coordinates = coordinates;
        //MainWindow.modeMenuItem.setSelected(false); //TODO to remove ?
        initComponents();

        setLocation(xOffset * openFrameCount, yOffset * openFrameCount);
        openFrameCount++;

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents3() {

        globalPanel = new javax.swing.JPanel();

        setAutoscrolls(true);
        setName(NAME); // NOI18N

        javax.swing.GroupLayout globalPanelLayout = new javax.swing.GroupLayout(globalPanel);
        globalPanel.setLayout(globalPanelLayout);
        globalPanelLayout.setHorizontalGroup(
                globalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 419, Short.MAX_VALUE)
        );
        globalPanelLayout.setVerticalGroup(
                globalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 323, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel globalPanel;
    private JMenu mnNodeFilters;
    private JMenu mnLinkFilters;
    private JTextField txtTextFilter;
    private JTextField txtSparqlFilter;
    // End of variables declaration//GEN-END:variables

    private void initComponents() throws IOException {
        setName("firstVisualisation");
        setAutoscrolls(true);
        globalPanel = new javax.swing.JPanel();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setPreferredSize(new Dimension(screenSize.width / 2, screenSize.height / 2));
        //ici qu'il ne faut pas liret  new ArrayList<CoordinateXY>()
        graphVisualization = new GraphVisualizationJPanel(graph, null, null, coordinates, clusterview);
        vv = graphVisualization.vv;

        menuBar = new javax.swing.JMenuBar();
        menuBar.setFont(new Font("Arial", Font.PLAIN, 11));
        menuBar.setMinimumSize(new Dimension(0, 22));
        menuBar.setMaximumSize(new Dimension(0, 22));

        mnNodeFilters = new JMenu("Node filters");
        mnNodeFilters.setFont(new Font("Arial", Font.PLAIN, 11));
        mnNodeFilters.setFocusPainted(true);
        menuBar.add(mnNodeFilters);

        JMenuItem item = new JMenuItem("Select all");
        item.addActionListener(getActionSelectAll());
        mnNodeFilters.add(item);

        mnNodeFilters.addSeparator();
        for (String type : graphVisualization.getTypesNode()) {
            item = new JCheckBoxMenuItem(type, true);
            /*item.setContentAreaFilled(false);
        	item.setBorder(null);
        	item.setIconTextGap(0);
        	item.setHorizontalAlignment(SwingConstants.LEFT);
        	item.setHorizontalTextPosition(SwingConstants.LEFT);
        	item.setFont(new Font("Arial", Font.PLAIN, 10));
            miFilterURI.setMaximumSize(new Dimension(35, 32767));
            miFilterURI.setMinimumSize(new Dimension(35, 0));
            miFilterURI.setPreferredSize(new Dimension(35, 22));
            miFilterURI.setSize(new Dimension(55, 0));*/
            item.addActionListener(getActionNodeFilter());
            mnNodeFilters.add(item);
        }

        JLabel libSep1 = new JLabel("");
        libSep1.setMinimumSize(new Dimension(10, 20));
        libSep1.setMaximumSize(new Dimension(10, 20));
        libSep1.setPreferredSize(new Dimension(10, 20));
        menuBar.add(libSep1);

        mnLinkFilters = new JMenu("Link filters");
        mnLinkFilters.setFont(new Font("Arial", Font.PLAIN, 11));
        mnLinkFilters.setFocusPainted(true);
        menuBar.add(mnLinkFilters);

        item = new JMenuItem("Select all");
        item.addActionListener(getActionSelectAll());
        mnLinkFilters.add(item);
        mnLinkFilters.addSeparator();
        for (String type : graphVisualization.getTypesLink()) {
            item = new JCheckBoxMenuItem(type, true);
        	/*item.setContentAreaFilled(false);
        	item.setBorder(null);
        	item.setIconTextGap(0);
        	item.setHorizontalAlignment(SwingConstants.LEFT);
        	item.setHorizontalTextPosition(SwingConstants.LEFT);
        	item.setFont(new Font("Arial", Font.PLAIN, 10));
            miFilterURI.setMaximumSize(new Dimension(35, 32767));
            miFilterURI.setMinimumSize(new Dimension(35, 0));
            miFilterURI.setPreferredSize(new Dimension(35, 22));
            miFilterURI.setSize(new Dimension(55, 0));*/
            item.addActionListener(getActionLinkFilter());
            mnLinkFilters.add(item);
        }

        JLabel lblSep2 = new JLabel("");
        lblSep2.setMinimumSize(new Dimension(10, 20));
        lblSep2.setMaximumSize(new Dimension(10, 20));
        lblSep2.setPreferredSize(new Dimension(10, 20));
        menuBar.add(lblSep2);

        JLabel lblTextFilter = new JLabel("Text filter : ");
        lblTextFilter.setHorizontalAlignment(SwingConstants.RIGHT);
        lblTextFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        lblTextFilter.setMinimumSize(new Dimension(60, 0));
        lblTextFilter.setMaximumSize(new Dimension(60, 32767));
        menuBar.add(lblTextFilter);

        txtTextFilter = new JTextField();
        txtTextFilter.setColumns(10);
        txtTextFilter.setMaximumSize(new Dimension(150, 2147483647));
        txtTextFilter.setPreferredSize(new Dimension(80, 20));
        txtTextFilter.setMinimumSize(new Dimension(80, 20));
        txtTextFilter.setBorder(new CompoundBorder(new EmptyBorder(3, 0, 3, 0), new LineBorder(new Color(0, 0, 0))));
        txtTextFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        txtTextFilter.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    graphVisualization.setNodeTextFilter(txtTextFilter.getText());
                }
            }
        });

        menuBar.add(txtTextFilter);


        JButton btCleanTxtFilter = new JButton("x");
        btCleanTxtFilter.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        txtTextFilter.setText("");
                        graphVisualization.setNodeTextFilter(null);
                    }
                }
        );
        btCleanTxtFilter.setPreferredSize(new Dimension(18, 18));
        btCleanTxtFilter.setMaximumSize(new Dimension(18, 18));
        btCleanTxtFilter.setMinimumSize(new Dimension(18, 18));
        btCleanTxtFilter.setMargin(new Insets(0, 0, 0, 0));
        btCleanTxtFilter.setBorder(null);
        menuBar.add(btCleanTxtFilter);

        JLabel lblSep3 = new JLabel("");
        lblSep3.setMaximumSize(new Dimension(10, 20));
        lblSep3.setMinimumSize(new Dimension(10, 20));
        lblSep3.setPreferredSize(new Dimension(10, 20));
        menuBar.add(lblSep3);

        JLabel lblSparqlFilter = new JLabel("Sparql filter : ");
        lblSparqlFilter.setMinimumSize(new Dimension(70, 0));
        lblSparqlFilter.setMaximumSize(new Dimension(70, 32767));
        lblSparqlFilter.setHorizontalAlignment(SwingConstants.RIGHT);
        lblSparqlFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        menuBar.add(lblSparqlFilter);

        txtSparqlFilter = new JTextField();
        txtSparqlFilter.setEditable(false);
        txtSparqlFilter.setPreferredSize(new Dimension(80, 20));
        txtSparqlFilter.setMinimumSize(new Dimension(80, 20));
        txtSparqlFilter.setMaximumSize(new Dimension(200, 2147483647));
        txtSparqlFilter.setFont(new Font("Arial", Font.PLAIN, 11));
        txtSparqlFilter.setColumns(10);
        txtSparqlFilter.setBorder(new CompoundBorder(new EmptyBorder(3, 0, 3, 0), new LineBorder(new Color(0, 0, 0))));
        txtSparqlFilter.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                getPatternSearch().setQuery(txtSparqlFilter.getText());
                getPatternSearch().setVisible(true);
                if (patternSearch.getDialogResult() == JOptionPane.OK_OPTION) {
                    txtSparqlFilter.setText(patternSearch.getQuery());
                    graphVisualization.setNodeSparqlFilter(txtSparqlFilter.getText());
                }
            }
        });
        menuBar.add(txtSparqlFilter);

        JButton btCleanSparqlFilter = new JButton("x");
        btCleanSparqlFilter.addActionListener(
                new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        txtSparqlFilter.setText("");
                        graphVisualization.setNodeSparqlFilter(null);
                    }
                }
        );
        btCleanSparqlFilter.setPreferredSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMinimumSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMaximumSize(new Dimension(18, 18));
        btCleanSparqlFilter.setMargin(new Insets(0, 0, 0, 0));
        btCleanSparqlFilter.setBorder(null);
        menuBar.add(btCleanSparqlFilter);

        JLabel lblSep4 = new JLabel("");
        lblSep4.setPreferredSize(new Dimension(10, 20));
        lblSep4.setMinimumSize(new Dimension(10, 20));
        lblSep4.setMaximumSize(new Dimension(10, 20));
        menuBar.add(lblSep4);

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
                if (graphVisualization.getNodeTargetFilter()) {
                    btnTarget.setIcon(new ImageIcon(getClass().getResource("/target-16px.png")));
                    vv.setCursor(Cursor.getDefaultCursor());
                    graphVisualization.setNodeTargetFilter(false);
                } else {
                    btnTarget.setIcon(btnTarget.getSelectedIcon());
                    vv.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
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
        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if (btnEdit.getIcon() == btnEdit.getSelectedIcon()) {
                    btnEdit.setIcon(new ImageIcon(getClass().getResource("/hand-16px.png")));
                    ((DefaultModalGraphMouse<RDFNode, Statement>) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.TRANSFORMING);
                } else {
                    btnEdit.setIcon(btnEdit.getSelectedIcon());
                    ((DefaultModalGraphMouse<RDFNode, Statement>) vv.getGraphMouse()).setMode(ModalGraphMouse.Mode.PICKING);
                }
            }
        });
        menuBar.add(btnEdit);

        JButton btOpen = new JButton();
        btOpen.setToolTipText("Open sub-graph");
        btOpen.setRolloverIcon(new ImageIcon(getClass().getResource("/copy-24px.png")));
        btOpen.setIcon(new ImageIcon(getClass().getResource("/copy-16px.png")));
        btOpen.addMouseListener(
                new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        try {
                            DirectedGraph<RDFNode, Statement> graph = graphVisualization.getVisibleSubGraph();
                            if (graph != null) {
                                FirstGraphVisualization frame = new FirstGraphVisualization(graph, getTitle() + " : " + "Sub-graph [" + graph.getVertexCount() + "x" + graph.getEdgeCount() + "]", false, graphVisualization.getGraphCoordinates());
                                frame.setVisible(true);
                                TIORG_APP.getMainWindow().getDesktop().add(frame);
                                frame.setSelected(true);
                                frame.setMaximum(true);
                            }
                        } catch (Exception ex) {
                            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
        );
        btOpen.setPreferredSize(new Dimension(30, 23));
        btOpen.setMinimumSize(new Dimension(30, 23));
        btOpen.setMaximumSize(new Dimension(30, 30));
        btOpen.setBorder(null);
        menuBar.add(btOpen);

        JLabel lblSep5 = new JLabel("");
        lblSep5.setPreferredSize(new Dimension(10, 20));
        lblSep5.setMinimumSize(new Dimension(10, 20));
        lblSep5.setMaximumSize(new Dimension(10, 20));
        menuBar.add(lblSep5);

        final ZoomBar zoomBar = new ZoomBar();
        zoomBar.setBackground(menuBar.getBackground());
        zoomBar.setPreferredSize(new Dimension(300, 30));
        zoomBar.setMinimumSize(new Dimension(300, 41));
        zoomBar.setMaximumSize(new Dimension(300, 32767));
        zoomBar.addListenerChanged(
                new PropertyChangeListener() {
                    public void propertyChange(PropertyChangeEvent evt) {
                        if (evt.getPropertyName() == "zoomValue") {
                            float zoom = (int) evt.getNewValue() / 100f;
                            try {
                                if (zoom > 1)
                                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).setScale(zoom, zoom, vv.getCenter());
                                else
                                    vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).setScale(zoom, zoom, vv.getCenter());
                            } catch (Exception e) {

                            }
                        }
                    }
                }
        );
        menuBar.add(zoomBar);

        setJMenuBar(menuBar);

        vv.addMouseWheelListener(new MouseWheelListener() {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e) {
                double zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.LAYOUT).getScale();
                if (zoom > 1) {
                    zoomBar.setZoomValue((int) (zoom * 100));
                } else {
                    zoom = vv.getRenderContext().getMultiLayerTransformer().getTransformer(Layer.VIEW).getScale();
                    if (zoom < 1)
                        zoomBar.setZoomValue((int) (zoom * 100));
                }
            }
        });

        final GraphZoomScrollPane graphPanel = new GraphZoomScrollPane(vv);
        graphPanel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
        javax.swing.GroupLayout globalPanelLayout = new javax.swing.GroupLayout(globalPanel);
        globalPanel.setLayout(globalPanelLayout);
        globalPanelLayout.setHorizontalGroup(
                globalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 419, Short.MAX_VALUE)
                        .addComponent(graphPanel));
        globalPanelLayout.setVerticalGroup(
                globalPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGap(0, 319, Short.MAX_VALUE)
                        .addComponent(graphPanel));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(globalPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));

        pack();

    }

    private PatternSearch getPatternSearch() {
        if (patternSearch == null) {
            patternSearch = new PatternSearch();
            patternSearch.setModal(true);
        }
        return patternSearch;
    }

    private ActionListener getActionNodeFilter() {
        if (actionNodeFilter == null) {
            actionNodeFilter = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (((JMenuItem) evt.getSource()).isSelected())
                        graphVisualization.removeNodeTypeFilter(evt.getActionCommand());
                    else
                        graphVisualization.addNodeTypeFilter(evt.getActionCommand());
                }
            };
        }
        return actionNodeFilter;
    }

    private ActionListener getActionLinkFilter() {
        if (actionLinkFilter == null) {
            actionLinkFilter = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    if (((JMenuItem) evt.getSource()).isSelected())
                        graphVisualization.removeEdgeTypeFilter(evt.getActionCommand());
                    else
                        graphVisualization.addEdgeTypeFilter(evt.getActionCommand());
                }
            };
        }
        return actionLinkFilter;
    }

    private ActionListener getActionSelectAll() {
        if (actionSelectAll == null) {
            actionSelectAll = new ActionListener() {
                public void actionPerformed(ActionEvent evt) {
                    JPopupMenu menu = (JPopupMenu) ((JMenuItem) evt.getSource()).getParent();
                    for (int i = 0; i < menu.getComponentCount(); i++) {
                        graphVisualization.setIgnoreRepaint(true);
                        try {
                            Component component = menu.getComponent(i);
                            if (component instanceof JCheckBoxMenuItem)
                                if (!((JCheckBoxMenuItem) component).isSelected())
                                    ((JCheckBoxMenuItem) component).doClick();
                        } finally {
                            graphVisualization.setIgnoreRepaint(false);
                            graphVisualization.repaint();
                        }
                    }
                }
            };
        }
        return actionSelectAll;
    }
}
