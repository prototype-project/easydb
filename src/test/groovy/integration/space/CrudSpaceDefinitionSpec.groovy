package integration.space;

import integration.BaseSpec
import com.easydb.easydb.api.SpaceDefinitionApiDto;
import com.easydb.easydb.domain.space.SpaceDefinitionRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException


class CrudSpaceDefinitionSpec extends BaseSpec {

	@Autowired
	SpaceDefinitionRepository definitionRepository

	def "should create new space"() {
		when:
		ResponseEntity<SpaceDefinitionApiDto> response = addSampleSpace()

		then:
		response.statusCode == HttpStatus.CREATED
		with (response.body) {
			spaceName != null
		}
	}

	def "should remove space"() {
		given:
		String spaceName = addSampleSpace().getBody().spaceName

		when:
		restTemplate.delete(localUrl("/api/v1/spaces/" + spaceName))

		and:
		restTemplate.getForEntity(localUrl("/api/v1/spaces/" + spaceName), SpaceDefinitionApiDto)

		then:
		def response = thrown(HttpClientErrorException)
		response.statusCode == HttpStatus.NOT_FOUND
	}

	def "should get space"() {
		given:
		String spaceName = addSampleSpace().getBody().spaceName

		when:
		ResponseEntity<SpaceDefinitionApiDto> response = restTemplate.getForEntity(
				localUrl("/api/v1/spaces/" + spaceName), SpaceDefinitionApiDto)

		then:
		response.statusCode == HttpStatus.OK

		response.body.spaceName == spaceName
	}

	def "should return 404 when get not existing space"() {
		when:
		restTemplate.getForEntity(
				localUrl("/api/v1/spaces/notexisting"), SpaceDefinitionApiDto)

		then:
		def response = thrown(HttpClientErrorException)
		response.statusCode == HttpStatus.NOT_FOUND
	}
}
