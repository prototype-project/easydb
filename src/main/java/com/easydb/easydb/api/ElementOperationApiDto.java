package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

public class ElementOperationApiDto {
    private List<ElementFieldApiDto> fields;

    public ElementOperationApiDto() {
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    public void setFields(List<ElementFieldApiDto> fields) {
        this.fields = fields;
    }

    public ElementCreateDto toCreateDto(String bucketName) {
        return ElementCreateDto.of(bucketName, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }

    public ElementUpdateDto toUpdateDto(String bucketName, String elementId) {
        return ElementUpdateDto.of(bucketName, elementId, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }
}
