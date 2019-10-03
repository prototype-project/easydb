package com.easydb.easydb.api.bucket;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.Element;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public class ElementCrudDto {

    @NotNull
    @Valid
    private final List<ElementFieldDto> fields;

    private final Optional<String> id;

    @JsonCreator
    public ElementCrudDto(
            @JsonProperty("fields") List<ElementFieldDto> fields,
            @JsonProperty("id") String id) {
        this.fields = fields;
        this.id = Optional.ofNullable(id);
    }

    public Optional<String> getId() {
        return id;
    }

    public List<ElementFieldDto> getFields() {
        return fields;
    }

    Element toDomain(String id, BucketName bucketName) {
        return Element.of(id, bucketName, fields.stream()
                .map(ElementFieldDto::toDomain)
                .collect(Collectors.toList()));
    }
}
