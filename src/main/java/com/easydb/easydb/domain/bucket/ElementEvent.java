package com.easydb.easydb.domain.bucket;

import java.util.Objects;

public class ElementEvent {
    private final Element element;
    private final Type type;

    public enum Type {
        CREATE, DELETE, UPDATE
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ElementEvent)) return false;
        ElementEvent that = (ElementEvent) o;
        return element.equals(that.element) &&
                type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, type);
    }
}
