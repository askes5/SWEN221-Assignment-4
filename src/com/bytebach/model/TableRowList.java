package com.bytebach.model;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Askes
 */
public class TableRowList extends AbstractList<TableRow> implements List<TableRow> {

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
    public boolean add(TableRow row) {
        return rows.add(row);
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
