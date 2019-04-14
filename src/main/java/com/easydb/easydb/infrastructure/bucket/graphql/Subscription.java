package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.easydb.easydb.domain.bucket.BucketEventsObserver;
import com.easydb.easydb.domain.bucket.ElementEvent;
import java.util.Optional;
import reactor.core.publisher.Flux;

public class Subscription implements GraphQLSubscriptionResolver {
    final static String DEFAULT_GRAPHQL_SUBSCRIPTION =
                    "{                    \n" +
                    "  elementEvents {    \n" +
                    "          id         \n" +
                    "          fields {   \n" +
                    "              name   \n" +
                    "              value  \n" +
                    "          }          \n" +
                    "      }              \n" +
                    "}                    \n";

    private final BucketEventsObserver eventsObserver;

    public Subscription(BucketEventsObserver eventsObserver) {
        this.eventsObserver = eventsObserver;
    }

    public Flux<ElementEvent> elementEvents(Optional<ElementFilter> filter) {
        return eventsObserver.observe()
                .filter(elementEvent -> ElementEventsFilter.filter(elementEvent, filter));
    }
}
