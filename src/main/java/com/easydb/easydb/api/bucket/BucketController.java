package com.easydb.easydb.api.bucket;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.BucketEventsPublisher;
import com.easydb.easydb.domain.bucket.BucketName;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.space.UUIDProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.UriEncoder;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping(value = "/api/v1/spaces/{spaceName}/buckets")
class BucketController {

    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;
    private final BucketService bucketService;
    private final BucketEventsPublisher bucketEventsPublisher;

    BucketController(
            UUIDProvider uuidProvider,
            ApplicationMetrics metrics,
            BucketService bucketService, BucketEventsPublisher bucketEventsPublisher) {
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
        this.bucketService = bucketService;
        this.bucketEventsPublisher = bucketEventsPublisher;
    }

    @DeleteMapping(path = "/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("spaceName") String spaceName, @PathVariable("bucketName") String bucketName) {
        bucketService.removeBucket(new BucketName(spaceName, bucketName));
        metrics.deleteBucketRequestsCounter(spaceName).increment();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    void createBucket(@PathVariable("spaceName") String spaceName, @RequestBody @Valid BucketDefinitionDto toCreate) {
        bucketService.createBucket(new BucketName(spaceName, toCreate.getName()));
        metrics.createBucketRequestsCounter(spaceName).increment();
    }

    @PostMapping(path = "/{bucketName}/elements")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryDto addElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestBody @Valid ElementCrudDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), new BucketName(spaceName, bucketName));
        bucketService.addElement(element);

        metrics.addElementRequestsCounter(new BucketName(spaceName, bucketName)).increment();
        return ElementQueryDto.of(element);
    }

    @DeleteMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        bucketService.removeElement(new BucketName(spaceName, bucketName), elementId);
        metrics.deleteElementRequestsCounter(new BucketName(spaceName, bucketName)).increment();
    }

    @PutMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody @Valid ElementCrudDto toUpdate) {
        bucketService.updateElement(toUpdate.toDomain(elementId, new BucketName(spaceName, bucketName)));
        metrics.updateElementRequestsCounter(new BucketName(spaceName, bucketName)).increment();
    }

    @GetMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryDto getElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        metrics.getElementRequestsCounter(new BucketName(spaceName, bucketName)).increment();
        return ElementQueryDto.of(bucketService.getElement(new BucketName(spaceName, bucketName), elementId));
    }

    @GetMapping(path = "/{bucketName}/elements")
    @ResponseStatus(value = HttpStatus.OK)
    PaginatedElementsDto filterElements(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            @RequestParam(value = "query") Optional<String> query,
            HttpServletRequest request) {
        Optional<String> uriDecodedQuery = query.map(UriEncoder::decode);
        BucketQuery bucketQuery = BucketQuery.of(new BucketName(spaceName, bucketName), limit, offset, uriDecodedQuery);

        List<ElementQueryDto> results = bucketService.filterElements(bucketQuery).stream()
                .map(ElementQueryDto::of)
                .collect(Collectors.toList());

        metrics.filterElementsRequestsCounter(new BucketName(spaceName, bucketName)).increment();
        return PaginatedElementsDto.of(
                getNextPageLink(bucketService.getNumberOfElements(new BucketName(spaceName, bucketName)), limit, offset, request),
                results);
    }

    @GetMapping(path = "/{bucketName}/element-events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    @ResponseStatus(value = HttpStatus.OK)
    Flux<ElementEventDto> subscribeEvents(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestParam(value = "query") Optional<String> query) {
        Optional<String> uriDecodedQuery = query.map(UriEncoder::decode);
        BucketSubscriptionQuery bucketQuery = BucketSubscriptionQuery.of(new BucketName(spaceName, bucketName), uriDecodedQuery);

        return bucketEventsPublisher.subscription(bucketQuery).map(ElementEventDto::of);
        // TODO metrics
    }

    private static String getNextPageLink(long numberOfElements, int limit, int offset,
                                          HttpServletRequest request) {
        return numberOfElements - (offset + limit) > 0 ?
                String.format("%s?limit=%d&offset=%d", getUrlFromRequest(request), limit, offset + limit) : null;
    }

    private static String getUrlFromRequest(HttpServletRequest request) {
        return request.getRequestURL().toString();
    }
}