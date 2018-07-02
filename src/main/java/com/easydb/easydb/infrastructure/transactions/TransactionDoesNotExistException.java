package com.easydb.easydb.infrastructure.transactions;

public class TransactionDoesNotExistException extends RuntimeException {

	TransactionDoesNotExistException(String id) {
		super("Transaction with id " + id + " does not exist");
	}
}
