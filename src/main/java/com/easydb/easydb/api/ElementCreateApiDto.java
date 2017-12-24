package com.easydb.easydb.api;

import com.easydb.easydb.domain.ElementCreateDto;
import com.easydb.easydb.domain.ElementFieldDto;

import java.util.List;
import java.util.stream.Collectors;

public class ElementCreateApiDto {
    private String bucketName;
    private List<ElementFieldApiDto> fields;

    public ElementCreateApiDto() {
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    public void setFields(List<ElementFieldApiDto> fields) {
        this.fields = fields;
    }

    public ElementCreateDto toDomainDto() {
        return ElementCreateDto.of(bucketName, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }
}
