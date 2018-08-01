package com.easydb.easydb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfiguration {

    @Bean
    ApplicationMetrics applicationMetrics() {
        return new ApplicationMetrics();
    }

    @Bean
    MetricsFilter metricsFilter() {
        return new MetricsFilter(applicationMetrics());
    }
}
