package com.easydb.easydb.domain.bucket;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import reactor.core.publisher.Flux;

public class BucketEventsObserver {
    private final BlockingQueue eventsQueue;

    BucketEventsObserver(int capacity) {
        this.eventsQueue = new LinkedBlockingQueue(capacity);
    }

    public Flux<ElementEvent> observe() {
        return null;
    }

    @SuppressWarnings("unchecked")
    public List<ElementEvent> getAvailableEvents() {
        List<ElementEvent> events = new ArrayList<>();
        eventsQueue.drainTo(events);
        return events;
    }
}
