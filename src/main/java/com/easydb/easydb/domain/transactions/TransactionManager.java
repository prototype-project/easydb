package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.locker.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private final UUIDProvider uuidProvider;
    private final TransactionRepository transactionRepository;
    private final BucketService simpleBucketService;
    private final SpaceRepository spaceRepository;
    private final int numberOfRetries;

    private final TransactionEngine transactionEngine;

    public TransactionManager(UUIDProvider uuidProvider,
                              TransactionRepository transactionRepository,
                              SpaceRepository spaceRepository,
                              BucketService simpleBucketService,
                              ElementsLockerFactory lockerFactory, int numberOfRetries) {
        this.uuidProvider = uuidProvider;
        this.transactionRepository = transactionRepository;
        this.spaceRepository = spaceRepository;
        this.simpleBucketService = simpleBucketService;
        this.numberOfRetries = numberOfRetries;

        this.transactionEngine = new TransactionEngine(lockerFactory, simpleBucketService);
    }

    public String beginTransaction(String spaceName) {
        ensureSpaceExists(spaceName);

        String uuid = uuidProvider.generateUUID();
        Transaction transaction = new Transaction(spaceName, uuid);
        transactionRepository.save(transaction);
        return uuid;
    }

    public void addOperation(String transactionId, Operation operation) {
        Transaction transaction = transactionRepository.get(transactionId);
        ensureElementAndBucketExist(operation);

        transaction.addOperation(operation);
        transactionRepository.update(transaction);
    }

    public void commitTransaction(String transactionId) {
        Transaction transaction = transactionRepository.get(transactionId);
        performTransactionWithRetries(transaction);
    }

    private void performTransactionWithRetries(Transaction transaction) {
        for (int i = 0; i < numberOfRetries; i++) {
            try {
                transactionEngine.performTransaction(transaction);
            } catch (Exception e) {
                if (i < numberOfRetries) {
                    logger.error("Retrying transaction {} ...", transaction.getId(), e);
                } else {
                    logger.error("Aborting transaction {} ...", transaction.getId(), e);
                    throw new TransactionAbortedException(
                            "Transaction " + transaction.getId() + " was aborted after " + numberOfRetries + " retries");
                }
            }
        }
    }

    private void ensureSpaceExists(String spaceName) {
        spaceRepository.get(spaceName);
    }

    private void ensureElementAndBucketExist(Operation operation) {
        if (!operation.getType().equals(Operation.OperationType.CREATE)) {
            simpleBucketService.getElement(operation.getElement().getBucketName(), operation.getElement().getId());
        }
    }
}
