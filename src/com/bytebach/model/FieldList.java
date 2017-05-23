package com.bytebach.model;

import java.util.AbstractList;
import java.util.List;

/**
 * Defines a list of fields. Used for a database.<br>
 * this list cannot be modified, i.e. is Immutable
 * @author Matthew Askes
 */
public class FieldList extends AbstractList<Field> {
    
    private final List<Field> fields;
    
    public FieldList(List<Field> fields) {
        this.fields = fields;
    }
    
    @Override
    public Field get(int index) {
        return new Field(fields.get(index));
    }
    
    @Override
    public int size() {
        return fields.size();
    }
}
