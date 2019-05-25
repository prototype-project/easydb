package com.easydb.easydb

import com.easydb.easydb.api.bucket.ElementCrudDto
import com.easydb.easydb.api.bucket.ElementFieldDto
import com.easydb.easydb.domain.bucket.BucketName

class ElementCrudDtoTestBuilder {
    private BucketName bucketName = new BucketName("testSpace","people")
    private List<ElementFieldDto> fields = [
            new ElementFieldDto('firstName', 'John'),
            new ElementFieldDto('lastName', 'Smith'),
    ]

    ElementCrudDtoTestBuilder fields(List<ElementFieldDto> fields) {
        this.fields = fields
        return this
    }

    ElementCrudDtoTestBuilder clearFields() {
        this.fields = []
        return this
    }

    ElementCrudDtoTestBuilder addField(ElementFieldDto field) {
        this.fields.add(field)
        return this
    }

    ElementCrudDto build() {
        return new ElementCrudDto(fields)
    }

    static ElementCrudDtoTestBuilder builder() {
        return new ElementCrudDtoTestBuilder()
    }
}
