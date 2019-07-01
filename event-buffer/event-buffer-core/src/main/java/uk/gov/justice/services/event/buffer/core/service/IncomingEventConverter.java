package uk.gov.justice.services.event.buffer.core.service;

import static java.lang.String.format;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import java.util.UUID;

import javax.inject.Inject;

public class IncomingEventConverter {

    @Inject
    private PositionInStreamExtractor positionInStreamExtractor;

    @Inject
    private EventSourceExtractor eventSourceExtractor;

    public IncomingEvent asIncomingEvent(final JsonEnvelope incomingEventEnvelope, final String component) {

        final Metadata metadata = incomingEventEnvelope.metadata();
        final UUID streamId = metadata.streamId().orElseThrow(() -> new IllegalStateException(format("No streamId found for event with id '%s'", metadata.id())));
        final long incomingEventPosition = positionInStreamExtractor.getPositionFrom(incomingEventEnvelope);
        final String source = eventSourceExtractor.getSourceFrom(incomingEventEnvelope);

        return new IncomingEvent(
                incomingEventEnvelope,
                streamId,
                incomingEventPosition,
                source,
                component
        );
    }
}
