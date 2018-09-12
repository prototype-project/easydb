package com.easydb.easydb.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

public class ApplicationMetrics {
    // TODO more metrics
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
        return buildCounter(buildSpaceApiCounterPath("deleteBucket", spaceName));
    }

    public Counter getBeginTransactionRequestsCounter(String spaceName) {
        return buildCounter(buildSpaceApiCounterPath("beginTransaction", spaceName));
    }

    public Counter getAddOperationToTransactionRequestCounter(String spaceName) {
        return buildCounter(buildSpaceApiCounterPath("addTransactionOperation", spaceName));
    }

    public Counter getCommitTransactionRequestCounter(String spaceName) {
        return buildCounter(buildSpaceApiCounterPath("commitTransaction", spaceName));
    }

    public Counter addElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiCounterPath("addElement", spaceName, bucketName));
    }

    public Counter deleteElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiCounterPath("deleteElement", spaceName, bucketName));
    }

    public Counter updateElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiCounterPath("updateElement", spaceName, bucketName));
    }

    public Counter getElementRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiCounterPath("getElement", spaceName, bucketName));
    }

    public Counter getFilterElementsRequestsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketApiCounterPath("filterElements", spaceName, bucketName));
    }

    public Counter getLockerErrorCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketCounterPath("lockerErrors", spaceName, bucketName));
    }

    public Counter getLockerTimeoutsCounter(String spaceName, String bucketName) {
        return buildCounter(buildBucketCounterPath("lockerTimetouts", spaceName, bucketName));
    }

    public Counter getAbortedTransactionCounter(String spaceName) {
        return buildCounter(buildSpaceCounterPath("abortedTransactions", spaceName));
    }

    private Counter buildCounter(String path, String... tags) {
        return Counter.builder(path)
                .tags(tags)
                .register(meterRegistry);
    }

    private String buildBucketApiCounterPath(String prefix, String spaceName, String bucketName) {
        return buildSpaceApiCounterPath(prefix, spaceName) + "." + bucketName;
    }

    private String buildSpaceApiCounterPath(String prefix, String spaceName) {
        return "api." + buildSpaceCounterPath(prefix, spaceName);
    }

    private String buildBucketCounterPath(String prefix, String spaceName, String bucketName) {
        return buildSpaceCounterPath(prefix, spaceName) + "." + bucketName;
    }

    private String buildSpaceCounterPath(String prefix, String spaceName) {
        return prefix + "." + spaceName;
    }
}
