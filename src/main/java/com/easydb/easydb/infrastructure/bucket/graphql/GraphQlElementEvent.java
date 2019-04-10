package com.easydb.easydb.infrastructure.bucket.graphql;

public class GraphQlElementEvent {
    private final GraphQlElementEvent type;
    private final GraphQlElement element;

    public GraphQlElementEvent(GraphQlElementEvent type, GraphQlElement element) {
        this.type = type;
        this.element = element;
    }

    public GraphQlElementEvent getType() {
        return type;
    }

    public GraphQlElement getElement() {
        return element;
    }

    public enum GraphQlElementEventType {
        CREATE,
        UPDATE,
        DELETE
    }
}
