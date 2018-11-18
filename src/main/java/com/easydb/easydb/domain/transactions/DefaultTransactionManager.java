package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.SimpleElementOperations;
import com.easydb.easydb.domain.bucket.factories.SimpleElementOperationsFactory;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.UUIDProvider;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DefaultTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(DefaultTransactionManager.class);

    private final UUIDProvider uuidProvider;
    private final TransactionRepository transactionRepository;
    private final SpaceRepository spaceRepository;
    private final TransactionEngineFactory transactionEngineFactory;
    private final SimpleElementOperationsFactory simpleElementOperationsFactory;
    private final ApplicationMetrics metrics;

    public DefaultTransactionManager(UUIDProvider uuidProvider,
                                     TransactionRepository transactionRepository,
                                     SpaceRepository spaceRepository,
                                     TransactionEngineFactory transactionEngineFactory,
                                     SimpleElementOperationsFactory simpleElementOperationsFactory,
                                     ApplicationMetrics metrics) {
        this.uuidProvider = uuidProvider;
        this.transactionRepository = transactionRepository;
        this.spaceRepository = spaceRepository;
        this.transactionEngineFactory = transactionEngineFactory;
        this.simpleElementOperationsFactory = simpleElementOperationsFactory;
        this.metrics = metrics;
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

        OperationResult result = getResultForOperation(transaction, operation);

        transaction.addOperation(operation);

        addReadElementIfNeeded(transaction, result);

        transactionRepository.update(transaction);
        return result;
    }

    public Transaction commitTransaction(String transactionId) {
        Transaction transaction = transactionRepository.get(transactionId);
        metrics.compoundTransactionTimer(transaction.getSpaceName())
                .record(() -> commit(transaction));
        return transaction;
    }

    private void commit(Transaction transaction) {
        TransactionEngine transactionEngine = transactionEngineFactory.build(transaction.getSpaceName());

        try {
            transactionEngine.commit(transaction);
        } catch (Exception e) {
            logger.error("Aborting transaction {} ...", transaction.getId(), e);
            metrics.abortedTransactionCounter(transaction.getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getId() + " was aborted", e);
        }
    }

    private void ensureSpaceExists(String spaceName) {
        spaceRepository.get(spaceName);
    }

    private void ensureElementAndBucketExist(String spaceName, Operation operation) {
        if (!operation.getType().equals(Operation.OperationType.CREATE) &&
                !operation.getType().equals(Operation.OperationType.READ)) {
            simpleElementOperationsFactory.buildSimpleElementOperations(spaceName)
                    .getElement(operation.getBucketName(), operation.getElementId());
        }
    }

    private OperationResult getResultForOperation(Transaction t, Operation o) {
        try {
            if (o.getType().equals(Operation.OperationType.READ)) {
                SimpleElementOperations simpleElementOperations = simpleElementOperationsFactory
                        .buildSimpleElementOperations(t.getSpaceName());

                return Optional.ofNullable(t.getReadElements().get(o.getElementId()))
                        .map(version ->
                                OperationResult.of(simpleElementOperations.getElement(o.getBucketName(), o.getElementId(), version), t.getSpaceName()))
                        .orElseGet(() -> OperationResult.of(simpleElementOperations.getElement(o.getBucketName(), o.getElementId()), t.getSpaceName()));
            }
            return OperationResult.emptyResult(t.getSpaceName());
        } catch (ConcurrentTransactionDetectedException e) {
            logger.error("Aborting transaction {} ...", t.getId(), e);
            metrics.abortedTransactionCounter(t.getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + t.getId() + " was aborted", e);
        }
    }

    private void addReadElementIfNeeded(Transaction transaction, OperationResult result) {
        result.getElement().ifPresent(transaction::addReadElement);
    }
}
