package uk.gov.justice.services.eventstore.management.catchup.observers;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import uk.gov.justice.services.eventstore.management.events.catchup.CatchupCompletedEvent;
import uk.gov.justice.services.eventstore.management.events.catchup.CatchupCompletedForSubscriptionEvent;
import uk.gov.justice.services.eventstore.management.events.catchup.CatchupProcessingOfEventFailedEvent;
import uk.gov.justice.services.eventstore.management.events.catchup.CatchupRequestedEvent;
import uk.gov.justice.services.eventstore.management.events.catchup.CatchupStartedEvent;
import uk.gov.justice.services.eventstore.management.events.catchup.CatchupStartedForSubscriptionEvent;
import uk.gov.justice.services.eventstore.management.logging.MdcLogger;

import java.util.function.Consumer;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class CatchupObserverTest {

    @Mock
    private CatchupLifecycle catchupLifecycle;

    @Mock
    private MdcLogger mdcLogger;

    @InjectMocks
    private CatchupObserver catchupObserver;

    private Consumer<Runnable> testConsumer = Runnable::run;

    @Test
    public void shouldHandleCatchupRequested() throws Exception {

        final CatchupRequestedEvent catchupRequestedEvent = mock(CatchupRequestedEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupRequested(catchupRequestedEvent);

        verify(catchupLifecycle).handleCatchupRequested(catchupRequestedEvent);
    }

    @Test
    public void shouldHandleCatchupStarted() throws Exception {

        final CatchupStartedEvent catchupStartedEvent = mock(CatchupStartedEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupStarted(catchupStartedEvent);
        verify(catchupLifecycle).handleCatchupStarted(catchupStartedEvent);
    }

    @Test
    public void shouldHandleCatchupStartedForSubscription() throws Exception {

        final CatchupStartedForSubscriptionEvent catchupStartedForSubscriptionEvent = mock(CatchupStartedForSubscriptionEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupStartedForSubscription(catchupStartedForSubscriptionEvent);

        verify(catchupLifecycle).handleCatchupStartedForSubscription(catchupStartedForSubscriptionEvent);
    }

    @Test
    public void shouldHandleCatchupCompleteForSubscription() throws Exception {

        final CatchupCompletedForSubscriptionEvent catchupCompletedForSubscriptionEvent = mock(CatchupCompletedForSubscriptionEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupCompleteForSubscription(catchupCompletedForSubscriptionEvent);

        verify(catchupLifecycle).handleCatchupCompleteForSubscription(catchupCompletedForSubscriptionEvent);
    }

    @Test
    public void shouldHandleCatchupComplete() throws Exception {

        final CatchupCompletedEvent catchupCompletedEvent = mock(CatchupCompletedEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupComplete(catchupCompletedEvent);

        verify(catchupLifecycle).handleCatchupComplete(catchupCompletedEvent);
    }

    @Test
    public void shouldHandleCatchupProcessingOfEventFailed() throws Exception {

        final CatchupProcessingOfEventFailedEvent catchupProcessingOfEventFailedEvent = mock(CatchupProcessingOfEventFailedEvent.class);

        when(mdcLogger.mdcLoggerConsumer()).thenReturn(testConsumer);

        catchupObserver.onCatchupProcessingOfEventFailed(catchupProcessingOfEventFailedEvent);

        verify(catchupLifecycle).handleCatchupProcessingOfEventFailed(catchupProcessingOfEventFailedEvent);
    }
}
