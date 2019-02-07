package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.VersionedElement;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// TODO transaction rollback
class TransactionEngine {
    private static final Logger logger = LoggerFactory.getLogger(TransactionEngine.class);

    private final ElementsLockerFactory lockerFactory;
    private final Retryier lockerRetryier;
    private final SimpleElementOperations simpleElementOperations;

    TransactionEngine(ElementsLockerFactory lockerFactory, Retryier lockerRetryier,
                      SimpleElementOperations simpleElementOperations) {
        this.lockerFactory = lockerFactory;
        this.lockerRetryier = lockerRetryier;
        this.simpleElementOperations = simpleElementOperations;
    }

    void commit(Transaction transaction) {
        ElementsLocker locker = lockerFactory.build(transaction.getSpaceName());

        try {
            transaction.getOperations().forEach(o -> {
                if (operationRequiresElementLock(o)) {
                    lockerRetryier.performWithRetries(() -> locker.lockElement(o.getBucketName(), o.getElementId()));
                }
            });
            transaction.getOperations().forEach(o -> performOperation(o, transaction));
        } catch (Exception e) {
            logger.error("Error during committing transaction {}. Making rollback...", transaction.getId(), e);
            throw e;
        } finally {
            // TODO think about corner cases (e.g. unlocking not already locked element)
            transaction.getOperations().forEach(o -> {
                if (operationRequiresElementLock(o)) {
                    locker.unlockElement(o.getBucketName(), o.getElementId());
                }
            });
        }
    }

    private boolean operationRequiresElementLock(Operation o) {
        return o.getType() == Operation.OperationType.UPDATE || o.getType() == Operation.OperationType.DELETE;
    }

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
