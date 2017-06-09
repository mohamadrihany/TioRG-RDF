package fr.views;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Paint;
import java.awt.Point;
import java.awt.Polygon;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComboBox;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
/*
 * @author houk
 */
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import edu.uci.ics.jung.algorithms.layout.FRLayout2;
import edu.uci.ics.jung.graph.DirectedGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.RenderContext;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.GraphMouseListener;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.Renderer.VertexLabel.Position;
import fr.jenajung.Transformers;
import fr.processings.FileToModelGraph;

@SuppressWarnings("serial")
public class GraphVisualizationJPanel extends JPanel {

	public VisualizationViewer<RDFNode, Statement> vv;
    public FRLayout2<RDFNode, Statement> layout;
    private Collection<Statement> edgesToSee;
    private Collection<RDFNode> vertexToSee;
    
    private ArrayList<RDFNode> nodeFilter;
    private ArrayList<NodePredicate> nodeTypeFilter;
    private Predicate<RDFNode> nodePredicate;
    
    private ArrayList<String> edgeTypeFilter;
    private Predicate<Statement> edgePredicate;
    
    private ArrayList<String> typesNode;
    private ArrayList<String> typesLink;
    
    private boolean nodeTargetFilter = false;
    private String nodeTextFilter = null;
    private String nodeSparqlFilter = null;
    
    private ArrayList<NodePredicate> getNodeTypeFilter()
    {
    	if(nodeTypeFilter == null)
    		nodeTypeFilter = new ArrayList<NodePredicate>();
    	return nodeTypeFilter;
    }
    
    private ArrayList<String> getEdgeTypeFilter()
    {
    	if(edgeTypeFilter == null)
    		edgeTypeFilter = new ArrayList<String>();
    	return edgeTypeFilter;
    }
    
    private Predicate<RDFNode> getNodePredicate()
	{
    	if(nodePredicate == null)
    	{
    		nodePredicate = new Predicate<RDFNode>()
			{
				public boolean evaluate(RDFNode node)
				{
					if( !getNodeFilter().isEmpty() )
						if( !getNodeFilter().contains(node) ) 
							return false;
					
					if(nodeTextFilter != null)
					{
						if( !node.isLiteral() )
						{
							if( !node.asResource().getLocalName().toUpperCase().contains(nodeTextFilter)
									&& !node.asResource().getLocalName().toUpperCase().matches(nodeTextFilter)
									)
								return false;
						}
						else if( !node.asLiteral().toString().toUpperCase().contains(nodeTextFilter)
								&& !node.asLiteral().toString().toUpperCase().matches(nodeTextFilter)
								)
							return false;
					}

					if(nodeTypeFilter != null && !nodeTypeFilter.isEmpty())
					{
						if( node.isLiteral() )
							return !nodeTypeFilter.contains(NodePredicate.literal);
		        		else if( node.isResource() )
		            	{
		            		if( nodeIsType(node) )
		            			return !nodeTypeFilter.contains(NodePredicate.type);
		            		else if( node.isURIResource() )
		            			return !nodeTypeFilter.contains(NodePredicate.uriResource);
		            		else if( node.isAnon() )
		            			return !nodeTypeFilter.contains(NodePredicate.anon);
		            		else
		            			return !nodeTypeFilter.contains(NodePredicate.resource);
		            	}
					}
					
					return true;
				}
			};
    	}
		return nodePredicate;
	}
    
    public void addNodeTypeFilter(String filter)
    {
    	if(filter != null)
    	{
    		NodePredicate predicate = NodePredicate.valueOf(filter);
    		if(predicate != null && !getNodeTypeFilter().contains(predicate))
    		{
    			getNodeTypeFilter().add(predicate);
		    	vv.invalidate();
		    	vv.repaint();
    		}
    	}
    }
    public void removeNodeTypeFilter(String filter)
    {
    	if(filter != null)
    	{
    		NodePredicate predicate = NodePredicate.valueOf(filter);
    		if(predicate != null && getNodeTypeFilter().contains(predicate))
    		{
    			getNodeTypeFilter().remove(predicate);
		    	vv.invalidate();
		    	vv.repaint();
    		}
    	}
    }
    
    public void addEdgeTypeFilter(String filter)
    {
    	if(filter != null)
    	{
    		if( !getEdgeTypeFilter().contains(filter) )
    		{
    			getEdgeTypeFilter().add(filter);
		    	vv.invalidate();
		    	vv.repaint();
    		}
    	}
    }
    public void removeEdgeTypeFilter(String filter)
    {
    	if(filter != null)
    	{
    		if( getEdgeTypeFilter().contains(filter) )
    		{
    			getEdgeTypeFilter().remove(filter);
		    	vv.invalidate();
		    	vv.repaint();
    		}
    	}
    }
    
    private Predicate<Statement> getEdgePredicate()
	{
    	if(edgePredicate == null)
    	{
    		edgePredicate = new Predicate<Statement>()
			{
				public boolean evaluate(Statement input)
				{
					if( !getNodePredicate().evaluate( input.getObject() ) 
						|| !getNodePredicate().evaluate( input.getSubject() ))
						return false;
					
					if(edgeTypeFilter != null && !edgeTypeFilter.isEmpty())
						return !edgeTypeFilter.contains( input.getPredicate().getLocalName() );
					
					return true;
				}
			};
    	}
		return edgePredicate;
	}
    
    public boolean getNodeTargetFilter()
    {
    	return nodeTargetFilter;
    }
    public void setNodeTargetFilter(boolean activate)
	{
    	nodeTargetFilter = activate;
    	if(!nodeTargetFilter)
    	{
    		getNodeFilter().clear();
    		vv.invalidate();
        	vv.repaint();
    	}
	}
    
    public void setNodeTextFilter(String filter)
	{
    	nodeTextFilter = (filter!=null && !filter.isEmpty() ? filter.toUpperCase() : null);
    	//if(!nodeTargetFilter)
    	{
    		//getNodeFilter().clear();
    		vv.invalidate();
        	vv.repaint();
    	}
	}
    
    public void setNodeSparqlFilter(String filter)
	{
    	if(nodeSparqlFilter != filter)
    	{
	    	nodeSparqlFilter = (filter!=null && !filter.isEmpty() ? filter : null);
	    	setNodeTargetFilter(false);
	    	if(nodeSparqlFilter != null)
	    	{
	    		for(RDFNode vertix : layout.getGraph().getVertices())
	        	{	
	    			try
	    			{
		        		QueryExecution exe = QueryExecutionFactory.create(nodeSparqlFilter, vertix.getModel());
		        		ResultSet set = exe.execSelect();
		        		while( set.hasNext() )
		        		{
		        			QuerySolution solution = set.next();
		        			for(String name : set.getResultVars())
		        			{
		        				//searchStatements( solution.get(name) );
		        				addNodeFilter( solution.get(name) );
		        			}
		        		}
	    			}
	    			catch(Exception ex)
	    			{
	    				JOptionPane.showMessageDialog(
	    						(Component)SwingUtilities.getRoot(GraphVisualizationJPanel.this), ex.getMessage(), "Sparql error", JOptionPane.ERROR_MESSAGE
	    						);
	    				Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
	    			}
	        		break;
	        	}
	    	}
	    	
			vv.invalidate();
	    	vv.repaint();
    	}
	}
    
    private ArrayList<RDFNode> getNodeFilter()
	{
    	if(nodeFilter == null)
    		nodeFilter = new ArrayList<RDFNode>();
		return nodeFilter;
	}
    private void addNodeFilter(RDFNode node)
	{
    	if( !getNodeFilter().contains(node) )
    		nodeFilter.add(node);
	}
    
    public ArrayList<String> getTypesNode()
	{
    	if(typesNode == null)
    		typesNode = new ArrayList<String>();
		return typesNode;
	}
    
    public ArrayList<String> getTypesLink()
	{
    	if(typesLink == null)
    		typesLink = new ArrayList<String>();
		return typesLink;
	}
    
    public void setVertexToSee(Collection<RDFNode> vertexToSee)
    {
    	this.vertexToSee = vertexToSee;
    }
    public void setEdgeToSee(Collection<Statement> edgesToSee)
    {
    	this.edgesToSee = edgesToSee;
    }

    public GraphVisualizationJPanel(final Graph<RDFNode, Statement> graph, Collection<RDFNode> nodesToSee, Collection<Statement> statementsToSee, HashMap<String,Point> coordinates, boolean cluster) throws IOException {
        MainWindow.modeMenuItem.setSelected(false);
        layout = new FRLayout2<RDFNode, Statement>(graph);
        layout.setMaxIterations(100);
        
        this.vertexToSee = nodesToSee;
        this.edgesToSee = statementsToSee;

        //vv le panel (classe jung heritée du jpanel) pour le graphe 
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        vv = new VisualizationViewer<RDFNode, Statement>(layout, new Dimension(screenSize.width - 100, screenSize.height - 150));
        
        vv.setBackground(Color.white);

        //gerer les annotations des noeuds et arcs 
        RenderContext<RDFNode, Statement> context = vv.getRenderContext();
        
        Transformers.EdgeT te = new Transformers.EdgeT();
        te.setFilters(getEdgePredicate());        
        context.setEdgeLabelTransformer(te); // property label
        
        Transformers.NodeT tn = new Transformers.NodeT();
        tn.setFilters(getNodePredicate());
        context.setVertexLabelTransformer(tn); // node label
        
        vv.getRenderer().getVertexLabelRenderer().setPosition(Position.SE);
        // affichage des labels et les couleurs des noeuds
        vv.setVertexToolTipTransformer(new ToStringLabeller<RDFNode>());
        
        vv.addGraphMouseListener(
        	new GraphMouseListener<RDFNode>() 
	        { 
	     		public void graphClicked(final RDFNode node, MouseEvent me) 
	     		{
	     			if(me.getClickCount()==2)
	     			{
	     				if(nodeTargetFilter)
		     				addWebForNode(node);
	     			}
	     		}
	     		public void graphPressed(RDFNode v, MouseEvent me) {
	     		    //System.err.println("Vertex "+v+" was pressed at ("+me.getX()+","+me.getY()+")");
	     		}
	     		public void graphReleased(RDFNode node, MouseEvent me) 
	     		{
	     			if(nodeTargetFilter && getNodeFilter().isEmpty())
	     				addWebForNode(node);
	     		}
	     		
	     		private void addWebForNode(RDFNode node)
	     		{
	     			searchStatements(node);
	     			vv.invalidate();
     	        	vv.repaint();
	     		}
	     });
        
        final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
        panel.setPreferredSize(new Dimension(screenSize.width - 40, screenSize.height - 150));
        this.add(panel);
        final DefaultModalGraphMouse<RDFNode, Statement> graphMouse = new DefaultModalGraphMouse<RDFNode, Statement>();

        vv.setGraphMouse(graphMouse);
        
        JMenuBar menu = new JMenuBar();
        menu.add(graphMouse.getModeMenu());
        panel.setCorner(menu);
        
        // **       changer la couleur des noeuds
        Transformer<RDFNode, Paint> vertexPaint = new Transformer<RDFNode, Paint>() {
            @Override
            public Paint transform(RDFNode node) 
            {
            	boolean isCurrent = false;
            	if(vertexToSee != null)
            	{
            		isCurrent = (vertexToSee.contains(node) || (node.isLiteral() && vertexToSee.contains(node.asLiteral())));
            		if( !isCurrent )
                	{
                		if(node.isLiteral())
                		{
                            String ppx = node.asLiteral().getString();
                            for(RDFNode no:vertexToSee)
                            {
                              if(no.isLiteral())
                            	  isCurrent = (no.asLiteral().getString().equalsIgnoreCase(ppx));
                              if(isCurrent)
                            	  break;
                            }
                        }
                		else
                		{
                			if(node.asResource().getLocalName() == null || node.asResource().getLocalName().isEmpty()) 
                                for (RDFNode nn : graph.getNeighbors(node)) 
                                {
    	                        	isCurrent = vertexToSee.contains(nn);
    	                        	if(isCurrent)
    	                          	  break;
                                }
                		}
                	}
            	}
            	
            	//if( !getNodePredicate().evaluate(node) )
            	//	return null;
            				
            	if( isCurrent )
            		return Color.YELLOW;

        		if(node.isLiteral())
        			return Color.CYAN;
        		else if( node.isResource() )
            	{
            		if( nodeIsType(node) )
            			return Color.RED;
            		else if( node.isURIResource() )
            			return Color.GREEN;
            		else if( node.isAnon() )
            			return Color.lightGray;
            		else
            			return Color.MAGENTA;
            	}
            	
        		return Color.PINK;
            }
        };
        vv.getRenderContext().setVertexFillPaintTransformer(vertexPaint);
        
        Transformer<RDFNode,Shape> vertexSize = new Transformer<RDFNode,Shape>(){
            public Shape transform(RDFNode node){
            	if( !getNodePredicate().evaluate(node) )
            		return new Rectangle2D.Double(0, 0, 0, 0);
            	
                // in this case, the vertex is twice as large
                if(node.isLiteral()) 
                	return new Polygon(new int[]{-15, 15, 0}, new int[]{-15, -15, 15}, 3);
                else if(node.isResource()) 
                {
                	if( nodeIsType(node) )
                		return new Ellipse2D.Double(-13, -13, 26, 26);
                	return new Rectangle2D.Double(-10, -10, 20, 20);
                }
                return new Ellipse2D.Double(-10, -10, 20, 20);
            }
        };
        vv.getRenderContext().setVertexShapeTransformer(vertexSize);
       
        Transformer<Statement, Paint> edgeFill = new Transformer<Statement, Paint>() {
            @Override
            public Paint transform(Statement i) {
            	if( !getEdgePredicate().evaluate(i) )
                		return null;
            		
                if(edgesToSee != null && edgesToSee.contains(i)) 
                    return Color.YELLOW;

                if(i.getPredicate().getLocalName().equals(EdgePredicate.sameFeature.name()) )
                	return Color.YELLOW;
                
                if(vertexToSee != null
                	&& vertexToSee.contains(i.getSubject())
                	&& vertexToSee.contains(i.getObject()))
					return Color.YELLOW;
                
                return null;
            }
        };
        vv.getRenderContext().setEdgeFillPaintTransformer(edgeFill);
        
        Transformer<Statement, Paint> edgePaint = new Transformer<Statement, Paint>() {
            @Override
            public Paint transform(Statement i) {
            	if( !getEdgePredicate().evaluate(i) )
            		return null;
            	
                if(i.getPredicate().getLocalName().equals(EdgePredicate.type.name()) )
                	return Color.RED;
                if(i.getPredicate().getLocalName().equals(EdgePredicate.subClassOf.name()) )
                	return Color.BLUE;

                return Color.BLACK;
            }
        };
        vv.getRenderContext().setEdgeDrawPaintTransformer(edgePaint);
        
        Transformer<Statement, Paint> arrowPaint = new Transformer<Statement, Paint>() {
            @Override
            public Paint transform(Statement i) {
            	if( !getEdgePredicate().evaluate(i) )
            		return null;
            	
                if(i.getPredicate().getLocalName().equals(EdgePredicate.type.name()) )
                	return Color.RED;
                if(i.getPredicate().getLocalName().equals(EdgePredicate.subClassOf.name()) )
                	return Color.BLUE;

                return Color.BLACK;
            }
        };
        vv.getRenderContext().setArrowDrawPaintTransformer(arrowPaint);
        
        Transformer<Statement, Paint> arrowFill = new Transformer<Statement, Paint>() {
            @Override
            public Paint transform(Statement i) {
            	if( !getEdgePredicate().evaluate(i) )
            		return null;
            	
                if(i.getPredicate().getLocalName().equals(EdgePredicate.type.name()) )
                	return Color.RED;
                if(i.getPredicate().getLocalName().equals(EdgePredicate.subClassOf.name()) )
                	return Color.BLUE;

                return Color.DARK_GRAY;
            }
        };
        vv.getRenderContext().setArrowFillPaintTransformer(arrowFill);

        JComboBox modeBox = graphMouse.getModeComboBox();
        modeBox.addItemListener(graphMouse.getModeListener());
        graphMouse.setMode(ModalGraphMouse.Mode.TRANSFORMING);

        if(coordinates != null && !coordinates.isEmpty())
        {
        	Point point = null;
        	String name = null;
            for(RDFNode node : graph.getVertices()) 
            {
            	if( node.isLiteral() )
            		name = node.asLiteral().toString();
            	else
            		name = node.asResource().getLocalName();
            	point = coordinates.get(name);
            	if(point != null)
            		layout.setLocation(node, point);
            }
        }
        
        String typeNode = null;
        for(RDFNode d : graph.getVertices()) 
        {
        	if( d.isResource() )
        	{
        		if( nodeIsType(d) )
        			typeNode = NodePredicate.type.name();
        		else if( d.isURIResource() )
                	typeNode = NodePredicate.uriResource.name();
        		else if( d.isAnon() )
                	typeNode = NodePredicate.anon.name();
        		else
        			typeNode = NodePredicate.resource.name();
        	}
            else if( d.isLiteral() )
            	typeNode = NodePredicate.literal.name();
            
            if( !getTypesNode().contains(typeNode) )
            	getTypesNode().add(typeNode);
        }
        
        String typeLink = null;
        for(Statement stm : graph.getEdges())
		{
        	typeLink = stm.getPredicate().getLocalName();
        	if( !getTypesLink().contains(typeLink) )
        		getTypesLink().add(typeLink);
		}
        
        layout.initialize();
    }
    
    private boolean nodeIsType(RDFNode node)
	{
		for(Statement stm : node.getModel().listStatements().toList())
		{
			if(stm.getSubject().equals(node) || stm.getObject().equals(node))
				if( stm.getPredicate().getLocalName().equals( EdgePredicate.type.name() ) )
					return true;
			
			if( stm.getObject().equals(node) )
				if( stm.getPredicate().getLocalName().equals( EdgePredicate.subClassOf.name() ) )
					return true;
		}
		return false;
	}
    
    private void searchStatements(RDFNode node)
	{
		for(Statement stm : node.getModel().listStatements().toList())
		{
			if(stm.getObject().equals(node) || stm.getSubject().equals(node))
			{
				addNodeFilter( stm.getObject() );
				addNodeFilter( stm.getSubject() );
			}
		}
	} 
    
    public HashMap<String,Point> getGraphCoordinates()
    {
    	HashMap<String,Point> xy = null;
    	if(layout != null && layout.getGraph() != null)
    	{
    		xy = new HashMap<String,Point>();
    		
    		String name = null;
    		for(RDFNode node : layout.getGraph().getVertices()) 
            {
            	if( node.isLiteral() )
            		name = node.asLiteral().toString();
            	else
            		name = node.asResource().getLocalName();
            	
            	xy.put(name, new Point((int)layout.getX(node), (int)layout.getY(node)));
            }
    	}
    	return xy;
    }
    
    public DirectedGraph<RDFNode, Statement> getVisibleSubGraph()
    {
    	ArrayList<RDFNode> nodes = new ArrayList<RDFNode>();
    	Shape zero = new Rectangle2D.Double(0, 0, 0, 0);
    	for(RDFNode node : layout.getGraph().getVertices()) 
        {
    		Shape shape = vv.getRenderContext().getVertexShapeTransformer().transform(node);
    		if( !shape.equals(zero) )
    			nodes.add(node);
        }
    	if(nodes.isEmpty() || nodes.size() == layout.getGraph().getVertexCount())
    		return null;
    	
    	Model model = ModelFactory.createDefaultModel();
    	for(Statement stm : layout.getGraph().getEdges())
    	{
    		if(nodes.contains(stm.getSubject())
    			&& nodes.contains(stm.getObject()))
    			model.add(stm.getSubject(), stm.getPredicate(), stm.getObject());
    	}
    	
    	return FileToModelGraph.ModelToGraph(model);
    }
}