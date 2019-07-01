package uk.gov.justice.services.event.buffer.core.service;

import static org.apache.commons.lang3.StringUtils.substringBefore;

import uk.gov.justice.services.messaging.JsonEnvelope;

public class EventSourceExtractor {

    /**
     * Calculates the source context name from the name of the event.
     *
     * This assumes that all event names start with the name of the context then a dot '.'.
     *
     * e.g. An event with the name 'people.events.do-stuff' would have a source of 'people'
     *
     * @param incomingEvent The incoming event
     * @return the name of the source context
     */
    public String getSourceFrom(final JsonEnvelope incomingEvent) {
        return substringBefore(incomingEvent.metadata().name(), ".");
    }
}
