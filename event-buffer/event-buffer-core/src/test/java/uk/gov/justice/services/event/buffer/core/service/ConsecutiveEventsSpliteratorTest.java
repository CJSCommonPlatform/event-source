package uk.gov.justice.services.event.buffer.core.service;

import static java.lang.Long.MAX_VALUE;
import static java.util.Spliterator.ORDERED;
import static java.util.stream.Stream.empty;
import static java.util.stream.Stream.of;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.test.utils.core.reflection.ReflectionUtil.getValueOfField;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;

import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class ConsecutiveEventsSpliteratorTest {


    @Test
    public void shouldAdvanceIfTheNextPositionIsLessThanTheCurrentPosition() throws Exception {

        final EventBufferEvent eventBufferEvent = mock(EventBufferEvent.class);

        final Consumer<? super EventBufferEvent> consumer = mock(Consumer.class);

        final long currentPosition = 23L;
        final long nextPosition = 22L;

        when(eventBufferEvent.getPosition()).thenReturn(nextPosition);

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                of(eventBufferEvent),
                currentPosition);

        assertThat(consecutiveEventsSpliterator.estimateSize(), is(MAX_VALUE));
        assertThat(consecutiveEventsSpliterator.hasCharacteristics(ORDERED), is(true));
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));

        assertThat(consecutiveEventsSpliterator.tryAdvance(consumer), is(true));

        verify(consumer).accept(eventBufferEvent);
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(nextPosition));
    }

    @Test
    public void shouldAdvanceIfTheNextPositionIsEqualToTheCurrentPosition() throws Exception {

        final EventBufferEvent eventBufferEvent = mock(EventBufferEvent.class);

        final Consumer<? super EventBufferEvent> consumer = mock(Consumer.class);

        final long currentPosition = 23L;
        final long nextPosition = 23L;

        when(eventBufferEvent.getPosition()).thenReturn(nextPosition);

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                of(eventBufferEvent),
                currentPosition);

        assertThat(consecutiveEventsSpliterator.estimateSize(), is(MAX_VALUE));
        assertThat(consecutiveEventsSpliterator.hasCharacteristics(ORDERED), is(true));
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));

        assertThat(consecutiveEventsSpliterator.tryAdvance(consumer), is(true));

        verify(consumer).accept(eventBufferEvent);
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(nextPosition));
    }

    @Test
    public void shouldAdvanceIfTheNextPositionIsGreaterThanTheCurrentPositionByOne() throws Exception {

        final EventBufferEvent eventBufferEvent = mock(EventBufferEvent.class);

        final Consumer<? super EventBufferEvent> consumer = mock(Consumer.class);

        final long currentPosition = 23L;
        final long nextPosition = 24L;

        when(eventBufferEvent.getPosition()).thenReturn(nextPosition);

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                of(eventBufferEvent),
                currentPosition);

        assertThat(consecutiveEventsSpliterator.estimateSize(), is(MAX_VALUE));
        assertThat(consecutiveEventsSpliterator.hasCharacteristics(ORDERED), is(true));
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));

        assertThat(consecutiveEventsSpliterator.tryAdvance(consumer), is(true));

        verify(consumer).accept(eventBufferEvent);
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(nextPosition));
    }

    @Test
    public void shouldNotAdvanceIfTheNextPositionIsGreaterThanTheCurrentPositionByMoreThanOne() throws Exception {

        final EventBufferEvent eventBufferEvent = mock(EventBufferEvent.class);

        final Consumer<? super EventBufferEvent> consumer = mock(Consumer.class);

        final long currentPosition = 23L;
        final long nextPosition = 25L;

        when(eventBufferEvent.getPosition()).thenReturn(nextPosition);

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                of(eventBufferEvent),
                currentPosition);

        assertThat(consecutiveEventsSpliterator.estimateSize(), is(MAX_VALUE));
        assertThat(consecutiveEventsSpliterator.hasCharacteristics(ORDERED), is(true));
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));

        assertThat(consecutiveEventsSpliterator.tryAdvance(consumer), is(false));

        verify(consumer, never()).accept(eventBufferEvent);
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));
    }

    @Test
    public void shouldNotAdvanceIfTheStreamIsComplete() throws Exception {

        final EventBufferEvent eventBufferEvent = mock(EventBufferEvent.class);

        final Consumer<? super EventBufferEvent> consumer = mock(Consumer.class);

        final long currentPosition = 23L;

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                empty(),
                currentPosition);

        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));

        assertThat(consecutiveEventsSpliterator.tryAdvance(consumer), is(false));

        verify(consumer, never()).accept(eventBufferEvent);
        assertThat(getValueOfField(consecutiveEventsSpliterator, "currentPosition", Long.class), is(currentPosition));
    }
}
