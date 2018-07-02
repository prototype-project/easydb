package com.easydb.easydb.domain.transactions;

public interface TransactionRepository {
	void save(Transaction t);
	Transaction get(String uuid);
	void update(Transaction t);
}
