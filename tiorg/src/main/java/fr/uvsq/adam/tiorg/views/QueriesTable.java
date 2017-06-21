package fr.uvsq.adam.tiorg.views;

/*
 * TableSortDemo.java requires no other files.
 */
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.util.*;
import javax.swing.*;
import javax.swing.table.AbstractTableModel;

public class QueriesTable extends JPanel {

    private boolean DEBUG = false;

    public QueriesTable() {
        super(new GridLayout(1, 0));

        JTable table = new JTable(new QueriesTableModel(new ArrayList())){
            @Override
             public String getToolTipText(MouseEvent e) {
                String tip = null;
                java.awt.Point p = e.getPoint();
                int rowIndex = rowAtPoint(p);
                int colIndex = columnAtPoint(p);

                try {
                    //comment row, exclude heading
                       tip=getValueAt(rowIndex, 0).toString();
                     // tip = Integer.toString(rowIndex);
                    
                } catch (RuntimeException e1) {
                    //catch null pointer exception if mouse is over an empty line
                }

                return tip;
            }
        };
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    public class QueriesTableModel extends AbstractTableModel {

        private List<Object[]> rows;
        private Class[] columnTypes = new Class[]{
        		Integer.class, String.class, Integer.class, String.class, Integer.class
        };
        private String[] columnNames = {"ID", "Result", "Score"};

        public QueriesTableModel(List<Object[]> data) {
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

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return columnTypes[columnIndex];
        }
    }
}
//**