package uk.gov.justice.services.eventsourcing.timer;

import static java.util.Arrays.asList;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;


@RunWith(MockitoJUnitRunner.class)
public class TimerServiceManagerTest {

    @Mock
    private TimerConfigFactory timerConfigFactory;

    @Mock
    private TimerCanceler timerCanceler;

    @InjectMocks
    private TimerServiceManager timerServiceManager;

    @Test
    public void shouldCreateAnIntervalTimer() throws Exception {
        final String timerJobName = "timer job name";
        final long timerStartWaitMilliseconds = 2384L;
        final long timerIntervalMilliseconds = 2098374L;

        final TimerService timerService = mock(TimerService.class);
        final TimerConfig timerConfig = mock(TimerConfig.class);

        when(timerConfigFactory.createNew()).thenReturn(timerConfig);

        timerServiceManager.createIntervalTimer(timerJobName, timerStartWaitMilliseconds, timerIntervalMilliseconds, timerService);

        final InOrder inOrder = inOrder(timerConfig, timerService);

        inOrder.verify(timerConfig).setPersistent(false);
        inOrder.verify(timerConfig).setInfo(timerJobName);
        inOrder.verify(timerService).createIntervalTimer(timerStartWaitMilliseconds, timerIntervalMilliseconds, timerConfig);
    }

    @Test
    public void shouldCancelOverlappingTimersIfOverTheThreshold() throws Exception {

        final TimerService timerService = mock(TimerService.class);

        final String timerJobName = "timerJobName";
        final int threshold = 2;

        final Timer timer_1 = mock(Timer.class);
        final Timer timer_2 = mock(Timer.class);
        final Timer timer_3 = mock(Timer.class);

        when(timerService.getAllTimers()).thenReturn(asList(timer_1, timer_2, timer_3));

        timerServiceManager.cancelOverlappingTimers(timerJobName, threshold, timerService);
        
        verify(timerCanceler).cancelTimer(timerJobName, timerService);
    }

    @Test
    public void shouldNotCancelTimersIfNumberOfTimersNotOverTheThreshold() throws Exception {

        final TimerService timerService = mock(TimerService.class);

        final String timerJobName = "timerJobName";
        final int threshold = 3;

        final Timer timer_1 = mock(Timer.class);
        final Timer timer_2 = mock(Timer.class);
        final Timer timer_3 = mock(Timer.class);

        when(timerService.getAllTimers()).thenReturn(asList(timer_1, timer_2, timer_3));

        timerServiceManager.cancelOverlappingTimers(timerJobName, threshold, timerService);

        verifyZeroInteractions(timerCanceler);
    }
}
