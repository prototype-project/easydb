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

    public Counter deleteBucketRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.deleteBucket", spaceName);
    }

    public Counter getBeginTransactionRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.beginTransaction", spaceName);
    }

    public Counter getAddOperationToTransactionRequestCounter(String spaceName, String bucketName,
                                                              String operationType) {
        return buildCounter("api.addTransactionOperation", "space", spaceName, "bucket", bucketName,
                "operation", operationType);
    }

    public Counter getCommitTransactionRequestCounter(String spaceName) {
        return buildSpaceCounter("api.commitTransaction", spaceName);
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

    public Counter getFilterElementsRequestsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("api.filterElements", spaceName, bucketName);
    }

    public Counter getLockerCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockElement", spaceName, bucketName);
    }

    public Counter getLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildBucketCounter("unlockElement", spaceName, bucketName);
    }

    public Counter getLockerErrorCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockerErrors", spaceName, bucketName);
    }

    public Counter getLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockerTimeouts", spaceName, bucketName);
    }

    public Counter getAbortedTransactionCounter(String spaceName) {
        return buildSpaceCounter("abortedTransactions", spaceName);
    }

    public Timer getCompoundTransactionTimer(String spaceName) {
        return buildSpaceTimer("compoundTransactionTime", spaceName);
    }

    public Timer getSingleElementTransactionTimer(String spaceName) {
        return buildSpaceTimer("singleElementTransactionTime", spaceName);
    }

    public Timer getElementLockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("lockingTime", spaceName, bucketName);
    }

    public Timer getElementUnlockingTimer(String spaceName, String bucketName) {
        return buildBucketTimer("unlockingTime", spaceName, bucketName);
    }

    private Counter buildBucketCounter(String path, String spaceName, String bucketName) {
        return buildCounter(path, "space", spaceName, "bucket", bucketName);
    }

    private Counter buildSpaceCounter(String path, String spaceName) {
        return buildCounter(path, "space", spaceName);
    }

    private Timer buildBucketTimer(String path, String spaceName, String bucketName) {
        return buildTimer(path, "space", spaceName, "bucket", bucketName);
    }

    private Timer buildSpaceTimer(String path, String spaceName) {
        return buildTimer(path, "space", spaceName);
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
