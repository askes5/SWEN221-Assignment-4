package com.bytebach.model;

import java.util.List;

/**
 * @author Matthew Askes
 */
public class ArrayTable implements Table {
    
    private final String name;
    private final FieldList fields;
    private final List<ValueList> rows;
    
    /**
     * creates a new table with
     * @param name the name of the table
     * @param fields the fields in the table
     * @param rows the rows in the table
     */
    public ArrayTable(String name, FieldList fields, List<ValueList> rows) {
        this.name = name;
        this.fields = fields;
        this.rows = rows;
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
        return rows();
    }
    
    @Override
    public List<Value> row(Value... keys) {
        return null;
    }
    
    @Override
    public void delete(Value... keys) {
        
    }
}
