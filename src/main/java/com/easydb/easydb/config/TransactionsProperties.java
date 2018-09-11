package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "transactions")
public class TransactionsProperties {
    private int transactionAttempts = 10;
    private int backoffMillis = 100;

    public int getTransactionAttempts() {
        return transactionAttempts;
    }

    public void setTransactionAttempts(int transactionAttempts) {
        this.transactionAttempts = transactionAttempts;
    }

    public int getBackoffMillis() {
        return backoffMillis;
    }

    public void setBackoffMillis(int backoffMillis) {
        this.backoffMillis = backoffMillis;
    }
}
