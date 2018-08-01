package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.easydb.easydb.domain.transactions.TransactionManager;
import com.easydb.easydb.domain.transactions.TransactionManagerFactory;
import java.util.ArrayList;
import java.util.List;


public class TransactionalBucketService implements BucketService {

    private final String spaceName;
    private final BucketService simpleBucketService;
    private final TransactionManager transactionManager;

    public TransactionalBucketService(String spaceName,
                                      BucketService simpleBucketService,
                                      TransactionManagerFactory transactionManagerFactory) {
        this.spaceName = spaceName;
        this.simpleBucketService = simpleBucketService;
        this.transactionManager = transactionManagerFactory.buildTransactionManager(simpleBucketService);
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return simpleBucketService.bucketExists(bucketName);
    }

    @Override
    public void removeBucket(String bucketName) {
        simpleBucketService.removeBucket(bucketName);
    }

    @Override
    public void addElement(Element element) {
        simpleBucketService.addElement(element);
    }

    @Override
    public Element getElement(String bucketName, String id) {
        return simpleBucketService.getElement(bucketName, id);
    }

    @Override
    public void removeElement(String bucketName, String elementId) {
        String transactionId = transactionManager.beginTransaction(spaceName);
        // TODO think about element id here
        Operation operation = Operation.of(OperationType.DELETE, Element.of(elementId, bucketName, new ArrayList<>()));
        transactionManager.addOperation(transactionId, operation);
        transactionManager.commitTransaction(transactionId);
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        return simpleBucketService.elementExists(bucketName, elementId);
    }

    @Override
    public void updateElement(Element toUpdate) {
        String transactionId = transactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.UPDATE, toUpdate);
        transactionManager.addOperation(transactionId, operation);
        transactionManager.commitTransaction(transactionId);
    }

    @Override
    public List<Element> filterElements(BucketQuery query) {
        return simpleBucketService.filterElements(query);
    }

    @Override
    public long getNumberOfElements(String bucketName) {
        return simpleBucketService.getNumberOfElements(bucketName);
    }
}
