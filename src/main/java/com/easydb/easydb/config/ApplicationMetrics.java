package com.easydb.easydb.config;

import com.easydb.easydb.domain.BucketName;
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

    public Counter addElementRequestsCounter(BucketName bucketName) {
        return buildBucketCounter("api.addElement", bucketName);
    }

    public Counter deleteElementRequestsCounter(BucketName bucketName) {
        return buildBucketCounter("api.deleteElement", bucketName);
    }

    public Counter updateElementRequestsCounter(BucketName bucketName) {
        return buildBucketCounter("api.updateElement", bucketName);
    }

    public Counter getElementRequestsCounter(BucketName bucketName) {
        return buildBucketCounter("api.getElement", bucketName);
    }

    public Counter filterElementsRequestsCounter(BucketName bucketName) {
        return buildBucketCounter("api.filterElements", bucketName);
    }

    public Counter elementsLockerCounter(BucketName bucketName) {
        return buildBucketCounter("lockElement", bucketName);
    }

    public Counter elementsLockerUnlockedCounter(BucketName bucketName) {
        return buildBucketCounter("unlockElement", bucketName);
    }

    public Counter bucketLockerCounter(BucketName bucketName) {
        return buildBucketCounter("lockBucket", bucketName);
    }

    public Counter bucketLockerUnlockedCounter(BucketName bucketName) {
        return buildBucketCounter("unlockBucket", bucketName);
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

    public Counter bucketLockerErrorCounter(BucketName bucketName) {
        return buildBucketCounter("bucketLockerErrors", bucketName);
    }

    public Counter elementLockerErrorCounter(BucketName bucketName) {
        return buildBucketCounter("elementLockerErrors", bucketName);
    }

    public Counter spaceLockerTimeoutsCounter(String spaceName) {
        return buildSpaceCounter("spaceLockerTimeouts", spaceName);
    }

    public Counter bucketLockerTimeoutsCounter(BucketName bucketName) {
        return buildBucketCounter("bucketLockerTimeouts", bucketName);
    }

    public Counter elementLockerTimeoutsCounter(BucketName bucketName) {
        return buildBucketCounter("elementLockerTimeouts", bucketName);
    }

    public Counter beginTransactionRequestsCounter(String spaceName) {
        return buildSpaceCounter("api.beginTransaction", spaceName);
    }

    public Counter addOperationToTransactionRequestCounter(BucketName bucketName,
                                                           String operationType) {
        return buildCounter("api.addTransactionOperation", "space", bucketName.getSpaceName(),
                "bucket", bucketName.getName(), "operation", operationType);
    }

    public Counter commitTransactionRequestCounter(String spaceName) {
        return buildSpaceCounter("api.commitTransaction", spaceName);
    }

    public Counter abortedTransactionCounter(String spaceName) {
        return buildSpaceCounter("abortedTransactions", spaceName);
    }

    Counter applicationAllRequests() {
        return buildCounter("api.v1.requests.all");
    }

    Counter application2xxRequests() {
        return buildCounter("api.v1.requests.2xx");
    }

    Counter application4xxRequests() {
        return buildCounter("api.v1.requests.4xx");
    }

    Counter application5xxRequests() {
        return buildCounter("api.v1.requests.5xx");
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

    public Timer bucketLockingTimer(BucketName bucketName) {
        return buildBucketTimer("bucketLockingTime", bucketName);
    }

    public Timer bucketUnLockingTimer(BucketName bucketName) {
        return buildBucketTimer("bucketUnlockingTime", bucketName);
    }

    public Timer elementLockingTimer(BucketName bucketName) {
        return buildBucketTimer("elementLockingTime", bucketName);
    }

    public Timer elementUnlockingTimer(BucketName bucketName) {
        return buildBucketTimer("elementUnlockingTime", bucketName);
    }

    private Counter buildBucketCounter(String path, BucketName bucketName) {
        return buildCounter(path, "space", bucketName.getSpaceName(), "bucket", bucketName.getName());
    }

    private Counter buildSpaceCounter(String path, String spaceName) {
        return buildCounter(path, "space", spaceName);
    }

    private Timer buildSpaceTimer(String path, String spaceName) {
        return buildTimer(path, "space", spaceName);
    }

    private Timer buildBucketTimer(String path, BucketName bucketName) {
        return buildTimer(path, "space", bucketName.getSpaceName(), "bucket", bucketName.getName());
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
