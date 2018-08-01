package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ElementTransactionOperationApiDto {

    private String id;

    @NotEmpty
    private String bucketName;

    @NotNull
    @Valid
    private List<ElementFieldApiDto> fields;

    @JsonCreator
    private ElementTransactionOperationApiDto(
            @JsonProperty("id") String id,
            @JsonProperty("bucketName") String bucketName,
            @JsonProperty("fields") List<ElementFieldApiDto> fields) {
        this.id = id;
        this.bucketName = bucketName;
        Collections.sort(fields);
        this.fields = ImmutableList.copyOf(fields);
    }

    public String getId() {
        return id;
    }

    public List<ElementFieldApiDto> getFields() {
        return fields;
    }

    Element toDomainElement() {
        return toDomainElement(this.id);
    }

    Element toDomainElement(String id) {
        List<ElementField> domainFields = fields.stream()
                .map(ElementFieldApiDto::toDomain)
                .collect(Collectors.toList());
        return Element.of(id, bucketName, domainFields);
    }
}
