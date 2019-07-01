package uk.gov.justice.services.event.buffer.core.service;

import static java.util.UUID.randomUUID;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;
import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.StreamStatusJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.Subscription;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.JsonObjectEnvelopeConverter;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EventBufferAccessorTest {

    @Mock
    private ConsecutiveEventsFromBufferFinder consecutiveEventsFromBufferFinder;

    @Mock
    private EventBufferJdbcRepository eventBufferJdbcRepository;

    @Mock
    private StreamStatusJdbcRepository streamStatusJdbcRepository;

    @Mock
    private JsonObjectEnvelopeConverter jsonObjectEnvelopeConverter;

    @InjectMocks
    private EventBufferAccessor eventBufferAccessor;

    @Test
    public void shouldAddAnEventToTheEventBuffer() throws Exception {

        final UUID streamId = randomUUID();
        final long incomingEventPosition = 1;
        final String source = "source";
        final String component = "component";
        final String eventJson = "event json";

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);

        final IncomingEvent incomingEvent = new IncomingEvent(
                incomingEventEnvelope,
                streamId,
                incomingEventPosition,
                source,
                component
        );

        when(jsonObjectEnvelopeConverter.asJsonString(incomingEventEnvelope)).thenReturn(eventJson);

        eventBufferAccessor.addToBuffer(incomingEvent);

        verify(eventBufferJdbcRepository).insert(
                new EventBufferEvent(
                        streamId,
                        incomingEventPosition,
                        eventJson,
                        source,
                        component));
    }

    @Test
    public void shouldGetAStreamOfTheIncomingEventPrependedToAnyContiguousEventsConvertedToEnvelopes() throws Exception {

        final UUID streamId = randomUUID();
        final long incomingEventPosition = 1;
        final String source = "source";
        final String component = "component";
        final String eventJson_2 = "event json 2";
        final String eventJson_3 = "event json 3";

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final JsonEnvelope event_2 = mock(JsonEnvelope.class);
        final JsonEnvelope event_3 = mock(JsonEnvelope.class);

        final EventBufferEvent bufferedEvent_2 = mock(EventBufferEvent.class);
        final EventBufferEvent bufferedEvent_3 = mock(EventBufferEvent.class);

        final IncomingEvent incomingEvent = new IncomingEvent(
                incomingEventEnvelope,
                streamId,
                incomingEventPosition,
                source,
                component
        );

        when(consecutiveEventsFromBufferFinder.getEventsConsecutiveTo(incomingEvent)).thenReturn(Stream.of(bufferedEvent_2, bufferedEvent_3));

        when(bufferedEvent_2.getPosition()).thenReturn(2L);
        when(bufferedEvent_3.getPosition()).thenReturn(3L);

        when(bufferedEvent_2.getStreamId()).thenReturn(streamId);
        when(bufferedEvent_3.getStreamId()).thenReturn(streamId);

        when(bufferedEvent_2.getEvent()).thenReturn(eventJson_2);
        when(bufferedEvent_3.getEvent()).thenReturn(eventJson_3);

        when(jsonObjectEnvelopeConverter.asEnvelope(eventJson_2)).thenReturn(event_2);
        when(jsonObjectEnvelopeConverter.asEnvelope(eventJson_3)).thenReturn(event_3);

        final Stream<JsonEnvelope> jsonEnvelopeStream = eventBufferAccessor.appendConsecutiveBufferedEventsTo(incomingEvent);

        final List<JsonEnvelope> envelopes = jsonEnvelopeStream.collect(toList());

        assertThat(envelopes.size(), is(3));
        assertThat(envelopes.get(0), is(incomingEventEnvelope));
        assertThat(envelopes.get(1), is(event_2));
        assertThat(envelopes.get(2), is(event_3));
    }

    @Test
    public void shouldKeepTheEventBufferTablesCorrectlyUpdatedAsTheStreamIsConsumed() throws Exception {

        final UUID streamId = randomUUID();
        final long incomingEventPosition = 1;
        final String source = "source";
        final String component = "component";
        final String eventJson_2 = "event json 2";
        final String eventJson_3 = "event json 3";

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final JsonEnvelope event_2 = mock(JsonEnvelope.class);
        final JsonEnvelope event_3 = mock(JsonEnvelope.class);

        final EventBufferEvent bufferedEvent_2 = mock(EventBufferEvent.class);
        final EventBufferEvent bufferedEvent_3 = mock(EventBufferEvent.class);

        final IncomingEvent incomingEvent = new IncomingEvent(
                incomingEventEnvelope,
                streamId,
                incomingEventPosition,
                source,
                component
        );

        when(consecutiveEventsFromBufferFinder.getEventsConsecutiveTo(incomingEvent)).thenReturn(Stream.of(bufferedEvent_2, bufferedEvent_3));

        when(bufferedEvent_2.getPosition()).thenReturn(2L);
        when(bufferedEvent_3.getPosition()).thenReturn(3L);

        when(bufferedEvent_2.getStreamId()).thenReturn(streamId);
        when(bufferedEvent_3.getStreamId()).thenReturn(streamId);

        when(bufferedEvent_2.getEvent()).thenReturn(eventJson_2);
        when(bufferedEvent_3.getEvent()).thenReturn(eventJson_3);

        when(jsonObjectEnvelopeConverter.asEnvelope(eventJson_2)).thenReturn(event_2);
        when(jsonObjectEnvelopeConverter.asEnvelope(eventJson_3)).thenReturn(event_3);

        final Stream<JsonEnvelope> jsonEnvelopeStream = eventBufferAccessor.appendConsecutiveBufferedEventsTo(incomingEvent);

        assertThat(jsonEnvelopeStream.collect(toList()), is(notNullValue()));

        final InOrder inOrder = inOrder(streamStatusJdbcRepository, eventBufferJdbcRepository);

        inOrder.verify(streamStatusJdbcRepository).update(new Subscription(streamId, 1L, source, component));
        inOrder.verify(eventBufferJdbcRepository).remove(bufferedEvent_2);
        inOrder.verify(streamStatusJdbcRepository).update(new Subscription(streamId, 2L, source, component));
        inOrder.verify(eventBufferJdbcRepository).remove(bufferedEvent_3);
        inOrder.verify(streamStatusJdbcRepository).update(new Subscription(streamId, 3L, source, component));
    }
}
