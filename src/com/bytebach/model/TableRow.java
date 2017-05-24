package com.bytebach.model;


import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

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
