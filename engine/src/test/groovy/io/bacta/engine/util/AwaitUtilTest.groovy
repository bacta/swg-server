package io.bacta.engine.util

import spock.lang.Specification

class AwaitUtilTest extends Specification {
    def "AwaitTrue"() {
        when:
        def result = AwaitUtil.awaitTrue({true}, 2)
        then:
        noExceptionThrown()
        result
    }

    def "AwaitTrueTimeout"() {
        when:
        def result = AwaitUtil.awaitTrue({false}, 2)
        then:
        noExceptionThrown()
        !result
    }
}
