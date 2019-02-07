package com.easydb.easydb.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

public class ApplicationMetrics {
    private final MeterRegistry meterRegistry = Metrics.globalRegistry;

    public Counter createSpaceRequestsCounter() {
        return buildCounter("api.createSpace");
    }

    public Counter deleteSpaceRequestsCounter() {
        return buildCounter("api.deleteSpace");
    }

    public Counter getSpaceRequestsCounter() {
        return buildCounter("api.getSpace");
    }

    public Counter createBucketRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.createBucket", spaceName);
    }

    public Counter deleteBucketRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.deleteBucket", spaceName);
    }

    public Counter addElementRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.addElement", spaceName, bucketName);
    }

    public Counter deleteElementRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.deleteElement", spaceName, bucketName);
    }

    public Counter updateElementRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.updateElement", spaceName, bucketName);
    }

    public Counter getElementRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.getElement", spaceName, bucketName);
    }

    public Counter filterElementsRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.filterElements", spaceName, bucketName);
    }

    public Counter elementsLockerCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockElement", spaceName, bucketName);
    }

    public Counter elementsLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildBucketCounter("unlockElement", spaceName, bucketName);
    }

    public Counter bucketLockerCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockBucket", spaceName, bucketName);
    }

    public Counter bucketLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildBucketCounter("unlockBucket", spaceName, bucketName);
    }

    public Counter spaceLockerCounter(String spaceName) {
        return buildSpaceCounter("lockSpace", spaceName);
    }

    public Counter spaceLockerUnlockedCounter(String spaceName) {
        return buildSpaceCounter("unlockSpace", spaceName);
    }

    public Counter spaceLockerErrorCounter(String spaceName) {
        return buildSpaceCounter("spaceLockerErrors", spaceName);
    }

    public Counter bucketLockerErrorCounter(String spaceName, String bucketName) {
        return buildBucketCounter("bucketLockerErrors", spaceName, bucketName);
    }

    public Counter elementLockerErrorCounter(String spaceName, String bucketName) {
        return buildBucketCounter("elementLockerErrors", spaceName, bucketName);
    }

    public Counter spaceLockerTimeoutsCounter(String spaceName) {
        return buildSpaceCounter("spaceLockerTimeouts", spaceName);
    }

    public Counter bucketLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("bucketLockerTimeouts", spaceName, bucketName);
    }

    public Counter elementLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("elementLockerTimeouts", spaceName, bucketName);
    }

    public Counter beginTransactionRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.beginTransaction", spaceName);
    }

    public Counter addOperationToTransactionRequestCounter(String spaceName, String bucketName,
                                                           String operationType) {
        return buildCounter("api.addTransactionOperation", "space", spaceName, "bucket", bucketName,
                "operation", operationType);
    }

    public Counter commitTransactionRequestCounter(String spaceName) {
        return buildSpaceCounter("api.commitTransaction", spaceName);
    }

    public Counter abortedTransactionCounter(String spaceName) {
        return buildSpaceCounter("abortedTransactions", spaceName);
    }

    public Timer compoundTransactionTimer(String spaceName) {
        return buildSpaceTimer("compoundTransactionTime", spaceName);
    }

    public Timer singleElementTransactionTimer(String spaceName) {
        return buildSpaceTimer("singleElementTransactionTime", spaceName);
    }

    public Timer spaceLockingTimer(String spaceName) {
        return buildSpaceTimer("spaceLockingTime", spaceName);
    }

    public Timer spaceUnlockingTimer(String spaceName) {
        return buildSpaceTimer("spaceUnlockingTime", spaceName);
    }

    public Timer bucketLockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("bucketLockingTime", spaceName, bucketName);
    }

    public Timer bucketUnLockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("bucketUnlockingTime", spaceName, bucketName);
    }

    public Timer elementLockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("elementLockingTime", spaceName, bucketName);
    }

    public Timer elementUnlockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("elementUnlockingTime", spaceName, bucketName);
    }

    private Counter buildBucketCounter(String path, String spaceName, String bucketName) {
        return buildCounter(path, "space", spaceName, "bucket", bucketName);
    }

    private Counter buildSpaceCounter(String path, String spaceName) {
        return buildCounter(path, "space", spaceName);
    }

    private Timer buildSpaceTimer(String path, String spaceName) {
        return buildTimer(path, "space", spaceName);
    }

    private Timer buildBucketTimer(String path, String spaceName, String bucketName) {
        return buildTimer(path, "space", spaceName, "bucket", bucketName);
    }

    private Counter buildCounter(String path, String... tags) {
        return Counter.builder(path)
                .tags(tags)
                .register(meterRegistry);
    }

    private Timer buildTimer(String path, String... tags) {
        return Timer.builder(path)
                .tags(tags)
                .register(meterRegistry);
    }
}
