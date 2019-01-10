package com.easydb.easydb.api;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TransactionDto {
    private final String transactionId;

    @JsonCreator
    TransactionDto(@JsonProperty("transactionId") String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTransactionId() {
        return transactionId;
    }
}
