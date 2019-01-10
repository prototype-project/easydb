package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ElementQueryApiDto {

    private String id;

    private List<ElementFieldApiDto> fields;

    @JsonCreator
    private ElementQueryApiDto(
            @JsonProperty("id") String id,
            @JsonProperty("fields") List<ElementFieldApiDto> fields) {
        this.id = id;
        Collections.sort(fields);
        this.fields = ImmutableList.copyOf(fields);
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    public String getId() {
        return id;
    }

    public static ElementQueryApiDto of(Element domainElement) {
        List<ElementField> fields = domainElement.getFields();
        List<ElementFieldApiDto> apiFields = fields.stream()
                .map(it -> new ElementFieldApiDto(it.getName(), it.getValue()))
                .collect(Collectors.toList());
        return new ElementQueryApiDto(domainElement.getId(), apiFields);
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
            return Objects.equals(id, other.id) && Objects.equals(fields, other.fields);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
