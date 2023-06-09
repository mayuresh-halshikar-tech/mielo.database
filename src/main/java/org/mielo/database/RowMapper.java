package org.mielo.database;

public interface RowMapper <T> {
    public T mapRow(Object[] row);
}
