package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.VersionedElement;
import java.util.Optional;

public class OperationResult {
    private final Optional<VersionedElement> element;

    private OperationResult(Optional<VersionedElement> element) {
        this.element = element;
    }

    public static OperationResult emptyResult() {
        return new OperationResult(Optional.empty());
    }

    public static OperationResult of(VersionedElement element) {
        return new OperationResult(Optional.of(element));
    }

    public Optional<VersionedElement> getElement() {
        return element;
    }
}
