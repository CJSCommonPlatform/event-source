package uk.gov.justice.services.eventsourcing.publishedevent;

public class PublishedEventException extends RuntimeException {

    public PublishedEventException(final String message) {
        super(message);
    }

    public PublishedEventException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
