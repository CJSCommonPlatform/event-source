package uk.gov.justice.services.eventsourcing.prepublish;

import static com.jayway.jsonassert.JsonAssert.with;
import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.services.common.converter.ZonedDateTimes;
import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventsourcing.publishing.helpers.EventFactory;
import uk.gov.justice.services.eventsourcing.publishing.helpers.EventStoreInitializer;
import uk.gov.justice.services.eventsourcing.publishing.helpers.TestEventInserter;
import uk.gov.justice.services.eventsourcing.repository.jdbc.event.Event;
import uk.gov.justice.services.test.utils.persistence.FrameworkTestDataSourceFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZonedDateTime;
import java.util.UUID;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class PrePublishRepositoryTest {

    private final DataSource eventStoreDataSource = new FrameworkTestDataSourceFactory().createEventStoreDataSource();
    private final TestEventInserter testEventInserter = new TestEventInserter();
    private final EventFactory eventFactory = new EventFactory();
    private final Clock clock = new UtcClock();


    @InjectMocks
    private PrePublishRepository prePublishRepository;

    @Before
    public void initDatabase() throws Exception {
        new EventStoreInitializer().initializeEventStore(eventStoreDataSource);
    }

    @Test
    public void shouldGetTheSequenceNumberOfAnEvent() throws Exception {

        final Event event_1 = eventFactory.createEvent("event-1", 101);
        final Event event_2 = eventFactory.createEvent("event-2", 102);
        final Event event_3 = eventFactory.createEvent("event-3", 103);
        final Event event_4 = eventFactory.createEvent("event-4", 104);

        testEventInserter.insertIntoEventLog(event_1);
        testEventInserter.insertIntoEventLog(event_2);
        testEventInserter.insertIntoEventLog(event_3);
        testEventInserter.insertIntoEventLog(event_4);

        try (final Connection connection = eventStoreDataSource.getConnection()) {
            assertThat(prePublishRepository.getSequenceNumber(event_1.getId(), connection), is(1L));
            assertThat(prePublishRepository.getSequenceNumber(event_2.getId(), connection), is(2L));
            assertThat(prePublishRepository.getSequenceNumber(event_3.getId(), connection), is(3L));
            assertThat(prePublishRepository.getSequenceNumber(event_4.getId(), connection), is(4L));
        }
    }

    @Test
    public void shouldGetThePreviousSequenceNumberOfAnEvent() throws Exception {

        final Event event_1 = eventFactory.createEvent("event-1", 101);
        final Event event_2 = eventFactory.createEvent("event-2", 102);
        final Event event_3 = eventFactory.createEvent("event-3", 103);
        final Event event_4 = eventFactory.createEvent("event-4", 104);

        testEventInserter.insertIntoEventLog(event_1);
        testEventInserter.insertIntoEventLog(event_2);
        testEventInserter.insertIntoEventLog(event_3);
        testEventInserter.insertIntoEventLog(event_4);

        try (final Connection connection = eventStoreDataSource.getConnection()) {

            assertThat(prePublishRepository.getPreviousSequenceNumber(1, connection), is(0L));
            assertThat(prePublishRepository.getPreviousSequenceNumber(2, connection), is(1L));
            assertThat(prePublishRepository.getPreviousSequenceNumber(3, connection), is(2L));
            assertThat(prePublishRepository.getPreviousSequenceNumber(4, connection), is(3L));
        }
    }

    @Test
    public void shouldUpdateTheMetadataOfAnEvent() throws Exception {

        final String eventName = "an-event";
        final Event event = eventFactory.createEvent(eventName, 101);

        testEventInserter.insertIntoEventLog(event);

        final String metadataOfEvent = getMetadataOfEvent(event.getId());

        with(metadataOfEvent)
                .assertThat("$.name", is(eventName))
        ;

        try (final Connection connection = eventStoreDataSource.getConnection()) {

            prePublishRepository.updateMetadata(event.getId(), "new metadata", connection);
        }

        assertThat(getMetadataOfEvent(event.getId()), is("new metadata"));
    }

    @Test
    public void shouldInsertEventIdIntoThePublishTable() throws Exception {

        final UUID eventId = randomUUID();
        final ZonedDateTime now = clock.now();

        try (final Connection connection = eventStoreDataSource.getConnection()) {

            prePublishRepository.addToPublishQueueTable(eventId, now, connection);


            try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT id, event_log_id, date_queued FROM publish_queue")) {

                final ResultSet resultSet = preparedStatement.executeQuery();

                assertThat(resultSet.next(), is(true));
                final long id = resultSet.getLong("id");
                final UUID eventLogId = (UUID) resultSet.getObject("event_log_id");
                final ZonedDateTime dateQueued = ZonedDateTimes.fromSqlTimestamp(resultSet.getTimestamp("date_queued"));

                assertThat(id, is(1L));
                assertThat(eventLogId, is(eventId));
                assertThat(dateQueued, is(now));

                assertThat(resultSet.next(), is(false));
            }
        }
    }

    private String getMetadataOfEvent(final UUID eventId) throws SQLException {

        try (final Connection connection = eventStoreDataSource.getConnection()) {
            try (final PreparedStatement preparedStatement = connection.prepareStatement("SELECT metadata FROM event_log WHERE id = ?")) {

                preparedStatement.setObject(1, eventId);

                try (final ResultSet resultSet = preparedStatement.executeQuery()) {

                    resultSet.next();
                    return resultSet.getString(1);
                }
            }
        }
    }
}