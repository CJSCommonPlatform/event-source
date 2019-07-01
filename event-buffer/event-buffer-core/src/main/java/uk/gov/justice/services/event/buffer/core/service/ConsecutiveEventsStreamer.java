package uk.gov.justice.services.event.buffer.core.service;

import static java.util.stream.StreamSupport.stream;

import uk.gov.justice.services.event.buffer.core.repository.streambuffer.EventBufferEvent;

import java.util.stream.Stream;

public class ConsecutiveEventsStreamer {

    /**
     * Converts the supplied event stream to be only a stream of consecutive events. If
     * the stream comes across a gap in the positions then the stream terminates
     *
     * @param eventBufferStream A Stream of all events in the event buffer
     * @param currentPositionInStream the current position of the buffer
     * @return A Stream of only events with a consecutive position
     */
    public Stream<EventBufferEvent> consecutiveEventStreamFromBuffer(
            final Stream<EventBufferEvent> eventBufferStream,
            final long currentPositionInStream) {

        final ConsecutiveEventsSpliterator consecutiveEventsSpliterator = new ConsecutiveEventsSpliterator(
                eventBufferStream,
                currentPositionInStream);

        return stream(consecutiveEventsSpliterator, false).onClose(eventBufferStream::close);
    }
}
