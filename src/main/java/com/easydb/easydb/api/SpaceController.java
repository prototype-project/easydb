package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.space.SpaceService;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceServiceFactory;
import com.easydb.easydb.domain.space.UUIDProvider;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/v1")
class SpaceController {

    private final SpaceServiceFactory spaceServiceFactory;
    private final SpaceRepository spaceRepository;
    private final UUIDProvider uuidProvider;

    private SpaceController(
            SpaceServiceFactory spaceServiceFactory,
            UUIDProvider uuidProvider,
            SpaceRepository spaceRepository) {
        this.spaceServiceFactory = spaceServiceFactory;
        this.uuidProvider = uuidProvider;
        this.spaceRepository = spaceRepository;
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("spaceName") String spaceName, @PathVariable("bucketName") String bucketName) {
        Space spaceDefinition = spaceRepository.get(spaceName);
        SpaceService space = spaceServiceFactory.buildSpaceService(spaceDefinition);
        space.removeBucket(bucketName);
    }

    @PostMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryApiDto addElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestBody @Valid ElementOperationApiDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), bucketName);
        Space spaceDefinition = spaceRepository.get(spaceName);
        SpaceService space = spaceServiceFactory.buildSpaceService(spaceDefinition);
        space.addElement(element);
        return ElementQueryApiDto.from(element);
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        Space spaceDefinition = spaceRepository.get(spaceName);
        SpaceService space = spaceServiceFactory.buildSpaceService(spaceDefinition);
        space.removeElement(bucketName, elementId);
    }

    @PutMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody @Valid ElementOperationApiDto toUpdate) {
        Space spaceDefinition = spaceRepository.get(spaceName);
        SpaceService space = spaceServiceFactory.buildSpaceService(spaceDefinition);
        space.updateElement(toUpdate.toDomain(elementId, bucketName));
    }

    @GetMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryApiDto getElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        Space spaceDefinition = spaceRepository.get(spaceName);
        SpaceService space = spaceServiceFactory.buildSpaceService(spaceDefinition);
        return ElementQueryApiDto.from(space.getElement(bucketName, elementId));
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