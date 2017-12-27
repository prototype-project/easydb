package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementCreateDto;
import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;
import com.easydb.easydb.domain.bucket.dto.ElementUpdateDto;

import java.util.List;
import java.util.stream.Collectors;

public class ElementOperationApiDto {
    private String id; // nullable
    private String bucketName;
    private List<ElementFieldApiDto> fields;

    public ElementOperationApiDto() {
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ElementCreateDto toCreateDto() {
        return ElementCreateDto.of(bucketName, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }

    public ElementUpdateDto toUpdateDto() {
        return ElementUpdateDto.of(bucketName, id, fields.stream()
                .map(it -> ElementFieldDto.of(it.getName(), it.getValue()))
                .collect(Collectors.toList()));
    }
}
