package com.easydb.easydb.api;

import com.easydb.easydb.domain.space.SpaceDefinition;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/v1/spaces/")
class SpaceDefinitionController {

	private final SpaceDefinitionRepository spaceDefinitionRepository;


	SpaceDefinitionController(SpaceDefinitionRepository spaceDefinitionRepository) {
		this.spaceDefinitionRepository = spaceDefinitionRepository;
	}

	@PostMapping
	@ResponseBody
	@ResponseStatus(value = HttpStatus.CREATED)
	SpaceDefinitionApiDto createSpace(@RequestBody SpaceDefinitionApiDto createDto) {
		spaceDefinitionRepository.save(createDto.toDomain());
		return createDto;
	}

	@DeleteMapping(path = "/{spaceName}")
	@ResponseStatus(value = HttpStatus.OK)
	void deleteSpace(@PathVariable("spaceName") String spaceName) {
		spaceDefinitionRepository.remove(spaceName);
	}

	@GetMapping(path = "/{spaceName}")
	@ResponseBody
	SpaceDefinitionApiDto getSpace(@PathVariable("spaceName") String spaceName) {
		SpaceDefinition fromDb = spaceDefinitionRepository.get(spaceName);
		return new SpaceDefinitionApiDto(fromDb.getSpaceName());
	}
}
