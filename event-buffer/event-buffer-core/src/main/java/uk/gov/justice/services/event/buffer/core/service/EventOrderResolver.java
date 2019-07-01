package uk.gov.justice.services.event.buffer.core.service;

/**
 * Calculates if the incoming event is out of order or obsolete
 */
public class EventOrderResolver {

    /**
     * Returns true if the current event is obsolete. That is an event the has already been processed,
     * meaning that the incoming event's position is less than or equal to the current position.
     *
     * @param incomingEvent The incoming event
     * @param currentPosition The current position of the stream
     * @return true if the current position is greater than the incoming event's position
     */
    public boolean incomingEventObsolete(final IncomingEvent incomingEvent, final long currentPosition) {
        return incomingEvent.getPosition() - currentPosition <= 0;
    }

    /**
     * Returns true if the position of the incoming event is greater than the current stored position
     * by more than one (i.e. there is a gap in the ordering of events)
     *
     * @param incomingEvent The incoming event
     * @param currentPosition The current position of the stream that was stored in the stream_status
     *                        table
     *
     * @return true if there is no gap between the incoming event position and the current event
     *      position
     */
    public boolean incomingEventNotInOrder(final IncomingEvent incomingEvent, final long currentPosition) {
        return incomingEvent.getPosition() - currentPosition > 1;
    }
}
