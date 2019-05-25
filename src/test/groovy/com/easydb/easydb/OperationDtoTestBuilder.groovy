package com.easydb.easydb

import com.easydb.easydb.api.bucket.ElementFieldDto
import com.easydb.easydb.api.transaction.OperationDto
import com.easydb.easydb.domain.transactions.Operation

class OperationDtoTestBuilder {
    Operation.OperationType type = Operation.OperationType.UPDATE
    String bucketName = "people"
    String elementId = UUID.randomUUID()
    List<ElementFieldDto> fields = [
            new ElementFieldDto('firstName', 'John'),
            new ElementFieldDto('lastName', 'Smith'),
    ]

    OperationDtoTestBuilder type(Operation.OperationType type) {
        this.type = type
        return this
    }

    OperationDtoTestBuilder bucketName(String bucketName) {
        this.bucketName = bucketName
        return this
    }

    OperationDtoTestBuilder elementId(String elementId) {
        this.elementId = elementId
        return this
    }

    OperationDtoTestBuilder fields(List<ElementFieldDto> fields) {
        this.fields = fields
        return this
    }

    OperationDto build() {
        return new OperationDto(type, bucketName, elementId, fields)
    }

    static OperationDtoTestBuilder builder() {
        return new OperationDtoTestBuilder()
    }
}
