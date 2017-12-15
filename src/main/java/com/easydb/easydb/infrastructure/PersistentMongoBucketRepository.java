package com.easydb.easydb.infrastructure;

import org.springframework.data.mongodb.repository.MongoRepository;

interface PersistentMongoBucketRepository extends MongoRepository<PersistentBucket, String> {
}
