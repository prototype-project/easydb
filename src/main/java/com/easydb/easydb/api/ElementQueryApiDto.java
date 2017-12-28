package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.dto.ElementFieldDto;
import com.easydb.easydb.domain.bucket.dto.ElementQueryDto;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElementQueryApiDto {
    private String id;
    private String bucketName;
    private List<ElementFieldApiDto> fields;

    @JsonCreator
    private ElementQueryApiDto(
            @JsonProperty("id") String id,
            @JsonProperty("bucketName") String bucketName,
            @JsonProperty("fields") List<ElementFieldApiDto> fields) {
        this.id = id;
        this.bucketName = bucketName;
        Collections.sort(fields);
        this.fields = ImmutableList.copyOf(fields);
    }

    public String getBucketName() {
        return bucketName;
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }

    public static ElementQueryApiDto from(ElementQueryDto elementQueryDto) {
        List<ElementFieldDto> fields = elementQueryDto.getFields();
        List<ElementFieldApiDto> apiFields = fields.stream()
                .map(it -> new ElementFieldApiDto(it.getName(), it.getValue()))
                .collect(Collectors.toList());
        return new ElementQueryApiDto(elementQueryDto.getId(),
                elementQueryDto.getBucketName(), apiFields);
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
