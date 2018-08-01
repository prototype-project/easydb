package com.easydb.easydb.infrastructure.bucket;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.BucketServiceFactory;
import com.easydb.easydb.domain.bucket.SimpleBucketService;
import com.easydb.easydb.domain.bucket.TransactionalBucketService;
import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;

public class TransactionalBucketServiceFactory implements BucketServiceFactory {
    private final BucketRepository bucketRepository;
    private final SpaceRepository spaceRepository;
    private final TransactionManagerFactory transactionManagerFactory;

    public TransactionalBucketServiceFactory(BucketRepository bucketRepository,
                                             SpaceRepository spaceRepository,
                                             TransactionManagerFactory transactionManagerFactory) {
        this.bucketRepository = bucketRepository;
        this.spaceRepository = spaceRepository;
        this.transactionManagerFactory = transactionManagerFactory;
    }

    public BucketService buildBucketService(String spaceName) {
        if (!spaceRepository.exists(spaceName)) {
            throw new SpaceDoesNotExistException(spaceName);
        }
        SimpleBucketService simpleBucketService =
                new SimpleBucketService(spaceName, spaceRepository, bucketRepository);
        return new TransactionalBucketService(spaceName, simpleBucketService, transactionManagerFactory);
    }
}