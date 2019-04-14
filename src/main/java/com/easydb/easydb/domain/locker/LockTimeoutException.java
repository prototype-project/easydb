package com.easydb.easydb.domain.locker;

import com.easydb.easydb.domain.BucketName;
import java.time.Duration;

public class LockTimeoutException extends RuntimeException {
    public LockTimeoutException(BucketName bucketName, String elementId, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on element - " + bucketName.getSpaceName() + "." + bucketName.getName() + "." + elementId);
    }

    public LockTimeoutException(BucketName bucketName, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on bucket - " + bucketName.getSpaceName() + "." + bucketName.getName());
    }

    public LockTimeoutException(String spaceName, Duration timeout) {
        super("Lock timeout after " + timeout.toMillis() + " millis" +
                " on space - " + spaceName);
    }
}
