package com.easydb.easydb.infrastructure.space;

import com.easydb.easydb.domain.space.Space;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentSpace {

    @Id
    private final String spaceName;

    private final Set<String> buckets;

    private PersistentSpace(String spaceName, Set<String> buckets) {
        this.spaceName = spaceName;
        this.buckets = buckets;
    }

    Space toDomain() {
        return Space.of(spaceName, buckets);
    }

    static PersistentSpace of(Space space) {
        return new PersistentSpace(space.getName(), space.getBuckets());
    }
}
