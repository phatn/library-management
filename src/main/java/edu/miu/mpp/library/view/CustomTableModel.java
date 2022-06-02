package edu.miu.mpp.library.view;

import javax.swing.table.AbstractTableModel;
import java.util.List;

class CustomTableModel extends AbstractTableModel {

    //this is a List of Object arrays
    private List<String[]> tableValues;

    public void setTableValues(List<String[]> vals) {
        if (vals == null) {
            return;
        }
        tableValues = vals;
    }

    /**
     * Implementation of the table model interface. It returns the
     * the object stored in the model at the specified indices.
     */
    public Object getValueAt(int rowIndex, int colIndex) {
        Object[] row = (Object[]) tableValues.get(rowIndex);
        return row[colIndex];
    }

    /**
     * This method is included because it is an abstract
     * method in AbstractTableModel. In this version of a table
     * model, the default method of setting values is used, so
     * the method here does not need a body.
     */
    public void setValueAt(Object val, int rowIndex, int colIndex) {
        //not used
    }

    public int getColumnCount() {
        return 0;
    }

    public int getRowCount() {
        if (tableValues == null) return 0;
        return tableValues.size();
    }
}
     