package com.bytebach.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Matthew Askes
 */
public class ValueList extends AbstractList<Value>{
    private List<Value> values;
    /**
     * specifies the type of values allowed in this row
     */
    private FieldList fields;
    
    /**
     * create a new row from given values
     * @param values the values in this row
     */
    public ValueList(List<Value> values, FieldList fields) {
        this.values = values;
        this.fields = fields;
    }
    
    /**
     * create a new row with no values
     */
    public ValueList(FieldList fields) {
        values = new ArrayList<>();
        this.fields = fields;
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
