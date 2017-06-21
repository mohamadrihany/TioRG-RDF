/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.tiorg.views;

import fr.uvsq.adam.processings.ManipulateDocument;
import edu.uci.ics.jung.algorithms.layout.AbstractLayout;
import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.CrossoverScalingControl;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ScalingControl;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import net.rootdev.jenajung.Transformers;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author houk
 */
public class GraphVisualizations extends JApplet {
//pour la creation des classes pour la modification des couleur, on aura la meme classe et des conditions si le parametre !=null

    /**
     * the visual component and renderer for the graph
     */
    VisualizationViewer<RDFNode, Statement> vv;
    AbstractLayout<RDFNode, Statement> layout;
    private JButton coefButtom = new JButton("Use");

    public GraphVisualizations(final Graph<RDFNode, Statement> g, final Collection vertexToSee, final ArrayList<Statement> edgesToSee, ArrayList<CoordinateXY> coordinates, String coefVal, final JFrame frame) throws IOException {
        final JTextField coef = new JTextField(coefVal);
        //layout du placement des noeuds automatiquement
      layout = new FRLayout(g);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // frame.setBounds(0,0,screenSize.width, screenSize.height-800);
        frame.setLocationRelativeTo(null);
        frame.setPreferredSize(new java.awt.Dimension(screenSize.width, screenSize.height - 50));
        //vv le panel (classe jung heritée du jpanel) pour le graphe 
        vv = new VisualizationViewer<>(layout, new Dimension(screenSize.width - 80, screenSize.height - 100));
        vv.setBackground(Color.white);

        //gerer les annotations des noeuds et arcs 
        RenderContext context = vv.getRenderContext();
        context.setEdgeLabelTransformer(Transformers.EDGE); // property label
        context.setVertexLabelTransformer(Transformers.NODE); // node label
        // affichage des labels et les couleurs des noeuds
        vv.setVertexToolTipTransformer(new ToStringLabeller());
        vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

        Container content = getContentPane();
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        content.add(panel);

        final DefaultModalGraphMouse graphMouse = new DefaultModalGraphMouse();

        vv.setGraphMouse(graphMouse);
        // **       changer la couleur des noeuds
        Transformer<RDFNode, Paint> vertexPaint = new Transformer<RDFNode, Paint>() {
            @Override
            public Paint transform(RDFNode node) {
                if (vertexToSee.contains(node)) {
                    return Color.YELLOW;
                }
                return Color.RED;
            }
        };
        Transformer<Statement, Paint> edgePaint = new Transformer<Statement, Paint>() {
            @Override
            public Paint transform(Statement i) {
                if (edgesToSee.contains(i)) {
                    return Color.YELLOW;
                }
                return null;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        vv.getRenderContext().setEdgeFillPaintTransformer(edgePaint);
        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        if (coordinates.isEmpty()) {
            ArrayList<CoordinateXY> newCoordinates = new ArrayList<>();
            double coeffecient = Double.parseDouble(coef.getText());
            for (RDFNode d : g.getVertices()) {
                double x = layout.getX(d) * coeffecient;
                double y = layout.getY(d) * coeffecient;
                CoordinateXY cor = new CoordinateXY(x, y);
                newCoordinates.add(cor);
            }
            ManipulateDocument.WriteTextFile(newCoordinates, new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "properties" + File.separator + "Coordonee.txt");

        } else {
            int iter = 0;
            for (RDFNode vertex : g.getVertices()) {
                layout.setLocation(vertex, coordinates.get(iter).X, coordinates.get(iter).Y);
                iter++;
            }
        }


        final ScalingControl scaler = new CrossoverScalingControl();

        JButton plus = new JButton("+");
        plus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1.1f, vv.getCenter());
            }
        });
        JButton minus = new JButton("-");
        minus.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                scaler.scale(vv, 1 / 1.1f, vv.getCenter());
            }
        });
        coefButtom.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<CoordinateXY> coordinates = new ArrayList<>();
                    double coeffecient = Double.parseDouble(coef.getText());
                    for (RDFNode d : g.getVertices()) {
                        double x = layout.getX(d) * coeffecient;
                        double y = layout.getY(d) * coeffecient;
                        CoordinateXY cor = new CoordinateXY(x, y);
                        coordinates.add(cor);
                    }
                    int iter = 0;
                    for (RDFNode vertex : g.getVertices()) {
                        layout.setLocation(vertex, coordinates.get(iter).X, coordinates.get(iter).Y);
                        iter++;

                    }
                    ManipulateDocument.WriteTextFile(coordinates, new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "properties" + File.separator + "Coordonee.txt");
                    frame.getContentPane().removeAll();
                    Container content = frame.getContentPane();
                    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                    GraphVisualizations dd = new GraphVisualizations(g, vertexToSee, edgesToSee, coordinates, coef.getText(), frame);
                    content.add(dd);
                    frame.getContentPane().repaint();
                    frame.getContentPane().revalidate();
                    frame.revalidate();
                    frame.repaint();
                    // frame.getContentPane().add(viz);
                    frame.pack();
                    frame.setLocationRelativeTo(null);


                    frame.setVisible(true);
                } catch (IOException ex) {
                    Logger.getLogger(GraphVisualizations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        JButton save = new JButton("Save positions");
        save.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    ArrayList<CoordinateXY> coordinates = new ArrayList<>();
                    for (RDFNode d : g.getVertices()) {
                        double x = layout.getX(d);
                        double y = layout.getY(d);
                        CoordinateXY cor = new CoordinateXY(x, y);
                        coordinates.add(cor);
                    }
                    ManipulateDocument.WriteTextFile(coordinates, new File(".").getCanonicalPath() + File.separator + "data" + File.separator + "properties" + File.separator + "Coordonee.txt");
                } catch (IOException ex) {
                    Logger.getLogger(GraphVisualizations.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });

        JPanel scaleGrid = new JPanel(new GridLayout(1, 0));
        JPanel scaleGridCoef = new JPanel(new GridLayout(1, 0));
        scaleGrid.setBorder(BorderFactory.createTitledBorder("Zoom"));
        scaleGridCoef.setBorder(BorderFactory.createTitledBorder("Zoom with a Coefficient"));
        JPanel controls = new JPanel();
        scaleGrid.add(plus);
        scaleGrid.add(minus);
        scaleGridCoef.add(coef);
        scaleGridCoef.add(coefButtom);
        // controls.add(radial);
        controls.add(save);
        controls.add(scaleGridCoef);
        controls.add(scaleGrid);

        controls.add(modeBox);

        content.add(controls, BorderLayout.SOUTH);

        frame.setVisible(true);

    }

    public static class CoordinateXY {
        // pour la creation des cordoonées de chaque noeud, 
    	private double X;
    	private double Y;

        public double getX() {
            return X;
        }

        public double getY() {
            return Y;
        }

        public CoordinateXY(double x, double y) {
            X = x;
            Y = y;
        }
    }

    /**
     * a driver for this demo
     */
    public static JFrame main(Graph<RDFNode, Statement> g, String nom, final Collection vertexToSee, JFrame frame, final ArrayList<Statement> edgesToSee, ArrayList<CoordinateXY> coordinates) throws IOException {
        // JFrame frame = new JFrame();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        // frame.setBounds(0,0,screenSize.width, screenSize.height-800);
        frame.setPreferredSize(new java.awt.Dimension(screenSize.width, screenSize.height - 50));
        frame.getContentPane().removeAll();
        Container content = frame.getContentPane();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        GraphVisualizations dd = new GraphVisualizations(g, vertexToSee, edgesToSee, coordinates, "1.0", frame);
        content.add(dd);
        frame.getContentPane().repaint();
        frame.getContentPane().revalidate();
        frame.revalidate();
        frame.repaint();
        // frame.getContentPane().add(viz);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        return frame;

    }
}
