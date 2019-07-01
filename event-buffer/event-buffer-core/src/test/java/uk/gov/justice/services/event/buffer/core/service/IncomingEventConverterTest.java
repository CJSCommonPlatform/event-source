package uk.gov.justice.services.event.buffer.core.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class IncomingEventConverterTest {

    @Mock
    private PositionInStreamExtractor positionInStreamExtractor;

    @Mock
    private EventSourceExtractor eventSourceExtractor;

    @InjectMocks
    private IncomingEventConverter incomingEventConverter;

    @Test
    public void shouldExtractTheRelevantValuesFromTheJsonEnvelopeAndReturnAsAnIncomintEvent() throws Exception {

        final String component = "EVENT_LISTENER";
        final UUID streamId = randomUUID();
        final long incomingEventPosition = 234L;
        final String source = "source";

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(positionInStreamExtractor.getPositionFrom(incomingEventEnvelope)).thenReturn(incomingEventPosition);
        when(eventSourceExtractor.getSourceFrom(incomingEventEnvelope)).thenReturn(source);

        when(incomingEventEnvelope.metadata()).thenReturn(metadata);
        when(metadata.streamId()).thenReturn(of(streamId));

        final IncomingEvent incomingEvent = incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component);

        assertThat(incomingEvent.getPosition(), is(incomingEventPosition));
        assertThat(incomingEvent.getComponent(), is(component));
        assertThat(incomingEvent.getSource(), is(source));
        assertThat(incomingEvent.getStreamId(), is(streamId));
        assertThat(incomingEvent.getIncomingEventEnvelope(), is(incomingEventEnvelope));
    }

    @Test
    public void shouldFailIfTheJsonEnvelopeDoesNotHaveAStreamId() throws Exception {

        final String component = "EVENT_LISTENER";
        final UUID eventId = fromString("132a0987-4778-46f8-a8e4-c2b2adac1d10");

        final JsonEnvelope incomingEventEnvelope = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(incomingEventEnvelope.metadata()).thenReturn(metadata);
        when(metadata.streamId()).thenReturn(empty());
        when(metadata.id()).thenReturn(eventId);

        try {
            incomingEventConverter.asIncomingEvent(incomingEventEnvelope, component);
            fail();
        } catch (final IllegalStateException expected) {
            assertThat(expected.getMessage(), is("No streamId found for event with id '132a0987-4778-46f8-a8e4-c2b2adac1d10'"));
        }


    }
}
