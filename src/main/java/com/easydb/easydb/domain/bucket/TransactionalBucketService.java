package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Transaction;
import com.easydb.easydb.domain.transactions.Retryier;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionalBucketService implements BucketService {

    private final String spaceName;
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final OptimizedTransactionManager optimizedTransactionManager;
    private final SimpleElementOperations simpleElementOperations;
    private final BucketLocker bucketLocker;
    private final SpaceLocker spaceLocker;
    private final Retryier transactionRetryier;
    private final Retryier lockerRetryier;

    public TransactionalBucketService(String spaceName,
                                      SpaceRepository spaceRepository,
                                      BucketRepository bucketRepository,
                                      SimpleElementOperationsFactory simpleElementOperationsFactory,
                                      OptimizedTransactionManager optimizedTransactionManager,
                                      BucketLocker bucketLocker,
                                      SpaceLocker spaceLocker,
                                      Retryier transactionRetryier,
                                      Retryier lockerRetryier) {
        this.spaceName = spaceName;
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.simpleElementOperations = simpleElementOperationsFactory.buildSimpleElementOperations(spaceName);
        this.optimizedTransactionManager = optimizedTransactionManager;
        this.bucketLocker = bucketLocker;
        this.spaceLocker = spaceLocker;
        this.transactionRetryier = transactionRetryier;
        this.lockerRetryier = lockerRetryier;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return bucketRepository.bucketExists(getBucketName(bucketName));
    }

    @Override
    public void removeBucket(String bucketName) {
        lockerRetryier.performWithRetries(() -> spaceLocker.lockSpace(spaceName));
        lockerRetryier.performWithRetries(() -> bucketLocker.lockBucket(spaceName, bucketName));

        try {
            Space space = spaceRepository.get(spaceName);
            space.getBuckets().remove(bucketName);
            spaceRepository.update(space);
            bucketRepository.removeBucket(getBucketName(bucketName));
        } finally {
            bucketLocker.unlockBucket(spaceName, bucketName);
            spaceLocker.unlockSpace(spaceName);
        }
    }

    @Override
    public void addElement(Element element) {
        // TODO maybe needs optimization to lock only when bucket not in space
        lockerRetryier.performWithRetries(() -> spaceLocker.lockSpace(spaceName));
        try {
            simpleElementOperations.addElement(element);
        } finally {
            spaceLocker.unlockSpace(spaceName);
        }
    }

    @Override
    public Element getElement(String bucketName, String id) {
        return simpleElementOperations.getElement(bucketName, id).toSimpleElement();
    }

    @Override
    public void removeElement(String bucketName, String elementId) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.DELETE, bucketName, elementId);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryier.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        return simpleElementOperations.elementExists(bucketName, elementId);
    }

    @Override
    public void updateElement(Element toUpdate) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.UPDATE, toUpdate);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryier.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        return simpleElementOperations.filterElements(query).stream()
                .map(VersionedElement::toSimpleElement)
                .collect(Collectors.toList());
    }

    @Override
    public long getNumberOfElements(String bucketName) {
        return simpleElementOperations.getNumberOfElements(bucketName);
    }

    private String getBucketName(String bucketName) {
        return NamesResolver.resolve(spaceName, bucketName);
    }
}
