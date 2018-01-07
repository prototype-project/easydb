package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;

public class ElementOperationApiDto {
    private final List<ElementFieldApiDto> fields;

    @JsonCreator
    public ElementOperationApiDto(
    		@JsonProperty("fields") List<ElementFieldApiDto> fields) {
        this.fields = fields;
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    Element toDomain(String id, String bucketName) {
        return Element.of(id, bucketName, fields.stream()
                .map(it -> ElementField.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }
}
