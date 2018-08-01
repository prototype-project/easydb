package com.easydb.easydb.domain.transactions;

public class TransactionDoesNotExistException extends RuntimeException {

    public TransactionDoesNotExistException(String id) {
        super("Transaction with id " + id + " does not exist");
    }
}
