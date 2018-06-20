package com.easydb.easydb

import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField

class ElementTestBuilder {
    private String id = UUID.randomUUID()
    private String bucketName = "people"
    private List<ElementField> fields = [
            ElementField.of('firstName', 'John'),
            ElementField.of('lastName', 'Smith'),
            ElementField.of('email', 'john.smith@op.pl')
    ]

    ElementTestBuilder id(String id) {
        this.id = id
        return this
    }

    ElementTestBuilder bucketName(String name) {
        this.bucketName = name
        return this
    }

    ElementTestBuilder fields(List<ElementField> fields) {
        this.fields = fields
        return this
    }

    Element build() {
        return Element.of(id, bucketName, fields)
    }

    static ElementTestBuilder builder() {
        return new ElementTestBuilder()
    }
}
