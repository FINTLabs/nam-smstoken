package no.rogfk.nam.idp.utils

import no.rogfk.nam.idp.SMSTokenConstants
import spock.lang.Specification

class MobileSpec extends Specification {
    def "GetMobile"() {
    }

    def "Return default mobile attribute for empty parameter"() {

        when:
        def mobileAttribute = Mobile.validateMobileNumberAttribute("")

        then:
        mobileAttribute[0] == SMSTokenConstants.DEFAULT_MOBILE_ATTRIBUTE
    }

    def "Return configured mobile attribute"() {

        when:
        def mobileAttribute = Mobile.validateMobileNumberAttribute("mobileNumber")

        then:
        mobileAttribute[0] == "mobileNumber"
    }
}
