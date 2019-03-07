package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.transactions.BucketRepository;
import com.easydb.easydb.domain.bucket.transactions.TransactionalElementService;

public class ElementServiceFactory {
    private final BucketRepository bucketRepository;

    public ElementServiceFactory(BucketRepository bucketRepository) {
        this.bucketRepository = bucketRepository;
    }

    public TransactionalElementService buildElementService(String spaceName) {
        return new TransactionalElementService(spaceName, bucketRepository);
    }
}
