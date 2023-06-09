package org.mielo.database;

public record Property(String name, Object val) {

    public String valueAsString() {
        if(val == null) return null;
        if(val instanceof String) {
            return "'" + val.toString() + "'";
        }
        return val.toString();
    }
}
