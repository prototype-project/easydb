package com.easydb.easydb.space

import com.easydb.easydb.BaseSpec
import com.easydb.easydb.api.ElementQueryApiDto
import com.easydb.easydb.api.PaginatedElementsApiDto
import com.easydb.easydb.api.SpaceDefinitionApiDto;
import groovy.json.JsonOutput
import org.springframework.http.HttpMethod
import org.springframework.http.ResponseEntity;

class SpaceQuerySpec extends BaseSpec {

	private static String BUCKET_NAME = "people"

	private String spaceName

	def setup() {
		spaceName = addSampleSpace()
		addSampleElement(spaceName, buildElementBody("Daniel"))
		addSampleElement(spaceName, buildElementBody("Bartek"))
		addSampleElement(spaceName, buildElementBody("Zdzisiek"))
	}

	def "should properly paginate results by offset when all elements was fetched"() {
		when:
		PaginatedElementsApiDto paginated = filterElements(spaceName, 1, 2)

		then:
		paginated.getResults().size() == 2
		paginated.getNext() == null
	}

	def "should properly paginate results by offset when there are still more elements to fetch"() {
		when:
		PaginatedElementsApiDto paginated = filterElements(spaceName, 0, 1)

		then:
		paginated.getResults().size() == 1
		paginated.getNext().contains("?limit=1&offset=1")

		when:
		paginated = filterElements(paginated.getNext())

		then:
		paginated.results.size() == 1
		paginated.next.contains("?limit=1&offset=2")

		when:
		paginated = filterElements(paginated.getNext())

		then:
		paginated.results.size() == 1
		paginated.next == null
	}

	def "should properly paginate results by limit when all elements was fetched"() {
		when:
		PaginatedElementsApiDto paginated = filterElements(spaceName, 0, 4)

		then:
		paginated.getResults().size() == 3
		paginated.getNext() == null
	}

	def "should properly paginate results by limit when there are still more elements to fetch"() {
		when:
		PaginatedElementsApiDto paginated = filterElements(spaceName, 0, 2)

		then:
		paginated.getResults().size() == 2
		paginated.getNext().contains("?limit=2&offset=2")

		when:
		paginated = filterElements(paginated.getNext())

		then:
		paginated.getResults().size() == 1
		paginated.getNext() == null
	}

	private PaginatedElementsApiDto filterElements(String fullUrl) {
		return restTemplate.getForEntity(
				fullUrl,
				PaginatedElementsApiDto.class
		).body
	}

	private PaginatedElementsApiDto filterElements(String spaceName, int offset, int limit) {
		return restTemplate.getForEntity(
				localUrl(String.format("/api/v1/%s/%s?limit=%d&offset=%d", spaceName, BUCKET_NAME, limit, offset)),
				PaginatedElementsApiDto.class
		).body
	}

	private String addSampleSpace() {
		return restTemplate.postForEntity(
				localUrl("/api/v1/spaces/"),
				Void,
				SpaceDefinitionApiDto.class).getBody().spaceName
	}

	ResponseEntity<ElementQueryApiDto> addSampleElement(String spaceName, String body) {
		restTemplate.exchange(
				localUrl('/api/v1/' + spaceName + '/'+ BUCKET_NAME),
				HttpMethod.POST,
				httpJsonEntity(body),
				ElementQueryApiDto.class)
	}

	def buildElementBody(String firstName) {
		JsonOutput.toJson([
				fields: [
						[
								name : "firstName",
								value: firstName
						]
				]
		])
	}
}
