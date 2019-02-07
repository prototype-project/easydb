package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.ElementField;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.util.CollectionUtils;

import static com.easydb.easydb.domain.transactions.Operation.OperationType.*;


@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationDto {

    @NotNull
    private final OperationType type;

    @NotEmpty
    private final String bucketName;

    private String elementId;

    @Valid
    private List<ElementFieldDto> fields;

    @JsonCreator
    public OperationDto(
            @JsonProperty("type") OperationType type,
            @JsonProperty("bucketName") String bucketName,
            @JsonProperty("elementId") String elementId,
            @JsonProperty("fields") List<ElementFieldDto> fields) {
        this.type = type;
        this.bucketName = bucketName;
        this.elementId = elementId;
        this.fields = fields;
        Collections.sort(fields);
        validate();
    }

    Operation toDomain(UUIDProvider uuidProvider) {
        if (type.equals(CREATE)) {
            return Operation.of(type, bucketName, uuidProvider.generateUUID(), toDomainFields());
        }
        return Operation.of(type, bucketName, elementId, toDomainFields());
    }

    OperationType getType() {
        return type;
    }

    String getBucketName() {
        return bucketName;
    }

    private void validate() {
        if (!type.equals(CREATE) && hasEmptyId()) {
            throw new ElementIdMustNotBeEmptyException();
        }
        if (type.equals(CREATE) && !hasEmptyId()) {
            throw new ElementIdMustBeEmptyException();
        }
        if ((type.equals(UPDATE) || type.equals(CREATE)) && CollectionUtils.isEmpty(fields)) {
            throw new ElementFieldsMustNotBeNull();
        }
    }

    private boolean hasEmptyId() {
        return Strings.isNullOrEmpty(elementId);
    }

    private List<ElementField> toDomainFields() {
        return fields.stream()
                .map(ElementFieldDto::toDomain)
                .collect(Collectors.toList());
    }
}
