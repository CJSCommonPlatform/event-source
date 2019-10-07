package uk.gov.justice.services.eventstore.management.indexer.observers;

import static java.lang.String.format;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventstore.management.indexer.events.IndexerCatchupCompletedEvent;
import uk.gov.justice.services.eventstore.management.indexer.events.IndexerCatchupCompletedForSubscriptionEvent;
import uk.gov.justice.services.eventstore.management.indexer.events.IndexerCatchupRequestedEvent;
import uk.gov.justice.services.eventstore.management.indexer.events.IndexerCatchupStartedEvent;
import uk.gov.justice.services.eventstore.management.indexer.events.IndexerCatchupStartedForSubscriptionEvent;
import uk.gov.justice.services.eventstore.management.indexer.process.EventIndexerCatchupRunner;
import uk.gov.justice.services.eventstore.management.indexer.process.IndexerCatchupDurationCalculator;
import uk.gov.justice.services.eventstore.management.indexer.process.IndexerCatchupInProgress;
import uk.gov.justice.services.eventstore.management.indexer.process.IndexerCatchupsInProgressCache;
import uk.gov.justice.services.eventstore.management.logging.MdcLogger;
import uk.gov.justice.services.jmx.api.command.SystemCommand;

import java.time.Duration;
import java.time.ZonedDateTime;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.slf4j.Logger;

@ApplicationScoped
public class IndexerCatchupObserver {

    @Inject
    private EventIndexerCatchupRunner eventIndexerCatchupRunner;

    @Inject
    private IndexerCatchupsInProgressCache indexerCatchupsInProgressCache;

    @Inject
    private IndexerCatchupDurationCalculator indexerCatchupDurationCalculator;

    @Inject
    private Event<IndexerCatchupCompletedEvent> indexerCatchupCompletedEventFirer;

    @Inject
    private UtcClock clock;

    @Inject
    private MdcLogger mdcLogger;

    @Inject
    private Logger logger;

    public void onIndexerCatchupRequested(@SuppressWarnings("unused") @Observes final IndexerCatchupRequestedEvent indexerCatchupRequestedEvent) {
        mdcLogger.mdcLoggerConsumer().accept(() -> {
            logger.info("Event indexer catchup requested");
            eventIndexerCatchupRunner.runEventIndexerCatchup(indexerCatchupRequestedEvent);
        });
    }

    public void onIndexerCatchupStarted(@Observes final IndexerCatchupStartedEvent catchupStartedEvent) {
        mdcLogger.mdcLoggerConsumer().accept(() -> {
            logger.info("Event indexer catchup started at " + catchupStartedEvent.getCatchupStartedAt());
            logger.info("Performing indexer catchup of events...");

            indexerCatchupsInProgressCache.removeAll();
        });
    }

    public void onIndexerCatchupStartedForSubscription(@Observes final IndexerCatchupStartedForSubscriptionEvent indexerCatchupStartedForSubscriptionEvent) {

        mdcLogger.mdcLoggerConsumer().accept(() -> {
            final String subscriptionName = indexerCatchupStartedForSubscriptionEvent.getSubscriptionName();
            final ZonedDateTime catchupStartedAt = indexerCatchupStartedForSubscriptionEvent.getCatchupStartedAt();

            indexerCatchupsInProgressCache.addCatchupInProgress(new IndexerCatchupInProgress(subscriptionName, catchupStartedAt));

            logger.info(format("Event indexer catchup for subscription '%s' started at %s", subscriptionName, catchupStartedAt));
        });
    }

    public void onIndexerCatchupCompleteForSubscription(@Observes final IndexerCatchupCompletedForSubscriptionEvent indexerCatchupCompletedForSubscriptionEvent) {

        mdcLogger.mdcLoggerConsumer().accept(() -> {
            final String subscriptionName = indexerCatchupCompletedForSubscriptionEvent.getSubscriptionName();

            final ZonedDateTime catchupCompletedAt = indexerCatchupCompletedForSubscriptionEvent.getIndexerCatchupCompletedAt();
            final int totalNumberOfEvents = indexerCatchupCompletedForSubscriptionEvent.getTotalNumberOfEvents();

            logger.info(format("Event indexer catchup for subscription '%s' completed at %s", subscriptionName, catchupCompletedAt));
            logger.info(format("Event indexer catchup for subscription '%s' caught up %d events", subscriptionName, totalNumberOfEvents));

            final IndexerCatchupInProgress catchupInProgress = indexerCatchupsInProgressCache.removeCatchupInProgress(subscriptionName);

            final Duration catchupDuration = indexerCatchupDurationCalculator.calculate(
                    catchupInProgress, indexerCatchupCompletedForSubscriptionEvent);

            logger.info(format("Event indexer catchup for subscription '%s' took %d milliseconds", subscriptionName, catchupDuration.toMillis()));

            if (indexerCatchupsInProgressCache.noCatchupsInProgress()) {
                final ZonedDateTime completedAt = clock.now();
                final SystemCommand target = indexerCatchupCompletedForSubscriptionEvent.getTarget();
                indexerCatchupCompletedEventFirer.fire(new IndexerCatchupCompletedEvent(target, completedAt));
                logger.info(format("Event indexer catchup fully complete at %s", completedAt));
            }
        });
    }
}