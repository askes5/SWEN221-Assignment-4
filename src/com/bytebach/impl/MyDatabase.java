package com.bytebach.impl;

import com.bytebach.model.Database;
import com.bytebach.model.Field;
import com.bytebach.model.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MyDatabase implements Database {
    // This is where you'll probably want to start. You'll need to provide an
    // implementation of Table as well.
    //
    // One of the key challenges in this assignment is to provide you're
    // own implementations of the List interface which can intercept the various
    // operations (e.g. add, set, remove, etc) and check whether they violate
    // the constraints and/or update the database appropriately (e.g. for the
    // cascading delete).
    //
    // HINT: to get started, don't bother providing your own implementations of
    // List as discussed above! Instead, implement MyDatabase and MyTabe using
    // conventional Collections. When you have that working, and the web system
    // is doing something sensible, then consider how you're going to get those
    // unit test to past.
    
    /**
     * The tables contained in this database
     */
    private List<Table> tables;
    
    /**
     * creates a new database
     * @param tables the tables in the database
     */
    public MyDatabase(List<Table> tables) {
        this.tables = tables;
    }
    
    @Override
    public Collection<? extends Table> tables() {
        return new ArrayList<>(tables);
    }
    
    @Override
    public Table table(String name) {
        for (Table table : tables) {
            if (table.name().equals(name)) return table;
        }
        return null;
    }
    
    @Override
    public void createTable(String name, List<Field> fields) {
        
    }
    
    @Override
    public void deleteTable(String name) {
        
    }
}
