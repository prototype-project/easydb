package com.easydb.easydb.domain.locker;

public class LockNotHoldException extends RuntimeException {
    public LockNotHoldException(String spaceName, String bucketName, String elementId) {
        super("Unlocking not held lock on element - " + spaceName + "." + bucketName + "." + elementId);
    }

    public LockNotHoldException(String spaceName, String bucketName) {
        super("Unlocking not held lock on bucket - " + spaceName + "." + bucketName);
    }
}
