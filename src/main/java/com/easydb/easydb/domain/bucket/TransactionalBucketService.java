package com.easydb.easydb.domain.bucket;

import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Operation.OperationType;
import com.easydb.easydb.domain.transactions.TransactionManager;
import java.util.List;
import java.util.stream.Collectors;

public class TransactionalBucketService implements BucketService {

    private final String spaceName;
    private final SpaceRepository spaceRepository;
    private final BucketRepository bucketRepository;
    private final TransactionManager transactionManager;
    private final SimpleElementOperations simpleElementOperations;

    public TransactionalBucketService(String spaceName,
                                      SpaceRepository spaceRepository,
                                      BucketRepository bucketRepository,
                                      SimpleElementOperationsFactory simpleElementOperationsFactory,
                                      TransactionManager transactionManager) {
        this.spaceName = spaceName;
        this.spaceRepository = spaceRepository;
        this.bucketRepository = bucketRepository;
        this.simpleElementOperations = simpleElementOperationsFactory.buildSimpleElementOperations(spaceName);
        this.transactionManager = transactionManager;
    }

    @Override
    public boolean bucketExists(String bucketName) {
        return bucketRepository.bucketExists(getBucketName(bucketName));
    }

    @Override
    public void removeBucket(String bucketName) {
        Space space = spaceRepository.get(spaceName);
        space.getBuckets().remove(bucketName);
        spaceRepository.update(space);
        bucketRepository.removeBucket(getBucketName(bucketName));;
    }

    @Override
    public void addElement(Element element) {
        simpleElementOperations.addElement(element);
    }

    @Override
    public Element getElement(String bucketName, String id) {
        return simpleElementOperations.getElement(bucketName, id).toSimpleElement();
    }

    @Override
    public void removeElement(String bucketName, String elementId) {
        String transactionId = transactionManager.beginTransaction(spaceName);
        Operation operation = Operation.of(OperationType.DELETE, bucketName, elementId);
        transactionManager.addOperation(transactionId, operation);
        transactionManager.commitTransaction(transactionId);
    }

    @Override
    public boolean elementExists(String bucketName, String elementId) {
        return simpleElementOperations.elementExists(bucketName, elementId);
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
        return simpleElementOperations.filterElements(query).stream()
                .map(VersionedElement::toSimpleElement)
                .collect(Collectors.toList());
    }

    @Override
    public long getNumberOfElements(String bucketName) {
        return simpleElementOperations.getNumberOfElements(bucketName);
    }

    private String getBucketName(String bucketName) {
        return simpleElementOperations.getBucketNameAccordinglyToSpace(bucketName);
    }
}
