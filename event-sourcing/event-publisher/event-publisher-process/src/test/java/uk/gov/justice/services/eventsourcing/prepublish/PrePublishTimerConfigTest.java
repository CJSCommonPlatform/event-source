package uk.gov.justice.services.eventsourcing.prepublish;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class PrePublishTimerConfigTest {

    @InjectMocks
    private PrePublishTimerConfig prePublishTimerConfig;

    @Test
    public void shouldGetTheStartWaitTime() throws Exception {

        final long milliseconds = 982374L;

        prePublishTimerConfig.timerStartWaitMilliseconds = "" + milliseconds;

        assertThat(prePublishTimerConfig.getTimerStartWaitMilliseconds(), is(milliseconds));
    }

    @Test
    public void shouldGetTheTimerInterval() throws Exception {

        final long milliseconds = 2998734L;

        prePublishTimerConfig.timerIntervalMilliseconds = "" + milliseconds;

        assertThat(prePublishTimerConfig.getTimerIntervalMilliseconds(), is(milliseconds));
    }
}
