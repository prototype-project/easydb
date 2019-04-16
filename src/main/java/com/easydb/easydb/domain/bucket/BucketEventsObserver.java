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
    private final BlockingQueue<ElementEvent> eventsQueue;
    private final ExecutorService executorService;

    BucketEventsObserver(int capacity, ExecutorService executorService) {
        this.eventsQueue = new LinkedBlockingQueue<>(capacity);
        this.executorService = executorService;
    }

    public Flux<ElementEvent> observe() {
        return Flux.interval(Duration.ofMillis(POLL_INTERVAL_MILLIS))
                .publishOn(Schedulers.fromExecutorService(executorService))
                .flatMap(i -> Flux.fromIterable(getAvailableEvents()));
    }

    public void addEvent(ElementEvent event) {
        eventsQueue.offer(event);
    }

    private List<ElementEvent> getAvailableEvents() {
        List<ElementEvent> events = new ArrayList<>();
        eventsQueue.drainTo(events);
        return events;
    }
}
