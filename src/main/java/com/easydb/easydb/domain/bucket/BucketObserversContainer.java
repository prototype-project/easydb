package com.easydb.easydb.domain.bucket;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BucketObserversContainer {
    private final int eventsQueueCapacity;
    private final ExecutorService executorService;
    private final Map<BucketName, BucketEventsObserver> observers = new ConcurrentHashMap<>();

    public BucketObserversContainer(int eventsQueueCapacity, int threadQueueCapacity,
                                    int threadPoolSize) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setNameFormat("BucketObserverThreadPool-%d")
                .build();

        this.eventsQueueCapacity = eventsQueueCapacity;
        this.executorService = new ThreadPoolExecutor(threadPoolSize, threadPoolSize, 0,
                TimeUnit.SECONDS, new LinkedBlockingQueue<>(threadQueueCapacity), threadFactory);
    }

    public BucketEventsObserver provide(BucketName bucketName) {
        return observers.computeIfAbsent(bucketName, name -> new BucketEventsObserver(eventsQueueCapacity, executorService));
    }
}
