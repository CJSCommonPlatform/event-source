package uk.gov.justice.services.event.buffer.core.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.fromString;
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
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PositionInStreamExtractorTest {

    @InjectMocks
    private PositionInStreamExtractor positionInStreamExtractor;

    @Test
    public void shouldGetThePositionInStreamFromTheJsonEnvelope() throws Exception {

        final long positionInStream = 23L;

        final JsonEnvelope jsonEnvelope = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(jsonEnvelope.metadata()).thenReturn(metadata);
        when(metadata.position()).thenReturn(of(positionInStream));

        assertThat(positionInStreamExtractor.getPositionFrom(jsonEnvelope), is(positionInStream));
    }

    @Test
    public void shouldFailIfNoPositionFound() throws Exception {

        final UUID eventId = fromString("98d93acf-95a1-40c0-8a46-82bcf734f5bd");

        final JsonEnvelope jsonEnvelope = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(jsonEnvelope.metadata()).thenReturn(metadata);
        when(metadata.id()).thenReturn(eventId);

        when(metadata.position()).thenReturn(empty());

        try {
            positionInStreamExtractor.getPositionFrom(jsonEnvelope);
            fail();
        } catch (final IllegalStateException expected) {
            assertThat(expected.getMessage(), is("No position in stream found for event with id '98d93acf-95a1-40c0-8a46-82bcf734f5bd'"));
        }
    }

    @Test
    public void shouldFailIfNoPositionLessThanOne() throws Exception {

        final JsonEnvelope jsonEnvelope = mock(JsonEnvelope.class);
        final Metadata metadata = mock(Metadata.class);

        when(jsonEnvelope.metadata()).thenReturn(metadata);
        when(metadata.position()).thenReturn(of(0L));

        try {
            positionInStreamExtractor.getPositionFrom(jsonEnvelope);
            fail();
        } catch (final IllegalStateException expected) {
            assertThat(expected.getMessage(), is("Position in stream cannot be less than 1. Was 0"));
        }
    }
}
