package uk.gov.justice.services.event.buffer.core.service;

import static java.util.UUID.randomUUID;
import static java.util.stream.Stream.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;
import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferJdbcRepository;
import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsecutiveEventsFromBufferFinderTest {

    @Mock
    private EventBufferJdbcRepository eventBufferJdbcRepository;

    @Mock
    private ConsecutiveEventsStreamer consecutiveEventsStreamer;

    @InjectMocks
    private ConsecutiveEventsFromBufferFinder consecutiveEventsFromBufferFinder;

    @Test
    public void shouldGetAllTheEventsForTheStreamFromTheEventBufferAndReturnJustTheConsecutiveOnes() throws Exception {

        final long positionInStream = 23;
        final UUID streamId = randomUUID();
        final String source = "source";
        final String component = "EVENT_LISTENER";

        final JsonEnvelope dontCare = null;

        final IncomingEvent incomingEvent = new IncomingEvent(
                dontCare,
                streamId,
                positionInStream,
                source,
                component
        );

        final Stream<EventBufferEvent> bufferedEvents = of(mock(EventBufferEvent.class));
        final Stream<EventBufferEvent> allEvents = of(mock(EventBufferEvent.class), mock(EventBufferEvent.class));

        when(eventBufferJdbcRepository.findStreamByIdSourceAndComponent(
                streamId,
                source,
                component)).thenReturn(bufferedEvents);
        when(consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                bufferedEvents,
                positionInStream)).thenReturn(allEvents);

        assertThat(consecutiveEventsFromBufferFinder.getEventsConsecutiveTo(incomingEvent), is(allEvents));
    }
}
