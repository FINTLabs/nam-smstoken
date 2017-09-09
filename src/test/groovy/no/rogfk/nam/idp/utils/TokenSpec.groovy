package no.rogfk.nam.idp.utils

import no.rogfk.nam.idp.SMSTokenConstants
import org.apache.commons.lang3.StringUtils
import spock.lang.Specification

class TokenSpec extends Specification {
    def "Get Token"() {
        given:
        def chars = "ABC123"
        def len = "4"
        when:
        def token = Token.getToken(chars, len)

        then:
        token.length() == Integer.valueOf(len)
        StringUtils.containsOnly(token, chars)
    }

    def "Get Token with default values and null parameters"() {
        when:
        def token = Token.getToken(null, null)

        then:
        token.length() == SMSTokenConstants.DEFAULT_TOKEN_LENGTH
        StringUtils.containsOnly(token, SMSTokenConstants.DEFAULT_TOKEN_CHARACTERS)
    }

    def "Get Token with default values and empty parameters"() {
        when:
        def token = Token.getToken("", "")

        then:
        token.length() == SMSTokenConstants.DEFAULT_TOKEN_LENGTH
        StringUtils.containsOnly(token, SMSTokenConstants.DEFAULT_TOKEN_CHARACTERS)
    }

    def "Validate token if reponse token not equal to sent token"() {
        when:
        def valid = Token.validateToken(response, sent)

        then:
        !valid

        where:
        response | sent
        ""       | "AA12"
        "AA12"   | ""
        ""       | ""
        "AA12"   | "ABC"
    }

    def "Validate token when reponse and sent token is equal"() {
        when:
        def valid = Token.validateToken("123", "123")

        then:
        valid
    }


}
