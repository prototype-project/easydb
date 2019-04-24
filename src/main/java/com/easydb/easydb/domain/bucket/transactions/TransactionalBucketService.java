package com.easydb.easydb.domain.bucket.transactions;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketAlreadyExistsException;
import com.easydb.easydb.domain.bucket.BucketObserversContainer;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.ElementEvent;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryer;
import com.easydb.easydb.domain.transactions.Transaction;

import java.util.Collections;
import java.util.List;

public class TransactionalBucketService implements BucketService {

    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final OptimizedTransactionManager optimizedTransactionManager;
    private final ElementService elementService;
    private final BucketObserversContainer observersContainer;
    private final BucketLocker bucketLocker;
    private final SpaceLocker spaceLocker;
    private final Retryer transactionRetryer;
    private final Retryer lockerRetryer;

    public TransactionalBucketService(SpaceRepository spaceRepository,
                                      BucketRepository bucketRepository,
                                      ElementService elementService,
                                      BucketObserversContainer observersContainer,
                                      OptimizedTransactionManager optimizedTransactionManager,
                                      BucketLocker bucketLocker,
                                      SpaceLocker spaceLocker,
                                      Retryer transactionRetryer,
                                      Retryer lockerRetryer) {
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.elementService = elementService;
        this.observersContainer = observersContainer;
        this.optimizedTransactionManager = optimizedTransactionManager;
        this.bucketLocker = bucketLocker;
        this.spaceLocker = spaceLocker;
        this.transactionRetryer = transactionRetryer;
        this.lockerRetryer = lockerRetryer;
    }

    @Override
    public boolean bucketExists(BucketName bucketName) {
        return bucketRepository.bucketExists(bucketName);
    }

    @Override
    public void removeBucket(BucketName bucketName) {
        Space space = spaceRepository.get(bucketName.getSpaceName());
        space.getBuckets().remove(bucketName.getName());

        lockerRetryer.performWithRetries(() -> spaceLocker.lockSpace(bucketName.getSpaceName()));
        lockerRetryer.performWithRetries(() -> bucketLocker.lockBucket(bucketName));

        try {
            spaceRepository.update(space);
            bucketRepository.removeBucket(bucketName);
        } finally {
            bucketLocker.unlockBucket(bucketName);
            spaceLocker.unlockSpace(bucketName.getSpaceName());
        }
    }

    @Override
    public void createBucket(BucketName bucketName) {
        if (bucketExists(bucketName)) {
            throw new BucketAlreadyExistsException(bucketName.getName());
        }

        Space space = spaceRepository.get(bucketName.getSpaceName());
        space.getBuckets().add(bucketName.getName());

        lockerRetryer.performWithRetries(() -> spaceLocker.lockSpace(bucketName.getSpaceName()));
        try {
            spaceRepository.update(space);
            bucketRepository.createBucket(bucketName);
        } finally {
            spaceLocker.unlockSpace(bucketName.getSpaceName());
        }
    }

    @Override
    public void addElement(Element element) {
        elementService.addElement(element);
        observersContainer.provide(element.getBucketName())
                .addEvent(new ElementEvent(element, ElementEvent.Type.CREATE));
    }

    @Override
    public Element getElement(BucketName bucketName, String id) {
        return elementService.getElement(bucketName, id).toSimpleElement();
    }

    @Override
    public void removeElement(BucketName bucketName, String elementId) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(bucketName.getSpaceName());
        Operation operation = Operation.of(OperationType.DELETE, bucketName.getName(), elementId);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryer.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));

        Element dummyElement = Element.of(elementId, bucketName, Collections.emptyList());
        observersContainer.provide(bucketName)
                .addEvent(new ElementEvent(dummyElement, ElementEvent.Type.DELETE));
    }

    @Override
    public boolean elementExists(BucketName bucketName, String elementId) {
        return elementService.elementExists(bucketName, elementId);
    }

    @Override
    public void updateElement(Element updated) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(updated.getBucketName().getSpaceName());
        Operation operation = Operation.of(OperationType.UPDATE, updated);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryer.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));

        observersContainer.provide(updated.getBucketName())
                .addEvent(new ElementEvent(updated, ElementEvent.Type.UPDATE));
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        return elementService.filterElements(query);
    }

    @Override
    public long getNumberOfElements(BucketName bucketName) {
        return elementService.getNumberOfElements(bucketName);
    }
}
