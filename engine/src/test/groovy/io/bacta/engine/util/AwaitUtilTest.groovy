package io.bacta.engine.util

import spock.lang.Specification

class AwaitUtilTest extends Specification {
    def "AwaitTrue"() {
        when:
        def result = AwaitUtil.awaitTrue({true}, 1)
        then:
        noExceptionThrown()
        result
    }

    def "AwaitTrueTimeout"() {
        when:
        def result = AwaitUtil.awaitTrue({false}, 1)
        then:
        noExceptionThrown()
        !result
    }

    def "AwaitFalse"() {
        when:
        def result = AwaitUtil.awaitFalse({false}, 1)
        then:
        noExceptionThrown()
        result
    }

    def "AwaitFalseTimeout"() {
        when:
        def result = AwaitUtil.awaitFalse({true}, 1)
        then:
        noExceptionThrown()
        !result
    }
}
