package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import com.easydb.easydb.infrastructure.space.UUIDProvider;
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
class SpaceDefinitionController {

	private final SpaceDefinitionRepository spaceDefinitionRepository;
	private final UUIDProvider uuidProvider;

	SpaceDefinitionController(SpaceDefinitionRepository spaceDefinitionRepository,
	                          UUIDProvider uuidProvider) {
		this.spaceDefinitionRepository = spaceDefinitionRepository;
		this.uuidProvider = uuidProvider;
	}

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	SpaceDefinitionApiDto createSpace() {
		SpaceDefinition spaceDefinition = SpaceDefinition.of(uuidProvider.generateUUID());
		spaceDefinitionRepository.save(spaceDefinition);
		return new SpaceDefinitionApiDto(spaceDefinition.getSpaceName());
	}

	@DeleteMapping(path = "/{spaceName}")
	@ResponseStatus(value = HttpStatus.OK)
	void deleteSpace(@PathVariable("spaceName") String spaceName) {
		spaceDefinitionRepository.remove(spaceName);
	}

	@GetMapping(path = "/{spaceName}")
	SpaceDefinitionApiDto getSpace(@PathVariable("spaceName") String spaceName) {
		SpaceDefinition fromDb = spaceDefinitionRepository.get(spaceName);
		return new SpaceDefinitionApiDto(fromDb.getSpaceName());
	}
}
