package com.easydb.easydb.api;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.bucket.BucketQuery;
import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.bucket.BucketService;
import com.easydb.easydb.domain.space.BucketServiceFactory;
import com.easydb.easydb.domain.space.UUIDProvider;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
class BucketController {

    private final BucketServiceFactory bucketServiceFactory;
    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;

    private BucketController(
            BucketServiceFactory bucketServiceFactory,
            UUIDProvider uuidProvider,
            ApplicationMetrics metrics) {
        this.bucketServiceFactory = bucketServiceFactory;
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("spaceName") String spaceName, @PathVariable("bucketName") String bucketName) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);
        bucketService.removeBucket(bucketName);
        metrics.deleteBucketRequestsCounter(spaceName).increment();
    }

    @PostMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryApiDto addElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestBody @Valid ElementOperationApiDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), bucketName);
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);
        bucketService.addElement(element);

        metrics.addElementRequestsCounter(spaceName, bucketName).increment();
        return ElementQueryApiDto.from(element);
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);
        bucketService.removeElement(bucketName, elementId);
        metrics.deleteElementRequestsCounter(spaceName, bucketName).increment();
    }

    @PutMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody @Valid ElementOperationApiDto toUpdate) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);
        bucketService.updateElement(toUpdate.toDomain(elementId, bucketName));
        metrics.updateElementRequestsCounter(spaceName, bucketName).increment();
    }

    @GetMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryApiDto getElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);

        metrics.getElementRequestsCounter(spaceName, bucketName).increment();
        return ElementQueryApiDto.from(bucketService.getElement(bucketName, elementId));
    }

    @GetMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    PaginatedElementsApiDto filterElements(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestParam Map<String, String> filters,
            @RequestParam(value = "limit", defaultValue = "20") int limit,
            @RequestParam(value = "offset", defaultValue = "0") int offset,
            HttpServletRequest request) {
        BucketService bucketService = bucketServiceFactory.buildBucketService(spaceName);

        BucketQuery query = BucketQuery.of(bucketName, limit, offset);

        List<ElementQueryApiDto> results = bucketService.filterElements(query).stream()
                .map(ElementQueryApiDto::from)
                .collect(Collectors.toList());

        metrics.getElementsRequestsCounter(spaceName, bucketName).increment();
        return PaginatedElementsApiDto.of(
                getNextPageLink(bucketService.getNumberOfElements(bucketName), limit, offset, request),
                results);
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