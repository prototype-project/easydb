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

    public OperationResult addOperation(TransactionKey transactionKey, Operation operation) {
        transactionConstraintsValidator.ensureSpaceExists(transactionKey.getSpaceName());

        Transaction transaction = transactionRepository.get(transactionKey);

        transactionConstraintsValidator.ensureOperationConstraints(transaction.getKey().getSpaceName(), operation);

        OperationResult result = getResultForOperation(transaction, operation);

        transaction.addOperation(operation);

        addReadElementIfNeeded(transaction, result);

        transactionRepository.update(transaction);
        return result;
    }

    public void commitTransaction(TransactionKey transactionKey) {
        transactionConstraintsValidator.ensureSpaceExists(transactionKey.getSpaceName());

        Transaction transaction = transactionRepository.get(transactionKey);
        metrics.compoundTransactionTimer(transaction.getKey().getSpaceName())
                .record(() -> commit(transaction));
    }

    private void commit(Transaction transaction) {
        try {
            transactionCommitter.commit(transaction);
        } catch (Exception e) {
            logger.info("Aborting transaction {} ...", transaction.getKey().getId(), e);
            metrics.abortedTransactionCounter(transaction.getKey().getId()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getKey().getId() + " was aborted", e);
        } finally {
            transactionRepository.delete(transaction);
        }
    }

    private OperationResult getResultForOperation(Transaction t, Operation o) {
        BucketName bucketName = new BucketName(t.getKey().getSpaceName(), o.getBucketName());
        try {
            if (o.getType().equals(Operation.OperationType.READ)) {
                return Optional.ofNullable(t.getReadElements().get(o.getElementId()))
                        .map(version ->
                                OperationResult.of(elementService.getElement(bucketName, o.getElementId(), version), t.getKey().getSpaceName()))
                        .orElseGet(() -> OperationResult.of(elementService.getElement(bucketName, o.getElementId()), t.getKey().getSpaceName()));
            }
            return OperationResult.emptyResult(t.getKey().getSpaceName());
        } catch (ConcurrentTransactionDetectedException e) {
            logger.info("Aborting transaction {} ...", t.getKey(), e);
            metrics.abortedTransactionCounter(t.getKey().getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + t.getKey() + " was aborted", e);
        }
    }

    private void addReadElementIfNeeded(Transaction transaction, OperationResult result) {
        result.getElement().ifPresent(transaction::addReadElement);
    }
}
