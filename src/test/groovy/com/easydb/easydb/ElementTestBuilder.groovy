package com.easydb.easydb

import com.easydb.easydb.domain.bucket.BucketName
import com.easydb.easydb.domain.bucket.Element
import com.easydb.easydb.domain.bucket.ElementField

class ElementTestBuilder {
    private String id = UUID.randomUUID()
    private BucketName bucketName = new BucketName("testSpace","people")
    private List<ElementField> fields = [
            ElementField.of('firstName', 'John'),
            ElementField.of('lastName', 'Smith'),
    ]

    ElementTestBuilder id(String id) {
        this.id = id
        return this
    }

    ElementTestBuilder bucketName(BucketName name) {
        this.bucketName = name
        return this
    }

    ElementTestBuilder fields(List<ElementField> fields) {
        this.fields = fields
        return this
    }

    ElementTestBuilder clearFields() {
        this.fields = []
        return this
    }

    ElementTestBuilder addField(ElementField field) {
        this.fields.add(field)
        return this
    }

    Element build() {
        return Element.of(id, bucketName, fields)
    }

    static ElementTestBuilder builder() {
        return new ElementTestBuilder()
    }
}
