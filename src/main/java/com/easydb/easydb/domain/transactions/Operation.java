package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Operation {

    public enum OperationType {
        CREATE, UPDATE, DELETE, READ
    }

    private final OperationType type;
    private final String bucketName;
    private final String elementId;

    private final List<ElementField> fields;

    private Operation(OperationType type,
                      String bucketName,
                      String elementId,
                      List<ElementField> fields) {
        this.type = type;
        this.bucketName = bucketName;
        this.elementId = elementId;
        this.fields = fields;
    }

    public OperationType getType() {
        return type;
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getElementId() {
        return elementId;
    }

    public List<ElementField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    public static Operation of(OperationType type, String bucketName, String elementId, List<ElementField> fields) {
        return new Operation(type, bucketName, elementId, fields);
    }

    public static Operation of(OperationType type, String bucketName, String elementId) {
        return of(type, bucketName, elementId, Collections.emptyList());
    }

    public static Operation of(OperationType type, Element element) {
        return of(type, element.getBucketName().getName(), element.getId(), element.getFields());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Operation that = (Operation) o;

        return type.equals(that.type) &&
                bucketName.equals(that.bucketName) &&
                elementId.equals(that.elementId) &&
                fields.equals(that.fields);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, bucketName, elementId, fields);
    }
}
