package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.transactions.Transaction;
import com.easydb.easydb.domain.transactions.TransactionDoesNotExistException;
import com.easydb.easydb.domain.transactions.TransactionKey;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoTransactionRepository implements TransactionRepository {

    private final MongoTemplate mongoTemplate;

    public MongoTransactionRepository(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void save(Transaction t) {
        mongoTemplate.insert(fromDomain(t), resolveTransactionCollectionName(t.getKey().getSpaceName()));
    }

    @Override
    public Transaction get(TransactionKey transactionKey) {
        PersistentTransaction persistentTransaction = getPersistentTransaction(transactionKey);
        if (persistentTransaction == null) {
            throw new TransactionDoesNotExistException(transactionKey);
        }
        return persistentTransaction.toDomain(transactionKey.getSpaceName());
    }

    @Override
    public void update(Transaction t) {
        ensureTransactionExists(t.getKey());
        mongoTemplate.save(fromDomain(t), resolveTransactionCollectionName(t.getKey().getSpaceName()));
    }

    @Override
    public void delete(Transaction t) throws TransactionDoesNotExistException {
        ensureTransactionExists(t.getKey());
        mongoTemplate.remove(fromDomain(t), resolveTransactionCollectionName(t.getKey().getSpaceName()));
    }

    private void ensureTransactionExists(TransactionKey transactionKey) {
        if (getPersistentTransaction(transactionKey) == null) {
            throw new TransactionDoesNotExistException(transactionKey);
        }
    }

    private PersistentTransaction getPersistentTransaction(TransactionKey transactionKey) {
        return mongoTemplate.findById(transactionKey.getId(), PersistentTransaction.class, resolveTransactionCollectionName(transactionKey.getSpaceName()));
    }

    private PersistentTransaction fromDomain(Transaction t) {
        return new PersistentTransaction(t.getKey().getId(), t.getOperations(), t.getReadElements());
    }

    private String resolveTransactionCollectionName(String spaceName) {
        return spaceName + ":transactions";
    }
}
