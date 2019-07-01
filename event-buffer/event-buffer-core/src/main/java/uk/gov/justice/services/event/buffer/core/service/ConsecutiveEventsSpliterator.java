package uk.gov.justice.services.event.buffer.core.service;

import static java.lang.Long.MAX_VALUE;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;

import java.util.Iterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * Spliterator enables to transform stream of events into a consecutive stream of events.
 * If a version gap in the eventStream is spotted then then the processing of the stream terminates.
 */
public class ConsecutiveEventsSpliterator extends AbstractSpliterator<EventBufferEvent> {

    private long currentPosition;
    private final Iterator<EventBufferEvent> eventStreamIterator;

    public ConsecutiveEventsSpliterator(final Stream<EventBufferEvent> eventStream, final long currentPosition) {
        super(MAX_VALUE, ORDERED);
        this.currentPosition = currentPosition;
        this.eventStreamIterator = eventStream.iterator();
    }

    @Override
    public boolean tryAdvance(final Consumer<? super EventBufferEvent> consumer) {
        if (!eventStreamIterator.hasNext()) {
            return false;
        } else {
            final EventBufferEvent next = eventStreamIterator.next();
            final long nextPosition = next.getPosition();
            if (versionGapFound(nextPosition)) {
                return false;
            } else {
                currentPosition = nextPosition;
                consumer.accept(next);
                return true;
            }
        }
    }

    private boolean versionGapFound(final long nextPosition) {
        return nextPosition - currentPosition > 1;
    }
}
