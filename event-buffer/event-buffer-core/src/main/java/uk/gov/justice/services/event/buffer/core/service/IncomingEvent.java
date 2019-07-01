package uk.gov.justice.services.event.buffer.core.service;

import uk.gov.justice.services.messaging.JsonEnvelope;

import java.util.Objects;
import java.util.UUID;

public class IncomingEvent {

    private final JsonEnvelope incomingEventEnvelope;
    private final UUID streamId;
    private final long position;
    private final String source;
    private final String component;

    public IncomingEvent(
            final JsonEnvelope incomingEventEnvelope,
            final UUID streamId,
            final long position,
            final String source,
            final String component) {
        this.incomingEventEnvelope = incomingEventEnvelope;
        this.streamId = streamId;
        this.position = position;
        this.source = source;
        this.component = component;
    }

    public JsonEnvelope getIncomingEventEnvelope() {
        return incomingEventEnvelope;
    }

    public UUID getStreamId() {
        return streamId;
    }

    public long getPosition() {
        return position;
    }

    public String getSource() {
        return source;
    }

    public String getComponent() {
        return component;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof IncomingEvent)) return false;
        final IncomingEvent that = (IncomingEvent) o;
        return position == that.position &&
                Objects.equals(incomingEventEnvelope, that.incomingEventEnvelope) &&
                Objects.equals(streamId, that.streamId) &&
                Objects.equals(source, that.source) &&
                Objects.equals(component, that.component);
    }

    @Override
    public int hashCode() {
        return Objects.hash(incomingEventEnvelope, streamId, position, source, component);
    }

    @Override
    public String toString() {
        return "IncomingEvent{" +
                "incomingEventEnvelope=" + incomingEventEnvelope +
                ", streamId=" + streamId +
                ", position=" + position +
                ", source='" + source + '\'' +
                ", component='" + component + '\'' +
                '}';
    }
}
