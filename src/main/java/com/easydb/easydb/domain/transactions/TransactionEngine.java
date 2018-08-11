package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionEngine {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEngine.class);

    private final ElementsLockerFactory lockerFactory;
    private final SimpleElementOperations simpleElementOperations;

    TransactionEngine(ElementsLockerFactory lockerFactory, SimpleElementOperations simpleElementOperations) {
        this.lockerFactory = lockerFactory;
        this.simpleElementOperations = simpleElementOperations;
    }

    void performTransaction(Transaction transaction) {
        ElementsLocker locker = lockerFactory.build(transaction.getSpaceName());

        try {
             // TODO needed reentrant lock
            transaction.getOperations().forEach(o -> {
                if (operationRequiresLock(o)) {
                    locker.lockElement(o.getBucketName(), o.getElementId());
                }
            });
            transaction.getOperations().forEach(this::performOperation);
        } catch (Exception e) {
            logger.error("Error during committing transaction {}. Making rollback...", transaction.getId());
            throw e;
        } finally {
            transaction.getOperations().forEach(o -> {
                if (operationRequiresLock(o)) {
                    locker.unlockElement(o.getBucketName(), o.getElementId());
                }
            });
        }
    }

    private boolean operationRequiresLock(Operation o) {
        return o.getType() == Operation.OperationType.UPDATE || o.getType() == Operation.OperationType.DELETE;
    }

    private void performOperation(Operation o) {
        Element element = Element.of(o.getElementId(), o.getBucketName(), o.getFields());
        switch (o.getType()) {
            case CREATE:
                simpleElementOperations.addElement(element);
                break;
            case UPDATE:
                simpleElementOperations.updateElement(element);
                break;
            case DELETE:
                simpleElementOperations.removeElement(o.getBucketName(), o.getElementId());
                break;
        }
    }
}
