package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "locking")
public class LockingProperties {
    private int lockAttempts = 10;
    private int backoffMillis = 100;

    int getLockAttempts() {
        return lockAttempts;
    }

    void setLockAttempts(int lockAttempts) {
        this.lockAttempts = lockAttempts;
    }

    int getBackoffMillis() {
        return backoffMillis;
    }

    void setBackoffMillis(int backoffMillis) {
        this.backoffMillis = backoffMillis;
    }
}
