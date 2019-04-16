package com.easydb.easydb.infrastructure.bucket.graphql;

import java.util.Optional;

class ElementEventsFilter {

    static boolean filter(GraphQlElementEvent elementEvent, Optional<ElementFilter> eventFilter) {
        return eventFilter.map((filter) -> false).orElse(true);
    }
}
