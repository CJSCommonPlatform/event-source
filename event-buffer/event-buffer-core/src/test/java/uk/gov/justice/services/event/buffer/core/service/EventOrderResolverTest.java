package uk.gov.justice.services.event.buffer.core.service;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventOrderResolverTest {


    @InjectMocks
    private EventOrderResolver eventOrderResolver;


    @Test
    public void shouldBeObsoleteIfTheIncomingPositionIsLessThanOrEqualToTheCurrentPosition() throws Exception {

        final long currentPosition = 23L;

        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        when(incomingEvent.getPosition()).thenReturn(22L, 23L, 24L, 25L, 26L);

        assertThat(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPosition), is(true));
        assertThat(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPosition), is(true));
        assertThat(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPosition), is(false));
        assertThat(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPosition), is(false));
        assertThat(eventOrderResolver.incomingEventObsolete(incomingEvent, currentPosition), is(false));
    }


    @Test
    public void shouldBeOutOfOrderIfTheIncomingPositionIsGreaterThanTheCurrentPositionByMoreThanOne() throws Exception {

        final long currentPosition = 23L;

        final IncomingEvent incomingEvent = mock(IncomingEvent.class);

        when(incomingEvent.getPosition()).thenReturn(22L, 23L, 24L, 25L, 26L);

        assertThat(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPosition), is(false));
        assertThat(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPosition), is(false));
        assertThat(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPosition), is(false));
        assertThat(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPosition), is(true));
        assertThat(eventOrderResolver.incomingEventNotInOrder(incomingEvent, currentPosition), is(true));
    }
}
