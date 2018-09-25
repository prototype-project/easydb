package com.easydb.easydb.domain.locker;

import java.time.Duration;

public class LockTimeoutException extends RuntimeException {
    public LockTimeoutException(String spaceName, String bucketName, String elementId, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on element - " + spaceName + "." + bucketName + "." + elementId);
    }

    public LockTimeoutException(String spaceName, String bucketName, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on bucket - " + spaceName + "." + bucketName);
    }

    public LockTimeoutException(String spaceName, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on space - " + spaceName);
    }
}
