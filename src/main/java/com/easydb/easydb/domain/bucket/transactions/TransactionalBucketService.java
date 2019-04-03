package com.easydb.easydb.domain.bucket.transactions;

import com.easydb.easydb.domain.bucket.BucketAlreadyExistsException;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;
import com.easydb.easydb.domain.locker.BucketLocker;
import com.easydb.easydb.domain.locker.SpaceLocker;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.easydb.easydb.domain.transactions.OptimizedTransactionManager;
import com.easydb.easydb.domain.transactions.Retryer;
import com.easydb.easydb.domain.transactions.Transaction;

import java.util.List;

public class TransactionalBucketService implements BucketService {

    private final String spaceName;
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final OptimizedTransactionManager optimizedTransactionManager;
    private final TransactionalElementService elementService;
    private final BucketLocker bucketLocker;
    private final SpaceLocker spaceLocker;
    private final Retryer transactionRetryer;
    private final Retryer lockerRetryer;

    public TransactionalBucketService(String spaceName,
                                      SpaceRepository spaceRepository,
                                      BucketRepository bucketRepository,
                                      ElementServiceFactory elementServiceFactory,
                                      OptimizedTransactionManager optimizedTransactionManager,
                                      BucketLocker bucketLocker,
                                      SpaceLocker spaceLocker,
                                      Retryer transactionRetryer,
                                      Retryer lockerRetryer) {
        this.spaceName = spaceName;
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.elementService = elementServiceFactory.buildElementService(spaceName);
        this.optimizedTransactionManager = optimizedTransactionManager;
        this.bucketLocker = bucketLocker;
        this.spaceLocker = spaceLocker;
        this.transactionRetryer = transactionRetryer;
        this.lockerRetryer = lockerRetryer;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return bucketRepository.bucketExists(getBucketName(bucketName));
    }

    @Override
    public void removeBucket(String bucketName) {
        Space space = spaceRepository.get(spaceName);
        space.getBuckets().remove(bucketName);

        lockerRetryer.performWithRetries(() -> spaceLocker.lockSpace(spaceName));
        lockerRetryer.performWithRetries(() -> bucketLocker.lockBucket(spaceName, bucketName));

        try {
            spaceRepository.update(space);
            bucketRepository.removeBucket(getBucketName(bucketName));
        } finally {
            bucketLocker.unlockBucket(spaceName, bucketName);
            spaceLocker.unlockSpace(spaceName);
        }
    }

    @Override
    public void createBucket(String bucketName) {
        if (bucketExists(bucketName)) {
            throw new BucketAlreadyExistsException(bucketName);
        }

        Space space = spaceRepository.get(spaceName);
        space.getBuckets().add(bucketName);

        lockerRetryer.performWithRetries(() -> spaceLocker.lockSpace(spaceName));
        try {
            spaceRepository.update(space);
            bucketRepository.createBucket(getBucketName(bucketName));
        } finally {
            spaceLocker.unlockSpace(spaceName);
        }
    }

    @Override
    public void addElement(Element element) {
        elementService.addElement(element);
    }

    @Override
    public Element getElement(String bucketName, String id) {
        return elementService.getElement(bucketName, id).toSimpleElement();
    }

    @Override
    public void removeElement(String bucketName, String elementId) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.DELETE, bucketName, elementId);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryer.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        return elementService.elementExists(bucketName, elementId);
    }

    @Override
    public void updateElement(Element toUpdate) {
        Transaction transaction = optimizedTransactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.UPDATE, toUpdate);
        optimizedTransactionManager.addOperation(transaction, operation);
        transactionRetryer.performWithRetries(() -> optimizedTransactionManager.commitTransaction(transaction));
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        return elementService.filterElements(query);
    }

    @Override
    public long getNumberOfElements(String bucketName) {
        return elementService.getNumberOfElements(bucketName);
    }

    private String getBucketName(String bucketName) {
        return NamesResolver.resolve(spaceName, bucketName);
    }
}
