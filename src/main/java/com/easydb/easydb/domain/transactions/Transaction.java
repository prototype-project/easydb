package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {

    private final TransactionKey transactionKey;
    private final List<Operation> operations;
    private final Map<String, Long> readElements;

    public Transaction(String spaceName, String id,
                        List<Operation> operations,
                        Map<String, Long> readElements) {
        this.transactionKey = TransactionKey.of(spaceName,id);
        this.operations = operations;
        this.readElements = readElements;
    }

    Transaction(String spaceName, String id) {
        this(spaceName, id, new ArrayList<>(), new HashMap<>());
    }

    public TransactionKey getKey() {
        return transactionKey;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public Map<String, Long> getReadElements() {
        return Collections.unmodifiableMap(readElements);
    }

    void addOperation(Operation operation) {
        operations.add(operation);
    }

    void addReadElement(VersionedElement element) {
        readElements.put(element.getId(), element.getVersionOrThrowErrorIfEmpty());
    }
}
