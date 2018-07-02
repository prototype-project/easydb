package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Transaction;
import java.util.List;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentTransaction {

	private final String spaceName;

	@Id
	private final String id;

	private final List<Operation> operations;

	private final Transaction.State state;

	PersistentTransaction(String spaceName, String id,
	                             List<Operation> operations, Transaction.State state) {
		this.spaceName = spaceName;
		this.id = id;
		this.operations = operations;
		this.state = state;
	}

	Transaction toDomain() {
		return new Transaction(spaceName, id, operations, state);
	}

}
