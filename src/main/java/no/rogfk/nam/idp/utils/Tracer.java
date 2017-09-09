package no.rogfk.nam.idp.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Tracer {

    private boolean trace;

    public Tracer(boolean trace) {
        this.trace = trace;
    }

    public void trace(String ... messages) {
        if (trace) {
            StringBuilder logMessage = new StringBuilder();
            for(String message : messages) {
                logMessage.append(" ").append(message);
            }
            log.info(logMessage.toString());
        }
    }
}
