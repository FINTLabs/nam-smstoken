package no.rogfk.nam.idp.utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Tracer {
    private final boolean trace;

    public Tracer(String property) {
        this.trace = Boolean.valueOf(property);
    }

    public void trace(String... messages) {
        if (trace) {
            log.info(createLogMessage(messages));
        }
    }

    String createLogMessage(String... messages) {
        StringBuilder logMessage = new StringBuilder();
        for (String message : messages) {
            logMessage.append(" ").append(message);
        }
        return logMessage.toString();
    }
}
