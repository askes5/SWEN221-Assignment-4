package com.bytebach.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Matthew Askes
 */
public class ListTable implements Table {
    
    private final String name;
    private final FieldList fields;
    private final TableRowList rows;
    
    /**
     * creates a new table with no rows
     * @param name the name of the table
     * @param fields the fields in the table
     */
    public ListTable(String name, FieldList fields) {
        this.name = name;
        this.fields = fields;
        this.rows = new TableRowList();
    }
    
    @Override
    public String name() {
        return name;
    }
    
    @Override
    public List<Field> fields() {
        return fields;
    }
    
    @Override
    public List<List<Value>> rows() {
        return rows;
    }
    
    @Override
    public List<Value> row(Value... keys) {
        for (TableRow row :
                rows) {
            if (Arrays.equals(row.toArray(), keys)) return row;
        }
        return null;
    }
    
    @Override
    public void delete(Value... keys) {
        for (TableRow row : rows) {
            if (Arrays.equals(row.toArray(), keys)) rows.remove(row);
        }
    }
    

    
}
