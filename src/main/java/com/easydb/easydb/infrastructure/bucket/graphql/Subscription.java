package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.easydb.easydb.domain.bucket.BucketEventsObserver;
import com.easydb.easydb.domain.bucket.ElementEvent;
import java.util.Optional;
import reactor.core.publisher.Flux;

public class Subscription implements GraphQLSubscriptionResolver {
    final static String DEFAULT_GRAPHQL_SUBSCRIPTION =
                    "{                 \n" +
                    "    elements {    \n" +
                    "        id        \n" +
                    "        fields {  \n" +
                    "            name  \n" +
                    "            value \n" +
                    "        }         \n" +
                    "    }             \n" +
                    "}                 \n";

    private final BucketEventsObserver eventsObserver;
    private final ElementEventsFilter eventsFilter;

    public Subscription(BucketEventsObserver eventsObserver, ElementEventsFilter eventsFilter) {
        this.eventsObserver = eventsObserver;
        this.eventsFilter = eventsFilter;
    }

    public Flux<ElementEvent> elementEvents(Optional<ElementFilter> filter) {
        return eventsObserver.observe()
                .filter(elementEvent -> eventsFilter.filter(elementEvent, filter));
    }
}
