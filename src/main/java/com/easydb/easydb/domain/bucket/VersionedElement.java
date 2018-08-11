package com.easydb.easydb.domain.bucket;

import java.util.List;
import java.util.Optional;

public class VersionedElement extends Element {

    private final Optional<Long> version;

    private VersionedElement(String id, String bucketName, List<ElementField> fields, Optional<Long> version) {
        super(id, bucketName, fields);
        this.version = version;
    }

    public static VersionedElement of(Element element) {
        return new VersionedElement(element.getId(), element.getBucketName(), element.getFields(), Optional.empty());
    }

    public static VersionedElement of(String id, String bucketName, List<ElementField> fields, long version) {
        return new VersionedElement(id, bucketName, fields, Optional.of(version));
    }

    public Element toSimpleElement() {
        return Element.of(super.getId(), super.getBucketName(), super.getFields());
    }

    public long getVersion() {
        return version.orElseThrow(TryingToGetEmptyVersionException::new);
    }

    static class TryingToGetEmptyVersionException extends RuntimeException {

        TryingToGetEmptyVersionException() {
            super("Cannot get empty version");
        }
    }
}
