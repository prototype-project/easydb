package com.easydb.easydb.domain.transactions;

import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;

public class Retryer {
    private final RetryTemplate retryTemplate;

    public Retryer(RetryTemplate retryTemplate) {
        this.retryTemplate = retryTemplate;
    }

    public void performWithRetries(Runnable transactionOperations) {
        retryTemplate.execute((RetryContext context) -> {
            transactionOperations.run();
            return null;
        });
    }
}