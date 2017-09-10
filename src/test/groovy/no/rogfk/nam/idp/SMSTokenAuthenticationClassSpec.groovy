package no.rogfk.nam.idp

import com.novell.nidp.NIDPPrincipal
import com.novell.nidp.servlets.NIDPServletContext
import no.rogfk.nam.idp.smsgateway.SMSGateway
import no.rogfk.nam.idp.smsgateway.exceptions.SMSGatewayUnableToSendException
import spock.lang.Specification

import javax.naming.NamingEnumeration
import javax.naming.directory.Attribute
import javax.naming.directory.Attributes
import javax.servlet.http.HttpServletRequest

class SMSTokenAuthenticationClassSpec extends Specification {
    private SMSTokenAuthenticationClass smsTokenAuthenticationClass
    private SMSGateway smsGateway

    void setup() {
        smsGateway = Mock(SMSGateway)
        smsTokenAuthenticationClass = new SMSTokenAuthenticationClass(new Properties(), smsGateway)
    }

    def "Return NOT_AUTHENTICATED when not valid authentication"() {
        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        authenticate == SMSTokenAuthenticationClass.NOT_AUTHENTICATED
    }

    def "Send sms and return SHOW_JSP"() {
        given:
        smsTokenAuthenticationClass = createFirstRunSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        1 * smsGateway.sendSMS(_ as String, _ as String)
        authenticate == SMSTokenAuthenticationClass.SHOW_JSP
    }

    def "Successfully validate token and return AUTHENTICATED"() {
        given:
        smsTokenAuthenticationClass = createValidateSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        0 * smsGateway.sendSMS(_ as String, _ as String)
        authenticate == SMSTokenAuthenticationClass.AUTHENTICATED
    }

    def "Unsuccessfully validate token and return SHOW_JSP"() {
        given:
        smsTokenAuthenticationClass = createInValidateSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        0 * smsGateway.sendSMS(_ as String, _ as String)
        authenticate == SMSTokenAuthenticationClass.SHOW_JSP

    }

    def "Missing mobile number"() {
        given:
        smsTokenAuthenticationClass = createMissingMobileNumberSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        0 * smsGateway.sendSMS(_ as String, _ as String)
        authenticate == SMSTokenAuthenticationClass.SHOW_JSP

    }

    def "Multiple mobile numbers"() {
        given:
        smsTokenAuthenticationClass = createMultipleMobileNumberSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        0 * smsGateway.sendSMS(_ as String, _ as String)
        authenticate == SMSTokenAuthenticationClass.SHOW_JSP

    }

    def "SMS Gateway exception"() {
        given:
        smsTokenAuthenticationClass = createSMSGatewayExceptionSMSToken()

        when:
        def authenticate = smsTokenAuthenticationClass.doAuthenticate()

        then:
        1 * smsGateway.sendSMS(_ as String, _ as String) >> { throw new SMSGatewayUnableToSendException('test exception') }
        authenticate == SMSTokenAuthenticationClass.SHOW_JSP

    }

    private SMSTokenAuthenticationClass createFirstRunSMSToken() {
        return createSMSTokenAuthenticationClass(false, null, 1)
    }

    private SMSTokenAuthenticationClass createValidateSMSToken() {
        return createSMSTokenAuthenticationClass(true, '1234', 1)
    }

    private SMSTokenAuthenticationClass createInValidateSMSToken() {
        return createSMSTokenAuthenticationClass(true, '4321', 1)
    }

    private SMSTokenAuthenticationClass createMissingMobileNumberSMSToken() {
        return createSMSTokenAuthenticationClass(false, '1234', 0)
    }

    private SMSTokenAuthenticationClass createMultipleMobileNumberSMSToken() {
        return createSMSTokenAuthenticationClass(false, '1234', 2)
    }

    private SMSTokenAuthenticationClass createSMSGatewayExceptionSMSToken() {
        return createSMSTokenAuthenticationClass(false, '1234', 1)
    }

    private SMSTokenAuthenticationClass createSMSTokenAuthenticationClass(boolean smsSent, String responseToken, int mobileAttributeSize) {

        Properties properties = new Properties()
        properties.put('Principal', Mock(NIDPPrincipal))

        Attribute mobile = Mock(Attribute) {
            toString() >> 'mobile: 12345678'
        }

        NamingEnumeration<String> ids = Mock(NamingEnumeration) {
            nextElement() >> 'mobile'

        }
        Attributes attributes = Mock(Attributes) {
            size() >> mobileAttributeSize
            getIDs() >> ids
            get('mobile') >> mobile
        }

        HttpServletRequest request = Mock(HttpServletRequest) {
            getRequestURL() >> new StringBuffer('http://localhost')
            getParameter('Response') >> responseToken
        }

        NIDPServletContext context = Mock(NIDPServletContext)

        SMSTokenAuthenticationClass smsTokenClazz = new SMSTokenAuthenticationClass(properties, smsGateway) {
            @Override
            protected void init() {
                m_Request = request
                m_NIDPContext = context
                smsToken = '1234'
                smsTokenSent = smsSent
            }

            @Override
            protected Attributes getPrincipalAttributes(String[] strings) {
                return attributes
            }
        }

        smsTokenClazz.init()
        return smsTokenClazz
    }
}
