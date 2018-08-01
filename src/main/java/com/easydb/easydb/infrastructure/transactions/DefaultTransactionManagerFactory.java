package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.locker.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.TransactionManager;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;
import com.easydb.easydb.domain.transactions.TransactionRepository;

public class DefaultTransactionManagerFactory implements TransactionManagerFactory {

    private final UUIDProvider uuidProvider;
    private final TransactionRepository transactionRepository;
    private final SpaceRepository spaceRepository;
    private final ElementsLockerFactory lockerFactory;
    private final int numberOfRetries;

    public DefaultTransactionManagerFactory(UUIDProvider uuidProvider,
                                            TransactionRepository transactionRepository,
                                            SpaceRepository spaceRepository,
                                            ElementsLockerFactory lockerFactory,
                                            int numberOfRetries) {
        this.uuidProvider = uuidProvider;
        this.transactionRepository = transactionRepository;
        this.spaceRepository = spaceRepository;
        this.lockerFactory = lockerFactory;
        this.numberOfRetries = numberOfRetries;
    }

    @Override
    public TransactionManager buildTransactionManager(BucketService simpleBucketService) {
        return new TransactionManager(uuidProvider, transactionRepository, spaceRepository,
                simpleBucketService, lockerFactory, numberOfRetries);
    }
}
