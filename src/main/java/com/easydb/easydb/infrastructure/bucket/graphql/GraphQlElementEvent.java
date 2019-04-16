package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.ElementEvent;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQlElementEvent {
    private final GraphQlElementEventType type;
    private final GraphQlElement element;

    public GraphQlElementEvent(GraphQlElementEventType type, GraphQlElement element) {
        this.type = type;
        this.element = element;
    }

    public GraphQlElementEventType getType() {
        return type;
    }

    public GraphQlElement getElement() {
        return element;
    }

    public static GraphQlElementEvent of(ElementEvent domainEvent) {
        List<GraphQlField> graphQlFields = domainEvent.getElement().getFields().stream()
                .map(domainField -> new GraphQlField(domainField.getName(), domainField.getValue()))
                .collect(Collectors.toList());

        GraphQlElement graphQlElement = new GraphQlElement(domainEvent.getElement().getId(), graphQlFields);
        return new GraphQlElementEvent(GraphQlElementEventType.valueOf(domainEvent.getType().name()), graphQlElement);
    }

    public enum GraphQlElementEventType {
        CREATE,
        UPDATE,
        DELETE
    }
}
