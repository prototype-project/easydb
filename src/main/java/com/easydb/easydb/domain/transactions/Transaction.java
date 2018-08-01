package com.easydb.easydb.domain.transactions;

import java.util.ArrayList;
import java.util.List;

public class Transaction {

    public enum State {
        ACTIVE, ABORTED, COMMITED;
    }

    private final String id;
    private String spaceName;
    private final List<Operation> operations;
    private State state;

    public Transaction(String spaceName, String id) {
        this(spaceName, id, new ArrayList<>(), State.ACTIVE);
    }

    public Transaction(String spaceName, String id,
                       List<Operation> operations, State state) {
        this.spaceName = spaceName;
        this.operations = operations;
        this.id = id;
        this.state = state;
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

    public void addOperation(Operation operation) {
        operations.add(operation);
    }
}
