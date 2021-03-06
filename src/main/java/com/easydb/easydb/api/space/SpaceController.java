package com.easydb.easydb.api.space;

import com.easydb.easydb.config.ApplicationMetrics;
import com.easydb.easydb.domain.space.Space;
import com.easydb.easydb.domain.space.SpaceRepository;
import com.easydb.easydb.domain.space.SpaceRemovalService;
import com.easydb.easydb.domain.space.UUIDProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/spaces")
class SpaceController {

    private final SpaceRepository spaceRepository;
    private final SpaceRemovalService spaceRemovalService;
    private final UUIDProvider uuidProvider;
    private final ApplicationMetrics metrics;

    SpaceController(SpaceRepository spaceRepository,
                    SpaceRemovalService spaceRemovalService, UUIDProvider uuidProvider,
                    ApplicationMetrics metrics) {
        this.spaceRepository = spaceRepository;
        this.spaceRemovalService = spaceRemovalService;
        this.uuidProvider = uuidProvider;
        this.metrics = metrics;
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    SpaceDefinitionCreateDto createSpace() {
        Space spaceDefinition = Space.of(uuidProvider.generateUUID());
        spaceRepository.save(spaceDefinition);
        metrics.createSpaceRequestsCounter().increment();
        return new SpaceDefinitionCreateDto(spaceDefinition.getName());
    }

    @DeleteMapping(path = "/{spaceName}")
    @ResponseStatus(value = HttpStatus.OK)
    void deleteSpace(@PathVariable("spaceName") String spaceName) {
        spaceRemovalService.remove(spaceName);
        metrics.deleteSpaceRequestsCounter().increment();
    }

    @GetMapping(path = "/{spaceName}")
    SpaceDetailsDto getSpace(@PathVariable("spaceName") String spaceName) {
        Space fromDb = spaceRepository.get(spaceName);
        metrics.getSpaceRequestsCounter().increment();
        return SpaceDetailsDto.of(fromDb);
    }
}
