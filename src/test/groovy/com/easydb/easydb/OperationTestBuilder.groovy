package com.easydb.easydb

import com.easydb.easydb.domain.bucket.ElementField
import com.easydb.easydb.domain.transactions.Operation

class OperationTestBuilder {
    Operation.OperationType type = Operation.OperationType.UPDATE
    String bucketName = "people"
    String elementId = UUID.randomUUID();
    List<ElementField> fields = [
            ElementField.of('firstName', 'John'),
            ElementField.of('lastName', 'Smith'),
    ]

    OperationTestBuilder type(Operation.OperationType type) {
        this.type = type
        return this
    }

    OperationTestBuilder bucketName(String bucketName) {
        this.bucketName = bucketName
        return this
    }

    OperationTestBuilder elementId(String elementId) {
        this.elementId = elementId
        return this
    }

    OperationTestBuilder fields(List<ElementField> fields) {
        this.fields = fields
        return this
    }

    Operation build() {
        return Operation.of(type, bucketName, elementId, fields)
    }

    static OperationTestBuilder builder() {
        return new OperationTestBuilder()
    }
}
