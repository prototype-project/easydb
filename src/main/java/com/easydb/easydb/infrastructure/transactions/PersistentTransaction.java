package com.easydb.easydb.infrastructure.transactions;

import com.easydb.easydb.domain.transactions.Operation;
import com.easydb.easydb.domain.transactions.Transaction;
import java.util.List;
import java.util.Map;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
class PersistentTransaction {

    @Id
    private final String id;

    private final List<Operation> operations;

    private final Map<String, Long> readElements;


    PersistentTransaction(String id,
                          List<Operation> operations,
                          Map<String, Long> readElements) {
        this.id = id;
        this.operations = operations;
        this.readElements = readElements;
    }

    Transaction toDomain(String spaceName) {
        return new Transaction(spaceName, id, operations, readElements);
    }

}
