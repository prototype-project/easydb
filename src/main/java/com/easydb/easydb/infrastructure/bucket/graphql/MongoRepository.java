package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.Collections;
import java.util.List;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoRepository {
    private final String spaceName;
    private final MongoTemplate mongoTemplate;

    MongoRepository(String spaceName, MongoTemplate mongoTemplate) {
        this.spaceName = spaceName;
        this.mongoTemplate = mongoTemplate;
    }

    public List<Element> elements() {
        return Collections.emptyList();
    }
}
