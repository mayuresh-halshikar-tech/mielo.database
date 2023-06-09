package org.mielo.database;

public interface IdAwareEntity<ID> {
    ID getId();
    void setId(ID id);
    default boolean isPersisted() {
        ID id = getId();
        if (id == null) {
            return false;
        } else if (id instanceof Number) {
            long numberValue = ((Number) id).longValue();
            if (numberValue == 0) {
                return false;
            }
        }
        return true;
    }
}
