package uk.gov.justice.services.event.buffer.core.service;

import static java.util.stream.Stream.concat;
import static java.util.stream.Stream.of;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;
import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.StreamStatusJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.Subscription;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;

import java.util.UUID;
import java.util.stream.Stream;

import javax.inject.Inject;

public class EventBufferAccessor {

    @Inject
    private ConsecutiveEventsFromBufferFinder consecutiveEventsFromBufferFinder;

    @Inject
    private EventBufferJdbcRepository eventBufferJdbcRepository;

    @Inject
    private StreamStatusJdbcRepository streamStatusJdbcRepository;

    @Inject
    private JsonObjectEnvelopeConverter jsonObjectEnvelopeConverter;

    /**
     * Gets a Stream of the incoming event plus any buffered events whose positions are consecutive
     * to the incoming event.
     *
     * For example, suppose there are events with positions 23, 34, and 25 in the event buffer. If
     * the incoming event has a position of 22 then all three events from the event buffer would be
     * appended to the returned stream. This would give a returned Stream of 4 events: the
     * incoming event (with position 22) then event with position 23, event with position 24 and
     * event with position 25.
     *
     * Only consecutive events would be appended to the stream. If, for example there was also an
     * event in the event buffer with a position of 27 it would not be appended to the Stream (as
     * there is no event in the buffer with position of 26).
     *
     * If there are no consecutive events in the event buffer, then the returned Stream would only
     * contain the one incomingEvent.
     *
     * The resulting stream adds a closure to each event as it is consumed from the stream that
     * first removes the consumed event from the event buffer and updates the current position
     * in the stream status table
     *
     * @param incomingEvent the incoming event
     * @return The incoming event in a stream with any other consecutive events from the event buffer
     */
    public Stream<JsonEnvelope> appendConsecutiveBufferedEventsTo(final IncomingEvent incomingEvent) {

        final JsonEnvelope incomingEventEnvelope = incomingEvent.getIncomingEventEnvelope();
        final UUID streamId = incomingEvent.getStreamId();
        final long incomingEventPosition = incomingEvent.getPosition();
        final String source = incomingEvent.getSource();
        final String component = incomingEvent.getComponent();

        streamStatusJdbcRepository.update(new Subscription(streamId, incomingEventPosition, source, component));

        final Stream<EventBufferEvent> consecutiveEvents = consecutiveEventsFromBufferFinder.getEventsConsecutiveTo(incomingEvent);

        return concat(of(incomingEventEnvelope), consecutiveEvents
                .peek(streamBufferEvent -> eventBufferJdbcRepository.remove(streamBufferEvent))
                .peek(streamBufferEvent -> streamStatusJdbcRepository.update(new Subscription(
                        streamBufferEvent.getStreamId(),
                        streamBufferEvent.getPosition(),
                        source,
                        component)))
                .map(streamBufferEvent -> jsonObjectEnvelopeConverter.asEnvelope(streamBufferEvent.getEvent())));
    }

    /**
     * Adds the (out of order) incoming event to the event buffer
     * @param incomingEvent The incoming event
     */
    public void addToBuffer(final IncomingEvent incomingEvent) {

        final String eventJson = jsonObjectEnvelopeConverter.asJsonString(incomingEvent.getIncomingEventEnvelope());

        eventBufferJdbcRepository.insert(
                new EventBufferEvent(
                        incomingEvent.getStreamId(),
                        incomingEvent.getPosition(),
                        eventJson,
                        incomingEvent.getSource(),
                        incomingEvent.getComponent()));
    }
}
