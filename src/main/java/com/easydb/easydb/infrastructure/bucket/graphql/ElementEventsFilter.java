package com.easydb.easydb.infrastructure.bucket.graphql;

import com.easydb.easydb.domain.bucket.ElementEvent;
import java.util.Optional;

class ElementEventsFilter {

    static boolean filter(ElementEvent elementEvent, Optional<ElementFilter> eventFilter) {
        return eventFilter.map((filter) -> false).orElse(true);
    }
}
