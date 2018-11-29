package uk.gov.justice.services.event.sourcing.subscription.manager;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.justice.services.core.interceptor.InterceptorContext.interceptorContextWithInput;
import static uk.gov.justice.test.utils.FieldAccessor.getFieldFrom;

import uk.gov.justice.services.core.interceptor.DefaultContextPayload;
import uk.gov.justice.services.core.interceptor.InterceptorChainProcessor;
import uk.gov.justice.services.core.interceptor.InterceptorContext;
import uk.gov.justice.services.event.buffer.api.EventBufferService;
import uk.gov.justice.services.event.sourcing.subscription.manager.cdi.InterceptorContextProvider;
import uk.gov.justice.services.messaging.JsonEnvelope;
import uk.gov.justice.test.utils.FieldAccessor;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class BackwardsCompatibleSubscriptionManagerTest {

    @Mock
    private InterceptorChainProcessor interceptorChainProcessor;

    @Mock
    private InterceptorContextProvider interceptorContextProvider;

    @Captor
    private ArgumentCaptor<InterceptorContext> interceptorContextCaptor;

    @InjectMocks
    private BackwardsCompatibleSubscriptionManager backwardsCompatibleSubscriptionManager;

    @SuppressWarnings("unchecked")
    @Test
    public void shouldProcessEnvelopeUsingInterceptorChain() throws Exception {

        final JsonEnvelope incomingJsonEnvelope = mock(JsonEnvelope.class);
        final InterceptorContext interceptorContext = mock(InterceptorContext.class);

        when(interceptorContextProvider.getInterceptorContext(incomingJsonEnvelope)).thenReturn(interceptorContext);

        backwardsCompatibleSubscriptionManager.process(incomingJsonEnvelope);

        verify(interceptorChainProcessor).process(interceptorContextCaptor.capture());

        assertThat(interceptorContextCaptor.getValue(), is(interceptorContext));
    }

    @Test
    public void shouldDoNothingOnStartup() throws Exception {

        backwardsCompatibleSubscriptionManager.startSubscription();
        verifyZeroInteractions(interceptorChainProcessor);
    }

}