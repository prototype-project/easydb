package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.List;

public class GraphQlElement {
    private final String id;
    private final List<GraphQlField> fields;

    public GraphQlElement(String id, List<GraphQlField> fields) {
        this.id = id;
        this.fields = fields;
    }

    public List<GraphQlField> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }
}
