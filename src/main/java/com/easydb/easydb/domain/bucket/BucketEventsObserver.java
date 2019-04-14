package com.easydb.easydb.domain.bucket;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class BucketEventsObserver {
    private static int POLL_INTERVAL_MILLIS = 100;
    private final BlockingQueue eventsQueue;
    private final ExecutorService executorService;

    BucketEventsObserver(int capacity, ExecutorService executorService) {
        this.eventsQueue = new LinkedBlockingQueue(capacity);
        this.executorService = executorService;
    }

    public Flux<ElementEvent> observe() {
        return Flux.interval(Duration.ofMillis(POLL_INTERVAL_MILLIS), Schedulers.fromExecutor(executorService))
                .flatMap(i -> Flux.fromIterable(getAvailableEvents()));
    }

    @SuppressWarnings("unchecked")
    private List<ElementEvent> getAvailableEvents() {
        List<ElementEvent> events = new ArrayList<>();
        eventsQueue.drainTo(events);
        return events;
    }
}
