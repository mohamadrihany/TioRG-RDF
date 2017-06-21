/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.uvsq.adam.tiorg.views;

import fr.uvsq.adam.preprocessing.GraphCreation;
import edu.uci.ics.jung.graph.Graph;
import fr.uvsq.adam.processings.ReadXMLFile.MyElementWithString;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;

import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static fr.uvsq.adam.tiorg.TioRG.TIORG_APP;

/**
 * @author houk
 */
public class PreprocessingFrame extends javax.swing.JDialog
{
	private static final long serialVersionUID = 1L;

	
	private Graph<RDFNode,Statement> globalGraph;
	private String poids;
    private String optimisations;
	
	private static int I_NAME = 0;
	private static int I_CRITERIA = 1;
	private static int I_VALUE = 2;
	private static int I_COUNT = 3;
	private static int I_EXEMPLE = 4;

	public static boolean fullGraph = true;
	
	private JTable tablePredicates;
	private JScrollPane scrollCriterion;
	private javax.swing.JPanel jPanel2;
	private javax.swing.JButton AnnulerButton;
	private javax.swing.JButton ValiderButton;
	private javax.swing.JButton VisualiserButton;
	private JTabbedPane tabbedPane;
	private JPanel panelParams;
	private JLabel optimisationLabel;
	private JTextField optimisationText;
	private JLabel poidsLabel;
	private JTextField poidsText;
	private JScrollPane scrollPatterns;
	private JTable tablePatterns;
	private JPanel panel;
	private JSplitPane splitPane;
	private JButton btnAddPattern;
	private PatternSearch patternSearch;
	
	private int dialogResult = JOptionPane.CANCEL_OPTION;
	private JButton btnRemoveQuery;
    
    public int getDialogResult()
	{
		return dialogResult;
	}
    
    public ArrayList<Properties> getPredicates()
    {
    	ArrayList<Properties> array = new ArrayList<Properties>();

    	for(int i = 0; i < getTablePredicates().getRowCount(); i++)
		{
    		Properties vals = new Properties();
    		Object obj = getTablePredicates().getValueAt(i, I_CRITERIA);
    		if(obj != null && !obj.toString().equals("Unchanged"))
    		{
    			if( obj.toString().equals("Having value") )
    			{
    				Object val = getTablePredicates().getValueAt(i, I_VALUE);
    				if(val != null && !obj.toString().trim().isEmpty())
    				{
    					vals.put(ProjectManager.PREDICATE_CRITERIA, obj.toString());
    					vals.put(ProjectManager.PREDICATE_VALUE, val.toString());
    				}
    			}
    			else
    				vals.put(ProjectManager.PREDICATE_CRITERIA, obj.toString());
    		}
    		
    		if( !vals.isEmpty() )
    		{
    			vals.put(ProjectManager.PREDICATE_NAME, getTablePredicates().getValueAt(i, I_NAME).toString());
    			array.add(vals);
    		}
		}
			
    	return array;
    }
    
    public ArrayList<String> getQueries()
    {
    	ArrayList<String> array = new ArrayList<String>();

    	for(int i = 0; i < getTablePatterns().getRowCount(); i++)
		{
    		Object obj = getTablePatterns().getValueAt(i, 0);
    		if(obj != null && obj.toString().trim().length() > 0)
    		{
    			array.add( obj.toString().trim() );
    		}
		}
			
    	return array;
    }
    
	public Double getPoids()
	{
		try
		{
			return Double.parseDouble(poidsText.getText());
		}
		catch(Exception e)
		{
		}
		
		return 0.2;
	}

	public Double getOptimisations()
	{
		try
		{
			return Double.parseDouble(optimisationText.getText());
		}
		catch(Exception e)
		{
		}
		
		return 0.1;
	}

	/**
	 * Creates new form PreprocessingFrame
	 */
	public PreprocessingFrame(Graph<RDFNode,Statement> globalGraph, Properties predicates, List<String> queries, String poids, String optimisations) throws IOException
	{
		this.poids = poids;
        this.optimisations = optimisations;
        this.globalGraph = globalGraph;
        
		setPreferredSize(new Dimension(600, 300));
		setMinimumSize(new Dimension(500, 300));
		setLocationRelativeTo(null);		
		initComponents();
		
		setPredicates(predicates);
		setQueries(queries);
	}
	
	private  void setPredicates(Properties predicates) throws IOException
	{
		if(globalGraph != null)
		{
			String edge = null;
			Integer value = null;
			HashMap<String,Integer> map = new HashMap<String,Integer>();
			HashMap<String,String> exemples = new HashMap<String,String>();
			for(Statement stm : globalGraph.getEdges())
			{
				edge = stm.getPredicate().getLocalName();
				value = map.putIfAbsent(edge, 1);
				if(value == null)
				{
					Triple triple = stm.asTriple();
					String subject = null;
					if( triple.getSubject().isLiteral() )
						subject = triple.getSubject().toString();
					else
						subject = triple.getSubject().getLocalName();
					String object = null;
					if( triple.getObject().isLiteral() )
						object = triple.getObject().getLiteralValue().toString();
					else
						object = triple.getObject().getLocalName();
					exemples.put(edge, subject+" > "+triple.getPredicate().getLocalName()+" > "+object);
				}
				else
					map.put(edge, value+1);
			}
			
			Object[] row = new Object[5];
			if( !map.isEmpty() )
			{
				DefaultTableModel tableModel = (DefaultTableModel)getTablePredicates().getModel();
				for(Map.Entry<String,Integer> entry : map.entrySet())
				{
					String criteria = "Unchanged";
					String criteriaValue = null;
					if(predicates != null)
					{
						Properties predicate = (Properties)predicates.get( entry.getKey() );
						if(predicate != null)
						{
							String txt = predicate.getProperty(ProjectManager.PREDICATE_CRITERIA);
							if(txt != null)
							{
								criteria = txt;
								txt = predicate.getProperty(ProjectManager.PREDICATE_VALUE);
								if(txt != null)
									criteriaValue = txt;
							}
						}
					}

					Arrays.fill(row,  null);
					row[I_NAME] = entry.getKey();
					row[I_CRITERIA] = criteria;
					row[I_VALUE] = criteriaValue;
					row[I_COUNT] = entry.getValue();
					row[I_EXEMPLE] = exemples.get(entry.getKey());
					tableModel.addRow(row);
				}
			}
		}
			//
			/*ReadXMLFile.TripleValorisation pairValorisation;

			pairValorisation = ReadXMLFile.GetLinksWeight(
					new File(".").getCanonicalPath() + File.separator + "data" + File.separator
							+ "properties" + File.separator + "linkImportance.xml");

			ArrayList<ReadXMLFile.MyElement> weightsList = pairValorisation.weightsList;

			for(String eage : eages)
			{

				int weight = 1;
				if(MainWindow.proc > 0)
				{
					weight = getWeight(weightsList, eage);
				}

				ArrayList column = new ArrayList();
				column.add(eage);
				column.add(Integer.toString(weight));
				//tableModel.addRow(column.toArray());
			}
			
			//
			ReadXMLFile.TripleValorisation pairCaractéristique;

			pairCaractéristique = ReadXMLFile.GetLinksWeight(
					new File(".").getCanonicalPath() + File.separator + "data" + File.separator
							+ "properties" + File.separator + "linkCaracteristique.xml");
			ArrayList<MyElementWithString> fusionFeaturesList = pairCaractéristique.fusionList;
			ArrayList<MyElementWithString> regroupedFeaturesList = pairCaractéristique.sharedList;
			//
			for(String eage : eages)
			{

				ArrayList column = new ArrayList();
				column.add(eage);
				if(getFusionOrShared(fusionFeaturesList, eage))
				{
					column.add("Groupe the Resources Related by this Property");
					// column.add("Grouper les ressources liées par ce prédicat");
				}
				else if(getFusionOrShared(regroupedFeaturesList, eage))
				{
					column.add("Groupe a Set of Resources Having the Same Value");
				}
				else
				{
					column.add("Unchanged");
				}
				//tableModel.addRow(column.toArray());

			}*/
	}
	
	private  void setQueries(List<String> queries) throws IOException
	{
		if(globalGraph != null)
		{
			Object[] row = new Object[1];
			if(queries != null && !queries.isEmpty())
			{
				DefaultTableModel tableModel = (DefaultTableModel)getTablePatterns().getModel();
				for(String query : queries)
				{
					row[0] = query;
					tableModel.addRow(row);
				}
			}
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form. WARNING: Do NOT
	 * modify this code. The content of this method is always regenerated by the Form Editor.
	 */
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	/*
	 * private void initComponents() { globalPanel = new javax.swing.JPanel(); jTabbedPane1 = new
	 * javax.swing.JTabbedPane(); jPanel2 = new javax.swing.JPanel(); VisualiserButton = new
	 * javax.swing.JButton(); AnnulerButton = new javax.swing.JButton(); ValiderButton = new
	 * javax.swing.JButton();
	 * setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE); setTitle(
	 * "Critères sémantiques de clustering"); globalPanel.setAutoscrolls(true);
	 * predicatesTableCaracteristique = new javax.swing.JTable(); predicatesTableValorisation = new
	 * javax.swing.JTable(); jTabbedPane1.setAutoscrolls(true); jPanel2.setBorder(
	 * javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
	 * VisualiserButton.setText("Visualize the Resulting Graph");
	 * VisualiserButton.addActionListener(new java.awt.event.ActionListener() { public void
	 * actionPerformed(java.awt.event.ActionEvent evt) { VisualiserButtonActionPerformed(evt); } });
	 * AnnulerButton.setText("Cancel"); AnnulerButton.addActionListener(new
	 * java.awt.event.ActionListener() { public void actionPerformed(java.awt.event.ActionEvent evt)
	 * { AnnulerButtonActionPerformed(evt); } }); ValiderButton.setText("Save");
	 * ValiderButton.addActionListener(new java.awt.event.ActionListener() { public void
	 * actionPerformed(java.awt.event.ActionEvent evt) { ValiderButtonActionPerformed(evt); } });
	 * javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
	 * jPanel2.setLayout(jPanel2Layout); jPanel2Layout.setHorizontalGroup(jPanel2Layout
	 * .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	 * .addGroup(jPanel2Layout.createSequentialGroup() .addContainerGap(103,
	 * Short.MAX_VALUE).addComponent(VisualiserButton) .addGap(37, 37,
	 * 37).addComponent(AnnulerButton).addGap(29, 29, 29)
	 * .addComponent(ValiderButton).addContainerGap())); jPanel2Layout.setVerticalGroup(
	 * jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	 * .addGroup(jPanel2Layout.createSequentialGroup()
	 * .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
	 * .addGroup(jPanel2Layout .createParallelGroup( javax.swing.GroupLayout.Alignment.BASELINE)
	 * .addComponent(ValiderButton).addComponent(AnnulerButton) .addComponent(VisualiserButton))));
	 * javax.swing.GroupLayout globalPanelLayout = new javax.swing.GroupLayout(globalPanel);
	 * globalPanel.setLayout(globalPanelLayout);
	 * globalPanelLayout.setHorizontalGroup(globalPanelLayout
	 * .createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
	 * .addGroup(globalPanelLayout.createSequentialGroup().addContainerGap()
	 * .addGroup(globalPanelLayout .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	 * .addGroup(globalPanelLayout.createSequentialGroup() .addGap(0, 0, Short.MAX_VALUE)
	 * .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 488,
	 * javax.swing.GroupLayout.PREFERRED_SIZE) .addContainerGap())
	 * .addGroup(globalPanelLayout.createSequentialGroup() .addComponent(jPanel2,
	 * javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
	 * javax.swing.GroupLayout.PREFERRED_SIZE) .addGap(0, 0, Short.MAX_VALUE)))));
	 * globalPanelLayout.setVerticalGroup(globalPanelLayout
	 * .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	 * .addGroup(globalPanelLayout.createSequentialGroup() .addContainerGap(37, Short.MAX_VALUE)
	 * .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 200,
	 * javax.swing.GroupLayout.PREFERRED_SIZE)
	 * .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED) .addComponent(jPanel2,
	 * javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
	 * javax.swing.GroupLayout.PREFERRED_SIZE) .addGap(7, 7, 7))); PredicateTable
	 * predicatesPanelValorisation = new PredicateTable(true);
	 * predicatesPanelValorisation.setOpaque(true); JScrollPane predicatesList = (JScrollPane)
	 * predicatesPanelValorisation.getComponent(0); JViewport predicatesViewport =
	 * predicatesList.getViewport(); predicatesTableValorisation = (JTable)
	 * predicatesViewport.getView(); PredicateTableModel tableModel = (PredicateTableModel)
	 * predicatesTableValorisation .getModel(); int num = tableModel.getRowCount() - 1; for(int i =
	 * 0; i <= num; i++) { tableModel.removeRow(0); } if(!eages.isEmpty()) { for(String eage :
	 * eages) { ArrayList column = new ArrayList(); column.add(eage); column.add("1");
	 * tableModel.addRow(column.toArray()); } } PredicateTable predicatesPanelPartage = new
	 * PredicateTable(false); JScrollPane clustersList = (JScrollPane)
	 * predicatesPanelPartage.getComponent(0); JViewport clustersViewport =
	 * clustersList.getViewport(); predicatesTableCaracteristique = (JTable)
	 * clustersViewport.getView(); PredicateTableModel tableModel2 = (PredicateTableModel)
	 * predicatesTableCaracteristique .getModel(); int num2 = tableModel2.getRowCount() - 1; for(int
	 * i = 0; i <= num2; i++) { tableModel2.removeRow(0); } if(!eages.isEmpty()) { for(String eage :
	 * eages) { ArrayList column = new ArrayList(); column.add(eage); column.add("Aucun");
	 * tableModel2.addRow(column.toArray()); } } predicatesPanelPartage.setOpaque(true);
	 * predicatesPanelValorisation.setVisible(true); predicatesPanelPartage.setVisible(true);
	 * predicatesPanelValorisation.setAutoscrolls(true);
	 * predicatesPanelPartage.setAutoscrolls(true); jTabbedPane1.addTab("Pondération des prédicats",
	 * predicatesPanelValorisation); jTabbedPane1.addTab("Critères de regroupement",
	 * predicatesPanelPartage); javax.swing.GroupLayout layout = new
	 * javax.swing.GroupLayout(getContentPane()); getContentPane().setLayout(layout);
	 * layout.setHorizontalGroup(
	 * layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(
	 * javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup() .addContainerGap()
	 * .addComponent(globalPanel, javax.swing.GroupLayout.PREFERRED_SIZE,
	 * javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
	 * .addContainerGap())); layout.setVerticalGroup(layout
	 * .createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
	 * .addGroup(layout.createSequentialGroup().addContainerGap() .addComponent(globalPanel,
	 * javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE,
	 * javax.swing.GroupLayout.PREFERRED_SIZE)
	 * .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))); pack(); }
	 */// </editor-fold>//GEN-END:initComponents
	
	

	private void initComponents() throws IOException
	{

		setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
		setTitle("Clustering Parameters");
		
		getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

		
		jPanel2 = new javax.swing.JPanel();
		jPanel2.setPreferredSize(new Dimension(400, 30));
		jPanel2.setMaximumSize(new Dimension(32767, 30));
		jPanel2.setMinimumSize(new Dimension(400, 30));
		
		
		VisualiserButton = new javax.swing.JButton();
		VisualiserButton.setVisible(false);
		VisualiserButton.setVerticalAlignment(SwingConstants.TOP);
		VisualiserButton.setText("Visualize the Resulting Graph");
		VisualiserButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				VisualiserButtonActionPerformed(evt);
			}
		});
		
		AnnulerButton = new javax.swing.JButton();
		AnnulerButton.setVerticalAlignment(SwingConstants.TOP);
		AnnulerButton.setText("Cancel");
		AnnulerButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				AnnulerButtonActionPerformed(evt);
			}
		});
		
		ValiderButton = new javax.swing.JButton();
		ValiderButton.setVerticalAlignment(SwingConstants.TOP);
		ValiderButton.setText("Apply");
		ValiderButton.addActionListener(new java.awt.event.ActionListener()
		{
			public void actionPerformed(java.awt.event.ActionEvent evt)
			{
				ValiderButtonActionPerformed(evt);
			}
		});

		javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
		jPanel2Layout.setHorizontalGroup(
			jPanel2Layout.createParallelGroup(Alignment.TRAILING)
				.addGroup(jPanel2Layout.createSequentialGroup()
					.addContainerGap()
					.addComponent(VisualiserButton)
					.addPreferredGap(ComponentPlacement.RELATED, 241, Short.MAX_VALUE)
					.addComponent(AnnulerButton)
					.addGap(30)
					.addComponent(ValiderButton)
					.addContainerGap())
		);
		jPanel2Layout.setVerticalGroup(
			jPanel2Layout.createParallelGroup(Alignment.CENTER)
				.addGroup(jPanel2Layout.createSequentialGroup()
					.addGap(0, 0, Short.MAX_VALUE)
					.addGroup(jPanel2Layout.createParallelGroup(Alignment.BASELINE)
						.addComponent(ValiderButton)
						.addComponent(AnnulerButton)
						.addComponent(VisualiserButton))
					.addGap(23))
		);
		jPanel2.setLayout(jPanel2Layout);
		
		optimisationLabel = new JLabel();
		optimisationLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		optimisationLabel.setBounds(15, 66, 160, 14);
		optimisationLabel.setText("Cluster Expansion Threshold");
		
		optimisationText = new JTextField();
		optimisationText.setBorder(new LineBorder(new Color(171, 173, 179)));
		optimisationText.setBounds(185, 63, 50, 20);
		optimisationText.setText(optimisations);
		
		poidsLabel = new JLabel();
		poidsLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		poidsLabel.setBounds(15, 31, 160, 14);
		poidsLabel.setText(" Clustering Threshold");
		
		poidsText = new JTextField();
		poidsText.setBorder(new LineBorder(new Color(171, 173, 179)));
		poidsText.setBounds(185, 28, 50, 20);
		poidsText.setText(poids);
		
		panelParams = new JPanel();
		panelParams.setPreferredSize(new Dimension(245, 235));
		panelParams.setLayout(null);
		panelParams.add(optimisationLabel);
		panelParams.add(optimisationText);
		panelParams.add(poidsLabel);
		panelParams.add(poidsText);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.addTab("Thresholds", null, panelParams, null);
		tabbedPane.addTab("Match by Property", null, getScrollCriterion(), null);
		tabbedPane.addTab("Match by Query", null, getSplitPane(), null);
		
		getContentPane().add(tabbedPane);
		getContentPane().add(jPanel2);
		
		pack();
	}

	private JSplitPane getSplitPane()
	{
		if(splitPane == null)
		{
			splitPane = new JSplitPane();
			splitPane.setDividerSize(0);
			splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			btnAddPattern = new JButton("Add query");
			btnAddPattern.addActionListener(
				new ActionListener() 
				{
					public void actionPerformed(ActionEvent arg0) 
					{
						((DefaultTableModel)tablePatterns.getModel()).addRow(new String[]{""});
					}
			});
			btnAddPattern.setMaximumSize(new Dimension(70, 20));
			btnAddPattern.setPreferredSize(new Dimension(70, 20));
			btnAddPattern.setContentAreaFilled(false);
			btnAddPattern.setMinimumSize(new Dimension(70, 20));
			btnAddPattern.setMargin(new Insets(0, 0, 0, 0));
			btnAddPattern.setBorder(new LineBorder(new Color(0, 0, 0)));
			
			panel = new JPanel();
			FlowLayout flowLayout = (FlowLayout) panel.getLayout();
			flowLayout.setVgap(2);
			flowLayout.setAlignment(FlowLayout.LEFT);
			panel.setMinimumSize(new Dimension(10, 26));
			panel.add(btnAddPattern);
			
			splitPane.setTopComponent(panel);
			
			btnRemoveQuery = new JButton("Remove query");
			btnRemoveQuery.addActionListener(
				new ActionListener()
				{
					public void actionPerformed(ActionEvent arg0) 
					{
						int i = tablePatterns.getSelectedRow();
						if(i != -1)
						{
							((DefaultTableModel)tablePatterns.getModel()).removeRow(i);
						}
					}
				}
			);
			btnRemoveQuery.setPreferredSize(new Dimension(90, 20));
			btnRemoveQuery.setMinimumSize(new Dimension(90, 20));
			btnRemoveQuery.setMaximumSize(new Dimension(90, 20));
			btnRemoveQuery.setMargin(new Insets(0, 0, 0, 0));
			btnRemoveQuery.setContentAreaFilled(false);
			btnRemoveQuery.setBorder(new LineBorder(new Color(0, 0, 0)));
			panel.add(btnRemoveQuery);
			splitPane.setBottomComponent(getScrollPatterns());
		}
		return splitPane;
	}

	private JTable getTablePredicates()
	{
		if(tablePredicates == null)
		{
			DefaultTableModel tModel = new DefaultTableModel()
			{
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column)
				{
					if(column == I_NAME || column == I_COUNT || column == I_EXEMPLE)
						return false;
					if(column == I_VALUE)
					{
						Object value = getValueAt(row, I_CRITERIA);
						if(value != null)
							return value.equals("Having value");
					}
					return true;
				}
				
				@Override
				public void setValueAt(Object value, int row, int column)
				{
					super.setValueAt(value, row, column);
					if(column == I_CRITERIA)
					{
						if(value == null || !(value.equals("Having value")))
							super.setValueAt(null, row, I_VALUE);
					}
				}
				
				public Class<?> getColumnClass(int i)
				{
					if(i == I_COUNT)
						return Integer.class;
					return super.getColumnClass(i);
				}
			};
			
			DefaultTableColumnModel cModel = new DefaultTableColumnModel();
			
			TableColumn aColumn = new TableColumn(0);
			aColumn.setPreferredWidth(100);
			aColumn.setHeaderValue("Property");
			aColumn.setResizable(true);			
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			JComboBox<String> comboBox = new JComboBox<String>();
			comboBox.addItem("Unchanged");
			comboBox.addItem("Groupe a Set of Resources Having the Same Value");
			comboBox.addItem("Groupe the Resources Related by this Property");
			comboBox.addItem("Having value");
	        
			aColumn = new TableColumn(1);
			aColumn.setPreferredWidth(100);
			aColumn.setResizable(true);	
			aColumn.setHeaderValue("Criteria");
			aColumn.setCellEditor(new DefaultCellEditor(comboBox));
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			aColumn = new TableColumn(2);
			aColumn.setPreferredWidth(100);
			aColumn.setResizable(true);	
			aColumn.setHeaderValue("Value");
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			aColumn = new TableColumn(3);
			aColumn.setPreferredWidth(50);
			aColumn.setMaxWidth(70);
			aColumn.setHeaderValue("Count");
			aColumn.setResizable(false);
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			aColumn = new TableColumn(4);
			aColumn.setPreferredWidth(100);
			aColumn.setResizable(true);	
			aColumn.setHeaderValue("Exemple");
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			tablePredicates = new JTable(tModel, cModel)
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public String getToolTipText(MouseEvent event)
				{
					Object value = null; 
					int rowIndex = rowAtPoint(event.getPoint());
	                int colIndex = columnAtPoint(event.getPoint());

	                try 
	                {
	                    value = getValueAt(rowIndex, colIndex);
	                } 
	                catch (RuntimeException e)
	                {
	                	return super.getToolTipText(event);
	                }
	                
	                if(value != null)
	                	return value.toString();
	                
					return super.getToolTipText(event);
				}
			};
			tablePredicates.setAutoscrolls(true);
			tablePredicates.setOpaque(true);
			tablePredicates.setAutoCreateRowSorter(true);
		}
		return tablePredicates;
	}
	
	private JTable getTablePatterns()
	{
		if(tablePatterns == null)
		{
			DefaultTableModel tModel = new DefaultTableModel()
			{
				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public boolean isCellEditable(int row, int column)
				{
					return false;
				}
				
				@Override
				public void setValueAt(Object value, int row, int column)
				{
					super.setValueAt(value, row, column);
				}
			};
			
			DefaultTableColumnModel cModel = new DefaultTableColumnModel();
			
			TableColumn aColumn = new TableColumn(0);
			aColumn.setPreferredWidth(100);
			aColumn.setHeaderValue("Query");
			aColumn.setResizable(true);			
			cModel.addColumn(aColumn);
			tModel.addColumn(aColumn.getHeaderValue());
			
			tablePatterns = new JTable(tModel, cModel)
			{
				private static final long serialVersionUID = 1L;

				@Override
				public String getToolTipText(MouseEvent event)
				{
					Object value = null; 
					int rowIndex = rowAtPoint(event.getPoint());
	                int colIndex = columnAtPoint(event.getPoint());

	                try 
	                {
	                    value = getValueAt(rowIndex, colIndex);
	                } 
	                catch (RuntimeException e)
	                {
	                	return super.getToolTipText(event);
	                }
	                
	                if(value != null)
	                	return value.toString();
	                
					return super.getToolTipText(event);
				}
			};
			tablePatterns.addMouseListener(
				new MouseAdapter() 
				{
					public void mouseClicked(MouseEvent arg0) 
					{
						if(arg0.getClickCount() > 1)
						{
							int i = tablePatterns.getSelectedRow();
							if(i != -1)
							{
								getPatternSearch().setQuery( (String)tablePatterns.getValueAt(i, 0) );
				        		getPatternSearch().setVisible(true);
				        		if(patternSearch.getDialogResult() == JOptionPane.OK_OPTION)
				        		{
				        			tablePatterns.setValueAt(patternSearch.getQuery(), i, 0);
				        		}
							}
						}
					}
				}
			);
			tablePatterns.setRowHeight(30);
			tablePatterns.setAutoscrolls(true);
			tablePatterns.setOpaque(true);
		}
		return tablePatterns;
	}

	private JScrollPane getScrollCriterion()
	{
		if(scrollCriterion == null)
		{
			scrollCriterion = new JScrollPane();
			scrollCriterion.setViewportView(getTablePredicates());
		}
		return scrollCriterion;
	}
	
	private JScrollPane getScrollPatterns()
	{
		if(scrollPatterns == null)
		{
			scrollPatterns = new JScrollPane();
			scrollPatterns.setMinimumSize(new Dimension(50, 23));
			scrollPatterns.setViewportView(getTablePatterns());
		}
		return scrollPatterns;
	}
	
	private PatternSearch getPatternSearch()
    {
    	if(patternSearch == null)
    	{
    		patternSearch = new PatternSearch();
    		patternSearch.setModal(true);
    	}
    	return patternSearch;
    }

	public boolean getFusionOrShared(ArrayList<MyElementWithString> pairs, String stat)
	{
		boolean find = false;
		for(MyElementWithString pair : pairs)
		{
			if(stat.equalsIgnoreCase(pair.link))
			{
				find = true;
				break;
			}
		}
		return find;
	}

	private void VisualiserButtonActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_VisualiserButtonActionPerformed
		try
		{
			ArrayList<MyElementWithString> sharedFeaturesList = new ArrayList<>();
			ArrayList<MyElementWithString> fusionFeaturesList = new ArrayList<>();
			ArrayList<MyElementWithString> valueFeaturesList = new ArrayList<>();

			for(int i = 0; i < getTablePredicates().getRowCount(); i++)
			{

				if(getTablePredicates().getValueAt(i, I_CRITERIA).toString()
						.equalsIgnoreCase("Groupe a Set of Resources Having the Same Value"))
				{
					MyElementWithString myelement = new MyElementWithString("Ajout d'arcs",
							getTablePredicates().getValueAt(i, I_NAME).toString());
					sharedFeaturesList.add(myelement);

				}
				else if(getTablePredicates().getValueAt(i, I_CRITERIA).toString()
						.equalsIgnoreCase("Groupe the Resources Related by this Property"))
				{
					MyElementWithString myelement = new MyElementWithString("Fusion",
							getTablePredicates().getValueAt(i, I_NAME).toString());
					fusionFeaturesList.add(myelement);
				}
				else if(getTablePredicates().getValueAt(i, I_CRITERIA).toString()
						.equalsIgnoreCase("Having value"))
				{
					String[] arr = getTablePredicates().getValueAt(i, I_VALUE).toString().split("[;]");
					for(String value : arr)
					{
						valueFeaturesList.add( 
								new MyElementWithString(value, getTablePredicates().getValueAt(i, I_NAME).toString())
							);
					}
				}
			}

			// if (instancesRadioButton.isSelected()) {
			// Graph<RDFNode, Statement> globalGraphInter =
			// GraphCreation.graphToClusterWithStatement(dataGraph, fusionFeaturesList,
			// sharedFeaturesList);
			//
			// FirstGraphVisualization firstframeInt = new FirstGraphVisualization(globalGraphInter,
			// "Visualization of the Resulting Graph", true);
			// firstframeInt.setVisible(true); //necessary as of 1.3
			// jDesktopPane.add(firstframeInt);
			//
			// firstframeInt.setSelected(true);
			// } else {
			Graph<RDFNode,Statement> globalGraphInter = GraphCreation.graphToClusterWithStatement(
					globalGraph, fusionFeaturesList, sharedFeaturesList, valueFeaturesList);
			FirstGraphVisualization firstframeInt = new FirstGraphVisualization(globalGraphInter,
					"Visualization of the Resulting Graph", true, null);
			firstframeInt.setVisible(true); // necessary as of 1.3
			TIORG_APP.getMainWindow().getDesktop().add(firstframeInt);
			firstframeInt.setSelected(true);

		}
		catch(PropertyVetoException | ClassNotFoundException | HeadlessException | IOException ex)
		{
			Logger.getLogger(PreprocessingFrame.class.getName()).log(Level.SEVERE, null, ex);
		}

	}// GEN-LAST:event_VisualiserButtonActionPerformed

	private void AnnulerButtonActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_AnnulerButtonActionPerformed
		dialogResult = JOptionPane.CANCEL_OPTION;
		setVisible(false);
	}// GEN-LAST:event_AnnulerButtonActionPerformed

	private void ValiderButtonActionPerformed(java.awt.event.ActionEvent evt)
	{// GEN-FIRST:event_ValiderButtonActionPerformed
/*
		ArrayList<Paire> listPairesEageCaractéristique = new ArrayList();
		ArrayList<Paire> listPairesEageValorisation = new ArrayList();
		try
		{

			for(int i = 0; i < getTablePredicates().getRowCount(); i++)
			{
				Paire paire = new Paire(getTablePredicates().getValueAt(i, 0).toString(),
						getTablePredicates().getValueAt(i, 2).toString());
				listPairesEageValorisation.add(paire);
			}
			ManipulateDocument.CreationPaire(listPairesEageValorisation,
					new File(".").getCanonicalPath() + File.separator + "data" + File.separator
							+ "properties" + File.separator + "linkImportance.xml");

		}
		catch(IOException ex)
		{
			Logger.getLogger(PreprocessingFrame.class.getProjectName()).log(Level.SEVERE, null, ex);
		}
		try
		{

			for(int i = 0; i < getTablePredicates().getRowCount(); i++)
			{

				if(getTablePredicates().getValueAt(i, 2).toString()
						.equalsIgnoreCase("Unchanged"))
				{
					Paire paire = new Paire(
							getTablePredicates().getValueAt(i, 0).toString(),
							getTablePredicates().getValueAt(i, 1).toString());

					listPairesEageCaractéristique.add(paire);
				}
				else if(getTablePredicates().getValueAt(i, 2).toString()
						.equalsIgnoreCase("Groupe a Set of Resources Having the Same Value"))
				{
					Paire paire = new Paire(
							getTablePredicates().getValueAt(i, 0).toString(),
							"Ajout d'arcs");
					listPairesEageCaractéristique.add(paire);

				}
				else if(getTablePredicates().getValueAt(i, 2).toString()
						.equalsIgnoreCase("Groupe the Resources Related by this Property"))
				{
					Paire paire = new Paire(
							getTablePredicates().getValueAt(i, 0).toString(), "Fusion");
					listPairesEageCaractéristique.add(paire);
				}
			}
			ManipulateDocument.CreationPaire(listPairesEageCaractéristique,
					new File(".").getCanonicalPath() + File.separator + "data" + File.separator
							+ "properties" + File.separator + "linkCaracteristique.xml");

		}
		catch(IOException ex)
		{
			Logger.getLogger(PreprocessingFrame.class.getProjectName()).log(Level.SEVERE, null, ex);
		}
		MainWindow.weightedGraph = true;
		MainWindow.proc++;
		this.dispose();
*/		
		dialogResult = JOptionPane.OK_OPTION;
		setVisible(false);
	}// GEN-LAST:event_ValiderButtonActionPerformed

}
