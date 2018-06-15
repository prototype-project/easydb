package com.easydb.easydb.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;

public class ApplicationMetrics {

	private final MeterRegistry meterRegistry = Metrics.globalRegistry;

	private final Counter totalRequestsCounter;

	public ApplicationMetrics() {
		this.totalRequestsCounter = buildCounter("requests.total");
	}

	Counter getTotalRequestsCounter() {
		return totalRequestsCounter;
	}

	public Counter deleteBucketRequestsCounter(String spaceName) {
		return buildCounter(buildSpaceCounterPath("deleteBucket", spaceName));
	}

	public Counter addElementRequestsCounter(String spaceName, String bucketName) {
		return buildCounter(buildBucketCounterPath("addElement", spaceName, bucketName));
	}

	public Counter deleteElementRequestsCounter(String spaceName, String bucketName) {
		return buildCounter(buildBucketCounterPath("deleteElement", spaceName, bucketName));
	}

	public Counter updateElementRequestsCounter(String spaceName, String bucketName) {
		return buildCounter(buildBucketCounterPath("updateElement", spaceName, bucketName));
	}

	public Counter getElementRequestsCounter(String spaceName, String bucketName) {
		return buildCounter(buildBucketCounterPath("getElement", spaceName, bucketName));
	}

	public Counter getElementsRequestsCounter(String spaceName, String bucketName) {
		return buildCounter(buildBucketCounterPath("updateElements", spaceName, bucketName));
	}

	public Counter createSpaceRequestsCounter() {
		return buildCounter("requests.createSpace");
	}

	public Counter deleteSpaceRequestsCounter() {
		return buildCounter("requests.deleteSpace");
	}

	public Counter getSpaceRequestsCounter() {
		return buildCounter("requests.getSpace");
	}

	private Counter buildCounter(String path, String ...tags) {
		return Counter.builder(path)
				.tags(tags)
				.register(meterRegistry);
	}

	private String buildBucketCounterPath(String prefix, String spaceName, String bucketName) {
		 return buildSpaceCounterPath(prefix, spaceName) + "." + bucketName;
	}

	private String buildSpaceCounterPath(String prefix, String spaceName) {
		return "requests." + prefix + "." + spaceName;
	}
}
