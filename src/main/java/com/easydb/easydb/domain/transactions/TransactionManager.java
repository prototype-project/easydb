package com.easydb.easydb.domain.transactions;

public interface TransactionManager {

	String beginTransaction(String spaceName);

	void addOperation(String transactionId, Operation operation);

	void commitTransaction(String transactionId);
}