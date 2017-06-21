package fr.uvsq.adam.tiorg.views;

/*
 * houk
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

public class PredicateTable extends JPanel {

    private boolean DEBUG = false;

    public PredicateTable(boolean b) {
        super(new GridLayout(1, 0));
JTable table;
     
        if (b) {
              table = new JTable(new PredicateTableModel(new ArrayList()));
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
            setUpPoidsColumnTrue(table.getColumnModel().getColumn(1));
        } else {
            table = new JTable(new PredicateTableModel2(new ArrayList()));
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);
            setUpPoidsColumnFalse(table.getColumnModel().getColumn(1));
        }

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    private void setUpPoidsColumnTrue(TableColumn poidsColumn) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("1");
        comboBox.addItem("2");
        comboBox.addItem("3");


        poidsColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Choosing the valorization of the property");
        poidsColumn.setCellRenderer(renderer);
    }

    private void setUpPoidsColumnFalse(TableColumn PartageColumn) {
        //Set up the editor for the sport cells.
        JComboBox comboBox = new JComboBox();
        comboBox.addItem("Unchanged");
        comboBox.addItem("Groupe a Set of Resources Having the Same Value");
        comboBox.addItem("Groupe the Resources Related by this Property");


        PartageColumn.setCellEditor(new DefaultCellEditor(comboBox));

        //Set up tool tips for the sport cells.
        DefaultTableCellRenderer renderer = new DefaultTableCellRenderer();
        renderer.setToolTipText("Capturing User Preferences");
        PartageColumn.setCellRenderer(renderer);
    }
    
     public class PredicateTableModel2 extends AbstractTableModel {

        private List<Object[]> rows;
//        private Class[] columnTypes = new Class[]{
//            String.class, Integer.class,JComboBox.class
//        };
      
        private String[] columnNames = {"Property", "Criteria"};
 
        public PredicateTableModel2(List<Object[]> data) {
            this.rows = data;
        }

        public void addRow(Object[] row) {
            rows.add(0, row);
            fireTableRowsInserted(0, 0);
        }

        public void removeRow(int index) {
            rows.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return rows.get(row)[col];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            rows.get(rowIndex)[columnIndex] = aValue;
        }
//        @Override
//        public Class<?> getColumnClass(int columnIndex) {
//            return columnTypes[columnIndex];
//        }
    }

    public class PredicateTableModel extends AbstractTableModel {

        private List<Object[]> rows;
//        private Class[] columnTypes = new Class[]{
//            String.class, Integer.class,JComboBox.class
//        };
      
        private String[] columnNames = {"Property", "Weight"};
 
        public PredicateTableModel(List<Object[]> data) {
            this.rows = data;
        }

        public void addRow(Object[] row) {
            rows.add(0, row);
            fireTableRowsInserted(0, 0);
        }

        public void removeRow(int index) {
            rows.remove(index);
            fireTableRowsDeleted(index, index);
        }

        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 1) {
                return false;
            } else {
                return true;
            }
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            return rows.get(row)[col];
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            rows.get(rowIndex)[columnIndex] = aValue;
        }
//        @Override
//        public Class<?> getColumnClass(int columnIndex) {
//            return columnTypes[columnIndex];
//        }
    }
}