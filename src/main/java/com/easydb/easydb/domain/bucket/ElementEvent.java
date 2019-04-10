package com.easydb.easydb.domain.bucket;

public class ElementEvent {
    private final Element element;
    private final Type type;

    public ElementEvent(Element element, Type type) {
        this.element = element;
        this.type = type;
    }

    public Element getElement() {
        return element;
    }

    public Type getType() {
        return type;
    }

    public enum Type {
        CREATE, DELETE, UPDATE
    }
}
