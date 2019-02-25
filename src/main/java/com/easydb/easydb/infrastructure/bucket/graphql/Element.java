package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.List;

public class Element {
    private final String id;
    private final List<Field> fields;

    public Element(String id, List<Field> fields) {
        this.id = id;
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }
}
