package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.UUIDProvider;
import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Transaction;
import com.easydb.easydb.domain.transactions.TransactionManager;
import com.easydb.easydb.domain.transactions.TransactionRepository;

public class DefaultTransactionManager implements TransactionManager {
	private final TransactionRepository repository;
	private final UUIDProvider uuidProvider;
	private final SpaceService spaceService;

	public DefaultTransactionManager(TransactionRepository repository,
	                                 UUIDProvider uuidProvider, SpaceService spaceService) {
		this.repository = repository;
		this.uuidProvider = uuidProvider;
		this.spaceService = spaceService;
	}

	@Override
	public String beginTransaction(String spaceName) {
		ensureSpaceExists(spaceName);

		String uuid = uuidProvider.generateUUID();
		Transaction transaction = new Transaction(spaceName, uuid);
		repository.save(transaction);
		return uuid;
	}

	@Override
	public void addOperation(String transactionId, Operation operation) {
		Transaction transaction = repository.get(transactionId);

		ensureElementAndBucketExist(transaction, operation);
		transaction.addOperation(operation);
		repository.update(transaction);
	}

	@Override
	public void commitTransaction(String transactionId) {

	}

	private void ensureSpaceExists(String spaceName) {
		spaceService.get(spaceName);
	}

	private void ensureElementAndBucketExist(Transaction transaction, Operation operation) {
		BucketService bucketService = spaceService.bucketServiceForSpace(transaction.getSpaceName());
		if (!operation.getType().equals(Operation.OperationType.CREATE)) {
			bucketService.getElement(operation.getElement().getBucketName(), operation.getElement().getId());
		}
	}
}
