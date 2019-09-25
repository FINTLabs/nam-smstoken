package no.rogfk.nam.idp.smsgateway

import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayConfigurationException
import spock.lang.Specification

class ConfigSpec extends Specification {
    private Config config

    void setup() {
        config = new Config()
    }

    def "Validate configuration on valid configset"() {
        given:
        def config = new Config()
        config.setGatewayDestName("destName")
        config.setGatewayError("error")
        config.setGatewaySuccess("success")
        config.setGatewayURL("http://localhost")
        config.setGatewayMessageName("messageName")
        config.setGatewayPasswordParameter("password")
        config.setGatewayUserParameter("user")

        when:
        config.validateConfiguration()

        then:
        noExceptionThrown()
    }

    def "Validate configuration on invalid configset"() {
        when:
        def config = new Config()
        config.setGatewayDestName(destName)
        config.setGatewayError(error)
        config.setGatewaySuccess(success)
        config.setGatewayURL(url)
        config.setGatewayMessageName(message)
        config.setGatewayPasswordParameter(password)
        config.setGatewayUserParameter(user)
        config.validateConfiguration()

        then:
        thrown SMSGatewayConfigurationException

        where:
        destName | error | success | url | message | password | user
        ""       | ""    | ""      | ""  | ""      | ""       | ""
        "c"      | ""    | ""      | ""  | ""      | ""       | ""
        "c"      | "c"   | ""      | ""  | ""      | ""       | ""
        "c"      | "c"   | "c"     | ""  | ""      | ""       | ""
        "c"      | "c"   | "c"     | "c" | ""      | ""       | ""
        "c"      | "c"   | "c"     | "c" | "c"     | ""       | ""
        "c"      | "c"   | "c"     | "c" | "c"     | "c"      | ""
        ""       | ""    | ""      | ""  | ""      | ""       | "c"
        ""       | ""    | ""      | ""  | ""      | "c"      | "c"
        ""       | ""    | ""      | ""  | "c"     | "c"      | "c"
        ""       | ""    | ""      | "c" | "c"     | "c"      | "c"
        ""       | ""    | "c"     | "c" | "c"     | "c"      | "c"
        ""       | "c"   | "c"     | "c" | "c"     | "c"      | "c"

    }

    def "Has extra parameter"() {
        given:
        def config = new Config()
        config.setGatewayExtraParameter1("p")
        config.setGatewayExtraParameter2("p")

        when:
        def hasParam1 = config.hasExtraParameter1()
        def hasParam2 = config.hasExtraParameter2()

        then:
        hasParam1
        hasParam2
    }

    def "Has not extra parameter"() {
        when:
        def config = new Config()

        then:
        !config.hasExtraParameter1()
        !config.hasExtraParameter2()
    }

    def "Matches gateway success, string contains"() {
        given:
        def config = new Config()
        config.setGatewaySuccessRegex('false')
        config.setGatewaySuccess('test')

        when:
        def matches = config.matchesGatewaySuccess('test 123')

        then:
        matches
    }

    def "Matches gateway success, regular expression"() {
        given:
        def config = new Config()
        config.setGatewaySuccessRegex('true')
        config.setGatewaySuccess('.+')

        when:
        def matches = config.matchesGatewaySuccess('test')

        then:
        matches
    }

    def "Matches gateway error, string contains"() {
        given:
        def config = new Config()
        config.setGatewayErrorRegex('false')
        config.setGatewayError('test')

        when:
        def matches = config.matchesGatewayError('test 123')

        then:
        matches
    }

    def "Matches gateway error, regular expression"() {
        given:
        def config = new Config()
        config.setGatewayErrorRegex('true')
        config.setGatewayError('.+')

        when:
        def matches = config.matchesGatewayError('test')

        then:
        matches
    }

    def "Null value in regex sets value to false"() {
        given:
        def config = new Config()

        when:
        config.setGatewaySuccessRegex(null)
        config.setGatewayErrorRegex(null)
        def gatewaySuccessRegex = config.isGatewaySuccessRegex()
        def gatewayErrorRegex = config.isGatewayErrorRegex()

        then:
        !gatewaySuccessRegex
        !gatewayErrorRegex
    }
}
