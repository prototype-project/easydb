package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.Objects;

public class GraphQlField {
    private final String name;
    private final String value;


    public GraphQlField(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof GraphQlField)) return false;
        GraphQlField that = (GraphQlField) o;
        return name.equals(that.name) &&
                value.equals(that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }
}
