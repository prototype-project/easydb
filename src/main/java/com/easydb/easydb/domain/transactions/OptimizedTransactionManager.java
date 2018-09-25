package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
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
    private final TransactionEngineFactory transactionEngineFactory;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;
    private final ApplicationMetrics metrics;

    public OptimizedTransactionManager(UUIDProvider uuidProvider,
                                       SpaceRepository spaceRepository,
                                       TransactionEngineFactory transactionEngineFactory,
                                       SimpleElementOperationsFactory simpleElementOperationsFactory,
                                       ApplicationMetrics metrics) {
        this.uuidProvider = uuidProvider;
        this.spaceRepository = spaceRepository;
        this.transactionEngineFactory = transactionEngineFactory;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
        this.metrics = metrics;
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
        metrics.getSingleElementTransactionTimer(transaction.getSpaceName())
                .record(() -> commit(transaction));
    }

    private void commit(Transaction transaction) {
        TransactionEngine transactionEngine = transactionEngineFactory.build(transaction.getSpaceName());
        try {
            transactionEngine.commit(transaction);
        } catch (Exception e) {
            logger.error("Aborting transaction {} ...", transaction.getId(), e);
            metrics.getAbortedTransactionCounter(transaction.getSpaceName()).increment();
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
