package com.easydb.easydb.domain.transactions;

public interface TransactionRepository {
    void save(Transaction t);
    Transaction get(String uuid) throws TransactionDoesNotExistException;
    void update(Transaction t) throws TransactionDoesNotExistException;
    void delete(Transaction t) throws TransactionDoesNotExistException;
}
