package uk.gov.justice.services.event.buffer.core.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.UUID.fromString;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.event.buffer.core.repository.subscription.StreamStatusJdbcRepository;
import uk.gov.justice.services.event.buffer.core.repository.subscription.Subscription;

import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class CurrentPositionProviderTest {

    @Mock
    private StreamStatusJdbcRepository streamStatusJdbcRepository;

    @InjectMocks
    private CurrentPositionProvider currentPositionProvider;

    @Test
    public void shouldInitialiseTheStreamStatusTableIfNecessaryAndReturnTheCurrentPosition() throws Exception {

        final UUID streamId = fromString("931d14db-6944-40b6-9263-27978a8bc658");
        final String source = "source";
        final String component = "EVENT_LISTENER";
        final long position = 23L;

        final IncomingEvent incomingEvent = mock(IncomingEvent.class);
        final Subscription subscription = mock(Subscription.class);

        when(incomingEvent.getStreamId()).thenReturn(streamId);
        when(incomingEvent.getSource()).thenReturn(source);
        when(incomingEvent.getComponent()).thenReturn(component);
        when(streamStatusJdbcRepository.findByStreamIdAndSource(streamId, source, component)).thenReturn(of(subscription));
        when(subscription.getPosition()).thenReturn(position);

        assertThat(currentPositionProvider.getCurrentPositionInStream(incomingEvent), is(position));
    }

    @Test
    public void shouldFailIfTheEventDoesNotHaveAPosition() throws Exception {

        final UUID streamId = fromString("931d14db-6944-40b6-9263-27978a8bc658");
        final String source = "source";
        final String component = "EVENT_LISTENER";

        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        when(incomingEvent.getStreamId()).thenReturn(streamId);
        when(incomingEvent.getSource()).thenReturn(source);
        when(incomingEvent.getComponent()).thenReturn(component);
        when(streamStatusJdbcRepository.findByStreamIdAndSource(streamId, source, component)).thenReturn(empty());

        try {
            currentPositionProvider.getCurrentPositionInStream(incomingEvent);
            fail();
        } catch (final IllegalStateException expected) {
            assertThat(expected.getMessage(), is("No stream status found for streamId: '931d14db-6944-40b6-9263-27978a8bc658', source: 'source', component: 'EVENT_LISTENER'"));
        }
    }
}
