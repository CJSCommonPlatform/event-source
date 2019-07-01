package uk.gov.justice.services.event.buffer.core.service;

import static java.util.stream.Stream.empty;

import uk.gov.justice.services.event.buffer.api.EventBufferService;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.stream.Stream;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.slf4j.Logger;

@ApplicationScoped
@Alternative
@Priority(2)
public class ConsecutiveEventBufferService implements EventBufferService {

    @Inject
    private EventBufferAccessor eventBufferAccessor;

    @Inject
    private IncomingEventConverter incomingEventConverter;

    @Inject
    private CurrentPositionProvider currentPositionProvider;

    @Inject
    private EventOrderResolver eventOrderResolver;

    @Inject
    private Logger logger;

    /**
     * Takes an incoming event and returns a stream of json envelopes. If the event is not
     * consecutive according to the stream_status repository then an empty stream is returned and
     * the incomingEventEnvelope is added to the event-buffer-repository. If the
     * incomingEventEnvelope is consecutive then it is returned as a stream with any consecutive
     * events from the buffer. If an event is the first to be processed for that streamId then the
     * version value must be 1 or the incomingEventEnvelope is added to the buffer and an empty
     * stream is returned.
     *
     * @return stream of consecutive events
     */
    @Override
    public Stream<JsonEnvelope> currentOrderedEventsWith(final JsonEnvelope incomingEventEnvelope, final String component) {

        final IncomingEvent incomingEvent = incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component);
        final long currentPositionInStream = currentPositionProvider.getCurrentPositionInStream(incomingEvent);

        if (eventOrderResolver.incomingEventObsolete(incomingEvent, currentPositionInStream)) {
            logger.warn("Message : {} is an obsolete version", incomingEvent);
            return empty();

        } else if (eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPositionInStream)) {
            eventBufferAccessor.addToBuffer(incomingEvent);
            return empty();
        }

        return eventBufferAccessor.appendConsecutiveBufferedEventsTo(incomingEvent);
    }
}
