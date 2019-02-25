package com.easydb.easydb.infrastructure.bucket.graphql;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FieldFilter {
    private String name;
    private String value;

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("value")
    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
