package no.rogfk.nam.idp

import com.novell.nidp.NIDPContext
import com.novell.nidp.servlets.NIDPServletContext
import no.rogfk.nam.idp.smsgateway.SMSGateway
import spock.lang.Specification

class SMSTokenSpec extends Specification {
    private SMSToken smsToken
    private SMSGateway smsGateway

    void setup() {
        /*
        def servletContext = Mock(NIDPServletContext) {
            getLoggableDeviceId() >> '123'
        }
        Mock(NIDPContext) {
            getNIDPContext() >> servletContext
        }
        */
        smsGateway = Mock(SMSGateway)
        smsToken = new SMSToken(smsGateway)
    }

    def "Return NOT_AUTHENTICATED when not valid authentication"() {
        when:
        def authenticate = smsToken.doAuthenticate()

        then:
        authenticate == SMSToken.NOT_AUTHENTICATED
    }
}
