package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import com.easydb.easydb.domain.locker.ElementsLocker;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionCommitter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionCommitter.class);

    private final ElementsLocker elementsLocker;
    private final Retryer lockerRetryer;
    private final ElementService elementService;

    public TransactionCommitter(ElementsLocker locker, Retryer lockerRetryer,
                                ElementService elementService) {
        this.elementsLocker = locker;
        this.lockerRetryer = lockerRetryer;
        this.elementService = elementService;
    }

    void commit(Transaction transaction) {
        try {
            transaction.getOperations().forEach(o -> {
                if (operationRequiresElementLock(o)) {
                    BucketName bucketName = new BucketName(transaction.getSpaceName(), o.getBucketName());
                    lockerRetryer.performWithRetries(() -> elementsLocker.lockElement(bucketName, o.getElementId()));
                }
            });
            transaction.getOperations().forEach(o -> performOperation(o, transaction));
        } catch (Exception e) {
            logger.info("Error during committing transaction {}. Making rollback...", transaction.getId(), e);
            throw e;
        } finally {
            // TODO think about corner cases (e.g. unlocking not already locked element)
            transaction.getOperations().forEach(o -> {
                if (operationRequiresElementLock(o)) {
                    BucketName bucketName = new BucketName(transaction.getSpaceName(), o.getBucketName());
                    elementsLocker.unlockElement(bucketName, o.getElementId());
                }
            });
        }
    }

    private boolean operationRequiresElementLock(Operation o) {
        return o.getType() == Operation.OperationType.UPDATE || o.getType() == Operation.OperationType.DELETE;
    }

    private void performOperation(Operation o, Transaction t) {
        BucketName bucketName = new BucketName(t.getSpaceName(), o.getBucketName());

        Element element = Element.of(o.getElementId(), bucketName, o.getFields());
        switch (o.getType()) {
            case CREATE:
                elementService.addElement(element);
                break;
            case UPDATE:
                performUpdateOperation(o, t);
                break;
            case DELETE:
                elementService.removeElement(bucketName, o.getElementId());
                break;
        }
    }

    private void performUpdateOperation(Operation o, Transaction t) {
        BucketName bucketName = new BucketName(t.getSpaceName(), o.getBucketName());

        Optional<Long> version = Optional.ofNullable(t.getReadElements().get(o.getElementId()));
        VersionedElement versionedElement = version
                .map(v -> VersionedElement.of(o.getElementId(), bucketName, o.getFields(), v))
                .orElseGet(() -> VersionedElement.of(o.getElementId(), bucketName, o.getFields()));

        elementService.updateElement(versionedElement);
    }
}
