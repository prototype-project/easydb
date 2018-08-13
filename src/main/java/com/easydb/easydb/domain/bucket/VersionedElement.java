package com.easydb.easydb.domain.bucket;

import java.util.List;
import java.util.Optional;

public class VersionedElement {

    private final Optional<Long> version;
    private final Element element;

    private VersionedElement(String id, String bucketName, List<ElementField> fields, Optional<Long> version) {
        this.element = Element.of(id, bucketName, fields);
        this.version = version;
    }

    public static VersionedElement of(String id, String bucketName, List<ElementField> fields) {
        return new VersionedElement(id, bucketName, fields, Optional.empty());
    }

    public static VersionedElement of(String id, String bucketName, List<ElementField> fields, long version) {
        return new VersionedElement(id, bucketName, fields, Optional.of(version));
    }

    public Element toSimpleElement() {
        return new Element(element.getId(), element.getBucketName(), element.getFields());
    }

    public Optional<Long> getVersion() {
        return version;
    }

    public String getBucketName() {
        return element.getBucketName();
    }

    public List<ElementField> getFields() {
        return element.getFields();
    }

    public String getId() {
        return element.getId();
    }



    public long getVersionOrThrowErrorIfEmpty() {
        return version.orElseThrow(TryingToGetEmptyVersionException::new);
    }

    @Override
    public String toString() {
        return String.format("Element(id=%s, bucketName=%s, fields=[...], version=%d)",
                element.getId(), element.getBucketName(), getVersionOrThrowErrorIfEmpty());
    }

    static class TryingToGetEmptyVersionException extends RuntimeException {

        TryingToGetEmptyVersionException() {
            super("Cannot get empty version");
        }
    }
}
