package com.easydb.easydb.api;

import java.util.List;

public class BucketDefinitionApiDto {
    private String name;
    private List<String> fields;

    public BucketDefinitionApiDto() {
    }

    public String getName() {
        return name;
    }

    public List<String> getFields() {
        return fields;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFields(List<String> fields) {
        this.fields = fields;
    }
}
