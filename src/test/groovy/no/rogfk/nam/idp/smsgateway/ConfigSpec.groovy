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
}
