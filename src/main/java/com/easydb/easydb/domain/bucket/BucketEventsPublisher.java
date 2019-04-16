package com.easydb.easydb.domain.bucket;

import reactor.core.publisher.Flux;

public interface BucketEventsPublisher {
    Flux<ElementEvent> subscription(BucketSubscriptionQuery query);
}
