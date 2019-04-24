package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.ElementService;
import com.easydb.easydb.domain.space.UUIDProvider;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PersistentTransactionManager {
    private static final Logger logger = LoggerFactory.getLogger(PersistentTransactionManager.class);

    private final UUIDProvider uuidProvider;
    private final TransactionRepository transactionRepository;
    private final TransactionConstraintsValidator transactionConstraintsValidator;
    private final TransactionCommitter transactionCommitter;
    private final ElementService elementService;
    private final ApplicationMetrics metrics;

    public PersistentTransactionManager(UUIDProvider uuidProvider,
                                        TransactionRepository transactionRepository,
                                        TransactionConstraintsValidator transactionConstraintsValidator,
                                        TransactionCommitter transactionCommitter, ElementService elementService, ApplicationMetrics metrics) {
        this.uuidProvider = uuidProvider;
        this.transactionRepository = transactionRepository;
        this.transactionConstraintsValidator = transactionConstraintsValidator;
        this.transactionCommitter = transactionCommitter;
        this.elementService = elementService;
        this.metrics = metrics;
    }

    public String beginTransaction(String spaceName) {
        transactionConstraintsValidator.ensureSpaceExists(spaceName);

        String uuid = uuidProvider.generateUUID();
        Transaction transaction = new Transaction(spaceName, uuid);
        transactionRepository.save(transaction);
        return uuid;
    }

    public OperationResult addOperation(String transactionId, Operation operation) {
        Transaction transaction = transactionRepository.get(transactionId);

        transactionConstraintsValidator.ensureOperationConstraints(transaction.getSpaceName(), operation);

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

        try {
            transactionCommitter.commit(transaction);
        } catch (Exception e) {
            logger.info("Aborting transaction {} ...", transaction.getId(), e);
            metrics.abortedTransactionCounter(transaction.getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getId() + " was aborted", e);
        } finally {
            transactionRepository.delete(transaction);
        }
    }

    private OperationResult getResultForOperation(Transaction t, Operation o) {
        BucketName bucketName = new BucketName(t.getSpaceName(), o.getBucketName());
        try {
            if (o.getType().equals(Operation.OperationType.READ)) {
                return Optional.ofNullable(t.getReadElements().get(o.getElementId()))
                        .map(version ->
                                OperationResult.of(elementService.getElement(bucketName, o.getElementId(), version), t.getSpaceName()))
                        .orElseGet(() -> OperationResult.of(elementService.getElement(bucketName, o.getElementId()), t.getSpaceName()));
            }
            return OperationResult.emptyResult(t.getSpaceName());
        } catch (ConcurrentTransactionDetectedException e) {
            logger.info("Aborting transaction {} ...", t.getId(), e);
            metrics.abortedTransactionCounter(t.getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + t.getId() + " was aborted", e);
        }
    }

    private void addReadElementIfNeeded(Transaction transaction, OperationResult result) {
        result.getElement().ifPresent(transaction::addReadElement);
    }
}
