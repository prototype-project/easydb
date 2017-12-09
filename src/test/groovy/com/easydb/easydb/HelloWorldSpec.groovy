package com.easydb.easydb

import spock.lang.Specification

class HelloWorldSpec extends Specification {

    def "hello test"() {
        when:
        def x = 0

        then:
        x == 0
    }

}
