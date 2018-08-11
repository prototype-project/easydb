package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.VersionedElement;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(TransactionManager.class);

    private final UUIDProvider uuidProvider;
    private final TransactionRepository transactionRepository;
    private final SpaceRepository spaceRepository;
    private final ElementsLockerFactory lockerFactory;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;

    public TransactionManager(UUIDProvider uuidProvider,
                              TransactionRepository transactionRepository,
                              SpaceRepository spaceRepository,
                              ElementsLockerFactory lockerFactory,
                              SimpleElementOperationsFactory simpleElementOperationsFactory) {
        this.uuidProvider = uuidProvider;
        this.transactionRepository = transactionRepository;
        this.spaceRepository = spaceRepository;
        this.lockerFactory = lockerFactory;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
    }

    public String beginTransaction(String spaceName) {
        ensureSpaceExists(spaceName);

        String uuid = uuidProvider.generateUUID();
        Transaction transaction = new Transaction(spaceName, uuid);
        transactionRepository.save(transaction);
        return uuid;
    }

    public OperationResult addOperation(String transactionId, Operation operation) {
        Transaction transaction = transactionRepository.get(transactionId);
        ensureElementAndBucketExist(transaction.getSpaceName(), operation);

        transaction.addOperation(operation);

        OperationResult result = getResultForOperation(transaction.getSpaceName(), operation);
        addReadElementIfNeeded(transaction, result);

        transactionRepository.update(transaction);
        return result;
    }

    public void commitTransaction(String transactionId) {
        Transaction transaction = transactionRepository.get(transactionId);
        performTransaction(transaction);
    }

    private void performTransaction(Transaction transaction) {
        SimpleElementOperations simpleElementOperations =
                simpleElementOperationsFactory.buildSimpleElementOperations(transaction.getSpaceName());
        TransactionEngine transactionEngine = new TransactionEngine(lockerFactory, simpleElementOperations);
        try {
            transactionEngine.performTransaction(transaction);
        } catch (Exception e) {
            logger.error("Aborting transaction {} ...", transaction.getId(), e);
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getId() + " was aborted");
        }
    }

    private void ensureSpaceExists(String spaceName) {
        spaceRepository.get(spaceName);
    }

    private void ensureElementAndBucketExist(String spaceName, Operation operation) {
        if (!operation.getType().equals(Operation.OperationType.CREATE)) {
            simpleElementOperationsFactory.buildSimpleElementOperations(spaceName)
                    .getElement(operation.getBucketName(), operation.getElementId());
        }
    }

    private OperationResult getResultForOperation(String spaceName, Operation o) {
        if (o.getType().equals(Operation.OperationType.READ)) {
            VersionedElement element = simpleElementOperationsFactory.buildSimpleElementOperations(spaceName)
                    .getElement(o.getBucketName(), o.getElementId());
            return OperationResult.of(element);
        }
        return OperationResult.emptyResult();
    }

    private void addReadElementIfNeeded(Transaction transaction, OperationResult result) {
        result.getElement().ifPresent(transaction::addReadElement);
    }
}
