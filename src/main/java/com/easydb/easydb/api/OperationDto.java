package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.Operation;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Strings;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;


@JsonIgnoreProperties(ignoreUnknown = true)
public class OperationDto {

    @NotNull
    private final Operation.OperationType type;

    @NotNull
    @Valid
    private final ElementTransactionOperationApiDto element;

    @JsonCreator
    public OperationDto(
            @JsonProperty("type") Operation.OperationType type,
            @JsonProperty("element") ElementTransactionOperationApiDto element) {
        this.type = type;
        this.element = element;
        validate();
    }

    public ElementTransactionOperationApiDto getElement() {
        return element;
    }

    Operation toDomain(UUIDProvider uuidProvider) {
        if (type.equals(Operation.OperationType.CREATE)) {
            return Operation.of(type, element.toDomainElement(uuidProvider.generateUUID()));
        }
        return Operation.of(type, element.toDomainElement());
    }

    private void validate() {
        if (!type.equals(Operation.OperationType.CREATE) && elementHasEmptyId()) {
            throw new ElementIdMustNotBeEmptyException();
        }
        if (type.equals(Operation.OperationType.CREATE) && !elementHasEmptyId()) {
            throw new ElementIdMustBeEmptyException();
        }
    }

    private boolean elementHasEmptyId() {
        return Strings.isNullOrEmpty(element.getId());
    }
}
