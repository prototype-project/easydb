package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ElementCrudApiDto {

    @NotNull
    @Valid
    private final List<ElementFieldApiDto> fields;

    @JsonCreator
    public ElementCrudApiDto(
    		@JsonProperty("fields") List<ElementFieldApiDto> fields) {
        this.fields = fields;
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    Element toDomain(String id, String bucketName) {
        return Element.of(id, bucketName, fields.stream()
                .map(ElementFieldApiDto::toDomain)
                .collect(Collectors.toList()));
    }
}
