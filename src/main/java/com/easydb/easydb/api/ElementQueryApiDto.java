package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElementQueryApiDto {
    private String id;
    private String bucketName;
    private List<ElementFieldApiDto> fields;

    public ElementQueryApiDto(String id, String bucketName, List<ElementFieldApiDto> fields) {
        this.id = id;
        this.bucketName = bucketName;
        this.fields = fields;
        Collections.sort(this.fields);
        this.fields = ImmutableList.copyOf(this.fields);
    }

    public ElementQueryApiDto() {
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

    public static ElementQueryApiDto from(ElementQueryDto elementQueryDto) {
        List<ElementFieldDto> fields = elementQueryDto.getFields();
        List<ElementFieldApiDto> apiFields = fields.stream()
                .map(it -> new ElementFieldApiDto(it.getName(), it.getValue()))
                .collect(Collectors.toList());
        return new ElementQueryApiDto(elementQueryDto.getId(),
                elementQueryDto.getName(), apiFields);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj == null) {
            return false;
        } else if (obj.getClass() != this.getClass()) {
            return false;
        } else {
            ElementQueryApiDto other = (ElementQueryApiDto) obj;
            return Objects.equals(id, other.id) && Objects.equals(bucketName, other.bucketName)
                    && Objects.equals(fields, other.fields);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
