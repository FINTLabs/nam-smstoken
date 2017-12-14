package no.rogfk.nam.idp.utils

import spock.lang.Specification

class TracerSpec extends Specification {

    def "Create log message"() {
        when:
        def tracer = new Tracer(true)
        def logMessage = tracer.createLogMessage('message1', 'message2')

        then:
        logMessage == ' message1 message2'
    }
}
