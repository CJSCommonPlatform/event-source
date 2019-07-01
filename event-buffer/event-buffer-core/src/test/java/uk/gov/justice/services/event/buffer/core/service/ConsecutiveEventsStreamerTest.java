package uk.gov.justice.services.event.buffer.core.service;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Stream.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class ConsecutiveEventsStreamerTest {

    @InjectMocks
    private ConsecutiveEventsStreamer consecutiveEventsStreamer;

    @Test
    public void shouldReturnTheEntireStreamIfContiguous() throws Exception {

        final EventBufferEvent event_1 = mock(EventBufferEvent.class);
        final EventBufferEvent event_2 = mock(EventBufferEvent.class);
        final EventBufferEvent event_3 = mock(EventBufferEvent.class);

        when(event_1.getPosition()).thenReturn(1L);
        when(event_2.getPosition()).thenReturn(2L);
        when(event_3.getPosition()).thenReturn(3L);

        final Stream<EventBufferEvent> events = of(
                event_1,
                event_2,
                event_3
        );

        final int currentPositionInStream = 1;
        final Stream<EventBufferEvent> consecutiveEventStream = consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                events,
                currentPositionInStream);

        final List<EventBufferEvent> consecutiveEventList = consecutiveEventStream.collect(toList());

        assertThat(consecutiveEventList.size(), is(3));

        assertThat(consecutiveEventList.get(0), is(event_1));
        assertThat(consecutiveEventList.get(1), is(event_2));
        assertThat(consecutiveEventList.get(2), is(event_3));
    }

    @Test
    public void shouldReturnAStreamOfOnlyContiguousEvents() throws Exception {

        final EventBufferEvent event_1 = mock(EventBufferEvent.class);
        final EventBufferEvent event_2 = mock(EventBufferEvent.class);
        final EventBufferEvent event_3 = mock(EventBufferEvent.class);
        final EventBufferEvent event_5 = mock(EventBufferEvent.class);
        final EventBufferEvent event_6 = mock(EventBufferEvent.class);

        when(event_1.getPosition()).thenReturn(1L);
        when(event_2.getPosition()).thenReturn(2L);
        when(event_3.getPosition()).thenReturn(3L);
        when(event_5.getPosition()).thenReturn(5L);
        when(event_6.getPosition()).thenReturn(6L);

        final Stream<EventBufferEvent> events = of(
                event_1,
                event_2,
                event_3,
                event_5,
                event_6
        );


        final int currentPositionInStream = 1;
        final Stream<EventBufferEvent> consecutiveEventStream = consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                events,
                currentPositionInStream);

        final List<EventBufferEvent> consecutiveEventList = consecutiveEventStream.collect(toList());

        assertThat(consecutiveEventList.size(), is(3));

        assertThat(consecutiveEventList.get(0), is(event_1));
        assertThat(consecutiveEventList.get(1), is(event_2));
        assertThat(consecutiveEventList.get(2), is(event_3));
    }

    @Test
    public void shouldReturnAnEmptyStreamIfTheCurrentPositionIsBeforeTheFirstEventsPosition() throws Exception {


        final EventBufferEvent event_11 = mock(EventBufferEvent.class);
        final EventBufferEvent event_12 = mock(EventBufferEvent.class);
        final EventBufferEvent event_13 = mock(EventBufferEvent.class);

        when(event_11.getPosition()).thenReturn(11L);
        when(event_12.getPosition()).thenReturn(12L);
        when(event_13.getPosition()).thenReturn(13L);

        final Stream<EventBufferEvent> events = of(
                event_11,
                event_12,
                event_13
        );


        final int currentPositionInStream = 5;
        final Stream<EventBufferEvent> consecutiveEventStream = consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                events,
                currentPositionInStream);

        assertThat(consecutiveEventStream.count(), is(0L));
    }

    @Test
    public void shouldAlwaysCloseTheOriginalEventStream() throws Exception {

        final EventBufferEvent event_1 = mock(EventBufferEvent.class);
        final EventBufferEvent event_2 = mock(EventBufferEvent.class);
        final EventBufferEvent event_3 = mock(EventBufferEvent.class);

        final AtomicBoolean closed = new AtomicBoolean(false);

        when(event_1.getPosition()).thenReturn(1L);
        when(event_2.getPosition()).thenReturn(2L);
        when(event_3.getPosition()).thenReturn(3L);

        final Stream<EventBufferEvent> events = of(
                event_1,
                event_2,
                event_3
        ).onClose(() -> closed.set(true));

        final int currentPositionInStream = 1;
        final Stream<EventBufferEvent> consecutiveEventStream = consecutiveEventsStreamer.consecutiveEventStreamFromBuffer(
                events,
                currentPositionInStream);

        consecutiveEventStream.close();

        assertThat(closed.get(), is(true));
    }
}
