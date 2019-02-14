package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.VersionedElement;
import com.easydb.easydb.domain.locker.ElementsLocker;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TransactionCommitter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionCommitter.class);

    private final ElementsLockerFactory lockerFactory;
    private final Retryier lockerRetryier;
    private final ElementService elementService;

    TransactionCommitter(ElementsLockerFactory lockerFactory, Retryier lockerRetryier,
                         ElementService elementService) {
        this.lockerFactory = lockerFactory;
        this.lockerRetryier = lockerRetryier;
        this.elementService = elementService;
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
                elementService.addElement(element);
                break;
            case UPDATE:
                performUpdateOperation(o, t);
                break;
            case DELETE:
                elementService.removeElement(o.getBucketName(), o.getElementId());
                break;
        }
    }

    private void performUpdateOperation(Operation o, Transaction t) {
        Optional<Long> version = Optional.ofNullable(t.getReadElements().get(o.getElementId()));
        VersionedElement versionedElement = version
                .map(v -> VersionedElement.of(o.getElementId(), o.getBucketName(), o.getFields(), v))
                .orElseGet(() -> VersionedElement.of(o.getElementId(), o.getBucketName(), o.getFields()));

        elementService.updateElement(versionedElement);
    }
}
