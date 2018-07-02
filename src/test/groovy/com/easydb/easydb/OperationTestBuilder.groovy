package com.easydb.easydb

import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.transactions.Operation

class OperationTestBuilder {
    private Operation.OperationType type = Operation.OperationType.CREATE
    private Element element = ElementTestBuilder.builder().id(null).build()

    OperationTestBuilder type(Operation.OperationType type) {
        this.type = type
        return this
    }

    OperationTestBuilder element(Element element) {
        this.element = element
        return this
    }

    Operation build() {
        return Operation.of(type, element)
    }

    static OperationTestBuilder builder() {
        return new OperationTestBuilder()
    }
}
