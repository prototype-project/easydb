package com.easydb.easydb.domain.transactions;

public class TransactionDoesNotExistException extends RuntimeException {

    public TransactionDoesNotExistException(TransactionKey transactionKey) {
        super("Transaction " + transactionKey + " does not exist");
    }
}
