package com.easydb.easydb.api.bucket;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElementQueryDto {

    private String id;

    private List<ElementFieldDto> fields;

    @JsonCreator
    private ElementQueryDto(
            @JsonProperty("id") String id,
            @JsonProperty("fields") List<ElementFieldDto> fields) {
        this.id = id;
        Collections.sort(fields);
        this.fields = ImmutableList.copyOf(fields);
    }

    public List<ElementFieldDto> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }

    public static ElementQueryDto of(Element domainElement) {
        List<ElementField> fields = domainElement.getFields();
        List<ElementFieldDto> apiFields = fields.stream()
                .map(it -> new ElementFieldDto(it.getName(), it.getValue()))
                .collect(Collectors.toList());
        return new ElementQueryDto(domainElement.getId(), apiFields);
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
            ElementQueryDto other = (ElementQueryDto) obj;
            return Objects.equals(id, other.id) && Objects.equals(fields, other.fields);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
