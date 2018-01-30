package com.easydb.easydb.config.metrics;

import com.codahale.metrics.Meter;
import com.codahale.metrics.MetricRegistry;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.springframework.stereotype.Component;

@Component
public class MetricsFilter implements Filter {

	private final Meter requests;

	MetricsFilter(MetricRegistry metricRegistry) {
		this.requests = metricRegistry.meter("requests");
	}


	@Override
	public void init(FilterConfig config) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {
		chain.doFilter(request, response);

		requests.mark();
	}

	@Override
	public void destroy() { }
}
