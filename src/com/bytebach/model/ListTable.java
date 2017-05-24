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
        for (List<Value> row :
                rows) {
            if (Arrays.equals(new TableRow(row).getKeyValues().toArray(), keys)) return row;
        }
        return null;
    }
    
    @Override
    public void delete(Value... keys) {
        for (int i = 0; i < rows.size(); i++) {
            List<Value> row = rows.get(i).getKeyValues();
            if (Arrays.equals(row.toArray(), keys)) rows.remove(i);
        }
    }
    
    /**
     * Defines a list of table rows, used as the list of rows in a table
     * @author Askes
     */
    public class TableRowList extends AbstractList<List<Value>> implements List<List<Value>> {
        
        List<TableRow> rows;
        
        /**
         * Create new row list with given rows
         * @param rows the rows in this row list
         */
        public TableRowList(List<TableRow> rows) {
            this.rows = rows;
        }
        
        /**
         * Creates a new row list with default values, i.e. no rows
         */
        public TableRowList() {
            this.rows = new ArrayList<>();
        }
    
        @Override
        public List<Value> remove(int index) {
            return rows.remove(index);
        }
    
        @Override
        public boolean add(List<Value> row) {
            TableRow toAdd= new TableRow(row);
    
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (field.isKey()) {
                    for (TableRow tableRow : rows) {
                        if (tableRow.get(i).equals(toAdd.get(i))) throw new InvalidOperation("Cannot add row with same key field");
                    }
                }
            }
            
            return rows.add(new TableRow(row));
        }
        
        @Override
        public TableRow get(int index) {
            return rows.get(index);
        }
        
        @Override
        public int size() {
            return rows.size();
        }
        
    }
    
    /**
     * Defines a row in the table as a list of values
     * @author Matthew Askes
     */
    public class TableRow extends AbstractList<Value> implements List<Value> {
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
            values = new ArrayList<>(fields.size());
        }
    
        /**
         * returns all of the values that are keys
         * @return a list of keys, or null if no keys found
         */
        public List<Value> getKeyValues(){
            List<Value> keys = new ArrayList<>();
            for (int i = 0; i < fields.size(); i++) {
                Field field = fields.get(i);
                if (field.isKey()) {
                    keys.add(values.get(i));
                }
            }
            return keys.size() == 0 ? null : keys;
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
