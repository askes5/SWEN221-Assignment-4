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
    private final List<TableRow> rows;
    
    /**
     * creates a new table with no rows
     * @param name the name of the table
     * @param fields the fields in the table
     */
    public ListTable(String name, FieldList fields) {
        this.name = name;
        this.fields = fields;
        this.rows = new ArrayList<>();
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
        return new ArrayList<>(rows);
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
    
    
    /**
     * Defines a row in the table as a list of values
     * @author Matthew Askes
     */
    public class TableRow extends AbstractList<Value> implements List<Value>{
        private List<Value> values;
        
        /**
         * create a new row from given values
         * @param values the values in this row
         */
        public TableRow(List<Value> values) {
            this.values = values;
        }
        
        /**
         * create a new row with no values
         */
        public TableRow() {
            values = new ArrayList<>();
        }
        
        @Override
        public Value set(int index, Value element) {
            if (fields.get(index).isKey()) throw new InvalidOperation("Cannot modify a field that is a key field");
            return values.set(index, element);
        }
        
        @Override
        public int size() {
            return values.size();
        }
        
        @Override
        public Value get(int index) {
            return values.get(index);
        }
    }
    
}
