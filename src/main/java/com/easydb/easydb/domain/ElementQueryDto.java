package com.easydb.easydb.domain;

import java.util.List;

public class ElementQueryDto {
    private final String id;
    private final String name;
    private final List<ElementFieldDto> fields;

    private ElementQueryDto(String id, String name, List<ElementFieldDto> fields) {
        this.id = id;
        this.name = name;
        this.fields = fields;
    }

    static ElementQueryDto of(String id, String name, List<ElementFieldDto> fields) {
        return new ElementQueryDto(id, name, fields);
    }

    String getId() {
        return id;
    }

    String getName() {
        return name;
    }

    List<ElementFieldDto> getFields() {
        return fields;
    }
}
