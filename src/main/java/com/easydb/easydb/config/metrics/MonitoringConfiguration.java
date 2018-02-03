package com.easydb.easydb.config.metrics;

import com.codahale.metrics.jvm.GarbageCollectorMetricSet;
import com.codahale.metrics.jvm.MemoryUsageGaugeSet;
import com.codahale.metrics.jvm.ThreadStatesGaugeSet;
import com.ryantenney.metrics.spring.config.annotation.EnableMetrics;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import com.codahale.metrics.MetricFilter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;
import com.codahale.metrics.graphite.GraphiteReporter.Builder;
import com.ryantenney.metrics.spring.config.annotation.MetricsConfigurerAdapter;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableMetrics
public class MonitoringConfiguration extends MetricsConfigurerAdapter {

	@Value("${monitoring.enabled:false}")
	private boolean enabled;

	@Value("${monitoring.graphite.host:}")
	private String graphiteHost;

	@Value("${monitoring.graphite.port:2003}")
	private int graphitePort;

	@Value("${monitoring.graphite.amountOfTimeBetweenPollsMillis:5000}")
	private long graphiteAmountOfTimeBetweenPollsMillis;

	@Value("${monitoring.graphite.prefix:easydb}")
	private String graphitePrefix;

	@Autowired
	private MetricRegistry registry;

	@Override
	public void configureReporters(MetricRegistry metricRegistry) {
		if (enabled) {
			GraphiteReporter graphiteReporter = getGraphiteReporterBuilder(metricRegistry).build(getGraphite());
			registerReporter(graphiteReporter);
			graphiteReporter.start(graphiteAmountOfTimeBetweenPollsMillis,
					TimeUnit.MILLISECONDS);
		}
	}

	@Override
	public MetricRegistry getMetricRegistry() {
		return registry;
	}

	@PostConstruct
	public void init() {
		configureReporters(registry);
	}

	private Builder getGraphiteReporterBuilder(MetricRegistry metricRegistry) {
		metricRegistry.register("gc", new GarbageCollectorMetricSet());
		metricRegistry.register("memory", new MemoryUsageGaugeSet());
		metricRegistry.register("threads", new ThreadStatesGaugeSet());
		return GraphiteReporter.forRegistry(metricRegistry)
				.convertRatesTo(TimeUnit.SECONDS)
				.convertDurationsTo(TimeUnit.MILLISECONDS)
				.filter(MetricFilter.ALL)
				.prefixedWith(graphitePrefix);
	}

	private Graphite getGraphite() {
		return new Graphite(new InetSocketAddress(graphiteHost, graphitePort));
	}
}