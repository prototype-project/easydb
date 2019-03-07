package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import java.util.Optional;

public class OperationResult {
    private final Optional<VersionedElement> element;
    private final String spaceName;

    private OperationResult(Optional<VersionedElement> element, String spaceName) {
        this.element = element;
        this.spaceName = spaceName;
    }

    public static OperationResult of(VersionedElement element, String spaceName) {
        return new OperationResult(Optional.of(element), spaceName);
    }

    static OperationResult emptyResult(String spaceName) {
        return new OperationResult(Optional.empty(), spaceName);
    }

    public Optional<VersionedElement> getElement() {
        return element;
    }

    public String getSpaceName() {
        return spaceName;
    }
}
