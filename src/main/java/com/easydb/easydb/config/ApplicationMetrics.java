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

    public Counter getElementsLockerCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockElement", spaceName, bucketName);
    }

    public Counter getElementsLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildBucketCounter("unlockElement", spaceName, bucketName);
    }

    public Counter getBucketLockerCounter(String spaceName, String bucketName) {
        return buildBucketCounter("lockBucket", spaceName, bucketName);
    }

    public Counter getBucketLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildBucketCounter("unlockBucket", spaceName, bucketName);
    }

    public Counter getSpaceLockerCounter(String spaceName) {
        return buildSpaceCounter("lockSpace", spaceName);
    }

    public Counter getSpaceLockerUnlockedCounter(String spaceName) {
        return buildSpaceCounter("unlockSpace", spaceName);
    }

    public Counter getSpaceLockerErrorCounter(String spaceName) {
        return buildSpaceCounter("spaceLockerErrors", spaceName);
    }

    public Counter getBucketLockerErrorCounter(String spaceName, String bucketName) {
        return buildBucketCounter("bucketLockerErrors", spaceName, bucketName);
    }

    public Counter getElementLockerErrorCounter(String spaceName, String bucketName) {
        return buildBucketCounter("elementLockerCounter", spaceName, bucketName);
    }

    public Counter getSpaceLockerTimeoutsCounter(String spaceName) {
        return buildSpaceCounter("spaceLockerTimeouts", spaceName);
    }

    public Counter getBucketLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildBucketCounter("bucketLockerTimeouts", spaceName, bucketName);
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

    public Timer getLockingTimer() {
        return buildTimer("lockingTime");
    }

    public Timer getUnlockingTimer() {
        return buildTimer("unlockingTime");
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
