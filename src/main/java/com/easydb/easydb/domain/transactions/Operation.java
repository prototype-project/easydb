package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import java.util.Objects;

public class Operation {

    public enum OperationType {
        CREATE, UPDATE, DELETE, READ
    }

    private final OperationType type;
    private final Element element;

    private Operation(OperationType type, Element element) {
        this.type = type;
        this.element = element;
    }

    public OperationType getType() {
        return type;
    }

    public Element getElement() {
        return element;
    }

    public static Operation of(OperationType type, Element element) {
        return new Operation(type, element);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation that = (Operation) o;

        return type.equals(that.type) &&
                element.equals(that.element);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, element);
    }
}
