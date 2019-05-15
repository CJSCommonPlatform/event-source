package uk.gov.justice.services.eventsourcing.publishedevent;

import static java.util.UUID.fromString;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Answers.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventsourcing.publishedevent.prepublish.PrePublishRepository;
import uk.gov.justice.services.eventsourcing.publishedevent.prepublish.PublishedEventFactory;
import uk.gov.justice.services.eventsourcing.repository.jdbc.event.Event;
import uk.gov.justice.services.eventsourcing.repository.jdbc.event.EventConverter;
import uk.gov.justice.services.eventsourcing.repository.jdbc.event.PublishedEvent;
import uk.gov.justice.services.eventsourcing.source.core.EventStoreDataSourceProvider;
import uk.gov.justice.services.messaging.Metadata;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.json.JsonObject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventPrePublisherTest {

    @Mock(answer = RETURNS_DEEP_STUBS)
    private EventStoreDataSourceProvider eventStoreDataSourceProvider;

    @Mock
    private MetadataEventNumberUpdater metadataEventNumberUpdater;

    @Mock
    private PrePublishRepository prePublishRepository;

    @Mock
    private PublishedEventInserter publishedEventInserter;

    @Mock
    private UtcClock clock;

    @Mock
    private EventConverter eventConverter;

    @Mock
    private PublishedEventFactory publishedEventFactory;

    @InjectMocks
    private EventPrePublisher eventPrePublisher;

    @Test
    public void shouldAddSequenceNumberIntoTheEventMetadataAndSetItForPublishing() throws Exception {

        final UUID eventId = randomUUID();
        final Metadata originalMetadata = mock(Metadata.class);
        final Metadata updatedMetadata = mock(Metadata.class);
        final JsonObject metadataJsonObject = mock(JsonObject.class);
        final String updatedMetadataString = "updated metadata";

        final long eventNumber = 982L;
        final long previousEventNumber = 981L;

        final ZonedDateTime now = new UtcClock().now();

        final Connection connection = mock(Connection.class);
        final Event event = mock(Event.class);
        final PublishedEvent publishedEvent = mock(PublishedEvent.class);

        when(event.getId()).thenReturn(eventId);
        when(eventStoreDataSourceProvider.getDefaultDataSource().getConnection()).thenReturn(connection);
        when(prePublishRepository.getEventNumber(eventId, connection)).thenReturn(eventNumber);
        when(prePublishRepository.getPreviousEventNumber(eventNumber, connection)).thenReturn(previousEventNumber);
        when(clock.now()).thenReturn(now);
        when(eventConverter.metadataOf(event)).thenReturn(originalMetadata);

        when(metadataEventNumberUpdater.updateMetadataJson(
                originalMetadata,
                previousEventNumber,
                eventNumber)).thenReturn(updatedMetadata);
        when(publishedEventFactory.create(event, updatedMetadata, eventNumber, previousEventNumber)).thenReturn(publishedEvent);

        when(updatedMetadata.asJsonObject()).thenReturn(metadataJsonObject);
        when(metadataJsonObject.toString()).thenReturn(updatedMetadataString);

        eventPrePublisher.prePublish(event);

        final InOrder inOrder = inOrder(publishedEventInserter, prePublishRepository);
        inOrder.verify(publishedEventInserter).insertPublishedEvent(publishedEvent, connection);
        inOrder.verify(prePublishRepository).addToPublishQueueTable(eventId, now, connection);
    }

    @Test
    public void shouldThrowAPublishQueueExceptionIfAnSQLExceptionIsThrown() throws Exception {

        final SQLException sqlException = new SQLException("Ooops");

        final UUID eventId = fromString("5dd46779-07a6-4772-b5e8-e9d280708269");

        final Connection connection = mock(Connection.class);
        final Event event = mock(Event.class);

        when(event.getId()).thenReturn(eventId);
        when(eventStoreDataSourceProvider.getDefaultDataSource().getConnection()).thenReturn(connection);
        when(prePublishRepository.getEventNumber(eventId, connection)).thenThrow(sqlException);

        try {
            eventPrePublisher.prePublish(event);
            fail();
        } catch (final PublishQueueException expected) {
            assertThat(expected.getCause(), is(sqlException));
            assertThat(expected.getMessage(), is("Failed to insert event_number into metadata in event_log table for event id 5dd46779-07a6-4772-b5e8-e9d280708269"));
        }
    }
}