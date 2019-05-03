package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementEvent;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import com.easydb.easydb.domain.locker.ElementsLocker;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionCommitter {
    private static final Logger logger = LoggerFactory.getLogger(TransactionCommitter.class);

    private final ElementsLocker elementsLocker;
    private final Retryer lockerRetryer;
    private final ElementService elementService;
    private final BucketObserversContainer observersContainer;

    public TransactionCommitter(ElementsLocker locker, Retryer lockerRetryer,
                                ElementService elementService,
                                BucketObserversContainer observersContainer) {
        this.elementsLocker = locker;
        this.lockerRetryer = lockerRetryer;
        this.elementService = elementService;
        this.observersContainer = observersContainer;
    }

    void commit(Transaction transaction) {
        List<ElementEvent> eventsToEmit = new ArrayList<>();
        try {
            setupLocks(transaction);

            transaction.getOperations().forEach(o -> performOperation(o, transaction, eventsToEmit));
        } catch (Exception e) {
            logger.info("Error during committing transaction {}. Making rollback...", transaction.getId(), e);
            throw e;
        } finally {
            cleanupLocks(transaction);
        }
        emit(eventsToEmit);
    }

    private boolean operationRequiresElementLock(Operation o) {
        return o.getType() == Operation.OperationType.UPDATE || o.getType() == Operation.OperationType.DELETE;
    }

    private void performOperation(Operation o, Transaction t, List<ElementEvent> events) {
        BucketName bucketName = new BucketName(t.getSpaceName(), o.getBucketName());

        Element element = Element.of(o.getElementId(), bucketName, o.getFields());
        switch (o.getType()) {
            case CREATE:
                elementService.addElement(element);
                events.add(new ElementEvent(element, ElementEvent.Type.CREATE));
                break;
            case UPDATE:
                events.add(new ElementEvent(performUpdateOperation(o, t), ElementEvent.Type.UPDATE));
                break;
            case DELETE:
                Element removed = elementService.removeElement(bucketName, o.getElementId()).toSimpleElement();
                events.add(new ElementEvent(removed, ElementEvent.Type.DELETE));
                break;
        }
    }

    private Element performUpdateOperation(Operation o, Transaction t) {
        BucketName bucketName = new BucketName(t.getSpaceName(), o.getBucketName());

        Optional<Long> version = Optional.ofNullable(t.getReadElements().get(o.getElementId()));
        VersionedElement versionedElement = version.map(v -> VersionedElement.of(o.getElementId(), bucketName, o.getFields(), v))
                .orElseGet(() -> VersionedElement.of(o.getElementId(), bucketName, o.getFields()));

        elementService.updateElement(versionedElement);
        return versionedElement.toSimpleElement();
    }

    private void setupLocks(Transaction transaction) {
        transaction.getOperations().forEach(o -> {
            if (operationRequiresElementLock(o)) {
                BucketName bucketName = new BucketName(transaction.getSpaceName(), o.getBucketName());
                lockerRetryer.performWithRetries(() -> elementsLocker.lockElement(bucketName, o.getElementId()));
            }
        });
    }

    private void cleanupLocks(Transaction transaction) {
        // TODO think about corner cases (e.g. unlocking not already locked element)
        transaction.getOperations().forEach(o -> {
            if (operationRequiresElementLock(o)) {
                BucketName bucketName = new BucketName(transaction.getSpaceName(), o.getBucketName());
                elementsLocker.unlockElement(bucketName, o.getElementId());
            }
        });
    }

    private void emit(List<ElementEvent> events) {
        events.forEach(e -> observersContainer.get(e.getElement().getBucketName())
                .ifPresent(observer -> observer.addEvent(e)));
    }
}
