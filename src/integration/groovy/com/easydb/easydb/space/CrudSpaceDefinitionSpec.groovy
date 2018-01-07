package com.easydb.easydb.space;


import com.easydb.easydb.BaseSpec
import com.easydb.easydb.api.SpaceDefinitionApiDto;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import groovy.json.JsonOutput;
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException


class CrudSpaceDefinitionSpec extends BaseSpec {
	static String SPACE_NAME = "daniel_proto"

	@Autowired
	SpaceDefinitionRepository definitionRepository

	def cleanup() {
		definitionRepository.remove(SPACE_NAME)
	}

	def "should create new space"() {
		when:
		ResponseEntity<SpaceDefinitionApiDto> response = addSampleSpace()

		then:
		response.statusCode == HttpStatus.CREATED
		with (response.body) {
			spaceName == SPACE_NAME
		}
	}

	def "should return 400 if trying to create already existing space"() {
		given:
		addSampleSpace()

		when:
		addSampleSpace()

		then:
		def response = thrown(HttpClientErrorException)
		response.statusCode == HttpStatus.BAD_REQUEST
	}

	def "should remove space"() {
		given:
		addSampleSpace()

		when:
		restTemplate.delete(localUrl("/api/v1/spaces/" + SPACE_NAME))

		and:
		restTemplate.getForEntity(localUrl("/api/v1/spaces/" + SPACE_NAME), SpaceDefinitionApiDto)

		then:
		def response = thrown(HttpClientErrorException)
		response.statusCode == HttpStatus.NOT_FOUND
	}

	def "should get space"() {
		given:
		addSampleSpace()

		when:
		ResponseEntity<SpaceDefinitionApiDto> response = restTemplate.getForEntity(
				localUrl("/api/v1/spaces/" + SPACE_NAME), SpaceDefinitionApiDto)

		then:
		response.statusCode == HttpStatus.OK

		with(response.body) {
			spaceName == SPACE_NAME
		}
	}

	def "should return 404 when get not existing space"() {
		when:
		restTemplate.getForEntity(
				localUrl("/api/v1/spaces/" + SPACE_NAME), SpaceDefinitionApiDto)

		then:
		def response = thrown(HttpClientErrorException)
		response.statusCode == HttpStatus.NOT_FOUND
	}

	private ResponseEntity<SpaceDefinitionApiDto> addSampleSpace() {
		restTemplate.exchange(
				localUrl("/api/v1/spaces/"),
				HttpMethod.POST,
				httpJsonEntity(sampleSpaceJson()),
				SpaceDefinitionApiDto.class)
	}

	private static def sampleSpaceJson() {
		return JsonOutput.toJson([
		        "spaceName": SPACE_NAME
		])
	}
}
