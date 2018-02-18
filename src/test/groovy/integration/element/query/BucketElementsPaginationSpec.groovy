package integration.element.query

import integration.BaseSpec
import com.easydb.easydb.api.PaginatedElementsApiDto
import groovy.json.JsonOutput
import org.springframework.web.client.HttpClientErrorException;

class BucketElementsPaginationSpec extends BaseSpec implements QueryUtils {

	private String spaceName

	def setup() {
		spaceName = addSampleSpace().body.spaceName
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

	def "should throw error when trying to paginate by limit <= 0"() {
		when:
		filterElements(spaceName, 0, 0)

		then:
		def response = thrown(HttpClientErrorException)
		response.rawStatusCode == 400
	}

	def "should throw error when trying to paginate by offset < 0"() {
		when:
		filterElements(spaceName, -1, 2)

		then:
		def response = thrown(HttpClientErrorException)
		response.rawStatusCode == 400
	}

	static String buildElementBody(String firstName) {
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
