package com.easydb.easydb.api;

import com.easydb.easydb.domain.bucket.Element;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.domain.space.SpaceFactory;
import com.easydb.easydb.infrastructure.space.UUIDProvider;
import javax.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/v1")
class SpaceController {

    private final SpaceFactory spaceFactory;
    private final SpaceDefinitionRepository spaceDefinitionRepository;
    private final UUIDProvider uuidProvider;

    private SpaceController(
            SpaceFactory spaceFactory,
            UUIDProvider uuidProvider,
            SpaceDefinitionRepository spaceDefinitionRepository) {
        this.spaceFactory = spaceFactory;
        this.uuidProvider = uuidProvider;
        this.spaceDefinitionRepository = spaceDefinitionRepository;
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteBucket(@PathVariable("spaceName") String spaceName, @PathVariable("bucketName") String bucketName) {
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        space.removeBucket(bucketName);
    }

    @PostMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.CREATED)
    ElementQueryApiDto addElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @RequestBody @Valid ElementOperationApiDto toCreate) {
        Element element = toCreate.toDomain(uuidProvider.generateUUID(), bucketName);
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        space.addElement(element);
        return ElementQueryApiDto.from(element);
    }

    @DeleteMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        space.removeElement(bucketName, elementId);
    }

    @PutMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    void updateElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId,
            @RequestBody @Valid ElementOperationApiDto toUpdate) {
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        space.updateElement(toUpdate.toDomain(elementId, bucketName));
    }

    @GetMapping(path = "/{spaceName}/{bucketName}")
    @ResponseStatus(value = HttpStatus.OK)
    List<ElementQueryApiDto> getAllElements(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName) {
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        return space.getAllElements(bucketName).stream()
                .map(ElementQueryApiDto::from)
                .collect(Collectors.toList());
    }

    @GetMapping(path = "/{spaceName}/{bucketName}/{elementId}")
    @ResponseStatus(value = HttpStatus.OK)
    ElementQueryApiDto getElement(
            @PathVariable("spaceName") String spaceName,
            @PathVariable("bucketName") String bucketName,
            @PathVariable("elementId") String elementId) {
        SpaceDefinition spaceDefinition = spaceDefinitionRepository.get(spaceName);
        Space space = spaceFactory.buildSpace(spaceDefinition);
        return ElementQueryApiDto.from(space.getElement(bucketName, elementId));
    }
}