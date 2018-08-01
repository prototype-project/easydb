package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties
public class TransactionProperties {
    private int numberOfRetries = 10;

    public int getNumberOfRetries() {
        return numberOfRetries;
    }

    public void setNumberOfRetries(int numberOfRetries) {
        this.numberOfRetries = numberOfRetries;
    }
}
