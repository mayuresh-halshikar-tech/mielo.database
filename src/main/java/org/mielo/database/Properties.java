package org.mielo.database;

import java.util.Collection;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class Properties {

    private LinkedList<Property> properties;

    public Properties(String name, Object value) {
        and(name, value);
    }

    private LinkedList<Property> props() {
        if(properties == null) {
            properties = new LinkedList<>();
        }
        return properties;
    }

    public Properties and(String name, Object value) {
        props().add(new Property(name, value));
        return this;
    }

    public Collection<Property> get() {
        return properties;
    }

    public String whereClause(String alias) {
        return props()
                .stream()
                .map(property -> alias + "." + property.name() + " = " + property.valueAsString())
                .collect(Collectors.joining(" and "));
    }
}
