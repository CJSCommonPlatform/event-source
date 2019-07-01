package uk.gov.justice.services.event.buffer.core.service;

import static java.lang.String.format;

import uk.gov.justice.services.event.buffer.core.repository.subscription.StreamStatusJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.Subscription;

import java.util.UUID;

import javax.inject.Inject;

public class CurrentPositionProvider {

    @Inject
    private StreamStatusJdbcRepository streamStatusJdbcRepository;

    /**
     * Gets the current position of the stream of the incoming event. If no row exists for this
     * stream, then one with a position of 0 is inserted
     *
     * @param incomingEvent the incoming event
     * @return The current position of the stream of the incoming event
     */
    public long getCurrentPositionInStream(final IncomingEvent incomingEvent) {

        final UUID streamId = incomingEvent.getStreamId();
        final String source = incomingEvent.getSource();
        final String component = incomingEvent.getComponent();

        streamStatusJdbcRepository.insertOrDoNothing(new Subscription(streamId, 0L, source, component));

        return streamStatusJdbcRepository.findByStreamIdAndSource(streamId, source, component)
                .orElseThrow(() -> new IllegalStateException(format("No stream status found for streamId: '%s', source: '%s', component: '%s'", streamId, source, component)))
                .getPosition();
    }
}
