package com.easydb.easydb.api.bucket;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.bucket.BucketEventsPublisher;
import com.easydb.easydb.domain.bucket.BucketSubscriptionQuery;
import com.easydb.easydb.domain.bucket.ElementEvent;
import com.easydb.easydb.domain.bucket.factories.BucketServiceFactory;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.space.UUIDProvider;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.util.UriEncoder;

@RestController
@RequestMapping(value = "/api/v1/spaces/{spaceName}/buckets")
class BucketController {

    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;
    private final BucketServiceFactory bucketServiceFactory;
    private final BucketEventsPublisher bucketEventsPublisher;

    BucketController(
            UUIDProvider uuidProvider,
            ApplicationMetrics metrics,
            BucketServiceFactory bucketServiceFactory,
            BucketEventsPublisher bucketEventsPublisher) {
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
        this.bucketServiceFactory = bucketServiceFactory;
        this.bucketEventsPublisher = bucketEventsPublisher;
    }

    @DeleteMapping(path = "/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("spaceName") String spaceName, @PathVariable("bucketName") String bucketName) {
        bucketServiceFactory.buildBucketService(spaceName).removeBucket(bucketName);
        metrics.deleteBucketRequestsCounter(spaceName).increment();
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    void createBucket(@PathVariable("spaceName") String spaceName, @RequestBody @Valid BucketDefinitionDto toCreate) {
        bucketServiceFactory.buildBucketService(spaceName).createBucket(toCreate.getName());
        metrics.createBucketRequestsCounter(spaceName).increment();
    }

    @PostMapping(path = "/{bucketName}/elements")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryDto addElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestBody @Valid ElementCrudDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), bucketName);
        bucketServiceFactory.buildBucketService(spaceName).addElement(element);

        metrics.addElementRequestsCounter(spaceName, bucketName).increment();
        return ElementQueryDto.of(element);
    }

    @DeleteMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        bucketServiceFactory.buildBucketService(spaceName).removeElement(bucketName, elementId);
        metrics.deleteElementRequestsCounter(spaceName, bucketName).increment();
    }

    @PutMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody @Valid ElementCrudDto toUpdate) {
        bucketServiceFactory.buildBucketService(spaceName).updateElement(toUpdate.toDomain(elementId, bucketName));
        metrics.updateElementRequestsCounter(spaceName, bucketName).increment();
    }

    @GetMapping(path = "/{bucketName}/elements/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryDto getElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);

        metrics.getElementRequestsCounter(spaceName, bucketName).increment();
        return ElementQueryDto.of(bucketService.getElement(bucketName, elementId));
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
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);

        Optional<String> uriDecodedQuery = query.map(UriEncoder::decode);
        BucketQuery bucketQuery = BucketQuery.of(bucketName, limit, offset, uriDecodedQuery);

        List<ElementQueryDto> results = bucketService.filterElements(bucketQuery).stream()
                .map(ElementQueryDto::of)
                .collect(Collectors.toList());

        metrics.filterElementsRequestsCounter(spaceName, bucketName).increment();
        return PaginatedElementsDto.of(
                getNextPageLink(bucketService.getNumberOfElements(bucketName), limit, offset, request),
                results);
    }

    @GetMapping(path = "/{bucketName}/elements")
    @ResponseStatus(value = HttpStatus.OK)
    ElementEvent subscribeForChanges(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestParam(value = "query") Optional<String> query,
            HttpServletRequest request) {
        Optional<String> uriDecodedQuery = query.map(UriEncoder::decode);
        BucketSubscriptionQuery subscriptionQuery = BucketSubscriptionQuery.of(spaceName, bucketName, query);
        return bucketEventsPublisher.subscribe(subscriptionQuery).next().block();
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