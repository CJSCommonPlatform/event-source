package uk.gov.justice.services.event.buffer.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.services.messaging.Metadata;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class EventSourceExtractorTest {

    @InjectMocks
    private EventSourceExtractor eventSourceExtractor;

    @Test
    public void shouldExtractTheSourceContextNameFromTheEventsName() throws Exception {

        final String eventName = "people.events.person-date-of-birth-updated";

        final JsonEnvelope incomingEvent = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(incomingEvent.metadata()).thenReturn(metadata);
        when(metadata.name()).thenReturn(eventName);

        assertThat(eventSourceExtractor.getSourceFrom(incomingEvent), is("people"));
    }
}
