package com.bytebach.model;

import com.bytebach.impl.MyDatabase;

import java.util.*;

/**
 * @author Matthew Askes
 */
public class ListTable implements Table {
    
    private final String name;
    private final FieldList fields;
    private final TableRowList rows;
    /**
     * the database this table belongs to
     */
    private final Database database;

    /**
     * creates a new table with no rows
     * @param name the name of the table
     * @param fields the fields in the table
     * @param database the database this table belongs to
     */
    public ListTable(String name, FieldList fields, Database database) {
        this.name = name;
        this.fields = fields;
        this.database = database;
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
        for (List<Value> row : rows) {
            List<Value> rowKeyValues = ((TableRow) row).getKeyValues(); // requires casting to TableRow to allow access to getKeyValues
            if (rowKeyValues.size() == keys.length && rowKeyValues.containsAll(Arrays.asList(keys))) return row;
        }
        throw new InvalidOperation("No row found");
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
        
        private List<TableRow> rows;
        
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
            TableRow row = rows.get(index);
    
            if (database instanceof MyDatabase) {
                for (Value value : row) {
                    Value[] keyValues = new Value[0];
                    keyValues = row.getKeyValues().toArray(keyValues);
                    ((MyDatabase) database).cascadeDelete(name, keyValues, value);
                }
            }
            return rows.remove(index);
        }
    
        @Override
        public boolean add(List<Value> row) {
            TableRow toAdd= new TableRow(row);

            if (row == null ) {
                throw new InvalidOperation("Cannot add a null row to the list");
            }
            if (row.size() != fields.size()){
                throw new InvalidOperation("wrong number of values");
            }
            
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
            checkValidReferences();
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
    
        /**
         * returns all the values in this row that are references
         * @return a list of reference values in this table, (will return an empty list if no values found)
         */
        public List<ReferenceValue> getReferenceValues(){
            List<ReferenceValue> referenceValues = new ArrayList<>();
            for (Value value : values) {
                if (value instanceof ReferenceValue) referenceValues.add((ReferenceValue) value);
            }
            return referenceValues;
        }

        /**
         * checks that all the references in this row are valid (with respect to the database that contains this table)
         * @return true if all references are valid
         * @throws InvalidOperation if any references are not valid
         */
        public void checkValidReferences(){
            for (int i = 0; i < values.size(); i++) {
                if (values.get(i) instanceof ReferenceValue){
                    ReferenceValue reference = (ReferenceValue) values.get(i);
                    try {
                        if (database.table(reference.table()).row(reference.keys()).get(i) == null) {
                            throw new InvalidOperation("invalid reference: " + reference);
                        }
                    } catch (NullPointerException e){
                        throw new InvalidOperation("invalid reference: " + reference);
                    }
                }
            }
        }

        @Override
        public boolean add(Value value) {
            throw new InvalidOperation("Add is forbidden in table row");
        }

        @Override
        public Value remove(int index) {
            throw new InvalidOperation("Remove is forbidden in table row");
        }
    
        @Override
        public boolean remove(Object o) {
            throw new InvalidOperation("Remove is forbidden in table row");
        }
    
        @Override
        public Value set(int index, Value element) {
            if (fields.get(index).isKey()) throw new InvalidOperation("Cannot modify a field that is a key field");
            Field.Type type = fields.get(index).type();

            if (element instanceof IntegerValue && type != Field.Type.INTEGER) throw new InvalidOperation("incorrect type, expected an integer");
            if (element instanceof StringValue){
                StringValue stringValue = (StringValue)element;
                if (type == Field.Type.TEXT && stringValue.value().contains("\n")){
                    throw new InvalidOperation("Text cannot have new lines");
                }
                if (type != Field.Type.TEXT && type != Field.Type.TEXTAREA){
                    throw new InvalidOperation("incorrect type, expected text or text area");
                }
            }
            if (element instanceof ReferenceValue && type != Field.Type.REFERENCE) throw new InvalidOperation("incorrect type, expected a reference");
            if (element instanceof BooleanValue && type != Field.Type.BOOLEAN) throw new InvalidOperation("incorrect type, expected a boolean");

            Value prev = values.set(index,element);
            checkValidReferences();
            return prev;
        }
        
        @Override
        public int size() {
            return values.size();
        }
        
        @Override
        public Value get(int index) {
            Value value = values.get(index);
//            if (value instanceof ReferenceValue){ // this code forces a reference value ot return the value it references instead of the reference
//                ReferenceValue reference = (ReferenceValue) value;
//                return database.table(reference.table()).row(reference.keys()).get(index);
//            } else {
                return value;
//            }
        }
    }
    
}
