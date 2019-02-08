package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.BucketRepository;
import com.easydb.easydb.domain.bucket.NamesResolver;
import com.easydb.easydb.domain.bucket.factories.ElementServiceFactory;
import com.easydb.easydb.domain.space.SpaceRepository;

public class TransactionConstraintsValidator {
    private SpaceRepository spaceRepository;
    private BucketRepository bucketRepository;
    private ElementServiceFactory elementServiceFactory;

    public TransactionConstraintsValidator(SpaceRepository spaceRepository,
                                           BucketRepository bucketRepository,
                                           ElementServiceFactory elementServiceFactory) {
        this.bucketRepository = bucketRepository;
        this.spaceRepository = spaceRepository;
        this.elementServiceFactory = elementServiceFactory;
    }

    void ensureSpaceExists(String spaceName) {
        spaceRepository.get(spaceName);
    }

    void ensureOperationConstraints(String spaceName, Operation operation) {
        if (!operation.getType().equals(Operation.OperationType.CREATE)) {
            elementServiceFactory.buildElementService(spaceName)
                    .getElement(operation.getBucketName(), operation.getElementId());
        } else {
            String resolvedName = NamesResolver.resolve(spaceName, operation.getBucketName());
            if (!bucketRepository.bucketExists(resolvedName)) {
                throw new BucketDoesNotExistException(resolvedName);
            }
        }
    }

}
