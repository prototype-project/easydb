package com.easydb.easydb.domain.transactions;

public class TransactionKey {
    private final String id;
    private final String spaceName;

    private TransactionKey(String spaceName, String id) {
        this.spaceName = spaceName;
        this.id = id;
    }

    public static TransactionKey of(String spaceName, String id) {
        return new TransactionKey(spaceName, id);
    }

    public String getSpaceName() {
        return spaceName;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "TransactionKey{" +
                "id=" + id +
                ",spaceName=" + spaceName +
                "}";
    }
}
