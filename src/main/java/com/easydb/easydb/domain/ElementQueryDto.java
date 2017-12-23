package com.easydb.easydb.domain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ElementQueryDto {
    private final String id;
    private final String name;
    private final Map<String, ElementFieldDto> fieldsAsMap;

    private ElementQueryDto(String id, String name, List<ElementFieldDto> fields) {
        this.id = id;
        this.name = name;
        this.fieldsAsMap = fields.stream().collect(
                Collectors.toMap(ElementFieldDto::getName, it -> it));
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

    String getFieldValue(String fieldName) {
        return fieldsAsMap.get(fieldName).getValue();
    }
}
