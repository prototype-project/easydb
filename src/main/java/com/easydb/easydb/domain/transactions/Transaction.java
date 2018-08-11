package com.easydb.easydb.domain.transactions;

import com.easydb.easydb.domain.bucket.VersionedElement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Transaction {

    public enum State {
        ACTIVE, ABORTED, COMMITED;
    }

    private final String id;
    private String spaceName;
    private final List<Operation> operations;
    private final Set<ElementVersion> readElements;
    private State state;

    Transaction(String spaceName, String id) {
        this(spaceName, id, new ArrayList<>(), new HashSet<>(), State.ACTIVE);
    }

    public Transaction(String spaceName, String id,
                        List<Operation> operations,
                        Set<ElementVersion> readElements,
                        State state) {
        this.spaceName = spaceName;
        this.operations = operations;
        this.readElements = readElements;
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

    public Set<ElementVersion> getReadElements() {
        return readElements;
    }

    void addOperation(Operation operation) {
        operations.add(operation);
    }

    void addReadElement(VersionedElement element) {
        readElements.add(new ElementVersion(element.getId(), element.getVersion()));
    }

    public static class ElementVersion {
        private final String elementId;
        private final long version;

        ElementVersion(String elementId, long version) {
            this.elementId = elementId;
            this.version = version;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ElementVersion that = (ElementVersion) o;

            return elementId.equals(that.elementId) &&
                    version == that.version;
        }

        @Override
        public int hashCode() {
            return Objects.hash(elementId, version);
        }
    }
}
