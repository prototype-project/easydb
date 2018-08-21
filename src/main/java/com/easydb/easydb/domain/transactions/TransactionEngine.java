package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.VersionedElement;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO transaction rollback, add metrics about transaction time, aborted count itd...
class TransactionEngine {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEngine.class);

    private final ElementsLockerFactory lockerFactory;
    private final SimpleElementOperations simpleElementOperations;

    TransactionEngine(ElementsLockerFactory lockerFactory, SimpleElementOperations simpleElementOperations) {
        this.lockerFactory = lockerFactory;
        this.simpleElementOperations = simpleElementOperations;
    }

    void commit(Transaction transaction) {
        ElementsLocker locker = lockerFactory.build(transaction.getSpaceName());

        try {
            // TODO needed reentrant lock
            transaction.getOperations().forEach(o -> {
                if (operationRequiresLock(o)) {
                    locker.lockElement(o.getBucketName(), o.getElementId());
                }
            });
            transaction.getOperations().forEach(o -> performOperation(o, transaction));
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

    // TODO add support for operations on buckets and spaces
    private void performOperation(Operation o, Transaction t) {
        Element element = Element.of(o.getElementId(), o.getBucketName(), o.getFields());
        switch (o.getType()) {
            case CREATE:
                simpleElementOperations.addElement(element);
                break;
            case UPDATE:
                performUpdateOperation(o, t);
                break;
            case DELETE:
                simpleElementOperations.removeElement(o.getBucketName(), o.getElementId());
                break;
        }
    }

    private void performUpdateOperation(Operation o, Transaction t) {
        Optional<Long> version = Optional.ofNullable(t.getReadElements().get(o.getElementId()));
        VersionedElement versionedElement = version
                .map(v -> VersionedElement.of(o.getElementId(), o.getBucketName(), o.getFields(), v))
                .orElseGet(() -> VersionedElement.of(o.getElementId(), o.getBucketName(), o.getFields()));

        simpleElementOperations.updateElement(versionedElement);
    }
}
