package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.ElementsLockerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionEngine {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEngine.class);

    private final ElementsLockerFactory lockerFactory;
    private final BucketService simpleBucketService;

    TransactionEngine(ElementsLockerFactory lockerFactory, BucketService bucketService) {
        this.lockerFactory = lockerFactory;
        this.simpleBucketService = bucketService;
    }

    void performTransaction(Transaction transaction) {
        ElementsLocker locker = lockerFactory.build(transaction.getSpaceName());

        try {
            // needed reentrant lock
            transaction.getOperations().forEach(o -> {
                if (operationRequiresLock(o)) {
                    locker.lockElement(o.getElement().getBucketName(), o.getElement().getId());
                }
            });
            transaction.getOperations().forEach(o -> performOperation(o, simpleBucketService));
        } catch (Exception e) {
            logger.error("Error during committing transaction {}. Making rollback...", transaction.getId());
            throw e;
        } finally {
            transaction.getOperations().forEach(o -> {
                if (operationRequiresLock(o)) {
                    locker.unlockElement(o.getElement().getBucketName(), o.getElement().getId());
                }
            });
        }
    }

    private boolean operationRequiresLock(Operation o) {
        return o.getType() == Operation.OperationType.UPDATE || o.getType() == Operation.OperationType.DELETE;
    }

    private void performOperation(Operation o, BucketService bucketService) {
        switch (o.getType()) {
            case CREATE:
                bucketService.addElement(o.getElement());
                break;
            case UPDATE:
                bucketService.updateElement(o.getElement());
                break;
            case DELETE:
                bucketService.removeElement(o.getElement().getBucketName(), o.getElement().getId());
                break;
            case READ:
                ;
        }
    }
}
