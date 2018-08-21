package com.easydb.easydb.domain.bucket;

import com.google.common.collect.ImmutableList;

import com.google.common.collect.ImmutableMap;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Element {

    private final String id;
    private final String bucketName;
    private final List<ElementField> fields;
    private final Map<String, ElementField> fieldsAsMap;

    private Element(String id, String bucketName, List<ElementField> fields) {
        this.id = id;
        this.bucketName = bucketName;
        this.fields = ImmutableList.copyOf(fields);
        this.fieldsAsMap = ImmutableMap.copyOf(fields.stream().collect(
                Collectors.toMap(ElementField::getName, it -> it)));
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

    public String getFieldValue(String fieldName) {
        return fieldsAsMap.get(fieldName).getValue();
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
