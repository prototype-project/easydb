package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.locker.factories.ElementsLockerFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
  The purpose of this class is to manage transactions in the optimized way. We use it during
  creating transactions for single BucketService operation e.g updateElement() or removeElement().
 */
public class OptimizedTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(OptimizedTransactionManager.class);

    private final UUIDProvider uuidProvider;
    private final SpaceRepository spaceRepository;
    private final ElementsLockerFactory lockerFactory;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;

    public OptimizedTransactionManager(UUIDProvider uuidProvider,
                                       SpaceRepository spaceRepository,
                                       ElementsLockerFactory lockerFactory,
                                       SimpleElementOperationsFactory simpleElementOperationsFactory) {
        this.uuidProvider = uuidProvider;
        this.spaceRepository = spaceRepository;
        this.lockerFactory = lockerFactory;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
    }

    public Transaction beginTransaction(String spaceName) {
        ensureSpaceExists(spaceName);
        String uuid = uuidProvider.generateUUID();
        return new Transaction(spaceName, uuid);
    }

    public void addOperation(Transaction transaction, Operation operation) {
        ensureElementAndBucketExist(transaction.getSpaceName(), operation);
        transaction.addOperation(operation);
    }

    public void commitTransaction(Transaction transaction) {
        SimpleElementOperations simpleElementOperations =
                simpleElementOperationsFactory.buildSimpleElementOperations(transaction.getSpaceName());
        TransactionEngine transactionEngine = new TransactionEngine(lockerFactory, simpleElementOperations);
        try {
            transactionEngine.commit(transaction);
        } catch (Exception e) {
            logger.error("Aborting transaction {} ...", transaction.getId(), e);
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getId() + " was aborted", e);
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
}
