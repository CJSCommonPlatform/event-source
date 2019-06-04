package uk.gov.justice.services.eventsourcing.util.jee.timer;

import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    public void shouldCreateASingleActionTimer() throws Exception {

        final String timerJobName = "timer job name";
        final long duration = 9839798342L;
        final TimerService timerService = mock(TimerService.class);
        final TimerConfig timerConfig = mock(TimerConfig.class);

        when(timerConfigFactory.createNew()).thenReturn(timerConfig);

        timerServiceManager.createSingleActionTimer(timerJobName, duration, timerService);

        final InOrder inOrder = inOrder(timerConfig, timerService);

        inOrder.verify(timerConfig).setPersistent(false);
        inOrder.verify(timerConfig).setInfo(timerJobName);
        inOrder.verify(timerService).createSingleActionTimer(duration, timerConfig);
    }
}
