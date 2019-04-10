package com.easydb.easydb.api.transaction;

import com.easydb.easydb.api.bucket.ElementQueryDto;
import com.easydb.easydb.domain.transactions.OperationResult;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;

public class OperationResultDto {
    private final Optional<ElementQueryDto> element;

    @JsonCreator
    private OperationResultDto(@JsonProperty("element") ElementQueryDto element) {
        this.element = Optional.ofNullable(element);
    }

    public static OperationResultDto of(OperationResult result) {
        return result.getElement()
                .map(versionedElement -> new OperationResultDto(ElementQueryDto.of(versionedElement.toSimpleElement())))
                .orElseGet(OperationResultDto::empty);
    }

    private static OperationResultDto empty() {
        return new OperationResultDto(null);
    }

    public Optional<ElementQueryDto> getElement() {
        return element;
    }
}
