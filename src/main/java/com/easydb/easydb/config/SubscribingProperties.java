package com.easydb.easydb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("bucket.subscribing")
public class SubscribingProperties {
    private int eventsObserverQueueCapacity = 10000;
    private int eventsObserverThreadPoolQueueCapacity = 1000000;
    private int eventsObserversThreadPoolSize = 100;

    public int getEventsObserverQueueCapacity() {
        return eventsObserverQueueCapacity;
    }

    public void setEventsObserverQueueCapacity(int eventsObserverQueueCapacity) {
        this.eventsObserverQueueCapacity = eventsObserverQueueCapacity;
    }

    public int getEventsObserversThreadPoolSize() {
        return eventsObserversThreadPoolSize;
    }

    public void setEventsObserversThreadPoolSize(int eventsObserversThreadPoolSize) {
        this.eventsObserversThreadPoolSize = eventsObserversThreadPoolSize;
    }

    public int getEventsObserverThreadPoolQueueCapacity() {
        return eventsObserverThreadPoolQueueCapacity;
    }

    public void setEventsObserverThreadPoolQueueCapacity(int eventsObserverThreadPoolQueueCapacity) {
        this.eventsObserverThreadPoolQueueCapacity = eventsObserverThreadPoolQueueCapacity;
    }
}
