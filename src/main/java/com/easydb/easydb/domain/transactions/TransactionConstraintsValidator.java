package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketDoesNotExistException;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.bucket.transactions.BucketRepository;
import com.easydb.easydb.domain.space.SpaceRepository;

public class TransactionConstraintsValidator {
    private SpaceRepository spaceRepository;
    private BucketRepository bucketRepository;
    private ElementService elementService;

    public TransactionConstraintsValidator(SpaceRepository spaceRepository,
                                           BucketRepository bucketRepository,
                                           ElementService elementService) {
        this.bucketRepository = bucketRepository;
        this.spaceRepository = spaceRepository;
        this.elementService = elementService;
    }

    void ensureSpaceExists(String spaceName) {
        spaceRepository.get(spaceName);
    }

    void ensureOperationConstraints(String spaceName, Operation operation) {
        BucketName bucketName = new BucketName(spaceName, operation.getBucketName());
        if (!operation.getType().equals(Operation.OperationType.CREATE)) {
            elementService.getElement(bucketName, operation.getElementId());
        } else {
            if (!bucketRepository.bucketExists(bucketName)) {
                throw new BucketDoesNotExistException(bucketName.getName());
            }
        }
    }
}
