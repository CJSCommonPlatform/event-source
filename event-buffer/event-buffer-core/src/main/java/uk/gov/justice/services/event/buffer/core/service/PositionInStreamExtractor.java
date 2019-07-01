package uk.gov.justice.services.event.buffer.core.service;

import static java.lang.String.format;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

public class PositionInStreamExtractor {

    private static final long FIRST_POSITION = 1L;

    /**
     * Gets the position in stream from the supplied event. Handles missing position and a postion
     * of less that one by throwing an IllegalStateException
     *
     * @param event A JsonEnvelope event from whom to get the position
     * @return The position of this event in the event stream
     */
    public long getPositionFrom(final JsonEnvelope event) {

        final Metadata metadata = event.metadata();

        final long incomingEventPosition = metadata.position()
                .orElseThrow(() -> new IllegalStateException(format("No position in stream found for event with id '%s'", metadata.id())));

        if (incomingEventPosition < FIRST_POSITION) {
            throw new IllegalStateException("Position in stream cannot be less than 1. Was " + incomingEventPosition);
        }

        return incomingEventPosition;
    }
}
