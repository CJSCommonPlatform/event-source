package uk.gov.justice.services.eventstore.management.catchup.process;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.eventstore.management.catchup.events.CatchupRequestedEvent;
import uk.gov.justice.services.eventstore.management.catchup.events.CatchupStartedEvent;
import uk.gov.justice.subscription.domain.subscriptiondescriptor.SubscriptionsDescriptor;
import uk.gov.justice.subscription.registry.SubscriptionsDescriptorsRegistry;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import org.slf4j.Logger;

public class EventCatchupRunner {

    @Inject
    private SubscriptionsDescriptorsRegistry subscriptionsDescriptorsRegistry;

    @Inject
    private Event<CatchupStartedEvent> catchupStartedEventFirer;

    @Inject
    private EventCatchupByComponentRunner eventCatchupByComponentRunner;

    @Inject
    private UtcClock clock;

    @Inject
    private Logger logger;

    public void runEventCatchup(final CatchupRequestedEvent catchupRequestedEvent) {

        logger.info("Received CatchupRequestedEvent");

        catchupStartedEventFirer.fire(new CatchupStartedEvent(clock.now()));

        final List<SubscriptionsDescriptor> subscriptionsDescriptors =
                subscriptionsDescriptorsRegistry.getAll();

        subscriptionsDescriptors.forEach(subscriptionsDescriptor -> eventCatchupByComponentRunner.runEventCatchupForComponent(
                subscriptionsDescriptor,
                catchupRequestedEvent));
    }
}