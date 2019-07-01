package uk.gov.justice.services.event.buffer.core.service;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;
import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferJdbcRepository;

import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

public class ConsecutiveEventsFromBufferFinder {

    @Inject
    private EventBufferJdbcRepository eventBufferJdbcRepository;

    @Inject
    private ConsecutiveEventsStreamer consecutiveEventsStreamer;

    /**
     * Returns a Stream of all events that have a position consecutive to the incomingEvent.
     * For example; if the incoming event has a postion of 22, then events with positions 24,
     * 25 and 26 would be returned (assuming of course they exist in the event buffer)
     * Non consecutive events (for example 27) would not be returned.
     *
     * @param incomingEvent The incoming event
     * @return A Stream of any events with consecutive positions found in the event buffer
     */
    public Stream<EventBufferEvent> getEventsConsecutiveTo(final IncomingEvent incomingEvent) {

        final long positionInStream = incomingEvent.getPosition();
        final UUID streamId = incomingEvent.getStreamId();
        final String source = incomingEvent.getSource();
        final String component = incomingEvent.getComponent();

        final Stream<EventBufferEvent> eventStream = eventBufferJdbcRepository.findStreamByIdSourceAndComponent(
                streamId,
                source,
                component);

        return consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                eventStream,
                positionInStream);
    }
}
