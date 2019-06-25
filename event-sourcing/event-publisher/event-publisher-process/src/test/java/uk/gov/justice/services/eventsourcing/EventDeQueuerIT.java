package uk.gov.justice.services.eventsourcing;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.common.converter.ZonedDateTimes.toSqlTimestamp;

import uk.gov.justice.services.common.util.Clock;
import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventsourcing.publishing.helpers.EventFactory;
import uk.gov.justice.services.eventsourcing.publishing.helpers.EventStoreInitializer;
import uk.gov.justice.services.eventsourcing.publishing.helpers.TestEventInserter;
import uk.gov.justice.services.eventsourcing.repository.jdbc.event.Event;
import uk.gov.justice.services.test.utils.persistence.FrameworkTestDataSourceFactory;
import uk.gov.justice.subscription.registry.SubscriptionDataSourceProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class EventDeQueuerIT {

    private final DataSource dataSource = new FrameworkTestDataSourceFactory().createEventStoreDataSource();
    private final TestEventInserter testEventInserter = new TestEventInserter();
    private final EventFactory eventFactory = new EventFactory();
    private final Clock clock = new UtcClock();

    @Mock
    private SubscriptionDataSourceProvider subscriptionDataSourceProvider;

    @InjectMocks
    private EventDeQueuer eventDeQueuer;

    @Before
    public void initDatabase() throws Exception {
        new EventStoreInitializer().initializeEventStore(dataSource);
    }

    @Test
    public void shouldPopEventsFromThePrePublishQueue() throws Exception {

        final String tableName = "pre_publish_queue";

        when(subscriptionDataSourceProvider.getEventStoreDataSource()).thenReturn(dataSource);

        assertThat(eventDeQueuer.popNextEventId(tableName).isPresent(), is(false));

        final Event event_1 = eventFactory.createEvent("example.first-event", 1L);
        final Event event_2 = eventFactory.createEvent("example.second-event", 2L);
        final Event event_3 = eventFactory.createEvent("example.third-event", 3L);

        testEventInserter.insertIntoEventLog(event_1);
        testEventInserter.insertIntoEventLog(event_2);
        testEventInserter.insertIntoEventLog(event_3);

        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_1.getId()));
        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_2.getId()));
        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_3.getId()));

        assertThat(eventDeQueuer.popNextEventId(tableName).isPresent(), is(false));
    }

    @Test
    public void shouldPopEventsFromThePublishQueue() throws Exception {

        final String tableName = "publish_queue";

        when(subscriptionDataSourceProvider.getEventStoreDataSource()).thenReturn(dataSource);

        assertThat(eventDeQueuer.popNextEventId(tableName).isPresent(), is(false));

        final Event event_1 = eventFactory.createEvent("example.first-event", 1L);
        final Event event_2 = eventFactory.createEvent("example.second-event", 2L);
        final Event event_3 = eventFactory.createEvent("example.third-event", 3L);

        insertInPublishQueue(event_1,  event_2, event_3);

        testEventInserter.insertIntoEventLog(event_1);
        testEventInserter.insertIntoEventLog(event_2);
        testEventInserter.insertIntoEventLog(event_3);

        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_1.getId()));
        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_2.getId()));
        assertThat(eventDeQueuer.popNextEventId(tableName).get(), is(event_3.getId()));

        assertThat(eventDeQueuer.popNextEventId(tableName).isPresent(), is(false));
    }

    private void insertInPublishQueue(final Event... events) throws SQLException {
        try(final Connection connection = dataSource.getConnection()) {

            final String sql = "INSERT into publish_queue (event_log_id, date_queued) VALUES (?, ?)";
            try(final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

                for(Event event: events) {
                    preparedStatement.setObject(1, event.getId());
                    preparedStatement.setTimestamp(2, toSqlTimestamp(clock.now()));

                    preparedStatement.executeUpdate();
                }
            }
        }
    }
}