package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.transactions.Transaction;
import com.easydb.easydb.domain.transactions.TransactionRepository;
import org.springframework.data.mongodb.core.MongoTemplate;

public class MongoTransactionRepository implements TransactionRepository {

	private final MongoTemplate mongoTemplate;

	public MongoTransactionRepository(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public void save(Transaction t) {
		mongoTemplate.insert(fromDomain(t));
	}

	@Override
	public Transaction get(String uuid) {
		PersistentTransaction persistentTransaction = getPersistentTransaction(uuid);
		if (persistentTransaction == null) {
			throw new TransactionDoesNotExistException(uuid);
		}
		return persistentTransaction.toDomain();
	}

	@Override
	public void update(Transaction t) {
		ensureTransactionExists(t.getId());
		mongoTemplate.save(fromDomain(t));
	}

	private PersistentTransaction getPersistentTransaction(String id) {
		return mongoTemplate.findById(id, PersistentTransaction.class);
	}

	private void ensureTransactionExists(String id) {
		if (getPersistentTransaction(id) == null) {
			throw new TransactionDoesNotExistException(id);
		}
	}

	private PersistentTransaction fromDomain(Transaction t) {
		return new PersistentTransaction(t.getSpaceName(), t.getId(), t.getOperations(), t.getState());
	}
}
