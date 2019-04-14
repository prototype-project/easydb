package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.domain.BucketName;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementField;
import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlElement;
import com.easydb.easydb.infrastructure.bucket.graphql.GraphQlField;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class PersistentBucketElement {

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

    VersionedElement toDomainVersionedElement(BucketName bucketName) {
        return VersionedElement.of(id, bucketName, fields, version);
    }

    List<ElementField> getFields() {
        return fields;
    }

    public GraphQlElement toGraphQlElement() {
        List<GraphQlField> graphQlFields = fields.stream()
                .map(gf -> new GraphQlField(gf.getName(), gf.getValue()))
                .collect(Collectors.toList());

        return new GraphQlElement(id, graphQlFields);
    }
}

