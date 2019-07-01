package uk.gov.justice.services.event.buffer.core.service;

import static java.util.stream.Stream.of;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.slf4j.Logger;

@RunWith(MockitoJUnitRunner.class)
public class ConsecutiveEventBufferServiceTest {

    @Mock
    private EventBufferAccessor eventBufferAccessor;

    @Mock
    private IncomingEventConverter incomingEventConverter;

    @Mock
    private CurrentPositionProvider currentPositionProvider;

    @Mock
    private EventOrderResolver eventOrderResolver;

    @Mock
    private Logger logger;

    @InjectMocks
    private ConsecutiveEventBufferService consecutiveEventBufferService;

    @Test
    public void shouldGetAllCurrentBufferedEventsIfTheIncomingEventIsInOrder() throws Exception {

        final String component = "EVENT_LISTENER";
        final long currentPositionInStream = 23;

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final JsonEnvelope nextEventInBuffer = mock(JsonEnvelope.class);
        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        final Stream<JsonEnvelope> bufferedEvents = of(incomingEventEnvelope, nextEventInBuffer);

        when(incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component)).thenReturn(incomingEvent);
        when(currentPositionProvider.getCurrentPositionInStream(incomingEvent)).thenReturn(currentPositionInStream);

        when(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPositionInStream)).thenReturn(false);
        when(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPositionInStream)).thenReturn(false);

        when(eventBufferAccessor.appendConsecutiveBufferedEventsTo(incomingEvent)).thenReturn(bufferedEvents);

        assertThat(consecutiveEventBufferService.currentOrderedEventsWith(incomingEventEnvelope, component), is(bufferedEvents));
    }

    @Test
    public void shouldReturnEmptyAndBufferTheEventIfTheIncomingEventIsNotInOrder() throws Exception {

        final String component = "EVENT_LISTENER";
        final long currentPositionInStream = 23;

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        when(incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component)).thenReturn(incomingEvent);
        when(currentPositionProvider.getCurrentPositionInStream(incomingEvent)).thenReturn(currentPositionInStream);

        when(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPositionInStream)).thenReturn(false);
        when(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPositionInStream)).thenReturn(true);

        final Stream<JsonEnvelope> bufferedEvents = consecutiveEventBufferService.currentOrderedEventsWith(incomingEventEnvelope, component);
        assertThat(bufferedEvents.count(), is(0L));

        verify(eventBufferAccessor).addToBuffer(incomingEvent);
    }

    @Test
    public void shouldReturnEmptyAndIgnoreTheEventIfTheIncomingEventIsObsolete() throws Exception {

        final String component = "EVENT_LISTENER";
        final long currentPositionInStream = 23;

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        when(incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component)).thenReturn(incomingEvent);
        when(currentPositionProvider.getCurrentPositionInStream(incomingEvent)).thenReturn(currentPositionInStream);

        when(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPositionInStream)).thenReturn(true);

        final Stream<JsonEnvelope> bufferedEvents = consecutiveEventBufferService.currentOrderedEventsWith(incomingEventEnvelope, component);
        assertThat(bufferedEvents.count(), is(0L));

        verify(logger).warn("Message : {} is an obsolete version", incomingEvent);
        verifyZeroInteractions(eventBufferAccessor);
    }
}
