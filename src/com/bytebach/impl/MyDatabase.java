package com.bytebach.impl;

import com.bytebach.model.*;

import java.util.*;

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
    // List as discussed above! Instead, implement MyDatabase and MyTable using
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
    
    /**
     * create a new empty database
     */
    public MyDatabase() {
        tables = new ArrayList<>();
    }
    
    @Override
    public Collection<? extends Table> tables() {
        return new ArrayList<>(tables);
    }
    
    @Override
    public Table table(String name) {
        if (name == null) throw new InvalidOperation("name cannot be null");
        for (Table table : tables) {
            if (table.name().equals(name)) return table;
        }
        return null;
    }
    
    @Override
    public void createTable(String name, List<Field> fields) {
        if (name == null) throw new InvalidOperation("name cannot be null");
        if (fields == null) throw new InvalidOperation("fields cannot be null");
        for (Table table : tables) {
            if (table.name().equals(name)) throw new InvalidOperation("cannot create table with same name");
        }
        tables.add(new ListTable(name, new FieldList(fields),this));
    }
    
    @Override
    public void deleteTable(String name) {
        for (Table table : tables) {
            if (table.name().equals(name)) {
                tables.remove(table);
                return;
            }
        }
    }
    
    /**
     * finds and removes all rows with references to a particular value
     * @param value the value to find references to
     * @param keys the keys in the row containing the value
     * @param tableName the name of the table that the value belongs to
     * @return a set of all the rows removed
     */
    public Set<List<Value>> cascadeDelete(String tableName, Value[] keys, Value value){
        if (tableName == null || keys == null || value == null){
            throw new InvalidOperation("When cascade deleting no parameter may be null");
        }
        if (keys.length == 0) throw new InvalidOperation("Must have at least one key when cascade deleting");
        
        Set<List<Value>> rows = new HashSet<>();
        for (Table table : tables) { // for each table in the database
            List<List<Value>> tableRows = table.rows();
            for (int i = 0; i < tableRows.size(); i++) { // is a indexed for loop to prevent errors from editing the row while it is being iterated
                List<Value> row = tableRows.get(i); // for each row in the table
                for (int j = 0; j < row.size(); j++) { // for each item in the row
                    Value item = row.get(j);
                    if (item instanceof ReferenceValue) { // if the item is a reference value
                        ReferenceValue referenceValue = (ReferenceValue) item;
                        try {
                            List<Value> referencedRow = this.table(referenceValue.table()).row(referenceValue.keys()); // find row the reference value references
                            if (referencedRow.get(j).equals(value) //if the reference value equals the value we are cascade deleting
                                    && referenceValue.table().equals(tableName)
                                    && Arrays.equals(referenceValue.keys(), keys)) {
                                rows.add(row);
                                Value[] keyValues = new Value[0];
                                keyValues = ((ListTable.TableRow) row).getKeyValues().toArray(keyValues); // get the key values of the reference value
                                rows.addAll(cascadeDelete(table.name(),
                                                          keyValues,
                                                          item));
                                table.delete(keyValues); // remove the reference row from the table
                            }
                        } catch (InvalidOperation e) { // catches an invalid operation when a row is not found
                        }
                    }
                }
            }
        }
        return rows;
    }
}
