package com.easydb.easydb.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Timer;

public class ApplicationMetrics {
    private final MeterRegistry meterRegistry = Metrics.globalRegistry;

    private final Counter totalRequestsCounter;

    public ApplicationMetrics() {
        this.totalRequestsCounter = buildCounter("requests.total");
    }

    Counter getTotalRequestsCounter() {
        return totalRequestsCounter;
    }

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
        return buildCounter(buildSpaceApiPath("deleteBucket", spaceName));
    }

    public Counter getBeginTransactionRequestsCounter(String spaceName) {
        return buildCounter(buildSpaceApiPath("beginTransaction", spaceName));
    }

    public Counter getAddOperationToTransactionRequestCounter(String spaceName) {
        return buildCounter(buildSpaceApiPath("addTransactionOperation", spaceName));
    }

    public Counter getCommitTransactionRequestCounter(String spaceName) {
        return buildCounter(buildSpaceApiPath("commitTransaction", spaceName));
    }

    public Counter addElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiPath("addElement", spaceName, bucketName));
    }

    public Counter deleteElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiPath("deleteElement", spaceName, bucketName));
    }

    public Counter updateElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiPath("updateElement", spaceName, bucketName));
    }

    public Counter getElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiPath("getElement", spaceName, bucketName));
    }

    public Counter getFilterElementsRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiPath("filterElements", spaceName, bucketName));
    }

    public Counter getLockerCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketPath("lockElement", spaceName, bucketName));
    }

    public Counter getLockerUnlockedCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketPath("unlockElement", spaceName, bucketName));
    }

    public Counter getLockerErrorCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketPath("lockerErrors", spaceName, bucketName));
    }

    public Counter getLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketPath("lockerTimetouts", spaceName, bucketName));
    }

    public Counter getAbortedTransactionCounter(String spaceName) {
        return buildCounter(buildSpacePath("abortedTransactions", spaceName));
    }

    public Timer getCompoundTransactionTimer(String spaceName) {
        return buildTimer(buildSpacePath("compoundTransactionTime", spaceName));
    }

    public Timer getSingleElementTransactionTimer(String spaceName) {
        return  buildTimer(buildSpacePath("singleElementTransactionTime", spaceName));
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

    private String buildBucketApiPath(String prefix, String spaceName, String bucketName) {
        return buildSpaceApiPath(prefix, spaceName) + "." + bucketName;
    }

    private String buildSpaceApiPath(String prefix, String spaceName) {
        return "api." + buildSpacePath(prefix, spaceName);
    }

    private String buildBucketPath(String prefix, String spaceName, String bucketName) {
        return buildSpacePath(prefix, spaceName) + "." + bucketName;
    }

    private String buildSpacePath(String prefix, String spaceName) {
        return prefix + "." + spaceName;
    }
}
