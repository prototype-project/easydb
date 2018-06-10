package com.easydb.easydb.config.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Metrics;

public class ApplicationMetrics {

	private final Counter totalRequestsCounter;

	public ApplicationMetrics() {
		this.totalRequestsCounter = Counter.builder("requests.total")
				.tag("requests", "total")
				.register(Metrics.globalRegistry);
	}

	Counter getTotalRequestsCounter() {
		return totalRequestsCounter;
	}
}
