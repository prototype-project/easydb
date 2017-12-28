package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;

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

    ElementCreateDto toCreateDto(String bucketName) {
        return ElementCreateDto.of(bucketName, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }

    ElementUpdateDto toUpdateDto(String bucketName, String elementId) {
        return ElementUpdateDto.of(bucketName, elementId, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }
}
