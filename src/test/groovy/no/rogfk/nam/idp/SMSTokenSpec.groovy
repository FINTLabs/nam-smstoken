package no.rogfk.nam.idp

import no.rogfk.nam.idp.smsgateway.SMSGateway
import spock.lang.Specification

class SMSTokenSpec extends Specification {
    private SMSToken smsToken
    private SMSGateway smsGateway

    void setup() {

        smsGateway = Mock(SMSGateway)
        smsToken = new SMSToken(smsGateway)
    }

    def "Return NOT_AUTHENTICATED when not valid authentication"() {
        when:
        def authenticate = smsToken.doAuthenticate()

        then:
        authenticate == SMSToken.NOT_AUTHENTICATED
    }

    def "Name"() {
        given:


        when:
        def authenticate = smsToken.doAuthenticate()

        then:
        1 * smsToken.validAuthentication() >> true
        authenticate == SMSToken.SHOW_JSP


    }
}
