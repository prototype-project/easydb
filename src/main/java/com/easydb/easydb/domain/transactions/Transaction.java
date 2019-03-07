package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.transactions.VersionedElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Transaction {

    public enum State {
        ACTIVE, ABORTED, COMMITED;
    }

    private final String id;
    private String spaceName;
    private final List<Operation> operations;
    private final Map<String, Long> readElements;
    private State state;

    public Transaction(String spaceName, String id,
                        List<Operation> operations,
                        Map<String, Long> readElements,
                        State state) {
        this.spaceName = spaceName;
        this.operations = operations;
        this.readElements = readElements;
        this.id = id;
        this.state = state;
    }

    Transaction(String spaceName, String id) {
        this(spaceName, id, new ArrayList<>(), new HashMap<>(), State.ACTIVE);
    }

    public String getId() {
        return id;
    }

    public List<Operation> getOperations() {
        return operations;
    }

    public State getState() {
        return state;
    }

    public String getSpaceName() {
        return spaceName;
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
