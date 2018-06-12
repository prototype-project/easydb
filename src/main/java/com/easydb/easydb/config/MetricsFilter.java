package com.easydb.easydb.config;

import com.easydb.easydb.config.metrics.ApplicationMetrics;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;

@WebFilter
public class MetricsFilter implements Filter {

	private final ApplicationMetrics applicationMetrics;

	MetricsFilter(ApplicationMetrics applicationMetrics) {
		this.applicationMetrics = applicationMetrics;
	}

	@Override
	public void init(FilterConfig config) throws ServletException { }

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws java.io.IOException, ServletException {
		chain.doFilter(request, response);

		applicationMetrics.getTotalRequestsCounter().increment();
	}

	@Override
	public void destroy() { }
}
