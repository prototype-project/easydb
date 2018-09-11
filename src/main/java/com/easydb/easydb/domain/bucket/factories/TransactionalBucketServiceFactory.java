package com.easydb.easydb.domain.bucket.factories;

import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.TransactionalBucketService;
import com.easydb.easydb.domain.space.SpaceDoesNotExistException;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.TransactionRetryier;

public class TransactionalBucketServiceFactory implements BucketServiceFactory {
    private final BucketRepository bucketRepository;
    private final SpaceRepository spaceRepository;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;
    private final OptimizedTransactionManager defaultTransactionManager;
    private final TransactionRetryier transactionRetryier;

    public TransactionalBucketServiceFactory(SpaceRepository spaceRepository,
                                             BucketRepository bucketRepository,
                                             SimpleElementOperationsFactory simpleElementOperationsFactory,
                                             OptimizedTransactionManager defaultTransactionManager,
                                             TransactionRetryier transactionRetryier) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
        this.defaultTransactionManager = defaultTransactionManager;
        this.transactionRetryier = transactionRetryier;
    }

    public BucketService buildBucketService(String spaceName) {
        if (!spaceRepository.exists(spaceName)) {
            throw new SpaceDoesNotExistException(spaceName);
        }
        return new TransactionalBucketService(spaceName, spaceRepository, bucketRepository,
                simpleElementOperationsFactory, defaultTransactionManager, transactionRetryier);
    }
}