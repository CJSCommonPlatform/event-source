package uk.gov.justice.services.eventsourcing.source.core;

import uk.gov.justice.services.eventsourcing.repository.jdbc.EventRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JdbcEventSourceFactory {

    @Inject
    private EventStreamManager eventStreamManager;

    @Inject
    private EventRepository eventRepository;

    public JdbcBasedEventSource create(final String eventSourceName) {

        return new JdbcBasedEventSource(
                eventStreamManager,
                eventRepository,
                eventSourceName);
    }
}
