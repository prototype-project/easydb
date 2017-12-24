package com.easydb.easydb.domain;

import com.google.common.collect.ImmutableMap;

import java.util.ArrayList;
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
        this.fieldsAsMap = ImmutableMap.copyOf(fields.stream().collect(
                Collectors.toMap(ElementFieldDto::getName, it -> it)));
    }

    public static ElementQueryDto of(String id, String name, List<ElementFieldDto> fields) {
        return new ElementQueryDto(id, name, fields);
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getFieldValue(String fieldName) {
        return fieldsAsMap.get(fieldName).getValue();
    }

    public List<ElementFieldDto> getFields() {
        return new ArrayList<>(fieldsAsMap.values());
    }
}
