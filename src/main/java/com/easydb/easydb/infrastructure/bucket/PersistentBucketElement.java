package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import com.easydb.easydb.domain.bucket.VersionedElement;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentBucketElement {

    @Id
    private final String id;
    private final List<ElementField> fields;

    private long version;

    private PersistentBucketElement(String id, List<ElementField> fields, long version) {
        this.id = id;
        this.fields = fields;
        this.version = version;

    }

    static PersistentBucketElement of(Element element) {
        return new PersistentBucketElement(element.getId(), element.getFields(), 0);
    }

    static PersistentBucketElement of(String id, List<ElementField> fields, long version) {
        return new PersistentBucketElement(id, fields, version);
    }

    VersionedElement toDomainVersionedElement(String bucketName) {
        return VersionedElement.of(id, bucketName, fields, version);
    }

    List<ElementField> getFields() {
        return fields;
    }

    long getVersion() {
        return version;
    }
}

