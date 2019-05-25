package com.easydb.easydb.api.bucket

import com.easydb.easydb.ElementCrudDtoTestBuilder
import com.easydb.easydb.IntegrationDatabaseSpec
import com.easydb.easydb.TestHttpOperations
import org.springframework.http.HttpStatus
import org.springframework.web.client.HttpClientErrorException


class FilterBucketElementsSpec extends IntegrationDatabaseSpec implements TestHttpOperations {

    private String spaceName

    def danielFaderski
    def janBrzechwa
    def jurekOgorek

    def setup() {
        spaceName = addSampleSpace().body.spaceName
        createTestBucket(spaceName)

        danielFaderski = addElementToTestBucket(spaceName, buildElementBody(
                ElementCrudDtoTestBuilder
                        .builder()
                        .clearFields()
                        .addField(new ElementFieldDto("firstName", "Daniel"))
                        .addField(new ElementFieldDto("lastName", "Faderski"))
                        .build())).getBody()

        janBrzechwa = addElementToTestBucket(spaceName, buildElementBody(
                ElementCrudDtoTestBuilder
                        .builder()
                        .clearFields()
                        .addField(new ElementFieldDto("firstName", "Jan"))
                        .addField(new ElementFieldDto("lastName", "Brzechwa"))
                        .build())).getBody()

        jurekOgorek = addElementToTestBucket(spaceName, buildElementBody(
                ElementCrudDtoTestBuilder
                        .builder()
                        .clearFields()
                        .addField(new ElementFieldDto("firstName", "Jurek"))
                        .addField(new ElementFieldDto("lastName", "Ogórek"))
                        .build())).getBody()
    }

    def "should filter elements with `or` operator"() {
        given: "given query to find all people with lastName 'Faderski' or name 'Jan'"
        String query = """
        {
            elements(filter: {
                or: [
                        {
                            fieldsFilters: [
                                {
                                    name: "firstName"
                                    value: "Jan"
                                }
                            ]
                        },
                        {
                            fieldsFilters: [
                                {
                                    name: "lastName"
                                    value: "Faderski"
                                }
                            ]
                        }
                    ]
            }) {
                id
                fields {
                    name 
                    value
                }
            }
        }
        """

        when:
        PaginatedElementsDto elements = getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        elements.results.size() == 2
        elements.results as Set == [janBrzechwa, danielFaderski] as Set
    }

    def "should filter elements with `and` operator"() {
        given: "given query to find all people with lastName 'Faderski' and name 'Daniel'"
        String query = """
        {
            elements(filter: {
                and: [
                        {
                            fieldsFilters: [
                                {
                                    name: "firstName"
                                    value: "Daniel"
                                }
                            ]
                        },
                        {
                            fieldsFilters: [
                                {
                                    name: "lastName"
                                    value: "Faderski"
                                }
                            ]
                        }
                    ]
            }) {
                id
                fields {
                    name 
                    value
                }
            }
        }
        """

        when:
        PaginatedElementsDto elements = getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        elements.results.size() == 1
        elements.results == [danielFaderski]
    }

    def "should filter elements with nested query"() {
        given:
        String query = """
        {
            elements(filter: {
                and: [
                        {
                            or: [
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "firstName"
                                                value: "Jan"
                                            }
                                        ]
                                    },
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "lastName"
                                                value: "Faderski"
                                            }
                                        ]
                                    }
                                ]
                        }
                        {
                            or: [
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "firstName"
                                                value: "Jurek"
                                            }
                                        ]
                                    },
                                    {
                                        fieldsFilters: [
                                            {
                                                name: "lastName"
                                                value: "Brzechwa"
                                            }
                                        ]
                                    }
                                ]
                        }
                    ]
            }) {
                id
                fields {
                    name
                    value
                }
            }
        }
        """

        when:
        PaginatedElementsDto elements = getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        elements.results.size() == 1
        elements.results == [janBrzechwa]
    }

    def "should throw validation error in case of empty filter query"() {
        given:
        String query = """
        {
            elements(unknown: {
            }) {
                id
                fields {
                    name
                    value
                }
            }
        }
        """

        when:
        getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw validation error in case of two operators at the same time"() {
        given:
        String query = """
        {
            elements(filter: {
                               fieldsFilters: [
                                   {
                                       name: "firstName"
                                       value: "Jan"
                                   }
                               ],
                               or: [ 
                                       {
                                           fieldsFilters: [
                                               {
                                                   name: "firstName"
                                                   value: "Jan"
                                               }
                                           ]
                                       }
                               ]
            }) {
                id
                fields {
                    name
                    value
                }
            }
        }
        """

        when:
        getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

    def "should throw validation error in case of syntax error"() {
        given:
        String query = """
        {
            elements(filter: {
                               fieldsFilters: [
                                   {
                                       name: "firstName"
                                       value: "Jan"
                                   
                               ]
            }) {
                id
                fields {
                    name
                    value
                }
            }
        }
        """

        when:
        getElementsFromTestBucket(spaceName, 0, 20, query)

        then:
        def ex = thrown(HttpClientErrorException)
        ex.statusCode == HttpStatus.BAD_REQUEST
    }

}
