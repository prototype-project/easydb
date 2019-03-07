package com.easydb.easydb

import com.easydb.easydb.domain.bucket.Element

trait ElementUtils {
    static getFieldValue(Element element, String fieldName) {
        return element.fields.stream()
                .filter({f -> f.getName() == fieldName})
                .map({it.value}).findFirst().get()
    }
}
