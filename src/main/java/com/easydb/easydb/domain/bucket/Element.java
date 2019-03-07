package com.easydb.easydb.domain.bucket;

import com.google.common.collect.ImmutableList;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class Element {

    private final String id;
    private final String bucketName;
    private final List<ElementField> fields;

    private Element(String id, String bucketName, List<ElementField> fields) {
        this.id = id;
        this.bucketName = bucketName;
        this.fields = ImmutableList.copyOf(fields);
    }

    public static Element of(String id, String bucketName, List<ElementField> fields) {
        return new Element(id, bucketName, fields);
    }

    public String getBucketName() {
        return bucketName;
    }

    public String getId() {
        return id;
    }

    public List<ElementField> getFields() {
        return Collections.unmodifiableList(fields);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Element that = (Element) o;

        return bucketName.equals(that.bucketName) &&
                fields.equals(that.fields) &&
                id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bucketName, fields, id);
    }

    @Override
    public String toString() {
        return String.format("Element(id=%s, bucketName=%s, fields=[...]", id, bucketName);
    }
}
