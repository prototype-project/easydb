package com.easydb.easydb.infrastructure.bucket.graphql;

import com.coxautodev.graphql.tools.GraphQLSubscriptionResolver;
import com.easydb.easydb.domain.bucket.BucketEventsObserver;
import java.util.Optional;
import org.reactivestreams.Publisher;

public class Subscription implements GraphQLSubscriptionResolver {
    final static String DEFAULT_GRAPHQL_SUBSCRIPTION =
                    " subscription {                   \n" +
                    "     elementsEvents {             \n" +
                    "         type                     \n" +
                    "         element {                \n" +
                    "             id                   \n" +
                    "             fields {             \n" +
                    "                 name             \n" +
                    "                         value    \n" +
                    "             }                    \n" +
                    "         }                        \n" +
                    "     }                            \n" +
                    " }                                \n";

    private final BucketEventsObserver eventsObserver;

    public Subscription(BucketEventsObserver eventsObserver) {
        this.eventsObserver = eventsObserver;
    }

    public Publisher<GraphQlElementEvent> elementsEvents(Optional<ElementFilter> filter) {
        return eventsObserver.observe()
                .map(GraphQlElementEvent::of)
                .filter(elementEvent -> ElementEventsFilter.filter(elementEvent, filter));
    }
}
