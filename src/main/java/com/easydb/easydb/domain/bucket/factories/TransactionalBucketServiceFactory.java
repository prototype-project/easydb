package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.TransactionalBucketService;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryier;

public class TransactionalBucketServiceFactory implements BucketServiceFactory {
    private final BucketRepository bucketRepository;
    private final SpaceRepository spaceRepository;
    private final ElementServiceFactory elementServiceFactory;
    private final OptimizedTransactionManager defaultTransactionManager;
    private final BucketLocker bucketLocker;
    private final SpaceLocker spaceLocker;
    private final Retryier transactionRetryier;
    private final Retryier lockerRetryier;

    public TransactionalBucketServiceFactory(SpaceRepository spaceRepository,
                                             BucketRepository bucketRepository,
                                             ElementServiceFactory elementServiceFactory,
                                             OptimizedTransactionManager defaultTransactionManager,
                                             BucketLocker bucketLocker,
                                             SpaceLocker spaceLocker,
                                             Retryier transactionRetryier,
                                             Retryier lockerRetryier) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.elementServiceFactory = elementServiceFactory;
        this.defaultTransactionManager = defaultTransactionManager;
        this.bucketLocker = bucketLocker;
        this.spaceLocker = spaceLocker;
        this.transactionRetryier = transactionRetryier;
        this.lockerRetryier = lockerRetryier;
    }

    public BucketService buildBucketService(String spaceName) {
        if (!spaceRepository.exists(spaceName)) {
            throw new SpaceDoesNotExistException(spaceName);
        }
        return new TransactionalBucketService(spaceName, spaceRepository, bucketRepository,
                elementServiceFactory, defaultTransactionManager, bucketLocker,
                spaceLocker, transactionRetryier, lockerRetryier);
    }
}