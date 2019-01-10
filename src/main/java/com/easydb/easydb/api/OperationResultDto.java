package com.easydb.easydb.api;

import com.easydb.easydb.domain.transactions.OperationResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class OperationResultDto {
    private final Optional<ElementQueryApiDto> element;

    @JsonCreator
    private OperationResultDto(@JsonProperty("element") ElementQueryApiDto element) {
        this.element = Optional.ofNullable(element);
    }

    public static OperationResultDto of(OperationResult result) {
        return result.getElement()
                .map(versionedElement -> new OperationResultDto(ElementQueryApiDto.of(versionedElement.toSimpleElement())))
                .orElseGet(OperationResultDto::empty);
    }

    private static OperationResultDto empty() {
        return new OperationResultDto(null);
    }

    public Optional<ElementQueryApiDto> getElement() {
        return element;
    }
}
