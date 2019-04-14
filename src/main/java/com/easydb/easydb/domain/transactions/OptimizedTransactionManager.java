package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.config.ApplicationMetrics;
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
    private final TransactionConstraintsValidator transactionConstraintsValidator;
    private final TransactionCommitter transactionCommitter;
    private final ApplicationMetrics metrics;

    public OptimizedTransactionManager(UUIDProvider uuidProvider,
                                       TransactionConstraintsValidator transactionConstraintsValidator,
                                       TransactionCommitter transactionCommitter,
                                       ApplicationMetrics metrics) {
        this.uuidProvider = uuidProvider;
        this.transactionConstraintsValidator = transactionConstraintsValidator;
        this.transactionCommitter = transactionCommitter;
        this.metrics = metrics;
    }

    public Transaction beginTransaction(String spaceName) {
        transactionConstraintsValidator.ensureSpaceExists(spaceName);
        String uuid = uuidProvider.generateUUID();
        return new Transaction(spaceName, uuid);
    }

    public void addOperation(Transaction transaction, Operation operation) {
        transactionConstraintsValidator.ensureOperationConstraints(transaction.getSpaceName(), operation);
        transaction.addOperation(operation);
    }

    public void commitTransaction(Transaction transaction) {
        metrics.singleElementTransactionTimer(transaction.getSpaceName())
                .record(() -> commit(transaction));
    }

    private void commit(Transaction transaction) {
        try {
            transactionCommitter.commit(transaction);
        } catch (Exception e) {
            logger.info("Aborting transaction {} ...", transaction.getId(), e);
            metrics.abortedTransactionCounter(transaction.getSpaceName()).increment();
            throw new TransactionAbortedException(
                    "Transaction " + transaction.getId() + " was aborted", e);
        }
    }
}
